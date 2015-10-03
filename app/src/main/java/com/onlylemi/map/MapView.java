package com.onlylemi.map;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Picture;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.onlylemi.map.core.MapBaseOverlay;
import com.onlylemi.map.core.MapMainView;

public class MapView extends FrameLayout {

    public static final String TAG = "MapView: ";

    private MapMainView mapMainView;
    private MapController mapController;
    private ImageView brandImageView;

    private int widthFloorPlan, heightFloorPlan;

    public MapView(Context context) {
        this(context, null);
    }

    public MapView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MapView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        Log.i(TAG, "mapMainView");
        mapMainView = new MapMainView(context, attrs, defStyle);
        addView(mapMainView);
        brandImageView = new ImageView(context, attrs, defStyle);
        brandImageView.setScaleType(ScaleType.FIT_START);
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                        15, context.getResources().getDisplayMetrics()));
        params.gravity = Gravity.BOTTOM | Gravity.LEFT;
        params.leftMargin = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 4, context.getResources()
                        .getDisplayMetrics());
        params.bottomMargin = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 10, context.getResources()
                        .getDisplayMetrics());
        addView(brandImageView, params);
    }

    /**
     * @return the map controller.
     */
    public MapController getController() {
        if (this.mapController == null) {
            this.mapController = new MapController(this);
        }
        return this.mapController;
    }

    public void registerMapViewListener(MapViewListener idrMapViewListener) {
        this.mapMainView.registeMapViewListener(idrMapViewListener);
    }

    public void loadMap(Picture picture) {
        this.mapMainView.loadMap(picture);
        widthFloorPlan = picture.getWidth();
        heightFloorPlan = picture.getHeight();
    }

    public void setBrandBitmap(Bitmap bitmap) {
        this.brandImageView.setImageBitmap(bitmap);
    }

    public void refresh() {
        this.mapMainView.refresh();
    }

    /**
     * @return whether the map is already loaded.
     */
    public boolean isMapLoadFinsh() {
        return this.mapMainView.isMapLoadFinsh();
    }

    /**
     * get the current map. It will be callback in the map listener of
     * 'onGetCurrentMap'
     */
    public void getCurrentMap() {
        this.mapMainView.getCurrentMap();
    }

    public float getCurrentRotateDegrees() {
        return this.mapMainView.getCurrentRotateDegrees();
    }

    public float getCurrentZoomValue() {
        return this.mapMainView.getCurrentZoomValue();
    }

    public float getMaxZoomValue() {
        return this.mapMainView.getMaxZoomValue();
    }

    public float getMinZoomValue() {
        return this.mapMainView.getMinZoomValue();
    }

    public float[] getMapCoordinateWithScreenCoordinate(float screenX,
                                                        float screenY) {
        return this.mapMainView.getMapCoordinateWithScreenCoordinate(screenX,
                screenY);
    }

    public List<MapBaseOverlay> getOverLays() {
        return this.mapMainView.getOverLays();
    }

    public void onDestroy() {
        this.mapMainView.onDestroy();
    }

    public void onPause() {
        this.mapMainView.onPause();
    }

    public void onResume() {
        this.mapMainView.onResume();
    }

    public int getWidthFloorPlan() {
        return widthFloorPlan;
    }

    public int getHeightFloorPlan() {
        return heightFloorPlan;
    }

}
