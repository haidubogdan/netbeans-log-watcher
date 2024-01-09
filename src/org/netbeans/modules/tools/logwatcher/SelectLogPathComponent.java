package org.netbeans.modules.tools.logwatcher;

import java.awt.BorderLayout;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.explorer.ExplorerManager;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.TopComponent;
import java.awt.BorderLayout;
import java.util.logging.Logger;
import javax.swing.ActionMap;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
import org.openide.util.ImageUtilities;
import org.netbeans.api.settings.ConvertAsProperties;
import org.netbeans.modules.tools.logwatcher.Bundle;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.explorer.view.BeanTreeView;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;

/**
 * inspired from https://netbeans.apache.org/tutorial/main/tutorials/nbm-feedreader/
 * @author bhaidu
 */
/*
@TopComponent.Description(
        preferredID = "SelectLogPathComponent",
        persistenceType = TopComponent.PERSISTENCE_ALWAYS)
@TopComponent.Registration(
        mode = "explorer",
        openAtStartup = true)
@ActionID(
        category = "Window",
        id = "org.netbeans.modules.tools.logwatcher.SelectLogPathComponent")
@ActionReferences({
    @ActionReference(
            path = "Menu/Window",
            position = 0)
})
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_ReadLogAction")
@Messages({
    "CTL_ReadLogAction=Open Log File Window"})
*/
public class SelectLogPathComponent extends TopComponent implements ExplorerManager.Provider {

    private final ExplorerManager manager = new ExplorerManager();

    public SelectLogPathComponent() {
        setName(Bundle.CTL_ReadLogAction());
        setToolTipText("read log tool");
        setLayout(new BorderLayout());
        add(new BeanTreeView(), BorderLayout.CENTER);
        try {
            FileObject rssFeedsFolder = FileUtil.getConfigFile("LogFiles");
            Node rssFeedsNode = DataObject.find(rssFeedsFolder).getNodeDelegate();
            manager.setRootContext(new RootNode(rssFeedsNode));
        } catch (DataObjectNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        }
        ActionMap map = getActionMap();
        map.put("delete", ExplorerUtils.actionDelete(manager, true));
        associateLookup(ExplorerUtils.createLookup(manager, map));
    }

    @Override
    public ExplorerManager getExplorerManager() {
        return manager;
    }
}
