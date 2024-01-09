/*
Licensed to the Apache Software Foundation (ASF)
 */
package org.netbeans.modules.tools.logwatcher;

import java.util.ArrayList;
import java.util.List;
import javax.swing.Action;
import org.netbeans.api.core.ide.ServicesTabNodeRegistration;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

/**
 *
 * @author bogdan
 */
public class LogWatcherNode extends AbstractNode {

    private static final String LOG_WATCHER_ICON = "org/netbeans/modules/tools/logwatcher/resources/icon.png"; // NOI18N

    private static LogWatcherNode node;

    private LogWatcherNode(LogFolderChildFactory factory) {
        super(Children.create(factory, true));

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
            LogFolderChildFactory factory = new LogFolderChildFactory(rootFolder);
            List<FileObject> files = new ArrayList<>();
            files.add(rootFolder);
            try {
                Node rootFolderNode = DataObject.find(rootFolder).getNodeDelegate();
            } catch (DataObjectNotFoundException ex) {
                Exceptions.printStackTrace(ex);
            }
            factory.createKeys(files);
            node = new LogWatcherNode(factory);
        }
        return node;
    }

    @Override
    public Action[] getActions(boolean context) {
        List<? extends Action> rootActions = Utilities.actionsForPath("Actions/LogWatcher");
        return rootActions.toArray(new Action[rootActions.size()]);
    }
}
