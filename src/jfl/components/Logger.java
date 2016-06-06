package jfl.components;

import jfl.components.Message;
import java.util.*;
import org.json.*;

public class Logger {     
    private HashMap<String,ArrayList<Message>>
            pmLogs=new HashMap(),
            channelLogs=new HashMap();

    private int maxLogSize=1000;
    
    public HashMap<String,ArrayList<Message>> getPMLogs() {
        return pmLogs;
    }
    
    public ArrayList<Message> getPMLog(String character) {
        return pmLogs.get(character);
    }

    public HashMap<String,ArrayList<Message>> getChannelLogs() {
        return channelLogs;
    }
        
    public ArrayList<Message> getChannelLog(String channel) {
        return channelLogs.get(channel);
    }
        
    public void logPM(String characterName,Message message) {
        addLog(pmLogs,characterName,message);
    }
    
    public void logChannelMessage(String channelName,Message message) {
        addLog(channelLogs,channelName,message);
    }
    
    private void addLog(HashMap<String,ArrayList<Message>> logs,String name,Message message) {
        if (!logs.containsKey(name))
            logs.put(name,new ArrayList());

        ArrayList<Message> messages=logs.get(name); 
        
        if (messages.size()==maxLogSize) 
            messages.remove(0);
        
        messages.add(message);        
    }

    public static String logToString(ArrayList<Message> messages) {
        String logString="";
                
        for (Message message:messages) 
            logString+=message.getFormattedTime()+"   "+message.getSender()+"   "+message.getContent()+"\n";
        
        return logString;        
    }
    
    public JSONObject getJSONObject() {
        JSONObject obj=new JSONObject();
        obj.put("pm",getLogsAsJSON(pmLogs));
        obj.put("channel",getLogsAsJSON(channelLogs));
        return obj;
    }

    public static JSONObject getLogsAsJSON(HashMap<String,ArrayList<Message>> logs) {
        JSONObject obj=new JSONObject();
        
        for (Map.Entry entry : logs.entrySet()) {
            String key=(String)entry.getKey();
            ArrayList<Message>messages=(ArrayList<Message>)entry.getValue();
            
            JSONArray messageArray=new JSONArray();
            for (int i=0; i<messages.size(); i++) 
                messageArray.put(i,messages.get(i).toJSONObject());
            
            obj.put(key,messageArray);
        }
        
        return obj;
    }
}
