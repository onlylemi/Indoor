package com.onlylemi.map.overlay;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.widget.Toast;

import com.onlylemi.indoor.R;
import com.onlylemi.map.MapView;
import com.onlylemi.map.core.MapBaseOverlay;
import com.onlylemi.map.core.PMark;

import java.util.ArrayList;
import java.util.List;

public class BitmapOverlay extends MapBaseOverlay {

    public final static String TAG = "BitmapOverlay:";

    private MapView mapView;
    private List<PMark> views; //view集
    private List<Integer> viewsActivityVidList; //活动views id
    private Bitmap mBitmap;

    private Paint paint;

    public BitmapOverlay(MapView mapView) {
        initLayer(mapView);
    }

    private void initLayer(MapView mapView) {
        this.mapView = mapView;
        mBitmap = BitmapFactory.decodeResource(mapView.getResources(),
                R.mipmap.icon_views_activity);
        this.paint = new Paint();
        this.paint.setAntiAlias(true);
        this.paint.setStyle(Paint.Style.FILL_AND_STROKE);

        viewsActivityVidList = new ArrayList<>();
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
        /*if (mapCoordinate[0] >= x && mapCoordinate[0] <= x + mBitmap.getWidth()
                && mapCoordinate[1] >= y
                && mapCoordinate[1] <= y + mBitmap.getHeight()) {
            Toast.makeText(mapView.getContext(), "Clicked on bitmap",
                    Toast.LENGTH_LONG).show();
        }*/
    }

    @Override
    public void draw(Canvas canvas, Matrix matrix, float currentZoom,
                     float currentRotateDegrees) {
        canvas.save();
        if (isVisible && views != null) {
            //canvas.setMatrix(matrix);
            for (int i = 0; i < views.size(); i++) {
                for (int j = 0; j < viewsActivityVidList.size(); j++) {
                    if (views.get(i).id == viewsActivityVidList.get(j)) {
                        float goal[] = {views.get(i).x, views.get(i).y};
                        matrix.mapPoints(goal);
                        canvas.drawBitmap(mBitmap, goal[0] - mBitmap.getWidth() / 2, goal[1]
                                - mBitmap.getHeight(), paint);
                        break;
                    }
                }
            }
        }
        canvas.restore();
    }

    public void setViews(List<PMark> views) {
        this.views = views;
    }

    public void setViewsActivityVidList(List<Integer> viewsActivityVidList) {
        this.viewsActivityVidList = viewsActivityVidList;
    }
}
