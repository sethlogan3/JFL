package jfl.components;

import static jfl.components.FChat.*;
import static jfl.components.channel.Channel.*;
import jfl.util.*;
import jfl.components.channel.*;
import jfl.fields.CharacterFields;
import java.net.*;
import java.lang.reflect.Method;
import java.util.*;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.*;

public abstract class FClient { 
    public static class TypingStatus{
        public static String 
            typing="typing",
            clear="clear",
            paused="paused";
    }
    
    public static final String CLIENT_DEFAULT="JFL FClient";
    public static final String VERSION_DEFAULT="1.0";
    public static final String JFL_CREATOR="Slogan";
    
    public Logger logger=new Logger();
    private String client,version,ticket,user;
    private WebSocketClient websocket;
    private String clientCharacter;
    private HashMap<String,HashMap<String,String>> tempCustomKinks,tempProfileTags=new HashMap();
 
    public int chatMax,privMax,lfrpMax,lfrpFlood;
    public double msgFlood;
    public String permissions;
    
    public FChat fchat=new FChat();
    
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
        m.invoke(this,param);    
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

    public void send(String message) {
        System.out.println(">> "+message);
        websocket.send(message);
    }

    private void send(String command,String... args) {
        JSONObject info=new JSONObject(); 
             
        for (int i=0; i<args.length;i+=2) 
            info.put(args[i],args[i+1]);

        send(command+" "+info.toString());
    }

    public void chatopsRecieved(ArrayList<String> opNames) {}
    private void gotADL(JSONObject param) {
        JSONArray opNames=param.getJSONArray("ops");
        chatopsRecieved(JSONUtil.jsonToArrayList(opNames));
    }
    
    public void characterPromotedToChatOp(String opName) {} 
    private void gotAOP(JSONObject param) {
        characterPromotedToChatOp(param.getString("character"));
    }

    public void broadcastRecieved(String message) {}
    private void gotBRO(JSONObject param) {
        broadcastRecieved(param.getString("message"));
    }
    
    public void channelDescriptionChanged(String channelName,String description) {}
    private void gotCDS(JSONObject param) {
        String channelName=param.getString("channel");
        String description=param.getString("description");
        
        Channel channel=fchat.getChannel(channelName);
        if (channel!=null) channel.setDescription(description);
        
        channelDescriptionChanged(channelName,description);
    }
    

    public void publicChannelsRecieved(ArrayList<ChannelSnapshot> channels) {}
    private void gotCHA(JSONObject param) {
        publicChannelsRecieved(getChannelSnapshotData(param,"mode"));
    }
    
    public void privateChannelsRecieved(ArrayList<ChannelSnapshot> channels) {}
    private void gotORS(JSONObject param) {
        publicChannelsRecieved(getChannelSnapshotData(param,"title"));
    }

    private ArrayList<ChannelSnapshot> getChannelSnapshotData(JSONObject param,String data) {
        ArrayList<ChannelSnapshot > returnSnapshots=new ArrayList();
        JSONArray channelInfo=param.getJSONArray("channels");
        ChannelSnapshot channelSnapshot;      
        
        for (int i=0; i<channelInfo.length(); i++) {
            JSONObject object=channelInfo.getJSONObject(i);
            channelSnapshot=new PublicSnapshot(
                object.getString("name"),
                object.getString(data),
                object.getInt("characters")
            );
            returnSnapshots.add(channelSnapshot);
        }    
        
        return returnSnapshots;
    }
    
    public void inviteRecieved(String senderName,String channelName,String title) {}
    private void gotCIU(JSONObject param) {
        inviteRecieved(
            param.getString("sender"),
            param.getString("name"),
            param.getString("title")
        );
    }
    
    public void characterBanned(String operatorName,String channelName,String characterName) {}
    private void gotCBU(JSONObject param) {
        characterBanned(
            param.getString("operator"),
            param.getString("channel"),
            param.getString("character")
        );
    }
 
    public void characterKicked(String operatorName,String channelName,String characterName) {}
    private void gotCKU(JSONObject param) {
        characterKicked(
            param.getString("operator"),
            param.getString("channel"),
            param.getString("character")
        );
    }
    
