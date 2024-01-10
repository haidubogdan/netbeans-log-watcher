package org.netbeans.modules.tools.logwatcher.nodes;

import org.netbeans.modules.tools.logwatcher.actions.FilterFilesListAction;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Action;
import org.netbeans.modules.tools.logwatcher.LogFolder;
import org.netbeans.modules.tools.logwatcher.LogNodeSupport;
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
    private final LogFolder logFolder;
    
    public FolderNode(Node filterNode, FileObject nodeFo) throws DataObjectNotFoundException {
        super(filterNode, new LogFolderChildren(filterNode));
        this.nodeFo = nodeFo;
        logFolder = LogNodeSupport.getLogFolder(nodeFo);
    }

    @Override
    public String getHtmlDisplayName(){
        String name = nodeFo.getName();
        if (logFolder != null && logFolder.dir != null){
            name += " <font color='AAAAAA'><i>" + logFolder.dir.getPath() + "</i></font>";
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
