package com.onlylemi.map.overlay;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.TypedValue;
import android.view.MotionEvent;

import com.onlylemi.indoor.R;
import com.onlylemi.map.MapView;
import com.onlylemi.map.core.MapBaseOverlay;

public class LocationOverlay extends MapBaseOverlay {

    // 定位类别
    public static final int MODE_NORMAL = 0;
    public static final int MODE_COMPASS = 1;

    // 指南针的颜色
    private static final int DEFAULT_LOCATION_COLOR = 0xFF3EBFC9;
    private static final int DEFAULT_LOCATION_SHADOW_COLOR = 0xFF909090;
    private static final int DEFAULT_INDICATOR_ARC_COLOR = 0xFFFA4A8D;
    private static final int DEFAULT_INDICATOR_CIRCLE_COLOR = 0xFF00F0FF;
    private static final float COMPASS_DELTA_ANGLE = 5.0f;
    private float defaultLocationCircleRadius;

    // 指南者参数
    private float compassLineLength;
    private float compassLineWidth;
    private float compassLocationCircleRadius;
    private float compassRadius;
    private float compassArcWidth;
    private float compassIndicatorCircleRadius;
    private float compassIndicatorGap;
    private float compassIndicatorArrowRotateDegree;
    private float compassIndicatorCircleRotateDegree = 0;
    private Bitmap compassIndicatorArrowBitmap;

    private Paint compassLinePaint;
    private Paint locationPaint;
    private Paint indicatorCirclePaint;
    private Paint indicatorArcPaint;

    private PointF currentPosition = null;
    private int currentMode = MODE_NORMAL;

    /**
     * 默认为 mode = MODE_NORMAL
     *
     * @param mapView  当前地图视图
     * @param position 初始坐标
     */
    public LocationOverlay(MapView mapView, PointF position) {
        initLayer(mapView, position);
    }

    /**
     * 默认为 mode = MODE_COMPASS;
     *
     * @param mapView                            当前地图视图
     * @param position                           初始坐标
     * @param compassIndicatorArrowRotateDegree  当前指示箭头 旋转角度
     * @param compassIndicatorCircleRotateDegree 设置当前指示器 圆弧 旋转角度
     */
    public LocationOverlay(MapView mapView, PointF position,
                           float compassIndicatorArrowRotateDegree,
                           float compassIndicatorCircleRotateDegree) {
        this(mapView, position);
        this.currentMode = MODE_COMPASS;
        this.compassIndicatorArrowBitmap = BitmapFactory.decodeResource(
                mapView.getResources(), R.mipmap.indicator_arrow);
        this.setIndicatorArrowRotateDegree(compassIndicatorArrowRotateDegree);
        this.setIndicatorCircleRotateDegree(compassIndicatorCircleRotateDegree);
    }

