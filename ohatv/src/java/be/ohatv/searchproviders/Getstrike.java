/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ohatv.searchproviders;

import be.ohatv.sqlite.DbFunctions;
import com.google.common.base.Strings;
import java.net.URLEncoder;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;

/**
 *
 * @author glenn
 */
public class Getstrike {
    public static String StrikeUrl = "https://getstrike.net";
    
    //example of a phrase 12 Monkeys S01E01 720p
    public static JSONObject doSearch(String phrase){
        try{
            JSONObject jsonsettings = DbFunctions.getBittorrentclient();
            String ignorewords[] = null;
            if(jsonsettings.has("btignorewords")){
                String btignorewords = jsonsettings.getString("btignorewords");
                ignorewords = btignorewords.split(";");
            }
            StringBuilder sburl = new StringBuilder();
            sburl.append(StrikeUrl).append("/api/v2/torrents/search/?phrase=").append(URLEncoder.encode(phrase, "UTF-8"));//.append("&category=TV");

            String jsonstring = Webclient.sendGet(sburl.toString());
            JSONObject jsonstrike = new JSONObject(jsonstring);
            phrase = phrase.replaceAll("\\(", "");
            phrase = phrase.replaceAll("\\)", "");
            String strwords[] = phrase.split(" ");
            String quality = "";
            if(phrase.contains("720p")){
                quality = "720p";
            } else if(phrase.contains("1080p")) {
                quality = "1080p";
            }
            if(jsonstrike.has("torrents")){
                JSONArray ja = jsonstrike.getJSONArray("torrents");
                for(int i = 0; i < ja.length(); i++){
                    boolean correct = true;
                    JSONObject jsontorrent = ja.getJSONObject(i);
                    String title = jsontorrent.getString("torrent_title");
                    String magnet = jsontorrent.getString("magnet_uri");
                    String hash = jsontorrent.getString("torrent_hash");
                    for(String s : strwords){ //title has the correct words
                        if(title.contains(s) == false){
                            correct = false;
                            break;
                        }
                    }
                    if(correct == false){
                        for(String s : strwords){ //magnet has the correct words
                            if(magnet.contains(s) == false){
                                correct = false;
                                break;
                            }
                        }
                    }                 
                    if(correct){
                        if(ignorewords != null){ //ignore magnet with these words
                            for(String s : ignorewords){
                                if(Strings.isNullOrEmpty(s) == false){
                                    if(magnet.contains(s) || title.contains(s)){
                                        correct = false;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    if(correct){
                        if(Strings.isNullOrEmpty(quality)){
                            if(magnet.contains("720p") || magnet.contains("1080p")){
                                continue;
                            }else{
                                    JSONObject json = new JSONObject();
                                    json.put("magnet", magnet);
                                    json.put("hash", hash);
                                    json.put("title", title);
                                    return json;
                            }
                        } else {
                            if(magnet.contains(quality)){
                                JSONObject json = new JSONObject();
                                json.put("magnet", magnet);
                                json.put("hash", hash);
                                json.put("title", title);
                                return json;
                            }
                        }
                    }
                }
            }
        } catch(Exception ex){
            
        }
        return null;
    }
    
}
