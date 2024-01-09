/*
Licensed to the Apache Software Foundation (ASF)
 */
package org.netbeans.modules.tools.logwatcher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.filesystems.FileObject;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;
import org.openide.util.NbCollections;
import org.openide.util.RequestProcessor;
import org.openide.util.WeakListeners;
/**
 *
 * @author bogdan
 */
public class LogWatcherChildFactory extends ChildFactory<FileObject> implements ChangeListener {

    @Override
    protected boolean createKeys(List<FileObject> list) {
        list.addAll(NbCollections.checkedListByCopy(new ArrayList<>(), FileObject.class, true));
        return true;
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        
    }
    
}
