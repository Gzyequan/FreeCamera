package com.alvasystems.freecamera;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.Toast;

import com.alvasystems.freecamera.constant.Constant;
import com.alvasystems.freecamera.customview.CircleImageView;
import com.alvasystems.freecamera.util.CameraInterface;
import com.alvasystems.freecamera.util.DisplayUtils;
import com.alvasystems.freecamera.util.ImageUtil;

public class MainActivity extends BaseActivity implements View.OnClickListener, CameraInterface.SavePhotoListener {

    private FreeGLSurfaceView mFreeGLSurfaceView;
    private Button takePicture;
    private CircleImageView previewPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkPermission();
        setFullScreen(this, true);
        initParams();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initView() {
        mFreeGLSurfaceView = (FreeGLSurfaceView) findViewById(R.id.fc_freeGLSurfaceView);
        takePicture = ((Button) findViewById(R.id.takePic));
        previewPhoto = (CircleImageView) findViewById(R.id.photo_preview);
    }

    private void initThumb() {
        String lastPhoto = ImageUtil.getLastPhoto();
        if (null != lastPhoto) {
            Bitmap thumbnailImage = ImageUtil.getThumbnailImage(lastPhoto, 200, 200);
            previewPhoto.setVisibility(View.VISIBLE);
            previewPhoto.setImageBitmap(thumbnailImage);
        } else {
            previewPhoto.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void initEvent() {
        takePicture.setOnClickListener(this);
    }

    private void initParams() {
        LayoutParams layoutParams = mFreeGLSurfaceView.getLayoutParams();
        Point screenMetrics = DisplayUtils.getScreenMetrics(this);
        layoutParams.width = screenMetrics.x;
        layoutParams.height = screenMetrics.y;
        mFreeGLSurfaceView.setLayoutParams(layoutParams);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.takePic:
                takePicture.setEnabled(false);
                CameraInterface.getInstance(this).setEnableShutter(false).doTakePicture(this);
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        takePicture.setEnabled(true);
    }

    @Override
    public void onSuccess(byte[] data, String photoName) {
        Toast.makeText(this, "save photo success", Toast.LENGTH_SHORT).show();
        takePicture.setEnabled(true);
        ImageUtil.updateAlbum(MainActivity.this, Constant.PHOTO_PATH + photoName + ".jpg");
        Bitmap thumbnailImage = ImageUtil.getThumbnailImage(data, 200, 200);
        previewPhoto.setVisibility(View.VISIBLE);
        previewPhoto.setImageBitmap(ImageUtil.getRotateBitmap(thumbnailImage, 90.0f));
    }

    @Override
    public void onError() {
        Toast.makeText(this, "save photo in error.", Toast.LENGTH_SHORT).show();
        takePicture.setEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFreeGLSurfaceView.onResume();
        initThumb();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mFreeGLSurfaceView.onPause();
    }

    private String[] mPermissions = {
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private static final int PERMISSION_REQUEST_CODE = 0x0001;

    @TargetApi(23)
    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int cameraPermission = checkSelfPermission(mPermissions[0]);
            if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(mPermissions, PERMISSION_REQUEST_CODE);
            }
            int storagePermission1 = checkSelfPermission(mPermissions[1]);
            if (storagePermission1 != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(mPermissions, PERMISSION_REQUEST_CODE);
            }
            int storagePermission2 = checkSelfPermission(mPermissions[2]);
            if (storagePermission2 != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(mPermissions, PERMISSION_REQUEST_CODE);
            }
        }
    }
}
