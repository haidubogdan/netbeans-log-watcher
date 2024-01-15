/*
Licensed to the Apache Software Foundation (ASF)
 */
package org.netbeans.modules.tools.logwatcher;

import java.io.File;
import static org.netbeans.modules.tools.logwatcher.LogWatcherNode.LOG_PATH_ATTR;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author bogdan
 */
public class LogNodeSupport {

    public static String getLogPath(FileObject fo) {
        return (String) fo.getAttribute(LOG_PATH_ATTR);
    }

    public static FileObject fromLogPathAttr(FileObject fo) {
        String path = getLogPath(fo);
        if (path != null) {
            return FileUtil.toFileObject(new File(path));
        }
        return null;
    }

    public static File getFileFromLogPathAttr(FileObject fo) {
        String path = getLogPath(fo);
        if (path != null) {
            return new File(path);
        }
        return null;
    }
}