    public void characterPromotedToChanop(String channelName,String characterName) {}
    private void gotCOA(JSONObject param) {
        String characterName=param.getString("character");
        String channelName=param.getString("channel");
        
        Channel channel=fchat.getChannel(channelName);
        if (channel!=null) channel.addOp(characterName);
        
        characterPromotedToChanop(characterName,channelName);
    }
 
    public void channelOpsRecieved(String channelName,ArrayList<String> opNames) {}
    private void gotCOL(JSONObject param) { 
        String channelName=param.getString("channel");
        JSONArray ops=param.getJSONArray("oplist");
        
        ArrayList<String> opNames=JSONUtil.jsonToArrayList(ops);
        
        Channel channel=fchat.getChannel(channelName);
        if (channel!=null) channel.setOps(opNames);

        channelOpsRecieved(channelName,opNames);
    }
 
    public void serverConnectionsRecieved(int connections) {}
    private void gotCON(JSONObject param) {
        int connections=Integer.valueOf(param.getString("count"));
        fchat.serverConnections=connections;
        serverConnectionsRecieved(connections);
    }
    
    public void chanopRemoved(String characterName,String channelName) {}
    private void gotCOR(JSONObject param) {
        String characterName=param.getString("character");
        String channelName=param.getString("channel");
        
        Channel channel=fchat.getChannel(channelName);
        if (channel!=null) channel.removeOp(characterName);
        chanopRemoved(characterName,channelName);
    }
    
    public void channelOwnerChanged(String characterName,String channelName) {}
    private void gotCSO(JSONObject param) {
        String characterName=param.getString("character");
        String channelName=param.getString("chanel");
        
        Channel channel=fchat.getChannel(channelName);
        if (channel!=null) channel.setOwner(characterName);
        channelOwnerChanged(characterName,channelName);
    }  
    
    public void characterTemporarilyBanned(String operatorName,String channelName,String characterName, int time) {} 
    private void gotCTU(JSONObject param) {
        characterTemporarilyBanned(
            param.getString("operator"),
            param.getString("channel"),
            param.getString("character"),
            param.getInt("length")
        );
    }
        
    public void chatopRemoved(String character) throws Exception {}
    private void gotDOP(JSONObject param) throws Exception {
        chatopRemoved(param.getString("character"));
    }
    
    public void errorRecieved(String error, int number) {}
    private void gotERR(JSONObject param) {
        errorRecieved(
            param.getString("message"),
            param.getInt("number")
        );
    }
    
    public void searchResultsRecieved(ArrayList<String> characterNames,ArrayList<Kink> kinks) {}
    private void gotFKS(JSONObject param) {
        ArrayList<Kink>resultKinks=new ArrayList();
        
        ArrayList<String> characterNames=JSONUtil.jsonToArrayList(param.getJSONArray("characters"));
        ArrayList<String> kinkIDs=JSONUtil.jsonToArrayList(param.getJSONArray("kinks"));

        for (String kinkID:kinkIDs)
            resultKinks.add(Kink.getKinkByID(Integer.valueOf(kinkID)));

        searchResultsRecieved(characterNames,resultKinks);
    }
    
    public void characterDisconnected(String characterName) throws Exception {}
    private void gotFLN(JSONObject param) throws Exception {
        String characterName=param.getString("character");
        Character character=fchat.getCharacter(characterName);
        if (character!=null) fchat.removeCharacter(characterName);
        
        characterDisconnected(characterName);
    }
    
    public void helloRecieved(String message) {}
    private void gotHLO(JSONObject param) {
        helloRecieved(param.getString("message"));
    }
    
    public void channelDataRecieved(String channel,ArrayList<String> occupants,String mode) {}
    private void gotICH(JSONObject param) throws Exception {
        String channelName=param.getString("channel");
        String mode=param.getString("mode");
        
        JSONArray characterArray=param.getJSONArray("users");  
        ArrayList<String> occupantNames=new ArrayList();
        for (int i=0; i<characterArray.length();i++) 
            occupantNames.add(characterArray.getJSONObject(i).getString("identity"));

        Channel channel=fchat.getChannel(channelName); 
        if (channel!=null) {
            channel.clearOccupants();
            channel.setMode(mode);        

            for (String occupantName:occupantNames) {
                Character occupant=fchat.getCharacter(occupantName);
                if (occupant!=null) channel.addOccupant(occupant); 
            }
        }
        
        channelDataRecieved(channelName,occupantNames,mode);
    }
    
