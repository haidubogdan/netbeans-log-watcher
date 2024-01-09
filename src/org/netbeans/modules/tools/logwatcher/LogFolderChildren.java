package org.netbeans.modules.tools.logwatcher;

import java.io.IOException;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

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
                return new Node[]{new RootNode(n)};
            } catch (DataObjectNotFoundException ex) {
                Exceptions.printStackTrace(ex);
            }
        } else {
            LogFile logFile = getLogFile(fo.getLookup());
            if (logFile != null) {
                return new Node[]{new LogFileNode(n, logFile.getLogFile())};
            }
        }
        // best effort
        return new Node[]{new FilterNode(n)};
    }

    /**
     * Looking up a feed
     */
    private static LogFile getLogFile(Lookup lookup) {
        LogFile f = FileUtil.getConfigObject("LogFiles/sample.instance", LogFile.class);
        if (f == null) {
            throw new IllegalStateException("Bogus file in feeds folder: "
                    + lookup.lookup(FileObject.class));
        }
        return f;
    }

}
