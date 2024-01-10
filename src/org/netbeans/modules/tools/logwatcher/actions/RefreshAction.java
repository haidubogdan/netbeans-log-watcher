package org.netbeans.modules.tools.logwatcher.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.swing.AbstractAction;
import org.netbeans.modules.tools.logwatcher.LogFolder;
import org.netbeans.modules.tools.logwatcher.LogNodeSupport;
import org.netbeans.modules.tools.logwatcher.WatchDir;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 *
 * @author bhaidu
 */
@ActionID(category = "RootActions", id = "org.netbeans.modules.tools.logwatcher.actions.RefreshAction")
@ActionRegistration(displayName = "Refresh")
public class RefreshAction extends AbstractAction implements ActionListener {

    private final DataFolder folder;

    public RefreshAction(DataFolder df) {
        folder = df;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        FileObject primaryFile = folder.getPrimaryFile();
        LogFolder lf = LogNodeSupport.getLogFolder(primaryFile);

        if (lf != null && lf.dir != null) {
            try {
                WatchDir.watch(lf.dir.toPath());
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }

    }
}
