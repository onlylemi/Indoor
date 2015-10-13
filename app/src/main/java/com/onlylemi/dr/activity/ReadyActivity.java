package com.onlylemi.dr.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.onlylemi.dr.util.DiskCache;
import com.onlylemi.dr.util.DiskLruCache;
import com.onlylemi.dr.util.JSONHttp;
import com.onlylemi.dr.util.JSONParse;
import com.onlylemi.dr.util.NetworkJudge;
import com.onlylemi.indoor.R;
import com.onlylemi.user.Assist;

import java.io.File;

public class ReadyActivity extends Activity {

    public static final String TAG = "ReadyActivity:";

    //延时 handler
    public static Handler handler;
    //表名列表

    private DiskCache diskCache;

    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //判断wi-fi是否存在
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
                        Intent intent = new Intent(ReadyActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    default:
                        break;
                }
            }
        };
        DiskCache.initContext(getApplicationContext());
        diskCache = DiskCache.getInstance();

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

        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        editor = preferences.edit();
        int flag = preferences.getInt("Flag", -1);
        Assist.user = preferences.getString("user", "");

        judgeClearPictureCache(flag);

        setContentView(R.layout.activity_ready);
        CheckThread.getCheckThread().startCheck();
    }

    /**
     * 从JSON串中解析数据 JSON串的来源根据应用启动次数判断，如果是10的倍数，从
     * 网络获取，否则从文件获取。
     *
     * @param flag 应用启动次数
     */
    private void initData(int flag) {
        if (flag == -1) {
            editor.putInt("Flag", 1);
            JSONParse();//由于数据比较少，直接解析所有JSON数据 存到本地
            editor.apply();
        } else if (flag % 10 == 0) {
            JSONParse();//由于数据比较少，直接解析所有JSON数据 存到本地
            editor.putInt("Flag", flag + 1);
            Log.i("Test", "network::::" + flag);
            editor.apply();
        } else {
            JSONParseFromFile();//从本地获取
            editor.putInt("Flag", flag + 1);
            Log.i("Test", "file::::" + flag);
            editor.apply();
        }
    }

    /**
     * 是否清楚图片缓存 更具应用启动次数
     *
     * @param flag 应用启动次数
     */
    private void judgeClearPictureCache(int flag) {
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

    }

    /**
     * 从网上解析JSON
     */
    private void JSONParse() {
        String url = "http://indoor.onlylemi.com/android/?r=place";
        //所有地方信息初始化
        new JSONHttp(url, new JSONHttp.JSONHttpReturn() {
            @Override
            public void JSONReturn(String s) {
                diskCache.write(diskCache.PlaceTableName, s);//写入缓存
                JSONParse.parsePlace(s);
            }
        }).start();

        url = "http://indoor.onlylemi.com/android/?r=activity";
        //所有活动信息初始化
        new JSONHttp(url, new JSONHttp.JSONHttpReturn() {
            @Override
            public void JSONReturn(String s) {
                diskCache.write(diskCache.ActivityTableName, s);//写入缓存
                JSONParse.parseActivity(s);
            }
        }).start();

        url = "http://indoor.onlylemi.com/android/?r=views";
        //所有view信息初始化
        new JSONHttp(url, new JSONHttp.JSONHttpReturn() {
            @Override
            public void JSONReturn(String s) {
                diskCache.write(diskCache.ViewsTableName, s);//写入缓存
                JSONParse.parseView(s);
            }
        }).start();

        url = "http://indoor.onlylemi.com/android/?r=floor_plan";
        //所有floorplan信息初始化
        new JSONHttp(url, new JSONHttp.JSONHttpReturn() {
            @Override
            public void JSONReturn(String s) {
                diskCache.write(diskCache.FloorPlanTableName, s);//写入缓存
                JSONParse.parseFloor(s);
            }
        }).start();

        url = "http://indoor.onlylemi.com/android/?r=nodes_contact";
        //所有nodescontact信息初始化
        new JSONHttp(url, new JSONHttp.JSONHttpReturn() {
            @Override
            public void JSONReturn(String s) {
                diskCache.write(diskCache.NodesContactTableName, s);//写入缓存
                JSONParse.parseNodesContact(s);
            }
        }).start();

        url = "http://indoor.onlylemi.com/android/?r=nodes";
        //所有nodes信息初始化
        new JSONHttp(url, new JSONHttp.JSONHttpReturn() {
            @Override
            public void JSONReturn(String s) {
                diskCache.write(diskCache.NodesTableName, s);//写入缓存
                JSONParse.parseNodes(s);
            }
        }).start();

        //城市列表 初始化
        url = "http://indoor.onlylemi.com/android/?r=city";
        JSONHttp http = new JSONHttp(url, new JSONHttp.JSONHttpReturn() {
            @Override
            public void JSONReturn(String s) {
                diskCache.write(diskCache.CityTableName, s);//写入缓存
                JSONParse.parseCity(s);
                handler.sendEmptyMessageDelayed(Constant.READY_GO, 1000);
            }
        });
        http.setPriority(Thread.MIN_PRIORITY);
        http.start();
    }

    /**
     * 从本地文件中解析JSON
     */
    private void JSONParseFromFile() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //地方信息初始化
                String s = diskCache.read(diskCache.PlaceTableName);
                JSONParse.parsePlace(s);

                //活动信息初始化
                s = diskCache.read(diskCache.ActivityTableName);
                JSONParse.parseActivity(s);

                //所有view信息初始化
                s = diskCache.read(diskCache.ViewsTableName);
                JSONParse.parseView(s);

                //所有floorplan信息初始化
                s = diskCache.read(diskCache.FloorPlanTableName);
                JSONParse.parseFloor(s);

                //所有nodescontact信息初始化
                s = diskCache.read(diskCache.NodesContactTableName);
                JSONParse.parseNodesContact(s);

                //所有nodes信息初始化
                s = diskCache.read(diskCache.NodesTableName);
                JSONParse.parseNodes(s);

                //城市信息初始化
                s = diskCache.read(diskCache.CityTableName);
                JSONParse.parseCity(s);
            }
        }).start();
    }
}
