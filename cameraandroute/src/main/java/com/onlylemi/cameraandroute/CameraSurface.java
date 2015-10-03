package com.onlylemi.cameraandroute;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.Iterator;

@SuppressWarnings("deprecation")
class CameraSurface extends SurfaceView implements SurfaceHolder.Callback {

    private static final String TAG = "CameraSurface:";
    public Camera camera;
    protected SurfaceHolder holder = getHolder();
    private boolean previewing = false;

    public CameraSurface(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
        //this.holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        this.holder.addCallback(this);
    }


    private void setPreferredFormat(Camera.Parameters paramParameters,
                                    int paramInt) {
        Iterator<Integer> localIterator = paramParameters
                .getSupportedPreviewFormats().iterator();
        while (true) {
            if (!localIterator.hasNext())
                return;
            if (localIterator.next().intValue() == paramInt) {
                paramParameters.setPreviewFormat(paramInt);
            }
        }
    }

    private void setPreferredSize(Camera.Parameters paramParameters,
                                  int paramInt1, int paramInt2) {
        Iterator localIterator = paramParameters.getSupportedPreviewSizes()
                .iterator();
        while (true) {
            if (!localIterator.hasNext())
                return;
            Camera.Size localSize = (Camera.Size) localIterator.next();
            if ((localSize.width == paramInt1)
                    && (localSize.height == paramInt2))
                paramParameters.setPreviewSize(paramInt1, paramInt2);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder paramSurfaceHolder, int paramInt1,
                               int paramInt2, int paramInt3) {
        if (this.camera == null)
            return;
        if (this.previewing)
            this.camera.stopPreview();
        Camera.Parameters localParameters = this.camera.getParameters();
        setPreferredSize(localParameters, paramInt2, paramInt3);
        setPreferredFormat(localParameters, paramInt1);
//        this.camera.setDisplayOrientation(90);
        this.camera.setParameters(localParameters);
        this.camera.startPreview();
        this.previewing = true;
    }

    @Override
    public void surfaceCreated(SurfaceHolder paramSurfaceHolder) {
        this.camera = Camera.open();
        try {
            this.camera.setPreviewDisplay(paramSurfaceHolder);

            return;
        } catch (IOException localIOException) {
            Log.e("CameraSurface", "Error setting preview display.");
            this.camera.release();
            this.camera = null;
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder paramSurfaceHolder) {
        if (camera != null) {
            if (previewing) {
                this.camera.stopPreview();
            }
            this.previewing = false;
            this.camera.release();
            this.camera = null;
        }
    }
}
