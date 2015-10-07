package com.onlylemi.dr.util;

/**
 * Created by 董神 on 2015/9/14.
 * I love programming
 */

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.util.LruCache;
import android.widget.ImageView;

import com.onlylemi.dr.activity.ReadyActivity;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 图片异步加载类
 *
 * @author Leslie.Fang
 */
public class AsyncImageLoader {

    public static final String TAG = "AsyncTmageLoader: ";

    private Context context;
    // 内存缓存默认 5M
    static final int MEM_CACHE_DEFAULT_SIZE = 5 * 1024 * 1024;
    // 文件缓存默认 10M
    static final int DISK_CACHE_DEFAULT_SIZE = 50 * 1024 * 1024;
    // 一级内存缓存基于 LruCache
    private LruCache<String, Bitmap> memCache;
    // 二级文件缓存基于 DiskLruCache
    private DiskLruCache diskCache;
    //
    private Handler handler;


    public AsyncImageLoader(Context context) {
        this.context = context;
        initMemCache();
        initDiskLruCache();
    }

    public AsyncImageLoader(Context context, Handler handler) {
        this.context = context;
        this.handler = handler;
        initMemCache();
        initDiskLruCache();
    }

    /**
     * 初始化内存缓存
     */
    private void initMemCache() {
        memCache = new LruCache<String, Bitmap>(MEM_CACHE_DEFAULT_SIZE) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount();
            }
        };
    }

    /**
     * 初始化文件缓存
     */
    private void initDiskLruCache() {
        try {
            File cacheDir = getDiskCacheDir(context, "bitmap");
            Log.i(AsyncImageLoader.TAG, "cache dir :　" + cacheDir);
            if (!cacheDir.exists()) {
                cacheDir.mkdirs();
            }
            diskCache = DiskLruCache.open(cacheDir, getAppVersion(context), 1, DISK_CACHE_DEFAULT_SIZE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 从内存缓存中拿
     *
     * @param url
     */
    public Bitmap getBitmapFromMem(String url) {
        return memCache.get(url);
    }

    /**
     * 加入到内存缓存中
     *
     * @param url
     * @param bitmap
     */
    public void putBitmapToMem(String url, Bitmap bitmap) {
        memCache.put(url, bitmap);
    }

    /**
     * 从文件缓存中拿
     *
     * @param url
     */
    public Bitmap getBitmapFromDisk(String url) {
        try {
            String key = hashKeyForDisk(url);
            DiskLruCache.Snapshot snapShot = diskCache.get(key);
            if (snapShot != null) {
                InputStream is = snapShot.getInputStream(0);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.RGB_565;
                Bitmap bitmap = BitmapFactory.decodeStream(is, null, options);
                return bitmap;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 从 url 加载图片
     *
     * @param imageView
     * @param imageUrl
     */
    public Bitmap loadImage(final ImageView imageView, final String imageUrl) {

        new Thread(new Runnable() {
            @Override
            public void run() {

                // 先从内存中拿
                Bitmap bitmap = getBitmapFromMem(imageUrl);
                if (bitmap != null) {
                    Log.i(AsyncImageLoader.TAG, "image exists in memory");
                    final BitmapDrawable drawable = new BitmapDrawable(null, bitmap);
                    ReadyActivity.handler.post(new Runnable() {
                        @Override
                        public void run() {
                            imageView.setBackground(drawable);
                        }
                    });
                    return;
                }

                // 再从文件中找
                bitmap = getBitmapFromDisk(imageUrl);
                if (bitmap != null) {
                    Log.i(AsyncImageLoader.TAG, "image exists in file");
                    final BitmapDrawable drawable = new BitmapDrawable(null, bitmap);
                    // 重新缓存到内存中
                    putBitmapToMem(imageUrl, bitmap);
                    ReadyActivity.handler.post(new Runnable() {
                        @Override
                        public void run() {
                            imageView.setBackground(drawable);
                        }
                    });
                    return;
                }

                if (bitmap == null) {
                    ReadyActivity.handler.post(new Runnable() {
                        @Override
                        public void run() {
                            // 内存和文件中都没有再从网络下载
                            if (!TextUtils.isEmpty(imageUrl)) {
                                new ImageDownloadTask(imageView).execute(imageUrl);
                            }
                        }
                    });
                }
            }
        }).start();
        return null;
    }

    class ImageDownloadTask extends AsyncTask<String, Integer, Bitmap> {
        private String imageUrl;
        private ImageView imageView;

        public ImageDownloadTask(ImageView imageView) {
            this.imageView = imageView;
        }

        @Override
        protected void onPreExecute() {
            Log.i("ImageDownloadTask", "prepare to download");
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            try {
                Log.i("ImageDownloadTask", "file address is " + params[0]);
                imageUrl = params[0];
                String key = hashKeyForDisk(imageUrl);
                // 下载成功后直接将图片流写入文件缓存
                DiskLruCache.Editor editor = diskCache.edit(key);
                if (editor != null) {
                    OutputStream outputStream = editor.newOutputStream(0);
                    if (downloadUrlToStream(imageUrl, outputStream)) {
                        editor.commit();
                    } else {
                        editor.abort();
                    }
                }
                diskCache.flush();

                Bitmap bitmap = getBitmapFromDisk(imageUrl);
                if (bitmap != null) {
                    // 将图片加入到内存缓存中
                    putBitmapToMem(imageUrl, bitmap);
                }

                return bitmap;
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            if (result != null) {
                // 通过 tag 来防止图片错位
                if (imageView.getTag() != null && imageView.getTag().equals(imageUrl)) {
                    Log.i("ImageDownloadTask", "download success and setting the imageview");
                    Drawable drawable = new BitmapDrawable(null, result);
                    imageView.setBackground(drawable);
                }
            }
        }

        private boolean downloadUrlToStream(String urlString, OutputStream outputStream) {
            HttpURLConnection urlConnection = null;
            BufferedOutputStream out = null;
            BufferedInputStream in = null;
            try {
                final URL url = new URL(urlString);
                urlConnection = (HttpURLConnection) url.openConnection();
                in = new BufferedInputStream(urlConnection.getInputStream(), 8 * 1024);
                out = new BufferedOutputStream(outputStream, 8 * 1024);
                int b;
                while ((b = in.read()) != -1) {
                    out.write(b);
                }
                return true;
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (final IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                try {
                    if (out != null) {
                        out.close();
                    }
                    if (in != null) {
                        in.close();
                    }
                } catch (final IOException e) {
                    e.printStackTrace();
                }
            }
            return false;
        }
    }

    private File getDiskCacheDir(Context context, String uniqueName) {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return new File(cachePath + File.separator + uniqueName);
    }

    private int getAppVersion(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 1;
    }

    private String hashKeyForDisk(String key) {
        String cacheKey;
        try {
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(key.getBytes());
            cacheKey = bytesToHexString(mDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            cacheKey = String.valueOf(key.hashCode());
        }
        return cacheKey;
    }

    private String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }
}