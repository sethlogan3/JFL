package jfl;

import java.net.*;
import java.lang.reflect.Method;
import java.util.*;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.*;
import static jfl.Character.*;
import static jfl.Channel.*;

public abstract class FClient{
    public enum Server{PUBLIC,TEST};
    
    public final String CLIENT_DEFAULT="FClient";
    public final String VERSION_DEFAULT="1.0";
    public final String TEST_URL="ws://chat.f-list.net:8722";
    public final String PUBLIC_URL="ws://chat.f-list.net:9722";
    
    private String client,version;
    private static String ticket,user;
    private WebSocketClient websocket;
    private String clientCharacter;
    
    private int serverConnections;
    
    //Generate new FClient
    public FClient(String clientName,String clientVersion) throws Exception {
        client=clientName;
        version=clientVersion;
        Kink.updateKinksList();
    }

    public FClient() throws Exception {
        client=CLIENT_DEFAULT;
        version=VERSION_DEFAULT;
        Kink.updateKinksList();
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
    
    public void login(String username,String character,String password,Server server) throws Exception {
        user=username;
        clientCharacter=character;
        ticket=EndpointUtil.getTicket(username,password);
        
        if (server==Server.PUBLIC)
            websocket=generateWebsocket(PUBLIC_URL);
        else
            websocket=generateWebsocket(TEST_URL);
        
        websocket.connect();
    }

    public void login(String username,String character,String password) throws Exception {
        login(username,character,password,Server.PUBLIC);
    }
    
    public void setClientName(String name) {
        client=name;
    }

    public void setClientVersion(String versionName) {
        version=versionName;
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

    public void chatopsRecieved(ArrayList<String> ops) {}
    private void gotADL(JSONObject param) {
        JSONArray ops=param.getJSONArray("ops");
        chatopsRecieved(FUtil.jsonToArrayList(ops));
    }
    
    public void characterPromotedToChatOp(Character character) {} 
    private void gotAOP(JSONObject param) throws Exception {
        String name=param.getString("character");
        characterPromotedToChatOp(getCharacter(name));
    }

    public void broadcastRecieved(String message) {}
    private void gotBRO(JSONObject param) {
        broadcastRecieved(param.getString("message"));
    }
    
    public void channelDescriptionChanged(Channel channel,String description) {}
    private void gotCDS(JSONObject param) {
        Channel channel=getChannel(param.getString("channel"));
        String description=param.getString("description");
        channel.setDescription(description);
        channelDescriptionChanged(channel,description);
    }
    
    public void publicChannelsRecieved(ArrayList<Channel> channels) {}
    private void gotCHA(JSONObject param) {
        ArrayList<Channel> returnChannels=new ArrayList();
        JSONArray channelsInfo=param.getJSONArray("channels");
        
        for (int i=0; i<channelsInfo.length(); i++) {
            JSONObject current=channelsInfo.getJSONObject(i);
            String name=current.getString("name");
            String mode=current.getString("mode");
            int numberOfOccupants=current.getInt("characters");
            
            Channel channel=getChannel(name);
            
            if (channel==null) {
                channel=new Channel(name,name,mode,numberOfOccupants);
                getChannels().add(channel); 
            }
            else {
                channel.setMode(mode);
                channel.setNumberOfOccupants(numberOfOccupants);
            }
            
            returnChannels.add(channel);
        }  
        
        publicChannelsRecieved(returnChannels);
    }
    
    public void inviteRecieved(Character sender,Channel channel) throws Exception {}
    private void gotCIU(JSONObject param) throws Exception {
        String senderName=param.getString("sender");
        String channelName=param.getString("name");
        String title=param.getString("title");
        Channel channel=getChannel(channelName);
        
        if (channel==null) {
            channel=new Channel(channelName,title);
            getChannels().add(channel);
        }
        else
            channel.setTitle(title);
        
        inviteRecieved(getCharacter(senderName),channel);
    }
    
    public void characterBanned(Character operator,Channel channel,Character bannedCharacter) throws Exception{}
    private void gotCBU(JSONObject param) throws Exception{
        Character operator=getCharacter(param.getString("operator"));
        Character bannedCharacter=getCharacter(param.getString("character"));
        Channel channel=getChannel(param.getString("channel"));
        
        characterBanned(operator,channel,bannedCharacter);
    }
 
    public void characterKicked(Character operator,Channel channel,Character kickedCharacter) throws Exception{}
    private void gotCKU(JSONObject param) throws Exception{
        Character operator=getCharacter(param.getString("operator"));
        Character kickedCharacter=getCharacter(param.getString("character"));
        Channel channel=getChannel(param.getString("channel"));
        
        characterKicked(operator,channel,kickedCharacter);
    }
    
    public void characterPromotedToChanop(Character chanop, Channel channel) throws Exception {}
    private void gotCOA(JSONObject param) throws Exception {
        Character chanop=getCharacter(param.getString("character"));
        Channel channel=getChannel(param.getString("channel"));
        channel.addOp(chanop);
        characterPromotedToChanop(chanop,channel);
    }
 
    public void channelOpsRecieved(Channel channel,ArrayList<Character> opsList) throws Exception{}
    private void gotCOL(JSONObject param) throws Exception {
        ArrayList<Character> opsList=new ArrayList();
        Channel channel=getChannel(param.getString("channel"));
        JSONArray ops=param.getJSONArray("oplist");

        for (int i=0; i<ops.length(); i++) {
           Character op=getCharacter(ops.getString(i)); 
           opsList.add(op);
        }
        
        channel.setOps(opsList);
        channel.setOwner(opsList.get(0));
        channelOpsRecieved(channel,opsList);
    }
 
    public void connectionCountRecieved(int connections) {}
    private void gotCON(JSONObject param) {
        int connections=Integer.valueOf(param.getString("count"));
        connectionCountRecieved(connections);
    }
    
    public void chanopDemoted(Character demotedCharacter,Channel channel) throws Exception {}
    private void gotCOR(JSONObject param) throws Exception {
        Character demotedCharacter=getCharacter(param.getString("character"));
        Channel channel=getChannel(param.getString("channel"));
        channel.removeOp(demotedCharacter);
        characterPromotedToChanop(demotedCharacter,channel);
    }
    
    public void channelOwnerChanged() throws Exception {}
    private void gotCSO(JSONObject param) throws Exception {
        Character newOwner=getCharacter(param.getString("character"));
        Channel channel=getChannel(param.getString("channel"));
        channel.setOwner(newOwner);
        characterPromotedToChanop(newOwner,channel);
    }  
    
    public void characterTemporarilyBanned(Character operator,Channel channel,Character bannedCharacter, int time) throws Exception{} 
    private void gotCTU(JSONObject param) throws Exception {
        Character operator=getCharacter(param.getString("operator"));
        Character bannedCharacter=getCharacter(param.getString("character"));
        Channel channel=getChannel(param.getString("channel"));
        characterTemporarilyBanned(operator,channel,bannedCharacter,param.getInt("length"));
    }
        
    public void chatopDemoted(Character character) throws Exception {}
    private void gotDOP(JSONObject param) throws Exception {
        Character demotedCharacter=getCharacter(param.getString("character"));
        chatopDemoted(demotedCharacter);
    }
    
    public void errorRecieved(String error, int number) {}
    private void gotERR(JSONObject param) {
        errorRecieved(param.getString("message"),param.getInt("number"));
    }
    
    public void helloRecieved(String message) {}
    private void gotHLO(JSONObject param) {
        helloRecieved(param.getString("message"));
    }
    
    public void channelDataRecieved(Channel channel,ArrayList<Character> occupants,String mode) {}
    private void gotICH(JSONObject param) throws Exception {
        ArrayList<Character> occupants=new ArrayList();

        Channel channel=getChannel(param.getString("channel")); 
        channel.clearOccupants();
        
        String mode=param.getString("mode");
        channel.setMode(mode);        

        JSONArray characterArray=param.getJSONArray("users");
        
        for (int i=0; i<characterArray.length();i++) {
            String name=((JSONObject)characterArray.get(i)).getString("identity");
            Character character=getCharacter(name);
            channel.addOccupant(character); 
        }

        channelDataRecieved(channel,occupants,mode);
    }
    
    public void onLogin() throws Exception{}
    private void gotIDN(JSONObject unused) throws Exception {
        onLogin();
    }

    public void characterJoinedChannel(Character character,Channel channel) {}
    private void gotJCH(JSONObject param) throws Exception {
        String characterName=param.getJSONObject("character").getString("identity");
        Character character=getCharacter(characterName);
        
        String channelName=param.getString("channel");
        Channel channel;

        if (characterName.equals(clientCharacter)) {
            getChannels().add(new Channel(channelName,param.getString("title")));
            channel=getChannel(channelName);
            
            if (channelName.substring(0,3).equals("ADH"))
                channel.setType("private");
            else
                channel.setType("public");
        } 
        else
            channel=getChannel(channelName);

        channel.addOccupant(character);
        characterJoinedChannel(character,channel);
    }

    public void characterLeftChannel(Channel channel,Character character) {}
    private void gotLCH(JSONObject param) throws Exception {
        String characterName=param.getString("character");
        Character character=getCharacter(characterName);
        
        String channelName=param.getString("channel");
        Channel channel=getChannel(channelName);
        
        characterLeftChannel(channel,character);    
        
        if (characterName.equals(clientCharacter)) 
            removeChannel(channelName);
        else
            channel.removeOccupant(character);
    }
    
    public void characterConnected(Character character) {}
    private void gotNLN(JSONObject param) throws Exception {        
        Character character=getCharacter(param.getString("identity"));
        character.setGender(param.getString("gender").toLowerCase());
        character.setStatus(param.getString("status"));       
        character.setOnline();   
        
        serverConnections++;
        characterConnected(character);
    }

    public void channelMessageRecieved(Character sender, String message, Channel channel) throws Exception {}
    private void gotMSG(JSONObject param) throws Exception {
        Character sender=getCharacter(param.getString("character"));
        Channel channel=getChannel(param.getString("channel"));
        String message=param.getString("message");
        
        channel.addMessage(new Message(sender,message));
        channelMessageRecieved(sender,message,channel);
    }
    
    public void privateMessageRecieved(Character sender, String message) throws Exception {}
    private void gotPRI(JSONObject param) throws Exception {
        Character sender=getCharacter(param.getString("character"));
        String message=param.getString("message");
               
        sender.addMessage(new Message(sender,message));
        privateMessageRecieved(sender,message);
    }
        
    public void onPing() {}
    private void gotPIN(JSONObject unused) throws JSONException {
        ping();
        onPing();
    }
    

    public void roomModeChanged(Channel channel,String mode) {} 
    private void gotRMO(JSONObject param) throws JSONException {
        Channel channel=getChannel(param.getString("channel"));
        String mode=param.getString("mode");
        channel.setMode(mode);
        roomModeChanged(channel,mode);
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
    public void searchCharacters(Search search) throws Exception {
        send("FKS "+search);
    }

    public void searchCharacters(Integer... kinks) throws Exception {
        send("FKS "+new Search(kinks));
    }

    public void searchCharacters(String... kinks) throws Exception {
        send("FKS "+new Search(kinks));
    }
        
    public void searchCharacters(Kink... kinks) throws Exception {
        send("FKS "+new Search(kinks));
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
    public void joinChannel(String channelName) throws JSONException {
        sendCommand("JCH","channel",channelName);
    }

    public void joinChannel(Channel channel) throws JSONException {
        sendCommand("JCH","channel",channel.getName());
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
    public void sendPrivateMessage(String recipient,String message) throws Exception, FListException {
        if (getCharacter(recipient).isOnline()) {    
            sendCommand("PRI","recipient",recipient,"message",message);
        }else
            throw new FListException("Character offline.");
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
