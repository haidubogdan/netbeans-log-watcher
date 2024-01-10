package org.netbeans.modules.tools.logwatcher.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractAction;
import org.netbeans.modules.tools.logwatcher.WatchDir;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;

@ActionID(category = "RootActions", id = "org.netbeans.modules.tools.logwatcher.actions.StopWatchAction")
@ActionRegistration(displayName = "Stop")
public class StopWatchAction extends AbstractAction implements ActionListener {

//    private final DataFolder folder;

    public StopWatchAction() {
        putValue (NAME, "Stop");
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        WatchDir watchDir = WatchDir.getInstance();
        if (watchDir != null){
            watchDir.killProcess();
        }
    }
}