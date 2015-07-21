/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ohatv.btclients;

import be.ohatv.sqlite.DbFunctions;
import com.google.common.base.Strings;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.codehaus.jettison.json.JSONObject;

/**
 *
 * @author glenn
 */
public class Transmission {
    //private static String url = "http://localhost:9091/transmission/rpc";
    //private static String path = "/home/glenn/Downloads";
    private static String sessiontoken;
    
    private static void getsessiontoken(String url){
        try{
            String s = getTransmissionSessionId(url);
            if(Strings.isNullOrEmpty(s) == false){
                sessiontoken = s;
            }
        } catch(Exception ex){
            
        }
    }
    
    public static boolean addTorrent(String url, String magnet, String downloadpath){
        try{   
            if(Strings.isNullOrEmpty(sessiontoken)){
                getsessiontoken(url);
            }
            if(Strings.isNullOrEmpty(sessiontoken) == false){
                JSONObject arg = new JSONObject();
                arg.put("filename", magnet);
                arg.put("download-dir", downloadpath);
                JSONObject json = new JSONObject();
                json.put("method", "torrent-add");
                json.put("arguments", arg);
                if(sendPost(url, json.toString(), "X-Transmission-Session-Id", sessiontoken) == false){
                    sessiontoken = null;
                    return false;
                }
                return true;
            } else {
                DbFunctions.addNotifications(3, "Can't open session with Transmission.");
            } 
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return false;
    }
    
    public static boolean transmissionTest(){
        try{
            JSONObject jsonbtsettings = DbFunctions.getBittorrentclient();
            if(jsonbtsettings.has("bturl")){
                if(Strings.isNullOrEmpty(sessiontoken)){
                    getsessiontoken(jsonbtsettings.getString("bturl"));
                }
                if(Strings.isNullOrEmpty(sessiontoken) == false){
                    return true;
                }
            }
        } catch(Exception ex){
            
        }
        return false;
    }
    
    private static String getTransmissionSessionId(String url) throws Exception {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", "Mozilla/5.0");
        int responseCode = con.getResponseCode();
        if(responseCode == 409){
            String header = con.getHeaderField("X-Transmission-Session-Id");
            return header;
        }
        return null;
    }
    

    private static boolean sendPost(String url, String data, String headerkey, String headervalue) throws Exception {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", "Mozilla/5.0");
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        con.setRequestProperty(headerkey, headervalue);
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(data);
        wr.flush();
        wr.close();
        int responseCode = con.getResponseCode();
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
        }
        in.close();
        if(responseCode == 200){
            return true;
        }
        return false;
    }
}
