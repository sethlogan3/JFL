# JFL - Java Flist Library

## Overview
*Note: JFL is still in development as of May 2016. Code will remain incomplete and will likely contain errors until this header is removed.*

JFL is a third-party Java library for the online roleplaying website f-list.net, offering features for easy implementation of clients and bots for F-chat, and general access of API endpoint data.

##Code Example

The following code is a simple implementation of the FClient class, which reads a JSON file containing the user’s login information, logs into F-chat, and joins the specified private channel when login is successful.

```java
public class Bot extends FClient {
    //Constructor calls super to access parent constructor
    public Bot(String clientName,String clientVersion) throws Exception {
        super(clientName,clientVersion);
    }

    //Gets called by the client when it received an IDN response command from the server
    @Override public void onLogin() throws Exception {
        joinChannel("ADH-491cbcdbbbe8039e87cb"); //Joins a channel (takes one parameter, the name of the room)
    }

    //Main method that creates an instance of the Bot class and logs in
    public static void main(String[] args) throws Exception {
        Bot myBot=new Bot(“client_name”,”version_number”); //Creates a bot (FClient) object
        myBot.login(“username”,”character_name”,”password”); //Logs in
    }
}
```

##Features 

###Testing 
* User can easily switch between operating on the public server and development server without needing to know their respective URLs or ports

###API access
* Handles all POST requests for API endpoints
 * Automatically acquires f-chat tickets
 * User can invoke methods for acquiring any needed character data, such as gender and kinks

###F-chat communication
* Handles all websocket communication 
* Handles client-server JSON commands and formatting. Every command has a corresponding method.
 * User can implement methods that are called for incoming server commands
 * User can invoke methods for any outgoing client commands


###F-chat data tracking
* Tracks and logs PMs and channel messages
* Tracks data for all characters that enter f-chat (as well as any character that the user specifies) including gender, status, status message, and any character data pulled from searches or requested from the API
* Tracks data for all channels that the client enters, including names/titles, chanops, channel settings, and the characters occupying each room


##Additional Resources
[F-chat server commands](https://wiki.f-list.net/F-Chat_Server_Commands)

[F-chat client commands](https://wiki.f-list.net/F-Chat_Client_Commands)

[Error code reference](https://wiki.f-list.net/F-Chat_Error_Codes)

[F-chat development basics](https://wiki.f-list.net/F-Chat_Protocol)

[F-chat client and bot rules](https://wiki.f-list.net/F-Chat_Protocol#Guidelines)

[Are you a Ruby developer? Check out LibFchat by Jippen Faddoul](https://github.com/rgooler/libfchat-ruby)

##Contact
Contact Seth Logan through an f-list note to his character found [here](https://www.f-list.net/c/slogan/)