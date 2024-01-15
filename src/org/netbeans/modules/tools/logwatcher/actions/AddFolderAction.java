package org.netbeans.modules.tools.logwatcher.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import static org.netbeans.modules.tools.logwatcher.LogWatcherNode.LOG_PATH_ATTR;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataFolder;
import org.openide.util.Exceptions;
import org.openide.windows.WindowManager;

/**
 *
 * @author bogdan
 */
@ActionID(id = "org.netbeans.modules.tools.logwatcher.actions.AddFolderAction", category = "RootActions")
@ActionRegistration(displayName = "Add Folder")
public class AddFolderAction extends AbstractAction implements ActionListener {

    private final DataFolder folder;

    public AddFolderAction(DataFolder df) {
        folder = df;
        putValue(NAME, "Add Folder");
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        /**
         * using file dialog as jChooser is quite slow
         */
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select Project Location");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

//        FileDialog fileDialog = new FileDialog(WindowManager.getDefault().getMainWindow());
//        fileDialog.setVisible(true);
//        fileDialog.setTitle("Select a file from a folder");

        if (JFileChooser.APPROVE_OPTION == chooser.showOpenDialog(WindowManager.getDefault().getMainWindow())) {
            File dir = chooser.getSelectedFile();
            FileObject dirFo = FileUtil.toFileObject(dir);

            System.out.println("added listener on folder " + dirFo.getName());

            final String folderString = dir.getName();
            try {
                DataFolder fd = DataFolder.create(folder, folderString);
                FileObject fod = fd.getPrimaryFile();
                fod.setAttribute(LOG_PATH_ATTR, dirFo.getPath());
                
                /*
                FileObject writeTo = fod.createData(LogNodeSupport.FOLDER_DATA_NAME + folderString);
                FileLock lock = writeTo.lock();
                try {
                    try (ObjectOutputStream str = new ObjectOutputStream(writeTo.getOutputStream(lock))) {
                        str.writeObject(new LogFolder(dir));
                        str.close();
                    }
                } finally {
                    lock.releaseLock();
                }
                */
                //LogWatcherPropertiesSupport.getInstance().setFilePath(dir.getName(), dirFo);
                //WatchDir.watch(dir.toPath());
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

}
