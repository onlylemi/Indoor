package com.onlylemi.cameraandroute;

import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;

@SuppressWarnings("deprecation")
public class PreviewSurface extends CameraSurface implements
        Camera.PreviewCallback {

    public static final String TAG = "PreviewSurface:";

    public PreviewSurface(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
    }

    @Override
    public void onPreviewFrame(byte[] paramArrayOfByte, Camera paramCamera) {
        Size size = paramCamera.getParameters().getPreviewSize();
        try {
            // 调用image.compressToJpeg() 将YUV格式图像数据data转为jpg格式
            YuvImage image = new YuvImage(paramArrayOfByte, ImageFormat.NV21,
                    size.width, size.height, null);
//            if (image != null) {
//                ByteArrayOutputStream outstream = new ByteArrayOutputStream();
//                image.compressToJpeg(new Rect(0, 0, size.width, size.height),
//                        80, outstream);
//                outstream.flush();
//                 启用线程将图像数据发送出去
//				Thread th = new MyThread(outstream, ipname);
//				th.start();
//            }
        } catch (Exception ex) {
            Log.e("Sys", "Error:" + ex.getMessage());
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder paramSurfaceHolder) {
        super.surfaceCreated(paramSurfaceHolder);
        this.camera.setPreviewCallback(this);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder paramSurfaceHolder) {
        this.camera.setPreviewCallback(null);
        super.surfaceDestroyed(paramSurfaceHolder);
    }

}
