/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ohatv.searchproviders;

import be.ohatv.sqlite.DbFunctions;
import com.google.common.base.Strings;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 *
 * @author glenn
 */
public class Showrss {
    //private static String showrssurl = "http://showrss.info/feeds/1014.rss"
    private static String showrssurl = "http://showrss.info/feeds/<showid>.rss";

    
    private static List<String> GrapXmlPage(int showid, int showrssid){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        List<String> lstitems = DbFunctions.getShowProviderfeed(showid, "SHOWRSS");
        List<String> lst = new ArrayList<>();
        try{
            if(lstitems != null){
                if(lstitems.size() == 2){
                    String feed = lstitems.get(0);
                    String date = lstitems.get(1);
                    Date datesql = sdf.parse(date);
                    Date now = new Date();
                    if(datesql.getTime() + 1800000 > now.getTime()){
                        String[] arg = feed.split("/SPLIT/");
                        for(String magnet : arg){
                            lst.add(magnet);
                        }
                        if(lst.size() > 0){
                            return lst;
                        }
                    }
                }
            }
        } catch(Exception ex){
            
        }
        
        try{
            String showrssxml = Webclient.sendGet(showrssurl.replaceAll("<showid>", String.valueOf(showrssid)));
            if(Strings.isNullOrEmpty(showrssxml) == false){
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                InputSource is = new InputSource();
                is.setCharacterStream(new StringReader(showrssxml));
                Document doc = db.parse(is);
                NodeList nList = doc.getElementsByTagName("item");
                for (int temp = 0; temp < nList.getLength(); temp++) {
                    Node nNode = nList.item(temp);
                    if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element eElement = (Element) nNode;
                        //String title = eElement.getElementsByTagName("title").item(0).getTextContent();
                        String link = eElement.getElementsByTagName("link").item(0).getTextContent();
                        lst.add(link);
                    }
                }
            }
            if(lst.size() > 0){
                StringBuilder sbez = new StringBuilder();
                boolean isfirst = true;
                for(String magnet : lst){
                    if(isfirst == false){
                        sbez.append("/SPLIT/");
                    }
                    else{
                        isfirst = false;
                    }
                    sbez.append(magnet);
                }
                DbFunctions.updateProviderFeed(showid, "SHOWRSS", sbez.toString());
                return lst;
            }
        } catch(Exception ex){

        }
        return null;
    }
    
    public static String searchShowRss(int showid, int showrssid, String episodeneeded, String quality){
        List<String> lstarry = GrapXmlPage(showid, showrssid);
        if(lstarry != null){
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
        }        
        return null;
    }
    
}
