package com.onlylemi.dr.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by 董神 on 2015/9/14.
 * I love programming
 */
public class JSONHttp extends Thread {

    private String stringUrl = "http://indoor.onlylemi.com/android/?r=place&c=1";
    private static final String TAG = "JSONHttp:";
    private JSONHttpReturn jsonHttpReturn;
    private String result;

    /**
     * @param s 网络地址
     * @param jsonHttpReturn 监听
     */
    public JSONHttp(String s, JSONHttpReturn jsonHttpReturn) {
        this.stringUrl = s;
        this.jsonHttpReturn = jsonHttpReturn;
    }

    @Override
    public void run() {
        try {
            URL url = new URL(stringUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(5000);
            connection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuffer buffer = new StringBuffer();
            String string = null;

            while ((string = reader.readLine()) != null) {
                buffer.append(string);
            }
            result = buffer.toString();
            jsonHttpReturn.JSONReturn(result);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public interface JSONHttpReturn{
        void JSONReturn(String s);
    }

}