    private void initLayer(MapView mapView, PointF position) {
        this.showLevel = LOCATION_LEVEL;
        this.setPosition(position);

        // 设置默认值
        defaultLocationCircleRadius = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 8, mapView.getResources()
                        .getDisplayMetrics());
        compassRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                38, mapView.getResources().getDisplayMetrics());
        compassLocationCircleRadius = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 0.5f, mapView.getResources()
                        .getDisplayMetrics());
        compassLineWidth = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 1.3f, mapView.getResources()
                        .getDisplayMetrics());
        compassLineLength = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 2.3f, mapView.getResources()
                        .getDisplayMetrics());
        compassArcWidth = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 4.0f, mapView.getResources()
                        .getDisplayMetrics());
        compassIndicatorCircleRadius = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 2.6f, mapView.getResources()
                        .getDisplayMetrics());
        compassIndicatorGap = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 15.0f, mapView.getResources()
                        .getDisplayMetrics());

        // 初始化locationPaint
        locationPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        locationPaint.setAntiAlias(true);
        locationPaint.setStyle(Paint.Style.FILL);
        locationPaint.setColor(DEFAULT_LOCATION_COLOR);
        locationPaint.setShadowLayer(5, 3, 3, DEFAULT_LOCATION_SHADOW_COLOR);
        // 初始化compassLinePaint
        compassLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        compassLinePaint.setAntiAlias(true);
        compassLinePaint.setStrokeWidth(compassLineWidth);
        // 初始化indicatorCirclePaint
        indicatorCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        indicatorCirclePaint.setAntiAlias(true);
        indicatorCirclePaint.setStyle(Paint.Style.FILL);
        indicatorCirclePaint.setShadowLayer(3, 1, 1,
                DEFAULT_LOCATION_SHADOW_COLOR);
        indicatorCirclePaint.setColor(DEFAULT_INDICATOR_CIRCLE_COLOR);
        // 初始化indicatorArcPaint
        indicatorArcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        indicatorArcPaint.setStyle(Paint.Style.STROKE);
        indicatorArcPaint.setColor(DEFAULT_INDICATOR_ARC_COLOR);
        indicatorArcPaint.setStrokeWidth(compassArcWidth);
    }

    /**
     * 设置指南针指示箭头图片
     *
     * @param bitmap
     */
    public void setIndicatorArrowBitmap(Bitmap bitmap) {
        this.compassIndicatorArrowBitmap = bitmap;
    }

    /**
     * 设置定位指示类型
     *
     * @param mode
     */
    public void setMode(int mode) {
        this.currentMode = mode;
    }

    /**
     * 得到当前定位位置
     *
     * @return
     */
    public PointF getPosition() {
        return currentPosition;
    }

    /**
     * 设置当前定位位置
     *
     * @param position
     */
    public void setPosition(PointF position) {
        this.currentPosition = position;
    }

    /**
     * 返回当前指示 箭头 旋转角度
     *
     * @return
     */
    public float getCompassIndicatorArrowRotateDegree() {
        return compassIndicatorArrowRotateDegree;
    }

    /**
     * 当前指示器 圆弧 旋转角度
     *
     * @return
     */
    public float getCompassIndicatorCircleRotateDegree() {
        return compassIndicatorCircleRotateDegree;
    }

    /**
     * 设置当前指示 箭头 旋转角度
     *
     * @param degree
     */
    public void setIndicatorArrowRotateDegree(float degree) {
        this.compassIndicatorArrowRotateDegree = degree % 360;
    }

    /**
     * 设置当前指示器 圆弧 旋转角度
     *
     * @param degree
     */
    public void setIndicatorCircleRotateDegree(float degree) {
        this.compassIndicatorCircleRotateDegree = degree % 360;
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onTouch(MotionEvent event) {

    }

    @Override
    public void draw(Canvas canvas, Matrix matrix, float currentZoom,
                     float currentRotateDegrees) {
        canvas.save();
        if (this.isVisible && this.currentPosition != null) {
            float goal[] = {currentPosition.x, currentPosition.y};
            matrix.mapPoints(goal);

            canvas.drawCircle(goal[0], goal[1], defaultLocationCircleRadius,
                    locationPaint);

            if (currentMode == MODE_COMPASS) {
                for (int i = 0; i < 360 / COMPASS_DELTA_ANGLE; i++) {
                    canvas.save();
                    canvas.rotate(COMPASS_DELTA_ANGLE * i, goal[0], goal[1]);
                    if (i % (90 / COMPASS_DELTA_ANGLE) == 0) {
                        canvas.drawLine(goal[0], goal[1] - compassRadius
                                + compassLocationCircleRadius, goal[0], goal[1]
                                - compassRadius + compassLocationCircleRadius
                                - compassLineLength, compassLinePaint);
                    } else {
                        canvas.drawCircle(goal[0], goal[1] - compassRadius,
                                compassLocationCircleRadius, new Paint());
                    }
                    canvas.restore();
                }
                if (compassIndicatorArrowBitmap != null) {
                    canvas.save();
                    canvas.rotate(this.compassIndicatorArrowRotateDegree,
                            goal[0], goal[1]);
                    canvas.drawBitmap(compassIndicatorArrowBitmap, goal[0]
                                    - compassIndicatorArrowBitmap.getWidth() / 2,
                            goal[1] - defaultLocationCircleRadius
                                    - compassIndicatorGap, new Paint());
                    canvas.restore();
                    if (360 - (this.compassIndicatorArrowRotateDegree - this.compassIndicatorCircleRotateDegree) > 180) {
                        canvas.drawArc(
                                new RectF(goal[0] - compassRadius, goal[1]
                                        - compassRadius, goal[0]
                                        + compassRadius, goal[1]
                                        + compassRadius),
                                -90 + this.compassIndicatorCircleRotateDegree,
                                (this.compassIndicatorArrowRotateDegree - this.compassIndicatorCircleRotateDegree),
                                false, indicatorArcPaint);
                    } else {
                        canvas.drawArc(
                                new RectF(goal[0] - compassRadius, goal[1]
                                        - compassRadius, goal[0]
                                        + compassRadius, goal[1]
                                        + compassRadius),
                                -90 + this.compassIndicatorArrowRotateDegree,
                                360 - (this.compassIndicatorArrowRotateDegree - this.compassIndicatorCircleRotateDegree),
                                false, indicatorArcPaint);
                    }

                }
                canvas.save();
                canvas.rotate(compassIndicatorCircleRotateDegree, goal[0],
                        goal[1]);
                canvas.drawCircle(goal[0], goal[1] - compassRadius,
                        compassIndicatorCircleRadius, indicatorCirclePaint);
                canvas.restore();
            }

        }
        canvas.restore();
    }
}
