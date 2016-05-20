package jfl;

import java.util.*;

public class Channel extends Logger{
    private static ArrayList<Channel> channels=new ArrayList();
    private ArrayList<Character> occupants=new ArrayList();
    private ArrayList<Character> ops=new ArrayList();
    
    private Character owner;
    private String name,title,description,mode,type;
    private int numberOfOccupants;
    
    public Channel(String channelName) {
        name=channelName;
    }

    public Channel(String channelName,String channelTitle) {
        name=channelName;
        title=channelTitle;
    }
    
    public Channel(String channelName,String channelTitle,String channelMode,int num) {
        name=channelName;
        title=channelTitle;
        mode=channelMode;
        numberOfOccupants=num;
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

    public void setDescription(String descript) {
        description=descript;
    }
        
    public Character getOwner() {
        return owner;
    }
 
    public int getNumberOfOccupants() {
        return numberOfOccupants;
    }
    
    public void setNumberOfOccupants(int num) {
        numberOfOccupants=num;
    }
    
    public void setOwner(Character newOwner) {
        owner=newOwner;
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

    public void setOccupants(ArrayList<Character> initialOccupants) {
        occupants.clear();
        occupants=initialOccupants;
    }
    
    public ArrayList<String> getOccupantNames() {
        ArrayList<String> names=new ArrayList();
        
        for (Character occupant:occupants)
            names.add(occupant.getName());
        
        return names;
    }
 
    public ArrayList<Character> getOccupants() {
        return occupants;    
    }
        
    public void clearOccupants() {
        occupants.clear();
    }
    
    public void setOps(ArrayList<Character> newOps) {
        ops=newOps;
    }
    
    public ArrayList<Character> getOps() {
        return ops;
    }
    
    public void addOp(Character op) {
        ops.add(op);
    }

    public void removeOp(Character op) {
        ops.remove(op);
    }
    
    public void addOccupant(Character occupant) {
        occupants.add(occupant);
    }
    
    public void removeOccupant(Character occupant) {
        occupants.remove(occupant);
    }
  
    @Override public String toString() {
        return name+"; Title:"+title+"; Mode:"+mode+"; Occupants:"+occupants+"; Description:"+description+"; Ops:"+ops+"; Owner:"+owner+"; Type: "+type;
    }

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
    
    public static void removeChannel(String name) {
        for (Channel channel:channels) {
            if (channel.getName().equals(name))
                channels.remove(channel);
        }
    }
}
