package org.netbeans.modules.tools.logwatcher;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.annotations.common.NullAllowed;
import org.openide.util.ChangeSupport;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;

/**
 *
 * @author bhaidu
 */
public class LogFilesImpl implements LookupListener, ChangeListener {

    private final ChangeSupport changeSupport = new ChangeSupport(this);

    public void addChangeListener(ChangeListener listener) {
        changeSupport.addChangeListener(listener);
    }

    public void removeChangeListener(ChangeListener listener) {
        changeSupport.removeChangeListener(listener);
    }

    @Override
    public void resultChanged(LookupEvent ev) {
//        removeImportantFilesListener();
//        resetAllInstances();
//        getAllInstances();
        fireChange();
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        fireChange();
    }

    private void fireChange() {
        changeSupport.fireChange();
    }
}
