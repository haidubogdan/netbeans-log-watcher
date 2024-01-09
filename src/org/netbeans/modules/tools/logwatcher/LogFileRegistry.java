package org.netbeans.modules.tools.logwatcher;

import java.io.File;
import javax.swing.event.ChangeListener;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.ChangeListener;
import org.openide.util.ChangeSupport;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author bhaidu
 */
public class LogFileRegistry {

    private static final Logger LOGGER = Logger.getLogger(LogFileRegistry.class.getName());
    private final ChangeSupport changeSupport = new ChangeSupport(this);
    private static LogFileRegistry registry;

    private final File path;
    //private final Lookup.Result<ServerInstanceProvider> result;

    private final Lookup lookup;

    private LogFileRegistry(File path) {
        this.path = path;
        lookup = Lookups.forPath("log_file_reader");
        //result = lookup.lookupResult(ServerInstanceProvider.class);
    }

    public static synchronized LogFileRegistry getInstance(File path) {
        if (registry == null) {
            registry = new LogFileRegistry(path);
            //registry.result.allItems();
            //registry.result.addLookupListener(l = new ProviderLookupListener(registry.changeSupport));
        }
        return registry;
    }

    public void addChangeListener(ChangeListener listener) {
        changeSupport.addChangeListener(listener);
    }

    public void removeChangeListener(ChangeListener listener) {
        changeSupport.removeChangeListener(listener);
    }

    private static class ProviderLookupListener implements LookupListener {

        private final ChangeSupport changeSupport;

        public ProviderLookupListener(ChangeSupport changeSupport) {
            this.changeSupport = changeSupport;
        }

        public void resultChanged(LookupEvent ev) {
            LOGGER.log(Level.FINE, "Provider lookup change {0}", ev);
            changeSupport.fireChange();
        }

    }
}
