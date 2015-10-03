package com.onlylemi.parse;

import android.graphics.PointF;
import android.util.Log;

import com.onlylemi.map.core.PMark;
import com.onlylemi.map.utils.Assist;
import com.onlylemi.parse.info.BaseTable;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by only乐秘 on 2015-09-07.
 */
public class JSONParseTable {

    public static final String TAG = "JSONParseTable:";

    public static final String URL_SERVER = "http://indoor.onlylemi.com/";

    public static List<PointF> getNodesList() {
        final List<PointF> nodes = new ArrayList<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                String result1 = null;
                String url = "http://indoor.onlylemi.com/android/?r=nodes&p=2&f=1";

                HttpGet httpGet = new HttpGet(url);
                try {
                    HttpResponse httpResponse = new DefaultHttpClient().execute(httpGet);
                    if (httpResponse.getStatusLine().getStatusCode() == 200) {
                        result1 = EntityUtils.toString(httpResponse.getEntity());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    JSONObject jsonObject = new JSONObject(result1);
                    Log.i(TAG, jsonObject.toString());
                    JSONArray jsonArray = jsonObject.getJSONArray("nodes");
                    Log.i(TAG, jsonArray.toString());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jo = jsonArray.optJSONObject(i);
                        nodes.add(new PointF(Integer.parseInt(jo.getString("x")), Integer.parseInt(jo.getString("y"))));
                    }
                    Assist.init(nodes, null);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        return nodes;
    }

    public static List<PointF> getNodesContactList() {
        final List<PointF> nodesContact = new ArrayList<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                String result1 = null;
                String url = "http://indoor.onlylemi.com/android/?r=nodes_contact&p=2&f=1";

                HttpGet httpGet = new HttpGet(url);
                try {
                    HttpResponse httpResponse = new DefaultHttpClient().execute(httpGet);
                    if (httpResponse.getStatusLine().getStatusCode() == 200) {
                        result1 = EntityUtils.toString(httpResponse.getEntity());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    JSONObject jsonObject = new JSONObject(result1);
                    Log.i(TAG, jsonObject.toString());
                    JSONArray jsonArray = jsonObject.getJSONArray("nodes_contact");
                    Log.i(TAG, jsonArray.toString());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jo = jsonArray.optJSONObject(i);
                        nodesContact.add(new PointF(Integer.parseInt(jo.getString("n1")), Integer.parseInt(jo.getString("n2"))));
                    }
                    Assist.init(null, nodesContact);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        return nodesContact;
    }

    public static List<PMark> getViewsList() {
        final List<PMark> views = new ArrayList<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                String result1 = null;
                String url = "http://indoor.onlylemi.com/android/?r=views&p=2&f=1";

                HttpGet httpGet = new HttpGet(url);
                try {
                    HttpResponse httpResponse = new DefaultHttpClient().execute(httpGet);
                    if (httpResponse.getStatusLine().getStatusCode() == 200) {
                        result1 = EntityUtils.toString(httpResponse.getEntity());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    JSONObject jsonObject = new JSONObject(result1);
                    Log.i(TAG, jsonObject.toString());
                    JSONArray jsonArray = jsonObject.getJSONArray("views");
                    Log.i(TAG, jsonArray.toString());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jo = jsonArray.optJSONObject(i);
                        views.add(new PMark(Integer.parseInt(jo.getString("x")), Integer.parseInt(jo.getString("y")), jo.getString("name")));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        return views;
    }

}
