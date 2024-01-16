/*
Licensed to the Apache Software Foundation (ASF)
 */
package org.netbeans.modules.tools.logwatcher;

import java.io.File;
import java.io.IOException;
import org.netbeans.modules.tools.logwatcher.nodes.RootNode;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Action;
import org.netbeans.api.core.ide.ServicesTabNodeRegistration;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 * todo add a empty node
 * 
 * @author bogdan
 */
public class LogWatcherNode extends AbstractNode {

    private static final String LOG_WATCHER_ICON = "org/netbeans/modules/tools/logwatcher/resources/icon.png"; // NOI18N
    public static final String LOG_PATH_ATTR = "log_path";
    public static final String LOG_LAST_REFRESHED = "last_refreshed";
    public static final String LOG_DIR_HAS_FILTERS_ATTR = "has_filters";
    public static final String LOG_FILE_WATCH_ATTR = "log_watch";
    private static LogWatcherNode node;
    private static RootNode rootNode;

    private LogWatcherNode(LogFolderChildFactory factory, Lookup lookup) {
        super(Children.create(factory, true), lookup);

        setName("logwatcher"); // NOI18N
        setDisplayName("Log watcher");
        setShortDescription("Log watcher");
        setIconBaseWithExtension(LOG_WATCHER_ICON);
    }

    @ServicesTabNodeRegistration(
            name = "logwatcher",
            displayName = "Log watcher",
            shortDescription = "Log watcher",
            iconResource = "org/netbeans/modules/tools/logwatcher/resources/icon.png",
            position = 1000
    )
    public static synchronized LogWatcherNode getInstance() {
        if (node == null) {
            try {
                Node rootNodeFolder = LogWatchTree.getRootNode();
                DataFolder fd = rootNodeFolder.getLookup().lookup(DataFolder.class);

                for (DataObject dob : fd.getChildren()) {
                    FileObject fo = dob.getLookup().lookup(FileObject.class);
                    if (fo == null) {
                        continue;
                    }
                    String path = (String) fo.getAttribute(LOG_PATH_ATTR);
                    if (path != null) {
                        FileObject realDirFo = FileUtil.toFileObject(new File(path));
                        if (realDirFo == null){
                            //broken
                            continue;
                        }
                        try {
                            for (FileObject realChild : realDirFo.getChildren()){
                                if (realChild.isFolder() || fo.getFileObject(realChild.getNameExt())!= null){
                                    continue;
                                }
                                FileObject logNode = fo.createData(realChild.getName(), realChild.getExt());
                                logNode.setAttribute(LOG_PATH_ATTR, realChild.getPath());
                            }
                        } catch (IOException ex) {
                            Exceptions.printStackTrace(ex);
                        }
                    }
                    
                }

                rootNode = new RootNode(rootNodeFolder);
                List<RootNode> nodes = new ArrayList<>();
                nodes.add(rootNode);
                LogFolderChildFactory factory = new LogFolderChildFactory(rootNode);
                factory.createKeys(nodes);
                NodeLogLookup lookup = new NodeLogLookup();
                node = new LogWatcherNode(factory, lookup);
            } catch (DataObjectNotFoundException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        return node;
    }

    @Override
    public Action[] getActions(boolean context) {
        return new Action[0];
    }
    
    public static RootNode getRootNode(){
        if (node != null){
            return node.getLookup().lookup(RootNode.class);
        }
        return rootNode;
    }
}
