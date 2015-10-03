package com.onlylemi.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Picture;
import android.graphics.RectF;

public class Assist {

    public static Picture getPictureFromBitmap(Bitmap bitmap) {
        Picture picture = new Picture();
        Canvas canvas = picture.beginRecording(bitmap.getWidth(),
                bitmap.getHeight());
        canvas.drawBitmap(
                bitmap,
                null,
                new RectF(0f, 0f, (float) bitmap.getWidth(), (float) bitmap
                        .getHeight()), null);
        picture.endRecording();
        return picture;
    }

}
