package superbot;

import java.net.*;
import java.lang.reflect.Method;
import java.util.*;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.*;
import superbot.Channel.*;

public abstract class FClient{
    public String client="FClient";
    public String version="1.0";
    public static String ticket,user;
    private final WebSocketClient websocket;
    //public static HashMap<Integer,Kink> kinksList=new HashMap();
    public HashMap<String,Channel> channels=new HashMap();
    public HashMap<String,Character> characters=new HashMap();
    public String clientCharacter;
    
    //Generate new FClient
    public FClient(String server) throws Exception {  
        websocket=generateWebsocket(server);
        updateKinksList();
    }

    private WebSocketClient generateWebsocket(String uri) throws URISyntaxException {
        return new WebSocketClient(new URI(uri)) {
            @Override public void onOpen(ServerHandshake sh) {
                System.out.println( "Notice: Websocket connection opened..." );
                try {
                    identify(user,clientCharacter,ticket,client,version,"ticket");
                } catch (Exception ex) {}
            }

            @Override public void onMessage(String message)  {       
                JSONObject json=new JSONObject();
                System.out.println("<< "+message);       
                String command=message.substring(0,3);
                
                if (message.length()>3)
                    json=new JSONObject(message.substring(4,message.length()));

                try {
                    interpretServerCommand(command,json);
                } catch (Exception ex) {}
            }

            @Override public void onClose(int i, String string, boolean bool) {
                System.out.println( "Notice: Websocket connection closed." );
            }

            @Override public void onError(Exception e) {
                System.out.println(e);
            }
        
        };
    }
    
    private void interpretServerCommand(String command,JSONObject param) throws Exception{
        String methodName="got"+command;
        Method m=FClient.class.getDeclaredMethod(methodName,new Class[]{JSONObject.class});
        m.invoke(FClient.this,param);    
    }
    
    public void login(String username,String character,String password) throws Exception {
        user=username;
        clientCharacter=character;
        ticket=EndpointUtil.getTicket(user,password);
        websocket.connect();
    }

    public void setClientName(String name) {
        client=name;
    }

    public Character getCharacter(String name) {
        if (!characters.containsKey(name)) 
            characters.put(name,new Character(name));     
         
        return characters.get(name);
    }
    
    public void setClientVersion(String versionName) {
        version=versionName;
    }

    public final void updateKinksList() throws Exception {
        JSONObject kinkInfo=EndpointUtil.postRequest(EndpointUtil.getApiUrl("kink-list"),"").getJSONObject("kinks");
        Iterator keys = kinkInfo.keys();

        while(keys.hasNext()) {
            JSONObject currentGroup=kinkInfo.getJSONObject((String)keys.next());
            String category=currentGroup.getString("group");
            JSONArray items=currentGroup.getJSONArray("items");

            for (int i=0; i<items.length(); i++) {
                JSONObject currentKink=items.getJSONObject(i);
                int id=currentKink.getInt("kink_id");
                //kinksList.put(id,new Kink(currentKink.getString("name"),id,category)); 
                Character.kinksList.add(new Kink(currentKink.getString("name"),id,category)); 
            }
        }
    } 
    
    private void send(String message) {
        System.out.println(">> "+message);
        websocket.send(message);
    }
    
    private void sendCommand(String command,String... args) throws JSONException {
        JSONObject info=new JSONObject(); 
             
        for (int i=0; i<args.length;i+=2) 
            info.put(args[i],args[i+1]);

        send(command+" "+info.toString());
    }

    
    public void onLogin() throws Exception{};
    private void gotIDN(JSONObject unused) throws Exception {
        onLogin();
    }
    
    private void gotNLN(JSONObject param) throws Exception {
        String name=param.getString("identity");
        String genderString=param.getString("gender").toLowerCase();
        getCharacter(name).setGender(genderString);
    }
    
    public void pingRecieved() {};
    private void gotPIN(JSONObject unused) throws JSONException {
        ping();
        pingRecieved();
    }
    
    public void characterJoinedRoom(Character character,Channel channel) {};
    private void gotJCH(JSONObject param) throws JSONException {
        String characterName=param.getJSONObject("character").getString("identity");
        Character character=characters.get(characterName);
        
        String channelName=param.getString("channel");
        Channel channel;

        if (characterName.equals(clientCharacter)) {
            channels.put(channelName,new Channel(channelName,param.getString("title")));
            channel=channels.get(channelName);
            
            if (channelName.substring(0,3).equals("ADH"))
                channel.setType(ChannelType.PRIVATE);
            else
                channel.setType(ChannelType.PUBLIC);
        } 
        else
            channel=channels.get(channelName);

        characterJoinedRoom(character,channel);
    }

    
    private void gotRMO(JSONObject param) throws JSONException {
        Channel channel=channels.get(param.getString("channel"));
        String mode=param.getString("mode");
        channel.setMode(mode);
        roomModeChanged(channel,mode);
    }
    public void roomModeChanged(Channel channel,String mode) {};
    
