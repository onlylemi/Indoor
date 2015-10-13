package com.onlylemi.dr.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by 董神 on 2015/10/12.
 * I love programming
 */
public class DiskCache {

    private static final int DISK_CACHE_DEFAULT_SIZE = 5 * 1024 * 1024;


    private static Context context;
    //表名列表
    public final String CityTableName = "CityTable";
    public final String FloorPlanTableName = "FloorPlanTable";
    public final String NodesContactTableName = "NodesContactTable";
    public final String PlaceTableName = "PlaceTable";
    public final String ViewsTableName = "ViewsTable";
    public final String ActivityTableName = "ActivityTable";
    public final String NodesTableName = "NodesTable";

    private DiskLruCache diskLruCache;

    private static DiskCache diskCache = null;

    public static DiskCache getInstance() {
        if(diskCache == null) {
            diskCache = new DiskCache();
        }
        return diskCache;
    }

    private DiskCache() {
        initDiskCache();
    }

    public static void initContext(Context context) {
        if(DiskCache.context == null) {
            DiskCache.context = context;
        }else {
        }
    }

    /**
     * 初始化硬盘缓存
     */
    private void initDiskCache() {
        try {
            String cache;
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)
                    || !Environment.isExternalStorageRemovable()) {
                if (context.getExternalCacheDir() != null) {
                    cache = context.getExternalCacheDir().getPath();
                    Log.i("Test", "SD");
                } else {
                    throw new Exception("SD卡连接错误");
                }
            } else {
                cache = context.getCacheDir().getPath();
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
            diskLruCache = DiskLruCache.open(fileDir, getAppVersion(context), 1, DISK_CACHE_DEFAULT_SIZE);
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
    public String read(String name) {
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
    public void write(String name, String content) {
        try {
            DiskLruCache.Editor editors = diskLruCache.edit(name);
            if (editors != null) {
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
