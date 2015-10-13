package com.onlylemi.dr.fragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.onlylemi.dr.util.BaiduLocate;
import com.onlylemi.dr.util.NetworkJudge;
import com.onlylemi.indoor.R;
import com.onlylemi.parse.Data;
import com.onlylemi.parse.info.PlaceTable;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MapFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapFragment extends Fragment implements BaiduMap.OnMarkerClickListener, OnGetGeoCoderResultListener {

    public static final String TAG = "MapFragment";

    private Context mContext;
    public MapView mapView; //百度xml  mapview
    private BaiduMap baiduMap;//百度地图实例
    private GeoCoder search;
    private boolean isFirstLoc = true; // 是否定位
    private double latitude; //城市中心经度
    private double longitude;//城市中心维度
    private String currentCity;//当前城市
    private String selectCity;//选中城市
    private Map<LatLng, PlaceTable> map = new HashMap<>();
    private Map<Marker, PlaceTable> mapMarker = new HashMap<>();
    private InfoWindow infoWindow;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MapFragment.
     */
    public static MapFragment newInstance() {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mContext = getActivity();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_map, container, false);
        mapView = (MapView) view.findViewById(R.id.bmapView);
        mapView.showZoomControls(false);
        baiduMap = mapView.getMap();
        baiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);//设置地图类型
        baiduMap.setOnMarkerClickListener(this);
        search = GeoCoder.newInstance();//初始化搜索
        search.setOnGetGeoCodeResultListener(this);//设置搜索监听
        if (initLocation()) {
            initPlace();
        } else {
            if(NetworkJudge.isWifiEnabled(mContext)) {
                Toast.makeText(mContext, "初始化定位失败， 请手动选择城市 ", Toast.LENGTH_LONG).show();
            }else {
                Toast.makeText(mContext, "初始化定位失败，  no wi-fi", Toast.LENGTH_LONG).show();
            }
        }
        return view;
    }

    /**
     * 初始化地图上的景点
     */
    private void initPlace() {
        int cid = Data.getCityId(currentCity);
        for (PlaceTable placeTable : Data.placeTableList) {
            if (placeTable.getCid() == cid) {
                LatLng latLng = new LatLng(placeTable.getLat(), placeTable.getLng());
                OverlayOptions options = new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory
                        .fromResource(R.mipmap.position_buildingone));
                Marker marker = (Marker) baiduMap.addOverlay(options);
                mapMarker.put(marker, placeTable);
                Log.i(TAG, "palcetable :  " + placeTable.toString());
            }
        }
    }

    /**
     * 初始化地图中心
     */
    private boolean initLocation() {
        if (BaiduLocate.getCurrentCity() != null) {
            //更新地图中心
            currentCity = BaiduLocate.getCurrentCity();
            Log.i(TAG, "map init location success");
            baiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(new LatLng(BaiduLocate.latitude, BaiduLocate.longitude)));
            return true;
        } else {
            Log.i(TAG, "init location faile");
            return false;
        }
    }

    /**
     * Rename method, update argument and hook method into UI event
     *
     * @param uri 待定
     */
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * baidu map mark click
     *
     * @param marker 自定义地图上的点
     * @return flag
     */
    @Override
    public boolean onMarkerClick(Marker marker) {
        PlaceTable data;
        if ((data = mapMarker.get(marker)) != null) {
            TextView textView = new TextView(mContext);
            textView.setBackgroundColor(Color.CYAN);
            textView.setTextColor(mContext.getResources().getColor(R.color.turquoise));
            textView.setText(data.getName());
            ImageView imageView = new ImageView(mContext);
            //Todo:异步加载图片待写
            final LinearLayout layout = new LinearLayout(mContext);
            layout.addView(imageView, 160, 160);
            layout.addView(textView);
            infoWindow = new InfoWindow(layout, marker.getPosition(), -94);
            baiduMap.showInfoWindow(infoWindow);
            Log.e(TAG, "程序OK");

        } else {
            Log.e(TAG, "程序逻辑出错");
        }
        return true;
    }

    /**
     * 栓换地图模式
     */
    public void switchModel() {
        if (baiduMap.getMapType() == BaiduMap.MAP_TYPE_SATELLITE) {
            baiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        }else {
            baiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
        }
    }
    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    /**
     * update map through number data
     *
     * @param latLng
     */
    public void updateMap(LatLng latLng) {
        MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(latLng);
        baiduMap.animateMapStatus(u);
    }

    /**
     * update map through address
     *
     * @param city    city
     * @param address address int the city
     */
    public void updateMap(String city, String address) {
        this.currentCity = city;
        search.geocode(new GeoCodeOption().city(city).address(address));
    }

    /**
     * baidu map search + result
     *
     * @param result result
     */
    @Override
    public void onGetGeoCodeResult(GeoCodeResult result) {
        if (result != null) {
            Log.i(TAG, "map start again locate");
            MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(result.getLocation());
            baiduMap.animateMapStatus(u);
        }
    }

    /**
     * baidu map search - result
     *
     * @param result result
     */
    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {

    }

    @Override
    public void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        search.destroy();
        baiduMap = null;
        mapView.onDestroy();
        super.onDestroy();
    }
}
