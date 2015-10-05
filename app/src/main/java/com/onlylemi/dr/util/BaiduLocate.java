package com.onlylemi.dr.util;

import android.content.Context;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.onlylemi.dr.costant_interface.Constant;

/**
 * Created by 董神 on 2015/9/23.
 * I love programming
 */
public class BaiduLocate implements BDLocationListener, OnGetGeoCoderResultListener {

    public static final String TAG = "BaiduLocate";
    private static Context mContext;
    private boolean isLoc = true; // 是否定位
    private LocationClient mClient;//百度定位执行器
    public static double latitude; //纬度
    public static double longitude;//经度
    private String currentCity;//当前城市
    public static String currentStaticCity;//静态的方便其他模块调用
    private GeoCoder search;//百度查询组件
    private MyLocateListener myLocateListener;//百度定位回调
    private static boolean locatePermission = true;

    private static BaiduLocate ourInstance;

    public static BaiduLocate getInstance() {
        return ourInstance;
    }

    public static void InitContext(Context context) {
        mContext = context;
        ourInstance = new BaiduLocate();
    }

    private BaiduLocate() {
        init();
    }

    private void init() {
        mClient = new LocationClient(mContext);
        mClient.registerLocationListener(this);//设置监听器
        LocationClientOption option = new LocationClientOption();//设置监听参数
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);//设置刷新时间
        option.setIsNeedAddress(true);//设置允许地址与经纬度转换
        mClient.setLocOption(option);
        search = GeoCoder.newInstance();//初始化百度查询功能
        search.setOnGetGeoCodeResultListener(this);
    }

    public void startLocate() {
        mClient.start();

    }

    public void stopLocate() {
        mClient.stop();
    }

    public void destoryLocate() {
        search.destroy();
    }

    @Override
    public void onReceiveLocation(BDLocation location) {
        // map view 销毁后不在处理新接收的位置
        if (location == null)
            return;
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        Log.i(TAG, "定位监听运行到此处" + isLoc);
        if (isLoc) {
            try {
                currentCity = location.getCity().substring(0, location.getCity().length() - 1);
                search(currentCity, currentCity);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onGetGeoCodeResult(GeoCodeResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Log.i(TAG, "搜索失败 no result");
            return;
        }
        Log.i(TAG, "搜索成功" + result.getLocation() + " location " + result.getLocation().latitude + "---" + result.getLocation().longitude);
        if (isLoc == true) {
            latitude = result.getLocation().latitude;
            longitude = result.getLocation().longitude;
            currentStaticCity = currentCity;
            isLoc = false;
            stopLocate();
            if (myLocateListener != null) {
                myLocateListener.LocateCallBack(Constant.LOCATE_SUCCESS, currentCity, null);
            }
        }
    }

    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {

    }

    /**
     * @param city    所在城市
     * @param address 具体位置
     */
    public boolean search(String city, String address) {
        GeoCodeOption info = new GeoCodeOption();
        info.address(address);
        info.city(city);
        return search.geocode(info);
    }


    /**
     * @param myLocateListener 设置回调监听
     */
    public void setMyLocateListener(MyLocateListener myLocateListener) {
        this.myLocateListener = myLocateListener;
    }


    /**
     * 定位成功接口回调
     */
    public interface MyLocateListener {
        void LocateCallBack(int flag, String city, String address);
    }

    /**
     * 当前定位到的城市 若没开启定位功能 返回null
     *
     * @return 城市
     */
    public static String getCurrentCity() {
        if (locatePermission) {
            return currentStaticCity;
        } else {
            return null;
        }
    }
}
