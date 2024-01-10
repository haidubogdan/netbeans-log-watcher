package org.netbeans.modules.tools.logwatcher;

import org.netbeans.modules.tools.logwatcher.nodes.RootNode;
import java.util.List;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;

/**
 *
 * @author bhaidu
 */
public class LogFolderChildFactory extends ChildFactory<RootNode> {

    private final RootNode rootNode;

    public LogFolderChildFactory(RootNode rootNode) {
        this.rootNode = rootNode;
    }

    @Override
    protected boolean createKeys(List<RootNode> list) {
        list.add(rootNode);
        return true;
    }

    @Override
    protected Node createNodeForKey(RootNode node) {
        return node;
    }

}