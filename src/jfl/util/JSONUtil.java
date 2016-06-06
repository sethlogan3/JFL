package jfl.util;

import java.io.*;
import java.util.*;
import org.json.*;

public class JSONUtil {
    
    public static JSONObject loadJSON(String filename) throws FileNotFoundException {
        Scanner s=new Scanner(new File(filename));
        String content = s.useDelimiter("\\Z").next();
        return new JSONObject(content);
    }

    public static int[] jsonToIntArray(JSONArray array) {
        int[] intArray=new int[array.length()];
        
        for (int i=0; i<array.length(); i++)
            intArray[i]=array.getInt(i);
        
        return intArray;
    }

    public static String[] jsonToStringArray(JSONArray array) {
        String[] stringArray=new String[array.length()];

        for (int i=0; i<array.length(); i++)
            stringArray[i]=array.getString(i);
        
        return stringArray;
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
