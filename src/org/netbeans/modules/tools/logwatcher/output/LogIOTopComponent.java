/*
Licensed to the Apache Software Foundation (ASF)
 */
package org.netbeans.modules.tools.logwatcher.output;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import org.netbeans.modules.tools.logwatcher.LogWatchTree;
import org.netbeans.modules.tools.logwatcher.actions.ClearLogIOAction;
import org.netbeans.modules.tools.logwatcher.actions.FilterFilesListAction;
import org.netbeans.modules.tools.logwatcher.actions.StopWatchAction;
import org.netbeans.modules.tools.logwatcher.actions.WatchAction;
import org.openide.filesystems.FileObject;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.IOContainer;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;
import org.openide.windows.RetainLocation;
import org.openide.windows.TopComponent;

/**
 *
 * @author bogdan
 */
@TopComponent.Description(
        preferredID = "LogIOTopComponent",
        persistenceType = TopComponent.PERSISTENCE_NEVER)
@RetainLocation(value = "output")
@Messages({
    "CTL_LogIOTopComponent=LogIOTopComponent",
    "HINT_LogIOTopComponent=This is aLogIOTopComponent"
})
public final class LogIOTopComponent extends TopComponent {

    public static String ID;
    private LogIOProvider ioProvider;
    private InputOutput io;
    private JToolBar actionBar;

    private JPanel infoPanel;
    private JPanel infoPanelContent;
    private final File watchFolder;
    private JButton watchButton;
    private JButton stopButton;
    private JPanel watchLabelContainer;

    private static final HashMap<String, LogIOTopComponent> instances = new HashMap<>();

    public LogIOTopComponent(File watchFolder) {
        this.watchFolder = watchFolder;
        initComponents();
        ID = "logwatch" + watchFolder.getName();
    }

    //might use window component
    public static LogIOTopComponent getInstance(File folder) {
        String name = folder.getName();
        if (instances.containsKey(name)) {
            return instances.get(name);
        }

        LogIOTopComponent instance = new LogIOTopComponent(folder);
        instances.put(name, instance);
        return instance;
    }

    protected void initComponents() {
        setName("Log watch - " + watchFolder.getName());
        setToolTipText("Log watch - " + watchFolder.getName());

        setLayout(new BorderLayout());
        infoPanel = new JPanel();
        infoPanel.setLayout(new BorderLayout());

        JScrollPane scrollPane = new JScrollPane();
        infoPanelContent = new JPanel(new FlowLayout(FlowLayout.LEADING));
        scrollPane.setViewportView(infoPanelContent);
        infoPanel.add(scrollPane, BorderLayout.CENTER);

        add(infoPanel);
        actionBar = new JToolBar();
        actionBar.setFloatable(false);
        actionBar.setOrientation(JToolBar.VERTICAL);
        infoPanel.add(actionBar, BorderLayout.WEST);
        ioProvider = new LogIOProvider(infoPanelContent);
        IOContainer container = IOContainer.create(ioProvider);
        List<Action> actions = new ArrayList<>();
        FileObject nodeFolder = LogWatchTree.getFolderFileObject(watchFolder.getName());
        if (nodeFolder != null) {
            actions.add(new WatchAction(nodeFolder));
            actions.add(new StopWatchAction(nodeFolder));
            actions.add(new ClearLogIOAction(nodeFolder));
            actions.add(new FilterFilesListAction(nodeFolder));
        }
        io = IOProvider.getDefault().getIO(getName(), actions.toArray(new Action[0]), container);
        infoPanel.validate();
        infoPanel.repaint();

    }

    public InputOutput getIo() {
        return io;
    }

    private void setButtons(Action[] actions) {
        if (actions == null) {
            actions = new Action[0];
        }
        JButton[] buttons = new JButton[actions.length];
        for (int ax = 0; ax < actions.length; ax++) {
            Action a = actions[ax];
            JButton b = new JButton(a);
            //not sure if it is the best approach
            if (a instanceof StopWatchAction) {
                stopButton = b;
            } else if (a instanceof WatchAction) {
                watchButton = b;
            }
            buttons[ax] = adjustButton(b);
        }

        actionBar.removeAll();
        JLabel watchLabel = new JLabel("", JLabel.CENTER);
        watchLabel.setIcon(new ImageIcon(
                getClass().getClassLoader().
                        getResource("org/netbeans/modules/tools/logwatcher/resources/wait16.gif")));
        watchLabelContainer = new JPanel(new BorderLayout());
        watchLabelContainer.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        watchLabelContainer.add(watchLabel);
        watchLabelContainer.setVisible(false);
        
        actionBar.add(watchLabelContainer, BorderLayout.CENTER);
        if (buttons.length != 0) {
            actionBar.setVisible(true);
            for (JButton b : buttons) {
                actionBar.add(b);
            }
        } else {
            actionBar.setVisible(false);
        }
        
        actionBar.revalidate();
        actionBar.repaint();
    }

    public void watchStarted() {
        watchButton.setEnabled(false);
        stopButton.setEnabled(true);
        watchLabelContainer.setVisible(true);
    }

    public void watchStoped() {
        watchButton.setEnabled(true);
        stopButton.setEnabled(false);
        watchLabelContainer.setVisible(false);
    }

    private JButton adjustButton(JButton b) {
        b.setBorderPainted(false);
        b.setOpaque(false);
        b.setText(null);
        b.putClientProperty("hideActionText", Boolean.TRUE);	// NOI18N
        // NOI18N
        return b;
    }

    private class LogIOProvider implements IOContainer.Provider {

        private JComponent parent;

        public LogIOProvider(JComponent parent) {
            this.parent = parent;
        }

        @Override
        public void open() {
        }

        @Override
        public void requestActive() {
        }

        @Override
        public void requestVisible() {
        }

        @Override
        public boolean isActivated() {
            return true;
        }

        @Override
        public void add(JComponent comp, IOContainer.CallBacks cb) {
            assert parent != null;
            parent.setLayout(new BorderLayout());
            parent.add(comp, BorderLayout.CENTER);
        }

        @Override
        public void remove(JComponent comp) {
            assert parent != null;
            parent.remove(comp);
        }

        @Override
        public void select(JComponent comp) {
        }

        @Override
        public JComponent getSelected() {
            return parent;
        }

        @Override
        public void setTitle(JComponent comp, String name) {
        }

        @Override
        public void setToolTipText(JComponent comp, String text) {
        }

        @Override
        public void setIcon(JComponent comp, Icon icon) {
        }

        @Override
        public void setToolbarActions(JComponent comp, Action[] toolbarActions) {
            setButtons(toolbarActions);
        }

        @Override
        public boolean isCloseable(JComponent comp) {
            return false;
        }

        private void close() {
            parent = null;
        }

    }
}
