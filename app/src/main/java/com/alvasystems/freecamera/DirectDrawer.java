package com.alvasystems.freecamera;

import android.content.Context;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;

import com.alvasystems.freecamera.util.ShaderUtils;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import static com.alvasystems.freecamera.util.ResourcesUtils.loadFromAssetsFile;

/**
 * Created by Administrator on 2017/6/5 0005.
 */

public class DirectDrawer {
    private FloatBuffer vertexBuffer, textureVerticesBuffer;
    private ShortBuffer drawOrderBuffer;
    private int mProgram;
    private int mTextureId;

    private float[] vertexArray = {
            -1.0f, 1.0f,
            -1.0f, -1.0f,
            1.0f, -1.0f,
            1.0f, 1.0f
    };

    private float[] textureArray = {
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 0.0f
    };

    private short[] drawOrderArray = {
            0, 1, 2,
            0, 2, 3
    };

    public DirectDrawer(Context context, int textureId) {
        this.mTextureId = textureId;

        vertexBuffer = ShaderUtils.createFloatBuffer(vertexArray);
        textureVerticesBuffer = ShaderUtils.createFloatBuffer(textureArray);
        drawOrderBuffer = ShaderUtils.createShortBuffer(drawOrderArray);

        String vertexShaderCode = loadFromAssetsFile("vertexshader.glsl", context.getResources());
        String fragmentShaderCode = loadFromAssetsFile("fragmentshader.glsl", context.getResources());

        int vertexShader = ShaderUtils.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = ShaderUtils.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        mProgram = ShaderUtils.createProgram(vertexShader, fragmentShader);
    }

    public void drawSelf() {
        GLES20.glUseProgram(mProgram);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, mTextureId);

        int positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        GLES20.glVertexAttribPointer(positionHandle, 2, GLES20.GL_FLOAT, false, 2 * 4, vertexBuffer);
        GLES20.glEnableVertexAttribArray(positionHandle);

        int textureHandle = GLES20.glGetAttribLocation(mProgram, "inputTextureCoordinate");
        GLES20.glVertexAttribPointer(textureHandle, 2, GLES20.GL_FLOAT, false, 2 * 4, textureVerticesBuffer);
        GLES20.glEnableVertexAttribArray(textureHandle);

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, drawOrderArray.length, GLES20.GL_UNSIGNED_SHORT, drawOrderBuffer);

        GLES20.glDisableVertexAttribArray(positionHandle);
        GLES20.glDisableVertexAttribArray(textureHandle);
    }


}
