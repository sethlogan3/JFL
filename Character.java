package superbot;

import java.util.*;
import org.json.*;

public class Character {
    public static ArrayList<Kink> kinksList=new ArrayList<>();
    public String name;
    
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
 
    public String getKinkChoice(int i) throws Exception {
        return getKinks().get(kinksList.get(i));
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
                Kink kink=kinksList.get(item.getInt("id"));
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
        JSONObject param=EndpointUtil.characterDataPOST("character-get",name).getJSONObject("character");
        profileInfo=FUtil.parseJSONObject(param);
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
    
    private Kink getKinkByID(int id) {
        for (Kink kink:kinksList) {
            if (kink.getID()==id)
                return kink;
        }
        
        return null;
    }
}
