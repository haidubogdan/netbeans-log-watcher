package org.netbeans.modules.tools.logwatcher.nodes;

import org.netbeans.modules.tools.logwatcher.actions.AddFolderAction;
import org.netbeans.modules.tools.logwatcher.actions.StopWatchAction;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Action;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.Utilities;

/**
 * have a look at https://netbeans.apache.org/tutorial/main/tutorials/nbm-nodesapi2/
 * 
 * @author bhaidu
 */
public class RootNode extends FilterNode {

    public RootNode(Node filterNode) throws DataObjectNotFoundException {
        super(filterNode, new LogFolderChildren(filterNode));
    }

    @Override
    public Action[] getActions(boolean bln) {
        List<Action> actions = new ArrayList<>();
        DataFolder fd = getLookup().lookup(DataFolder.class);
        actions.add(new AddFolderAction(fd));
        actions.add(new StopWatchAction());
        return actions.toArray(new Action[actions.size()]);
    }

}
