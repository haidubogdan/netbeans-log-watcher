package org.netbeans.modules.tools.logwatcher;

import java.awt.BorderLayout;
import java.beans.IntrospectionException;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import javax.swing.Action;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import org.openide.actions.OpenAction;
import org.openide.cookies.OpenCookie;
import org.openide.filesystems.FileObject;
import org.openide.nodes.BeanNode;
import org.openide.nodes.FilterNode;
import org.openide.util.Exceptions;
import org.openide.util.Utilities;
import org.openide.util.actions.SystemAction;
import org.openide.util.lookup.Lookups;
import org.openide.windows.TopComponent;

/**
 *
 * @author bhaidu
 */
public class FileEntryNode extends FilterNode {

    private final FileObject file;

    public FileEntryNode(FileObject entry) throws IntrospectionException {
        super(new BeanNode<FileObject>(entry),
                Children.LEAF,
                Lookups.fixed(new EntryOpenCookie(entry)));
        this.file = entry;
    }

    /**
     * Using HtmlDisplayName ensures any HTML in RSS entry titles are properly
     * handled, escaped, entities resolved, etc.
     */
    @Override
    public String getHtmlDisplayName() {
        return file.getName();
    }

    /**
     * Making a tooltip out of the entry's description
     */
    @Override
    public String getShortDescription() {
        StringBuilder sb = new StringBuilder();
//        sb.append("Author: ").append(file.getAuthor()).append("; ");
//        if (file.getPublishedDate() != null) {
//            sb.append("Published: ").append(file.getPublishedDate().toString());
//        }
        return sb.toString();
    }

    /**
     * Providing the Open action on a feed entry
     */
    @Override
    public Action[] getActions(boolean popup) {
        List<? extends Action> rootActions = Utilities.actionsForPath("Actions/LogWatcher");
        return rootActions.toArray(new Action[rootActions.size()]);
    }

    @Override
    public Action getPreferredAction() {
        return getActions(false)[0];
    }

    /**
     * Specifying what should happen when the user invokes the Open action
     */
    private static class EntryOpenCookie implements OpenCookie {

        private final FileObject entry;

        EntryOpenCookie(FileObject entry) {
            this.entry = entry;
        }

        @Override
        public void open() {
            BrowserTopComponent btc = new BrowserTopComponent(entry);
            btc.open();
            btc.requestActive();
        }

    }

    public static final class BrowserTopComponent extends TopComponent {

        public BrowserTopComponent(FileObject entry) {
            setName(entry.getName());
            setLayout(new BorderLayout());
            JEditorPane editorPane = new JEditorPane();
            editorPane.setEditable(false);
            InputStream description;
            try {
                description = entry.getInputStream();
                if (description != null) {
                    //editorPane.setContentType("text/html");
                    //editorPane.setText(description.getValue());
                }
            } catch (FileNotFoundException ex) {
                Exceptions.printStackTrace(ex);
            }

            add(new JScrollPane(editorPane), BorderLayout.CENTER);
            putClientProperty(/*PrintManager.PRINT_PRINTABLE*/"print.printable", true);
        }
    }

}
