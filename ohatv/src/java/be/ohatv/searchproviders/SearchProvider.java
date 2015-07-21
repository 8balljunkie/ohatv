/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ohatv.searchproviders;

import be.ohatv.btclients.BTclient;
import be.ohatv.sqlite.DbFunctions;
import com.google.common.base.Strings;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;

/**
 *
 * @author glenn
 */
public class SearchProvider {
     public static void doSearch(){
         try{
            JSONArray ja = DbFunctions.getRequestdownloads(0);
            if(ja != null){
                for(int i = 0; i < ja.length(); i++){
                    JSONObject jsonrequest = ja.getJSONObject(i);
                    int requestid = jsonrequest.getInt("ID");
                    int showid = jsonrequest.getInt("showid");
                    JSONObject JSONFULL = DbFunctions.getFullShowObject(showid);
                    String episode = jsonrequest.getString("searchparam");
                    String quality = "";
                    if(JSONFULL.has("quality")){
                        quality = JSONFULL.getString("quality");
                        if(quality.equalsIgnoreCase("null") || quality.equalsIgnoreCase("HDTV")){
                            quality = "";
                        }
                    }
                    JSONObject json = DbFunctions.getFullShowObject(showid);
                    String Showname = "";
                    if(json != null){
                        if(json.has("tvdbinfo")){
                            try{
                                JSONObject jsontvdbinfo = json.getJSONObject("tvdbinfo");
                                Showname = jsontvdbinfo.getString("Showname");
                                StringBuilder sbnotification = new StringBuilder();
                                sbnotification.append("Search started for \"").append(Showname).append(" ").append(episode).append("\"");
                                DbFunctions.addNotifications(2, sbnotification.toString());
                            } catch(Exception ex){
                                
                            }
                        }
                        
                        String magnet = null;
                        String hash = null; 
                        String title = null; 
                        if(json.has("tvdbinfo") && Strings.isNullOrEmpty(magnet)){
                            try{
                                JSONObject jsontvdbinfo = json.getJSONObject("tvdbinfo");
                                StringBuilder sbsearchparam = new StringBuilder();
                                sbsearchparam.append(jsontvdbinfo.getString("Showname")).append(" ").append(episode).append(" ").append(quality);
                                JSONObject jsonTor = new JSONObject();
                                jsonTor = Getstrike.doSearch(sbsearchparam.toString().trim());
                                magnet = jsonTor.getString("magnet");
                                hash = jsonTor.getString("hash");
                                title = jsonTor.getString("title");
                                if(Strings.isNullOrEmpty(magnet)){
                                    magnet = Btdigg.GrapMagnet(sbsearchparam.toString().trim());
                                }
                                
                            } catch (Exception ex){

                            }
                        }
                        if(Strings.isNullOrEmpty(magnet)){
                            if(json.has("showrssid")){
                                try{
                                    magnet = Showrss.searchShowRss(showid, json.getInt("showrssid"), episode, quality);
                                } catch (Exception ex){

                                }
                            } 
                        }

                        if(Strings.isNullOrEmpty(magnet)){
                            StringBuilder sbnotification = new StringBuilder();
                            sbnotification.append("No magnet found for \"").append(Showname).append(" ").append(episode).append("\"");
                            DbFunctions.addNotifications(4, sbnotification.toString());
                        } else {
                            DbFunctions.updateRequestdownload(requestid, 1);
                            StringBuilder sbnotification = new StringBuilder();
                            sbnotification.append("found magnet for \"").append(Showname).append(" ").append(episode).append("\"");
                            DbFunctions.addNotifications(4, sbnotification.toString());
                            BTclient.sendToBTclient(magnet, hash, title);
                        }
                    }
                    Thread.sleep(500);
                }
            }
        } catch (Exception ex){
            
        }
    }
}
