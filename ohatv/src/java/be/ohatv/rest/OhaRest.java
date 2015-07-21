/*
 * REST API
 * @author glenn
 */
package be.ohatv.rest;

import be.ohatv.btclients.Blackhole;
import be.ohatv.btclients.qBittorrent;
import be.ohatv.filemanager.Filemanager;
import be.ohatv.searchproviders.Btdigg;
import be.ohatv.searchproviders.SearchProvider;
import be.ohatv.searchproviders.Thetvdb;
import be.ohatv.sqlite.*;
import be.ohatv.threading.OhaTvDoStuffOnYourOwn;
import java.util.Iterator;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.PathParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;

@Path("api")
public class OhaRest {

    @Context
    private UriInfo context;

    public OhaRest() {
    }
    
    @Path("test")
    @GET
    @Produces("application/json")
    public Response getTest() {
        try{
            Blackhole.downloadfile("9609F0336566953F3BF342241B25E2437F65B2C8", "/home/glenn/Downloads/aaa.torrent");
            //Btdigg.GrapMagnet("Game of Thrones S05E10 720p");
            //return Response.ok(Filemanager.linkmanualy(8).toString(), MediaType.APPLICATION_JSON).build();
            //return Response.ok(Filemanager.getAllFiles(8).toString(), MediaType.APPLICATION_JSON).build();
            //qBittorrent.SendMagnet();
            //return Response.ok(Thetvdb.searchshow("12 Monkeys").toString(), MediaType.APPLICATION_JSON).build();
        } catch(Exception ex){
            ex.printStackTrace();
        }
        return Response.ok("{\"status\":\"ok\"}", MediaType.APPLICATION_JSON).build();
    }
    
    @Path("searchshow/{showname}")
    @GET
    @Produces("application/json")
    public Response getSearchShow(@PathParam("showname") String showname) {
        try{
            JSONArray ja = Thetvdb.searchshow(showname);
            if(ja != null){
                return Response.ok(ja.toString(), MediaType.APPLICATION_JSON).build();
            }
        } catch(Exception ex){
            ex.printStackTrace();
        }
        return Response.serverError().entity("{\"status\":\"Error\"}").build();
    }
    //Nashville2012 259055/508/724/nashville-2012/null
    //example 272644/1014/1170/12-monkeys
    //example /121361/350/481/game-of-thrones
    @Path("addShow/{thetvdbid}/{showrssid}/{quality}")
    @GET
    @Produces("application/json")
    public Response addShow(@PathParam("thetvdbid") int thetvdbid,
                            @PathParam("showrssid") int showrssid,
                            @PathParam("quality") String quality) {
        try{
            if(conManager.isDatabaseSet()){
                JSONObject json = new JSONObject();
                json.put("thetvdbid", thetvdbid);
                if(showrssid != 0){
                    json.put("showrssid", showrssid);
                }
                //if(eztvid.equalsIgnoreCase("null") == false && eztvname.equalsIgnoreCase("null") == false) {
                //    json.put("eztv", eztvid +"/"+eztvname);
                //}
                if(quality.equalsIgnoreCase("null") == false ) {
                    json.put("quality", quality);
                }
                JSONObject jsontvdbid = Thetvdb.getTvDBinfo(thetvdbid);
                if(jsontvdbid.has("Showname")){
                    json.put("tvdbinfo", jsontvdbid);
                    JSONObject jsonshort = new JSONObject();
                    String status = jsontvdbid.getString("Status");
                    jsonshort.put("Showname", jsontvdbid.getString("Showname"));
                    if(status.equalsIgnoreCase("Ended") == false){
                        jsonshort.put("NextAirdate", "01/01/2015");
                    }
                    jsonshort.put("Status", status);
                    jsonshort.put("EpisodeCount", jsontvdbid.getInt("EpisodeCount"));
                    jsonshort.put("EpisodesIgnored", jsontvdbid.getInt("EpisodeCount"));
                    jsonshort.put("EpisodesWanted", 0);
                    jsonshort.put("EpisodesDownloaded", 0);
                    DbFunctions.addShow(jsontvdbid.getString("Showname"), json, jsonshort);
                    DbFunctions.addNotifications(2, "Show " + jsontvdbid.getString("Showname") + " added.");
                    return Response.ok("{\"status\":\"ok\"}", MediaType.APPLICATION_JSON).build();
                }
                //return Response.ok(json.toString(), MediaType.APPLICATION_JSON).build();
            } else {
                return Response.serverError().entity("{\"status\":\"Database gone\"}").build();
            }
        } catch(Exception ex){
            
        }
        return Response.serverError().entity("{\"status\":\"Error\"}").build();
    }
    
