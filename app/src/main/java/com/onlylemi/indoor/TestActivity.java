package com.onlylemi.indoor;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.onlylemi.camera.PreviewSurface;
import com.onlylemi.camera.RouteSurface;
import com.onlylemi.view.dialog.AddViewsActivityDialog;
import com.onlylemi.map.MapView;
import com.onlylemi.map.MapViewListener;
import com.onlylemi.map.core.PMark;
import com.onlylemi.map.overlay.LocationOverlay;
import com.onlylemi.map.overlay.MarkOverlay;
import com.onlylemi.map.overlay.RouteOverlay;
import com.onlylemi.map.utils.Assist;
import com.onlylemi.parse.JSONParseTable;

public class TestActivity extends AppCompatActivity implements OnClickListener,
        MarkOverlay.MarkIsClickListener, SensorEventListener {

    private static final String TAG = "TestActivity:";

    private MapView mapView;
    private Button button, button2;
    private ImageView imageView;

    private List<PMark> views; //景点集
    private List<PointF> nodes; //节点集
    private List<PointF> nodesContact; //节点连通集

    //图层
    private LocationOverlay locationOverlay;
    private RouteOverlay routeOverlay;
    private MarkOverlay markOverlay;

    private List<Integer> routeList; //路线list
    private List<Float> routeListDegrees;

    private PopupWindow markPop;

    private TextView mark_name;
    private TextView mark_distance;
    private LinearLayout mark_intro;
    private LinearLayout mark_route;

    //传感器
    private SensorManager sensorManager;
    public static float mapDegree = 0.0f;


    private boolean isMapViewSmall = false;

    private PreviewSurface previewSurface;
    private RouteSurface routeSurface;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        mapView = (MapView) findViewById(R.id.mapview1);
        button = (Button) findViewById(R.id.button1);
        button2 = (Button) findViewById(R.id.button21);
        imageView = (ImageView) findViewById(R.id.position_btn1);

        //传感器
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_NORMAL);

        //获取地图数据
        int pid = 2;
        int fn = 1;
        views = JSONParseTable.getViewsList(pid, fn);
        nodes = JSONParseTable.getNodesList(pid, fn);
        nodesContact = JSONParseTable.getNodesContactList(pid, fn);

//        final Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
//                R.mipmap.supermarket);

        previewSurface = (PreviewSurface) findViewById(R.id.preview_surface1);
        routeSurface = (RouteSurface) findViewById(R.id.route_camera_surface1);

//        final Bitmap bitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getPath() + "/indoor/tangjiu.png");
        final Bitmap bitmap = getImageFromAssetsFile("tangjiu.png");
        mapView.loadMap(com.onlylemi.utils.Assist.getPictureFromBitmap(bitmap));

        mapView.registerMapViewListener(new MapViewListener() {

            @Override
            public void onMapLoadError() {

            }

            @Override
            public void onMapLoadComplete() {
                // 标记点层
                markOverlay = new MarkOverlay(mapView, 20);
                markOverlay.setMarkIsClickListener(TestActivity.this);
                markOverlay.setMarks(views);
                mapView.getOverLays().add(markOverlay);

                // 路径层
                routeOverlay = new RouteOverlay(mapView, nodes,
                        null);
                mapView.getOverLays().add(routeOverlay);

                // 定位层
                locationOverlay = new LocationOverlay(mapView,
                        new PointF(400, 500), -45, 90);
                mapView.getOverLays().add(locationOverlay);
                mapView.refresh();
            }

            @Override
            public void onGetCurrentMap(Bitmap bitmap) {

            }
        });

        button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mapView.isMapLoadFinsh()) {
                    /*PointF i = nodesContact.get(new Random().nextInt(nodesContact.size() - 1));
                    PointF location = AssistMath.getEveryPointBetweenTwoPoints(nodes.get((int) i.x),
                            nodes.get((int) i.y), new Random().nextFloat());
                    locationOverlay.setPosition(location);*/

                    //设置定位点为当前视图的中心
//                    mapView.getController().setMapCenterWithPoint(locationOverlay.getPosition());
//                    mapView.refresh();

                    ViewGroup.LayoutParams lp = mapView.getLayoutParams();
                    if (!isMapViewSmall) {
                        lp.height = mapView.getHeight() / 3;
                        lp.width = mapView.getWidth() / 3;
                        previewSurface.setVisibility(View.VISIBLE);
                        routeSurface.setVisibility(View.VISIBLE);
                    } else {
                        lp.height = mapView.getHeight() * 3;
                        lp.width = mapView.getWidth() * 3;
                        previewSurface.setVisibility(View.GONE);
                        routeSurface.setVisibility(View.GONE);

                    }
                    isMapViewSmall = !isMapViewSmall;
                    mapView.setLayoutParams(lp);


                }
            }
        });

        imageView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                int start = new Random().nextInt(73 + 73) + 73;
                int end;
                while (true) {
                    end = new Random().nextInt(19);
                    if (end != start) {
                        break;
                    }
                }

                routeList = new ArrayList<>();
