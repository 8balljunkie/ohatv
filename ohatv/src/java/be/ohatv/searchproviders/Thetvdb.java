package be.ohatv.searchproviders;

import com.google.common.base.Strings;
import java.io.StringReader;
import java.net.URLEncoder;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class Thetvdb {

    private static final String tvdburlsearchshow = "http://www.thetvdb.com/api/GetSeries.php?language=en&seriesname=";
    private static final String tvdburl = "http://thetvdb.com/api/A17D3473F4E68E1B/series/<showid>/all/en.xml";  //example http://thetvdb.com/api/A17D3473F4E68E1B/series/272644/all/en.xml"
    

    public static JSONArray searchshow(String param){
        try{
            StringBuilder sburl = new StringBuilder();
            sburl.append(tvdburlsearchshow).append(URLEncoder.encode(param, "UTF-8"));
            String xml = Webclient.sendGet(sburl.toString());
            if(Strings.isNullOrEmpty(xml) == false){
                JSONArray ja = new JSONArray();
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                InputSource is = new InputSource();
                is.setCharacterStream(new StringReader(xml));
                Document doc = db.parse(is);
                NodeList nList = doc.getElementsByTagName("Series");
                for (int temp = 0; temp < nList.getLength(); temp++) {
                    JSONObject jsonShow = new JSONObject();
                    Node nNode = nList.item(temp);
                    if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                        try{
                            Element eElement = (Element) nNode;
                            String id = eElement.getElementsByTagName("id").item(0).getTextContent();
                            String showname = eElement.getElementsByTagName("SeriesName").item(0).getTextContent();
                            String firstaired = eElement.getElementsByTagName("FirstAired").item(0).getTextContent();
                            String Network = eElement.getElementsByTagName("Network").item(0).getTextContent();
                            String IMDBID = eElement.getElementsByTagName("IMDB_ID").item(0).getTextContent();
                            jsonShow.put("thetvdbid", id);
                            jsonShow.put("Showname", showname);
                            jsonShow.put("firstaired", firstaired);
                            jsonShow.put("Network", Network);
                            jsonShow.put("IMDBID", IMDBID);
                            ja.put(jsonShow);
                        } catch(Exception ex){
                            
                        }
                    }
                }
                return ja;
            }
        } catch(Exception ex){
            ex.printStackTrace();
        }
        return null;
    }
    
    public static JSONObject getTvDBinfo(int tvdbshowid) {
        try {
            JSONObject jsonShow = new JSONObject();
            String xml = Webclient.sendGet(tvdburl.replaceAll("<showid>", String.valueOf(tvdbshowid)));
            if (Strings.isNullOrEmpty(xml) == false) {
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                InputSource is = new InputSource();
                is.setCharacterStream(new StringReader(xml));
                Document doc = db.parse(is);
                NodeList nList = doc.getElementsByTagName("Series");
                for (int temp = 0; temp < nList.getLength(); temp++) {
                    Node nNode = nList.item(temp);
                    if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element eElement = (Element) nNode;
                        String id = eElement.getElementsByTagName("id").item(0).getTextContent();
                        String showname = eElement.getElementsByTagName("SeriesName").item(0).getTextContent();
                        String overview = eElement.getElementsByTagName("Overview").item(0).getTextContent();
                        String Status = eElement.getElementsByTagName("Status").item(0).getTextContent();
                        jsonShow.put("thetvdbid", id);
                        jsonShow.put("Showname", showname);
                        jsonShow.put("Overview", overview);
                        jsonShow.put("Status", Status);
                    }
                }
                int episodecount = 0;
                int seasoncount = 0;
                nList = doc.getElementsByTagName("Episode");
                JSONObject jsonseaonList = new JSONObject();
                JSONObject jsonEpisodeLink = new JSONObject();
                JSONObject jsonairdate = new JSONObject();
                JSONObject jsonepisodestatus = new JSONObject();
                for (int temp = 0; temp < nList.getLength(); temp++) {
                    Node nNode = nList.item(temp);
                    if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element eElement = (Element) nNode;
                        int Combined_season = Integer.parseInt(eElement.getElementsByTagName("Combined_season").item(0).getTextContent());
                        String EpisodeName = eElement.getElementsByTagName("EpisodeName").item(0).getTextContent();
                        int EpisodeNumber = Integer.parseInt(eElement.getElementsByTagName("EpisodeNumber").item(0).getTextContent());
                        String firstaired = eElement.getElementsByTagName("FirstAired").item(0).getTextContent();
                        StringBuilder sbfilename = new StringBuilder();
                        String seasonnumber = String.format("%02d", Combined_season);
                        String episodenumber = String.format("%02d", EpisodeNumber);
                        if (Combined_season != 0) {
                            JSONArray ja = new JSONArray();
                            if (jsonseaonList.has("Season " + Combined_season)) {
                                ja = jsonseaonList.getJSONArray("Season " + Combined_season);
                            } else {
                                seasoncount++;
                            }
                            sbfilename.append(jsonShow.getString("Showname")).append(" - S").append(seasonnumber).append("E").append(episodenumber).append(" - ").append(EpisodeName);
                            JSONObject json = new JSONObject();
                            json.put("Season", Combined_season);
                            json.put("EpisodeNumber", EpisodeNumber);
                            json.put("EpisodeName", EpisodeName);
                            json.put("FileName", sbfilename.toString());
                            json.put("AirDate", firstaired);
                            json.put("EpisodeStatus", 0); //0 ignored, 1 Wanted, 2 Snatched, 3 Downloaded

                            if (Combined_season <= 9) {
                                if (EpisodeNumber <= 9) {
                                    String episode = "S0" + Combined_season + "E0" + EpisodeNumber;
                                    jsonEpisodeLink.put(episode, sbfilename.toString());
                                    jsonairdate.put(episode, firstaired);
                                    jsonepisodestatus.put(episode, 0);
                                } else {
                                    String episode = "S0" + Combined_season + "E" + EpisodeNumber;
                                    jsonEpisodeLink.put(episode, sbfilename.toString());
                                    jsonairdate.put(episode, firstaired);
                                    jsonepisodestatus.put(episode, 0);
                                }
                            } else {
                                if (EpisodeNumber <= 9) {
                                    String episode = "S" + Combined_season + "E0" + EpisodeNumber;
                                    jsonEpisodeLink.put(episode, sbfilename.toString());
                                    jsonairdate.put(episode, firstaired);
                                    jsonepisodestatus.put(episode, 0);
                                } else {
                                    String episode = "S" + Combined_season + "E" + EpisodeNumber;
                                    jsonEpisodeLink.put(episode, sbfilename.toString());
                                    jsonairdate.put(episode, firstaired);
                                    jsonepisodestatus.put(episode, 0);
                                }
                            }
                            ja.put(json);
                            jsonseaonList.put("Season " + Combined_season, ja);
                            episodecount++;
                        }
                    }
                }
                jsonShow.put("EpisodeStatus", jsonepisodestatus);
                jsonShow.put("SearchAndFileLink", jsonEpisodeLink);
                jsonShow.put("EpisodeAirdates", jsonairdate);
                jsonShow.put("SeasonCount", seasoncount);
                jsonShow.put("EpisodeCount", episodecount);
                jsonShow.put("SeasonList", jsonseaonList);
                return jsonShow;
            }

        } catch (Exception ex) {

        }
        return null;
    }
}
