package org.netbeans.modules.tools.logwatcher;

import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import javax.swing.AbstractAction;
import org.netbeans.modules.tools.logwatcher.Bundle;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataFolder;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle.Messages;
import org.openide.util.RequestProcessor;
import org.openide.windows.WindowManager;

/**
 *
 * @author bogdan
 */
@ActionID(id = "org.netbeans.modules.tools.logreader.AddFolderAction", category = "LogWatcher")
@ActionRegistration(displayName = "#FN_addfolderbutton")
@Messages("FN_addfolderbutton=Add Folder")
public class AddFolderAction implements ActionListener {

//    private final DataFolder folder;
//
//    public AddFolderAction(DataFolder df) {
//        folder = df;
//    }

    @Messages({
        "FN_askfolder_msg=Enter the folder name",
        "FN_askfolder_title=New Folder"
    })
    @Override
    public void actionPerformed(ActionEvent ae) {
        /**
         * using file dialog as jChooser is very slow
         */
        FileDialog fileDialog = new FileDialog(WindowManager.getDefault().getMainWindow());
        fileDialog.setVisible(true);
        fileDialog.setTitle("Select a file from a folder");
        //fileDialog.s

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
            FileObject dirFo = FileUtil.toFileObject(dir);
            dirFo.addFileChangeListener(FileUtil.weakFileChangeListener(fileSupportListener, dir));

            System.out.println("added listener on folder " + dirFo.getName());
            tc.setDisplayName("Logoutput component " + dir.getName());
            tc.open();
            tc.requestActive();

        }
    }

    /*
    public void actionPerformed2(ActionEvent ae) {
NotifyDescriptor.InputLine nd = new NotifyDescriptor.InputLine(
                Bundle.FN_askfolder_msg(),
                Bundle.FN_askfolder_title(),
                NotifyDescriptor.OK_CANCEL_OPTION,
                NotifyDescriptor.PLAIN_MESSAGE);
        Object result =  DialogDisplayer.getDefault().notify(nd);
        if (result.equals(NotifyDescriptor.OK_OPTION)) {
            final String folderString = nd.getInputText();
            try {
                DataFolder.create(folder, folderString);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }
     */
}