    public void onLogin() throws Exception {}
    public void onLogin(String characterName) throws Exception {}
    private void gotIDN(JSONObject param) throws Exception {
        onLogin(param.getString("character"));
        onLogin();
    }

    public void characterJoinedChannel(String characterName,String channelName) {}
    private void gotJCH(JSONObject param) throws Exception {
        String characterName=param.getJSONObject("character").getString("identity");
        String channelName=param.getString("channel");
        String title=param.getString("title");

        if (characterName.equals(clientCharacter)) {
            Channel channel=fchat.getChannel(channelName);
            
            if (channel==null)  {
                if (channelName.substring(0,3).equals("ADH"))
                    fchat.addChannel(new Channel(channelName,title,ChannelType.PRIVATE));   
                else
                    fchat.addChannel(new Channel(channelName,title,ChannelType.PUBLIC));
            }
        } 
        
        characterJoinedChannel(characterName,channelName);
    }

    public void customKinksRecieved(String characterName, HashMap<String,String> customKinks) throws Exception {}
    private void gotKID(JSONObject param) throws Exception {
        String characterName=param.getString("character");
        
        switch (param.getString("type")) {
            case "start":
                tempCustomKinks.put(characterName,new HashMap());
                break;
            case "end":
                Character character=fchat.getCharacter(characterName);     
                HashMap<String,String> customKinks=tempCustomKinks.get(characterName);
                if (character!=null) character.setCustomKinks(customKinks);
                
                customKinksRecieved(characterName,customKinks);  
                break;
            default:
                HashMap<String,String> keyVal=tempCustomKinks.get(characterName);
                keyVal.put(
                    param.getString("key"),
                    param.getString("value")
                );

                tempCustomKinks.put(characterName,keyVal);
        }
    }
    
    public void characterLeftChannel(String channelName,String characterName) {}
    private void gotLCH(JSONObject param) throws Exception {
        String characterName=param.getString("character");
        String channelName=param.getString("channel");
        
        Channel channel=fchat.getChannel(channelName);
        if (channel!=null) {
            if (characterName.equals(clientCharacter)) 
                fchat.removeChannel(channelName);
            else
                channel.removeOccupant(characterName);
        }
        
        characterLeftChannel(channelName,characterName);    
    }
    
    public void characterListRecieved(ArrayList<Character> character) throws Exception {}
    private void gotLIS(JSONObject param) throws Exception {
        ArrayList<Character> returnCharacters=new ArrayList();
        JSONArray characterInfo=param.getJSONArray("characters");
        
        for (int i=0; i<characterInfo.length(); i++) {
            JSONArray object=characterInfo.getJSONArray(i);
            String characterName=object.getString(0),
                   gender=object.getString(1),
                   status=object.getString(2),
                   statusMessage=object.getString(3);
            
            Character character=fchat.getCharacter(characterName);
            if (character==null) {
                character=new Character(characterName);
                fchat.addCharacter(character);
            }
            
            character.setGender(gender);
            character.setStatus(status);
            character.setStatusMessage(statusMessage);
            returnCharacters.add(character);
        }
        
        characterListRecieved(returnCharacters);
    }
    
    public void characterConnected(Character character) {}
    public void characterConnected(String characterName) {}
    private void gotNLN(JSONObject param) throws Exception {
        String characterName=param.getString("identity");     

        Character character=fchat.getCharacter(characterName);
        if (character==null) {
            character=new Character(
                characterName,
                param.getString("gender"),
                param.getString("status")
            );
            fchat.addCharacter(character);
        }

        characterConnected(character);
        characterConnected(characterName);
    }

