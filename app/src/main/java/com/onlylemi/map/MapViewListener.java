package com.onlylemi.map;

import android.graphics.Bitmap;

/**
 * 地图事件监听类
 * 
 */
public interface MapViewListener {
	void onMapLoadComplete();

	void onMapLoadError();

	void onGetCurrentMap(Bitmap bitmap);
}
