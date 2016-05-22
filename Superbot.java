package jfl;

import java.util.*;
import org.json.JSONObject;
import static jfl.Character.*;
import static jfl.Channel.*;
import static jfl.Kink.*;

public class Superbot extends FClient{
    public Superbot(String clientName,String clientVersion) throws Exception {
        super(clientName,clientVersion);
    }

    @Override public void onLogin() throws Exception{
        joinChannel("ADH-491cbcdbbbe8039e87cb");
        Character character=new Character("Sid the Kid");
        System.out.println(character.getSexualDetails("cock length (inches)"));
    }
 
    public static void main(String[] args) throws Exception {
        JSONObject loginInfo=FUtil.loadJSON("data/LoginInfo.json");  
        Superbot superbot=new Superbot(
            loginInfo.getString("client name"),
            loginInfo.getString("client version"));
        superbot.login(
            loginInfo.getString("username"),
            loginInfo.getString("character"),
            loginInfo.getString("password"),
            Server.TEST);
    }
}