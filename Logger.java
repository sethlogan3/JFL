package jfl;

import java.io.*;
import java.util.ArrayList;

public class Logger {
    private final ArrayList<Message> messages=new ArrayList();
    private int maxLogSize=1000;
 
    public ArrayList<Message> getMessages() {
        return messages;
    }
        
    public void addMessage(Message message) {
        messages.add(message);
    }
    
    public void clearLog() {
        messages.clear();
    }
    
    public int getLogSize() {
        return messages.size();
    }
    
    public Message getMessage(int index) {
        return messages.get(index);
    }
    
    public Message getLastMessage() {
        return messages.get(messages.size()-1);
    }
    
    public int getMaxLogSize() {
        return maxLogSize;
    }
    
    public void setmaxLogSize(int max) {
        maxLogSize=max;
    }
    
    public String getLogString() {
        String logString="";
        for (Message message:messages) 
            logString+=message.getFormattedTime()+"   "+message.getSenderName()+"   "+message.getContent()+"\n";
        
        return logString;
    }
    
    public void saveLog(String filename) throws FileNotFoundException {
        try(PrintWriter out = new PrintWriter(filename)){
            out.println(getLogString());
        }
    }
}
