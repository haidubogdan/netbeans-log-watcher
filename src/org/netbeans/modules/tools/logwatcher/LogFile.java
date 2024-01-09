package org.netbeans.modules.tools.logwatcher;

import org.openide.filesystems.FileObject;


/**
 *
 * @author bhaidu
 */
public class LogFile {
    
    private static final long serialVersionUID = 1L;
    
    private final FileObject file;
    
    public LogFile(FileObject file) {
        this.file = file;
    }
    
    public FileObject getLogFile(){
        return file;
    }
}
