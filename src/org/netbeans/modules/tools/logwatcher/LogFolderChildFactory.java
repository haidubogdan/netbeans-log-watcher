package org.netbeans.modules.tools.logwatcher;

import java.beans.IntrospectionException;
import java.util.ArrayList;
import java.util.List;
import org.openide.filesystems.FileObject;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.NbCollections;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;
import org.openide.util.NbCollections;

/**
 *
 * @author bhaidu
 */
public class LogFolderChildFactory extends ChildFactory<FileObject> {

    private final FileObject file;

    public LogFolderChildFactory(FileObject file) {
        this.file = file;
    }

    @Override
    protected boolean createKeys(List<FileObject> list) {
        list.add(file);
        list.addAll(NbCollections.checkedListByCopy(new ArrayList<>(), FileObject.class, true));
        return true;
    }

    @Override
    protected Node createNodeForKey(FileObject file) {
        FileEntryNode node = null;
        try {
            node = new FileEntryNode(file);
        } catch (IntrospectionException ex) {
            Exceptions.printStackTrace(ex);
        }
        return node;
    }

}