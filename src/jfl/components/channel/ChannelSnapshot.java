package jfl.components.channel;

public abstract class ChannelSnapshot {
    public int numberOfOccupants;
    public String name;
    
    public void setNumberOfOccupants(int num) {
        numberOfOccupants=num;
    }
    
    @Override public String toString() {
        return "ChannelSnapshot: "+name+" "+numberOfOccupants;
    }
}
