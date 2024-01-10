package org.netbeans.modules.tools.logwatcher.nodes;

import java.awt.BorderLayout;
import java.awt.Image;
import java.beans.IntrospectionException;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Date;
import javax.swing.Action;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import org.openide.actions.OpenAction;
import org.openide.cookies.OpenCookie;
import org.openide.filesystems.FileObject;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.actions.SystemAction;
import org.openide.util.lookup.Lookups;
import org.openide.windows.TopComponent;

/**
 * on the list for removal
 *
 * @author bhaidu
 */
public class FileEntryNode extends AbstractNode {

    private final FileObject file;

    public FileEntryNode(FileObject entry) throws IntrospectionException {
        super(Children.LEAF,
                Lookups.fixed(new EntryOpenCookie(entry)));
        this.file = entry;
    }

    /** 
     * Using HtmlDisplayName ensures any HTML in RSS entry titles are properly
     * handled, escaped, entities resolved, etc.
     */
    @Override
    public String getHtmlDisplayName() {
        String name = file.getNameExt();
        Date lastModified = file.lastModified();
        
        if (lastModified != null){
            name += " <font color='AAAAAA'><i>" + lastModified.toString() + "</i></font>";
        }
        return name;
    }

    /**
     * Providing the Open action on a feed entry
     */
    @Override
    public Action[] getActions(boolean popup) {
        //return new Action[]{SystemAction.get(OpenAction.class)};
        return new Action[0];
    }

    /*
    @Override
    public Action getPreferredAction() {
        return getActions(false)[0];
    }
*/
    
    @Override
    public Image getIcon(int type) {
        return ImageUtilities.loadImage("org/netbeans/modules/tools/logwatcher/resources/file.png");
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
