package com.alvasystems.freecamera.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.MediaScannerConnection;
import android.media.ThumbnailUtils;
import android.net.Uri;

import com.alvasystems.freecamera.constant.Constant;

import java.io.File;

public class ImageUtil {

    public static Bitmap getRotateBitmap(Bitmap b, float rotateDegree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(rotateDegree);
        Bitmap rotaBitmap = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), matrix, false);
        return rotaBitmap;
    }

    public static Bitmap getThumbnailImage(byte[] data, int width, int height) {
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);
        options.inJustDecodeBounds = false;
        int w = options.outWidth;
        int h = options.outHeight;
        int wRatio = w / width;
        int hRatio = h / height;
        int ratio = 1;
        if (wRatio < hRatio) {
            ratio = wRatio;
        } else {
            ratio = hRatio;
        }
        if (ratio <= 0) {
            ratio = 1;
        }
        options.inSampleSize = ratio;
        bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }

    public static Bitmap getThumbnailImage(String imagePath, int width, int height) {
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        bitmap = BitmapFactory.decodeFile(imagePath, options);
        options.inJustDecodeBounds = false;
        int w = options.outWidth;
        int h = options.outHeight;
        int wRatio = w / width;
        int hRatio = h / height;
        int ratio = 1;
        if (wRatio < hRatio) {
            ratio = wRatio;
        } else {
            ratio = hRatio;
        }
        if (ratio <= 0) {
            ratio = 1;
        }
        options.inSampleSize = ratio;
        bitmap = BitmapFactory.decodeFile(imagePath, options);
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }

    public static void updateAlbum(Context context, String photoPath) {

        MediaScannerConnection scannerConnection = new MediaScannerConnection(
                context,
                new MediaScannerConnection.MediaScannerConnectionClient() {

                    @Override
                    public void onMediaScannerConnected() {

                    }

                    @Override
                    public void onScanCompleted(String path, Uri uri) {

                    }

                });
        scannerConnection.scanFile(context,
                new String[]{photoPath}, new String[]{"image/jpeg"},
                new MediaScannerConnection.OnScanCompletedListener() {
                    @Override
                    public void onScanCompleted(String path, Uri uri) {

                    }
                });
    }

    public static String getLastPhoto() {
        long last = 0;
        String photoPath = null;
        File freeCamera = new File(Constant.PHOTO_PATH);
        if (!freeCamera.exists()) {
            freeCamera.mkdirs();
        }
        File[] photos = freeCamera.listFiles();
        if (null != photos) {
            for (int i = 0; i < photos.length; i++) {
                long modified = photos[i].lastModified();
                if (last < modified) {
                    last = modified;
                    photoPath = photos[i].getAbsolutePath();
                }
            }
        }
        return photoPath;
    }
}