    public void channelMessageRecieved(String characterName, String message, String channelName) throws Exception {}
    private void gotMSG(JSONObject param) throws Exception {
        String characterName=param.getString("character");
        String channelName=param.getString("channel");
        String message=param.getString("message");
        logger.logChannelMessage(channelName,new Message(characterName,message));
        
        channelMessageRecieved(characterName,message,channelName);
    }
    
    public void privateMessageRecieved(String characterName, String message) {}
    private void gotPRI(JSONObject param) {
        String characterName=param.getString("character");
        String message=param.getString("message");
        logger.logChannelMessage(characterName,new Message(characterName,message));
        
        privateMessageRecieved(characterName,message);
    }
      
    public void profileTagsRecieved(HashMap<String,String> profileTags) {}
    private void gotPRD(JSONObject param) throws Exception {
        String characterName=param.getString("character");
        
        switch (param.getString("type")) {
            case "start":
                tempProfileTags.put(characterName,new HashMap());
                break;
            case "end":   
                HashMap<String,String> tags=tempProfileTags.get(characterName);                
                profileTagsRecieved(tags);  
                break;
            default:

                HashMap<String,String> keyVal=tempProfileTags.get(characterName);
                String key=param.getString("key");
                String value=param.getString("value");
                
                Character character=fchat.getCharacter(characterName);
                if (character!=null) {       
                    if (CharacterFields.isSexualDetail(key)) 
                        character.setSexualDetail(key,value);
                    else if (CharacterFields.isGeneralDetail(key))
                        character.setGeneralDetail(key,value);
                    else if (CharacterFields.isContactDetail(key))
                        character.setContactDetail(key,value);
                    else if (CharacterFields.isRPingPreference(key))
                        character.setRPingPreference(key,value);
                }
                                
                keyVal.put(key,value);
                tempProfileTags.put(characterName,keyVal);
        }
    }
    
    public void onPing() {}
    private void gotPIN(JSONObject unused) {
        ping();
        onPing();
    }
    
    public void adRecieved(String characterName,String message,String channelName) {}
    private void gotLRP(JSONObject param) {
        String characterName=param.getString("character");
        String channelName=param.getString("channel");
        String message=param.getString("message");
        logger.logChannelMessage(channelName,new Message(characterName,message,true));
        
        adRecieved(characterName,message,channelName);        
    }
    
    public void bottleSpun(String targetCharacter,String channelName,String message,String characterName) {}    
    public void diceRolled(int[] results,String channelName,ArrayList<String> rolls,String characterName,int endResult,String message) {}
    private void gotRLL(JSONObject param) {
        String channelName=param.getString("channel"),
               characterName=param.getString("character"),
               message=param.getString("message");
        
        switch(param.getString("type")) {
            case "dice" :
                int[] results=JSONUtil.jsonToIntArray(param.getJSONArray("results"));
                ArrayList<String> rolls=JSONUtil.jsonToArrayList(param.getJSONArray("rolls"));
                
                diceRolled(results,channelName,rolls,characterName,param.getInt("endresult"),message);
                break;
            case "bottle":
                bottleSpun(param.getString("target"),channelName,message,characterName);
        }
    }
    
    public void roomModeChanged(String channelName,String mode) {} 
    private void gotRMO(JSONObject param) {
        String channelName=param.getString("channel");
        String mode=param.getString("mode");
        
        Channel channel=fchat.getChannel(channelName);
        if (channel!=null) channel.setMode(mode);
        
        roomModeChanged(channelName,mode);
    }

    public void noteRecieved(String characterName, String message) {}
    private void gotRTB(JSONObject param) {
        //To be implemented
    }

    //Will be updated/expanded
    public void alertRecieved(JSONObject param) {}
    public void alertClaimed(JSONObject param) {}
    private void gotSFC(JSONObject param) {
        switch(param.getString("type")) {
            case "report":
                alertRecieved(param);
                break;
            default:
                alertClaimed(param);
        }
    }
    
    public void statusChanged(String status,String characterName,String statusMessage) {}
    public void gotSTA(JSONObject param) {
        String status=param.getString("status"),
               characterName=param.getString("character"),
               statusMessage=param.getString("statusmsg");
        Character character=fchat.getCharacter(characterName);
        
        if (character!=null) {
            character.setStatus(status);
            character.setStatusMessage(statusMessage);
        }
        
        statusChanged(status,characterName,statusMessage);
    }

