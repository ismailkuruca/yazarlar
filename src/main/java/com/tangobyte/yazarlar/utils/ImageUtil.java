package com.tangobyte.yazarlar.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by erk on 08.04.2015.
 */
public class ImageUtil {

    public static void createImage(String image, String name, boolean resize) throws Exception{
        final String USER_AGENT = "Mozilla/5.0";

        String url = "http://erkspace.com/news/imageDownload.php?pass=abracadabra&name=" + name;

        if (resize) {url = url + "&rezize=true";}

        url = url + "&url=" + image;

        System.out.println("url = " + url);

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // optional default is GET
        con.setRequestMethod("GET");

        //add request header
        con.setRequestProperty("User-Agent", USER_AGENT);

        int responseCode = con.getResponseCode();

        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        //print result
        System.out.println(response.toString());

    }

}
