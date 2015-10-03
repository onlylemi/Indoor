package com.onlylemi.map.core;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Picture;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

import com.onlylemi.map.MapViewListener;
import com.onlylemi.map.utils.AssistMath;
import com.onlylemi.map.utils.AssistRect;

public class MapMainView extends SurfaceView implements Callback {

    private static final String TAG = "MapMainView:";

    private MapViewListener mapViewListener = null;
    private SurfaceHolder surfaceHolder;
    private List<MapBaseOverlay> layers;
    private MapOverlay mapOverlay;

    private boolean isRotationGestureEnabled = true;
    private boolean isZoomGestureEnabled = true;
    private boolean isScrollGestureEnabled = true;
    private boolean isRotateWithTouchEventCenter = false;
    private boolean isZoomWithTouchEventCenter = false;
    private boolean isMapLoadFinsh = false;

    private static final int TOUCH_STATE_REST = 0;
    private static final int TOUCH_STATE_SCROLLING = 1;
    private static final int TOUCH_STATE_SCALING = 2;
    private static final int TOUCH_STATE_ROTATE = 3;
    private static final int TOUCH_STATE_POINTED = 4;
    private int mTouchState = MapMainView.TOUCH_STATE_REST;

    private float disX;
    private float disY;
    private float disZ;
    private float lastX;
    private float lastY;

    private float minZoomValue = 2.0f; // 默认的最小缩放
    private float maxZoomValue = 3.0f;
    private boolean isFirstPointedMove = true;

    private Matrix matrix = new Matrix(); // 当前地图应用的矩阵变化
    private Matrix savedMatrix = new Matrix(); // 保存手势Down下时的矩阵

    private PointF start = new PointF(); // 手势触摸的起始点
    private PointF mid = new PointF(); // 双指手势的中心点

    private float firstDegrees; //
    private float firstDistance; // 判断旋转和缩放的手势专用

    private float rotateDegrees = 0f;
    private float currentRotateDegrees = 0f;
    private float zoom = 1f;
    private float currentZoom = 1f;

    private Rect dirty = null;