    public void systemMessageRecieved(String channelName,String message) {}
    public void characterUnbanned(String channelName,String characterName) {}
    public void bannedCharactersRecieved(String channelName,ArrayList<String> characterNames) {}
    public void channelStatusChanged(String channelName,String newStatus) {}
    public void invitationSent() {}
    public void gotSYS(JSONObject param) {
        String message=param.getString("message");
        String channelName="";
        
        if (param.has("channel")) channelName=param.getString("channel");
        int index=message.indexOf(" has been removed from the channel ban list.");
        
        if (index>-1) 
            characterUnbanned(message.substring(0,index),channelName);
        else if (message.startsWith("Channel moderators")) 
            channelOpsRecieved(channelName,tokenizeSystemMessage(message));
        else if (message.startsWith("Channel bans")) 
            bannedCharactersRecieved(channelName,tokenizeSystemMessage(message));
        else if (message.startsWith("This channel is now")) {
            if (message.contains("closed"))
                channelStatusChanged(channelName,ChannelStatus.PUBLIC);
            else
                channelStatusChanged(channelName,ChannelStatus.PRIVATE);
        }
        else if (message.equals("Your invitation has been sent.")) 
            invitationSent();

        systemMessageRecieved(message,channelName);
    }
    
    private ArrayList<String> tokenizeSystemMessage(String message) {
        ArrayList<String> items=new ArrayList();
        String modString=message.substring(message.indexOf(":")+2);
        StringTokenizer tokenizer = new StringTokenizer(modString,", ");

        while (tokenizer.hasMoreTokens()) 
            items.add(tokenizer.nextToken());  
        
        return items;
    }
    
    public void typingStatusRecieved(String characterName, String typingStatus) {}
    private void gotTPN(JSONObject param) {
        typingStatusRecieved(
            param.getString("character"),
            param.getString("status")
        );
    }
    
    public void serverStatsRecieved(int time,int startTime,String startString,int connectionsAccepted,
            int channelCount,int userCount,int maxUsers) {}
    private void gotUPT(JSONObject param) {
        serverStatsRecieved(
            param.getInt("time"),
            param.getInt("starttime"),
            param.getString("startstring"),
            param.getInt("accepted"),
            param.getInt("channels"),
            param.getInt("users"),
            param.getInt("maxusers")
        );
    }
    
    public void chatMaxRecieved(int chatMax) {}
    public void privMaxRecieved(int privMax) {}
    public void lfrpMaxRecieved(int lfrpMax) {}
    public void lfrpFloodRecieved(int lfrpFlood) {}
    public void msgFloodRecieved(double msgFlood) {}
    public void permissionsRecieved(String permissions) {}
    public void iconBlacklistRecieved(ArrayList<String> channels) {}
    public void variableRecieved(String variableName,Object value) {}
    public void gotVAR(JSONObject param) {
        String variableName=param.getString("variable");
        Object valueObj=param.get("value");
        
        switch (variableName) {
            case "chat_max": 
                chatMax=param.getInt("value");
                chatMaxRecieved(chatMax); 
                break;
            case "priv_max":
                privMax=param.getInt("value");
                privMaxRecieved(privMax); 
                break;
            case "lfrp_max":
                lfrpMax=param.getInt("value"); 
                lfrpMaxRecieved(lfrpMax);
                break;
            case "lfrp_flood":
                lfrpFlood=param.getInt("value");
                lfrpFloodRecieved(lfrpFlood);
                break;
            case "msg_flood":
                msgFlood=param.getDouble("value");
                msgFloodRecieved(msgFlood);
                break;
            case "permissions":
                permissions=param.getString("value");
                permissionsRecieved(permissions);
                break;
            case "icon_blacklist":
                ArrayList<String> blacklist=JSONUtil.jsonToArrayList(param.getJSONArray("value"));
                iconBlacklistRecieved(blacklist);   
        }

        variableRecieved(variableName,valueObj);
    }
    //This command requires chat op or higher. Request a character's account be banned from the server.
    public void globalBan(String character) {
        send("ACB","character",character);
    }

