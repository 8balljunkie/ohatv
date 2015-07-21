package be.ohatv.threading;

import be.ohatv.filemanager.Filemanager;
import be.ohatv.searchproviders.SearchProvider;
import be.ohatv.searchproviders.Thetvdb;
import be.ohatv.sqlite.DbFunctions;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import org.codehaus.jettison.json.JSONObject;
import org.codehaus.jettison.json.JSONArray;

public class OhaTvDoStuffOnYourOwn implements Runnable//otdsoyo
{   
    private static OhaTvDoStuffOnYourOwn otd = null;
    
    private Boolean keeprunning;
    private boolean isProcessing = false;
    private Date lastcheck = null;
    
    public static OhaTvDoStuffOnYourOwn getInstance(){
        if(otd == null){
            otd = new OhaTvDoStuffOnYourOwn();
            otd.keeprunning = false;
        }
        return otd;
    }
    
    @Override
    public void run() {
        try{
            while(keeprunning){
                processShows();
                Thread.sleep(3600000);
            }
        } catch(Exception ex){
            
        }
        
        
    }
    
    public void processShows(){
        try{
            if(isProcessing == false){
                isProcessing = true;
                if(lastcheck == null){
                    lastcheck = new Date(new Date().getTime()-3699999);
                }
                markAsDownloaded();
                DbFunctions.deleteRequestdownload(2);
                refreshshowinfo();
                checkEpisodeWanted();
                checkepisodeairdates();
                Filemanager.getAllVideoFiles();
                SearchProvider.doSearch();
                lastcheck = new Date();
            }
        } catch(Exception ex){
            
        }
        isProcessing = false;
    }
    
    public void cancel(){
        keeprunning = false;
        isProcessing = false;
        otd = null;
    }
    
    private void refreshshowinfo(){
        try{
            if(lastcheck.getTime()+3600000 < new Date().getTime()){
                JSONArray ja = DbFunctions.getAllShows();
                for(int i = 0; i < ja.length(); i++){
                    JSONObject jsonfull = ja.getJSONObject(i);
                    JSONObject jsonEpisodeStatusold = jsonfull.getJSONObject("tvdbinfo").getJSONObject("EpisodeStatus");
                    JSONObject jsontvdbid = Thetvdb.getTvDBinfo(jsonfull.getInt("thetvdbid"));
                    if(jsontvdbid != null){
                        int id = jsonfull.getInt("ID");
                        jsonfull.remove("ID");
                        jsonfull.remove("tvdbinfo");
                        jsonfull.put("tvdbinfo", jsontvdbid);
                        Iterator<?> keys = jsonEpisodeStatusold.keys();
                        JSONObject jsonEpisodeStatusnew = jsonfull.getJSONObject("tvdbinfo").getJSONObject("EpisodeStatus");
                        while(keys.hasNext()){
                            String key = (String)keys.next();
                            int status = jsonEpisodeStatusold.getInt(key);
                            if(status > 0){
                                jsonEpisodeStatusnew.remove(key);
                                jsonEpisodeStatusnew.put(key, status);
                            }
                        }
                        DbFunctions.updateTvdbInfo(id, jsonfull);
                    }
                    Thread.sleep(500);
                }
            }
        } catch(Exception ex){
            
        }
    }
    
    private void checkepisodeairdates(){
        try{
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date datetoday = new Date();
            JSONArray ja = DbFunctions.getAllShows();
            for(int i = 0; i < ja.length(); i++){
                JSONObject jsonfull = ja.getJSONObject(i);
                JSONObject jsonepisodestatus = jsonfull.getJSONObject("tvdbinfo").getJSONObject("EpisodeStatus");      
                JSONObject jsonairdates = jsonfull.getJSONObject("tvdbinfo").getJSONObject("EpisodeAirdates");     
                Iterator<?> keys = jsonepisodestatus.keys();
                while(keys.hasNext()){
                    String key = (String)keys.next();
                    if(jsonairdates.has(key)){
                        if(jsonepisodestatus.getInt(key) != 2 && jsonepisodestatus.getInt(key) != 0){
                            String strairdate = jsonairdates.getString(key);
                            Date airdate = sdf.parse(strairdate);
                            if(airdate.after(datetoday)){
                                String quality = null;
                                if(jsonfull.has("quality")){
                                    quality = jsonfull.getString("quality");
                                }
                                DbFunctions.addRequestdownload(jsonfull.getInt("ID"), key, quality, false);
                                jsonepisodestatus.remove(key);
                                jsonepisodestatus.put(key, 2);
                            }
                        }
                    }
                }
            }
        } catch(Exception ex){
            
        }
    }
    
    private void checkEpisodeWanted(){
        try{
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date datetoday = new Date();
            JSONArray ja = DbFunctions.getAllShows();
            for(int i = 0; i < ja.length(); i++){
                JSONObject jsonfull = ja.getJSONObject(i);
                JSONObject jsonepisodestatus = jsonfull.getJSONObject("tvdbinfo").getJSONObject("EpisodeStatus");      
                Iterator<?> keys = jsonepisodestatus.keys();
                List<String> lstidssnatched = new ArrayList();
                while(keys.hasNext()){
                    String key = (String)keys.next();
                    int status = jsonepisodestatus.getInt(key);
                    if(status == 1){
                        String quality = null;
                        if(jsonfull.has("quality")){
                            quality = jsonfull.getString("quality");
                        }
                        DbFunctions.addRequestdownload(jsonfull.getInt("ID"), key, quality, false);
                        lstidssnatched.add(key);
                    }
                }
                for(String key : lstidssnatched){
                    //jsonepisodestatus.remove(key);
                    jsonepisodestatus.put(key, 2);
                }
                jsonfull.getJSONObject("tvdbinfo").remove("EpisodeStatus");
                jsonfull.getJSONObject("tvdbinfo").put("EpisodeStatus", jsonepisodestatus);
            }
        } catch(Exception ex){
            
        }
    }
    
    public void markAsDownloaded(){
        try{
            JSONArray ja = DbFunctions.getRequestdownloads(2);
            if(ja != null){
                for(int i = 0; i < ja.length(); i++){
                    JSONObject jsonRequest = ja.getJSONObject(i);
                    int showid = jsonRequest.getInt("showid");
                    JSONObject jsonFull = DbFunctions.getFullShowObject(showid);
                    jsonFull.getJSONObject("tvdbinfo").getJSONObject("EpisodeStatus").put(jsonRequest.getString("searchparam"), 2);
                    DbFunctions.updateTvdbInfo(showid, jsonFull);
                    DbFunctions.updateTLDRshows(showid);
                }
            }
        } catch(Exception ex){
            
        }
    }

    public void setKeeprunning(Boolean keeprunning) {
        this.keeprunning = keeprunning;
    }

    public Boolean getKeeprunning() {
        return keeprunning;
    }

    public String getLastcheck() {
        if(lastcheck != null){
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            return sdf.format(lastcheck);
        }
        return null;
    }
    
}
