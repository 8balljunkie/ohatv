/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ohatv.btclients;

import be.ohatv.sqlite.DbFunctions;
import com.google.common.base.Strings;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.codehaus.jettison.json.JSONObject;

/**
 *
 * @author glenn
 */
public class qBittorrent {

    //https://github.com/qbittorrent/qBittorrent/wiki/WebUI-API-Documentation

    private static String authorizationheader = null;

    public static boolean SendMagnet() {//String url){
        try {
            String url = "http://localhost:8086/command/download";
            String magnet = "magnet:?xt=urn:btih:BDC1B443696D3E68DB59933FDADD085F822EDA06&dn=tyrese+black+rose+2015+l+audio+l+albumtrack+l+320kbps+l+cbr+l+mp3+l+sn3h1t87&tr=udp%3A%2F%2Ftracker.openbittorrent.com%3A80%2Fannounce&tr=udp%3A%2F%2Fopen.demonii.com%3A1337";
            JSONObject arg = new JSONObject();
            arg.put("urls", magnet);
            JSONObject json = new JSONObject();

            String data = arg.toString();
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("User-Agent", "Mozilla/5.0");
            con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            con.setRequestProperty("auth", "HTTPDigestAuth(admin, abcd)");
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(data);
            wr.flush();
            wr.close();
            int responseCode = con.getResponseCode();
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
            }
            in.close();
            if(responseCode == 200){
                return true;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

}
