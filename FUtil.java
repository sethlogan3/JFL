package jfl;

import java.io.*;
import java.util.*;
import org.json.*;

public class FUtil {
    public static String formatAsEnum(String str) {
        String replaced=str.replaceAll(" ","_").replaceAll("-","_").replace("/","_").replace("(","").replace(")","");
        return replaced.trim().toUpperCase();
    } 
    
    public static Enum getEnum(String str,Enum[] en) {
        for(Enum e : en) {
            if (FUtil.formatAsEnum(str).equals(e.toString()))
                return e;
        }
        
        return null;
    }
    
    public static JSONObject loadJSON(String filename) throws FileNotFoundException {
        Scanner s=new Scanner(new File(filename));
        String content = s.useDelimiter("\\Z").next();
        return new JSONObject(content);
    }
    
    public static ArrayList<String> jsonToArrayList(JSONArray array) {
        ArrayList<String> arrayList=new ArrayList();
        
        for (int i=0; i<array.length(); i++) 
            arrayList.add(array.getString(i));
        
        return arrayList;     
    }
 
    public static JSONArray arrayListToJSON(ArrayList<String> array) {
        JSONArray jsonArray=new JSONArray();

        for (String str:array) 
            jsonArray.put(str);
        
        return jsonArray;
    }
    
    public static JSONArray arrayToJSON(String[] array) {
        JSONArray jsonArray=new JSONArray();

        for (String str:array) 
            jsonArray.put(str);
        
        return jsonArray;
    }
    
    public static HashMap parseJSONObject(JSONObject object) {
        String key;
        HashMap hashMap=new HashMap();
        Iterator keys = object.keys();

        while(keys.hasNext()) {
            key=(String)keys.next();
            hashMap.put(key,object.get(key));        
        }
        
        return hashMap;
    }
}
