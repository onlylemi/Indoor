package com.onlylemi.dr.util;

import android.util.Log;

import com.onlylemi.parse.Data;

/**
 * Created by 董神 on 2015/10/12.
 * I love programming
 */
public class CheckThread extends Thread implements Runnable {
    private static final String TAG = CheckThread.class.getSimpleName();
    private static CheckThread checkThread = new CheckThread();
    private int flag;
    private int sum;
    private DiskCache diskCache;

    private CheckThread() {
        sum = 0;
        flag = 1;
        diskCache = DiskCache.getInstance();
    }

    public static CheckThread getCheckThread() {
        return checkThread;
    }

    public void startCheck() {
        checkThread.start();
    }

    @Override
    public void run() {
        Log.w(TAG, "Check thread start running");
        Log.w(TAG, "flag = " + flag);
        while (flag == 1) {
            int result = check();
            if(result != 0){
                Log.e(TAG, "check refresh number = " + result);
            }
            Log.i(TAG, "thread number : " + Thread.activeCount());
            try {
                ++sum;
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Log.w(TAG, "flag = " + flag);
        }
        Log.w(TAG, "Check thread end running");
    }

    public void endCheck() {
        flag = 0;
    }

    private int check() {
        int i = 0;

        if (Data.getCityTableList().size() == 0) {
            JSONParse.parseCity(diskCache.read(diskCache.CityTableName));
            i++;
        }

        if (Data.viewTableList.size() == 0) {
            JSONParse.parseView(diskCache.read(diskCache.ViewsTableName));
            i++;
        }

        if (Data.placeTableList.size() == 0) {
            JSONParse.parsePlace(diskCache.read(diskCache.PlaceTableName));
            i++;
        }

        if (Data.nodesTableList.size() == 0) {
            JSONParse.parseNodes(diskCache.read(diskCache.NodesTableName));
            i++;
        }

        if (Data.nodesContactTableList.size() == 0) {
            JSONParse.parseNodesContact(diskCache.read(diskCache.NodesContactTableName));
            i++;
        }

        if (Data.activityTableList.size() == 0) {
            JSONParse.parseActivity(diskCache.read(diskCache.ActivityTableName));
            i++;
        }

        if (Data.floorPlanTableList.size() == 0) {
            JSONParse.parseFloor(diskCache.read(diskCache.FloorPlanTableName));
            i++;
        }

        return i;
    }
}
