/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.grt192.core;

import java.util.Date;

/**
 *
 * @author student
 */
public class Message {
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
}
