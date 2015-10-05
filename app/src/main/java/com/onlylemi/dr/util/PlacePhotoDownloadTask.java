package com.onlylemi.dr.util;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by 董神 on 2015/8/29.
 * I love programming
 */
public class PlacePhotoDownloadTask extends AsyncTask<String, Integer, Drawable> {

    private Drawable drawable;
    private ImageView imageView;
    private static int SUM = 0;
    private String string;
    private ViewHolder viewHolder;
    private int position;

    public PlacePhotoDownloadTask(Drawable drawable) {
        super();
        this.drawable = drawable;
    }

    public PlacePhotoDownloadTask(ImageView imageView) {
        this.imageView = imageView;
    }

    public PlacePhotoDownloadTask(Drawable drawable, ImageView imageView) {
        this.drawable = drawable;
        this.imageView = imageView;
    }



    @Override
    protected Drawable doInBackground(String... params) {
        Log.i("TAG", "Place photo is downloading ");
        string = params[0];
        if (params.length == 1) {
            try {
                URL url = new URL(params[0]);
                InputStream inputStream = url.openStream();
                drawable = Drawable.createFromStream(inputStream, null);
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
        }
        return drawable;
    }

    @Override
    protected void onPostExecute(Drawable drawable) {
        if (drawable != null) {
            Log.i("TAG", "Place photo download success image url = " + string);
            Log.e("TAG", "Place photo sum = " + ++SUM);
            this.drawable = drawable;
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
             if (imageView != null && viewHolder == null) {
                imageView.setImageBitmap(bitmapDrawable.getBitmap());
            }
        } else {
            Log.i("TAG", "Place photo download faile image url = " + string);
        }
        super.onPostExecute(drawable);
    }

    @Override
    protected void onPreExecute() {
        Log.i("TAG", "Place photo download prepare");
        super.onPreExecute();
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        Log.i("TAG", "Place photo download %" + values[0].intValue());
        super.onProgressUpdate(values);
    }

}
