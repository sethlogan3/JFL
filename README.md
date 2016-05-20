# JFL - Java Flist Library

## Overview

JFL is a Java library for the online sexual roleplaying website f-list.net, offering features for easy implementation of clients and bots for F-chat, and general access of API endpoint data.

##Code Example
```java
public class Superbot extends FClient{
    public Superbot(String clientName,String clientVersion) throws Exception {
        super(clientName,clientVersion);
    }

    @Override public void onLogin() throws Exception{
        joinChannel("ADH-491cbcdbbbe8039e87cb");
    }

    public static void main(String[] args) throws Exception {
        JSONObject loginInfo=FUtil.loadJSON("data/LoginInfo.json");  
        Superbot superbot=new Superbot(loginInfo.getString("client name"),
                                       loginInfo.getString("client version"));
        superbot.login(loginInfo.getString("username"),
                       loginInfo.getString("character"),
                       loginInfo.getString("password"),
                       Server.TEST);
    }
}
```