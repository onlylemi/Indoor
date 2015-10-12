package com.onlylemi.dr.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.onlylemi.dr.costant_interface.Constant;
import com.onlylemi.dr.util.AsyncImageLoader;
import com.onlylemi.dr.util.BaiduLocate;
import com.onlylemi.dr.util.CheckThread;
import com.onlylemi.dr.util.DiskLruCache;
import com.onlylemi.dr.util.JSONHttp;
import com.onlylemi.dr.util.NetworkJudge;
import com.onlylemi.indoor.R;
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
import java.io.InputStream;
import java.io.OutputStream;

public class ReadyActivity extends Activity {

    public static final String TAG = "ReadyActivity:";

    private static final int DISK_CACHE_DEFAULT_SIZE = 5 * 1024 * 1024;
    //延时 handler
    public static Handler handler;
    //表名列表
    private final String CityTableName = "CityTable";
    private final String FloorPlanTableName = "FloorPlanTable";
    private final String NodesContactTableName = "NodesContactTable";
    private final String PlaceTableName = "PlaceTable";
    private final String ViewsTableName = "ViewsTable";
    private final String ActivityTableName = "ActivityTable";
    private final String NodesTableName = "NodesTable";
    private DiskLruCache diskLruCache;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!NetworkJudge.isWifiEnabled(this)) {
            synchronized (ReadyActivity.this) {
                Toast.makeText(ReadyActivity.this, "no wi-fi", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "no wi-fi");
            }
        }
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case Constant.READY_GO:
                        Log.e(TAG, "NodesContact number : " + Data.nodesContactTableList.size());
                        Log.e(TAG, "city number : " + Data.getCityTableList().size());
                        Log.e(TAG, "Nodes number : " + Data.nodesTableList.size());
                        Log.e(TAG, "floorplan number : " + Data.floorPlanTableList.size());
                        Log.e(TAG, "views number : " + Data.viewTableList.size());
                        Log.e(TAG, "palce number : " + Data.placeTableList.size());
                        Log.e(TAG, "activity number : " + Data.activityTableList.size());
                        Intent intent = new Intent(ReadyActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    default:
                        break;
                }
            }
        };

        //百度地图SDK初始化 必须在 setContentView 之前
        SDKInitializer.initialize(getApplicationContext());

        //百度定位初始化
        BaiduLocate.InitContext(getApplicationContext());
        BaiduLocate.getInstance().setMyLocateListener(new BaiduLocate.MyLocateListener() {
            @Override
            public void LocateCallBack(int flag, String city, String address) {
                Log.e(BaiduLocate.TAG, "定位回调监听成功-----" + "address : " + address);
                Log.i(TAG, "city : " + city);
                BaiduLocate.getInstance().stopLocate();
            }
        });
        BaiduLocate.getInstance().startLocate();

        initDiskCache();
        //是否删除文件缓存
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        editor = preferences.edit();
        int flag = preferences.getInt("Flag", -1);
        if (flag == -1) {
            editor.putInt("Flag", 1);
            JSONParse();//由于数据比较少，直接解析所有JSON数据 存到本地
            editor.apply();
        } else if (flag % 10 == 0) {
            JSONParse();//由于数据比较少，直接解析所有JSON数据 存到本地
            editor.putInt("Flag", ++flag);
            Log.i("Test", "network::::" + --flag);
            editor.apply();
        } else {
            JSONParseFromFile();//从本地获取
            editor.putInt("Flag", ++flag);
            Log.i("Test", "file::::" + --flag);
            editor.apply();
        }


        //判断是否删除图片缓存
        if (NetworkJudge.isWifiEnabled(getApplicationContext())) {
            try {
                String cachePath;
                if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                        || !Environment.isExternalStorageRemovable()) {
                    if (getApplicationContext().getExternalCacheDir() != null) {
                        cachePath = getApplicationContext().getExternalCacheDir().getPath();
                        Log.i("Test", "SD");
                    } else {
                        throw new Exception("SD卡连接错误");
                    }
                } else {
                    cachePath = getApplicationContext().getCacheDir().getPath();
                }
                File cacheDir = new File(cachePath + File.separator + "bitmap");
                Log.i(AsyncImageLoader.TAG, "cache dir :　" + cacheDir);
                if (cacheDir.exists()) {
                    if ((flag - 1) % 15 == 0) {
                        DiskLruCache.deleteContents(cacheDir);
                        Log.i(AsyncImageLoader.TAG, "cache dir :　" + cacheDir);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(ReadyActivity.this, "当前无wifi连接", Toast.LENGTH_SHORT).show();
        }

        setContentView(R.layout.activity_ready);

        CheckThread.getCheckThread().start();
    }

    /**
     * 所有JSON解析
     */
    private void JSONParse() {
        String url = "http://indoor.onlylemi.com/android/?r=place";
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
                    }
                    write(PlaceTableName, s);//写入缓存
                    if(Data.placeTableList.size() == 0) {
                        Log.e(TAG, "palce number : " + Data.placeTableList.size());
                    }else {
                        Log.i(TAG, "palce number : " + Data.placeTableList.size());
                    }
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
                    write(ActivityTableName, s);//写入缓存
                    if(Data.activityTableList.size() == 0) {
                        Log.e(TAG, "activity number : " + Data.activityTableList.size());
                    }else {
                        Log.i(TAG, "activity number : " + Data.activityTableList.size());
                    }
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
                    write(ViewsTableName, s);//写入缓存
                    if(Data.viewTableList.size() == 0) {
                        Log.e(TAG, "views number : " + Data.viewTableList.size());
                    }else {
                        Log.i(TAG, "views number : " + Data.viewTableList.size());
                    }
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
                    write(FloorPlanTableName, s);//写入缓存
                    if(Data.floorPlanTableList.size() == 0) {
                        Log.e(TAG, "floorplan number : " + Data.floorPlanTableList.size());
                    }else {
                        Log.i(TAG, "floorplan number : " + Data.floorPlanTableList.size());
                    }
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
                    write(NodesContactTableName, s);//写入缓存
                    if(Data.nodesContactTableList.size() == 0) {
                        Log.e(TAG, "NodesContact number : " + Data.nodesContactTableList.size());
                    }else {
                        Log.i(TAG, "NodesContact number : " + Data.nodesContactTableList.size());
                    }
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
                    Data.activityTableList.clear();
                    Data.getCityTableList().clear();
                    Data.floorPlanTableList.clear();
                    Data.viewTableList.clear();
                    Data.nodesContactTableList.clear();
                    Data.nodesTableList.clear();
                    Data.placeTableList.clear();
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
                    write(NodesTableName, s);//写入缓存
                    if(Data.nodesTableList.size() == 0) {
                        Log.e(TAG, "Nodes number : " + Data.nodesTableList.size());
                    }else {
                        Log.i(TAG, "Nodes number : " + Data.nodesTableList.size());
                    }
                } catch (Exception e) {
                    Log.e(TAG, "解析nodes列表时出错" + e.toString());
                }
            }
        }).start();

        //城市列表 初始化
        url = "http://indoor.onlylemi.com/android/?r=city";
        JSONHttp http = new JSONHttp(url, new JSONHttp.JSONHttpReturn() {
            @Override
            public void JSONReturn(String s) {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray arrays = jsonObject.getJSONArray("city");
                    Data.getCityTableList().clear();
                    for (int i = 0; i < arrays.length(); i++) {
                        CityTable city = new CityTable();
                        JSONObject object = arrays.getJSONObject(i);
                        city.setName(object.getString("name"));
                        city.setId(object.getInt("id"));
                        Data.getCityTableList().add(city);
                    }
                    write(CityTableName, s);//写入缓存
                    if(Data.getCityTableList().size() == 0) {
                        Thread.sleep(1000);
                        Log.e(TAG, "city number : " + Data.getCityTableList().size());
                    }else {
                        Log.i(TAG, "city number : " + Data.getCityTableList().size());
                    }
                    Log.w(ReadyActivity.TAG, "size ===== " + Data.getCityTableList().size());
                    handler.sendEmptyMessageDelayed(Constant.READY_GO, 500);
                } catch (Exception e) {
                    Log.e(TAG, "解析城市列表时出错");
                    e.printStackTrace();
                }
            }
        });
        http.setPriority(Thread.MIN_PRIORITY);
        http.start();
    }

    private void JSONParseFromFile() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Data.activityTableList.clear();
                Data.getCityTableList().clear();
                Data.floorPlanTableList.clear();
                Data.viewTableList.clear();
                Data.nodesContactTableList.clear();
                Data.nodesTableList.clear();
                Data.placeTableList.clear();
                //城市信息初始化
                String s = read(CityTableName);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray arrays = jsonObject.getJSONArray("city");
                    Data.getCityTableList().clear();
                    for (int i = 0; i < arrays.length(); i++) {
                        CityTable city = new CityTable();
                        JSONObject object = arrays.getJSONObject(i);
                        city.setName(object.getString("name"));
                        city.setId(object.getInt("id"));
                        Data.getCityTableList().add(city);
                    }
                    Log.i(TAG, "city number : " + Data.getCityTableList().size());
                    handler.sendEmptyMessageDelayed(Constant.READY_GO, 2000);
                } catch (Exception e) {
                    Log.e(TAG, "解析城市列表时出错");
                    e.printStackTrace();
                }

                //地方信息初始化
                s = read(PlaceTableName);
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
                    }
                    Log.i(TAG, "palce number : " + Data.placeTableList.size());
                } catch (Exception e) {
                    Log.e(TAG, "解析地方列表时出错");
                    e.printStackTrace();
                }

                //活动信息初始化
                s = read(ActivityTableName);
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
                    write(ActivityTableName, s);//写入缓存
                    Log.i(TAG, "activity number : " + Data.activityTableList.size());
                } catch (Exception e) {
                    Log.e(TAG, "解析活动列表时出错");
                    e.printStackTrace();
                }

                //所有view信息初始化
                s = read(ViewsTableName);
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
                    write(ViewsTableName, s);//写入缓存
                    Log.i(TAG, "views number : " + Data.viewTableList.size());
                } catch (Exception e) {
                    Log.e(TAG, "解析views列表时出错");
                    e.printStackTrace();
                }

                //所有floorplan信息初始化
                s = read(FloorPlanTableName);
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
                    write(FloorPlanTableName, s);//写入缓存
                    Log.i(TAG, "floorplan number : " + Data.floorPlanTableList.size());
                } catch (Exception e) {
                    Log.e(TAG, "解析floorplan列表时出错");
                    e.printStackTrace();
                }

                //所有nodescontact信息初始化
                s = read(NodesContactTableName);
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
                    write(NodesContactTableName, s);//写入缓存
                    Log.i(TAG, "NodesContact number : " + Data.nodesContactTableList.size());
                } catch (Exception e) {
                    Log.e(TAG, "解析nodesContactList列表时出错" + e.toString());
                    e.printStackTrace();
                }
                //所有nodes信息初始化
                s = read(NodesTableName);
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
                    write(NodesTableName, s);//写入缓存
                    Log.i(TAG, "Nodes number : " + Data.nodesTableList.size());
                } catch (Exception e) {
                    Log.e(TAG, "解析nodes列表时出错" + e.toString());
                }
            }
        }).start();
    }

    /**
     * 初始化硬盘缓存
     */
    private void initDiskCache() {
        try {
            String cache;
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)
                    || !Environment.isExternalStorageRemovable()) {
                if (getApplicationContext().getExternalCacheDir() != null) {
                    cache = getApplicationContext().getExternalCacheDir().getPath();
                    Log.i("Test", "SD");
                } else {
                    throw new Exception("SD卡连接错误");
                }
            } else {
                cache = getApplicationContext().getCacheDir().getPath();
                Log.i("Test", "mobilephone");
            }

            /**
             * SD卡数据缓存目录
             */
            String dataFileName = "indoor_data";

            cache = cache + File.separator + dataFileName;
            File fileDir = new File(cache);
            if (!fileDir.exists()) {
                if (fileDir.mkdirs()) {
                    Log.e("Test", "创建目录: " + fileDir.getAbsolutePath());
                } else {
                    Log.e("Test", "创建目录失败");
                }
            } else {
                Log.e("Test", "目录: " + fileDir.getAbsolutePath());
            }
            diskLruCache = DiskLruCache.open(fileDir, getAppVersion(getApplicationContext()), 1, DISK_CACHE_DEFAULT_SIZE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 从缓存中读数据
     *
     * @param name 表名
     * @return JSON数据串
     */
    private String read(String name) {
        String s = "";
        StringBuilder stringBuilder = new StringBuilder("");
        try {
            DiskLruCache.Snapshot snapshot = diskLruCache.get(name);
            if (snapshot != null) {
                InputStream in = snapshot.getInputStream(0);
                byte[] bytes = new byte[1024];
                int length;
                while ((length = in.read(bytes)) > 0) {
                    stringBuilder.append(new String(bytes, 0, length));
                }
                s = stringBuilder.toString();
            } else {
                Log.e("Test", "found fail");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return s;
    }

    /**
     * 把数据写入缓存
     *
     * @param name    表名
     * @param content JSON数据串
     */
    private void write(String name, String content) {
        try {
            DiskLruCache.Editor editors = diskLruCache.edit(name);
            if (editor != null) {
                OutputStream out = editors.newOutputStream(0);
                byte[] bytes = content.getBytes();
                out.write(bytes);
                out.flush();
                editors.commit();
                diskLruCache.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 当前APP版本
     *
     * @param context context对象
     * @return 版本号
     */
    private int getAppVersion(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 1;
    }

}
