package org.netbeans.modules.tools.logwatcher.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.swing.AbstractAction;
import static javax.swing.Action.SMALL_ICON;
import org.netbeans.modules.tools.logwatcher.ConfigSupport;
import org.netbeans.modules.tools.logwatcher.WatchDir;
import org.netbeans.modules.tools.logwatcher.output.LogIOTopComponent;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;

/**
 * @TODO find abetter name for dataFolder
 * 
 * @author bhaidu
 */
@ActionID(category = "WatchActions", id = "org.netbeans.modules.tools.logwatcher.actions.WatchAction")
@ActionRegistration(displayName = "Watch")
public class WatchAction extends AbstractAction implements ActionListener {

    private final FileObject dataFolder;

    public WatchAction(FileObject dataFolder) {
        this.dataFolder = dataFolder;
        putValue (NAME, "Watch");
        putValue(SMALL_ICON, ImageUtilities.loadImageIcon("org/netbeans/modules/tools/logwatcher/resources/watch.png", false));
        putValue(SHORT_DESCRIPTION, "Watch Folder");
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        File logFile = ConfigSupport.getLogFileReference(dataFolder);

        if (logFile != null && logFile.isDirectory()) {
            try {
                WatchDir.watch(logFile.toPath());
                dataFolder.setAttribute("c_modified", System.currentTimeMillis());
                LogIOTopComponent ltp = LogIOTopComponent.getInstance(logFile);
                if (ltp != null){
                    ltp.watchStarted();
                }
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }
}
