package com.alvasystems.freecamera.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.Size;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.util.List;

/**
 * Created by Administrator on 2017/6/6 0006.
 */

public class CameraInterface {
    private static String TAG = "ALVASystems";
    private static CameraInterface mCameraInterface;
    private Camera mCamera;
    private Camera.Parameters parameters;
    private boolean isPreviewing = false;
    private Context mContext;
    private int mCameraId = 0;
    private MediaPlayer mMediaPlayer = null;
    private boolean enableShutter = false;


    public interface CameraOpenListener {
        public void cameraOpening();

        public void cameraHasOpened();

        public void cameraOpenedError(Throwable throwable);
    }

    public interface SavePhotoListener {
        public void onStart();

        public void onSuccess(byte[] data, String photoName);

        public void onError();
    }

    private CameraInterface(Context context) {
        this.mContext = context;
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
        }
    }

    public static synchronized CameraInterface getInstance(Context context) {
        if (mCameraInterface == null) {
            mCameraInterface = new CameraInterface(context);
        }
        return mCameraInterface;
    }

    public CameraInterface doOpenCamera(int cameraId, CameraOpenListener cameraOpenListener) {
        this.mCameraId = cameraId;
        if (mCamera == null) {
            if (cameraOpenListener != null) {
                cameraOpenListener.cameraOpening();
            }
            mCamera = Camera.open(cameraId);
            if (cameraOpenListener != null) {
                cameraOpenListener.cameraHasOpened();
                Log.i(TAG, "camera opened...");
            }
        } else {
            Log.i(TAG, "camera opened error...");
            if (cameraOpenListener != null) {
                cameraOpenListener.cameraOpenedError(new Throwable("camera has being occupied"));
            }
            doStopCamera();
        }
        return mCameraInterface;
    }

    public CameraInterface doStartPreview(SurfaceTexture surfaceTexture) {
        if (isPreviewing) {
            mCamera.stopPreview();
            return mCameraInterface;
        }
        if (mCamera != null) {
            try {
                mCamera.setPreviewTexture(surfaceTexture);
            } catch (IOException e) {
                Log.e(TAG, "doStartPreview:   " + e.getMessage());
            }
            initCamera();
        }
        return mCameraInterface;
    }

    public CameraInterface doTakePicture(final SavePhotoListener photoListener) {
        if (null != mCamera && isPreviewing) {
            if (enableShutter) {
                mCamera.takePicture(new ShutterCallback() {
                    @Override
                    public void onShutter() {
                    }
                }, null, new PictureCallback() {
                    @Override
                    public void onPictureTaken(byte[] data, Camera camera) {
                        if (null != data) {
                            isPreviewing = false;
                            mCamera.stopPreview();
                            savePicture(data, photoListener);
                            mCamera.startPreview();
                            isPreviewing = true;
                        }
                    }
                });
            } else {
                mCamera.takePicture(null, null, new PictureCallback() {
                    @Override
                    public void onPictureTaken(byte[] data, Camera camera) {
                        if (null != data) {
                            isPreviewing = false;
                            mCamera.stopPreview();
                            savePicture(data, photoListener);
                            mCamera.startPreview();
                            isPreviewing = true;
                        }
                    }
                });
            }
        }
        return mCameraInterface;
    }

    private void savePicture(final byte[] data, final SavePhotoListener listener) {

        new AsyncTask<Void, Boolean, Boolean>() {
            Bitmap bitmap = null;
            String photoName = null;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                listener.onStart();
            }

            @Override
            protected Boolean doInBackground(Void... params) {
                if (null != bitmap) {
                    Bitmap rotateBitmap = ImageUtil.getRotateBitmap(bitmap, 90.0f);
                    photoName = TimeUtil.getCurrentTimeMillis() + "";
                    return FileUtil.saveBitmap(rotateBitmap, photoName);
                }
                return false;
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                if (aBoolean) {
                    listener.onSuccess(data, photoName);
                } else {
                    listener.onError();
                }
            }
        }.execute();
    }

    public CameraInterface setEnableShutter(boolean enableShutter) {
        this.enableShutter = enableShutter;
        return mCameraInterface;
    }

    public CameraInterface initCamera() {
        if (mCamera != null) {
            parameters = mCamera.getParameters();
            parameters.setPictureFormat(PixelFormat.JPEG);
            Size propPictureSize = CameraParamUtils.getInstance(mContext).getPropPictureSize(mCamera);
            parameters.setPictureSize(propPictureSize.width, propPictureSize.height);
            Size propPreviewSize = CameraParamUtils.getInstance(mContext).getPropPreviewSize(mCamera);
            parameters.setPreviewSize(propPreviewSize.width, propPreviewSize.height);
            List<String> focusModes = parameters.getSupportedFocusModes();
            if (focusModes.contains("continuous-video")) {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            }
            mCamera.setParameters(parameters);
            mCamera.startPreview();
            isPreviewing = true;
            parameters = mCamera.getParameters();
        }
        return mCameraInterface;
    }

    public CameraInterface doStopCamera() {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            isPreviewing = false;
            mCamera.release();
            mCamera = null;
        }
        return mCameraInterface;
    }

    public boolean isPreviewing() {
        return isPreviewing;
    }

}
