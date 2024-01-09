package org.netbeans.modules.tools.logwatcher;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import javax.swing.JTextArea;
import org.apache.commons.io.input.ReversedLinesFileReader;
import org.openide.filesystems.FileChangeAdapter;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileRenameEvent;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author bhaidu
 */
public class FileChangeSupport extends FileChangeAdapter {

    private LogOutputComponent tc;
    private JTextArea textArea;

    public FileChangeSupport(LogOutputComponent tc) {
        this.tc = tc;
        this.textArea = tc.getTextComponent();
    }

    @Override
    public void fileDataCreated(FileEvent fe) {
        String changedLine = "";
        File file = FileUtil.toFile(fe.getFile());
        if (file.isDirectory()){
            return;
        }
        try (ReversedLinesFileReader reader = new ReversedLinesFileReader(file, StandardCharsets.UTF_8)) {
            changedLine = "\n" + reader.readLine();
        } catch (IOException e) {
            // ignore for now
        }
        if (textArea != null) {
            textArea.append(fe.getFile().getNameExt() + " a+ " + changedLine + "\n");
        }
    }

    @Override
    public void fileChanged(final FileEvent fe) {
        String changedLine = "";
        File file = FileUtil.toFile(fe.getFile());
        if (file.isDirectory()){
            return;
        }
        try (ReversedLinesFileReader reader = new ReversedLinesFileReader(file, StandardCharsets.UTF_8)) {
            changedLine = "\n" + reader.readLine();
        } catch (IOException e) {
            // ignore for now
        }
        if (textArea != null) {
            textArea.append(fe.getFile().getNameExt() + " m " + changedLine + "\n");
        }
    }

    @Override
    public void fileRenamed(final FileRenameEvent fe) {
        System.out.println("File renmaed");
    }

}
