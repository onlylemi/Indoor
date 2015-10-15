package com.onlylemi.indoor;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.indooratlas.android.CalibrationState;
import com.indooratlas.android.FloorPlan;
import com.indooratlas.android.FutureResult;
import com.indooratlas.android.IndoorAtlas;
import com.indooratlas.android.IndoorAtlasException;
import com.indooratlas.android.IndoorAtlasFactory;
import com.indooratlas.android.IndoorAtlasListener;
import com.indooratlas.android.ResultCallback;
import com.indooratlas.android.ServiceState;
import com.onlylemi.camera.PreviewSurface;
import com.onlylemi.camera.RouteSurface;
import com.onlylemi.dr.listViewAnimation.ScaleInAnimationAdapter;
import com.onlylemi.dr.util.UpdateThread;
import com.onlylemi.map.MapView;
import com.onlylemi.map.MapViewListener;
import com.onlylemi.map.core.PMark;
import com.onlylemi.map.overlay.BitmapOverlay;
import com.onlylemi.map.overlay.LocationOverlay;
import com.onlylemi.map.overlay.MarkOverlay;
import com.onlylemi.map.overlay.RouteOverlay;
import com.onlylemi.parse.JSONParseTable;
import com.onlylemi.parse.info.ActivityTable;
import com.onlylemi.parse.info.UserPositionTable;
import com.onlylemi.utils.Assist;
import com.onlylemi.utils.Constants;
import com.onlylemi.view.dialog.AddViewsActivityDialog;
import com.onlylemi.view.dialog.LoginDialog;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by only乐秘 on 2015-08-23.
 */
