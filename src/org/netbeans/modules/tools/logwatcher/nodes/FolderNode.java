package org.netbeans.modules.tools.logwatcher.nodes;

import org.netbeans.modules.tools.logwatcher.actions.FilterFilesListAction;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Action;
import static org.netbeans.modules.tools.logwatcher.LogWatcherNode.LOG_PATH_ATTR;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.Utilities;

/**
 *
 * @author bhaidu
 */
public class FolderNode extends FilterNode {

    private final FileObject nodeFo;
    private final String logPath;
    
    public FolderNode(Node filterNode, FileObject nodeFo) throws DataObjectNotFoundException {
        super(filterNode, new LogFolderChildren(filterNode));
        this.nodeFo = nodeFo;
        this.logPath = (String) nodeFo.getAttribute(LOG_PATH_ATTR);
    }

    @Override
    public String getHtmlDisplayName(){
        String name = nodeFo.getName();
        if (logPath != null ){
            name += " <font color='AAAAAA'><i>" + logPath + "</i></font>";
        }
        return name;
    }
    
    @Override
    public Action[] getActions(boolean bln) {
        List<? extends Action> rootActions = Utilities.actionsForPath("Actions/RootActions");
        List<Action> actions = new ArrayList<>();
        actions.add(new FilterFilesListAction(getLookup().lookup(DataFolder.class)));
        actions.addAll(rootActions);
        return actions.toArray(new Action[actions.size()]);
    }

}
