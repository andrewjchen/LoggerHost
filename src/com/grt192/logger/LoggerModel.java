
package com.grt192.logger;

import com.grt192.core.Message;
import com.grt192.networking.SocketEvent;
import com.grt192.networking.SocketListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.TimeZone;

/**
 *
 * @author ajc
 */
public class LoggerModel implements SocketListener{

    private Hashtable<String,ArrayList<Message>> messages;
    private ArrayList<MessageListener> listeners;
    private Comparator<Message> comp = new Comparator<Message>() {

            public int compare(Message o1, Message o2) {
                return o1.getReceived().compareTo(o2.getReceived());
            }

        };

    public LoggerModel() {
        messages = new Hashtable<String,ArrayList<Message>>();
        listeners = new ArrayList<MessageListener>();
    }

    public void run() {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    public void onConnect(SocketEvent e) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    public void onDisconnect(SocketEvent e) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    public void dataRecieved(SocketEvent e) {
        String s = e.getData();
        Date d = new Date();
        String type = s.substring(0,s.indexOf("/"));
        String message = s.substring(s.indexOf("/")+1);
        if(messages.get(type)==null) {
            messages.put(type,new ArrayList<Message>());
        }
        Message m = new Message(d,type,message);
        messages.get(type).add(m);
        for(MessageListener ml : listeners) {
            ml.messageReceived(m);
        }
    }

    public Message[] getMessages(String[] types) {
        if(types==null) return null;
        ArrayList<Message> ret = new ArrayList<Message>();
        for(int i=0;i<types.length;i++) {
            String type = types[i];
            ArrayList<Message> m = messages.get(type);
            ret.addAll(m);
        }

        Message[] pairs = (Message[])ret.toArray();

        Arrays.sort(pairs,comp);

        return pairs;
        
    }
    public Message[] getAllMessages() {
        int count=0;
        Enumeration<ArrayList<Message>> e = messages.elements();
        ArrayList<Message> ret = new ArrayList<Message>();
        while(e.hasMoreElements()) {
            ArrayList<Message> list = e.nextElement();
            ret.addAll(list);
        }

        Message[] pairs = (Message[])ret.toArray();

        Arrays.sort(pairs,comp);

        return pairs;
        
    }

    public void registerMessageListener(MessageListener m) {
        listeners.add(m);
    }
    public void removeMessageListener(MessageListener m) {
        listeners.remove(m);
    }

}
