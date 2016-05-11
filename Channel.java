package superbot;

import java.util.ArrayList;

public class Channel {
    public static enum ChannelType{PUBLIC,PRIVATE};
    
    public ArrayList<String> occupants=new ArrayList<>();
    public ArrayList<String> ops=new ArrayList<>();
    
    public String name,title,description,owner,mode;
    public ChannelType type;
    
    public Channel(String initialName) {
        name=initialName;
    }
 
    public Channel(String initialName,String initialTitle) {
        name=initialName;
        title=initialTitle;
    }
        
    public void setOccupants(ArrayList<String> initialOccupants) {
        occupants=initialOccupants;
    }
    
    public void setOps(ArrayList<String> initialOps) {
        ops=initialOps;
    }
    
    public void setType(ChannelType channelType) {
        type=channelType;
    }
    
    public void addOp(String op) {
        ops.add(op);
    }
    
    public void setDescription(String descript) {
        description=descript;
    }
    
    public void setOwner(String name) {
        owner=name;
    }
    
    public void addOccupant(String name) {
        occupants.add(name);
    }
    
    public void removeOccupant(String name) {
        occupants.remove(name);
    }
  
    public void setMode(String m) {
        mode=m;
    }

    @Override public String toString() {
        return name+"; Title:"+title+"; Mode:"+mode+"; Occupants:"+occupants+"; Description:"+description+"; Ops:"+ops+"; Owner:"+owner+"; Type: "+type;
    }
}
