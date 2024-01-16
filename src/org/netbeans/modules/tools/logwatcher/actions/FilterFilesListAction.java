package org.netbeans.modules.tools.logwatcher.actions;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Map.Entry;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import org.netbeans.modules.tools.logwatcher.LogNodeSupport;
import static org.netbeans.modules.tools.logwatcher.LogWatcherNode.LOG_DIR_HAS_FILTERS_ATTR;
import static org.netbeans.modules.tools.logwatcher.LogWatcherNode.LOG_FILE_WATCH_ATTR;
import org.netbeans.modules.tools.logwatcher.ui.JCheckBoxTree;
import org.netbeans.modules.tools.logwatcher.ui.JCheckBoxTree.CheckedNode;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataFolder;
import org.openide.util.Exceptions;

/**
 *
 * @author bhaidu
 */
public class FilterFilesListAction extends AbstractAction implements ActionListener {

    private final DataFolder folder;
    StandardDialog dialog;

    public FilterFilesListAction(DataFolder df) {
        putValue(NAME, "Filter Files");
        folder = df;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        FileObject root = folder.getPrimaryFile();
        FileObject logFo = LogNodeSupport.fromLogPathAttr(root);

        if (logFo != null && logFo.isFolder()) {
            dialog = new StandardDialog("Filtered  files", root, logFo);
            dialog.setVisible(true);
        }

    }

    private static final class StandardDialog extends JDialog {

        public StandardDialog(
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
            //scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
            add(scroll, BorderLayout.CENTER);
            JPanel panel = new JPanel();
            JButton b = new JButton("Update Filter");

            JDialog self = this;

            b.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    for (Entry<TreePath, CheckedNode> item : cbt.getPaths().entrySet()) {
                        for (Object kp : item.getKey().getPath()) {
                            if (kp instanceof DefaultMutableTreeNode) {
                                Object uo = (((DefaultMutableTreeNode) kp).getUserObject());
                                if (uo instanceof FileObject) {
                                    try {
                                        int sel = item.getValue().isSelected ? 1 : 0;
                                        FileObject logFile = (FileObject) uo;
                                        logFile.setAttribute(LOG_FILE_WATCH_ATTR, sel);
                                        FileObject dirParent = logFile.getParent();
                                        if (dirParent.getAttribute(LOG_DIR_HAS_FILTERS_ATTR) == null) {
                                            dirParent.setAttribute(LOG_DIR_HAS_FILTERS_ATTR, 1);
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

        public void uodateComponent() {

        }
    }

}
