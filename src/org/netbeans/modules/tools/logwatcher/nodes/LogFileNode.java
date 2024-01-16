package org.netbeans.modules.tools.logwatcher.nodes;

import java.awt.Component;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Action;
import javax.swing.JCheckBox;
import static org.netbeans.modules.tools.logwatcher.LogWatcherNode.LOG_DIR_HAS_FILTERS_ATTR;
import static org.netbeans.modules.tools.logwatcher.LogWatcherNode.LOG_FILE_WATCH_ATTR;
import static org.netbeans.modules.tools.logwatcher.LogWatcherNode.LOG_PATH_ATTR;
import org.openide.actions.DeleteAction;
import org.openide.filesystems.FileObject;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.ImageUtilities;
import org.openide.util.Utilities;
import org.openide.util.actions.SystemAction;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author bhaidu
 */
public class LogFileNode extends FilterNode {

    public LogFileNode(Node logFileNode) {
        super(logFileNode, Children.LEAF);
    }

    @Override
    public String getDisplayName() {
        return getLookup().lookup(FileObject.class).getNameExt();
    }

    @Override
    public String getHtmlDisplayName() {
        FileObject shadowFile = getLookup().lookup(FileObject.class);
        String name = shadowFile.getNameExt();
        FileObject parent = shadowFile.getParent();

        Integer checkedStatus = (Integer) shadowFile.getAttribute(LOG_FILE_WATCH_ATTR);
        if (parent.getAttribute(LOG_DIR_HAS_FILTERS_ATTR) != null
                && (checkedStatus == null || checkedStatus == 0)) {
            name += " <font color='AAAAAA'><i>(skipped from watch)</i></font>";
        }

        return name;
    }

    @Override
    public Image getIcon(int type) {

        return ImageUtilities.loadImage("org/netbeans/modules/tools/logwatcher/resources/file.png");
    }

    @Override
    public Action[] getActions(boolean context) {
        List<? extends Action> rootActions = Utilities.actionsForPath("Actions/RootActions");
        List<Action> actions = new ArrayList<>();
//        Action act = SystemAction.get(DeleteAction.class);
//        actions.add(act);
        for (Action action : rootActions) {
            Object name = action.getValue(Action.NAME);
            switch ((String) name) {
                case "Watch":
                case "Delete":
                    actions.add(action);
                    break;
            }
        }
        return actions.toArray(new Action[actions.size()]);
    }

}
