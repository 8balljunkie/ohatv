/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ohatv.btclients;

import be.ohatv.filemanager.Filemanager;
import be.ohatv.sqlite.DbFunctions;
import org.codehaus.jettison.json.JSONObject;

/**
 *
 * @author glenn
 */
public class BTclient {
    private static JSONObject jsonsettings;
    
    public static boolean sendToBTclient(String magnet, String hash, String title){
        try{
            if(jsonsettings == null){
                jsonsettings = DbFunctions.getBittorrentclient();
            }
            if(jsonsettings != null){
                if(jsonsettings.has("btclient")){
                    String client = jsonsettings.getString("btclient");
                    if(jsonsettings.has("bturl") && jsonsettings.has("btpath")){
                        if(client.equalsIgnoreCase("transmission")){
                            return Transmission.addTorrent(jsonsettings.getString("bturl"), magnet, jsonsettings.getString("btpath"));
                        }
                        else if(client.equalsIgnoreCase("deluge")){
                            
                        }
                        else if(client.equalsIgnoreCase("utorrent")){
                            
                        }
                        else if(client.equalsIgnoreCase("qbittorrent")){
                            
                        } 
                        else if(client.equalsIgnoreCase("blackhole")){
                            if(Filemanager.getPcsystem() == 1){
                                Blackhole.downloadfile(hash, jsonsettings.getString("btpath") + "/" + title + ".torrent");
                            } else if(Filemanager.getPcsystem() == 2){
                                Blackhole.downloadfile(hash, jsonsettings.getString("btpath") + "\\" + title + ".torrent");
                            }
                        }
                    }
                }
            }
        } catch (Exception ex){
            
        }
        clearjsonsettings();
        return false;
    }
    
    public static void clearjsonsettings(){
        jsonsettings = null;
    }
    
}