    @Path("updateShow/{showid}/{showrssid}/{quality}")
    @GET
    @Produces("application/json")
    public Response updateShow(@PathParam("showid") int showid,
                            @PathParam("showrssid") int showrssid,
                            @PathParam("quality") String quality) {
        try{
            if(showid != 0){
                JSONObject jsonfull = DbFunctions.getFullShowObject(showid);
                jsonfull.put("showrssid", showrssid);
                //jsonfull.put("eztv", eztvid +"/"+eztvname);
                if(quality.equalsIgnoreCase("null") == false ) {
                    jsonfull.put("quality", quality);
                } else {
                    jsonfull.remove("quality");
                }
                DbFunctions.updateTvdbInfo(showid, jsonfull);
                return Response.ok("{\"status\":\"ok\"}", MediaType.APPLICATION_JSON).build();
            }
        } catch (Exception ex){
            
        }
        return Response.serverError().entity("{\"status\":\"Error\"}").build();
    }
    
    @Path("getTLDRshows")
    @GET
    @Produces("application/json")
    public Response getTLDRshows() {
        try{
            if(conManager.isDatabaseSet()){
                JSONArray ja = DbFunctions.getTLDRshows();
                if(ja != null){
                    JSONObject json = new JSONObject();
                    json.put("count", ja.length());
                    json.put("Shows", ja);
                    return Response.ok(json.toString(), MediaType.APPLICATION_JSON).build();
                }
            }
        } catch(Exception ex){
            ex.printStackTrace();
        }
        return Response.serverError().entity("{\"status\":\"Error\"}").build();
    }
    
    @Path("getFullShows")
    @GET
    @Produces("application/json")
    public Response getAllShows() {
        try{
            if(conManager.isDatabaseSet()){
                JSONArray ja = DbFunctions.getAllShows();
                if(ja != null){
                    JSONObject json = new JSONObject();
                    json.put("count", ja.length());
                    json.put("Shows", ja);
                    return Response.ok(json.toString(), MediaType.APPLICATION_JSON).build();
                }
            }
            else{
                return Response.serverError().entity("{\"status\":\"No Database\"}").build();
            }
        } catch(Exception ex){
            
        }
        return Response.serverError().entity("{\"status\":\"Error\"}").build();
    }
    
    @Path("getFullShowObject/{showid}")
    @GET
    @Produces("application/json")
    public Response getShowObject(@PathParam("showid") int showid) {
        try{
            if(conManager.isDatabaseSet()){
                JSONObject json = DbFunctions.getFullShowObject(showid);
                if(json != null){
                    return Response.ok(json.toString(), MediaType.APPLICATION_JSON).build();
                }
            }
            else{
                return Response.serverError().entity("{\"status\":\"No Database\"}").build();
            }
        } catch(Exception ex){
            
        }
        return Response.serverError().entity("{\"status\":\"Error\"}").build();
    }
    
    @Path("getCountStatus")
    @GET
    @Produces("application/json")
    public Response getCount() {
        try{
            if(conManager.isDatabaseSet()){
                JSONObject json = DbFunctions.countStatus();
                return Response.ok(json.toString(), MediaType.APPLICATION_JSON).build();
            }
        } catch(Exception ex){
            ex.printStackTrace();
        }
        return Response.serverError().entity("{\"status\":\"Error\"}").build();
    }
    
    @Path("getNotifications/{status}/{limit}")
    @GET
    @Produces("application/json")
    public Response getNotifications(@PathParam("status") int status, @PathParam("limit") int limit) {
        try{
            if(conManager.isDatabaseSet()){
               JSONArray janotifications = DbFunctions.getNotifications(status, limit);
               if(janotifications != null){
                    JSONObject json = new JSONObject();
                    json.put("count", janotifications.length());
                    json.put("Notifications", janotifications);
                    return Response.ok(json.toString(), MediaType.APPLICATION_JSON).build();
                }
            }
        } catch(Exception ex){
            ex.printStackTrace();
        }
        return Response.serverError().entity("{\"status\":\"Error\"}").build();
    }
    
