package com.onlylemi.dr.util;

import android.util.Log;

/**
 * Created by 董神 on 2015/10/12.
 * I love programming
 */
public class CheckThread extends Thread implements Runnable{
    private static final String TAG = CheckThread.class.getSimpleName();
    private static CheckThread checkThread = new CheckThread();
    private int flag;
    private int sum;
    private CheckThread() {
        sum = 0;
        flag = 1;
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
        while(flag == 1) {
            check();
            Log.e(TAG, "thread number : " + Thread.activeCount());
            try {
                ++sum;
                Thread.sleep(1500);
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

    private void check() {

    }
}
