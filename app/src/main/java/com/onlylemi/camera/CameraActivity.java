package com.onlylemi.camera;

import android.app.Activity;
import android.os.Bundle;

import com.onlylemi.indoor.R;
import com.onlylemi.map.overlay.RouteOverlay;

public class CameraActivity extends Activity {

    public static final String TAG = "CameraActivity:";

    private PreviewSurface previewSurface;
    private RouteSurface routeSurface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        previewSurface = (PreviewSurface) findViewById(R.id.previewSurface);
        routeSurface = (RouteSurface) findViewById(R.id.routeSurface);

    }

}
