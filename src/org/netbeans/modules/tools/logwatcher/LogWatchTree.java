package org.netbeans.modules.tools.logwatcher;

import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;

/**
 *
 * @author bhaidu
 */
public class LogWatchTree {

    public static Node getRootNode() throws DataObjectNotFoundException {
        FileObject rootFolder = FileUtil.getConfigFile("LogFiles");
        return DataObject.find(rootFolder).getNodeDelegate();
    }

    public static DataObject getRootDob() throws DataObjectNotFoundException {
        return getRootNode().getLookup().lookup(DataFolder.class);
    }
    
    public static FileObject getRootFileObject() throws DataObjectNotFoundException {
        return getRootDob().getPrimaryFile();
    }

    public static FileObject getFolderFileObject(String folderName){
        try {
            return getRootFileObject().getFileObject(folderName);
        } catch (DataObjectNotFoundException ex) {
            //Exceptions.printStackTrace(ex);
        }
        return null;
    }
}
