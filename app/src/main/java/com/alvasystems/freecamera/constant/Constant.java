package com.alvasystems.freecamera.constant;

import android.os.Environment;

/**
 * Created by Administrator on 2017/6/8 0008.
 */

public class Constant {
    public static final String DST_FOLDER_NAME = "FreeCamera";
    public static final String PHOTO_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + DST_FOLDER_NAME + "/";
}