    @Path("search/{showid}/{searchparam}/{quality}")
    @GET
    @Produces("application/json")
    public Response searchMagnet(@PathParam("showid") int showid, @PathParam("searchparam") String searchparam, @PathParam("quality") String quality) {
        try{
            if(conManager.isDatabaseSet()){
                DbFunctions.addRequestdownload(showid, searchparam, quality, true);
                SearchProvider.doSearch();
                return Response.ok("{\"status\":\"ok\"}", MediaType.APPLICATION_JSON).build();
            }
        } catch(Exception ex){
            ex.printStackTrace();
        }
        return Response.serverError().entity("{\"status\":\"Error\"}").build();
    }
    
    @Path("updatebtclient")
    @POST
    @Produces("application/json")
    @Consumes("application/json")
    //{"btclient":"transmission", "bturl":"http://localhost:9091/rpc", "btpath":"/home/glenn", "MoveTo":"/home/glenn/Downloads"}
    public Response updatebtclient(String jsonstring) {
        try{
            JSONObject json = new JSONObject(jsonstring);
            DbFunctions.updateBittorrentclient(json);
            return Response.ok("{\"status\":\"ok\"}", MediaType.APPLICATION_JSON).build();
        } catch(Exception ex){
            ex.printStackTrace();
        }
        return Response.serverError().entity("{\"status\":\"Error\"}").build();
    }
    
    @Path("process")
    @GET
    @Produces("application/json")
    public Response process() {
        try{
            if(conManager.isDatabaseSet()){
                OhaTvDoStuffOnYourOwn otdsoyo = OhaTvDoStuffOnYourOwn.getInstance();
                otdsoyo.processShows();
                return Response.ok("{\"status\":\"ok\"}", MediaType.APPLICATION_JSON).build();
            }
        } catch(Exception ex){
            ex.printStackTrace();
        }
        return Response.serverError().entity("{\"status\":\"Error\"}").build();
    }
    
    @Path("processTimer")
    @GET
    @Produces("application/json")
    public Response processPeriodically() {
        try{
            if(conManager.isDatabaseSet()){
                OhaTvDoStuffOnYourOwn otdsoyo = OhaTvDoStuffOnYourOwn.getInstance();
                if(otdsoyo.getKeeprunning() == false){
                    otdsoyo.setKeeprunning(true);
                    Thread t1 = new Thread(otdsoyo);
                    
                    t1.start();
                }
                return Response.ok("{\"status\":\"ok\"}", MediaType.APPLICATION_JSON).build();
            }
        } catch(Exception ex){
            ex.printStackTrace();
        }
        return Response.serverError().entity("{\"status\":\"Error\"}").build();
    }
    
    @Path("StopProcessTimer")
    @GET
    @Produces("application/json")
    public Response StopprocessPeriodically() {
        try{
            if(conManager.isDatabaseSet()){
                OhaTvDoStuffOnYourOwn otdsoyo = OhaTvDoStuffOnYourOwn.getInstance();
                otdsoyo.cancel();
                return Response.ok("{\"status\":\"ok\"}", MediaType.APPLICATION_JSON).build();
            }
        } catch(Exception ex){
            ex.printStackTrace();
        }
        return Response.serverError().entity("{\"status\":\"Error\"}").build();
    }
    
    @Path("lastProcessCheck")
    @GET
    @Produces("application/json")
    public Response lastProcessCheck() {
        try{
            if(conManager.isDatabaseSet()){
                OhaTvDoStuffOnYourOwn otdsoyo = OhaTvDoStuffOnYourOwn.getInstance();
                String time = otdsoyo.getLastcheck();
                if(time != null){
                    JSONObject json = new JSONObject();
                    json.put("lastProcessCheck", time);
                    return Response.ok(json.toString(), MediaType.APPLICATION_JSON).build();
                }
                return Response.ok("{\"status\":\"false\"}", MediaType.APPLICATION_JSON).build();
            }
        } catch(Exception ex){
            ex.printStackTrace();
        }
        return Response.serverError().entity("{\"status\":\"Error\"}").build();
    }
    