    public MapMainView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MapMainView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initMapView();
    }

    private void initMapView() {
        layers = new ArrayList<MapBaseOverlay>() {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean add(MapBaseOverlay overlay) {
                synchronized (this) {
                    if (this.size() != 0) {
                        if (overlay.showLevel >= this.get(this.size() - 1).showLevel) {
                            super.add(overlay);
                        } else {
                            for (int i = 0; i < this.size(); i++) {
                                if (overlay.showLevel <= this.get(i).showLevel) {
                                    super.add(i, overlay);
                                    break;
                                }
                            }
                        }
                    } else {
                        super.add(overlay);
                    }

                }
                return true;
            }

            @Override
            public void clear() {
                super.clear();
                MapMainView.this.mapOverlay = null;
            }
        };
        getHolder().addCallback(this);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        this.surfaceHolder = holder;
        if (dirty == null || dirty.bottom == 0 || dirty.right == 0) {
            dirty = new Rect(0, 0, this.getWidth(), this.getHeight());
        }
        if (surfaceHolder != null) {
            this.refresh();
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        this.surfaceHolder = holder;
        Canvas canvas = holder.lockCanvas();
        canvas.drawColor(-1);
        holder.unlockCanvasAndPost(canvas);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    public void refresh() {
        try {
            if (surfaceHolder != null) {
                synchronized (this.surfaceHolder) {
                    Canvas canvas = surfaceHolder.lockCanvas(dirty);
                    if (canvas != null) {
                        canvas.drawColor(-1);
                        for (int i = 0; i < layers.size(); i++) {
                            if (layers.get(i).isVisible) {
                                layers.get(i).draw(canvas, matrix, currentZoom,
                                        currentRotateDegrees);
                            }
                        }
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isMapLoadFinsh || mapOverlay == null) {
            return false;
        }

        switch (event.getAction() & MotionEvent.ACTION_MASK) {

            case MotionEvent.ACTION_DOWN:
                savedMatrix.set(matrix);
                start.set(event.getX(), event.getY());
                this.mTouchState = TOUCH_STATE_SCROLLING;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                if (event.getPointerCount() == 2) {
                    this.mTouchState = TOUCH_STATE_POINTED;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                isFirstPointedMove = true;
                this.refresh();
                if (this.mTouchState == TOUCH_STATE_SCALING) {
                    this.zoom = this.currentZoom;
                } else if (this.mTouchState == TOUCH_STATE_ROTATE) {
                    this.rotateDegrees = this.currentRotateDegrees;
                } else if (AssistRect.withRect(event, start.x, start.y, 6)
                        && event.getAction() == MotionEvent.ACTION_UP) {
                    try {
                        for (int i = 0; i < layers.size(); i++) {
                            layers.get(i).onTouch(event);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if (!isRotationGestureEnabled) {
                    // 调整地图的位置居中显示
                    mapCenter(true, true);
                }

                this.mTouchState = TOUCH_STATE_REST;
                break;

            case MotionEvent.ACTION_MOVE:
                if (this.mTouchState == TOUCH_STATE_POINTED) {
                    if (isFirstPointedMove) {
                        midPoint(mid, event);
                        lastX = event.getX(0);
                        lastY = event.getY(0);
                        disX = AssistMath.getDistanceBetweenTwoPoints(
                                event.getX(0), event.getY(0), mid.x, mid.y);
                        isFirstPointedMove = false;
                    } else {
                        savedMatrix.set(matrix);
                        disY = AssistMath.getDistanceBetweenTwoPoints(lastX,
                                lastY, event.getX(0), event.getY(0));
                        disZ = AssistMath.getDistanceBetweenTwoPoints(mid.x,
                                mid.y, event.getX(0), event.getY(0));
                        if (justRotateGesture()) {
                            firstDegrees = rotation(event);
                            this.mTouchState = TOUCH_STATE_ROTATE;
                        } else {
                            firstDistance = spaceBetweenTwoEvents(event);
                            this.mTouchState = TOUCH_STATE_SCALING;
                        }
                    }
                } else if (this.mTouchState == TOUCH_STATE_SCALING) {
                    if (this.isZoomGestureEnabled) {
                        matrix.set(savedMatrix);
                        if (isZoomWithTouchEventCenter) {
                            midPoint(mid, event);
                        } else {
                            mid.x = this.getWidth() / 2;
                            mid.y = this.getHeight() / 2;
                        }
                        float sencondDistance = spaceBetweenTwoEvents(event);
                        float scale = sencondDistance / firstDistance;
                        float ratio = this.zoom * scale;
                        if (ratio < minZoomValue) {
                            ratio = minZoomValue;
                            scale = ratio / this.zoom;
                        } else if (ratio > maxZoomValue) {
                            ratio = maxZoomValue;
                            scale = ratio / this.zoom;
                        }
                        this.currentZoom = ratio;
                        this.matrix.postScale(scale, scale, mid.x, mid.y);
                        this.refresh();
                    }
                } else if (this.mTouchState == TOUCH_STATE_ROTATE) {
                    if (this.isRotationGestureEnabled) {
                        matrix.set(savedMatrix);
                        if (isRotateWithTouchEventCenter) {
                            midPoint(mid, event);
                        } else {
                            mid.x = this.getWidth() / 2;
                            mid.y = this.getHeight() / 2;
                        }
                        float deltaDegrees = rotation(event) - firstDegrees;
                        float tempD = (this.rotateDegrees + deltaDegrees) % 360;
                        this.currentRotateDegrees = tempD > 0 ? tempD : 360 + tempD;
                        this.matrix.postRotate(deltaDegrees, mid.x, mid.y);
                        this.refresh();
                    }
                } else if (this.mTouchState == TOUCH_STATE_SCROLLING) {
                    if (this.isScrollGestureEnabled) {
                        matrix.set(savedMatrix);
                        matrix.postTranslate(event.getX() - start.x, event.getY()
                                - start.y);
                        this.refresh();
                    }
                }
                break;
        }
        return true;
    }

    public void onDestroy() {
        try {
            for (int i = 0; i < layers.size(); i++) {
                layers.get(i).onDestroy();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onPause() {
        try {
            for (int i = 0; i < layers.size(); i++) {
                layers.get(i).onPause();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onResume() {
        try {
            for (int i = 0; i < layers.size(); i++) {
                layers.get(i).onResume();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 得到当前旋转角度
     *
     * @return
     */
    public float getCurrentRotateDegrees() {
        return this.currentRotateDegrees;
    }

    /**
     * 设置当前旋转角度
     *
     * @param degrees
     * @param pivotX
     * @param pivotY
     */
    public void setCurrentRotationDegrees(float degrees, float pivotX,
                                          float pivotY) {
        if (isRotationGestureEnabled) {
            this.matrix.postRotate(-currentRotateDegrees + degrees, pivotX,
                    pivotY);
            this.rotateDegrees = this.currentRotateDegrees = degrees;
            setCurrentRotateDegreesWithRule();
            refresh();
            //mapCenter(true, true);
        }
    }

    /**
     * 得到当前 缩放值
     *
     * @return
     */
    public float getCurrentZoomValue() {
        return this.currentZoom;
    }

    /**
     * 设置当前 缩放值
     *
     * @param zoom
     * @param pivotX
     * @param pivotY
     */
    public void setCurrentZoomValue(float zoom, float pivotX, float pivotY) {
        this.matrix.postScale(zoom / currentZoom, zoom / currentZoom, pivotX,
                pivotY);
        this.zoom = this.currentZoom = zoom;
        this.refresh();
    }

    /**
     * 得到最大 缩放值
     *
     * @return
     */
    public float getMaxZoomValue() {
        return maxZoomValue;
    }

    /**
     * 设置最大 缩放值
     *
     * @param maxZoomValue
     */
    public void setMaxZoomValue(float maxZoomValue) {
        this.maxZoomValue = maxZoomValue;
    }

    /**
     * 返回覆盖层list
     *
     * @return
     */
    public List<MapBaseOverlay> getOverLays() {
        return this.layers;
    }

    public void translateBy(float x, float y) {
        this.matrix.postTranslate(x, y);
    }

    /**
     * 得到当前最小缩放值
     *
     * @return
     */
    public float getMinZoomValue() {
        return minZoomValue;
    }

    /**
     * 设置当前最小缩放值
     *
     * @param minZoomValue
     */
    public void setMinZoomValue(float minZoomValue) {
        this.minZoomValue = minZoomValue;
    }

    /**
     * 得到地图在当前视图下的坐标值
     *
     * @param x
     * @param y
     * @return
     */
    public float[] getMapCoordinateWithScreenCoordinate(float x, float y) {
        Matrix inverMatrix = new Matrix();
        float returnValue[] = {x, y};
        this.matrix.invert(inverMatrix);
        inverMatrix.mapPoints(returnValue);
        return returnValue;
    }

    /**
     * 注册监听器
     *
     * @param mapViewListener
     */
    public void registeMapViewListener(MapViewListener mapViewListener) {
        this.mapViewListener = mapViewListener;
    }

    /**
     * 加载室内平面图
     *
     * @param picture
     */
    public void loadMap(final Picture picture) {
        isMapLoadFinsh = false;
        new Thread() {
            @Override
            public void run() {
                super.run();
                if (picture != null) {
                    if (MapMainView.this.mapOverlay == null) {
                        MapMainView.this.mapOverlay = new MapOverlay(
                                MapMainView.this);
                        MapMainView.this.getOverLays().add(mapOverlay);
                    }
                    MapMainView.this.mapOverlay.setData(picture);
                    Log.i(TAG, "mapLoadFinished");
                    if (mapViewListener != null) {
                        mapViewListener.onMapLoadComplete();
                    }
                    isMapLoadFinsh = true;
                } else {
                    if (mapViewListener != null) {
                        mapViewListener.onMapLoadError();
                    }
                }
            }
        }.start();
    }

    /**
     * 设置旋转手势是否可用
     *
     * @param enabled
     */
    public void setRotationGestureEnabled(boolean enabled) {
        this.isRotationGestureEnabled = enabled;
    }

    /**
     * 设置缩放手势是否可用
     *
     * @param enabled
     */
    public void setZoomGestureEnabled(boolean enabled) {
        this.isZoomGestureEnabled = enabled;
    }

    /**
     * 设置拖动手势是否可用
     *
     * @param enabled
     */
    public void setScrollGestureEnabled(boolean enabled) {
        this.isScrollGestureEnabled = enabled;
    }

    /**
     * 设置手势旋转中心
     *
     * @param isRotateWithTouchEventCenter
     */
    public void setRotateWithTouchEventCenter(
            boolean isRotateWithTouchEventCenter) {
        this.isRotateWithTouchEventCenter = isRotateWithTouchEventCenter;
    }

    /**
     * 设置手势缩放中心
     *
     * @param isZoomWithTouchEventCenter
     */
    public void setZoomWithTouchEventCenter(boolean isZoomWithTouchEventCenter) {
        this.isZoomWithTouchEventCenter = isZoomWithTouchEventCenter;
    }

    /**
     * 判断地图是否加载完成
     *
     * @return
     */
    public boolean isMapLoadFinsh() {
        return this.isMapLoadFinsh;
    }

    /**
     * 得到当前地图截图
     */
    public void getCurrentMap() {
        try {
            Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(),
                    Bitmap.Config.ARGB_8888);
            Canvas bitCanvas = new Canvas(bitmap);
            for (MapBaseOverlay layer : layers) {
                layer.draw(bitCanvas, matrix, currentZoom, currentRotateDegrees);
            }
            if (mapViewListener != null) {
                mapViewListener.onGetCurrentMap(bitmap);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置point该点为当前视图的中心
     *
     * @param point
     */
    public void setMapCenterWithPoint(PointF point) {
        Matrix m = new Matrix();
        m.set(matrix);
        RectF mapRect = new RectF(0, 0, this.mapOverlay.getFloorMap()
                .getWidth(), this.mapOverlay.getFloorMap().getHeight());
        m.mapRect(mapRect);
        float width = mapRect.width();
        float height = mapRect.height();
        float deltaX = 0;
        float deltaY = 0;
        float[] goal = {point.x, point.y};
        m.mapPoints(goal);
//        Log.i(TAG, "goal0:" + goal[0] + " goal1:" + goal[1]);
        //设置高
        deltaY = getHeight() / 2 - goal[1];
        //设置宽
        deltaX = getWidth() / 2 - goal[0];
        matrix.postTranslate(deltaX, deltaY);
        refresh();
    }

    /**
     * 得到当前mapoverlay的实例
     *
     * @return
     */
    public MapOverlay getMapOverlay() {
        return mapOverlay;
    }

    /**
     * **********************************************************************
     */

    /**
     * 地图居中显示
     *
     * @param horizontal
     * @param vertical
     */
    public void mapCenter(boolean horizontal, boolean vertical) {
        Matrix m = new Matrix();
        m.set(matrix);
        RectF mapRect = new RectF(0, 0, this.mapOverlay.getFloorMap()
                .getWidth(), this.mapOverlay.getFloorMap().getHeight());
        m.mapRect(mapRect);
        float width = mapRect.width();
        float height = mapRect.height();
        float deltaX = 0;
        float deltaY = 0;
        if (vertical) {
//            if (height < this.getHeight()) {
            deltaY = (getHeight() - height) / 2 - mapRect.top;
//            } else if (mapRect.top > 0) {
//                deltaY = -mapRect.top;
//            } else if (mapRect.bottom < getHeight()) {
//                deltaY = getHeight() - mapRect.bottom;
//            }
        }
        if (horizontal) {
//            if (width < getWidth()) {
            deltaX = (getWidth() - width) / 2 - mapRect.left;
//            } else if (mapRect.left > 0) {
//                deltaX = -mapRect.left;
//            } else if (mapRect.right < getWidth()) {
//                deltaX = getWidth() - mapRect.right;
//            }
        }
        matrix.postTranslate(deltaX, deltaY);
        refresh();
    }

    /**
     * 返回旋转角度
     *
     * @param event
     * @return
     */
    private float rotation(MotionEvent event) {
        float delta_x = (event.getX(0) - event.getX(1));
        float delta_y = (event.getY(0) - event.getY(1));
        double radians = Math.atan2(delta_y, delta_x);
        return (float) Math.toDegrees(radians);
    }

    /**
     * 返回两个手指间的距离
     *
     * @param event
     * @return
     */
    private float spaceBetweenTwoEvents(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    private boolean justRotateGesture() {
        if (!this.isRotationGestureEnabled) {
            return false;
        }
        float cos = (disX * disX + disY * disY - disZ * disZ)
                / (2 * disX * disY);
        if (Float.isNaN(cos)) {
            return false;
        }
        if (Math.acos(cos) * (180 / Math.PI) < 120
                && Math.acos(cos) * (180 / Math.PI) > 45)
            if (Math.acos(cos) * (180 / Math.PI) < 120
                    && Math.acos(cos) * (180 / Math.PI) > 45) {
                return true;
            }
        return false;
    }

    private float[] getHorizontalDistanceWithRotateDegree(float degrees,
                                                          float x, float y) {
        float[] goal = new float[2];
        double f = Math.PI * (degrees / 180.0F);
        goal[0] = (float) (x * Math.cos(f) - y * Math.sin(f));
        goal[1] = (float) (x * Math.sin(f) + y * Math.cos(f));
        return goal;
    }

    private void setCurrentRotateDegreesWithRule() {
        if (getCurrentRotateDegrees() > 360) {
            this.currentRotateDegrees = getCurrentRotateDegrees() % 360;
        } else if (getCurrentRotateDegrees() < 0) {
            this.currentRotateDegrees = 360 + (getCurrentRotateDegrees() % 360);
        }
    }

}
