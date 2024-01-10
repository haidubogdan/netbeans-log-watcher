/*
Licensed to the Apache Software Foundation (ASF)
 */
package org.netbeans.modules.tools.logwatcher;

import java.beans.IntrospectionException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import org.netbeans.modules.tools.logwatcher.nodes.FileEntryNode;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;

/**
 *
 * @author bogdan
 */
public class LogNodeSupport {

    public static LogFolder getLogFolder(FileObject dirFo) {
        FileObject[] list = dirFo.getChildren();

        if (list.length > 0) {
            FileObject dataDir = list[0];
            LogFolder lf = unserializeLogInfo(dataDir);
            if (lf != null) {
                return lf;
            }
        }

        return null;
    }

    public static LogFolder unserializeLogInfo(FileObject dirData) {
        try {
            try (ObjectInputStream in = new ObjectInputStream(dirData.getInputStream())) {
                try {
                    Object decoded = in.readObject();
                    if (decoded instanceof LogFolder) {
                        return (LogFolder) decoded;
                    }
                } catch (ClassNotFoundException ex) {
                    Exceptions.printStackTrace(ex);
                }
                in.close();
            }
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return null;
    }

    public static List<Node> getFileNodesForFolderData(FileObject dirData) {
        List<Node> files = new ArrayList<>();
        LogFolder lf = unserializeLogInfo(dirData);
        if (lf != null && lf.dir != null) {
            if (!lf.dir.isDirectory()) {
                return files;
            }
            FileObject dirfo = FileUtil.toFileObject(lf.dir);
            if (dirfo.isFolder()) {
                for (FileObject fo : dirfo.getChildren()) {
                    try {
                        files.add(new FileEntryNode(fo));
                    } catch (IntrospectionException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
            }
        }
        return files;
    }
}
