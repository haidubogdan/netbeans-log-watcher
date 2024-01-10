package org.netbeans.modules.tools.logwatcher;

import java.io.File;
import java.io.Serializable;
import org.openide.filesystems.FileObject;

/**
 *
 * @author bhaidu
 */
public class LogFolder implements Serializable {

    private static final long serialVersionUID = 12L;

    public File dir;

    public LogFolder(File dir) {
        this.dir = dir;
    }

}
