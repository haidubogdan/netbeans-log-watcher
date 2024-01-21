/*
Licensed to the Apache Software Foundation (ASF)
 */
package org.netbeans.modules.tools.logwatcher.actions;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.swing.AbstractAction;
import org.netbeans.modules.tools.logwatcher.ConfigSupport;
import static javax.swing.Action.NAME;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;

/**
 *
 * @author bogdan
 */
@ActionID(id = "org.netbeans.modules.tools.logwatcher.actions.OpenInSystemAction", category = "LogSystemActions")
@ActionRegistration(displayName = "Open in System")
public class OpenInSystemAction extends AbstractAction implements ActionListener {

    private final FileObject nodeFile;

    public OpenInSystemAction(FileObject nodeFile) {
        this.nodeFile = nodeFile;
        putValue(NAME, "Open in System");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        File logFile = ConfigSupport.getLogFileReference(nodeFile);
        
        if (logFile != null) {
            Desktop desktop = Desktop.getDesktop();
            try {
                desktop.open(logFile);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }
}
