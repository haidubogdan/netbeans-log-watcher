package org.netbeans.modules.tools.logwatcher.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractAction;
import static javax.swing.Action.NAME;
import static javax.swing.Action.SHORT_DESCRIPTION;
import static javax.swing.Action.SMALL_ICON;
import org.netbeans.modules.tools.logwatcher.ConfigSupport;
import org.netbeans.modules.tools.logwatcher.ui.FilterDialog;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.filesystems.FileObject;
import org.openide.util.ImageUtilities;

/**
 *
 * @author bhaidu
 */
@ActionID(category = "FolderActions", id = "org.netbeans.modules.tools.logwatcher.actions.FilterFilesListAction")
@ActionRegistration(displayName = "Filter Files", iconInMenu = true, iconBase="org/netbeans/modules/tools/logwatcher/resources/filter.png")
public class FilterFilesListAction extends AbstractAction implements ActionListener {

    private final FileObject folderFo;
    FilterDialog dialog;

    public FilterFilesListAction(FileObject folderFo) {
        this.folderFo = folderFo;
        putValue(NAME, "Filter Files");
        putValue(SMALL_ICON, ImageUtilities.loadImageIcon("org/netbeans/modules/tools/logwatcher/resources/filter.png", false));
        putValue(SHORT_DESCRIPTION, "Filter Files");
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        FileObject logFo = ConfigSupport.getLogReference(folderFo);

        if (logFo != null && logFo.isFolder()) {
            dialog = new FilterDialog("Filtered  files", folderFo, logFo);
            dialog.setVisible(true);
        }

    }

}
