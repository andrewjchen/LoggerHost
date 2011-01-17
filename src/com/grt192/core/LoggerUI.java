package com.grt192.core;

import com.grt192.logging.MessageListener;
import com.grt192.logging.LoggerModel;
import com.grt192.networking.GRTClientSocket;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.logging.*;
import javax.swing.*;
import javax.swing.text.DefaultCaret;
//import sun.awt.WindowClosingListener;

/**
 * Graphical interface for LoggerModel
 * @author ajc, data
 */
public class LoggerUI extends JPanel implements MessageListener {

    public static final String IP = "10.1.92.2";
    public static final int PORT = 192;

    public static void main(String[] args) {

        //new client
        final LoggerModel lms = new LoggerModel();

        //new connection to client
        GRTClientSocket gcs = new GRTClientSocket(IP, PORT);
        gcs.start();
        gcs.addSocketListener(lms);

        //new panel
        LoggerUI lu = new LoggerUI(lms);

        JFrame vars = new JFrame("vars");
        vars.setLayout(new BorderLayout());
        VariableUI v = new VariableUI(lms);
        vars.add(v,BorderLayout.CENTER);
        vars.setSize(500, 500);
        vars.setVisible(true);


        //new frame
        final JFrame j = new JFrame("Logger UI");
        //menu bar and co.
        JMenuBar bar = new JMenuBar();
        JMenu file = new JMenu("File");
        JMenu log = new JMenu("Log");



        //add item to log
        JMenuItem write = new JMenuItem("Write to file");
        log.add(write);
        JMenuItem getVers = new JMenuItem("Print version");
        log.add(getVers);

        //ad menu bar to frame
        j.setJMenuBar(bar);
        bar.add(file);
        bar.add(log);
        
        write.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                lms.writeLogs();
            }
        });

        getVers.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                int i = lms.getLastVersion();
                JOptionPane.showMessageDialog(j, i);
            }
        });

        j.setSize(500, 500);
        j.add(lu, BorderLayout.CENTER);
        j.setVisible(true);
        j.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        j.addWindowListener(new WindowAdapter() {

            public void windowClosing(WindowEvent e) {
                lms.writeLogs();
                System.exit(0);
            }
        });

    }
    private LoggerModel lm;
    private JTabbedPane tabs;
    private Hashtable<String, ArrayList<JScrollPane>> scrollers;
    private Hashtable<String, ArrayList<JEditorPane>> viewers;
    private JScrollPane allScroll;
    private JEditorPane allEditor;

    public LoggerUI(LoggerModel lm) {
        this.lm = lm;
        tabs = new JTabbedPane();
        allEditor = new JEditorPane();
        allEditor.setEditable(false);
        allScroll = new JScrollPane(allEditor);
        setLayout(new BorderLayout());
        add(allScroll, BorderLayout.CENTER);
        lm.registerMessageListener(this);
        DefaultCaret caret = (DefaultCaret) allEditor.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
    }

    public void messageReceived(Message m) {
        System.out.print(m + "\n");

        allEditor.setText(allEditor.getText() + m+ "\n");
        ArrayList<JScrollPane> scrl = scrollers.get(m.getType());
        ArrayList<JEditorPane> view = viewers.get(m.getType());
        for (int i = 0; i < scrl.size(); i++) {
            JEditorPane j = view.get(i);
            JScrollPane js = scrl.get(i);
            j.setText(j.getText() + m);
            JViewport v = js.getViewport();
            v.setLocation(0, j.getHeight() - v.getHeight());
        }
    }

    /**
     * @deprecated use message.toString()
     */
    private String messageToString(Message m) {
        Calendar c = new GregorianCalendar(TimeZone.getDefault());
        c.setTime(m.getReceived());
        return "(" + c.get(Calendar.HOUR) + ":" + c.get(Calendar.MINUTE) + ":" + c.get(Calendar.SECOND) + ")" + m.getType() + ":" + m.getMessage() + "\n\n";
    }

    public void addTab(String[] types) {
        String text = lm.getMessages(types);
        JEditorPane je = new JEditorPane("text/html", text);
        JScrollPane jsp = new JScrollPane(je);
        je.setEditable(false);
        for (int i = 0; i < types.length; i++) {
            if (scrollers.get(types[i]) == null) {
                scrollers.put(types[i], new ArrayList<JScrollPane>());
                viewers.put(types[i], new ArrayList<JEditorPane>());
            }
            scrollers.get(types[i]).add(jsp);
            viewers.get(types[i]).add(je);
        }
        String names = "";
        for (int i = 0; i < types.length - 1; i++) {
            names += types + ",";
        }
        names += types[types.length - 1];
        tabs.addTab(names, jsp);
    }

}
