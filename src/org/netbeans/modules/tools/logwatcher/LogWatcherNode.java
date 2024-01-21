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

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import javax.swing.Action;
import org.netbeans.api.core.ide.ServicesTabNodeRegistration;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.netbeans.modules.tools.logwatcher.nodes.RootNode;

/**
 *
 *
 * @author bogdan
 */
public class LogWatcherNode extends AbstractNode {

    private static final String LOG_WATCHER_ICON = "org/netbeans/modules/tools/logwatcher/resources/icon.png"; // NOI18N

    private static LogWatcherNode node;
    private static RootNode rootNode;

    private LogWatcherNode(LogFolderChildFactory factory, Lookup lookup) {
        super(Children.create(factory, true), lookup);

        setName("logwatcher"); // NOI18N
        setDisplayName("Log watcher"); // NOI18N
        setShortDescription("Log watcher"); // NOI18N
        setIconBaseWithExtension(LOG_WATCHER_ICON);
    }

    @ServicesTabNodeRegistration(
            name = "logwatcher",
            displayName = "org.netbeans.modules.tools.logwatcher.Bundle#RootNode_DISPLAYNAME",
            shortDescription = "Log watcher",
            iconResource = "org/netbeans/modules/tools/logwatcher/resources/icon.png",
            position = 1000
    )
    public static synchronized LogWatcherNode getInstance() {
        if (node == null) {
            try {
                Node rootNodeFolder = LogWatchTree.getRootNode();
                initNodes(rootNodeFolder);
                rootNode = new RootNode(rootNodeFolder);
                List<RootNode> nodes = new ArrayList<>();
                nodes.add(rootNode);
                LogFolderChildFactory factory = new LogFolderChildFactory(rootNode);
                factory.createKeys(nodes);
                NodeLogLookup lookup = new NodeLogLookup();
                node = new LogWatcherNode(factory, lookup);
            } catch (DataObjectNotFoundException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        return node;
    }

    @Override
    public Action[] getActions(boolean context) {
        return new Action[0];
    }

    public static RootNode getRootNode() {
        if (node != null) {
            return node.getLookup().lookup(RootNode.class);
        }
        return rootNode;
    }

    private static void initNodes(Node rootNodeFolder) {
        DataFolder fd = rootNodeFolder.getLookup().lookup(DataFolder.class);

        for (DataObject dob : fd.getChildren()) {
            FileObject fo = dob.getLookup().lookup(FileObject.class);
            if (fo == null) {
                continue;
            }
            String path = ConfigSupport.getLogFileReferencePath(fo);
            if (path != null) {
                FileObject referenceFolder = FileUtil.toFileObject(new File(path));
                if (referenceFolder == null) {
                    //broken path
                    ConfigSupport.setBroken(fo, 0);
                    continue;
                }

                for (FileObject referenceFile : referenceFolder.getChildren()) {
                    if (referenceFile.isFolder() || 
                            fo.getFileObject(referenceFile.getNameExt()) != null) {
                        //already referenced
                        continue;
                    }
                    FileObject logNode;
                    try {
                        logNode = fo.createData(referenceFile.getName(), referenceFile.getExt());
                        ConfigSupport.setLogReferencePath(logNode, referenceFile.getPath());
                    } catch (IOException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }

            }

        }
    }
}
