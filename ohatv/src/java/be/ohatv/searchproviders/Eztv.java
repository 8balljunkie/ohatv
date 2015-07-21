package be.ohatv.searchproviders;

import be.ohatv.sqlite.DbFunctions;
import com.google.common.base.Strings;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author glenn
 * This class is for code reference, Use at your own risk
 */
public class Eztv {
    private static String ezurl = "https://eztv.ch/shows/";
    private static HashMap<String, Date> hmHtmlTimeStamps = new HashMap<>();
    private static HashMap<String, List<String>> hmHtmlPages = new HashMap<>();
    
    private static List<String> GrapHtmlPage(int showid, String url){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        List<String> lstitems = DbFunctions.getShowProviderfeed(showid, "EZTV");
        List<String> lstarry = new ArrayList<>();
        try{
            if(lstitems != null){
                if(lstitems.size() == 2){
                    String feed = lstitems.get(0);
                    String date = lstitems.get(1);
                    Date datesql = sdf.parse(date);
                    Date now = new Date();
                    if(datesql.getTime() + 3600000 > now.getTime()){
                        String[] arg = feed.split("/SPLIT/");
                        for(String magnet : arg){
                            lstarry.add(magnet);
                        }
                        if(lstarry.size() > 0){
                            return lstarry;
                        }
                    }
                }
            }
        } catch(Exception ex){
            
        }
        
        try{
            String eztvpage = null;
            StringBuilder sb = new StringBuilder();
            sb.append(ezurl).append(url);
            eztvpage = Webclient.sendGet(sb.toString());
            
            if(Strings.isNullOrEmpty(eztvpage)){
                lstarry.clear();
                Date newdate = new Date();
                Pattern p = Pattern.compile("href=\"(.*?)\"");
                Matcher m = p.matcher(eztvpage);
                while (m.find()) {
                    String magnet = m.group(1); 
                    if(magnet.contains("magnet:?")){
                        lstarry.add(magnet);
                    }
                }
                if(lstarry.size() > 0){
                    StringBuilder sbez = new StringBuilder();
                    boolean isfirst = true;
                    for(String magnet : lstarry){
                        if(isfirst == false){
                            sbez.append("/SPLIT/");
                        }
                        else{
                            isfirst = false;
                        }
                        sbez.append(magnet);
                    }
                    DbFunctions.updateProviderFeed(showid, "EZTV", sbez.toString());
                    return lstarry;
                }
            }
        } catch(Exception ex){
            ex.printStackTrace();
        }
        return null;
    }
    
    public static String searchEzTv(int showid, String url, String episodeneeded, String quality){
        List<String> lstarry = GrapHtmlPage(showid, url);
        for(String magnet : lstarry){
            if(magnet.contains(episodeneeded)){
                if(Strings.isNullOrEmpty(quality)){
                    if(magnet.contains("720p") || magnet.contains("1080p")){
                        continue;
                    }
                    else{
                        return magnet;
                    }
                } else {
                    if(magnet.contains(quality)){
                        return magnet;
                    }
                }
            }
        }
        return null;
    } 
}