    @Path("updateEpisodeStatus/{showid}")
    @POST
    @Produces("application/json")
    @Consumes("application/json")
    //{"btclient":"transmission", "bturl":"http://localhost:9091/rpc", "btpath":"/home/glenn", "MoveTo":"/home/glenn/Downloads"}
    public Response updateEpisodeStatus(@PathParam("showid") int showid, String jsonstring) {
        try{
            JSONObject json = new JSONObject(jsonstring);
            JSONObject jsonfull = DbFunctions.getFullShowObject(showid);
            JSONObject jsontvdbinfo = jsonfull.getJSONObject("tvdbinfo");
            JSONObject jsonEpisodeStatus = jsontvdbinfo.getJSONObject("EpisodeStatus");
            Iterator<?> keys = json.keys();
            while(keys.hasNext()){
                String key = (String)keys.next();
                int newstatus = json.getInt(key);
                //jsonEpisodeStatus.remove(key);
                jsonEpisodeStatus.put(key, newstatus);
            }
            //jsontvdbinfo.remove("EpisodeStatus");
            jsontvdbinfo.put("EpisodeStatus", jsonEpisodeStatus);
            jsonfull.remove("tvdbinfo");
            jsonfull.put("tvdbinfo", jsontvdbinfo);
            DbFunctions.updateTvdbInfo(showid, jsonfull);
            DbFunctions.updateTLDRshows(showid);
            return Response.ok("{\"status\":\"ok\"}", MediaType.APPLICATION_JSON).build();
        } catch(Exception ex){
            ex.printStackTrace();
        }
        return Response.serverError().entity("{\"status\":\"Error\"}").build();
    }
    
    @Path("removeShow/{showid}")
    @GET
    @Produces("application/json")
    public Response removeShow(@PathParam("showid") int showid) {
        try{
            if(conManager.isDatabaseSet()){
                JSONObject json = DbFunctions.getTLDRshow(showid);
                DbFunctions.addNotifications(2, "Show " + json.getString("Showname") + " removed.");
                DbFunctions.deleteshow(showid);
                return Response.ok("{\"status\":\"ok\"}", MediaType.APPLICATION_JSON).build();
            }
        } catch(Exception ex){
            ex.printStackTrace();
        }
        return Response.serverError().entity("{\"status\":\"Error\"}").build();
    }
    
    @Path("removeNotifications")
    @GET
    @Produces("application/json")
    public Response removeNotifications() {
        try{
            if(conManager.isDatabaseSet()){
                boolean success = DbFunctions.removeNotifaction();
                if(success){
                    return Response.ok("{\"status\":\"ok\"}", MediaType.APPLICATION_JSON).build();
                }
            }
        } catch(Exception ex){
            ex.printStackTrace();
        }
        return Response.serverError().entity("{\"status\":\"Error\"}").build();
    }
    
    @Path("ManualLinkFiles/{showid}")
    @GET
    @Produces("application/json")
    public Response getManualLinkFiles(@PathParam("showid") int showid) {
        try{
            //return Response.ok(Filemanager.linkmanualy(8).toString(), MediaType.APPLICATION_JSON).build();
            return Response.ok(Filemanager.getAllFiles(showid).toString(), MediaType.APPLICATION_JSON).build();
        } catch(Exception ex){
            ex.printStackTrace();
        }
        return Response.serverError().entity("{\"status\":\"Error\"}").build();
    }
    
    @Path("getRequestDownloads")
    @GET
    @Produces("application/json")
    public Response getRequestDownloads() {
        try{
            JSONArray ja = DbFunctions.getAllRequestdownloads();
            if(ja != null){
                return Response.ok(ja.toString(), MediaType.APPLICATION_JSON).build();
            }
        } catch(Exception ex){
            ex.printStackTrace();
        }
        return Response.serverError().entity("{\"status\":\"Error\"}").build();
    }
    
    @Path("removeRequestDownload/{id}")
    @GET
    @Produces("application/json")
    public Response removeRequestDownload(@PathParam("id") int id) {
        try{
            DbFunctions.deleteRequestdownloadID(id);
            return Response.ok("{\"status\":\"ok\"}", MediaType.APPLICATION_JSON).build();
        } catch(Exception ex){
            ex.printStackTrace();
        }
        return Response.serverError().entity("{\"status\":\"Error\"}").build();
    }
    
    @Path("LinkFileWithEpisode")
    @POST
    @Consumes("application/json")
    public Response LinkFileWithEpisode(String jsonstring){
        try{
            JSONObject json = new JSONObject(jsonstring);
            boolean success = Filemanager.linkFile(json.getInt("SHOWID"), json.getString("SeasonEpisodeNumber"), json.getString("ShowfileName"), json.getString("filepath"));
            if(success){
                 return Response.ok("{\"status\":\"ok\"}", MediaType.APPLICATION_JSON).build();
            }
        } catch (Exception ex){
            ex.printStackTrace();
        }
        return Response.serverError().entity("{\"status\":\"Error\"}").build();
    }
}
