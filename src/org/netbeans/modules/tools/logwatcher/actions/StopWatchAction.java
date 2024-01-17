package org.netbeans.modules.tools.logwatcher.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.AbstractAction;
import org.netbeans.modules.tools.logwatcher.WatchDir;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataFolder;
import org.openide.util.Exceptions;

@ActionID(category = "RootActions", id = "org.netbeans.modules.tools.logwatcher.actions.StopWatchAction")
@ActionRegistration(displayName = "Stop")
public class StopWatchAction extends AbstractAction implements ActionListener {

    private final DataFolder folder;

    public StopWatchAction(DataFolder folder) {
        this.folder = folder;
        putValue (NAME, "Stop");
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        WatchDir watchDir = WatchDir.getInstance();
        if (watchDir != null){
            watchDir.killProcess();
        }
        
        if (folder != null){
            FileObject primaryFile = folder.getPrimaryFile();
            try {
                primaryFile.setAttribute("c_modified", System.currentTimeMillis());
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }
}