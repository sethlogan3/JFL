package jfl.components.channel;

import java.util.*;
import jfl.components.Character;
import org.json.JSONObject;

public class Channel { 
    public static class ChannelStatus {
        public static String PUBLIC="public";
        public static String PRIVATE="private";
    }
    
    private String name,title,description,mode,type;
    private String owner;
    
    private ArrayList<Character> occupants=new ArrayList();
    private ArrayList<String> ops=new ArrayList();
    private ArrayList<String> bannedCharacters=new ArrayList();
    
    public Channel(String channelName,String channelTitle, String channelType) {
        name=channelName;
        title=channelTitle;
        type=channelType;
    }
    
    public Channel(String channelName,String channelType) {
        name=channelName;
        type=channelType;
    }

        
    public String getOwner() {
        return owner;
    }

    public void setOwner(String newOwner) {
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

    public void removeOccupant(String occupantName) {
        for (Character occupant:occupants) {
            if (occupant.getName().equals(occupantName)) 
                occupants.remove(occupant);
        }
    }
    
    public void clearOccupants() {
        occupants.clear();
    }

    public ArrayList<String> getOps() {
        return ops;
    }
        
    public void setOps(ArrayList<String> newOps) {
        owner=newOps.get(0);
        ops=newOps;
    }

    public void addOp(String op) {
        ops.add(op);
    }

    public void removeOp(String op) {
        ops.remove(op);
    }

    public boolean isOccupant(String characterName) {
        for (Character occupant:occupants) {
            if (occupant.getName().equals(characterName))
                return true;
        }
        
        return false;
    }

    public int getNumberOfOccupants() {
        return occupants.size();
    }
    
    public JSONObject toJSON() {
        JSONObject obj=new JSONObject();
        obj.put("name",name);
        obj.put("title",title);
        obj.put("description",description);
        obj.put("name",name);
        obj.put("mode",mode);
        obj.put("type",type);
        obj.put("owner",owner);
        obj.put("occupants",occupants.size());
        return obj;
    }

    @Override public String toString() {
        return toJSON().toString();
    }
}
