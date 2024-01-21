package org.netbeans.modules.tools.logwatcher;

import java.nio.file.*;
import static java.nio.file.StandardWatchEventKinds.*;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.progress.ProgressHandle;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.Cancellable;
import org.openide.util.Exceptions;
import org.openide.util.RequestProcessor;
import org.openide.windows.IOContainer;
import org.netbeans.modules.tools.logwatcher.output.LogIO;

/**
 * https://docs.oracle.com/javase/tutorial/essential/io/examples/WatchDir.java
 *
 * @author bhaidu
 */
public class WatchDir implements ChangeListener {

    private static RequestProcessor WORKER = new RequestProcessor(WatchDir.class.getName(), 1, true);
    private final WatchService watcher;
    private final Map<WatchKey, Path> keys;
    //private final boolean recursive;
    public AtomicBoolean isRunning = new AtomicBoolean(false);
    private static WatchDir instance = null;
    //ProgressHandle ph;
    private final LogIO output;

    public static WatchDir watch(Path path) throws IOException {
        if (instance == null) {
            instance = new WatchDir(path);
        } else {
            instance.register(path);
        }
        boolean started = instance.isProcessRunning();
        if (!started) {
            instance.start();
            WORKER.post(new Runnable() {
                @Override
                public void run() {
                    ProgressHandle ph = ProgressHandle.createHandle("Watching for logs", cancellable());
                    ph.start();
                    ph.progress("watching logs ..");
                    instance.processEvents();
                    ph.finish();
                    ph.close();
                }
            });
        }

        IOContainer ioContainer = IOContainer.getDefault();
        ioContainer.open();

        return instance;
    }

    public static Cancellable cancellable() {

        return new Cancellable() {
            public @Override
            boolean cancel() {
                instance.killProcess();
                return true;
            }
        };
    }

    public static WatchDir getInstance() {
        return instance;
    }

    @SuppressWarnings("unchecked")
    static <T> WatchEvent<T> cast(WatchEvent<?> event) {
        return (WatchEvent<T>) event;
    }

    public boolean isRegistered(FileObject dirFo) {
        FileObject nodeFo = LogWatchTree.getFolderFileObject(dirFo.getName());
        if (nodeFo == null) {
            return false;
        }
        String referencePath = ConfigSupport.getLogFileReferencePath(nodeFo);
        File referenceFile = new File(referencePath);
        return keys.containsValue(referenceFile.toPath());
    }

    /**
     * Register the given directory with the WatchService
     */
    private void register(Path dir) throws IOException {
        WatchKey key = dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
        keys.put(key, dir);
        notifyLogIo(dir, "registered: " + dir.toString());
    }

    public void remove(Path dir) {
        if (keys.containsValue(dir)) {
            for (Map.Entry<WatchKey, Path> entry : keys.entrySet()) {
                if (dir.equals(entry.getValue())) {
                    keys.remove(entry.getKey());
                    break;
                }
            }
        }
        
        //stop process if we removed all the keys
        if (keys.isEmpty()) {
            killProcess();
        }
    }

    /**
     * Creates a WatchService and registers the given directory
     */
    WatchDir(Path dir) throws IOException {
        this.watcher = FileSystems.getDefault().newWatchService();
        this.keys = new HashMap<>();
        this.output = LogIO.getInstance();
        register(dir);
        this.isRunning.set(false);
    }

    public void start() {
        this.isRunning.set(true);
    }

    public synchronized void killProcess() {
        System.out.println("Process stopped");
        this.isRunning.set(false);

        if (!WORKER.isShutdown()) {
            WORKER.stop();
            WORKER.shutdown();
            //reset
            WORKER = new RequestProcessor(WatchDir.class.getName(), 1, true);
        }
    }

    public boolean isProcessRunning() {
        return this.isRunning.get();
    }

    /**
     * Process all events for keys queued to the watcher
     */
    public void processEvents() {
        if (!isProcessRunning()) {
            return;
        }
        for (;;) {
            if (!isProcessRunning()) {
                break;
            }

            if (Thread.interrupted()) {
                killProcess();
                return;
            }

            // wait for key to be signalled
            WatchKey key;
            try {
                key = watcher.take();
            } catch (InterruptedException x) {
                killProcess();
                return;
            }

            Path dir = keys.get(key);

            if (dir == null) {
                System.err.println("WatchKey not recognized!!");
                continue;
            }

            for (WatchEvent<?> event : key.pollEvents()) {
                WatchEvent.Kind kind = event.kind();

                // TBD - provide example of how OVERFLOW event is handled
                if (kind == OVERFLOW) {
                    continue;
                }

                // Context for directory entry event is the file name of entry
                WatchEvent<Path> ev = cast(event);
                Path name = ev.context();
                Path child = dir.resolve(name);

                // print out event
                System.out.format("%s: %s\n", event.kind().name(), child);
                String fileName = child.getFileName().toString();
                if (fileName.endsWith(".log") || fileName.endsWith(".txt")) {
                    try {
                        FileObject rootConfig = LogWatchTree.getRootFileObject();
                        FileObject subFolder = rootConfig.getFileObject(dir.getFileName().toString());

                        //Filtered notifications
                        if (ConfigSupport.logFolderHasFilters(subFolder)) {
                            FileObject fileSearch = subFolder.getFileObject(fileName);
                            if (fileSearch != null) {
                                if (ConfigSupport.fileIsMarkedForWatching(fileSearch)) {
                                    notifyLogIo(child, dir, event.kind().name());
                                } else {
                                    notifyLogIo(dir, "filtered out file, skipping update from : " + fileName);
                                }
                            } else {
                                //new file
                                notifyLogIo(child, dir, event.kind().name());
                            }
                        } else {
                            notifyLogIo(child, dir, event.kind().name());
                        }
                    } catch (DataObjectNotFoundException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
            }

            // reset key and remove from set if directory no longer accessible
            boolean valid = key.reset();
            if (!valid) {
                keys.remove(key);

                // all directories are inaccessible
                if (keys.isEmpty()) {
                    killProcess();
                    break;
                }
            }
        }
    }

    private void notifyLogIo(Path dir, String message) {
        Runnable awtTask = new Runnable() {
            @Override
            public void run() {
                output.notify(dir, message);
            }
        };
        if (SwingUtilities.isEventDispatchThread()) {
            awtTask.run();
        } else {
            try {
                SwingUtilities.invokeAndWait(awtTask);
            } catch (InterruptedException | InvocationTargetException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }
    
    private void notifyLogIo(Path filePath, Path dir, String event) {
        Runnable awtTask = new Runnable() {
            @Override
            public void run() {
                output.notify(filePath, dir, event);
            }
        };
        if (SwingUtilities.isEventDispatchThread()) {
            awtTask.run();
        } else {
            try {
                SwingUtilities.invokeAndWait(awtTask);
            } catch (InterruptedException ex) {
                Exceptions.printStackTrace(ex);
            } catch (InvocationTargetException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        System.out.println("state changed " + e);
    }

}
