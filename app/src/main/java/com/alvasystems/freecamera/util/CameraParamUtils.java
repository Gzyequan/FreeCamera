package com.alvasystems.freecamera.util;

import android.content.Context;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.util.Log;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CameraParamUtils {
    private static final String TAG = "ALVASystems";
    private CameraSizeComparator sizeComparator = new CameraSizeComparator();
    private static CameraParamUtils myCamPara = null;
    private Context mContext;

    private CameraParamUtils(Context context) {
        this.mContext = context;
    }

    public static CameraParamUtils getInstance(Context context) {
        if (myCamPara == null) {
            myCamPara = new CameraParamUtils(context);
            return myCamPara;
        } else {
            return myCamPara;
        }
    }

    public Size getPropPreviewSize(Camera camera) {
        int diff = Integer.MAX_VALUE;
        int bestX = 0;
        int bestY = 0;
        float tempRate;
        float screenRate = DisplayUtils.getScreenRate(mContext);
        List<String> previewSizes = getAllPreviewSizes(camera);
        if (previewSizes == null) {
            Point screenMetrics = DisplayUtils.getScreenMetrics(mContext);
            return camera.new Size(screenMetrics.x, screenMetrics.y);
        }
        for (String previewSize : previewSizes) {
            previewSize = previewSize.trim();
            int dimPosition = previewSize.indexOf("x");
            if (dimPosition == -1) {
                Log.e(TAG, "previewSizeException  : Bad pictureSizeString:" + previewSize);
                continue;
            }
            int newX = 0;
            int newY = 0;
            newX = Integer.parseInt(previewSize.substring(0, dimPosition));
            newY = Integer.parseInt(previewSize.substring(dimPosition + 1));
            float floatX = DataOperateUtil.intToFloat(newX);
            float floatY = DataOperateUtil.intToFloat(newY);
            tempRate = floatX / floatY;
            Point screenResolution = DisplayUtils.getScreenMetrics(mContext);
            int newDiff = Math.abs(newX - screenResolution.x) + Math.abs(newY - screenResolution.y);
            if (newDiff == diff) {
                bestX = newX;
                bestY = newY;
            } else if (newDiff < diff) {
                if (Math.abs(tempRate - screenRate) <= 0.1f) {
                    bestX = newX;
                    bestY = newY;
                    diff = newDiff;
                }
            }
        }
        if (bestX > 0 && bestY > 0) {
            return camera.new Size(bestX, bestY);
        }
        return null;
    }

    public Camera.Size getPropPictureSize(Camera camera) {
        int diff = Integer.MIN_VALUE;
        int bestX = 0;
        int bestY = 0;
        float tempRate;
        float screenRate = DisplayUtils.getScreenRate(mContext);
        List<String> pictureSizes = getAllPictureSizes(camera);
        if (pictureSizes == null) {
            Point screenMetrics = DisplayUtils.getScreenMetrics(mContext);
            return camera.new Size(screenMetrics.x, screenMetrics.y);
        }
        for (String pictureSize : pictureSizes) {
            pictureSize = pictureSize.trim();
            int dimPosition = pictureSize.indexOf("x");
            if (dimPosition == -1) {
                Log.e(TAG, "pictureSizeException  : Bad pictureSizeString:" + pictureSize);
                continue;
            }
            int newX = 0;
            int newY = 0;
            newX = Integer.parseInt(pictureSize.substring(0, dimPosition));
            newY = Integer.parseInt(pictureSize.substring(dimPosition + 1));
            float floatX = DataOperateUtil.intToFloat(newX);
            float floatY = DataOperateUtil.intToFloat(newY);
            tempRate = floatX / floatY;
            Point screenResolution = DisplayUtils.getScreenMetrics(mContext);
            int newDiff = Math.abs(newX - screenResolution.x) + Math.abs(newY - screenResolution.y);
            if (newDiff == diff) {
                bestX = newX;
                bestY = newY;
            } else if (newDiff > diff) {
                if (Math.abs(tempRate - screenRate) <= 0.1f) {
                    bestX = newX;
                    bestY = newY;
                    diff = newDiff;
                }
            }
        }
        if (bestX > 0 && bestY > 0) {
            return camera.new Size(bestX, bestY);
        }
        return null;
    }

    public List<String> getAllPreviewSizes(Camera camera) {
        List<String> allPreviewSize = new ArrayList<String>();
        Camera.Parameters parameters = camera.getParameters();
        String previewSizeString = parameters.get("preview-size-values");
        if (previewSizeString == null) {
            previewSizeString = parameters.get("preview-size-value");
        }
        if (previewSizeString == null) {
            return null;
        }
        String[] previewSizesString = previewSizeString.split(",");
        for (String previewSize : previewSizesString) {
            allPreviewSize.add(previewSize);
        }
        return allPreviewSize;
    }

    public List<String> getAllPictureSizes(Camera camera) {
        List<String> allPictureSize = new ArrayList<>();
        Camera.Parameters parameters = camera.getParameters();
        String pictureSizeString = parameters.get("picture-size-values");
        if (pictureSizeString == null) {
            pictureSizeString = parameters.get("picture-size-value");
        }
        if (pictureSizeString == null) {
            return null;
        }
        String[] pictureSizesString = pictureSizeString.split(",");
        for (String pictureSize : pictureSizesString) {
            allPictureSize.add(pictureSize);
        }
        return allPictureSize;
    }


    public class CameraSizeComparator implements Comparator<Camera.Size> {
        public int compare(Size lhs, Size rhs) {
            if (lhs.width == rhs.width) {
                return 0;
            } else if (lhs.width > rhs.width) {
                return 1;
            } else {
                return -1;
            }
        }

    }
}
