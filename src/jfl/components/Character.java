package jfl.components;

import java.util.*;
import jfl.util.EndpointUtil;
import org.json.*;
import jfl.util.FListException;
import jfl.util.JSONUtil;

public class Character {
    public String name,status,statusMessage,gender;
    public boolean chatop;

    private HashMap<Kink,String>kinks=new HashMap();

    private HashMap<String,String> 
        contactDetails=new HashMap(),
        sexualDetails=new HashMap(),
        generalDetails=new HashMap(),
        rpingPreferences=new HashMap(),
        profileInfo=new HashMap(),
        customKinks=new HashMap();
    
    public Character(String characterName,String characterGender,String characterStatus) throws Exception {
        name=characterName; 
        gender=characterGender;
        status=characterStatus;
    }
        
    public Character(String characterName) {
        name=characterName; 
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String characterName) {
        name=characterName;
    }
    
    public void setChatOp() {
        chatop=true;
    }
 
    public void setChatOp(boolean bool) {
        chatop=bool;
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
    
    public final void setGender(String characterGender) throws Exception {
        gender=characterGender;
        generalDetails.put("gender",characterGender);
    }
    
    public HashMap<String,String> getContactDetails() throws Exception {
        if (contactDetails.isEmpty())
            updateCharacterInfo();
        
        return contactDetails;
    }

    public String getContactDetail(String key) throws Exception {
        if (!contactDetails.containsKey(key))
           updateCharacterInfo();

        return contactDetails.get(key);
    }

    public void toSomething() {
        
    }
    public void setContactDetails(HashMap<String,String> details) throws Exception {
        contactDetails=details;
    }
 
    public void setContactDetail(String key,String value) throws Exception {
        contactDetails.put(key,value);
    }
        
    public HashMap<String,String> getSexualDetails() throws Exception {
        if (sexualDetails.isEmpty())
            updateCharacterInfo();
        
        return sexualDetails;
    }

    public String getSexualDetail(String key) throws Exception {
        if (!sexualDetails.containsKey(key))
           updateCharacterInfo();

        return sexualDetails.get(key);
    }

    public void setSexualDetails(HashMap<String,String> details) throws Exception {
        sexualDetails=details;
    }

    public void setSexualDetail(String key,String value) throws Exception {
        sexualDetails.put(key,value);
    }
    
    public HashMap<String,String> getGeneralDetails() throws Exception {
        if (generalDetails.isEmpty())
            updateCharacterInfo();
        
        return generalDetails;
    }
 
    public String getGeneralDetail(String key) throws Exception {
        if (!generalDetails.containsKey(key))
           updateCharacterInfo();

        return generalDetails.get(key);
    }
    
    public void setGeneralDetails(HashMap<String,String> details) throws Exception {
        generalDetails=details;
    }

    public void setGeneralDetail(String key,String value) throws Exception {
        generalDetails.put(key,value);
    }
    
    public HashMap<String,String> getRPingPreferences() throws Exception {
        if (rpingPreferences.isEmpty())
            updateCharacterInfo();
         
        return rpingPreferences;
    }

    public String getRPingPreference(String key) throws Exception {
        if (!rpingPreferences.containsKey(key))
           updateCharacterInfo();

        return rpingPreferences.get(key);
    }
    
    public void setRPingPreferences(HashMap<String,String> preferences) throws Exception {
        rpingPreferences=preferences;
    }

    public void setRPingPreference(String key,String value) throws Exception {
        rpingPreferences.put(key,value);
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
            updateKinksFromAPI(); 

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
      
    public void setCustomKinks(HashMap<String,String> kinks) throws Exception {
        customKinks=kinks;
    }

    public void setCustomKink(String key,String value) throws Exception {
        customKinks.put(key, value);
    }
    
    public void clearCustomKinks() {
        customKinks.clear();
    }
    
    public void addCustomKink(String key, String value) {
        customKinks.put(key,value);
    }
    
    public HashMap<Kink,String> updateKinksFromAPI() throws Exception {
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
        
        return kinks;
    }
    
    public HashMap<String,String> updateCustomKinks() throws Exception {
        JSONArray array=EndpointUtil.characterDataPOST("character-customkinks",name).getJSONArray("kinks");
        customKinks=parseJSONArray(array,"description");
        return customKinks;
    }    
    
    public HashMap<String,String> updateProfileInfo() throws Exception {
        JSONObject param=EndpointUtil.characterDataPOST("character-get",name);
        profileInfo=JSONUtil.parseJSONObject(param.getJSONObject("character"));
        return profileInfo;
    }
    
    public HashMap<String,String>[] updateCharacterInfo() throws Exception {    
        JSONObject param=EndpointUtil.characterDataPOST("character-info",name).getJSONObject("info");
                        
        contactDetails=parseJSONArray(param.getJSONObject("1").getJSONArray("items"),"value");
        sexualDetails=parseJSONArray(param.getJSONObject("2").getJSONArray("items"),"value");
        generalDetails=parseJSONArray(param.getJSONObject("3").getJSONArray("items"),"value");
        rpingPreferences=parseJSONArray(param.getJSONObject("5").getJSONArray("items"),"value");
        return new HashMap[]{contactDetails,sexualDetails,generalDetails,rpingPreferences};
    }
    
    private static HashMap parseJSONArray(JSONArray array,String value) {
        HashMap hashMap=new HashMap();
        JSONObject obj;

        for (int i=0; i<array.length(); i++) {
            obj=array.getJSONObject(i);
            hashMap.put(obj.getString("name"),obj.getString(value));
        }
        
        return hashMap;
    }

    public String getKinkChoice(int id) throws Exception {
        return getKinks().get(Kink.getKinkByID(id));
    }
    
    public String getKinkChoice(String name) throws Exception {
        return getKinks().get(Kink.getKinkByName(name));
    }
    
    @Override public String toString() {
        return "character:"+name;
    }
   
}