public class IndoorActivity extends BaseActivity implements View.OnClickListener, IndoorAtlasListener,
        MapViewListener, SensorEventListener, MarkOverlay.MarkIsClickListener, AdapterView.OnItemClickListener {

    public static final String TAG = "IndoorActivity:";

    public static String apiVenueId; // 地点id
    public static String apiFloorId; // 楼层id
    public static String apiFloorPlanId; // 楼层平面图id
    public static float mapDegree = 0.0f;

    //list
    public static List<PMark> views; //景点集
    public static List<PointF> nodes; //结点集
    public static List<PointF> nodesContact; //结点关系集
    public static List<ActivityTable> viewsActivityList; //活动列表
    private List<Integer> viewsActivityVidList; //活动 views id

    //头部
    private RelativeLayout layout_action;
    private LinearLayout layout_all;
    private TextView tv_color;

    private Button floor_1;
    private Button floor_2;
    private Button floor_3;
    private PopupWindow morePop;
    private ImageView addViewActivityBtn;

    //mapview
    private MapView mapView;
    private ImageView imgPosition;
    private ImageView imgCameraPosition;

    //map图层
    private LocationOverlay locationOverlay;
    private RouteOverlay routeOverlay;
    private MarkOverlay markOverlay;
    private BitmapOverlay bitmapOverlay;
    //路线list
    private List<Integer> routeList;
    private List<Float> routeListDegrees;
    private List<Integer> selectViewsList;

    //加载
    private RelativeLayout progress;
    private LinearLayout layout_no;
    private TextView tv_no;

    //indooratlas
    private IndoorAtlas mIndoorAtlas;
    private boolean mIsPositioning;

    //传感器
    private SensorManager sensorManager;

    //mark介绍
    private PopupWindow markPop;
    private TextView mark_name;
    private TextView mark_distance;
    private LinearLayout mark_intro;
    private LinearLayout mark_route;


    //照相机预览视图
    private RelativeLayout cameraRouteLayout;
    private PreviewSurface previewCameraSurface;
    private RouteSurface routeCameraSurface;

    private boolean isMapViewSmall = false;

    private String deviceId = "";

    //activity list
    private ListView listViewActivity;
    private ViewsActivityAdapter viewsActivityAdapter;

    private float startDegree;


    private SwipeRefreshLayout viewsActivitySwipe;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_indoor);

        Bundle bundle = this.getIntent().getExtras();
        selectViewsList = bundle.getIntegerArrayList("select_views_list");
        Log.i(TAG, selectViewsList.toString());
    }

    @Override
    public void initViews() {
        progress = (RelativeLayout) findViewById(R.id.progress);
        layout_no = (LinearLayout) findViewById(R.id.layout_no);
        tv_no = (TextView) findViewById(R.id.tv_no);

        layout_action = (RelativeLayout) findViewById(R.id.layout_action);
        layout_all = (LinearLayout) findViewById(R.id.layout_all);
        // 默认是ColorFly界面
        tv_color = (TextView) findViewById(R.id.tv_color);
        tv_color.setTag("floor_1");

        //mapview
        mapView = (MapView) findViewById(R.id.mapview);
        imgPosition = (ImageView) findViewById(R.id.position_btn);
        imgCameraPosition = (ImageView) findViewById(R.id.camera_position);

        addViewActivityBtn = (ImageView) findViewById(R.id.add_views_activity_btn);

        //传感器
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        //照相机
        cameraRouteLayout = (RelativeLayout) findViewById(R.id.camera_route);
        previewCameraSurface = (PreviewSurface) findViewById(R.id.preview_camera_surface);
        routeCameraSurface = (RouteSurface) findViewById(R.id.route_camera_surface);

        settingViewsActivitySwipe();

        //侧边商家活动
        viewsActivityAdapter = new ViewsActivityAdapter(this);
        listViewActivity = (ListView) findViewById(R.id.views_activity_list);
        ScaleInAnimationAdapter scaleInAnimationAdapter = new ScaleInAnimationAdapter(viewsActivityAdapter, 0f);
        scaleInAnimationAdapter.setListView(listViewActivity);
        listViewActivity.setAdapter(scaleInAnimationAdapter);

        viewsActivityVidList = viewsActivityAdapter.getViewsActivityVidList();
    }

    @Override
    public void initListeners() {
        layout_all.setOnClickListener(this);
        mapView.registerMapViewListener(this);
        imgPosition.setOnClickListener(this);
        imgCameraPosition.setOnClickListener(this);
        listViewActivity.setOnItemClickListener(this);
        addViewActivityBtn.setOnClickListener(this);

        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_NORMAL);
        //加速度
//        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
//                SensorManager.SENSOR_DELAY_NORMAL);
        //磁力
