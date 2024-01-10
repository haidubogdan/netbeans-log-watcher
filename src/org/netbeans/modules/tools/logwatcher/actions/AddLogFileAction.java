package org.netbeans.modules.tools.logwatcher.actions;

import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import javax.swing.AbstractAction;
import org.netbeans.modules.tools.logwatcher.LogFile;
import org.netbeans.modules.tools.logwatcher.WatchDir;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.filesystems.FileChangeAdapter;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataFolder;
import org.openide.util.Exceptions;
import org.openide.windows.WindowManager;


/**
 * for a file we need to use a different service or filter a folder to notify for just 
 * some files
 * 
 * @author bhaidu
 */
//@ActionID(category = "RootActions", id = "org.netbeans.modules.tools.logwatcher.actions.AddLogFileAction")
//@ActionRegistration(displayName = "Add Log File")
public class AddLogFileAction extends AbstractAction implements ActionListener {

    private final DataFolder folder;

    public AddLogFileAction(DataFolder df) {
        folder = df;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        FileDialog fileDialog = new FileDialog(WindowManager.getDefault().getMainWindow());
        fileDialog.setTitle("Select a file");
        fileDialog.setVisible(true);

        if (fileDialog.getDirectory() != null && fileDialog.getFile() != null) {
            String selFilePath = fileDialog.getFile();
            File dir = new File(fileDialog.getDirectory());
            File selFile = new File(dir, selFilePath);
            FileObject fo = FileUtil.toFileObject(selFile);

            System.out.println("added listener for " + fo.getNameExt());

            FileObject fld = folder.getPrimaryFile();
            String baseName = fo.getNameExt();
            try {
                WatchDir.watch(selFile.toPath());
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
            try {
                FileObject writeTo = fld.createData(baseName, "ser");
                FileLock lock = writeTo.lock();
                try {
                    ObjectOutputStream str = new ObjectOutputStream(writeTo.getOutputStream(lock));
                    try {
                        str.writeObject(new LogFile(selFile));
                    } finally {
                        str.close();
                    }
                } finally {
                    lock.releaseLock();
                }
            } catch (IOException ioe) {
                Exceptions.printStackTrace(ioe);
            }
        }
    }

    /*
    public void actionPerformed2(ActionEvent ae) {
        NotifyDescriptor.InputLine nd = new NotifyDescriptor.InputLine(
                Bundle.FN_askurl_msg(),
                Bundle.FN_askurl_title(),
                NotifyDescriptor.OK_CANCEL_OPTION,
                NotifyDescriptor.PLAIN_MESSAGE);
        Object result = DialogDisplayer.getDefault().notify(nd);
        if (result.equals(NotifyDescriptor.OK_OPTION)) {
            String urlString = nd.getInputText();
            FileObject url = null;
//            try {
//                url = new URL(urlString);
//            } catch (MalformedURLException e) {
//                Exceptions.attachLocalizedMessage(e, Bundle.FN_askurl_err(result));
//                Exceptions.printStackTrace(e);
//                return;
//            }
//            try {
//                checkConnection(url);
//            } catch (IOException e) {
//                Exceptions.attachLocalizedMessage(e, Bundle.FN_cannotConnect_err());
//                Exceptions.printStackTrace(e);
//                return;
//            }
            if (url == null) {
                return;
            }
            LogFile logFile = new LogFile(url);
            FileObject fld = folder.getPrimaryFile();
            String baseName = "LogFile";
            int ix = 1;
            while (fld.getFileObject(baseName + ix, "ser") != null) {
                ix++;
            }
            try {
                FileObject writeTo = fld.createData(baseName + ix, "ser");
                FileLock lock = writeTo.lock();
                try {
                    ObjectOutputStream str = new ObjectOutputStream(writeTo.getOutputStream(lock));
                    try {
                        str.writeObject(logFile);
                    } finally {
                        str.close();
                    }
                } finally {
                    lock.releaseLock();
                }
            } catch (IOException ioe) {
                Exceptions.printStackTrace(ioe);
            }
        }
    }
     */
    private static void checkConnection(final URL url) throws IOException {
        InputStream is = url.openStream();
        is.close();
    }

    private final class FileChangeListenerImpl extends FileChangeAdapter {

        //should be a map
        int lastChangedLine = 0;

        @Override
        public void fileFolderCreated(FileEvent fe) {
            processFile(fe.getFile());
        }

        @Override
        public void fileChanged(FileEvent fe) {
            processFile(fe.getFile());
        }

        @Override
        public void fileDataCreated(FileEvent fe) {
            processFile(fe.getFile());
        }

        private void processFile(FileObject file) {
            if (file.isFolder()) {
                System.out.println("Folder " + file.getNameExt() + " Changed");
                return;
            }
            System.out.println("File " + file.getNameExt() + " Changed");
            DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message("File " + file.getNameExt() + " Changed"));

            try {
                String currentLine;
                int i = 0;
                InputStream stream = file.getInputStream();
                try {
                    BufferedReader br = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
                    try {
                        while ((currentLine = br.readLine()) != null) {
                            i++;
                            if (i >= lastChangedLine) {
                                System.out.println(currentLine);
                            }
                        }
                        lastChangedLine = i;
                    } catch (IOException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                } catch (UnsupportedEncodingException ex) {
                    Exceptions.printStackTrace(ex);
                }
            } catch (FileNotFoundException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }
}
