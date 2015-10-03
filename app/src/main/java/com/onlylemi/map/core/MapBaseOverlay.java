package com.onlylemi.map.core;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.view.MotionEvent;

public abstract class MapBaseOverlay {
	// map draw level
	protected static final int MAP_LEVEL = 0;
	// location draw level
	protected static final int LOCATION_LEVEL = Integer.MAX_VALUE;
	// draw level
	public int showLevel;
	public boolean isVisible = true;

	/**
	 * onDestroy
	 */
	public abstract void onDestroy();

	/**
	 * onPause
	 */
	public abstract void onPause();

	/**
	 * onResume
	 */
	public abstract void onResume();

	/**
	 * 触摸事件响应
	 * 
	 * @param event
	 */
	public abstract void onTouch(MotionEvent event);

	/**
	 * 画图
	 * 
	 * @param canvas
	 * @param matrix
	 * @param currentZoom
	 * @param currentRotateDegrees
	 */
	public abstract void draw(Canvas canvas, Matrix matrix, float currentZoom,
			float currentRotateDegrees);

}
