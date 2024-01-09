package org.netbeans.modules.tools.logwatcher;

import java.awt.Image;
import javax.swing.Action;
import org.openide.actions.DeleteAction;
import org.openide.filesystems.FileObject;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.ImageUtilities;
import org.openide.util.actions.SystemAction;
import org.openide.util.lookup.Lookups;
/**
 *
 * @author bhaidu
 */
public class LogFileNode extends FilterNode {

    LogFileNode(Node logFileNode, FileObject logFile) {
        super(logFileNode, Children.create(new LogFolderChildFactory(logFile), false), Lookups.fixed(logFile));
    }

    @Override
    public String getDisplayName() {
        return getLookup().lookup(FileObject.class).getName();
    }

    @Override
    public Image getIcon(int type) {
        return null;
    }

    @Override
    public Image getOpenedIcon(int type) {
        return getIcon(type);
    }

    @Override
    public Action[] getActions(boolean context) {
        return new Action[]{SystemAction.get(DeleteAction.class)};
    }

}
