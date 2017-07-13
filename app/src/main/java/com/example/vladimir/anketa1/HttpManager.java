package com.example.vladimir.anketa1;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpManager {

    public static String getData(String uri, String postData) {
        Log.i("url", "HttpManagers " + uri + "." + postData);
        BufferedReader reader=null;
        try {
            URL url = new URL(uri);
            HttpURLConnection con = (HttpURLConnection)url.openConnection();

            con.setRequestMethod("POST");
            con.setDoOutput(true);
            OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());
            writer.write(postData);
            writer.flush();

            con.getOutputStream().write(postData.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            reader = new BufferedReader(new InputStreamReader(con.getInputStream()));


            String line;
            while((line = reader.readLine()) != null) {
                sb.append(line + "\n");
                Log.i("url", "HttpManager .. line " + line);
            }

            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if(reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }
    }
}
