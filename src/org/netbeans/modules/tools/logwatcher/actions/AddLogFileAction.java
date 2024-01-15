package org.netbeans.modules.tools.logwatcher.actions;

import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.swing.AbstractAction;
import static org.netbeans.modules.tools.logwatcher.LogWatcherNode.LOG_FILE_WATCH_ATTR;
import static org.netbeans.modules.tools.logwatcher.LogWatcherNode.LOG_PATH_ATTR;
import org.netbeans.modules.tools.logwatcher.WatchDir;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataFolder;
import org.openide.util.Exceptions;
import org.openide.windows.WindowManager;
import static org.netbeans.modules.tools.logwatcher.LogWatcherNode.LOG_DIR_HAS_FILTERS_ATTR;

/**
 * for a file we need to use a different service or filter a folder to notify
 * for just some files
 *
 * @author bhaidu
 */
//@ActionID(category = "RootActions", id = "org.netbeans.modules.tools.logwatcher.actions.AddLogFileAction")
//@ActionRegistration(displayName = "Add Log File")
public class AddLogFileAction extends AbstractAction implements ActionListener {

    private final DataFolder folder;

    public AddLogFileAction(DataFolder df) {
        folder = df;
        putValue(NAME, "Add Log File");
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
            FileObject logFile = FileUtil.toFileObject(selFile);

            FileObject fld = folder.getPrimaryFile();
            FileObject dirParent = logFile.getParent();

            if (!fld.getName().equals(dirParent.getName())) {
                try {
                    DataFolder parentDf = DataFolder.create(folder, dirParent.getName());
                    fld = parentDf.getPrimaryFile();
                    fld.setAttribute(LOG_PATH_ATTR, dirParent.getPath());
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                    return;
                }
            }

            try {
                FileObject logFileForNode = fld.getFileObject(logFile.getNameExt());
                if (logFileForNode == null) {
                    //add new node
                    logFileForNode = fld.createData(logFile.getName(), logFile.getExt());
                    int x = 1;
                }
                if (logFileForNode != null) {
                    logFileForNode.setAttribute(LOG_PATH_ATTR, logFile.getPath());
                    logFileForNode.setAttribute(LOG_FILE_WATCH_ATTR, 1);
                    if (fld.getAttribute(LOG_DIR_HAS_FILTERS_ATTR) == null){
                        fld.setAttribute(LOG_DIR_HAS_FILTERS_ATTR, 1);
                    }
                }
            } catch (IOException ioe) {
                Exceptions.printStackTrace(ioe);
                return;
            }
            try {
                WatchDir.watch(dir.toPath());
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

}
