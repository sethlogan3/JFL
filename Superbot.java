package superbot;

import java.util.*;
import java.util.Map.*;
import org.json.JSONObject;

public class Superbot extends FClient{
    public Superbot(String server) throws Exception {
        super(server);
    }

    @Override public void onLogin() throws Exception {
        joinChannel("ADH-491cbcdbbbe8039e87cb");
        HashMap<Kink,String> kinks=getCharacter("Sid the Kid").getKinks();
        
        System.out.println(kinks.get(kinksList.get(160)));
    }
                
    public static void main(String[] args) throws Exception {
        JSONObject loginInfo=FUtil.loadJSON("data/LoginInfo.json");  

        Superbot superbot=new Superbot(loginInfo.getString("server"));
        superbot.login(loginInfo.getString("username"),
                loginInfo.getString("character"),
                loginInfo.getString("password"));
    }
}