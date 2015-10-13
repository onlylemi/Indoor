package com.onlylemi.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Environment;
import android.view.View;

import com.onlylemi.indoor.R;

public class Share {


    /**
     * 保存到sdcard
     *
     * @param b
     * @param strFileName
     */
    public static void savePic(Bitmap b, String strFileName) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(strFileName);
            if (null != fos) {
                b.compress(Bitmap.CompressFormat.PNG, 90, fos);
                fos.flush();
                fos.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 分享照片
     *
     * @param photoUri
     */
    public static void sharePhoto(String photoUri, Context context) {

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        File file = new File(photoUri);
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "好友分享");
        shareIntent.putExtra("sms_body", "驻足");
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                "我正在使用 @only乐秘 团队开发的《驻足》，大家快来下载吧！");
        shareIntent.setType("image/*, text/*");
        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(Intent.createChooser(shareIntent,
                "分享 -《驻足》"));
    }

    /**
     * 设置里的分享
     *
     * @param context
     */
    public static void shareApp(Context context) {

        File folder = new File(Environment.getExternalStorageDirectory()
                .getPath() + "/indoor/ico");
        folder.mkdirs();
        String icoUrl = Environment.getExternalStorageDirectory().getPath()
                + "/indoor/ico/" + "icon";
        Bitmap icon = BitmapFactory.decodeResource(context.getResources(), R.mipmap.icon);
        savePic(icon, icoUrl);

        sharePhoto(icoUrl, context);
    }

}
