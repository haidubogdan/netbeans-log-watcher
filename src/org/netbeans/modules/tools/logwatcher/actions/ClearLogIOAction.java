package org.netbeans.modules.tools.logwatcher.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractAction;
import static javax.swing.Action.NAME;
import static javax.swing.Action.SHORT_DESCRIPTION;
import static javax.swing.Action.SMALL_ICON;
import org.netbeans.modules.tools.logwatcher.output.LogIO;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.filesystems.FileObject;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;

/**
 *
 * @author bhaidu
 */
@ActionID(category = "FolderActions", id = "org.netbeans.modules.tools.logwatcher.actions.ClearLogIOAction")
@ActionRegistration(displayName = "#FN_clearLogOutputButton")
@NbBundle.Messages("FN_clearLogOutputButton=Clear")
public class ClearLogIOAction extends AbstractAction implements ActionListener {

    private final FileObject folderFo;

    public ClearLogIOAction(FileObject folderFo) {
        this.folderFo = folderFo;
        putValue(NAME, "Clear");
        putValue(SMALL_ICON, ImageUtilities.loadImageIcon("org/netbeans/modules/tools/logwatcher/resources/clear.png", false));
        putValue(SHORT_DESCRIPTION, "Clear");
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        LogIO.clearLogOutput(folderFo);
    }
}
