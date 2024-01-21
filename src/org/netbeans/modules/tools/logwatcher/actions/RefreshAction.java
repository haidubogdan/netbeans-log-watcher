package org.netbeans.modules.tools.logwatcher.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.AbstractAction;
import org.netbeans.modules.tools.logwatcher.ConfigSupport;
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
@ActionID(category = "FolderActions", id = "org.netbeans.modules.tools.logwatcher.actions.RefreshAction")
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
        FileObject remoteDirFo = ConfigSupport.getLogReference(parentFolder);

        if (remoteDirFo == null || !remoteDirFo.isFolder()) {
            return;
        }
        if (ConfigSupport.referenceIsBroken(parentFolder)) {
            ConfigSupport.setBroken(remoteDirFo, 0);
        }
        for (FileObject logFile : remoteDirFo.getChildren()) {
            FileObject child = parentFolder.getFileObject(logFile.getNameExt());
            if (child != null) {
                continue;
            }
            try {
                FileObject logFileForNode = parentFolder.createData(logFile.getName(), logFile.getExt());
                ConfigSupport.setLogReferencePath(logFileForNode, logFile.getPath());
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }

        Node delegate = folder.getNodeDelegate();

        if (delegate instanceof RootNode) {
            ((RootNode) delegate).refreshChildren();
        }

        try {
            parentFolder.setAttribute("c_modified", System.currentTimeMillis());
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
}
