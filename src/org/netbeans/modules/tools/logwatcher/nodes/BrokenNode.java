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
package org.netbeans.modules.tools.logwatcher.nodes;

import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Action;
import org.openide.filesystems.FileObject;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.ImageUtilities;
import org.openide.util.Utilities;
import org.netbeans.modules.tools.logwatcher.ConfigSupport;

/**
 *
 * @author bogdan
 */
public class BrokenNode extends FilterNode {

    public BrokenNode(Node logFileNode) {
        super(logFileNode, Children.LEAF);
    }

    @Override
    public String getDisplayName() {
        return getLookup().lookup(FileObject.class).getNameExt();
    }
    
    @Override
    public String getShortDescription(){
        FileObject file = getLookup().lookup(FileObject.class);
        String logPath = ConfigSupport.getLogFileReferencePath(file);
        if (logPath == null){
            logPath = file.getName();
        }
        return "Real path for " + logPath + " not found";
    }

    @Override
    public Image getIcon(int type) {
        return ImageUtilities.loadImage("org/netbeans/modules/tools/logwatcher/resources/broken.png");
    }

    @Override
    public Action[] getActions(boolean context) {
        List<Action> actions = new ArrayList<>();
        actions.addAll(Utilities.actionsForPath("Actions/FolderActions"));
        return actions.toArray(new Action[0]);
    }
}
