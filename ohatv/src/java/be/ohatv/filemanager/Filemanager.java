package be.ohatv.filemanager;

import be.ohatv.sqlite.DbFunctions;
import com.google.common.base.Strings;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;

public class Filemanager {
    
    private static final int pcsystem = 2; //1 for linux and mac, 2 for windows 
    
    public static void getAllVideoFiles(){
        try{
            List<File> lstfiles = new ArrayList<>();
            JSONObject json = DbFunctions.getBittorrentclient();
            JSONArray ja = DbFunctions.getRequestdownloads(1);
            if(json != null && ja != null){
                if(json.has("btpath")){
                    lookintofiles(json.getString("btpath"), lstfiles);
                    File filetoremove = null;
                    for(int i = 0; i < ja.length(); i++){
                        JSONObject jrequest = ja.getJSONObject(i);
                        JSONObject jsonshort = DbFunctions.getTLDRshow(jrequest.getInt("showid"));
                        String filesearch = jrequest.getString("FILESEARCH");
                        String filename = URLDecoder.decode(jrequest.getString("FILENAME"),"UTF8");
                        String[] strwords = filesearch.split(" ");
                        for(File f : lstfiles){
                            int countwords = 0;
                            for(String word : strwords){
                                String name = f.getName().toLowerCase().replaceAll("\\(", "").replaceAll("\\)", "");
                                if(name.contains(word.toLowerCase())){
                                    countwords++;
                                } else {
                                    if(word.contains("'s")){
                                        word = word.replace("'s", "s");
                                        if(name.toLowerCase().contains(word.toLowerCase())){
                                            countwords++;
                                        }
                                    }
                                }
                            }
                            if(countwords == strwords.length){
                                String extension = "";
                                if(f.getName().endsWith(".mp4")){
                                    extension = ".mp4";
                                } else if(f.getName().endsWith(".mkv")){
                                    extension = ".mkv";
                                } else if(f.getName().endsWith(".avi")){
                                    extension = ".avi";
                                }
                                StringBuilder sbfilename = new StringBuilder();
                                sbfilename.append(filename).append(extension);
                                Boolean successMove = false;
                                if(json.has("MoveTo")){
                                    StringBuilder path = new StringBuilder();
                                    String season = jrequest.getString("searchparam").split("E")[0];
                                    season = season.replaceAll("S", "");
                                    int seasonnr = Integer.parseInt(season);
                                    if(pcsystem == 1){
                                         path.append(json.getString("MoveTo")).append("/").append(jsonshort.getString("Showname")).append("/").append("Season ").append(seasonnr);
                                    } else if(pcsystem == 2){
                                         path.append(json.getString("MoveTo")).append("\\").append(jsonshort.getString("Showname")).append("\\").append("Season ").append(seasonnr);
                                    }
                                    successMove = MoveFile(f.getAbsolutePath(), path.toString(), sbfilename.toString());
                                }else{
                                    successMove = MoveFile(f.getAbsolutePath(), f.getAbsolutePath(), sbfilename.toString());
                                }
                                if(successMove){
                                    DbFunctions.addNotifications(2, "Finished \"" + filename + "\"");
                                    DbFunctions.updateRequestdownload(jrequest.getInt("ID"), 2);
                                    filetoremove = f;
                                }
                                break;
                            }
                        }
                        lstfiles.remove(filetoremove);
                    }
                }
            }  
        }
        catch(Exception ex){
            DbFunctions.addNotifications(3, "Error renaming files");
            ex.printStackTrace();
        }
    }
    
    private static void lookintofiles(String path, List<File> lstfiles){
        try{
            File folder = new File(path);
            File[] listOfFiles = folder.listFiles();
            for (File listOfFile : listOfFiles) {
                if (listOfFile.isFile()) {
                    String filename = listOfFile.getName().toLowerCase();
                    if ((filename.endsWith(".mp4") || filename.endsWith(".mkv") || filename.endsWith(".avi")) && filename.toLowerCase().contains("sample") == false) {
                        lstfiles.add(listOfFile);
                    }
                } else if (listOfFile.isDirectory()) {
                    lookintofiles(listOfFile.getAbsolutePath(), lstfiles);
                }
            }
        } catch (Exception ex){
            DbFunctions.addNotifications(3, "Can't look in the download folder");
            ex.printStackTrace();
        }
    }
    
    
    private static Boolean MoveFile(String startlocation, String endlocation, String rename)
    {	
    	try{
            File afile = new File(startlocation);
            if(Strings.isNullOrEmpty(rename)){
                rename = afile.getName();
            }
            
            File direndlocation = new File(endlocation);
            if(direndlocation.exists() == false){
                try{
                    direndlocation.mkdirs();
                } catch(Exception ex){
                    ex.printStackTrace();
                }
            }
            
            if(pcsystem == 1){
                if(afile.renameTo(new File(endlocation + "/" + rename))){
                    DbFunctions.addNotifications(6, "File " + rename + " has been moved");
                    return true;
                }else{
                    DbFunctions.addNotifications(3, "Failed to move " + rename);
                    return false;
                }
            } else if(pcsystem == 2) {
                if(afile.renameTo(new File(endlocation + "\\" + rename))){
                    DbFunctions.addNotifications(6, "File " + rename + " has been moved");
                    return true;
                }else{
                    DbFunctions.addNotifications(3, "Failed to move " + rename);
                    return false;
                }
            }
    	}catch(Exception e){
                DbFunctions.addNotifications(3, "Can't move files, maybe permission problem?");
    		e.printStackTrace();
    	}
        return false;
    }
    
