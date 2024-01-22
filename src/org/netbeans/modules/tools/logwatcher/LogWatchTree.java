/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
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
