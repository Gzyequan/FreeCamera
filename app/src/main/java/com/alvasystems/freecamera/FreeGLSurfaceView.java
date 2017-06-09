package com.alvasystems.freecamera;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;

import com.alvasystems.freecamera.util.CameraInterface;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Administrator on 2017/6/5 0005.
 */

public class FreeGLSurfaceView extends GLSurfaceView implements GLSurfaceView.Renderer, SurfaceTexture.OnFrameAvailableListener, CameraInterface.CameraOpenListener {
    private static String TAG = "ALVASystems";
    private Context mContext;
    private DirectDrawer mDirectDrawer;
    private int mTextureId = -1;
    private SurfaceTexture mSurface;
    private int cameraId;

    public FreeGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        setEGLContextClientVersion(2);
        setRenderer(this);
        setRenderMode(RENDERMODE_WHEN_DIRTY);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mTextureId = createTextureId();
        mSurface = new SurfaceTexture(mTextureId);
        mSurface.setOnFrameAvailableListener(this);
        mDirectDrawer = new DirectDrawer(mContext, mTextureId);
        cameraId = 0;
        CameraInterface.getInstance(mContext).doOpenCamera(cameraId, this);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        if (!CameraInterface.getInstance(mContext).isPreviewing()) {
            CameraInterface.getInstance(mContext).doStartPreview(mSurface);
        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        mSurface.updateTexImage();
        mDirectDrawer.drawSelf();
    }

    private int createTextureId() {
        int[] textureId = new int[1];

        GLES20.glGenTextures(1, textureId, 0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureId[0]);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);

        return textureId[0];
    }

    @Override
    public void onResume() {
        super.onResume();
        CameraInterface.getInstance(mContext).doStartPreview(mSurface);
    }

    @Override
    public void onPause() {
        super.onPause();
        CameraInterface.getInstance(mContext).doStopCamera();
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        this.requestRender();
    }

    @Override
    public void cameraOpening() {
        Log.i(TAG, "camera opening...");
    }

    @Override
    public void cameraHasOpened() {
        Log.i(TAG, "camera has opened");
    }

    @Override
    public void cameraOpenedError(Throwable throwable) {

    }
}
