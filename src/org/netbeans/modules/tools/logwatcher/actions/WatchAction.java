package org.netbeans.modules.tools.logwatcher.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.swing.AbstractAction;
import org.netbeans.modules.tools.logwatcher.LogNodeSupport;
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
@ActionID(category = "RootActions", id = "org.netbeans.modules.tools.logwatcher.actions.WatchAction")
@ActionRegistration(displayName = "Watch")
public class WatchAction extends AbstractAction implements ActionListener {

    private final DataFolder folder;

    public WatchAction(DataFolder df) {
        folder = df;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        FileObject primaryFile = folder.getPrimaryFile();
        File logFile = LogNodeSupport.getFileFromLogPathAttr(primaryFile);

        if (logFile != null && logFile.isDirectory()) {
            try {
                WatchDir.watch(logFile.toPath());
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }

    }
}
