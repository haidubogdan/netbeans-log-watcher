package org.netbeans.modules.tools.logwatcher;

import java.io.File;
import java.io.Serializable;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author bhaidu
 */
public class LogFile implements Serializable {

    private static final long serialVersionUID = 232325L;

    private final File file;

    public LogFile(File file) {
        this.file = file;
    }

    public File getLogFile() {
        return file;
    }
}
