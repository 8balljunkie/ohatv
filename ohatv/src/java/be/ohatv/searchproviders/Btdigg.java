/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ohatv.searchproviders;

import be.ohatv.sqlite.DbFunctions;
import com.google.common.base.Strings;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.codehaus.jettison.json.JSONObject;

/**
 *
 * @author glenn
 */
public class Btdigg {
    private static final String url = "http://btdigg.org/search?info_hash=&q=";
    
    //example of a phrase 12 Monkeys S01E01 720p
    public static String GrapMagnet(String phrase){
        try{
            if(Strings.isNullOrEmpty(phrase) == false){
                JSONObject jsonsettings = DbFunctions.getBittorrentclient();
                String ignorewords[] = null;
                if(jsonsettings.has("btignorewords")){
                    String btignorewords = jsonsettings.getString("btignorewords");
                    ignorewords = btignorewords.split(";");
                }
                phrase = phrase.replaceAll("\\(", "");
                phrase = phrase.replaceAll("\\)", "");
                String strwords[] = phrase.split(" ");
                String quality = "";
                if(phrase.contains("720p")){
                    quality = "720p";
                } else if(phrase.contains("1080p")) {
                    quality = "1080p";
                }
                
                StringBuilder sbhtml = new StringBuilder();
                sbhtml.append(url).append(URLEncoder.encode(phrase, "UTF-8"));
                String btdiggpage = Webclient.sendGet(sbhtml.toString());
                if(Strings.isNullOrEmpty(btdiggpage) == false){
                    List<String> lstmagnets = new ArrayList<>();
                    Pattern p = Pattern.compile("href=\"(.*?)\"");
                    Matcher m = p.matcher(btdiggpage);
                    while (m.find()) {
                        String magnet = m.group(1); 
                        if(magnet.contains("magnet:?")){
                            lstmagnets.add(magnet);
                        }
                    }
                    if(lstmagnets.size() > 0){
                        int counter = 0;
                        for(String magnet : lstmagnets){
                            for(String word : strwords){
                                if(magnet.toLowerCase().contains(word.toLowerCase())){
                                    counter++;
                                }
                            }
                            if(counter == strwords.length){
                                if(Strings.isNullOrEmpty(quality) == false){
                                    if(magnet.contains(quality) == false){
                                        counter = 0;
                                    }  
                                } else {
                                    if(magnet.contains("720") && magnet.contains("1080")){
                                        counter = 0;
                                    }
                                }  
                            }
                            if(counter == strwords.length){
                                return magnet;
                            }
                        }
                    }
                }
            }
        } catch(Exception ex){
            ex.printStackTrace();
        }
        return null;
    }
}
