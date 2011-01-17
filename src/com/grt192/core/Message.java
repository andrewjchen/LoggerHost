/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.grt192.core;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 *
 * @author data, ajc
 */
public class Message implements Comparable<Message> {
    private Date received;
    private String message;
    private String type;
    public Message(Date received,String type,String message) {
        this.received = received;
        this.type = type;
        this.message = message;
    }
    public Date getReceived() { return received; }
    public String getType() { return type; }
    public String getMessage() { return message; }

    /**
     * String in format for message windows
     */
    public String toString(){
        Calendar c = new GregorianCalendar(TimeZone.getDefault());
        c.setTime(getReceived());
        return "(" + 
                c.get(Calendar.HOUR) + ":" +
                c.get(Calendar.MINUTE) + ":" +
                c.get(Calendar.SECOND) + ")" +
                getType() + ":" +
                getMessage() ;
    }

    public int compareTo(Message t) {
        return getReceived().compareTo(t.getReceived());
    }
}
