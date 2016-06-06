package jfl.components.channel;

public class PrivateSnapshot extends ChannelSnapshot{
    public String title;

    public PrivateSnapshot(String channelName, String channelTitle, int channelOccupants) {
        name=channelName;
        title=channelTitle;
        numberOfOccupants=channelOccupants;
    }   
    
    public String getTitle() {
        return title;
    }
}
