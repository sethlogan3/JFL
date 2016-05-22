package jfl;

import java.io.*;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;
import org.json.JSONObject;

public class EndpointUtil {
    public static final String TICKET_URL="https://www.f-list.net/json/getApiTicket.php";
    public static final String API_URL="https://www.f-list.net/json/api/";
    public static String ticket="",user;
    
    public static String getTicket(String username,String password) throws Exception {
        user=username;
        String parameters="account="+username+"&password="+password;
 
        System.out.println("Requesting ticket...");   
        JSONObject fullTicket=postRequest(TICKET_URL,parameters);
        
        if (fullTicket.getString("error").equals("")) {
            ticket=fullTicket.getString("ticket");
            System.out.println("Ticket approved: "+ticket);
        }else 
            System.out.println("Unable to get ticket: "+fullTicket.getString("error"));

        return ticket;
    }
    
    public static String getApiUrl(String name) {
        return API_URL+name+".php";
    }

    //Handles a general JSON endpoint POST request and returns the response in the form of a JSON Object
    public static JSONObject postRequest(String url,String paramString) throws Exception{
        String newLine,response="";
        HttpsURLConnection connection = (HttpsURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        try (DataOutputStream out = new DataOutputStream(connection.getOutputStream())) {
            out.writeBytes(paramString);
            out.flush();
        }
        
        System.out.println("POST request sent");
        
        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            while ((newLine = in.readLine()) != null)
                response+=newLine;
        }
        
        return new JSONObject(response);
    }
    
    public static JSONObject characterDataPOST(String description,String name) throws Exception{
        System.out.println("Requesting "+description+"...");
        String parameters="account="+user+"&ticket="+ticket+"&name="+name;
        return postRequest(getApiUrl(description),parameters);
    }
}
