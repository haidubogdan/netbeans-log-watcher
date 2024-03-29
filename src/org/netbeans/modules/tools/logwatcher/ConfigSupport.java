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
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;

/**
 *
 * @author bogdan
 */
public class ConfigSupport {

    public static final String LOG_PATH_ATTR = "log_path"; // NOI18N
    public static final String LOG_DIR_HAS_FILTERS_ATTR = "has_filters"; // NOI18N
    public static final String LOG_FILE_WATCH_ATTR = "log_watch"; // NOI18N
    public static final String LOG_FILE_BROKEN_ATTR = "log_broken"; // NOI18N

    public static String getLogFileReferencePath(FileObject dataFo) {
        return (String) dataFo.getAttribute(LOG_PATH_ATTR);
    }

    public static FileObject getLogReference(FileObject dataFo) {
        String path = getLogFileReferencePath(dataFo);
        if (path != null) {
            return FileUtil.toFileObject(new File(path));
        }
        return null;
    }

    public static File getLogFileReference(FileObject dataFo) {
        String path = getLogFileReferencePath(dataFo);
        if (path != null) {
            return new File(path);
        }
        return null;
    }

    public static boolean logFolderHasFilters(FileObject dataFo) {
        Object filterAttr = dataFo.getAttribute(LOG_DIR_HAS_FILTERS_ATTR);

        if (filterAttr == null) {
            return false;
        }
        return (Integer) filterAttr == 1;
    }

    public static boolean fileIsMarkedForWatching(FileObject dataFo) {
        Integer checkedStatus = (Integer) dataFo.getAttribute(LOG_FILE_WATCH_ATTR);
        return checkedStatus != null && checkedStatus == 1;
    }

    public static void setBroken(FileObject dataFo, int f) {
        try {
            dataFo.setAttribute(LOG_FILE_BROKEN_ATTR, f);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
    
    public static boolean referenceIsBroken(FileObject dataFo){
        Integer value = (Integer) dataFo.getAttribute(LOG_FILE_BROKEN_ATTR);
        return value != null && value == 1;
    }

    public static void setLogReferencePath(FileObject dataFo, String path) {
        try {
            dataFo.setAttribute(LOG_PATH_ATTR, path);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
    
    public static void markForWatching(FileObject dataFo, int f){
        try {
            dataFo.setAttribute(LOG_FILE_WATCH_ATTR, f);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
    
    public static void setFilteredStatus(FileObject dataFo, int f){
        try {
            dataFo.setAttribute(LOG_DIR_HAS_FILTERS_ATTR, 1);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
}
