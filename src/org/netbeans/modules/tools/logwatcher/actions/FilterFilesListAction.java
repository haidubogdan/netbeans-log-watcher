package org.netbeans.modules.tools.logwatcher.actions;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractAction;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import org.netbeans.modules.tools.logwatcher.LogNodeSupport;
import org.netbeans.modules.tools.logwatcher.ui.JCheckBoxTree;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataFolder;

/**
 *
 * @author bhaidu
 */
public class FilterFilesListAction extends AbstractAction implements ActionListener {

    private final DataFolder folder;

    public FilterFilesListAction(DataFolder df) {
        putValue(NAME, "Filter Files");
        folder = df;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        FileObject root = folder.getPrimaryFile();
        FileObject logFo = LogNodeSupport.fromLogPathAttr(root);

        if (logFo != null && logFo.isFolder()) {
            StandardDialog dialog = new StandardDialog("Filtered  files (not working)", root, logFo);
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
            for (FileObject fo : root.getChildren()){
                DefaultMutableTreeNode fileName = new DefaultMutableTreeNode(fo.getNameExt());
                fileName.setUserObject(fo);
                rootNode.add(fileName);
            }
            DefaultTreeModel model = new DefaultTreeModel(rootNode);
            cbt.setModel(model);
            cbt.addCheckChangeEventListener(new JCheckBoxTree.CheckChangeEventListener() {
                public void checkStateChanged(JCheckBoxTree.CheckChangeEvent event) {
                    System.out.println("event");
                    TreePath[] paths = cbt.getCheckedPaths();
                    for (TreePath tp : paths) {
                        for (Object pathPart : tp.getPath()) {
                            System.out.print(pathPart + ",");
                        }
                        System.out.println();
                    }
                }
            });
            JScrollPane scroll = new JScrollPane(cbt);
            //scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
            add(scroll, BorderLayout.CENTER);
            this.setSize(300, 400);
            final Toolkit toolkit = Toolkit.getDefaultToolkit();
            final Dimension screenSize = toolkit.getScreenSize();
            final int x = this.getWidth() / 2;
            final int y = (screenSize.height - this.getHeight()) / 2;
            this.setLocation(x, y);
        }
    }
}
