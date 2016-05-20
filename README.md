# JFL - Java Flist Library

## Overview

JFL is a Java library for the sexual roleplaying website f-list.net, offering features for easy implementation of clients and bots for F-chat, and general access of API endpoint data.

##Code Example

The following code is a simple implementation of the FClient class which reads a JSON file containing the user’s login information, logs into F-chat, and joins a specified private channel when login is successful.

```java
package jfl;
import org.json.JSONObject;

public class Bot extends FClient {
    public Bot(String clientName,String clientVersion) throws Exception {
        super(clientName,clientVersion);
    }

    @Override public void onLogin() throws Exception {
        joinChannel("ADH-491cbcdbbbe8039e87cb");
    }

    public static void main(String[] args) throws Exception {
        JSONObject loginInfo=FUtil.loadJSON("data/LoginInfo.json");  
        Bot myBot=new Bot(loginInfo.getString("client name"),
                               loginInfo.getString("client version"));
        myBot.login(loginInfo.getString("username"),
                    loginInfo.getString("character"),
                    loginInfo.getString("password"));
    }
}
```

##Features 

###Testing Features
* Easily switch between the public server and development server without needing to know their URLs or ports

###API access
* Handles all POST requests for API endpoints
 * Automatically acquires f-chat tickets
 * User can invoke methods for acquiring character data, such as gender and kinks

###F-chat communication
* Handles all websocket communication 
* Handles client-server JSON commands and formatting 
 * User can implement methods that are called for incoming server commands
 * User can invoke methods for any outgoing client commands


###F-chat data tracking
* Automatically tracks and logs PMs and channel messages
* Automatically tracks data for all characters that enter f-chat, as well as any character that the user specifies, including gender, status, status messages, and any character data requested from the API
* Automatically tracks data for all channels that the client enters, including names/titles, chanops, settings, and the characters occupying each room