package org.netbeans.modules.tools.logwatcher;

import java.util.prefs.Preferences;
import org.openide.filesystems.FileObject;
import org.openide.util.NbPreferences;

/**
 *
 * @author bhaidu
 */
public class LogWatcherPropertiesSupport {

    private static final String PREFS_NODE = "fileMapping";  //NOI18N
    private static LogWatcherPropertiesSupport instance = null;
    private static final Preferences prefs = NbPreferences.forModule(LogWatcherPropertiesSupport.class).node(PREFS_NODE);

    private LogWatcherPropertiesSupport() {
    }

    public static synchronized LogWatcherPropertiesSupport getInstance() {
        if (instance == null) {
            instance = new LogWatcherPropertiesSupport();
        }
        return instance;
    }

    private Preferences getPrefs() {
        return prefs;
    }
    
    public void setFilePath(String name, FileObject file){
        prefs.put(name, file.getPath());
    }
    
    public String getFilePath(String name){
        return prefs.get(name, "");
    }
}
