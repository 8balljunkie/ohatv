/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ohatv.btclients;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 *
 * @author glenn
 */
public class Blackhole {
    private static final String strikeurl = "https://getstrike.net/torrents/api/download/";
    
    public static void downloadfile(String torrenthash, String downloadlocation){
        try{
            downloadlocation = downloadlocation.replaceAll(" ", ".");
            BufferedInputStream bis = null;
            BufferedOutputStream bos = null;
            URL url = new URL(strikeurl + torrenthash + ".torrent");
            URLConnection urlConn = url.openConnection();
            urlConn.addRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1)");
            String contentType = urlConn.getContentType();
            InputStream is = urlConn.getInputStream();
            OutputStream outputStream = new FileOutputStream(new File(downloadlocation));
            int read = 0;
            byte[] bytes = new byte[1024];
            while ((read = is.read(bytes)) != -1) {
                    outputStream.write(bytes, 0, read);
            }
        } catch(Exception ex){
            ex.printStackTrace();
        }
        
    }
}
