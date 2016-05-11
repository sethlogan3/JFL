package superbot;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;
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
