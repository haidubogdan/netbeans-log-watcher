/*
Licensed to the Apache Software Foundation (ASF)
 */
package org.netbeans.modules.tools.logwatcher.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import org.netbeans.modules.tools.logwatcher.ConfigSupport;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;

/**
 *
 * @author bogdan
 */
public class FilterDialog extends JDialog {

    public FilterDialog(
            String title, FileObject root, FileObject dirFo
    ) {
        super((Frame) null, title, true);

        getContentPane().setLayout(new BorderLayout());
        final JCheckBoxTree cbt = new JCheckBoxTree();
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Files");
        for (FileObject fo : root.getChildren()) {
            DefaultMutableTreeNode fileName = new DefaultMutableTreeNode(fo.getNameExt());
            fileName.setUserObject(fo);
            rootNode.add(fileName);
        }

        DefaultTreeModel model = new DefaultTreeModel(rootNode);
        cbt.setModel(model);

        JScrollPane scroll = new JScrollPane(cbt);
        add(scroll, BorderLayout.CENTER);
        JPanel panel = new JPanel();
        //filter button
        JButton b = new JButton("Update Filter");

        JDialog self = this;

        b.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                for (Map.Entry<TreePath, JCheckBoxTree.CheckedNode> item : cbt.getPaths().entrySet()) {
                    for (Object kp : item.getKey().getPath()) {
                        if (kp instanceof DefaultMutableTreeNode) {
                            Object uo = (((DefaultMutableTreeNode) kp).getUserObject());
                            if (uo instanceof FileObject) {
                                try {
                                    int sel = item.getValue().isSelected ? 1 : 0;
                                    FileObject logFile = (FileObject) uo;
                                    ConfigSupport.markForWatching(logFile, sel);
                                    FileObject dirParent = logFile.getParent();
                                    if (!ConfigSupport.logFolderHasFilters(dirParent)) {
                                        ConfigSupport.setFilteredStatus(dirParent, 1);
                                    }
                                    dirParent.setAttribute("c_modified", System.currentTimeMillis());
                                } catch (IOException ex) {
                                    Exceptions.printStackTrace(ex);
                                }
                            }
                        }
                    }
                }

                self.dispose();
            }
        });
        panel.add(b);
        add(panel, BorderLayout.SOUTH);
        this.setSize(300, 400);
        final Toolkit toolkit = Toolkit.getDefaultToolkit();
        final Dimension screenSize = toolkit.getScreenSize();
        final int x = this.getWidth() / 2;
        final int y = (screenSize.height - this.getHeight()) / 2;
        this.setLocation(x, y);
    }
}