    public static void copyanddelete()
    {	
    	InputStream inStream = null;
	OutputStream outStream = null;
    	try{
    	    File afile =new File("C:\\folderA\\Afile.txt");
    	    File bfile =new File("C:\\folderB\\Afile.txt");
    	    inStream = new FileInputStream(afile);
    	    outStream = new FileOutputStream(bfile);
    	    byte[] buffer = new byte[1024];
    	    int length;
    	    //copy the file content in bytes 
    	    while ((length = inStream.read(buffer)) > 0){
                outStream.write(buffer, 0, length);
    	    }
    	    inStream.close();
    	    outStream.close();
    	    //delete the original file
    	    afile.delete();
    	}catch(IOException e){
    	    e.printStackTrace();
    	}
    }
    
    public static JSONObject linkmanualy(int showid){
        try{
            JSONObject jsonlinks = new JSONObject();
            JSONObject jsonfull = DbFunctions.getFullShowObject(showid);
            JSONObject jsontvdb = jsonfull.getJSONObject("tvdbinfo");
            JSONObject SearchAndFileLink = jsontvdb.getJSONObject("SearchAndFileLink");
            String Showname = jsontvdb.getString("Showname");
            String[] strwords = Showname.split(" ");

            List<File> lstfiles = new ArrayList<>();
            JSONObject json = DbFunctions.getBittorrentclient();
            lookintofiles(json.getString("btpath"), lstfiles);
            
            for(File f : lstfiles){
                int counter = 0;
                for(String s: strwords){
                    if(f.getName().toLowerCase().contains(s.toLowerCase())){
                        counter++;
                    }else{
                        break;
                    }
                }                
                if(counter == strwords.length){
                    Iterator it = SearchAndFileLink.keys();
                    while(it.hasNext()){
                        String key = (String)it.next();
                        if(f.getName().toLowerCase().contains(key.toLowerCase())){
                            jsonlinks.put(f.getPath(), SearchAndFileLink.getString(key));
                            break;
                        }
                        else{
                            StringBuilder sbkey = new StringBuilder();
                            sbkey.append(key.split("E")[0].replace("S", "").replace("0", "")).append(key.split("E")[1]); 
                            if(f.getName().toLowerCase().contains(sbkey.toString().toLowerCase())){
                                jsonlinks.put(f.getPath(), SearchAndFileLink.getString(key));
                                break;
                            }
                            else{
                                sbkey = new StringBuilder();
                                sbkey.append(key.split("E")[0].replace("S", "").replace("0", "")).append("x").append(key.split("E")[1]); 
                                if(f.getName().toLowerCase().contains(sbkey.toString().toLowerCase())){
                                    jsonlinks.put(f.getPath(), SearchAndFileLink.getString(key));
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            return jsonlinks; 
        } catch(Exception ex){
            
        }
        return null;
    }
    
    public static JSONObject getAllFiles(int showid){
        try{
            JSONObject jsonlinks = new JSONObject();
            JSONObject jsonfull = DbFunctions.getFullShowObject(showid);
            JSONObject jsontvdb = jsonfull.getJSONObject("tvdbinfo");
            JSONObject SearchAndFileLink = jsontvdb.getJSONObject("SearchAndFileLink");
            jsonlinks.put("SearchAndFileLink", SearchAndFileLink);
            String Showname = jsontvdb.getString("Showname");
            String[] strwords = Showname.split(" ");

            List<File> lstfiles = new ArrayList<>();
            JSONObject json = DbFunctions.getBittorrentclient();
            lookintofiles(json.getString("btpath"), lstfiles);
            
            JSONArray ja = new JSONArray();
            if(lstfiles.size() > 0){
                for(File f : lstfiles){
                    JSONObject jsonFiles = new JSONObject();
                    jsonFiles.put("FILENAME", f.getName());
                    jsonFiles.put("FILEPATH", f.getAbsolutePath());
                    ja.put(jsonFiles);
                }
            }
            jsonlinks.put("FILES", ja);
            return jsonlinks; 
        } catch(Exception ex){
            
        }
        return null;
    }
    
    public static boolean linkFile(int showid, String SeasonEpisodeNumber, String ShowfileName, String filepath){
        try{
            JSONObject json = DbFunctions.getBittorrentclient();
            JSONObject jsonshort = DbFunctions.getTLDRshow(showid);
            String season = SeasonEpisodeNumber.split("E")[0];
            season = season.replaceAll("S", "");
            int seasonnr = Integer.parseInt(season);
            StringBuilder path = new StringBuilder();
            if(pcsystem == 1){
                path.append(json.getString("MoveTo")).append("/").append(jsonshort.getString("Showname")).append("/").append("Season ").append(seasonnr);
            } else if(pcsystem == 2){
                path.append(json.getString("MoveTo")).append("\\").append(jsonshort.getString("Showname")).append("\\").append("Season ").append(seasonnr);
            }
            if(filepath.endsWith(".mp4")){
                ShowfileName += ".mp4";
            } else if(filepath.endsWith(".mkv")){
                ShowfileName += ".mkv";
            } else if(filepath.endsWith(".avi")){
                ShowfileName += ".avi";
            }
            return MoveFile(filepath, path.toString(), ShowfileName);
        } catch(Exception ex) {
            
        }
        return false;
    }

    public static int getPcsystem() {
        return pcsystem;
    }
}
