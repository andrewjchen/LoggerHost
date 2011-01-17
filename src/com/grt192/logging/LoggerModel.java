package com.grt192.logging;

import com.grt192.core.*;
import com.grt192.networking.*;
import java.io.*;
import java.util.*;
import java.util.logging.*;

/**
 * A host GRTLogger server client which sorts, processes, and logs data
 * @author ajc, data
 */
public class LoggerModel implements SocketListener {

    private HashMap<String, ArrayList<Message>> messages;
    private ArrayList<MessageListener> messageListeners;
    private ArrayList<MessageListener> variableListeners;
    private int version;

    public LoggerModel() {
        messages = new HashMap<String, ArrayList<Message>>();
        messageListeners = new ArrayList<MessageListener>();
        variableListeners = new ArrayList<MessageListener>();
        version = -1;
    }

//    public void run() {
//        //throw new UnsupportedOperationException("Not supported yet.");
//    }
    public void onConnect(SocketEvent e) {
        System.out.println("Connected: "+e.getSource());
    }

    public void onDisconnect(SocketEvent e) {
    }

    /**
     * Data receive and store
     */
    public void dataRecieved(SocketEvent e) {
        System.out.println(e.getData());
        //construct new message from server data and time
        String s = e.getData();
        Date d = new Date();
        String type = s.substring(0, s.indexOf(":"));
        String message = s.substring(s.indexOf(":") + 1);
        if (messages.get(type) == null) {
            messages.put(type, new ArrayList<Message>());
        }
        Message m = new Message(d, type, message);
        //place message in proper slot
        messages.get(type).add(m);
        //notify listeners
        if(type.startsWith("(var)")) {
            //System.out.println("this is a var");
            notifyListeners(variableListeners,m);
        } else {
            //System.out.println("this is not a var");
            notifyListeners(messageListeners,m);
        }

    }

    private void notifyListeners(ArrayList<MessageListener> arrl,Message m) {
        for (MessageListener ml : arrl) {
            ml.messageReceived(m);
        }
    }

    /**
     * Get all messages of selection
     */
    public String getMessages(String[] types) {
        if (types == null) {
            return null;
        }
        System.out.println("Get messages of types " + Arrays.toString(types));

        //get all useful messages
        ArrayList<Message> ret = new ArrayList<Message>();
        for (int i = 0; i < types.length; i++) {
            String type = types[i];
            ArrayList<Message> m = messages.get(type);
            ret.addAll(m);
        }

        //sort and return
        System.out.println("some messages of type " + types + "\n :" + ret);
        Message[] pairs = ret.toArray(new Message[0]);
        Arrays.sort(pairs);
        return messagesToString(pairs);
    }

    /**
     * Get all messages
     */
    public String getMessages() {
        //get all arraylists of messages and save
        Collection<ArrayList<Message>> values = messages.values();
        ArrayList<Message> all = new ArrayList<Message>();
        for (ArrayList<Message> a : values) {
            all.addAll(a);
        }

        //sort
        Message[] pairs = all.toArray(new Message[0]);
        Arrays.sort(pairs);
        //format


        return messagesToString(pairs);
    }

    public void writeLogs() {
        //for each type, save a log
        String[] keys = messages.keySet().toArray(new String[0]);
        for (String key : keys) {
            System.out.println("Writing key: "+ key);
            writeLog(key);
        }
        //for all, save a log
        //TODO better name than "root"?
        //globals, all, *,
        writeLogFile("root", getMessages());
        //TODO: doubles? X and Y combine log?
    }

    public void writeLog(String type) {
        //get all messages
        String[] types = {type};
        writeLogFile(type, getMessages(types));

    }
    
    public void writeLogFile(String name, String data) {
        try {
            new File(getVersion()+"").mkdir();
            File f = new File(getVersion()+"/"+name);
//            f.
            PrintWriter out =
                    new PrintWriter(
                    new BufferedWriter(
                    new FileWriter(f)));
            out.write(data);
            out.close();
        } catch (IOException ex) {
            Logger.getLogger(LoggerModel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public int getVersion() {
        if(version < 0){
            version = getLastVersion() + 1;
        }
        return version;
    }

    public int getLastVersion() {
        int greatestver = 0;
        String[] files = new File(".").list();
        for (String file : files) {
            String ver = file.substring(file.lastIndexOf(".") + 1);

            if(isInt(ver)){
                greatestver = Math.max(greatestver, Integer.parseInt(ver));
            }

        }
        return greatestver;
    }

    public boolean isInt(String s){
        char[] cs = s.toCharArray();
        for(char ch: cs){
            if(!Character.isDigit(ch)){
                return false;
            }
        }
        return true;
    }

//    public void writeToFile(String name) {
//        try {
//            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(name)));
//            System.out.println(getMessages());
//            out.write(getMessages());
//            out.close();
//        } catch (IOException ex) {
//            Logger.getLogger(LoggerModel.class.getName()).log(Level.SEVERE, null, ex);
//        }
//
//    }
    /**
     * @deprecated use message.toString()
     */
    private String messageToString(Message m) {
        Calendar c = new GregorianCalendar(TimeZone.getDefault());
        c.setTime(m.getReceived());
        return "(" + c.get(Calendar.HOUR) + ":" + c.get(Calendar.MINUTE) + ":" + c.get(Calendar.SECOND) + ")" + m.getType() + ":" + m.getMessage() + "\n\n";
    }

    private String messagesToString(Message[] pairs) {
        String r = "";
        for (Message m : pairs) {
            r += m + "\n";
        }
        return r;
    }

    public void registerMessageListener(MessageListener m) {
        messageListeners.add(m);
    }

    public void removeMessageListener(MessageListener m) {
        messageListeners.remove(m);
    }

    public void registerVariableListener(MessageListener m) {
        variableListeners.add(m);
    }

    public void removeVariableListener(MessageListener m) {
        variableListeners.remove(m);
    }
}