//        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
//                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void initData() {
        apiVenueId = Constants.TANGJIU_VENUE_Id;
        apiFloorId = Constants.TANGJIU_FLOOR_1_Id;
        apiFloorPlanId = Constants.TANGJIU_FLOOR_1_PLAN_Id;

        int pid = 2;
        int fn = 1;
        views = JSONParseTable.getViewsList(pid, fn);
        nodes = JSONParseTable.getNodesList(pid, fn);
        nodesContact = JSONParseTable.getNodesContactList(pid, fn);

        deviceId = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);


        initIndoorAtlas();
        loadMapView();
    }

    private void showView() {
        mapView.setVisibility(View.VISIBLE);
        layout_no.setVisibility(View.GONE);
        progress.setVisibility(View.VISIBLE);
    }


    /**
     * 展示楼层选择list
     */
    private void showListPop() {
        View view = LayoutInflater.from(this).inflate(R.layout.pop_color, null);
        // 注入
        floor_1 = (Button) view.findViewById(R.id.layout_floor_1);
        floor_2 = (Button) view.findViewById(R.id.layout_floor_2);
        floor_3 = (Button) view.findViewById(R.id.layout_floor_3);

        floor_1.setOnClickListener(this);
        floor_2.setOnClickListener(this);
        floor_3.setOnClickListener(this);
        morePop = new PopupWindow(view, mScreenWidth, 600);

        morePop.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    morePop.dismiss();
                    return true;
                }
                return false;
            }
        });

        morePop.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        morePop.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        morePop.setTouchable(true);
        morePop.setFocusable(true);
        morePop.setOutsideTouchable(true);
        morePop.setBackgroundDrawable(new BitmapDrawable());
        // 动画效果 从顶部弹下
        morePop.setAnimationStyle(R.style.MenuPop);
        morePop.showAsDropDown(layout_action, 0, -dip2px(this, 2.0F));
    }

    /**
     * 改变头部
     *
     * @param v
     */
    private void changeTextView(View v) {
        if (v == floor_1) {
            tv_color.setText("第 一 层");
        } else if (v == floor_2) {
            tv_color.setText("第 二 层");
        } else if (v == floor_3) {
            tv_color.setText("第 三 层");
        }
    }

    /**
     * 请求出错或者无数据时候显示的界面 showErrorView
     */
    private void showErrorView() {
        progress.setVisibility(View.GONE);
        mapView.setVisibility(View.GONE);
        layout_no.setVisibility(View.VISIBLE);

        tv_no.setText("暂无数据");
    }

    /**
     * 初始化 mIndoorAtlas
     */
    private void initIndoorAtlas() {
        Log.i(TAG, "initIndoorAtlas start");

        try {
            mIndoorAtlas = IndoorAtlasFactory.createIndoorAtlas(this.getApplicationContext(), this,
                    Constants.INDOORATLAS_API_KEY, Constants.INDOORATLAS_API_SECRET);
            Log.i(TAG, "mIndoorAtlas 创建");
            //togglePositioning();
        } catch (IndoorAtlasException e) {
            e.printStackTrace();
        }
    }

    /**
     * 加载地图
     */
    private void loadMapView() {
        showView();
        //加载平面图
        FutureResult<FloorPlan> result = mIndoorAtlas.fetchFloorPlan(apiFloorPlanId);
        result.setCallback(new ResultCallback<FloorPlan>() {
            @Override
            public void onResult(final FloorPlan floorPlan) {
                loadFloorPlanImage(floorPlan);
            }

            @Override
            public void onSystemError(IOException e) {
                Log.e(TAG, "FloorPlanTable onSystemError:" + e);
                showErrorView();
            }

            @Override
            public void onApplicationError(IndoorAtlasException e) {
                Log.e(TAG, "FloorPlanTable onApplicationError:" + e);
                showErrorView();
            }
        });
    }

    /**
     * 加载平面图
     *
     * @param floorPlan
     */
    private void loadFloorPlanImage(FloorPlan floorPlan) {
        BitmapFactory.Options options = createBitmapOptions(floorPlan);
        FutureResult<Bitmap> result = mIndoorAtlas.fetchFloorPlanImage(floorPlan, options);
        result.setCallback(new ResultCallback<Bitmap>() {
            @Override
            public void onResult(final Bitmap bitmap) {
                // now you have floor plan bitmap, do something with it
                updateImageViewInUiThread(bitmap);
            }

            @Override
            public void onSystemError(IOException e) {
                Log.e(TAG, "Bitmap onSystemError:" + e);
                showErrorView();
            }

            @Override
            public void onApplicationError(IndoorAtlasException e) {
                Log.e(TAG, "Bitmap onSystemError:" + e);
                showErrorView();
            }
        });
    }

    private void updateImageViewInUiThread(final Bitmap bitmap) {
        Log.i(TAG, "width:" + bitmap.getWidth() + " height:" + bitmap.getHeight());

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mapView.getOverLays().clear();
                mapView.loadMap(Assist.getPictureFromBitmap(bitmap));
                mapView.getController().initMapMainView(0.0f);

                progress.setVisibility(View.GONE);
            }
        });
    }

    private BitmapFactory.Options createBitmapOptions(FloorPlan floorPlan) {
        BitmapFactory.Options options = new BitmapFactory.Options();

        int reqWidth = 2048;
        int reqHeight = 2048;
        final int width = (int) floorPlan.dimensions[0];
        final int height = (int) floorPlan.dimensions[1];
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        options.inSampleSize = inSampleSize;
        return options;
    }

    /**
     * 释放定位
     */
    private void tearDown() {
        if (mIndoorAtlas != null) {
            mIndoorAtlas.tearDown();
        }
    }

    /**
     * 停止定位
     */
    private void stopPositioning() {
        mIsPositioning = false;
        if (mIndoorAtlas != null) {
            Log.i(TAG, "Stop positioning");
            mIndoorAtlas.stopPositioning();
        }
    }

    /**
     * 开始定位
     */
    private void startPositioning() {
        if (mIndoorAtlas != null) {
            try {
                mIndoorAtlas.startPositioning(apiVenueId,
                        apiFloorId,
                        apiFloorPlanId);
                mIsPositioning = true;
            } catch (IndoorAtlasException e) {
                Log.i(TAG, "startPositioning failed: " + e);
            }
        } else {
            Log.i(TAG, "calibration not ready, cannot start positioning");
        }
    }

    /**
     * 判断是否定位
     */
    private void togglePositioning() {
        if (mIsPositioning) {
            stopPositioning();
            Toast.makeText(this, "stop position", Toast.LENGTH_SHORT).show();
        } else {
            startPositioning();
            Toast.makeText(this, "start position", Toast.LENGTH_SHORT).show();
        }
    }

    // OnClickListener
    //==================================================================================

    @Override
    public void onClick(View v) {
        if (v == addViewActivityBtn) {
            if ("".equals(com.onlylemi.user.Assist.user)) {
                LoginDialog dialog = new LoginDialog(this);
                dialog.show();
            } else {
                Toast.makeText(this, com.onlylemi.user.Assist.user + "在线！", Toast.LENGTH_SHORT).show();
                AddViewsActivityDialog dialog = new AddViewsActivityDialog(this, views);
                dialog.show();
            }
        } else if (v == layout_all) {
            showListPop();
        } else if (v == floor_1) {
            changeTextView(v);
            morePop.dismiss();
            apiFloorId = Constants.TANGJIU_FLOOR_1_Id;
            apiFloorPlanId = Constants.TANGJIU_FLOOR_1_PLAN_Id;
            mapDegree = 0.0f;
            togglePositioning();
            loadMapView();
        } else if (v == floor_2) {
            morePop.dismiss();
            /*changeTextView(v);
            apiFloorId = Constants.INDOORATLAS_FLOOR_2_Id;
            apiFloorPlanId = Constants.INDOORATLAS_FLOOR_2_PLAN_Id;
            mapDegree = 0;
            mapView.setVisibility(View.GONE);
            togglePositioning();
            loadMapView();*/
            Toast.makeText(this, "该地区仅有一层", Toast.LENGTH_SHORT).show();
        } else if (v == floor_3) {
            morePop.dismiss();
            /*changeTextView(v);
            apiFloorId = Constants.INDOORATLAS_FLOOR_3_Id;
            apiFloorPlanId = Constants.INDOORATLAS_FLOOR_3_PLAN_Id;
            mapDegree = 0;
            togglePositioning();
            loadMapView();*/
            Toast.makeText(this, "该地区仅有一层", Toast.LENGTH_SHORT).show();
        } else if (v == imgPosition) {
            togglePositioning();
        } else if (v == imgCameraPosition) {
            //startActivity(new Intent().setClass(IndoorActivity.this, CameraActivity.class));

            ViewGroup.LayoutParams lp = mapView.getLayoutParams();
            if (!isMapViewSmall) {
                lp.height = mapView.getHeight() / 3;
                lp.width = mapView.getWidth() / 3;
                cameraRouteLayout.setVisibility(View.VISIBLE);
                previewCameraSurface.setVisibility(View.VISIBLE);
                routeCameraSurface.setVisibility(View.VISIBLE);
//                mapView.getController().setCurrentZoomValue(0.5f);
            } else {
                lp.height = mapView.getHeight() * 3;
                lp.width = mapView.getWidth() * 3;
                cameraRouteLayout.setVisibility(View.GONE);
                previewCameraSurface.setVisibility(View.GONE);
                routeCameraSurface.setVisibility(View.GONE);
            }
            isMapViewSmall = !isMapViewSmall;
            mapView.setLayoutParams(lp);


        } else if (v == mark_intro) {
            markPop.dismiss();

        } else if (v == mark_route) {
            markPop.dismiss();
            routeList = new ArrayList<>();

            PointF target = new PointF(views.get(markOverlay.getNum()).x, views.get(markOverlay.getNum()).y);

            routeList = com.onlylemi.map.utils.Assist.getShortestDistanceBetweenTwoPoints(locationOverlay.getPosition(),
                    target, nodes, nodesContact);
            routeListDegrees = com.onlylemi.map.utils.Assist.getDegreeBetweenTwoPointsWithVertical(routeList, nodes);
            Log.i(TAG, routeListDegrees.toString());

            if (mapView.isMapLoadFinsh()) {
                routeOverlay.setNodeList(nodes);
                routeOverlay.setRouteList(routeList);
                mapView.refresh();
            }
        }
    }

    // MapViewListener
    //==================================================================================

    @Override
    public void onMapLoadComplete() {
        //活动层
        bitmapOverlay = new BitmapOverlay(mapView);
        bitmapOverlay.setViews(views);
        bitmapOverlay.setViewsActivityVidList(viewsActivityVidList);
        mapView.getOverLays().add(bitmapOverlay);

        // 标记点层
        markOverlay = new MarkOverlay(mapView, 20);
        markOverlay.setMarkIsClickListener(this);
        markOverlay.setMarks(views);
        mapView.getOverLays().add(markOverlay);

        // 路径层
        if (selectViewsList.size() != 0 && views.size() != 0) {
            List<PointF> list = new ArrayList<>();
            list.add(new PointF(views.get(views.size() - 1).x, views.get(views.size() - 1).y));
            for (int i = 0; i < selectViewsList.size(); i++) {
                list.add(new PointF(views.get(selectViewsList.get(i)).x, views.get(selectViewsList.get(i)).y));
            }
            routeList = com.onlylemi.map.utils.Assist.getShortestPathBetweenPoints(list, nodes, nodesContact);
        }

        routeOverlay = new RouteOverlay(mapView, nodes,
                routeList);
        mapView.getOverLays().add(routeOverlay);

        // 定位层
        locationOverlay = new LocationOverlay(mapView,
                new PointF(400, 500), -45, 90);
        mapView.getOverLays().add(locationOverlay);
        mapView.refresh();
    }

    @Override
    public void onMapLoadError() {
        Log.i(TAG, "onMapLoadError");
    }

    @Override
    public void onGetCurrentMap(Bitmap bitmap) {

    }

    // IndoorAtlasListener
    //==================================================================================

    @Override
    public void onServiceUpdate(ServiceState state) {
        Log.i(TAG, "onServiceUpdate");

        if (mapView.isMapLoadFinsh()) {
            double lat = state.getGeoPoint().getLatitude();
            double lon = state.getGeoPoint().getLongitude();
            int i = state.getImagePoint().getI();
            int j = state.getImagePoint().getJ();
            double x = state.getMetricPoint().getX();
            double y = state.getMetricPoint().getY();
            double heading = state.getHeadingDegrees();
            double uncertainty = state.getUncertainty();
            long roundtrip = state.getRoundtrip();
            int pid = 2;
            int fn = 1;
            SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            //更新坐标
            locationOverlay.setPosition(new PointF(i, j));
            mapView.refresh();

            UserPositionTable userPositionTable = new UserPositionTable();
            userPositionTable.setDeviceId(deviceId);
            userPositionTable.setLat(lat);
            userPositionTable.setLon(lon);
            userPositionTable.setI(i);
            userPositionTable.setJ(j);
            userPositionTable.setX(x);
            userPositionTable.setY(y);
            userPositionTable.setHeading(heading);
            userPositionTable.setUncertainty(uncertainty);
            userPositionTable.setRoundtrip(roundtrip);
            userPositionTable.setTime(sd.format(new Date().getTime()));
            userPositionTable.setPid(pid);
            userPositionTable.setFn(fn);
            //上传
//            new JSONUpload().uploadPositionData("POST", userPositionTable);
        }

    }

    @Override
    public void onServiceFailure(int i, String s) {
        Log.i(TAG, "onServiceFailure");
    }

    @Override
    public void onServiceInitializing() {
        Log.i(TAG, "onServiceInitializing");
    }

    @Override
    public void onServiceInitialized() {
        Log.i(TAG, "onServiceInitialized");
    }

    @Override
    public void onInitializationFailed(String s) {
        Log.i(TAG, "onInitializationFailed");
    }

    @Override
    public void onServiceStopped() {
        Log.i(TAG, "onServiceStopped");
    }

    @Override
    public void onCalibrationReady() {
        Log.i(TAG, "onCalibrationReady");
    }

    @Override
    public void onCalibrationInvalid() {
        Log.i(TAG, "onCalibrationInvalid");
    }

    @Override
    public void onCalibrationFailed(String s) {
        Log.i(TAG, "onCalibrationFailed");
    }

    @Override
    public void onCalibrationStatus(CalibrationState calibrationState) {
        Log.i(TAG, "onCalibrationStatus, percentage" + calibrationState.getPercentage());
    }

    @Override
    public void onNetworkChangeComplete(boolean b) {
        Log.i(TAG, "onNetworkChangeComplete");
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "onDestroy");
        super.onDestroy();
        tearDown();
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    // SensorEventListener
    //==================================================================================

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (mapView.isMapLoadFinsh()) {

            //根据加速度与磁力计算方向
            /*float[] accelerometerValues = new float[3];
            float[] magneticFieldValues = new float[3];
            float[] values = new float[3];
            float[] rotate = new float[9];
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                accelerometerValues = event.values.clone();
            }
            if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                magneticFieldValues = event.values.clone();
            }
            Log.i(TAG, "accelerometerValues:" + Arrays.toString(accelerometerValues));
            Log.i(TAG, "magneticFieldValues:" + Arrays.toString(accelerometerValues));
            SensorManager.getRotationMatrix(rotate, null, accelerometerValues, magneticFieldValues);
            Log.i(TAG, "rotate:" + Arrays.toString(rotate));
            SensorManager.getOrientation(rotate, values);
            Log.i(TAG, "values:" + Arrays.toString(values));*/

            float degree = 0;
            if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
                degree = -event.values[0];
//                Log.i(TAG, "value[0]:" + values[0]);
//                Log.i(TAG, "value[1]:" + values[1]);
//                Log.i(TAG, "value[2]:" + values[2]);
            }

            if (isMapViewSmall) {
                mapView.getController().setMapCenterWithPoint(locationOverlay.getPosition());
                mapView.getController().setCurrentRotationDegrees(mapDegree + degree);
                if (routeListDegrees != null) {
                    float routeDegree = (routeListDegrees.get(0) + mapView.getCurrentRotateDegrees()) % 360.0f;
                    if (routeDegree > 180.0f) {
                        routeDegree = routeDegree - 360.0f;
                    } else if (routeDegree < -180.0f) {
                        routeDegree = routeDegree + 360.0f;
                    }

                    if (Math.abs(routeDegree - startDegree) > 10) {
                        routeCameraSurface.setRotateDegree(routeDegree);
                        routeCameraSurface.refresh();
                        startDegree = routeDegree;
                    }
                    //Log.i(TAG, "routeDegree:" + routeDegree + "");
                }
            }

            locationOverlay.setIndicatorCircleRotateDegree(degree);
            locationOverlay.setIndicatorArrowRotateDegree(mapDegree + mapView.getCurrentRotateDegrees() - degree);
            mapView.refresh();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    // MarkOverlayListener
    //==================================================================================

    @Override
    public void markIsClick() {
        if (markOverlay.getIsClickMark()) {
            showMarkPop(markOverlay.getNum());
        } else {
            markPop.dismiss();
        }
    }

    //展示mark介绍窗口
    private void showMarkPop(int num) {
        if (markPop != null) {
            markPop.dismiss();
        }

        View view = LayoutInflater.from(this).inflate(R.layout.mark_show, null);

        mark_name = (TextView) view.findViewById(R.id.mark_name);
        mark_distance = (TextView) view.findViewById(R.id.mark_distance);
        mark_intro = (LinearLayout) view.findViewById(R.id.mark_intro);
        mark_route = (LinearLayout) view.findViewById(R.id.mark_route);

        float distance = (Math.abs(views.get(num).x - locationOverlay.getPosition().x) +
                Math.abs(views.get(num).y - locationOverlay.getPosition().y)) / 25;

        mark_name.setText(views.get(num).name);
        mark_distance.setText(distance + "米");

        mark_intro.setOnClickListener(this);
        mark_route.setOnClickListener(this);

        markPop = new PopupWindow(view, 720, 600);
        markPop.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        markPop.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        //markPop.setFocusable(true);
        markPop.setTouchable(true);
        //markPop.setOutsideTouchable(true);
        markPop.setBackgroundDrawable(new BitmapDrawable());

        markPop.setAnimationStyle(R.style.MarkPop);
        markPop.showAsDropDown(imgPosition);
    }


    //AdapterView.OnItemClickListener
    //=============================================================================

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ActivityTable activityTable = (ActivityTable) viewsActivityAdapter.getItem(position);

        if (mapView.isMapLoadFinsh() && !isMapViewSmall) {
            for (int i = 0; i < views.size(); i++) {
                if (views.get(i).id == activityTable.getVid()) {
                    Log.i(TAG, views.get(i).name + " " + views.get(i).id + " " + activityTable.getVid());
                    mapView.getController().setMapCenterWithPoint(new PointF(views.get(i).x,
                            views.get(i).y));
                    mapView.refresh();
                    break;
                }
            }
        }
    }


    /**
     * 设置viewsActivity 下拉刷新
     */
    private void settingViewsActivitySwipe() {
        viewsActivitySwipe = (SwipeRefreshLayout) findViewById(R.id.views_activity_swipe);
        viewsActivitySwipe.setColorSchemeColors(getResources().getColor(R.color.custom_test_five_title_background)
                , getResources().getColor(R.color.custom_test_four)
                , getResources().getColor(R.color.custom_test_one)
                , getResources().getColor(R.color.custom_test_three));
        viewsActivitySwipe.setProgressBackgroundColorSchemeColor(getResources().getColor(R.color.gray));
        viewsActivitySwipe.setSize(SwipeRefreshLayout.LARGE);
        viewsActivitySwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                UpdateThread.getInstance().updateActivity(new UpdateThread.UpdateListener() {
                    @Override
                    public void success() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                viewsActivityAdapter = new ViewsActivityAdapter(IndoorActivity.this);
                                //viewsActivityAdapter.update();
                                viewsActivityAdapter.notifyDataSetChanged();
                                Toast.makeText(IndoorActivity.this, "活动列表更新成功！", Toast.LENGTH_SHORT).show();
                                viewsActivitySwipe.setRefreshing(false);
                            }
                        });
                    }

                    @Override
                    public void faile() {

                    }
                });
            }
        });
    }


}
