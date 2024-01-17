package org.netbeans.modules.tools.logwatcher.nodes;

import org.netbeans.modules.tools.logwatcher.actions.AddFolderAction;
import org.netbeans.modules.tools.logwatcher.actions.StopWatchAction;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Action;
import org.netbeans.modules.tools.logwatcher.actions.AddLogFileAction;
import org.openide.filesystems.FileAttributeEvent;
import org.openide.filesystems.FileChangeListener;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileRenameEvent;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.Utilities;

/**
 * have a look at
 * https://netbeans.apache.org/tutorial/main/tutorials/nbm-nodesapi2/
 *
 * @author bhaidu
 */
public class RootNode extends FilterNode {

    private FileChangeListener fileChangeListener;

    public RootNode(Node filterNode) throws DataObjectNotFoundException {
        super(filterNode, new LogFolderChildren(filterNode));
        this.fileChangeListener = new FileChangeListener() {
            @Override
            public void fileAttributeChanged(FileAttributeEvent fae) {
                refreshChildren();
            }

            @Override
            public void fileChanged(FileEvent fileAttributeEvent) {
                int x = 1;
            }

            @Override
            public void fileFolderCreated(FileEvent fe) {
                refreshChildren();
            }

            @Override
            public void fileDataCreated(FileEvent fe) {
            }

            @Override
            public void fileDeleted(FileEvent fe) {
            }

            @Override
            public void fileRenamed(FileRenameEvent fre) {
            }
        };
        
        FileObject fo = filterNode.getLookup().lookup(FileObject.class);
        FileChangeListener weakcl = FileUtil.weakFileChangeListener(this.fileChangeListener, fo);
        fo.addFileChangeListener(weakcl);
        
        
    }

    @Override
    public Action[] getActions(boolean bln) {
        List<Action> actions = new ArrayList<>();
        DataFolder fd = getLookup().lookup(DataFolder.class);
        actions.add(new AddFolderAction(fd));
        actions.add(new AddLogFileAction(fd));
        actions.add(new StopWatchAction(fd));
        return actions.toArray(new Action[actions.size()]);
    }

    public void refreshChildren() {
        ((LogFolderChildren) getChildren()).refreshNodes();
    }
}
