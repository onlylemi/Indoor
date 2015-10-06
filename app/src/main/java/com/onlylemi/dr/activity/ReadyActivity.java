package com.onlylemi.dr.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.baidu.mapapi.SDKInitializer;
import com.onlylemi.dr.costant_interface.Constant;
import com.onlylemi.dr.util.AsyncImageLoader;
import com.onlylemi.dr.util.BaiduLocate;
import com.onlylemi.dr.util.DiskLruCache;
import com.onlylemi.dr.util.JSONHttp;
import com.onlylemi.dr.util.NetworkJudge;
import com.onlylemi.indoor.R;
import com.onlylemi.indoor.TestActivity;
import com.onlylemi.parse.Data;
import com.onlylemi.parse.info.ActivityTable;
import com.onlylemi.parse.info.CityTable;
import com.onlylemi.parse.info.FloorPlanTable;
import com.onlylemi.parse.info.NodesContactTable;
import com.onlylemi.parse.info.NodesTable;
import com.onlylemi.parse.info.PlaceTable;
import com.onlylemi.parse.info.ViewsTable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

public class ReadyActivity extends Activity {

    private static final String TAG = "ReadyActivity:";

    //延时 handler
    public static Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //百度地图SDK初始化 必须在 setContentView 之前
        SDKInitializer.initialize(getApplicationContext());

        //百度定位初始化
        BaiduLocate.InitContext(getApplicationContext());
        BaiduLocate.getInstance().setMyLocateListener(new BaiduLocate.MyLocateListener() {
            @Override
            public void LocateCallBack(int flag, String city, String address) {
                Log.i(TAG, "定位回调监听成功");
                Log.i(TAG, "city : " + city);
                Log.i(BaiduLocate.TAG, "定位回调监听成功");
                Log.i(BaiduLocate.TAG, "address : " + address);
                BaiduLocate.getInstance().stopLocate();
            }
        });
        BaiduLocate.getInstance().startLocate();
        setContentView(R.layout.activity_ready);

        //由于数据比较少，直接解析所有JSON数据 存到本地
        JSONParse();