    //This command is admin only. Promotes a user to be a chatop (global moderator).
    public void promoteToChatop(String character) {
        send("AOP","character",character);
    }
        
    //This command requires chat op or higher. Requests a list of currently connected alts for a characters account.
    public void requestConnectedAlts(String character) {
        send("AWC","character",character);
    }
        
    //This command is admin only. Broadcasts a message to all connections.
    public void broadcast(String message) {
        send("BRO","message",message);
    }
    
    //This command requires channel op or higher. Request the channel banlist.
    public void requestBanlist(String channel) {
        send("CBL","channel",channel);
    }
    
    //This command requires channel op or higher. Bans a character from a channel.
    public void ban(String character,String channel) {
        send("CBU","character",character,"channel",channel);       
    }
    
    //Create a private, invite-only channel.
    public void createPrivateChannel(String channel) {
        send("CCR","channel",channel);
    }
    
    //This command requires channel op or higher. Changes a channel's description.
    public void changeDescription(String channel,String description) {
        send("CDS","channel",channel,"description",description);
    }
    
    //Request a list of all public channels.
    public void requestPublicChannels(){
        send("CHA");       
    }
    
    //This command requires channel op or higher. Sends an invitation for a channel to a user.
    public void invite(String channel,String character) {
        send("CIU","channel",channel,"character",character);       
    }
    
    //This command requires channel op or higher. Kicks a user from a channel.
    public void kick(String channel,String character) {
        send("CKU","channel",character,"character",channel);       
    }    
    
    //This command requires channel op or higher. Request a character be promoted to channel operator (channel moderator).
    public void promote(String channel,String character) {
        send("COA","channel",character,"character",channel);       
    }    
    
    //Requests the list of channel ops (channel moderators).
    public void requestChannelOps(String channel) {
        send("COL","channel",channel);       
    }
    
    //This command requires channel op or higher. Demotes a channel operator (channel moderator) to a normal user.
    public void demote(String channel,String character) {
        send("COR","channel",character,"character",channel);       
    }    
    
    //This command is admin only. Creates an official channel.
    public void createPublicChannel(String channel) {
        send("CRC","channel",channel);
    }
 
    //This command requires channel op or higher. Set a new channel owner.
    public void setOwner(String channel,String character) {
        send("CSO","character",character,"channel",channel);       
    }
    
    //This command requires channel op or higher. Temporarily bans a user from the channel for 1-90 minutes. A channel timeout.
    public void timeout(String channel,String character,String length) {
        send("CTU","channel",character,"character",channel,"length",length);       
    }
    
    //This command requires channel op or higher. Unbans a user from a channel.
    public void unban(String channel,String character) {
        send("CUB","channel",channel,"character",character);       
    } 
    
    //This command is admin only. Demotes a chatop (global moderator).
    public void globalDemote(String character) {
        send("DOP","character",character);
    }
      
    public void searchCharacters(ArrayList kinks,ArrayList<String> genders,ArrayList<String> orientations,
                                 ArrayList<String> languages,ArrayList<String> furryprefs,ArrayList<String> roles) throws FListException {
        
        if (kinks==null || kinks.isEmpty()) 
            throw new FListException("Kinks field must not be null or empty");
        else {     
            JSONObject obj=new JSONObject();
            String className=kinks.get(0).getClass().getName();
            ArrayList<String> kinkAsString=new ArrayList();
            
            switch (className) {
                case "java.lang.String":
                    kinkAsString=kinks;
                    break;
                case "java.lang.Integer":
                    for (Integer integer:(ArrayList<Integer>)kinks) 
                        kinkAsString.add(String.valueOf(integer));
                    break;
                case "jfl.Kink":
                    for (Kink kink:(ArrayList<Kink>)kinks) 
                        kinkAsString.add(kink.getIDString());                   
            }

            obj.put("kinks",JSONUtil.arrayListToJSON(kinkAsString));
                                
            if (genders!=null && !genders.isEmpty()) 
                obj.put("genders",JSONUtil.arrayListToJSON(genders));

            if (orientations!=null && !orientations.isEmpty()) 
                obj.put("orientations",JSONUtil.arrayListToJSON(orientations));

            if (languages!=null && !languages.isEmpty()) 
                obj.put("languages",JSONUtil.arrayListToJSON(languages));

            if (furryprefs!=null && !furryprefs.isEmpty()) 
                obj.put("furryprefs",JSONUtil.arrayListToJSON(furryprefs));

            if (roles!=null && !roles.isEmpty()) 
                obj.put("roles",JSONUtil.arrayListToJSON(roles));

            send("FKS "+obj);
        }
    }
    
    
    //This command is used to identify with the server.
    public void identify(String account,String character,String ticket, String client, String cversion, String method) throws Exception {
        send("IDN","account",account,"character",character,"ticket",ticket,"client",client,"cversion",cversion,"method",method);
    }