//                routeList = Assist.getShortestPathBetweenTwoPoints(start,
//                        end, nodes, nodesContact);
                int[] points = {start % 73, (start - 10) % 73, (start - 20) % 73, (start - 30) % 73, (start - 5) % 73, (start + 10) % 73, (start + 20) % 73};

                Log.i(TAG, "points:" + Arrays.toString(points));
                routeList = Assist.getShortestPathBetweenPoints(points,
                        nodes, nodesContact);


                PointF p1 = new PointF(new Random().nextInt(mapView.getWidthFloorPlan()),
                        new Random().nextInt(mapView.getHeightFloorPlan()));
//                PointF p2 = new PointF(new Random().nextInt(mapView.getWidthFloorPlan()),
//                        new Random().nextInt(mapView.getHeightFloorPlan()));
//                routeList = Assist.getShortestDistanceBetweenTwoPoints(p1, p2, nodes, nodesContact);

                Log.i(TAG, "routeList:" + routeList.toString());
                if (mapView.isMapLoadFinsh()) {
                    routeOverlay.setNodeList(nodes);
                    routeOverlay.setRouteList(routeList);
                    locationOverlay.setPosition(p1);
                    mapView.refresh();
                }


            }
        });

        button2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent().setClass(TestActivity.this, IndoorActivity.class));
                /*AddViewsActivityDialog dialog = new AddViewsActivityDialog(TestActivity.this, views, new Handler());
                dialog.show();*/

            }
        });

    }

    private Bitmap getImageFromAssetsFile(String fileName) {
        Bitmap image = null;
        AssetManager am = getResources().getAssets();
        try {
            InputStream is = am.open(fileName);
            image = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }


    /**
     * 节点集
     *
     * @return
     */
    private List<PointF> getNodesList() {
        List<PointF> marks = new ArrayList<PointF>();
        marks.add(new PointF(38, 947));
        marks.add(new PointF(199, 835));
        marks.add(new PointF(119, 566));
        marks.add(new PointF(119, 276));
        marks.add(new PointF(107, 29));
        marks.add(new PointF(362, 286));
        marks.add(new PointF(389, 32));
        marks.add(new PointF(374, 570));
        marks.add(new PointF(393, 835));
        marks.add(new PointF(1094, 854));
        marks.add(new PointF(1434, 856));
        marks.add(new PointF(1097, 582));
        marks.add(new PointF(1412, 566));
        marks.add(new PointF(1714, 570));
        marks.add(new PointF(791, 277));
        marks.add(new PointF(779, 23));
        marks.add(new PointF(1088, 296));
        marks.add(new PointF(1441, 289));
        marks.add(new PointF(1443, 118));

        return marks;
    }

    /**
     * 节点集关系
     *
     * @return
     */
    private List<PointF> getNodesContactList() {
        List<PointF> nodesContact = new ArrayList<PointF>();
        nodesContact.add(new PointF(0, 1));
        nodesContact.add(new PointF(1, 2));
        nodesContact.add(new PointF(2, 3));
        nodesContact.add(new PointF(3, 4));
        nodesContact.add(new PointF(5, 6));
        nodesContact.add(new PointF(5, 7));
        nodesContact.add(new PointF(7, 8));
        nodesContact.add(new PointF(14, 15));
        nodesContact.add(new PointF(9, 11));
        nodesContact.add(new PointF(11, 16));
        nodesContact.add(new PointF(10, 12));
        nodesContact.add(new PointF(12, 17));
        nodesContact.add(new PointF(17, 18));

        nodesContact.add(new PointF(1, 8));
        nodesContact.add(new PointF(8, 9));
        nodesContact.add(new PointF(9, 10));
        nodesContact.add(new PointF(2, 7));
        nodesContact.add(new PointF(7, 11));
        nodesContact.add(new PointF(11, 12));
        nodesContact.add(new PointF(12, 13));
        nodesContact.add(new PointF(3, 5));
        nodesContact.add(new PointF(5, 14));
        nodesContact.add(new PointF(14, 16));
        nodesContact.add(new PointF(16, 17));

        return nodesContact;
    }

    @Override
    public void onClick(View v) {

        if (v == mark_intro) {
            markPop.dismiss();

        } else if (v == mark_route) {
            markPop.dismiss();
            routeList = new ArrayList<>();
            PointF target = new PointF(views.get(markOverlay.getNum()).x, views.get(markOverlay.getNum()).y);

            routeList = Assist.getShortestDistanceBetweenTwoPoints(locationOverlay.getPosition(),
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

    /**
     * 显示mark弹出框
     */
    private void showMarkPop(int num) {
        if (markPop != null) {
            markPop.dismiss();
        }

        View view = LayoutInflater.from(this).inflate(R.layout.mark_show, null);

        mark_name = (TextView) view.findViewById(R.id.mark_name);
        mark_distance = (TextView) view.findViewById(R.id.mark_distance);
        mark_intro = (LinearLayout) view.findViewById(R.id.mark_intro);
        mark_route = (LinearLayout) view.findViewById(R.id.mark_route);

        mark_name.setText(views.get(num).name);
        float distance = (Math.abs(views.get(num).x - locationOverlay.getPosition().x) +
                Math.abs(views.get(num).y - locationOverlay.getPosition().y)) / 25;

        mark_name.setText(views.get(num).name);
        mark_distance.setText(distance + "米");

        mark_intro.setOnClickListener(this);
        mark_route.setOnClickListener(this);

        markPop = new PopupWindow(view, 720, 600);
        /*markPop.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(ViewsTable v, MotionEvent event) {
                *//*if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    markPop.dismiss();
                    return true;
                }*//*
                return false;
            }
        });*/
        markPop.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        markPop.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        //markPop.setFocusable(true);
        markPop.setTouchable(true);
        //markPop.setOutsideTouchable(true);
        markPop.setBackgroundDrawable(new BitmapDrawable());

        markPop.setAnimationStyle(R.style.MarkPop);
        markPop.showAsDropDown(imageView);
    }

    @Override
    public void markIsClick() {
        if (markOverlay.getIsClickMark()) {
            showMarkPop(markOverlay.getNum());
        } else {
            markPop.dismiss();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (sensorManager != null) {
            sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    //========================SensorEventListener=================================

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (mapView.isMapLoadFinsh()) {
            float degree = 0;
            if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
                degree = event.values[0];
            }

            //mapView.getController().setMapCenterWithPoint(locationOverlay.getPosition());
            //mapView.getController().setCurrentRotationDegrees(mapDegree - degree);

            locationOverlay.setIndicatorCircleRotateDegree(-degree);
            locationOverlay.setIndicatorArrowRotateDegree(mapDegree + mapView.getCurrentRotateDegrees() + degree);
            if (routeListDegrees != null) {
                float dddd = (routeListDegrees.get(1) + mapView.getCurrentRotateDegrees()) % 360.0f;
                if (dddd > 180.0f) {
                    dddd = dddd - 360.0f;
                } else if (dddd < -180.0f) {
                    dddd = dddd + 360.0f;
                }
                Log.i(TAG, "routeDegree:" + dddd + "");
            }
            mapView.refresh();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
