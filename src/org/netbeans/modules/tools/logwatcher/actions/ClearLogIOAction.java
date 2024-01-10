package org.netbeans.modules.tools.logwatcher.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.swing.AbstractAction;
import org.netbeans.modules.tools.logwatcher.output.LogIO;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataFolder;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 *
 * @author bhaidu
 */
@ActionID(category = "RootActions", id = "org.netbeans.modules.tools.logwatcher.actions.ClearLogIOAction")
@ActionRegistration(displayName = "#FN_clearLogOutputButton")
@NbBundle.Messages("FN_clearLogOutputButton=Clear")
public class ClearLogIOAction extends AbstractAction implements ActionListener {

    private final DataFolder folder;

    public ClearLogIOAction(DataFolder df) {
        folder = df;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        FileObject primaryFile = folder.getPrimaryFile();
        String name = primaryFile.getName();
        LogIO.clearLogOutput(name);
    }
}
