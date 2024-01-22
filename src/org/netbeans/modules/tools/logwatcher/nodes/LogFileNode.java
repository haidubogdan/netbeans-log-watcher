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
import javax.swing.Action;
import org.netbeans.modules.tools.logwatcher.ConfigSupport;
import org.netbeans.modules.tools.logwatcher.actions.OpenInSystemAction;
import org.openide.filesystems.FileObject;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.ImageUtilities;

/**
 *
 * @author bhaidu
 */
public class LogFileNode extends FilterNode {

    public LogFileNode(Node logFileNode) {
        super(logFileNode, Children.LEAF);
    }

    @Override
    public String getDisplayName() {
        return getLookup().lookup(FileObject.class).getNameExt();
    }

    @Override
    public String getHtmlDisplayName() {
        FileObject nodeFo = getLookup().lookup(FileObject.class);
        String name = nodeFo.getNameExt();

        if (isFilteredOutFromWatch(nodeFo)) {
            return "<font color='AAAAAA'>" + name + "<i>(skipped from watch)</i></font>";
        }

        return name;
    }

    @Override
    public Image getIcon(int type) {
        return ImageUtilities.loadImage("org/netbeans/modules/tools/logwatcher/resources/file.png");
    }

    @Override
    public Action[] getActions(boolean context) {
        FileObject file = getLookup().lookup(FileObject.class);
        return new Action[]{new OpenInSystemAction(file)};
    }

    private boolean isFilteredOutFromWatch(FileObject nodeFo) {
        FileObject parent = nodeFo.getParent();
        return ConfigSupport.logFolderHasFilters(parent)
                && !ConfigSupport.fileIsMarkedForWatching(nodeFo);
    }
}
