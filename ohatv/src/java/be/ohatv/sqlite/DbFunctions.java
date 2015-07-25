/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ohatv.sqlite;

import be.ohatv.btclients.BTclient;
import com.google.common.base.Strings;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;

/**
 *
 * @author glenn
 */
public class DbFunctions {
    public static Boolean addShow(String showname, JSONObject json, JSONObject jsonshort){
         try{
            StringBuilder sbsql = new StringBuilder();
            Class.forName("org.sqlite.JDBC");
            Connection c = DriverManager.getConnection(conManager.getdburl());
            sbsql.append("INSERT INTO TVSHOWS (SHOWNAME, JSONSHORT, JSONFULL) VALUES (")
                 .append("'").append(URLEncoder.encode(showname, "UTF-8")).append("', '").append(URLEncoder.encode(jsonshort.toString(), "UTF-8")).append("', '").append(URLEncoder.encode(json.toString(), "UTF-8") ).append("'")
                 .append(");");
            Statement stmt = c.createStatement();
            stmt.executeUpdate(sbsql.toString());
            stmt.close();
            c.close();
            addShowFeed(showname);
        } catch(Exception ex){
            ex.printStackTrace();
        }
        return false;
    }
    
    public static void addShowFeed(String showname){
        int id = 0;
        try{
            Class.forName("org.sqlite.JDBC");
            Connection connection = DriverManager.getConnection(conManager.getdburl());
            String qry = "SELECT ID FROM TVSHOWS WHERE SHOWNAME = '"+showname+"'";
            if(connection != null){
                try{
                    connection.setAutoCommit(false);
                    Statement stmt = connection.createStatement();
                    try{
                        ResultSet rs = stmt.executeQuery(qry);
                        if( rs.next() ) {
                            id = rs.getInt(1);
                        }
                        rs.close();
                    }catch (Exception ex) {

                    }
                    stmt.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                connection.close();
            }
        } catch(Exception ex){
            
        }
        if(id != 0){
            try{
                StringBuilder sbsql = new StringBuilder();
                Class.forName("org.sqlite.JDBC");
                Connection c = DriverManager.getConnection(conManager.getdburl());
                sbsql.append("INSERT INTO PROVIDERFEEDS (SHOWID) VALUES (")
                     .append(id)
                     .append(");");
                Statement stmt = c.createStatement();
                stmt.executeUpdate(sbsql.toString());
                stmt.close();
                c.close();
            } catch(Exception ex){
                ex.printStackTrace();
            }
        }
    }
    
    public static void updateTvdbInfo(int showid, JSONObject json){
        try{
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            StringBuilder sbsql = new StringBuilder();
            Class.forName("org.sqlite.JDBC");
            Connection c = DriverManager.getConnection(conManager.getdburl());
            sbsql.append("UPDATE TVSHOWS set JSONFULL = '").append(URLEncoder.encode(json.toString(), "UTF-8")).append("'")
                 .append(" WHERE ID = ").append(showid).append(";");
            Statement stmt = c.createStatement();
            stmt.executeUpdate(sbsql.toString());
            stmt.close();
            c.close();
        } catch(Exception ex){
            ex.printStackTrace();
        }
    }
    
    public static void updateProviderFeed(int showid, String feedName, String feed){
        try{
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            StringBuilder sbsql = new StringBuilder();
            Class.forName("org.sqlite.JDBC");
            Connection c = DriverManager.getConnection(conManager.getdburl());
            sbsql.append("UPDATE PROVIDERFEEDS set ").append(feedName).append(" = '").append(feed).append("', ").append(feedName).append("DATE = '").append(sdf.format(date)).append("'")
                 .append(" WHERE SHOWID = ").append(showid).append(";");
            Statement stmt = c.createStatement();
            stmt.executeUpdate(sbsql.toString());
            stmt.close();
            c.close();
        } catch(Exception ex){
            ex.printStackTrace();
        }
    }
    
    public static List<String> getShowProviderfeed(int showid, String feedName){
        try{
            List<String> lstitems = null;
            Class.forName("org.sqlite.JDBC");
            Connection connection = DriverManager.getConnection(conManager.getdburl());
            StringBuilder sbqry = new StringBuilder();
            sbqry.append("SELECT ").append(feedName).append(", ").append(feedName).append("DATE")
                 .append(" FROM PROVIDERFEEDS ").append("WHERE SHOWID = ").append(showid);
            if(connection != null){
                try{
                    connection.setAutoCommit(false);
                    Statement stmt = connection.createStatement();
                    try{
                        ResultSet rs = stmt.executeQuery(sbqry.toString());
                        if( rs.next() ) {
                            String sqlfeed = rs.getString(1);
                            String datetime = rs.getString(2);
                            if(Strings.isNullOrEmpty(sqlfeed) == false){
                                lstitems = new ArrayList<>();
                                lstitems.add(sqlfeed);
                                lstitems.add(datetime);
                            }
                        }
                        rs.close();
                    }catch (Exception ex) {

                    }
                    stmt.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                connection.close();
                return lstitems;
            }
        } catch(Exception ex){
            
        }
        return null;
    }
    
    public static JSONArray getAllShows(){
        try{
            JSONArray ja = new JSONArray();
            Class.forName("org.sqlite.JDBC");
            Connection connection = DriverManager.getConnection(conManager.getdburl());
            String qry = "SELECT JSONFULL, ID FROM TVSHOWS ORDER BY SHOWNAME;";
            if(connection != null){
                try{
                    connection.setAutoCommit(false);
                    Statement stmt = connection.createStatement();
                    try{
                        ResultSet rs = stmt.executeQuery(qry);
                        while ( rs.next() ) {
                            String jsonstring = URLDecoder.decode(rs.getString(1), "UTF-8");
                            int id = rs.getInt(2);
                            JSONObject json = new JSONObject(jsonstring);
                            json.put("ID", id);
                            ja.put(json);
                        }
                        rs.close();
                    }catch (Exception ex) {

                    }
                    stmt.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                connection.close();
                return ja;
            }
            
        } catch(Exception ex){
            
        }
        return null;
    }
    
    public static JSONArray getTLDRshows(){
        try{
            JSONArray ja = new JSONArray();
            Class.forName("org.sqlite.JDBC");
            Connection connection = DriverManager.getConnection(conManager.getdburl());
            String qry = "SELECT ID, JSONSHORT FROM TVSHOWS ORDER BY SHOWNAME;";
            if(connection != null){
                try{
                    connection.setAutoCommit(false);
                    Statement stmt = connection.createStatement();
                    try{
                        ResultSet rs = stmt.executeQuery(qry);
                        while ( rs.next() ) {
                            String jsonstring = URLDecoder.decode(rs.getString(2), "UTF-8");
                            JSONObject json = new JSONObject(jsonstring);
                            json.put("ID", rs.getInt(1));
                            ja.put(json);
                        }
                        rs.close();
                    }catch (Exception ex) {

                    }
                    stmt.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                connection.close();
                return ja;
            }
            
        } catch(Exception ex){
            
        }
        return null;
    }
    
    public static JSONObject getTLDRshow(int showid){
        try{
            Class.forName("org.sqlite.JDBC");
            Connection connection = DriverManager.getConnection(conManager.getdburl());
            String qry = "SELECT JSONSHORT FROM TVSHOWS WHERE ID = " +showid+ " ;";
            JSONObject json = null;
            if(connection != null){
                try{
                    connection.setAutoCommit(false);
                    Statement stmt = connection.createStatement();
                    try{
                        ResultSet rs = stmt.executeQuery(qry);
                        while ( rs.next() ) {
                            String jsonstring = URLDecoder.decode(rs.getString(1), "UTF-8");
                            json = new JSONObject(jsonstring);
                        }
                        rs.close();
                    }catch (Exception ex) {

                    }
                    stmt.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                connection.close();
                return json;
            }
            
        } catch(Exception ex){
            
        }
        return null;
    }
    
    public static void updateTLDRshows(int showid){
        try{
            Class.forName("org.sqlite.JDBC");
            Connection connection = DriverManager.getConnection(conManager.getdburl());
            String qry = "SELECT JSONSHORT, JSONFULL FROM TVSHOWS WHERE ID = "+showid+";";
            JSONObject jsonshort = null;
            JSONObject jsonFull = null;
            if(connection != null){
                try{
                    connection.setAutoCommit(false);
                    Statement stmt = connection.createStatement();
                    try{
                        ResultSet rs = stmt.executeQuery(qry);
                        if(rs.next()) {
                            String jsonstring = URLDecoder.decode(rs.getString(1), "UTF-8");
                            jsonshort = new JSONObject(jsonstring);
                            String jsonfullstring = URLDecoder.decode(rs.getString(2), "UTF-8");
                            jsonFull = new JSONObject(jsonfullstring);
                        }
                        rs.close();
                    }catch (Exception ex) {

                    }
                    stmt.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                connection.close();
            }
            if(jsonshort != null && jsonFull != null){
                JSONObject jsonepisodestatus = jsonFull.getJSONObject("tvdbinfo").getJSONObject("EpisodeStatus");
                int EpisodeCount = 0;
                int EpisodesIgnored = 0;
                int EpisodesWanted = 0;
                int EpisodesDownloaded = 0;
                
                Iterator<?> keys = jsonepisodestatus.keys();
                while(keys.hasNext()){
                    String key = (String)keys.next();
                    EpisodeCount++;
                    int status = jsonepisodestatus.getInt(key);
                    if(status == 0){
                        EpisodesIgnored++;
                    } else if(status == 1){
                        EpisodesWanted++;
                    } else if(status == 2){
                        EpisodesDownloaded++;
                    }
                }
                jsonshort.put("EpisodeCount", EpisodeCount);
                jsonshort.put("EpisodesIgnored", EpisodesIgnored);
                jsonshort.put("EpisodesWanted", EpisodesWanted);
                jsonshort.put("EpisodesDownloaded", EpisodesDownloaded);
                StringBuilder sbsql = new StringBuilder();
                sbsql.append("UPDATE TVSHOWS SET JSONSHORT = '").append(URLDecoder.decode(jsonshort.toString(),"UTF-8")).append("' WHERE ID = "+showid+";");
                Connection c = DriverManager.getConnection(conManager.getdburl());
                Statement stmt = c.createStatement();
                stmt.executeUpdate(sbsql.toString());
                stmt.close();
                c.close();
            }
        } catch(Exception ex){
            
        }
    }
    
    public static JSONObject getFullShowObject(int showid){
        try{
            JSONArray ja = new JSONArray();
            Class.forName("org.sqlite.JDBC");
            Connection connection = DriverManager.getConnection(conManager.getdburl());
            String qry = "SELECT JSONFULL FROM TVSHOWS WHERE ID = "+showid+";";
            JSONObject json = null;
            if(connection != null){
                try{
                    connection.setAutoCommit(false);
                    Statement stmt = connection.createStatement();
                    try{
                        ResultSet rs = stmt.executeQuery(qry);
                        if(rs.next()) {
                            String jsonstring = URLDecoder.decode(rs.getString(1), "UTF-8");
                            json = new JSONObject(jsonstring);
                        }
                        rs.close();
                    }catch (Exception ex) {

                    }
                    stmt.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                connection.close();
                return json;
            }
            
        } catch(Exception ex){
            
        }
        return null;
    }
    
    public static JSONObject countStatus(){
        try{
            Class.forName("org.sqlite.JDBC");
            Connection connection = DriverManager.getConnection(conManager.getdburl());
            String qry = "SELECT count(*) FROM REQUESTDOWNLOAD WHERE STATUS = 2;";
            JSONObject json = new JSONObject();
            if(connection != null){
                try{
                    connection.setAutoCommit(false);
                    Statement stmt = connection.createStatement();
                    try{
                        ResultSet rs = stmt.executeQuery(qry);
                        while ( rs.next() ) {
                            json.put("Downloads", rs.getInt(1));
                        }
                        rs.close();
                    }catch (Exception ex) {

                    }
                    stmt.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                connection.close();
            }
            connection = DriverManager.getConnection(conManager.getdburl());
            qry = "SELECT count(*) FROM LOG WHERE STATUS = 3;";
            if(connection != null){
                try{
                    connection.setAutoCommit(false);
                    Statement stmt = connection.createStatement();
                    try{
                        ResultSet rs = stmt.executeQuery(qry);
                        while ( rs.next() ) {
                            json.put("Errors", rs.getInt(1));
                        }
                        rs.close();
                    }catch (Exception ex) {

                    }
                    stmt.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                connection.close();
                return json;
            }
        } catch(Exception ex){
            
        }
        return null;
    }
    
    public static Boolean addNotifications(int status, String message){
        try{
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            StringBuilder sbsql = new StringBuilder();
            Class.forName("org.sqlite.JDBC");
            Connection c = DriverManager.getConnection(conManager.getdburl());
            sbsql.append("INSERT INTO LOG (STATUS, MESSAGE, DATE) VALUES (")
                 .append(status).append(", '").append(message).append("', '").append(sdf.format(new Date())).append("'")
                 .append(");");

            Statement stmt = c.createStatement();
            stmt.executeUpdate(sbsql.toString());
            stmt.close();
            c.close();
        } catch(Exception ex){
            ex.printStackTrace();
        }
        return false;
    }
    
    public static JSONArray getNotifications(int status, int limit){
        try{
            SimpleDateFormat sdffrom = new SimpleDateFormat("yyyyMMddHHmmss");
            SimpleDateFormat sdfto = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            Class.forName("org.sqlite.JDBC");
            Connection connection = DriverManager.getConnection(conManager.getdburl());
            StringBuilder sbqry = new StringBuilder();
            sbqry.append("SELECT STATUS, MESSAGE, DATE FROM LOG");
            if(status != 0){
                sbqry.append(" WHERE STATUS = ").append(status);
            }
            sbqry.append(" ORDER BY ID DESC");
            if(limit != 0){
                sbqry.append(" LIMIT ").append(limit);
            }
            
            JSONArray ja = new JSONArray();
            if(connection != null){
                try{
                    connection.setAutoCommit(false);
                    Statement stmt = connection.createStatement();
                    try{
                        ResultSet rs = stmt.executeQuery(sbqry.toString());
                        while ( rs.next() ) {
                            JSONObject json = new JSONObject();
                            json.put("STATUS", rs.getInt(1));
                            json.put("MESSAGE", rs.getString(2));
                            String datestring = rs.getString(3);
                            Date date = sdffrom.parse(datestring);
                            json.put("DATE", sdfto.format(date));
                            ja.put(json);
                        }
                        rs.close();
                    }catch (Exception ex) {

                    }
                    stmt.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                connection.close();
            }
            return ja;
        } catch(Exception ex){
            
        }
        return null;
    }
    
    public static boolean removeNotifaction(){
        try{
            Class.forName("org.sqlite.JDBC");
            Connection c = DriverManager.getConnection(conManager.getdburl());
            StringBuilder sbsql = new StringBuilder();
            sbsql.append("DELETE FROM LOG;");
            Statement stmt = c.createStatement();
            stmt.executeUpdate(sbsql.toString());
            stmt.close();
            c.close();
            return true;
        }
        catch(Exception ex){

        }
        return false;
    }
    
    public static void updateBittorrentclient(JSONObject json){
        BTclient.clearjsonsettings();
        try{
            Class.forName("org.sqlite.JDBC");
            Connection c = DriverManager.getConnection(conManager.getdburl());
            StringBuilder sbsql = new StringBuilder();
            sbsql.append("UPDATE SETTINGS SET VALUE = '").append(json.getString("btclient")).append("' WHERE key = 'btclient';");
            sbsql.append("UPDATE SETTINGS SET VALUE = '").append(json.getString("bturl")).append("' WHERE key = 'bturl';");
            sbsql.append("UPDATE SETTINGS SET VALUE = '").append(json.getString("btpath")).append("' WHERE key = 'btpath';");
            sbsql.append("UPDATE SETTINGS SET VALUE = '").append(json.getString("MoveTo")).append("' WHERE key = 'MoveTo';");
            sbsql.append("UPDATE SETTINGS SET VALUE = '").append(json.getString("btignorewords")).append("' WHERE key = 'btignorewords';");
            Statement stmt = c.createStatement();
            stmt.executeUpdate(sbsql.toString());
            stmt.close();
            c.close();
        }
        catch(Exception ex){

        }
    }
    
    public static JSONObject getBittorrentclient(){
        try{
            Class.forName("org.sqlite.JDBC");
            Connection connection = DriverManager.getConnection(conManager.getdburl());
            String qry = "SELECT KEY, VALUE FROM SETTINGS WHERE KEY = 'btclient' OR KEY = 'bturl' OR KEY = 'btpath' OR KEY = 'MoveTo' OR KEY = 'btignorewords';";
            JSONObject json = new JSONObject();
            if(connection != null){
                try{
                    connection.setAutoCommit(false);
                    Statement stmt = connection.createStatement();
                    try{
                        ResultSet rs = stmt.executeQuery(qry);
                        while ( rs.next() ) {
                            json.put(rs.getString(1), rs.getString(2));
                        }
                        rs.close();
                    }catch (Exception ex) {

                    }
                    stmt.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                connection.close();
                return json;
            }
        } catch(Exception ex){
            ex.printStackTrace();
        }
        return null;
    }
    
    public static void addRequestdownload(int showid, String searchparam, String quality, boolean redo){
        int status = -1;
        int id = 0;
        try{
            Class.forName("org.sqlite.JDBC");
            Connection connection = DriverManager.getConnection(conManager.getdburl());
            StringBuilder qry = new StringBuilder();
            qry.append("SELECT ID, STATUS FROM REQUESTDOWNLOAD WHERE REQUEST = '").append(showid).append(".").append(searchparam).append("'").append(";");
            if(connection != null){
                try{
                    connection.setAutoCommit(false);
                    Statement stmt = connection.createStatement();
                    try{
                        ResultSet rs = stmt.executeQuery(qry.toString());
                        if(rs.next()) {
                            id = rs.getInt(1);
                            status = rs.getInt(2);
                        }
                        rs.close();
                    }catch (Exception ex) {

                    }
                    stmt.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                connection.close();
            }
        } catch(Exception ex){
            ex.printStackTrace();
        }
        if(status > -1){
            if(redo){
                updateRequestdownload(id, 0);
            }
        } else {
            String showname = null;
            String filename = null;
            try{
                JSONObject jsonFULL = DbFunctions.getFullShowObject(showid);
                System.out.println(jsonFULL.toString());
                showname = jsonFULL.getJSONObject("tvdbinfo").getString("Showname") + " " + searchparam;
                filename = jsonFULL.getJSONObject("tvdbinfo").getJSONObject("SearchAndFileLink").getString(searchparam);
            } catch (Exception ex){
                
            }
            if(Strings.isNullOrEmpty(showname) == false && Strings.isNullOrEmpty(filename) == false){
                try{
                    Class.forName("org.sqlite.JDBC");
                    Connection c = DriverManager.getConnection(conManager.getdburl());
                    JSONObject json = new JSONObject();
                    json.put("showid", showid);
                    json.put("searchparam", searchparam);
                    json.put("quality", quality);
                    StringBuilder sbsql = new StringBuilder();
                    sbsql.append("INSERT INTO REQUESTDOWNLOAD (STATUS, JSON, REQUEST, FILESEARCH, FILENAME) VALUES (")
                     .append(0).append(", '").append(json.toString()).append("', ")
                     .append("'").append(showid).append(".").append(searchparam).append("',")
                     .append("'").append(URLEncoder.encode(showname, "UTF-8")).append("',")
                     .append("'").append(URLEncoder.encode(filename, "UTF-8")).append("'")
                     .append(");");
                    Statement stmt = c.createStatement();
                    stmt.executeUpdate(sbsql.toString());
                    stmt.close();
                    c.close();
                }
                catch(Exception ex){

                }
            }
        }
    }
    
    public static void updateRequestdownload(int id, int status){
        try{
            Class.forName("org.sqlite.JDBC");
            Connection c = DriverManager.getConnection(conManager.getdburl());
            StringBuilder sbsql = new StringBuilder();
            sbsql.append("UPDATE REQUESTDOWNLOAD SET STATUS = ").append(status).append(" WHERE ID = ").append(id).append(";");
            Statement stmt = c.createStatement();
            stmt.executeUpdate(sbsql.toString());
            stmt.close();
            c.close();
        }
        catch(Exception ex){

        }
    }
    
    public static void deleteRequestdownload(int status){
        try{
            Class.forName("org.sqlite.JDBC");
            Connection c = DriverManager.getConnection(conManager.getdburl());
            StringBuilder sbsql = new StringBuilder();
            sbsql.append("DELETE FROM REQUESTDOWNLOAD WHERE STATUS = ").append(status).append(";");
            Statement stmt = c.createStatement();
            stmt.executeUpdate(sbsql.toString());
            stmt.close();
            c.close();
        }
        catch(Exception ex){

        }
    }
    
    public static void deleteRequestdownloadID(int id){
        try{
            Class.forName("org.sqlite.JDBC");
            Connection c = DriverManager.getConnection(conManager.getdburl());
            StringBuilder sbsql = new StringBuilder();
            sbsql.append("DELETE FROM REQUESTDOWNLOAD WHERE ID = ").append(id).append(";");
            Statement stmt = c.createStatement();
            stmt.executeUpdate(sbsql.toString());
            stmt.close();
            c.close();
        }
        catch(Exception ex){

        }
    }
    
    public static JSONArray getRequestdownloads(int status){
        try{
            Class.forName("org.sqlite.JDBC");
            Connection connection = DriverManager.getConnection(conManager.getdburl());
            String qry = "SELECT JSON, ID, FILESEARCH, FILENAME FROM REQUESTDOWNLOAD WHERE status = " + status +";";
            JSONArray ja = new JSONArray();
            if(connection != null){
                try{
                    connection.setAutoCommit(false);
                    Statement stmt = connection.createStatement();
                    try{
                        ResultSet rs = stmt.executeQuery(qry);
                        while ( rs.next() ) {
                            JSONObject json = new JSONObject(rs.getString(1));
                            json.put("ID", rs.getInt(2));
                            json.put("FILESEARCH", URLDecoder.decode(rs.getString(3), "UTF-8"));
                            json.put("FILENAME", URLDecoder.decode(rs.getString(4), "UTF-8"));
                            ja.put(json);
                        }
                        rs.close();
                    }catch (Exception ex) {

                    }
                    stmt.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                connection.close();
                return ja;
            }
        } catch(Exception ex){
            ex.printStackTrace();
        }
        return null;
    }
    
    public static JSONArray getAllRequestdownloads(){
        try{
            Class.forName("org.sqlite.JDBC");
            Connection connection = DriverManager.getConnection(conManager.getdburl());
            String qry = "SELECT JSON, ID, FILESEARCH, FILENAME, STATUS FROM REQUESTDOWNLOAD;";
            JSONArray ja = new JSONArray();
            if(connection != null){
                try{
                    connection.setAutoCommit(false);
                    Statement stmt = connection.createStatement();
                    try{
                        ResultSet rs = stmt.executeQuery(qry);
                        while ( rs.next() ) {
                            JSONObject json = new JSONObject(rs.getString(1));
                            json.put("ID", rs.getInt(2));
                            json.put("FILESEARCH", URLDecoder.decode(rs.getString(3), "UTF-8"));
                            json.put("FILENAME", URLDecoder.decode(rs.getString(4), "UTF-8"));
                            json.put("STATUS", rs.getInt(5));
                            ja.put(json);
                        }
                        rs.close();
                    }catch (Exception ex) {

                    }
                    stmt.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                connection.close();
                return ja;
            }
        } catch(Exception ex){
            ex.printStackTrace();
        }
        return null;
    }
    
    public static void deleteshow(int showid){
        try{
            Class.forName("org.sqlite.JDBC");
            Connection c = DriverManager.getConnection(conManager.getdburl());
            StringBuilder sbsql = new StringBuilder();
            sbsql.append("DELETE FROM REQUESTDOWNLOAD WHERE REQUEST LIKE '").append(showid).append(".%").append("';");
            Statement stmt = c.createStatement();
            stmt.executeUpdate(sbsql.toString());
            stmt.close();
            c.close();
        }
        catch(Exception ex){

        }
        try{
            Class.forName("org.sqlite.JDBC");
            Connection c = DriverManager.getConnection(conManager.getdburl());
            StringBuilder sbsql = new StringBuilder();
            sbsql.append("DELETE FROM TVSHOWS WHERE ID = ").append(showid).append(";");
            Statement stmt = c.createStatement();
            stmt.executeUpdate(sbsql.toString());
            stmt.close();
            c.close();
        }
        catch(Exception ex){

        }
    }
}
