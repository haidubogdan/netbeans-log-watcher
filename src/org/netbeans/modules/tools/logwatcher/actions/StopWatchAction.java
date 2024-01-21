package org.netbeans.modules.tools.logwatcher.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.swing.AbstractAction;
import org.netbeans.modules.tools.logwatcher.ConfigSupport;
import org.netbeans.modules.tools.logwatcher.WatchDir;
import org.netbeans.modules.tools.logwatcher.output.LogIOTopComponent;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;

@ActionID(category = "WatchActions", id = "org.netbeans.modules.tools.logwatcher.actions.StopWatchAction")
@ActionRegistration(displayName = "Stop", iconInMenu = true, iconBase = "org/netbeans/modules/tools/logwatcher/resources/stop.png")
public class StopWatchAction extends AbstractAction implements ActionListener {

    private final FileObject folderFo;

    public StopWatchAction(FileObject folderFo) {
        this.folderFo = folderFo;
        putValue(NAME, "Stop");
        putValue(SMALL_ICON, ImageUtilities.loadImageIcon("org/netbeans/modules/tools/logwatcher/resources/stop.png", false));
        putValue(SHORT_DESCRIPTION, "Stop Watch");
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        WatchDir watchDir = WatchDir.getInstance();

        if (folderFo != null) {
            try {
                folderFo.setAttribute("c_modified", System.currentTimeMillis());
                File logFile = ConfigSupport.getLogFileReference(folderFo);

                if (logFile != null) {
                    //might change for all childrens
                    watchDir.remove(logFile.toPath());
                    LogIOTopComponent ltp = LogIOTopComponent.getInstance(logFile);
                    if (ltp != null) {
                        ltp.watchStoped();
                    }
                }
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        } else {
            if (watchDir != null) {
                watchDir.killProcess();
            }
        }
    }
}
