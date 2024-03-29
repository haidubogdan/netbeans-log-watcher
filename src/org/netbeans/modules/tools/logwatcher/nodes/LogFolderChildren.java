package org.netbeans.modules.tools.logwatcher.nodes;

import java.util.Collections;
import java.util.List;
import org.netbeans.modules.tools.logwatcher.ConfigSupport;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;

/**
 *
 * @author bhaidu
 */
public class LogFolderChildren extends FilterNode.Children {

    LogFolderChildren(Node logFolderNode) {
        super(logFolderNode);
    }

    @Override
    protected Node[] createNodes(Node n) {
        FileObject fo = n.getLookup().lookup(FileObject.class);
        if (fo != null && fo.isFolder()) {
            try {
                if (ConfigSupport.referenceIsBroken(fo)){
                    return new Node[]{new BrokenNode(n)};
                }
                return new Node[]{new FolderNode(n, fo)};
            } catch (DataObjectNotFoundException ex) {
                Exceptions.printStackTrace(ex);
            }
        } else {
            return new Node[]{new LogFileNode(n)};
        }

        return new Node[]{};
    }

    public void refreshNodes() {
        Node[] existingNodes = original.getChildren().getNodes();
        List<Node> empty = Collections.emptyList();
        setKeys(empty);
        setKeys(existingNodes);
    }
}
