package jfl.components;
import java.util.*;
import org.json.*;

public class Message {
    public String sender;
    public String content;
    public Calendar time;
    public boolean ad;
    
    public Message(String senderCharacter,String msg) {
        sender=senderCharacter;
        content=msg;
        time=Calendar.getInstance();
        ad=false;
    }

    public Message(String senderCharacter,String msg,boolean adBool) {
        sender=senderCharacter;
        content=msg;
        time=Calendar.getInstance();
        ad=adBool;
    }
    
    public String getSender() {
        return sender;
    }
    
    public String getContent() {
        return content;
    }
    
    public Calendar getTimeSent() {
        return time;

    }
    
    public String getFormattedTime() {
        return time.getTime().toString();
    }

    public void setAsAd() {
        ad=true;
    }

    public void setAsAd(boolean bool) {
        ad=bool;
    }
    
    public boolean isAd() {
        return ad;
    }
    
    public JSONObject toJSONObject() {
        JSONObject obj=new JSONObject();
        obj.put("sender",sender);
        obj.put("content",content);
        obj.put("time",time);
        obj.put("ad",ad);
        return obj;
    }
}