    public void channelOpsRecieved(JSONObject param) {};
    private void gotCOL(JSONObject param) throws JSONException {
        Channel channel=channels.get(param.getString("channel"));
        JSONArray ops=param.getJSONArray("oplist");
        channel.setOwner(ops.getString(0));
        
        for (int i=0; i<ops.length();i++)             
            channel.addOp(ops.getString(i));
        
        channelOpsRecieved(param);
    }

    public void channelDataRecieved(JSONObject param) {};
    public void gotICH(JSONObject param) throws JSONException {
        Channel channel=channels.get(param.getString("channel"));
        ArrayList<String> users=new ArrayList<>();
        JSONArray usersData=param.getJSONArray("users");
        
        for (int i=0; i<usersData.length();i++) 
            users.add(((JSONObject)usersData.get(i)).getString("identity"));

        channel.setOccupants(users);
        channel.setMode(param.getString("mode"));
        
        channelDataRecieved(param);
    }

    private void gotCDS(JSONObject param) {
        Channel channel=channels.get(param.getString("channel"));
        channel.setDescription(param.getString("description"));
        System.out.println("*"+channels.get(param.getString("channel")));
    }
    
    private void gotLCH(JSONObject param) {
        String character=param.getString("character");
        String channelName=param.getString("channel");
        
        if (character.equals(clientCharacter)) 
            channels.remove(channelName);
        else
            channels.get(channelName).removeOccupant(character);
    }
    
    //This command requires chat op or higher. Request a character's account be banned from the server.
    public void globalBan(String character) throws JSONException {
        sendCommand("ACB","character",character);
    }

    //This command is admin only. Promotes a user to be a chatop (global moderator).
    public void promoteToChatop(String character) throws JSONException {
        sendCommand("AOP","character",character);
    }
        
    //This command requires chat op or higher. Requests a list of currently connected alts for a characters account.
    public void getConnectedAlts(String character) throws JSONException {
        sendCommand("AWC","character",character);
    }
        
    //This command is admin only. Broadcasts a message to all connections.
    public void broadcast(String message) throws JSONException {
        sendCommand("BRO","message",message);
    }
    
    //This command requires channel op or higher. Request the channel banlist.
    public void getBanlist(String channel) throws JSONException {
        sendCommand("CBL","channel",channel);
    }
    
    //This command requires channel op or higher. Bans a character from a channel.
    public void ban(String character,String channel) throws JSONException {
        sendCommand("CBU","character",character,"channel",channel);       
    }
    
    //Create a private, invite-only channel.
    public void createPrivateChannel(String channel) throws JSONException {
        sendCommand("CCR","channel",channel);
    }
    
    //This command requires channel op or higher. Changes a channel's description.
    public void changeDescription(String channel,String description) throws JSONException {
        sendCommand("CDS","channel",channel,"description",description);
    }
    
    //Request a list of all public channels.
    public void getPublicChannels(){
        send("CHA");       
    }
    
    //This command requires channel op or higher. Sends an invitation for a channel to a user.
    public void invite(String channel,String character) throws JSONException {
        sendCommand("CIU","channel",channel,"character",character);       
    }
    
    //This command requires channel op or higher. Kicks a user from a channel.
    public void kick(String channel,String character) throws JSONException {
        sendCommand("CKU","channel",character,"character",channel);       
    }    
    
    //This command requires channel op or higher. Request a character be promoted to channel operator (channel moderator).
    public void promote(String channel,String character) throws JSONException {
        sendCommand("COA","channel",character,"character",channel);       
    }    
    
    //Requests the list of channel ops (channel moderators).
    public void getChannelOps(String channel) throws JSONException {
        sendCommand("COL","channel",channel);       
    }
    
    //This command requires channel op or higher. Demotes a channel operator (channel moderator) to a normal user.
    public void demote(String channel,String character) throws JSONException {
        sendCommand("COR","channel",character,"character",channel);       
    }    
    
    //This command is admin only. Creates an official channel.
    public void createPublicChannel(String channel) throws JSONException {
        sendCommand("CRC","channel",channel);
    }
 
    //This command requires channel op or higher. Set a new channel owner.
    public void setOwner(String character,String channel) throws JSONException {
        sendCommand("CSO","character",character,"channel",channel);       
    }
    
    //This command requires channel op or higher. Temporarily bans a user from the channel for 1-90 minutes. A channel timeout.
    public void timeout(String channel,String character,String length) throws JSONException {
        sendCommand("CTU","channel",character,"character",channel,"length",length);       
    }
    
