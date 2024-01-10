/*
Licensed to the Apache Software Foundation (ASF)
 */
package org.netbeans.modules.tools.logwatcher.actions;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.AbstractAction;
import org.netbeans.modules.tools.logwatcher.LogFolder;
import org.netbeans.modules.tools.logwatcher.LogNodeSupport;
import static javax.swing.Action.NAME;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataFolder;
import org.openide.util.Exceptions;

/**
 *
 * @author bogdan
 */
@ActionID(id = "org.netbeans.modules.tools.logwatcher.actions.AddFolderAction", category = "RootActions")
@ActionRegistration(displayName = "Open in System")
public class OpenInSystemAction extends AbstractAction implements ActionListener {

    private final DataFolder folder;

    public OpenInSystemAction(DataFolder df) {
        folder = df;
        putValue(NAME, "Open in System");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        FileObject primaryFile = folder.getPrimaryFile();
        LogFolder lf = LogNodeSupport.getLogFolder(primaryFile);
        if (lf != null && lf.dir != null) {
            Desktop desktop = Desktop.getDesktop();
            try {
                desktop.open(lf.dir);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }
}
