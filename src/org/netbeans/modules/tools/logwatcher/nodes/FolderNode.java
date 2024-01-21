package org.netbeans.modules.tools.logwatcher.nodes;

import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Action;
import org.netbeans.modules.tools.logwatcher.ConfigSupport;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.ImageUtilities;
import org.openide.util.Utilities;
import org.netbeans.modules.tools.logwatcher.WatchDir;
import org.netbeans.modules.tools.logwatcher.actions.OpenInSystemAction;

/**
 *
 * @author bhaidu
 */
public class FolderNode extends FilterNode {

    private final FileObject nodeFo;
    
    public FolderNode(Node filterNode, FileObject nodeFo) throws DataObjectNotFoundException {
        super(filterNode, new LogFolderChildren(filterNode));
        this.nodeFo = nodeFo;
    }
        
    @Override
    public Image getIcon(int param) {
        return badge(super.getIcon(param));
    }
    
    @Override
    public Image getOpenedIcon(int param){
        return badge(super.getOpenedIcon(param));
    }
    
    private Image badge(Image retValue) {
        if (WatchDir.getInstance() != null && WatchDir.getInstance().isProcessRunning() 
                && WatchDir.getInstance().isRegistered(nodeFo)){
            Image ann = ImageUtilities.loadImage("org/netbeans/modules/tools/logwatcher/resources/watching.png"); //NOI18N
            retValue = ImageUtilities.mergeImages(retValue, ann, -2, 7);//NOI18N
        }
        return retValue;
    }
    
    @Override
    public String getHtmlDisplayName(){
        String name = nodeFo.getName();
        String logPath = ConfigSupport.getLogFileReferencePath(nodeFo);
        if (logPath != null ){
            name += " <font color='AAAAAA'><i>" + logPath + "</i></font>";
        }
        return name;
    }
    
    @Override
    public Action[] getActions(boolean bln) {
        List<Action> actions = new ArrayList<>();
        actions.add( new OpenInSystemAction(nodeFo));
        actions.add(null);
        actions.addAll(Utilities.actionsForPath("Actions/WatchActions"));
        actions.add(null);
        actions.addAll(Utilities.actionsForPath("Actions/FolderActions"));
        return actions.toArray(new Action[0]);
    }

}
