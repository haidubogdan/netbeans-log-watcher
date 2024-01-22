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
package org.netbeans.modules.tools.logwatcher.output;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import org.openide.util.Exceptions;
import org.openide.windows.InputOutput;
import static java.nio.file.StandardWatchEventKinds.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.windows.IOColorPrint;
import org.openide.windows.IOColors;

/**
 * 
 *
 * @author bhaidu
 */
public class LogIO {

    private static final HashMap<String, Integer> lastReadLine = new HashMap<>();
    private static LogIO instance = null;

    public static LogIO getInstance() {

        if (instance == null) {
            instance = new LogIO();
        }
        return instance;
    }

    public void notify(Path dirPath, String message) {
        LogIOTopComponent ltp = LogIOTopComponent.getInstance(dirPath.toFile());
        if (!ltp.isOpened()) {
            ltp.open();
            ltp.requestActive();
        }

        InputOutput io = ltp.getIo();
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        try {
            IOColorPrint.print(io, dateFormat.format(new Date()) + " | " + message + "\n", IOColors.getColor(io, IOColors.OutputType.LOG_DEBUG));
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public void notify(Path filePath, Path dir, String event) {
        File file = filePath.toFile();

        LogIOTopComponent ltp = LogIOTopComponent.getInstance(dir.toFile());
        if (!ltp.isOpened()) {
            ltp.open();
            ltp.requestActive();
        }

        InputOutput io = ltp.getIo();

        String fileName = file.getName();

        if (event.equals(ENTRY_DELETE.name())) {
            if (lastReadLine.containsKey(fileName)) {
                lastReadLine.remove(fileName);
            }
            return;
        }

        int lastFileReadLine = 0;
        if (lastReadLine.containsKey(fileName)) {
            lastFileReadLine = lastReadLine.get(file.getName());
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            int lines = 0;
            String changedLine;
            boolean logReadAdded = false;

            int lastParsedLine = lastFileReadLine;
            ArrayList<String> sentence = new ArrayList<>();
            while ((changedLine = reader.readLine()) != null) {
                lines++;
                if (lines > lastParsedLine) {
                    logReadAdded = true;
                    sentence.add(lines + " : " + changedLine);
                    lastParsedLine = lines;
                }
            }

            if (logReadAdded && (lastParsedLine > lastFileReadLine)) {
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                io.getOut().println(dateFormat.format(new Date()) + " | " + file + " last line : " + lastFileReadLine + " | " + event);
                for (String line : sentence){
                    if (line.toLowerCase().contains(" exception ")){
                        io.getErr().println(line);
                    } else {
                        io.getOut().println(line);
                    }
                }
                
                lastReadLine.put(fileName, lastParsedLine);
            }
            //io.getOut().println(file + " last log line : " + lines);
            reader.close();
        } catch (IOException e) {
            // ignore for now
            io.getOut().println(e.getMessage());
        }
    }

    public static void clearLogOutput(FileObject dir) {
        LogIOTopComponent ltp = LogIOTopComponent.getInstance(FileUtil.toFile(dir));
        InputOutput io = ltp.getIo();
        try {
            io.getOut().reset();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        io.getOut().flush();
    }

}
