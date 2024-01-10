package org.netbeans.modules.tools.logwatcher.output;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import org.openide.util.Exceptions;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;
import static java.nio.file.StandardWatchEventKinds.*;
import java.util.HashMap;
import org.openide.windows.IOContainer;

/**
 * @TODO fix multiple watcher issue
 *
 * @author bhaidu
 */
public class LogIO {

    private static final HashMap<String, Integer> lastReadLine = new HashMap<>();
    private static LogIO instance = null;

    public static LogIO getInstance() {
        if (instance == null){
            instance = new LogIO();
        }
        return instance;
    }
    
    public void notify(Path filePath, Path dir, String event) {
        File file = filePath.toFile();
        InputOutput io = IOProvider.getDefault().getIO("Log watch " + dir.getFileName().toString(), false);
        
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
            String lineChange = "";
            while ((changedLine = reader.readLine()) != null) {
                lines++;
                if (lines > lastParsedLine) {
                    logReadAdded = true;
                    lineChange += lines + " : " + changedLine + "\n";
                    lastParsedLine = lines;
                }
            }

            if (logReadAdded && (lastParsedLine > lastFileReadLine)) {
                io.getOut().println(file + " last line : " + lastFileReadLine + " | " + event);
                io.getOut().println(lineChange);
                lastReadLine.put(fileName, lastParsedLine);
            }
            //io.getOut().println(file + " last log line : " + lines);
            reader.close();
        } catch (IOException e) {
            // ignore for now
            io.getOut().println(e.getMessage());
        }
    }

    public static void clearLogOutput(String name) {
        InputOutput io = IOProvider.getDefault().getIO("Log watch " + name, false);
        try {
            io.getOut().reset();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        io.getOut().flush();
    }

    public static void resetLastReadLines() {

    }
}
