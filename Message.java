package jfl;
import java.util.*;

public class Message {
    public Character sender;
    public String content;
    public Calendar timeSent;
    
    public Message(Character senderCharacter,String msg) {
        sender=senderCharacter;
        content=msg;
        timeSent=Calendar.getInstance();
    }
    
    public Character getSender() {
        return sender;
    }
    
    public String getSenderName() {
        return sender.getName();
    }
    
    public String getContent() {
        return content;
    }
    
    public Calendar getTimeSent() {
        return timeSent;

    }
    
    public String getFormattedTime() {
        return timeSent.getTime().toString();
    }
}
