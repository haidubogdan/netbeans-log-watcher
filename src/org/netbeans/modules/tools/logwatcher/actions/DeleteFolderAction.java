package org.netbeans.modules.tools.logwatcher.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.swing.AbstractAction;
import org.netbeans.modules.tools.logwatcher.LogFolder;
import org.netbeans.modules.tools.logwatcher.LogNodeSupport;
import org.netbeans.modules.tools.logwatcher.LogWatcherPropertiesSupport;
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
@ActionID(category = "RootActions", id = "org.netbeans.modules.tools.logwatcher.actions.DeleteFolderAction")
@ActionRegistration(displayName = "Delete")
public class DeleteFolderAction extends AbstractAction implements ActionListener {

    private final DataFolder folder;

    public DeleteFolderAction(DataFolder df) {
        folder = df;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        FileObject primaryFile = folder.getPrimaryFile();
        LogFolder lf = LogNodeSupport.getLogFolder(primaryFile);

        if (lf != null && lf.dir != null) {
            WatchDir watchDir = WatchDir.getInstance();
            if (watchDir != null) {
                watchDir.remove(lf.dir.toPath());
            }
        }
        try {
            folder.delete();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
}