        //判断是否删除缓存
        /*if (NetworkJudge.isWifiEnabled(getApplicationContext())) {
            try {
                String cachePath;
                if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                        || !Environment.isExternalStorageRemovable()) {
                    cachePath = getApplicationContext().getExternalCacheDir().getPath();
                } else {
                    cachePath = getApplicationContext().getCacheDir().getPath();
                }
                File cacheDir = new File(cachePath + File.separator + "bitmap");
                Log.i(AsyncImageLoader.TAG, "cache dir :　" + cacheDir);
                if (cacheDir.exists()) {
                    DiskLruCache.deleteContents(cacheDir);
                    Log.i(AsyncImageLoader.TAG, "cache dir :　" + cacheDir);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }*/

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case Constant.READY_GO:
                        Intent intent = new Intent(ReadyActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    default:
                        break;
                }
            }
        };

        handler.sendEmptyMessageDelayed(Constant.READY_GO, 2000);
    }


    /**
     * 主进程的handler
     *
     * @return Handler
     */
    public static Handler getHandler() {
        if (handler != null) return handler;
        else return null;
    }


    /**
     * 所有JSON解析
     */
    private void JSONParse() {
        //城市列表 初始化
        String url = "http://indoor.onlylemi.com/android/?r=city";
        new JSONHttp(url, new JSONHttp.JSONHttpReturn() {
            @Override
            public void JSONReturn(String s) {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray arrays = jsonObject.getJSONArray("city");

                    Data.cityTableList.clear();
                    for (int i = 0; i < arrays.length(); i++) {
                        CityTable city = new CityTable();
                        JSONObject object = arrays.getJSONObject(i);
                        city.setName(object.getString("name"));
                        city.setId(object.getInt("id"));
                        Data.cityTableList.add(city);
                    }
                    android.util.Log.i(TAG, "city number : " + Data.cityTableList.size());
                } catch (Exception e) {
                    Log.e(TAG, "解析城市列表时出错");
                    e.printStackTrace();

                }

            }
        }).start();

        url = "http://indoor.onlylemi.com/android/?r=place";
        //所有地方信息初始化
        new JSONHttp(url, new JSONHttp.JSONHttpReturn() {
            @Override
            public void JSONReturn(String s) {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray arrays = jsonObject.getJSONArray("place");
                    Data.placeTableList.clear();
                    for (int i = 0; i < arrays.length(); i++) {
                        PlaceTable placeTable = new PlaceTable();
                        JSONObject object = arrays.getJSONObject(i);
                        placeTable.setName(object.getString("name"));
                        placeTable.setId(object.getInt("id"));
                        placeTable.setImage("http://indoor.onlylemi.com/" + object.getString("image"));
                        placeTable.setVideo(object.getString("video"));
                        placeTable.setIntro(object.getString("intro"));
                        placeTable.setCid(object.getInt("cid"));
                        placeTable.setLat(object.getDouble("lat"));
                        placeTable.setLng(object.getDouble("lng"));
                        Data.placeTableList.add(placeTable);
                        Log.w(TAG, placeTable.getImage() + placeTable.getCid() + placeTable.getName());
                    }
                    Log.e(TAG, "palce number : " + Data.placeTableList.size());
                } catch (Exception e) {
                    Log.e(TAG, "解析difang列表时出错");
                    e.printStackTrace();
                }
            }
        }).start();

        url = "http://indoor.onlylemi.com/android/?r=activity";
        //所有活动信息初始化
        new JSONHttp(url, new JSONHttp.JSONHttpReturn() {
            @Override
            public void JSONReturn(String s) {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray arrays = jsonObject.getJSONArray("activity");
                    Data.activityTableList.clear();
                    for (int i = 0; i < arrays.length(); i++) {
                        ActivityTable activityTable = new ActivityTable();
                        JSONObject object = arrays.getJSONObject(i);
                        activityTable.setName(object.getString("name"));
                        activityTable.setId(object.getInt("id"));
                        activityTable.setImage(object.getString("image"));
                        activityTable.setStartTime(object.getString("start_time"));
                        activityTable.setEndTime(object.getString("end_time"));
                        activityTable.setIntro(object.getString("intro"));
                        activityTable.setVid(object.getInt("vid"));
                        Data.activityTableList.add(activityTable);
                    }
                    Log.e(TAG, "activity number : " + Data.activityTableList.size());
                } catch (Exception e) {
                    Log.e(TAG, "解析活动列表时出错");
                    e.printStackTrace();
                }
            }
        }).start();

        url = "http://indoor.onlylemi.com/android/?r=views";
        //所有view信息初始化
        new JSONHttp(url, new JSONHttp.JSONHttpReturn() {
            @Override
            public void JSONReturn(String s) {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray arrays = jsonObject.getJSONArray("views");
                    Data.viewTableList.clear();
                    for (int i = 0; i < arrays.length(); i++) {
                        ViewsTable viewsTable = new ViewsTable();
                        JSONObject object = arrays.getJSONObject(i);
                        viewsTable.setName(object.getString("name"));
                        viewsTable.setId(object.getInt("id"));
                        viewsTable.setImage("http://indoor.onlylemi.com/" + object.getString("image"));
                        viewsTable.setVideo(object.getString("video"));
                        viewsTable.setIntro(object.getString("intro"));
                        viewsTable.setX(object.getInt("x"));
                        viewsTable.setY(object.getInt("y"));
                        viewsTable.setPid(object.getInt("pid"));
                        viewsTable.setFn(object.getInt("fn"));
                        Data.viewTableList.add(viewsTable);
                    }
                    Log.e(TAG, "views number : " + Data.viewTableList.size());
                } catch (Exception e) {
                    Log.e(TAG, "解析views列表时出错");
                    e.printStackTrace();
                }
            }
        }).start();

        url = "http://indoor.onlylemi.com/android/?r=floor_plan";
        //所有floorplan信息初始化
        new JSONHttp(url, new JSONHttp.JSONHttpReturn() {
            @Override
            public void JSONReturn(String s) {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray arrays = jsonObject.getJSONArray("floor_plan");
                    Data.floorPlanTableList.clear();
                    for (int i = 0; i < arrays.length(); i++) {
                        FloorPlanTable floorPlanTable = new FloorPlanTable();
                        JSONObject object = arrays.getJSONObject(i);
                        floorPlanTable.setVenueid(object.getString("venueid"));
                        floorPlanTable.setFloorid(object.getString("floorid"));
                        floorPlanTable.setFloorplanid(object.getString("floorplanid"));
                        floorPlanTable.setPid(object.getInt("pid"));
                        floorPlanTable.setImage(object.getString("image"));
                        floorPlanTable.setFn(object.getInt("fn"));
                        floorPlanTable.setId(object.getInt("id"));
                        Data.floorPlanTableList.add(floorPlanTable);
                    }
                    Log.e(TAG, "floorplan number : " + Data.floorPlanTableList.size());
                } catch (Exception e) {
                    Log.e(TAG, "解析floorplan列表时出错");
                    e.printStackTrace();
                }
            }
        }).start();

        url = "http://indoor.onlylemi.com/android/?r=nodes_contact";
        //所有nodescontact信息初始化
        new JSONHttp(url, new JSONHttp.JSONHttpReturn() {
            @Override
            public void JSONReturn(String s) {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray arrays = jsonObject.getJSONArray("nodes_contact");
                    Data.nodesContactTableList.clear();
                    for (int i = 0; i < arrays.length(); i++) {
                        NodesContactTable nodesContact = new NodesContactTable();
                        JSONObject object = arrays.getJSONObject(i);
                        nodesContact.setN1(object.getInt("n1"));
                        nodesContact.setPid(object.getInt("pid"));
                        nodesContact.setN2(object.getInt("n2"));
                        nodesContact.setFn(object.getInt("fn"));
                        nodesContact.setId(object.getInt("id"));
                        Data.nodesContactTableList.add(nodesContact);
                    }
                    Log.e(TAG, "NodesContact number : " + Data.nodesContactTableList.size());
                } catch (Exception e) {
                    Log.e(TAG, "解析nodesContactList列表时出错" + e.toString());
                    e.printStackTrace();
                }
            }
        }).start();

        url = "http://indoor.onlylemi.com/android/?r=nodes";
        //所有nodes信息初始化
        new JSONHttp(url, new JSONHttp.JSONHttpReturn() {
            @Override
            public void JSONReturn(String s) {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray arrays = jsonObject.getJSONArray("nodes");
                    Data.nodesTableList.clear();
                    for (int i = 0; i < arrays.length(); i++) {
                        NodesTable nodesTable = new NodesTable();
                        JSONObject object = arrays.getJSONObject(i);
                        nodesTable.setX(object.getInt("x"));
                        nodesTable.setPid(object.getInt("pid"));
                        nodesTable.setY(object.getInt("y"));
                        nodesTable.setN(object.getInt("n"));
                        nodesTable.setFn(object.getInt("fn"));
                        nodesTable.setId(object.getInt("id"));
                        Data.nodesTableList.add(nodesTable);
                    }
                    Log.e(TAG, "Nodes number : " + Data.nodesTableList.size());
                } catch (Exception e) {
                    Log.e(TAG, "解析nodes列表时出错" + e.toString());
                }
            }
        }).start();
    }

}
