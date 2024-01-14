/*
Licensed to the Apache Software Foundation (ASF)
 */
package org.netbeans.modules.tools.logwatcher;

import java.io.IOException;
import org.netbeans.modules.tools.logwatcher.nodes.RootNode;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Action;
import javax.swing.ActionMap;
import org.netbeans.api.core.ide.ServicesTabNodeRegistration;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

/**
 *
 * @author bogdan
 */
public class LogWatcherNode extends AbstractNode {

    private static final String LOG_WATCHER_ICON = "org/netbeans/modules/tools/logwatcher/resources/icon.png"; // NOI18N
    private static LogWatcherNode node;

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
            FileObject rootFolder = FileUtil.getConfigFile("LogFiles");

            //we could create a list subfolder
            //todo use attribute
            for (FileObject child : rootFolder.getChildren()) {
                if (child.isFolder()) {
                    FileObject dataFileObj = child.getFileObject(LogNodeSupport.FOLDER_DATA_NAME + child.getName());
                    FileObject dirFileObj = null;
                    if (dataFileObj != null) {
                        dirFileObj = LogNodeSupport.getRealFileObject(dataFileObj);
                    }
                    try {
                        if (child.getFileObject("test.log") == null) {
                            child.createData("test", "log");
                        }

                        if (child.getFileObject("test1.log") == null) {
                            child.createData("test1", "log");
                        }
                        if (dirFileObj != null){
                            for (FileObject realChild : dirFileObj.getChildren()){
                                if (child.getFileObject(realChild.getNameExt()) == null){
                                    FileObject nodeChild = child.createData(realChild.getName(), realChild.getExt());
                                    nodeChild.setAttribute("log_path", child.getPath());
                                } else {
                                    realChild.getAttribute("log_path");
                                    int x = 1;
                                }
                            }
                        }
                    } catch (IOException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
            }

            try {
                Node rootNodeFolder = DataObject.find(rootFolder).getNodeDelegate();
                RootNode rootNode = new RootNode(rootNodeFolder);
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
}
