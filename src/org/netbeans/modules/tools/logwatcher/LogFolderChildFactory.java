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

import org.netbeans.modules.tools.logwatcher.nodes.RootNode;
import java.util.List;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;

/**
 *
 * @author bhaidu
 */
public class LogFolderChildFactory extends ChildFactory<RootNode> {

    private final RootNode rootNode;

    public LogFolderChildFactory(RootNode rootNode) {
        this.rootNode = rootNode;
    }

    @Override
    protected boolean createKeys(List<RootNode> list) {
        list.add(rootNode);
        return true;
    }

    @Override
    protected Node createNodeForKey(RootNode node) {
        return node;
    }

}