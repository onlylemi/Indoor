package com.onlylemi.parse;

import android.os.Handler;
import android.util.Log;

import com.onlylemi.parse.info.ActivityTable;
import com.onlylemi.parse.info.UserPositionTable;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by only乐秘 on 2015-09-28.
 */
public class JSONUpload {

    public static final String TAG = "JSONUpload:";

    private final static String URL_SERVER = "http://indoor.onlylemi.com/android/";
    private InputStream is = null;
    private JSONObject jObj = null;
    private String json = "";

    private OnUploadDataListener listener;

    private Handler handler;

    public JSONUpload(Handler handler) {
        this.handler = handler;
    }

    /**
     * 上传数据
     *
     * @param urlServer url地址
     * @param method    请求方式
     * @param params    数据表
     */
    public void uploadData(final String urlServer, final String method, final List<NameValuePair> params) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String url = urlServer;
                    if (method.equals("POST")) {
                        // request method is POST
                        DefaultHttpClient httpClient = new DefaultHttpClient();
                        HttpPost httpPost = new HttpPost(url);
                        httpPost.setEntity(new UrlEncodedFormEntity(params));

                        HttpResponse httpResponse = httpClient.execute(httpPost);
                        HttpEntity httpEntity = httpResponse.getEntity();
                        is = httpEntity.getContent();

                    } else if (method.equals("GET")) {
                        // request method is GET
                        DefaultHttpClient httpClient = new DefaultHttpClient();
                        String paramString = URLEncodedUtils.format(params, "utf-8");
                        url += "?" + paramString;
                        HttpGet httpGet = new HttpGet(url);

                        HttpResponse httpResponse = httpClient.execute(httpGet);
                        HttpEntity httpEntity = httpResponse.getEntity();
                        is = httpEntity.getContent();
                    }

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }


                //获取服务器返回数据
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(
                            is, "utf-8"), 8);
                    StringBuilder sb = new StringBuilder();
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    is.close();
                    json = sb.toString();
                } catch (Exception e) {
                    Log.e(TAG, "Error converting result " + e.toString());
                }

                // 解析json
                try {
                    jObj = new JSONObject(json);
                    final int success = jObj.getInt("success");
                    final String message = jObj.getString("message");

                    if (handler != null) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (listener != null) {
                                    if (success == 1) {
                                        listener.onSuccess(success, message);
                                    } else {
                                        listener.onFail(success, message);
                                    }
                                }
                            }
                        });
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "Error parsing data " + e.toString());
                }
            }
        }).start();
    }

    /**
     * 设置上传数据监听器
     *
     * @param listener
     */
    public void setOnUploadDataListener(OnUploadDataListener listener) {
        this.listener = listener;
    }

    /**
     * 上传监听器
     */
    public interface OnUploadDataListener {
        /**
         * 上传成功
         *
         * @param success 成功为 1
         * @param message 成功消息
         */
        void onSuccess(int success, String message);

        /**
         * 上传失败
         *
         * @param success 失败为 0
         * @param message 失败消息
         */
        void onFail(int success, String message);
    }

    /**
     * 上传用户位置数据
     *
     * @param method
     * @param userPositionTable
     */
    public void uploadPositionData(String method, UserPositionTable userPositionTable) {
        String url = URL_SERVER + "?r=upload_position";

        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("deviceId", userPositionTable.getDeviceId()));
        params.add(new BasicNameValuePair("lat", Double.toString(userPositionTable.getLat())));
        params.add(new BasicNameValuePair("lon", Double.toString(userPositionTable.getLon())));
        params.add(new BasicNameValuePair("i", Integer.toString(userPositionTable.getI())));
        params.add(new BasicNameValuePair("j", Integer.toString(userPositionTable.getJ())));
        params.add(new BasicNameValuePair("x", Double.toString(userPositionTable.getX())));
        params.add(new BasicNameValuePair("y", Double.toString(userPositionTable.getY())));
        params.add(new BasicNameValuePair("heading", Double.toString(userPositionTable.getHeading())));
        params.add(new BasicNameValuePair("uncertainty", Double.toString(userPositionTable.getUncertainty())));
        params.add(new BasicNameValuePair("roundtrip", Long.toString(userPositionTable.getRoundtrip())));
        params.add(new BasicNameValuePair("time", userPositionTable.getTime()));
        params.add(new BasicNameValuePair("pid", Integer.toString(userPositionTable.getPid())));
        params.add(new BasicNameValuePair("fn", Integer.toString(userPositionTable.getFn())));

        uploadData(url, method, params);
    }

    /**
     * 上传活动数据
     *
     * @param method
     * @param activityTable
     */
    public void uploadViewsActivityData(String method, ActivityTable activityTable) {
        String url = URL_SERVER + "?r=upload_activity";

        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("name", activityTable.getName()));
        params.add(new BasicNameValuePair("start_time", activityTable.getStartTime()));
        params.add(new BasicNameValuePair("end_time", activityTable.getEndTime()));
        params.add(new BasicNameValuePair("intro", activityTable.getIntro()));
        params.add(new BasicNameValuePair("vid", Integer.toString(activityTable.getVid())));

        uploadData(url, method, params);
    }
}
