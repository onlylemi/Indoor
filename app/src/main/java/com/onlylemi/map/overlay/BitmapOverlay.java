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

public class BitmapOverlay extends MapBaseOverlay {

	private MapView mapView;
	private int x = 200;
	private int y = 300;
	private Bitmap mBitmap;

	public BitmapOverlay(MapView mapView) {
		initLayer(mapView);
	}

	private void initLayer(MapView mapView) {
		this.mapView = mapView;
		mBitmap = BitmapFactory.decodeResource(mapView.getResources(),
				R.mipmap.ic_launcher);
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
		if (mapCoordinate[0] >= x && mapCoordinate[0] <= x + mBitmap.getWidth()
				&& mapCoordinate[1] >= y
				&& mapCoordinate[1] <= y + mBitmap.getHeight()) {
			Toast.makeText(mapView.getContext(), "Clicked on bitmap",
					Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void draw(Canvas canvas, Matrix matrix, float currentZoom,
			float currentRotateDegrees) {
		canvas.save();
		if (isVisible) {
			canvas.setMatrix(matrix);

			canvas.drawBitmap(mBitmap, x, y, new Paint(Paint.ANTI_ALIAS_FLAG));
		}
		canvas.restore();
	}
}
