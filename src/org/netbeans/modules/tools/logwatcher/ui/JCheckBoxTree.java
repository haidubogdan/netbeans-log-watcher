/*
Licensed to the Apache Software Foundation (ASF)
 */
package org.netbeans.modules.tools.logwatcher.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.EventListener;
import java.util.EventObject;
import java.util.HashMap;
import java.util.HashSet;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.event.EventListenerList;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import org.netbeans.modules.tools.logwatcher.ConfigSupport;
import org.openide.filesystems.FileObject;

/**
 *
 * @author bogdan
 */
public class JCheckBoxTree extends JTree {

    public static String ID = "log_files_tree";
    private static final long serialVersionUID = -4194122328392241790L;

    JCheckBoxTree selfPointer = this;

    // Defining data structure that will enable to fast check-indicate the state of each node
    // It totally replaces the "selection" mechanism of the JTree
    public class CheckedNode {

        public boolean isSelected;
        boolean hasChildren;
        boolean allChildrenSelected;

        public CheckedNode(boolean isSelected_, boolean hasChildren_, boolean allChildrenSelected_) {
            isSelected = isSelected_;
            hasChildren = hasChildren_;
            allChildrenSelected = allChildrenSelected_;
        }
    }
    HashMap<TreePath, CheckedNode> nodesCheckingState;
    HashSet<TreePath> checkedPaths = new HashSet<>();

    // Defining a new event type for the checking mechanism and preparing event-handling mechanism
    protected EventListenerList listenerLists = new EventListenerList();

    public class CheckChangeEvent extends EventObject {

        private static final long serialVersionUID = -8100230309044193368L;

        public CheckChangeEvent(Object source) {
            super(source);
        }
    }

    public HashMap<TreePath, CheckedNode> getPaths() {
        return nodesCheckingState;
    }

    public interface CheckChangeEventListener extends EventListener {

        public void checkStateChanged(CheckChangeEvent event);
    }

    public void addCheckChangeEventListener(CheckChangeEventListener listener) {
        listenerLists.add(CheckChangeEventListener.class, listener);
    }

    public void removeCheckChangeEventListener(CheckChangeEventListener listener) {
        listenerLists.remove(CheckChangeEventListener.class, listener);
    }

    void fireCheckChangeEvent(CheckChangeEvent evt) {
        Object[] listeners = listenerLists.getListenerList();
        for (int i = 0; i < listeners.length; i++) {
            if (listeners[i] == CheckChangeEventListener.class) {
                ((CheckChangeEventListener) listeners[i + 1]).checkStateChanged(evt);
            }
        }
    }

    // Override
    @Override
    public void setModel(TreeModel newModel) {
        super.setModel(newModel);
        resetCheckingState();
    }

    // New method that returns only the checked paths (totally ignores original "selection" mechanism)
    public TreePath[] getCheckedPaths() {
        return checkedPaths.toArray(new TreePath[0]);
    }

    // Returns true in case that the node is selected, has children but not all of them are selected
    public boolean isSelectedPartially(TreePath path) {
        CheckedNode cn = nodesCheckingState.get(path);
        return cn.isSelected && cn.hasChildren && !cn.allChildrenSelected;
    }

