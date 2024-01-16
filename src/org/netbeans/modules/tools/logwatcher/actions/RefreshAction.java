package org.netbeans.modules.tools.logwatcher.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.swing.AbstractAction;
import org.netbeans.modules.tools.logwatcher.LogNodeSupport;
import org.netbeans.modules.tools.logwatcher.LogWatcherNode;
import static org.netbeans.modules.tools.logwatcher.LogWatcherNode.LOG_FILE_WATCH_ATTR;
import static org.netbeans.modules.tools.logwatcher.LogWatcherNode.LOG_PATH_ATTR;
import org.netbeans.modules.tools.logwatcher.WatchDir;
import org.netbeans.modules.tools.logwatcher.nodes.RootNode;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataFolder;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;

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
        putValue(NAME, "Refresh");
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        FileObject parentFolder = folder.getPrimaryFile();
        FileObject remoteDirFo = LogNodeSupport.fromLogPathAttr(parentFolder);

        if (remoteDirFo == null || !remoteDirFo.isFolder()) {
            return;
        }
        for (FileObject logFile : remoteDirFo.getChildren()) {
            FileObject child =  parentFolder.getFileObject(logFile.getNameExt());
            if (child != null){
                continue;
            }
            try {
                FileObject logFileForNode = parentFolder.createData(logFile.getName(), logFile.getExt());
                logFileForNode.setAttribute(LOG_PATH_ATTR, logFile.getPath());
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        
        Node delegate = folder.getNodeDelegate();
        
        if (delegate instanceof RootNode){
            ((RootNode) delegate).refreshChildren();
        }
        
        try {
            parentFolder.setAttribute("c_modified", System.currentTimeMillis());
            //parentFolder.notify();
            int y = 1;
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        Object origin = ae.getSource();
        int y = 3;
//        parentFolder.refresh(true);
    }
}
