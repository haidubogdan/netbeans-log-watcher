package org.netbeans.modules.tools.logwatcher.nodes;

import java.awt.Component;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Action;
import javax.swing.JCheckBox;
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

    public JCheckBox checkBox;

    public LogFileNode(Node logFileNode) {
        super(logFileNode, Children.LEAF);
        checkBox = new JCheckBox();
        checkBox.setOpaque(false);
    }

    @Override
    public String getDisplayName() {
        return getLookup().lookup(FileObject.class).getName();
    }

    @Override
    public Image getOpenedIcon(int type) {
        return getIcon(type);
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
                case "Refresh":
                case "Delete":
                    actions.add(action);
                    break;
            }
        }
        return actions.toArray(new Action[actions.size()]);
    }

    @Override
    public boolean hasCustomizer() {
        return true;
    }

    @Override
    public Component getCustomizer() {
        return checkBox;
    }
}