    private void resetCheckingState() {
        nodesCheckingState = new HashMap<>();
        checkedPaths = new HashSet<>();
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) getModel().getRoot();
        if (node == null) {
            return;
        }
        addSubtreeToCheckingStateTracking(node);
    }

    // Creating data structure of the current model for the checking mechanism
    private void addSubtreeToCheckingStateTracking(DefaultMutableTreeNode node) {
        TreeNode[] path = node.getPath();
        TreePath tp = new TreePath(path);
        boolean checked = false;
        if (node.isLeaf()) {
            Object userObj = node.getUserObject();
            if (userObj != null && userObj instanceof FileObject) {
                //SHOULD USE A PROXY OBJ
                FileObject fo = (FileObject) node.getUserObject();
                if (ConfigSupport.fileIsMarkedForWatching(fo)) {
                    checked = true;
                }
            }
        }
        CheckedNode cn = new CheckedNode(checked, node.getChildCount() > 0, false);
        nodesCheckingState.put(tp, cn);
        for (int i = 0; i < node.getChildCount(); i++) {
            addSubtreeToCheckingStateTracking((DefaultMutableTreeNode) tp.pathByAddingChild(node.getChildAt(i)).getLastPathComponent());
        }
    }

    // Overriding cell renderer by a class that ignores the original "selection" mechanism
    // It decides how to show the nodes due to the checking-mechanism
    private class CheckBoxCellRenderer extends JPanel implements TreeCellRenderer {

        private static final long serialVersionUID = -7341833835878991719L;
        JCheckBox checkBox;

        public CheckBoxCellRenderer() {
            super();
            this.setLayout(new BorderLayout());
            checkBox = new JCheckBox();
            add(checkBox, BorderLayout.CENTER);
            setOpaque(false);
        }

        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value,
                boolean selected, boolean expanded, boolean leaf, int row,
                boolean hasFocus) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
            Object obj = node.getUserObject();
            TreePath tp = new TreePath(node.getPath());
            CheckedNode cn = nodesCheckingState.get(tp);
            if (cn == null) {
                return this;
            }
            checkBox.setSelected(cn.isSelected);
            if (obj instanceof FileObject) {
                checkBox.setText(((FileObject) obj).getNameExt());
            } else if (obj != null) {
                checkBox.setText(obj.toString());
            } else {
                checkBox.setText("n-a");
            }

            checkBox.setOpaque(cn.isSelected && cn.hasChildren && !cn.allChildrenSelected);
            return this;
        }
    }

    public JCheckBoxTree() {
        super();
        // Disabling toggling by double-click
        this.setToggleClickCount(0);
        // Overriding cell renderer by new one defined above
        CheckBoxCellRenderer cellRenderers = new CheckBoxCellRenderer();
        this.setCellRenderer(cellRenderers);

        // Overriding selection model by an empty one
        DefaultTreeSelectionModel dtsm = new DefaultTreeSelectionModel() {
            private static final long serialVersionUID = -8190634240451667286L;

            // Totally disabling the selection mechanism
            @Override
            public void setSelectionPath(TreePath path) {
            }

            @Override
            public void addSelectionPath(TreePath path) {
            }

            @Override
            public void removeSelectionPath(TreePath path) {
            }

            @Override
            public void setSelectionPaths(TreePath[] pPaths) {
            }
        };
        // Calling checking mechanism on mouse click
        this.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent arg0) {
                TreePath tp = selfPointer.getPathForLocation(arg0.getX(), arg0.getY());
                if (tp == null) {
                    return;
                }
                boolean checkMode = !nodesCheckingState.get(tp).isSelected;
                checkSubTree(tp, checkMode);
                updatePredecessorsWithCheckMode(tp, checkMode);
                // Firing the check change event
                fireCheckChangeEvent(new CheckChangeEvent(new Object()));
                // Repainting tree after the data structures were updated
                selfPointer.repaint();
            }

            @Override
            public void mouseEntered(MouseEvent arg0) {
            }

            @Override
            public void mouseExited(MouseEvent arg0) {
            }

            @Override
            public void mousePressed(MouseEvent arg0) {
            }

            @Override
            public void mouseReleased(MouseEvent arg0) {
            }
        });
        this.setSelectionModel(dtsm);
    }

    // When a node is checked/unchecked, updating the states of the predecessors
    protected void updatePredecessorsWithCheckMode(TreePath tp, boolean check) {
        TreePath parentPath = tp.getParentPath();
        // If it is the root, stop the recursive calls and return
        if (parentPath == null) {
            return;
        }
        CheckedNode parentCheckedNode = nodesCheckingState.get(parentPath);
        DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) parentPath.getLastPathComponent();
        parentCheckedNode.allChildrenSelected = true;
        parentCheckedNode.isSelected = false;
        for (int i = 0; i < parentNode.getChildCount(); i++) {
            TreePath childPath = parentPath.pathByAddingChild(parentNode.getChildAt(i));
            CheckedNode childCheckedNode = nodesCheckingState.get(childPath);
            // It is enough that even one subtree is not fully selected
            // to determine that the parent is not fully selected
            if (!childCheckedNode.allChildrenSelected) {
                parentCheckedNode.allChildrenSelected = false;
            }
            // If at least one child is selected, selecting also the parent
            if (childCheckedNode.isSelected) {
                parentCheckedNode.isSelected = true;
            }
        }
        if (parentCheckedNode.isSelected) {
            checkedPaths.add(parentPath);
        } else {
            checkedPaths.remove(parentPath);
        }
        // Go to upper predecessor
        updatePredecessorsWithCheckMode(parentPath, check);
    }

    // Recursively checks/unchecks a subtree
    protected void checkSubTree(TreePath tp, boolean check) {
        CheckedNode cn = nodesCheckingState.get(tp);
        cn.isSelected = check;
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) tp.getLastPathComponent();
        for (int i = 0; i < node.getChildCount(); i++) {
            checkSubTree(tp.pathByAddingChild(node.getChildAt(i)), check);
        }
        cn.allChildrenSelected = check;
        if (check) {
            checkedPaths.add(tp);
        } else {
            checkedPaths.remove(tp);
        }
    }

}