    //A multi-faceted command to handle actions related to the ignore list. The server does not actually handle much of the ignore process, as it is the client's responsibility to block out messages it recieves from the server if that character is on the user's ignore list.
    public void handleIgnore(String action, String character) {
        send("IGN","action",action,"character",character);
    }

    //Send a channel join request.
    public void joinChannel(String channelName) {
        send("JCH","channel",channelName);
    }

    public void joinChannel(Channel channel) {
        send("JCH","channel",channel.getName());
    }
    
    //This command requires chat op or higher. Request a character be kicked from the server.
    public void kickFromServer(String character) {
        send("KIK","character",character);
    }
    
    //Request a list of a user's kinks.
    public void requestCustomKinks(String character) {
        send("KIN","character",character);
    }
    
    //Request to leave a channel.
    public void leaveChannel(String channel) {
        send("LCH","channet",channel);
    }
    
    //UPDATE
    public void sendAd(String channel,String message) {
        send("LRP","channel",channel,"message",message);
    }
    
    //Sends a message to all other users in a channel.
    public void sendMessage(String channel,String message) {
        send("MSG","channel",channel,"message",message);
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
    public void sendPrivateMessage(String recipient,String message) {  
        send("PRI","recipient",recipient,"message",message);
    }
        
    //Requests some of the profile tags on a character, such as Top/Bottom position and Language Preference.
    public void getProfileTags(String character) {
        send("PRO","character",character);
    }
    
    //Roll dice or spin the bottle.
    public void roll(String channel,String dice) {
        send("RLL","channel",channel,"dice",dice);
    }
    
    //This command requires chat op or higher. Reload certain server config files
    public void reloadConfig(String save) {
         send("RLD","save",save);
    }
    
    //This command requires channel op or higher. Change room mode to accept chat, ads, or both.
    public void setRoomChatMode(String channel,String mode) {
         send("RMO","channel",channel,"mode",mode);
    }
    
    //This command requires channel op or higher. Sets a private room's status to closed or open.
    public void setRoomStatus(String channel,String status) {
         send("RST","channel",channel,"status",status);
    }  
    
    //This command is admin only. Rewards a user, setting their status to 'crown' until they change it or log out.
    public void reward(String character) {
         send("RWD","character",character);
    }  
    
    //Alerts admins and chatops (global moderators) of an issue.
    public void alertModerators(String action,String report,String character) throws JSONException {
         send("SFC","action",action,"report",report,"character",character);
    } 
    
    //Request a new status be set for your character.
    public void setStatus(String status,String statusmsg) throws JSONException {
         send("STA","status",status,"statusmsg",statusmsg);
    }

    //This command requires chat op or higher. Times out a user for a given amount minutes.    
    public void globalTimeout(String character,String time,String reason) throws JSONException {
         send("TMO","character",character,"time",time,"reason",reason);
    }

    //"user x is typing/stopped typing/has entered text" for private messages. 
    public void setTypingStatus(String character,String status) throws JSONException {
         send("TPN","character",character,"status",status);
    }
        
    //This command requires chat op or higher. Unbans a character's account from the server.
    public void globalUnban(String character) throws JSONException {
         send("UBN","character",character);
    }
    
    //Requests info about how long the server has been running, and some stats about usage.
    public void getServerInfo() {
        send("UPT");
    }
}
