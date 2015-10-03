package com.onlylemi.map.overlay;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;

import com.onlylemi.indoor.R;
import com.onlylemi.map.MapView;
import com.onlylemi.map.core.MapBaseOverlay;
import com.onlylemi.map.core.PMark;
import com.onlylemi.map.utils.AssistMath;

public class MarkOverlay extends MapBaseOverlay {

    private static final String TAG = "MarkOverlay:";

    private MapView mapView;
    private float radiusMark;
    private boolean isAddMark;

    private Bitmap bmpMark, bmpMarkTouch;
    private boolean isClickMark = false;
    private int num = -1;

    private Paint paint;

    private List<PMark> marks;

    private MarkIsClickListener listener;

    public MarkOverlay(MapView mapView, int radiusMark) {
        this.mapView = mapView;
        this.radiusMark = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 10.0f, mapView.getResources()
                        .getDisplayMetrics());
        this.isAddMark = true;

        marks = new ArrayList<>();

        bmpMark = BitmapFactory.decodeResource(mapView.getResources(), R.mipmap.mark);
        bmpMarkTouch = BitmapFactory.decodeResource(mapView.getResources(), R.mipmap.mark_touch);

        this.paint = new Paint();
        this.paint.setAntiAlias(true);
        this.paint.setStyle(Style.FILL_AND_STROKE);
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
        float[] mapCoordinate = mapView.getMapCoordinateWithScreenCoordinate(
                event.getX(), event.getY());
        int xMark = (int) mapCoordinate[0];
        int yMark = (int) mapCoordinate[1];

        // 判断是否在平面图内
        if (withFloorPlan(mapView, xMark, yMark)) {
            isAddMark = true;
            Log.i(TAG, "xMark=" + xMark + " yMark=" + yMark);

            // 当触摸已有位置时 消除标记
            if (marks.size() != 0) {
//                for (Iterator<PointF> iterator = marks.iterator(); iterator
//                        .hasNext(); ) {
//                    PointF mark = iterator.next();
//                    if (xMark >= mark.x - radiusMark / 2
//                            && xMark <= mark.x + radiusMark / 2
//                            && yMark >= mark.y - radiusMark / 2
//                            && yMark <= mark.y + radiusMark / 2) {
//                        iterator.remove();
//                        isAddMark = false;
//                        break;
//                    }
//
//
//                }

                for (int i = 0; i < marks.size(); i++) {
                    if (AssistMath.getDistanceBetweenTwoPoints(xMark, yMark, marks.get(i).x -
                            bmpMark.getWidth() / 2, marks.get(i).y - bmpMark.getHeight() / 2) <= 50) {
                        num = i;
                        isClickMark = true;
                        break;
                    }

                    if (i == marks.size() - 1) {
                        isClickMark = false;
                    }
                }
            }

            // 添加标记
//            if (isAddMark) {
//                marks.add(new PointF(xMark, yMark));
//            }

        } else {
            isClickMark = false;
        }
        mapView.refresh();

        if (listener != null) {
            listener.markIsClick();
        }
    }

    @Override
    public void draw(Canvas canvas, Matrix matrix, float currentZoom,
                     float currentRotateDegrees) {
        canvas.save();
        if (isVisible) {
            // canvas.setMatrix(matrix);
            for (int i = 0; i < marks.size(); i++) {
                PMark mark = marks.get(i);
                float goal[] = {mark.x, mark.y};
                matrix.mapPoints(goal);

                paint.setColor(Color.BLACK);
                paint.setTextSize(radiusMark);
                //Log.i(TAG, "CurrentZoomValue:" + mapView.getCurrentZoomValue());
                //画介绍
                if (mapView.getCurrentZoomValue() > 1.0) {
                    canvas.drawText(mark.name, goal[0] - radiusMark, goal[1] - radiusMark / 2, paint);
                }
                //画图标
                canvas.drawBitmap(bmpMark, goal[0] - bmpMark.getWidth() / 2,
                        goal[1] - bmpMark.getHeight() / 2, paint);
                if (i == num && isClickMark) {
                    canvas.drawBitmap(bmpMarkTouch, goal[0] - bmpMarkTouch.getWidth() / 2,
                            goal[1] - bmpMarkTouch.getHeight(), paint);
                }
                // canvas.drawCircle(goal[0], goal[1], radiusMark, paint);
                // paint.setColor(Color.WHITE);
                // canvas.drawCircle(goal[0], goal[1], radiusMark / 2, paint);
            }
        }
        canvas.restore();
    }

    /**
     * 判断一个点是否在平面图内
     *
     * @param mapView
     * @param pointX
     * @param pointY
     * @return
     */
    public boolean withFloorPlan(MapView mapView, int pointX, int pointY) {
        return pointX > 0 && pointY < mapView.getWidthFloorPlan() && pointY > 0
                && pointY < mapView.getHeightFloorPlan();
    }

    public void setMarks(List<PMark> marks) {
        this.marks = marks;
    }

    public List<PMark> getMarks() {
        return marks;
    }

    public int getNum() {
        return num;
    }

    public boolean getIsClickMark() {
        return isClickMark;
    }

    public void setMarkIsClickListener(MarkIsClickListener listener) {
        this.listener = listener;
    }

    public interface MarkIsClickListener {
        void markIsClick();
    }
}
