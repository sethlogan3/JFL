package jfl.components;

import java.util.*;
import jfl.util.EndpointUtil;
import org.json.*;

public class Kink {
    public static ArrayList<Kink> kinksList=new ArrayList(); 
    public String name,category;
    public int id;
    
    public Kink(String kinkName,int kinkId,String cat) {
        name=kinkName;
        id=kinkId;
        category=cat;
    }
    
    public int getID() {
        return id;
    }
  
    public String getIDString() {
        return String.valueOf(id);
    }
    
    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }
      
    public JSONObject kinkToJSON() {
        JSONObject returnObject=new JSONObject();
        JSONObject obj=new JSONObject();
        
        obj.put("name",name);
        obj.put("id",id);
        obj.put("category",category);
        returnObject.put("kink",obj);
        
        return returnObject;
    }
 
    public static ArrayList<Kink> getKinksList() {
        return kinksList;
    }
       
    public static void updateKinksList() throws Exception {
        JSONObject kinkInfo=EndpointUtil.postRequest(EndpointUtil.getApiUrl("kink-list"),"").getJSONObject("kinks");
        Iterator keys = kinkInfo.keys();

        while(keys.hasNext()) {
            JSONObject currentGroup=kinkInfo.getJSONObject((String)keys.next());
            String category=currentGroup.getString("group");
            JSONArray items=currentGroup.getJSONArray("items");

            for (int i=0; i<items.length(); i++) {
                JSONObject currentKink=items.getJSONObject(i);
                int id=currentKink.getInt("kink_id");
                kinksList.add(new Kink(currentKink.getString("name"),id,category)); 
            }
        }
    } 

    public static ArrayList<Kink> getKinksByCategory(String category) {
        ArrayList<Kink> returnKinks=new ArrayList<>();
        
        for (Kink kink:kinksList) {
            if (kink.getCategory().equals(category))
                returnKinks.add(kink);
        }
        
        return returnKinks;
    }
    
    public static Kink getKinkByID(int id) {
        for (Kink kink:kinksList) {
            if (kink.getID()==id)
                return kink;
        }
        
        return null;
    }
    
    public static Kink getKinkByName(String name) {
        for (Kink kink:kinksList) {
            if (kink.getName().equals(name))
                return kink;
        }
        
        return null;        
    }
    @Override public String toString() {
        return kinkToJSON().toString();
    }
}
