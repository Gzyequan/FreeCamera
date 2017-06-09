package com.alvasystems.freecamera.util;

import android.graphics.Bitmap;
import android.util.Log;

import com.alvasystems.freecamera.constant.Constant;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtil {
    private static final String TAG = "FileUtil";
    private static String storagePath = "";


    private static String initPath() {
        if (storagePath.equals("")) {
            storagePath = Constant.PHOTO_PATH;
            File f = new File(storagePath);
            if (!f.exists()) {
                f.mkdirs();
            }
        }
        return storagePath;
    }


    public static boolean saveBitmap(Bitmap b, String photoName) {
        boolean compress = false;
        String path = initPath();
        String jpegName = path + "/" + photoName + ".jpg";
        try {
            FileOutputStream fout = new FileOutputStream(jpegName);
            BufferedOutputStream bos = new BufferedOutputStream(fout);
            compress = b.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            Log.i(TAG, "save bitmap error");
            e.printStackTrace();
        }
        return compress;
    }


}
