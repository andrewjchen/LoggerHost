/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.grt192.core;

import com.grt192.logging.LoggerModel;
import com.grt192.logging.MessageListener;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 *
 * @author data
 */
public class VariableUI extends JComponent implements MessageListener {

    private LoggerModel lm;
    private JPanel panel;
    private Container root;
    private int w;
    private int h;
    private final Hashtable<String,GraphDisplay> wrapped;

    private class GraphDisplay extends JComponent {
        private String name;
        private final GraphComponent graph;
        private long mytime;
        public GraphDisplay(String s) {
            mytime = System.currentTimeMillis();
            name = s;
            setLayout(new BorderLayout());
            graph = new GraphComponent();
            JButton b = new JButton("reset");
            add(new JLabel(name),BorderLayout.NORTH);
            add(graph,BorderLayout.CENTER);
            add(b,BorderLayout.SOUTH);
            b.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    graph.reset();
                }
            });
        }
        @Override
        public Dimension getPreferredSize() {
            return new Dimension(w,h);
        }
        public void addPoint(double x,double y) {
            graph.addPoint((x-mytime)/10d, y);
        }
    }

    public VariableUI(LoggerModel lm) {
        wrapped = new Hashtable<String,GraphDisplay>();
        this.lm = lm;
        lm.registerVariableListener(this);
        setLayout(new FlowLayout());

        root = this;
        while(getParent()!=null) {
            root = getParent();
        }
    }
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Dimension d = root.getSize();
        w = (int)(root.getWidth()/3.1);
        h = (int)(root.getHeight()/3.1);
    }
    public void messageReceived(Message m) {
        System.out.println(m.getType()+" "+m.getMessage());
        GraphDisplay gd = wrapped.get(m.getType());
        if(gd==null) {
            gd = new GraphDisplay(m.getType());
            wrapped.put(m.getType(),gd);
            add(gd);
            revalidate();
        }
        gd.addPoint(m.getReceived().getTime(), Double.valueOf(m.getMessage()));
    }
    
}
