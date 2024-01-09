package org.netbeans.modules.tools.logwatcher;

import java.util.List;
import javax.swing.Action;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.Utilities;

/**
 *
 * @author bhaidu
 */
public class RootNode extends FilterNode {

    public RootNode(Node filterNode) throws DataObjectNotFoundException {
        super(filterNode, new LogFolderChildren(filterNode));
    }

    @Override
    public Action[] getActions(boolean bln) {
        List<? extends Action> rootActions = Utilities.actionsForPath("Actions/RootActions");
        return rootActions.toArray(new Action[rootActions.size()]);
    }

}
