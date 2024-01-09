package org.netbeans.modules.tools.logwatcher;

import java.awt.BorderLayout;
import java.awt.Component;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.windows.TopComponent;

/**
 *
 * @author bhaidu
 */
@TopComponent.Description(
        preferredID = "LogOutputComponent",
        persistenceType = TopComponent.PERSISTENCE_ALWAYS)
@TopComponent.Registration(
        mode = "output",
        position=2100,
        openAtStartup = false)
@ActionID(
        category = "Window", // NOI18N
id = "org.netbeans.modules.tools.logreader.LogOutputComponent") // NOI18N
@ActionReference(
        path = "Menu/Window/IDE Tools", // NOI18N
position = 200)
public class LogOutputComponent extends TopComponent {
    
    JTextArea ta;
    
    public LogOutputComponent(){
        setLayout(new BorderLayout());
        this.ta = new JTextArea();
        //add(ta, BorderLayout.CENTER);
        JScrollPane scroll = new JScrollPane ( ta );
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        add(scroll, BorderLayout.CENTER);
        int x = 1;
    }
    
    public JTextArea getTextComponent(){
        return ta;
    }
}
