package org.netbeans.modules.tools.logwatcher;

import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import javax.swing.AbstractAction;
import org.netbeans.modules.tools.logwatcher.Bundle;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.filesystems.FileChangeAdapter;
import org.openide.filesystems.FileChangeListener;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataFolder;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle.Messages;
import org.openide.util.RequestProcessor;
import org.openide.windows.WindowManager;

@ActionID(category = "LogWatcher", id = "org.netbeans.modules.tools.logwatcher.AddLogFileAction")
@ActionRegistration(displayName = "#FN_addbutton")
@Messages("FN_addbutton=Add Log File")
public class AddLogFileAction implements ActionListener {

//    private final DataFolder folder;
    private final FileChangeListener fileChangeListener = new FileChangeListenerImpl();
//
//    public AddLogFileAction(DataFolder df) {
//        folder = df;
//    }

    @Messages({
        "FN_askurl_msg=Enter the path of an Log File",
        "FN_askurl_title=Add log file",
        "FN_askurl_err=Invalid URL: {0}|",
        "FN_cannotConnect_err=Cannot Connect!"
    })

    @Override
    public void actionPerformed(ActionEvent ae) {
        FileDialog fileDialog = new FileDialog(WindowManager.getDefault().getMainWindow());
        fileDialog.setVisible(true);
        File[] files = new File[]{};
        if (fileDialog.getDirectory() != null && fileDialog.getFile() != null) {
            String selFilePath = fileDialog.getFile();
            File dir = new File(fileDialog.getDirectory());
            File selFile = new File(dir, selFilePath);
            files = new File[]{};
            //FileUtil.addRecursiveListener(fileChangeListener, dir);
            LogOutputComponent tc = new LogOutputComponent();
            FileChangeSupport fileSupportListener = new FileChangeSupport(tc);
            FileUtil.addRecursiveListener(fileSupportListener, dir);
            FileObject fo = FileUtil.toFileObject(selFile);
            fo.addFileChangeListener(FileUtil.weakFileChangeListener(fileSupportListener, dir));

            System.out.println("added listener for " + fo.getNameExt());
            tc.setDisplayName("Logoutput component " + dir.getName());
            tc.open();
            tc.requestActive();

            FileObject rootFolder = FileUtil.getConfigFile("LogFiles");
            int ix = 1;
            while (rootFolder.getFileObject("LogFiles" + ix, "ser") != null) {
                ix++;
            }

            try {
                FileObject writeTo = rootFolder.createData("LogFiles" + ix, "ser");
                FileLock lock = writeTo.lock();
                try {
                    ObjectOutputStream str = new ObjectOutputStream(writeTo.getOutputStream(lock));
                    try {
                        str.writeObject(fo.getName());
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
