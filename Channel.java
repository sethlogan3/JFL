package jfl;

import java.util.*;
import org.json.JSONObject;

public class Channel extends Logger{
    public static class Mode {
        public static final String ADS="ads";
        public static final String CHAT="chat";
        public static final String BOTH="both";
    }

    public static class Type {
        public static final String PUBLIC="public";
        public static final String PRIVATE="private";
    }
    
    private static ArrayList<Channel> channels=new ArrayList();
    
    public static ArrayList<Channel> getChannels() {
        return channels;
    }
    
    public static Channel getChannel(String name) {
        for (Channel channel:channels) {
            if (channel.getName().equals(name))
                return channel;
        }
        
        return null;
    }
    
    public static ArrayList<Channel> addChannel(Channel channel) {
        channels.add(channel);
        return channels;
    }
    
    public static ArrayList<Channel> removeChannel(String name) {
        for (Channel channel:channels) {
            if (channel.getName().equals(name))
                channels.remove(channel);
        }
        
        return channels;
    }
    
    
    //////////
    
    
    private String name,title,description,mode,type;
    private Character owner;
    private int numberOfOccupants;
    
    private ArrayList<Character> occupants=new ArrayList();
    private ArrayList<Character> ops=new ArrayList();
    


   // public Channel(String channelName,String channelTitle, String channelType) {
   //     name=channelName;
   //     title=channelTitle;
   //     type=channelType;
   // }
    
    public Channel(String channelName,String channelType) {
        name=channelName;
        type=channelType;
    }

        
    public Character getOwner() {
        return owner;
    }

    public void setOwner(Character newOwner) {
        owner=newOwner;
    }
        
    public String getName() {
        return name;
    }
 
    public void setName(String channelName) {
        name=channelName;
    }
    
    public String getTitle() {
        return title;
    }
     
    public void setTitle(String channelTitle) {
        title=channelTitle;
    }
    
    public String getDescription() {
        return description;
    }

    public void setDescription(String channelDescription) {
        description=channelDescription;
    }

    public int getNumberOfOccupants() {
        return numberOfOccupants;
    }
    
    public void setNumberOfOccupants(int num) {
        numberOfOccupants=num;
    }
    
    public String getMode() {
        return mode;
    }
 
    public void setMode(String m) {
        mode=m;
    }
 
    public String getType() {
        return type;
    }

    public void setType(String channelType) {
        type=channelType;
    }

    
    //////////
    
    
    public ArrayList<Character> getOccupants() {
        return occupants;    
    }
    
    public ArrayList<String> getOccupantNames() {
        ArrayList<String> names=new ArrayList();
        
        for (Character occupant:occupants)
            names.add(occupant.getName());
        
        return names;
    }

    public void setOccupants(ArrayList<Character> initialOccupants) {
        occupants.clear();
        occupants=initialOccupants;
    }
        
    public void addOccupant(Character occupant) {
        occupants.add(occupant);
    }
    
    public void removeOccupant(Character occupant) {
        occupants.remove(occupant);
    }

    public void clearOccupants() {
        occupants.clear();
    }

    public ArrayList<Character> getOps() {
        return ops;
    }
        
    public void setOps(ArrayList<Character> newOps) {
        ops=newOps;
    }

    public void addOp(Character op) {
        ops.add(op);
    }

    public void removeOp(Character op) {
        ops.remove(op);
    }

    public boolean isOccupant(String characterName) {
        for (Character occupant:occupants) {
            if (occupant.getName().equals(characterName))
                return true;
        }
        
        return false;
    }
    
    public JSONObject toJSON() {
        JSONObject obj=new JSONObject();
        obj.put("name",name);
        obj.put("title",title);
        obj.put("description",description);
        obj.put("name",name);
        obj.put("mode",mode);
        obj.put("type",type);
        obj.put("owner",owner.getName());
        obj.put("occupants",numberOfOccupants);
        return obj;
    }

    @Override public String toString() {
        return toJSON().toString();
    }
}
