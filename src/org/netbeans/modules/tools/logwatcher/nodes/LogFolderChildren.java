package org.netbeans.modules.tools.logwatcher.nodes;

import java.util.ArrayList;
import java.util.List;
import javax.swing.Action;
import org.netbeans.modules.tools.logwatcher.LogNodeSupport;
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
                return new Node[]{new FolderNode(n, fo)};
            } catch (DataObjectNotFoundException ex) {
                Exceptions.printStackTrace(ex);
            }
        } else {
//            if (n.getName().startsWith("dataDir")){
//                List<Node> filesNodes =  LogNodeSupport.getFileNodesForFolderData(fo);
//                
//                return filesNodes.toArray(new Node[filesNodes.size()]);
//            }
        }

        return new Node[]{};
    }

}
