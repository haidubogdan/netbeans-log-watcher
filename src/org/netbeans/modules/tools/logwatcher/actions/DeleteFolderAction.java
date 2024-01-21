package org.netbeans.modules.tools.logwatcher.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.swing.AbstractAction;
import org.netbeans.modules.tools.logwatcher.ConfigSupport;
import org.netbeans.modules.tools.logwatcher.WatchDir;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataFolder;
import org.openide.util.Exceptions;

/**
 *
 * @author bhaidu
 */
@ActionID(category = "FolderActions", id = "org.netbeans.modules.tools.logwatcher.actions.DeleteFolderAction")
@ActionRegistration(displayName = "Delete")
public class DeleteFolderAction extends AbstractAction implements ActionListener {

    private final DataFolder folder;

    public DeleteFolderAction(DataFolder df) {
        folder = df;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        FileObject primaryFile = folder.getPrimaryFile();
        File logFile = ConfigSupport.getLogFileReference(primaryFile);

        if (logFile != null && logFile.isDirectory() ) {
            WatchDir watchDir = WatchDir.getInstance();
            if (watchDir != null) {
                watchDir.remove(logFile.toPath());
            }
        }
        try {
            folder.delete();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
}
