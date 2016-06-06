package jfl.components.channel;

public class PublicSnapshot extends ChannelSnapshot{
    public String mode;

    public PublicSnapshot(String channelName, String channelMode, int channelOccupants) {
        name=channelName;
        mode=channelMode;
        numberOfOccupants=channelOccupants;
    }
    
    public String getMode() {
        return mode;
    }
}
