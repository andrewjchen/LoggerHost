/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.grt192.core;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Vector;
import javax.swing.JComponent;

/**
 *
 * @author anand
 */
public class GraphComponent extends JComponent {

    public static final int POINT_SIZE = 1;
    private double xValue;
    private double yValue;
    private boolean autoScale;
    private Vector xHistory;
    private Vector yHistory;
    private Vector markers;
    private double max;
    private boolean details;

    public GraphComponent() {
        this(true);
    }

    public GraphComponent(boolean auto) {
        this.autoScale = auto;
        xHistory = new Vector();
        yHistory = new Vector();
        markers = new Vector();
        max = (auto ? 0 : 1);
        details = true;
    }

    public double getMax() {
        return max;
    }

    public void setMax(double max) {
        this.max = max;
    }

    public void placeMarker(double xVal) {
        markers.addElement(new Double(xVal));
    }

    public void removeMarker(int no) {
        markers.removeElementAt(no);
    }

    public void clearMarkers() {
        markers.removeAllElements();
    }

    public Vector getMarkers() {
        return markers;
    }

    //@Override
    public void paintComponent(Graphics g) {

        try {
            g.setColor(Color.BLACK);
            g.fill3DRect(0, 0, this.getWidth(), this.getHeight(), false);
            g.setColor(Color.RED);
            for (int m = 0; m < markers.size(); m++) {
                g.fillRect(((Double) markers.elementAt(m)).intValue() % this.getWidth(), 0, POINT_SIZE, this.getHeight());
            }
            g.setColor(Color.WHITE);
            for (int i = xHistory.size() - 1; xHistory.size() - i <= this.getWidth() && i >= 0; i--) {
                g.drawRect(
                        (int) (((Double) (xHistory.elementAt(i))) % this.getWidth()), (int) (this.getHeight() * (1 - (((Double) (yHistory.elementAt(i))) / max)) - POINT_SIZE),
                        POINT_SIZE, POINT_SIZE);
                if (i > 0) {
                    g.drawLine((int) (((Double) (xHistory.elementAt(i))) % this.getWidth()),
                            (int) (this.getHeight() * (1 - (((Double) (yHistory.elementAt(i))) / max))),
                             (int) (((Double) (xHistory.elementAt(i - 1))) % this.getWidth()),
                            (int) (this.getHeight() * (1 - (((Double) (yHistory.elementAt(i - 1))) / max))));
                }
            }
            g.setColor(Color.GREEN);
            g.fillRect((int) xValue % this.getWidth(), (int) (this.getHeight() * (1 - (yValue / max))) - POINT_SIZE, POINT_SIZE + 2, POINT_SIZE + 2);
        } catch (Exception e) {
            //System.out.println("----");
        }
        g.setColor(Color.WHITE);
        if (details) {
            g.drawString(yValue + "", 5, 11);
        }
    }

    public void addPoint(double x, double y) {
        setXValue(x);
        setYValue(y);

        if (autoScale && y > max) {
            max = y;
        }
        this.repaint();
    }

    public boolean isDetails() {
        return details;
    }

    public void setDetails(boolean details) {
        this.details = details;
    }

    public void reset() {
        xHistory.removeAllElements();
        yHistory.removeAllElements();
    }

    public Vector getXHistory() {
        return xHistory;
    }

    public Vector getYHistory() {
        return yHistory;
    }

    public double getXValue() {
        return xValue;
    }

    public void setXValue(double xValue) {
        this.xValue = xValue;
        if (xHistory.size() > 1 && xValue % getWidth() < ((Double) xHistory.lastElement()) % getWidth()) {
            reset();
        }
        xHistory.addElement(new Double(xValue));
    }

    public double getYValue() {
        return yValue;
    }

    public void setYValue(double yValue) {
        this.yValue = yValue;
        if (yHistory.size() >= this.getWidth()) {
            yHistory.removeAllElements();
        }
        yHistory.addElement(new Double(yValue));
    }

    public boolean isAutoScale() {
        return autoScale;
    }

    public void setAutoScale(boolean autoScale) {
        this.autoScale = autoScale;
    }
}