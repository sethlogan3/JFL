package jfl;

import java.util.*;
import org.json.*;
import jfl.FListException;

public class Character extends Logger{
    private static final ArrayList<Character> characters=new ArrayList<>();
    private String name,status,statusMessage;
    private boolean online,chatop;

    private HashMap<String,String> 
        contactDetails=new HashMap(),
        sexualDetails=new HashMap(),
        generalDetails=new HashMap(),
        rpingPreferences=new HashMap(),
        profileInfo=new HashMap(),
        customKinks=new HashMap();

    private HashMap<Kink,String>kinks=new HashMap();
    
    public Character(String characterName) {
        name=characterName;    
    }

    public String getName() {
        return name;
    }
    
    public void setName(String characterName) {
        name=characterName;
    }
    
    public void setOnline() {
        online=true;
    }

    public void setOffline() {
        online=false;
    }

    public void setOnline(boolean state) {
        online=state;
    }
        
    public boolean isOnline() {
        return online;
    }
    
    public void setChatOp() {
        chatop=true;
    }
 
    public void setChatOp(boolean state) {
        chatop=state;
    }
    
    public boolean isChatOp() {
        return chatop;
    }    
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String characterStatus) {
        status=characterStatus;
    }
    
    public String getStatusMessage() {
        return statusMessage;
    }
    
    public void setStatusMessage(String message) {
        statusMessage=message;
    }
    
    public void setGender(String characterGender) throws Exception {
        generalDetails.put("gender",characterGender);
    }
    
    public HashMap<String,String> getContactDetails() throws Exception {
        if (contactDetails.isEmpty())
            updateCharacterInfo();
        
        return contactDetails;
    }
 
    public HashMap<String,String> getSexualDetails() throws Exception {
        if (sexualDetails.isEmpty())
            updateCharacterInfo();
        
        return sexualDetails;
    }

    public String getSexualDetails(String key) throws Exception {
        return getSexualDetails().get(key);
    }
    
    public HashMap<String,String> getGeneralDetails() throws Exception {
        if (generalDetails.isEmpty())
            updateCharacterInfo();
        
        return generalDetails;
    }
 
    public String getGeneralDetails(String key) throws Exception {
        return getGeneralDetails().get(key);
    }
    
    public HashMap<String,String> getRPingPreferences() throws Exception {
        if (rpingPreferences.isEmpty())
            updateCharacterInfo();
         
        return rpingPreferences;
    }

    public String getRPingPreferences(String key) throws Exception {
        return getRPingPreferences().get(key);
    }
        
    public HashMap<String,String> getProfileInfo() throws Exception {
        if (profileInfo.isEmpty())
            updateProfileInfo();
        
        return profileInfo;
    }

    public String getProfileInfo(String key) throws Exception {
        return getProfileInfo().get(key);
    }
     
    public HashMap<Kink,String> getKinks() throws Exception {
        if (kinks.isEmpty())
            updateKinks(); 

        return kinks;
    }

    public HashMap<Kink,String> getKinks(String category) throws Exception {
        HashMap<Kink,String> returnKinks=new HashMap(),kinkInfo=getKinks();
 
        for (Map.Entry<Kink,String> entry : kinkInfo.entrySet()) {
            Kink key=entry.getKey();

            if (key.getCategory().equals(category)) {
                
                returnKinks.put(key,entry.getValue());
            }
        }
        
        return returnKinks;
    }
        
    public HashMap<String,String> getCustomKinks() throws Exception {
        if (customKinks.isEmpty())
            updateCustomKinks();
        
        return customKinks;
    }
        
    public void updateKinks() throws Exception {
        JSONObject obj=EndpointUtil.characterDataPOST("character-kinks",name).getJSONObject("kinks");
        Iterator keys = obj.keys();

        while(keys.hasNext()) {
            JSONArray items=obj.getJSONObject((String)keys.next()).getJSONArray("items");

            for (int i=0; i<items.length(); i++) {
                JSONObject item=items.getJSONObject(i);
                Kink kink=Kink.getKinkByID(Integer.valueOf(item.getString("id")));
                           
                String choice=item.getString("choice").toLowerCase();
                kinks.put(kink,choice);
            }
        }
    }
    
    public void updateCustomKinks() throws Exception {
        JSONArray array=EndpointUtil.characterDataPOST("character-customkinks",name).getJSONArray("kinks");
        customKinks=parseJSONArray(array,"description");
    }    
    
    public void updateProfileInfo() throws Exception {
        JSONObject param=EndpointUtil.characterDataPOST("character-get",name);
        profileInfo=FUtil.parseJSONObject(param.getJSONObject("character"));
    }
    
    public void updateCharacterInfo() throws Exception {    
        JSONObject param=EndpointUtil.characterDataPOST("character-info",name).getJSONObject("info");
                        
        contactDetails=parseJSONArray(param.getJSONObject("1").getJSONArray("items"),"value");
        sexualDetails=parseJSONArray(param.getJSONObject("2").getJSONArray("items"),"value");
        generalDetails=parseJSONArray(param.getJSONObject("3").getJSONArray("items"),"value");
        rpingPreferences=parseJSONArray(param.getJSONObject("5").getJSONArray("items"),"value");
    }
    
    private static HashMap parseJSONArray(JSONArray array,String value) {
        HashMap hashMap=new HashMap();
        JSONObject obj;

        for (int i=0; i<array.length(); i++) {
            obj=array.getJSONObject(i);
            hashMap.put(obj.getString("name").toLowerCase(),obj.getString(value));
        }
        
        return hashMap;
    }

    public String getKinkChoice(int id) throws Exception {
        return getKinks().get(Kink.getKinkByID(id));
    }
    
    public String getKinkChoice(String name) throws Exception {
        return getKinks().get(Kink.getKinkByName(name));
    }
    
    public static Character getCharacter(String name) throws Exception {               
        Character character=findCharacterInList(name);

        if (character==null) { 
            JSONObject profileInfo=EndpointUtil.characterDataPOST("character-get",name);
            String error=profileInfo.getString("error");

            if (error.equals("Character not found."))
                throw new FListException(error);
            else {
                character=new Character(name);
                character.profileInfo=FUtil.parseJSONObject(profileInfo.getJSONObject("character"));
                characters.add(character);
            }
        }
        
        return character;
    }

    public static boolean characterExists(String name) throws Exception {
        try {
            getCharacter(name);
            return true;
        }
        catch(FListException fe) {
            return false;
        }
    }
    public static Character findCharacterInList(String name) {
        for (Character character:characters) {
            if (character.getName().equals(name))
                return character;
        }
        
        return null;
    }
    
    @Override public String toString() {
        return "character:"+name;
    }
    
    public static class Gender {
        public static final String 
            MALE="Male",
            FEMALE="Female",
            TRANSGENDER="Transgender",
            HERM="Herm",
            SHEMALE="Shemale",
            MALE_HERM="Male-Herm",
            CUNT_BOY="Cunt-boy",
            NONE="None";
    }
    
    public static class Role {
        public static final String 
            ALWAYS_SUBMISSIVE="Always submissive",
            USUALLY_SUBMISSIVE="Usually submissive",
            SWITCH="Switch",
            USUALLY_DOMINANT="Usually dominant",
            ALWAYS_DOMINANT="Always dominant";
    }
    
    public static class Orientation {
        public static final String 
            STRAIGHT="Straight",
            GAY="Gay",
            BISEXUAL="Bisexual",
            ASEXUAL="Asexual",
            UNSURE="Unsure",
            BI_MALE_PREF="Bi - male preference",
            BI_FEMALE_PREF="Bi - female preference",
            PANSEXUAL="pansexual",
            BI_CURIOUS="Bi-curious";
    }
    
    public static class Position {
        public static final String 
            ALWAYS_BOTTOM="Always Bottom",
            USUALLY_BOTTOM="Usually Bottom",
            SWITCH="Switch",
            USUALLY_TOP="Usually Top",
            ALWAYS_TOP="Always Top";
    }
    
    public static class Language {
        public static final String
            ARABIC="Arabic",
            CHINESE="Chinese",
            DUTCH="Dutch",
            ENGLISH="English",
            FRENCH="French",
            GERMAN="German",
            ITALIAN="Italian",
            JAPANESE="Japanese",
            KOREAN="Korean",
            PORTUGUESE="Portuguese",
            RUSSIAN="Russian",
            SPANISH="Spanish",
            SWEDISH="Swedish",
            OTHER="Other";
    }
}