    //This command requires channel op or higher. Unbans a user from a channel.
    public void unban(String channel,String character) throws JSONException {
        sendCommand("CUB","channel",character,"character",channel);       
    } 
    
    //This command is admin only. Demotes a chatop (global moderator).
    public void globalDemote(String character) throws JSONException {
        sendCommand("DOP","character",character);
    }
    
    //Search for characters fitting the user's selections. Kinks is required, all other parameters are optional.
    public void searchCharacters(String kinks, String genders, String orientations, String languages, String furryprefs, String roles) throws Exception {
        //Later implementation
    }

    //This command is used to identify with the server.
    public void identify(String account,String character,String ticket, String client, String cversion, String method) throws Exception {
        
        sendCommand("IDN","account",account,"character",character,"ticket",ticket,"client",client,"cversion",cversion,"method",method);
    }

    //A multi-faceted command to handle actions related to the ignore list. The server does not actually handle much of the ignore process, as it is the client's responsibility to block out messages it recieves from the server if that character is on the user's ignore list.
    public void handleIgnore(String action, String character) throws JSONException {
        sendCommand("IGN","action",action,"character",character);
    }

    //Send a channel join request.
    public void joinChannel(String channel) throws JSONException {
        sendCommand("JCH","channel",channel);
    }
    
    //This command requires chat op or higher. Request a character be kicked from the server.
    public void kickFromServer(String character) throws JSONException {
        sendCommand("KIK","character",character);
    }
    
    //Request a list of a user's kinks.
    public void getKinks(String character) throws JSONException {
        sendCommand("KIN","character",character);
    }
    
    //Request to leave a channel.
    public void leaveChannel(String channel) throws JSONException {
        sendCommand("LCH","channet",channel);
    }
    
    //UPDATE
    public void sendAd(String channel,String message) throws JSONException {
        sendCommand("LRP","channel",channel,"message",message);
    }
    
    //Sends a message to all other users in a channel.
    public void sendMessage(String channel,String message) throws JSONException {
        sendCommand("MSG","channel",channel,"message",message);
    }
     
    //Request a list of open private rooms.
    public void getPrivateRooms() {
        send("ORS");
    }

    //Sends a ping response to the server. Timeout detection, and activity to keep the connection alive.
    public void ping() {
        send("PIN");
        
    }   

    //Sends a private message to another user.
    public void sendPrivateMessage(String recipient,String message) throws JSONException {
        sendCommand("PRI","recipient",recipient,"message",message);
    }
        
    //Requests some of the profile tags on a character, such as Top/Bottom position and Language Preference.
    public void getProfileTags(String character) throws JSONException {
        sendCommand("PRO","character",character);
    }
    
    //Roll dice or spin the bottle.
    public void roll(String channel,String dice) throws JSONException {
        sendCommand("RLL","channel",channel,"dice",dice);
    }
    
    //This command requires chat op or higher. Reload certain server config files
    public void reloadConfig(String save) throws JSONException {
         sendCommand("RLD","save",save);
    }
    
    //This command requires channel op or higher. Change room mode to accept chat, ads, or both.
    public void setRoomChatMode(String channel,String mode) throws JSONException {
         sendCommand("RMO","channel",channel,"mode",mode);
    }
    
    //This command requires channel op or higher. Sets a private room's status to closed or open.
    public void setRoomStatusMode(String channel,String status) throws JSONException {
         sendCommand("RST","channel",channel,"mode",status);
    }  
    
    //This command is admin only. Rewards a user, setting their status to 'crown' until they change it or log out.
    public void reward(String character) throws JSONException {
         sendCommand("RWD","character",character);
    }  
    
    //Alerts admins and chatops (global moderators) of an issue.
    public void alertModerators(String action,String report,String character) throws JSONException {
         sendCommand("SFC","action",action,"report",report,"character",character);
    } 
    
    //Request a new status be set for your character.
    public void setStatus(String status,String statusmsg) throws JSONException {
         sendCommand("STA","status",status,"statusmsg",statusmsg);
    }

    //This command requires chat op or higher. Times out a user for a given amount minutes.    
    public void globalTimeout(String character,String time,String reason) throws JSONException {
         sendCommand("TMO","character",character,"time",time,"reason",reason);
    }

    //"user x is typing/stopped typing/has entered text" for private messages. 
    public void setTypingStatus(String character,String status) throws JSONException {
         sendCommand("TPN","character",character,"status",status);
    }
        
    //This command requires chat op or higher. Unbans a character's account from the server.
    public void globalUnban(String character) throws JSONException {
         sendCommand("UBN","character",character);
    }
    
    //Requests info about how long the server has been running, and some stats about usage.
    public void getServerInfo() {
        send("UPT");
    }
}
