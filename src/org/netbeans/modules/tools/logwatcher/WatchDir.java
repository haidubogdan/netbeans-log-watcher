package org.netbeans.modules.tools.logwatcher;

import org.netbeans.modules.tools.logwatcher.output.LogIO;
import java.awt.Component;
import java.nio.file.*;
import static java.nio.file.StandardWatchEventKinds.*;
import static java.nio.file.LinkOption.*;
import java.nio.file.attribute.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.progress.*;
import org.openide.util.Cancellable;
import org.openide.util.RequestProcessor;
import org.openide.windows.IOContainer;

/**
 * https://docs.oracle.com/javase/tutorial/essential/io/examples/WatchDir.java
 *
 * @author bhaidu
 */
public class WatchDir implements ChangeListener {

    private static final RequestProcessor WORKER = new RequestProcessor(WatchDir.class.getName(), 1, true);
    private final WatchService watcher;
    private final Map<WatchKey, Path> keys;
    //private final boolean recursive;
    private boolean trace = false;
    public AtomicBoolean isRunning = new AtomicBoolean(false);
    private static WatchDir instance = null;
    ProgressHandle ph;
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

    /**
     * Register the given directory with the WatchService
     */
    private void register(Path dir) throws IOException {
        WatchKey key = dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
        if (trace) {
            Path prev = keys.get(key);
            if (prev == null) {
                System.out.format("register: %s\n", dir);
            } else {
                if (!dir.equals(prev)) {
                    System.out.format("update: %s -> %s\n", prev, dir);
                }
            }
        }
        keys.put(key, dir);
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
    }

    /**
     *
     * @not necessary
     *
     * Register the given directory, and all its sub-directories, with the
     * WatchService.
     */
    private void registerAll(final Path start) throws IOException {
        // register directory and sub-directories
        Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
                    throws IOException {
                register(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    /**
     * Creates a WatchService and registers the given directory
     */
    WatchDir(Path dir) throws IOException {
        this.watcher = FileSystems.getDefault().newWatchService();
        this.keys = new HashMap<WatchKey, Path>();
//        this.recursive = recursive;

//        if (recursive) {
//            System.out.format("Scanning %s ...\n", dir);
//            registerAll(dir);
//            System.out.println("Done.");
//        } else {
        register(dir);
//        }

        this.output = LogIO.getInstance();
        //this.atomic = Thread.currentThread();
        // enable trace after initial registration
        this.trace = true;
        this.isRunning.set(false);
    }

    public void start() {
        this.isRunning.set(true);
    }

    public synchronized void killProcess() {
        System.out.println("Process stopped");
        this.isRunning.set(false);
        if (ph != null) {
            ph.finish();
            ph.close();
            ph = null;
        }
        if (!WORKER.isShutdown()) {
            WORKER.stop();
            WORKER.shutdown();
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
                this.output.notify(child, dir, event.kind().name());
                // if directory is created, and watching recursively, then
                // register it and its sub-directories
                /*
                if (recursive && (kind == ENTRY_CREATE)) {
                    try {
                        if (Files.isDirectory(child, NOFOLLOW_LINKS)) {
                            registerAll(child);
                        }
                    } catch (IOException x) {
                        // ignore to keep sample readbale
                    }
                }
                 */
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

    @Override
    public void stateChanged(ChangeEvent e) {
        System.out.println("state changed " + e);
    }

}
