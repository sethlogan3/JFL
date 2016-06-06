package jfl.components;

import jfl.components.Character;
import jfl.components.channel.Channel;
import java.util.*;
import org.json.*;

public class FChat {
    public enum Server{PUBLIC,TEST};
        
    public static final String TEST_URL="ws://chat.f-list.net:8722";
    public static final String PUBLIC_URL="ws://chat.f-list.net:9722";
    
    public ArrayList<Character> characters=new ArrayList(); 
    public ArrayList<Channel> channels=new ArrayList();   

    int serverConnections=0;
        
    public static class ChannelMode {
        public static final String ADS="ads";
        public static final String CHAT="chat";
        public static final String BOTH="both";
    }

    public static class ChannelType {
        public static final String PUBLIC="public";
        public static final String PRIVATE="private";
    }
    
    public int getServerConnections() {
        return serverConnections;
    }
    
    public ArrayList<Character> getCharacters() {
        return characters;
    }
    
    public void addCharacter(Character character) {
        serverConnections++;
        characters.add(character);
    }
    
    public Character getCharacter(String name) {               
        for (Character character:characters) {
            if (character.getName().equals(name))
                return character;
        }
        
        return null;
    }

    public void removeCharacter(Character character) {
        serverConnections--;
        characters.remove(character);
    }
        
    public void removeCharacter(String characterName) {
        characters.remove(getCharacter(characterName));
    }
    
    public ArrayList<Channel> getChannels() {
        return channels;
    }
    
    public Channel getChannel(String name) {
        for (Channel channel:channels) {
            if (channel.getName().equals(name))
                return channel;
        }
        
        return null;
    }

    public ArrayList<Channel> addChannel(Channel channel) {
        channels.add(channel);
        return channels;
    }
    
    public ArrayList<Channel> removeChannel(String name) {
        for (Channel channel:channels) {
            if (channel.getName().equals(name))
                channels.remove(channel);
        }
        
        return channels;
    }
    
    public boolean isOnline(String characterName) throws Exception {
        return getCharacter(characterName)==null;
    }
}
