
package com.grt192.core;

import com.grt192.logger.MessageListener;
import com.grt192.logger.LoggerModel;
import com.grt192.networking.GRTClientSocket;
import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.TimeZone;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JViewport;

/**
 * 
 * @author ajc
 */
public class LoggerUI extends JPanel implements MessageListener {
    public static void main(String[] args){
        LoggerModel lms = new LoggerModel();
        GRTClientSocket gcs = new GRTClientSocket("10.1.92.2",192);
        gcs.start();
        gcs.addSocketListener(lms);
        LoggerUI lu = new LoggerUI(lms);
        JFrame j = new JFrame();
        j.setSize(500,500);
        j.add(lu,BorderLayout.CENTER);
        j.setVisible(true);
        j.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private LoggerModel lm;

    private JTabbedPane tabs;
    private Hashtable<String,ArrayList<JScrollPane>> scrollers;
    private Hashtable<String,ArrayList<JEditorPane>> viewers;

    private JScrollPane allScroll;
    private JEditorPane allEditor;
    

//    JFrame mainFrame = new JFrame("Logger UI");

    public LoggerUI(LoggerModel lm){
        this.lm = lm;
        tabs = new JTabbedPane();
        allEditor = new JEditorPane();
        allEditor.setEditable(false);
        allScroll = new JScrollPane(allEditor);
        setLayout(new BorderLayout());
        add(allScroll,BorderLayout.CENTER);
        lm.registerMessageListener(this);
    }

    public void messageReceived(Message m) {
        System.out.println(getMessageText(m));
        allEditor.setText(allEditor.getText()+getMessageText(m));
        ArrayList<JScrollPane> scrl = scrollers.get(m.getType());
        ArrayList<JEditorPane> view = viewers.get(m.getType());
        for(int i=0;i<scrl.size();i++) {
            JEditorPane j = view.get(i);
            JScrollPane js = scrl.get(i);
            j.setText(j.getText()+getMessageText(m));
            JViewport v = js.getViewport();
            v.setLocation(0,j.getHeight()-v.getHeight());
        }
        JViewport v = allScroll.getViewport();
        v.setLocation(0,allScroll.getHeight()-v.getHeight());
    }

    private String getMessageText(Message m) {
        Calendar c = new GregorianCalendar(TimeZone.getDefault());
        c.setTime(m.getReceived());
        return "("+c.get(Calendar.HOUR)+":"+c.get(Calendar.MINUTE)+":"+c.get(Calendar.SECOND)+")"+m.getType()+":"+m.getMessage()+"\n\n";
    }

    public void addTab(String[] types) {
        Message[] ms = lm.getMessages(types);
        String text = "";
        for(int i=0;i<ms.length;i++) {
            text +=getMessageText(ms[i]);
        }
        JEditorPane je = new JEditorPane("text/html",text);
        JScrollPane jsp = new JScrollPane(je);
        je.setEditable(false);
        for(int i=0;i<types.length;i++) {
            if(scrollers.get(types[i])==null) {
                scrollers.put(types[i], new ArrayList<JScrollPane>());
                viewers.put(types[i],new ArrayList<JEditorPane>());
            }
            scrollers.get(types[i]).add(jsp);
            viewers.get(types[i]).add(je);
        }
        String names = "";
        for(int i=0;i<types.length-1;i++) {
            names += types+",";
        }
        names += types[types.length-1];
        tabs.addTab(names,jsp);
    }

}
