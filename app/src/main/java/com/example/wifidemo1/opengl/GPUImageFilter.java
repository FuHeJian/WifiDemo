/*
 * Copyright (C) 2012 CyberAgent
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.wifidemo1.opengl;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.PointF;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.Matrix;
import android.util.Log;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.LinkedList;

public class GPUImageFilter {
    private static final String TAG = "GPUImageFilter";
    public static final String NO_FILTER_VERTEX_SHADER = "" +
            "uniform mat4 customMatrix;\n" +
            "attribute vec4 position;\n" +
            "attribute vec4 inputTextureCoordinate;\n" +
            " \n" +
            "varying vec2 textureCoordinate;\n" +
            " \n" +
            "void main()\n" +
            "{\n" +
            "    gl_Position = customMatrix * position;\n" +
            "    textureCoordinate = inputTextureCoordinate.xy;\n" +
            "}";
    public static final String NO_FILTER_FRAGMENT_SHADER = "" +
            "#extension GL_OES_EGL_image_external : require\n" +
            "varying highp vec2 textureCoordinate;\n" +
            " \n" +
            "uniform samplerExternalOES inputTexture;\n" +
            " \n" +
            "void main()\n" +
            "{\n" +
            "     gl_FragColor = texture2D(inputTexture, textureCoordinate);\n" +
            "}";

    private final LinkedList<Runnable> mRunOnDraw;
    protected final String mVertexShader;
    protected final String mFragmentShader;
    protected int mGLProgId;
    protected int mGLAttribPosition;
    protected int mGLUniformTexture;
    protected int mGLAttribTextureCoordinate;
    protected int mGLCustomMatrix;
    protected int mOutputWidth;
    protected int mOutputHeight;
    protected boolean mIsInitialized;

    protected FloatBuffer mGLCubeBuffer;
    protected FloatBuffer mGLTextureBuffer;
    protected int rotation;
    private float[] customMatrix;
    private float[] scaleMatrix;
    private float[] rotateMatrix;
    private float[] calculateMatrix;
    private float[] unitMatrix;

    public void setRotation(int degress) {
        this.rotation = degress;
    }

    public GPUImageFilter() {
        this(NO_FILTER_VERTEX_SHADER, NO_FILTER_FRAGMENT_SHADER);
    }

    public GPUImageFilter(final String vertexShader, final String fragmentShader) {
        mRunOnDraw = new LinkedList<Runnable>();
        mVertexShader = vertexShader;
        mFragmentShader = fragmentShader;

        customMatrix = new float[16];
        scaleMatrix = new float[16];
        rotateMatrix = new float[16];
        calculateMatrix = new float[16];
        unitMatrix = new float[16];
        Matrix.setIdentityM(customMatrix, 0);
        Matrix.setIdentityM(scaleMatrix, 0);
        Matrix.setIdentityM(rotateMatrix, 0);
        Matrix.setIdentityM(calculateMatrix, 0);
        Matrix.setIdentityM(unitMatrix, 0);
    }

    public final void init() {
        onInit();
        mIsInitialized = true;
        onInitialized();
    }

    public void onInit() {
        mGLProgId = OpenGLUtils.loadProgram(mVertexShader, mFragmentShader);
        mGLAttribPosition = GLES20.glGetAttribLocation(mGLProgId, "position");
        OpenGLUtils.checkGlError("glGetAttribLocation");
        mGLUniformTexture = GLES20.glGetUniformLocation(mGLProgId, "inputTexture");
        OpenGLUtils.checkGlError("glGetUniformLocation");
        mGLAttribTextureCoordinate = GLES20.glGetAttribLocation(mGLProgId, "inputTextureCoordinate");
        OpenGLUtils.checkGlError("glGetAttribLocation");

        mGLCustomMatrix = GLES20.glGetUniformLocation(mGLProgId, "customMatrix");
        OpenGLUtils.checkGlError("glGetUniformLocation");

        mGLCubeBuffer = ByteBuffer.allocateDirect(OpenGLUtils.CUBE.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mGLCubeBuffer.put(OpenGLUtils.CUBE).position(0);
        mGLTextureBuffer = ByteBuffer.allocateDirect(TextureRotationUtil.TEXTURE_NO_ROTATION.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mGLTextureBuffer.put(TextureRotationUtil.TEXTURE_NO_ROTATION);

        mIsInitialized = true;
    }

    public void onInitialized() {
        setCustomMatrix(customMatrix);
    }

    public final void destroy() {
        onDestroy();
        if (mIsInitialized) {
            destroyFrameBuffer();
        }
        mIsInitialized = false;
        GLES20.glDeleteProgram(mGLProgId);
    }

    public void onDestroy() {
    }

    public void onOutputSizeChanged(final int width, final int height) {
        mOutputWidth = width;
        mOutputHeight = height;
        initFrameBuffer(width,height);
    }

    public void onDraw(final int textureId, final FloatBuffer cubeBuffer, final FloatBuffer textureBuffer) {
        GLES20.glUseProgram(mGLProgId);
        runPendingOnDrawTasks();
        if (!mIsInitialized) {
            return;
        }
        cubeBuffer.position(0);
        GLES20.glVertexAttribPointer(mGLAttribPosition, 2, GLES20.GL_FLOAT, false, 0, cubeBuffer);
        OpenGLUtils.checkGlError("glVertexAttribPointer");
        GLES20.glEnableVertexAttribArray(mGLAttribPosition);
        OpenGLUtils.checkGlError("glEnableVertexAttribArray");
        textureBuffer.position(0);
        GLES20.glVertexAttribPointer(mGLAttribTextureCoordinate, 2, GLES20.GL_FLOAT, false, 0, textureBuffer);
        OpenGLUtils.checkGlError("glVertexAttribPointer");
        GLES20.glEnableVertexAttribArray(mGLAttribTextureCoordinate);
        OpenGLUtils.checkGlError("glEnableVertexAttribArray");
        if (textureId != OpenGLUtils.NO_TEXTURE) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            OpenGLUtils.checkGlError("glActiveTexture");
            GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureId);
            OpenGLUtils.checkGlError("glBindTexture");
            GLES20.glUniform1i(mGLUniformTexture, 0);
            OpenGLUtils.checkGlError("glUniform1i");
        }
        onDrawArraysPre();
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        OpenGLUtils.checkGlError("glDrawArrays");
        GLES20.glDisableVertexAttribArray(mGLAttribPosition);
        OpenGLUtils.checkGlError("glDisableVertexAttribArray");
        GLES20.glDisableVertexAttribArray(mGLAttribTextureCoordinate);
        OpenGLUtils.checkGlError("glDisableVertexAttribArray");
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0);
        OpenGLUtils.checkGlError("glBindTexture");
    }


    public void onDraw(final int textureId) {
        GLES20.glUseProgram(mGLProgId);
        runPendingOnDrawTasks();
        if (!mIsInitialized) {
            return;
        }
        mGLCubeBuffer.position(0);
        GLES20.glVertexAttribPointer(mGLAttribPosition, 2, GLES20.GL_FLOAT, false, 0, mGLCubeBuffer);
        OpenGLUtils.checkGlError("glVertexAttribPointer");
        GLES20.glEnableVertexAttribArray(mGLAttribPosition);
        OpenGLUtils.checkGlError("glEnableVertexAttribArray");
        mGLTextureBuffer.position(0);
        GLES20.glVertexAttribPointer(mGLAttribTextureCoordinate, 2, GLES20.GL_FLOAT, false, 0, mGLTextureBuffer);
        OpenGLUtils.checkGlError("glVertexAttribPointer");
        GLES20.glEnableVertexAttribArray(mGLAttribTextureCoordinate);
        OpenGLUtils.checkGlError("glEnableVertexAttribArray");
        if (textureId != OpenGLUtils.NO_TEXTURE) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            OpenGLUtils.checkGlError("glActiveTexture");
            GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureId);
            OpenGLUtils.checkGlError("glBindTexture");
            GLES20.glUniform1i(mGLUniformTexture, 0);
            OpenGLUtils.checkGlError("glUniform1i");
        }
        onDrawArraysPre();
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        OpenGLUtils.checkGlError("glDrawArrays");
        GLES20.glDisableVertexAttribArray(mGLAttribPosition);
        OpenGLUtils.checkGlError("glDisableVertexAttribArray");
        GLES20.glDisableVertexAttribArray(mGLAttribTextureCoordinate);
        OpenGLUtils.checkGlError("glDisableVertexAttribArray");
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0);
        OpenGLUtils.checkGlError("glBindTexture");
    }

    public int onDrawFBO(final int textureId) {
//        GLES20.glUseProgram(mGLProgId);

        bindFrameBuffer();

        runPendingOnDrawTasks();
        if (!mIsInitialized) {
            return unBindFrameBuffer();
        }
        mGLCubeBuffer.position(0);
        GLES20.glVertexAttribPointer(mGLAttribPosition, 2, GLES20.GL_FLOAT, false, 0, mGLCubeBuffer);
        OpenGLUtils.checkGlError("glVertexAttribPointer");
        GLES20.glEnableVertexAttribArray(mGLAttribPosition);
        OpenGLUtils.checkGlError("glEnableVertexAttribArray");
        mGLTextureBuffer.position(0);
        GLES20.glVertexAttribPointer(mGLAttribTextureCoordinate, 2, GLES20.GL_FLOAT, false, 0, mGLTextureBuffer);
        OpenGLUtils.checkGlError("glVertexAttribPointer");
        GLES20.glEnableVertexAttribArray(mGLAttribTextureCoordinate);
        OpenGLUtils.checkGlError("glEnableVertexAttribArray");
        if (textureId != OpenGLUtils.NO_TEXTURE) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            OpenGLUtils.checkGlError("glActiveTexture");
            GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureId);
            OpenGLUtils.checkGlError("glBindTexture");
            GLES20.glUniform1i(mGLUniformTexture, 0);
            OpenGLUtils.checkGlError("glUniform1i");
        }
        onDrawArraysPre();
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        OpenGLUtils.checkGlError("glDrawArrays");
        GLES20.glDisableVertexAttribArray(mGLAttribPosition);
        OpenGLUtils.checkGlError("glDisableVertexAttribArray");
        GLES20.glDisableVertexAttribArray(mGLAttribTextureCoordinate);
        OpenGLUtils.checkGlError("glDisableVertexAttribArray");
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0);
        OpenGLUtils.checkGlError("glBindTexture");

        return unBindFrameBuffer();
    }


    protected void onDrawArraysPre() {
    }

    protected void runPendingOnDrawTasks() {
        synchronized (mRunOnDraw) {
            while (!mRunOnDraw.isEmpty()) {
                mRunOnDraw.removeFirst().run();
            }
        }
    }

    public boolean isInitialized() {
        return mIsInitialized;
    }

    public int getOutputWidth() {
        return mOutputWidth;
    }

    public int getOutputHeight() {
        return mOutputHeight;
    }

    public int getProgram() {
        return mGLProgId;
    }

    public int getAttribPosition() {
        return mGLAttribPosition;
    }

    public int getAttribTextureCoordinate() {
        return mGLAttribTextureCoordinate;
    }

    public int getUniformTexture() {
        return mGLUniformTexture;
    }

    protected void setInteger(final int location, final int intValue) {
        runOnDraw(new Runnable() {
            @Override
            public void run() {
                GLES20.glUniform1i(location, intValue);
            }
        });
    }

    protected void setFloat(final int location, final float floatValue) {
        runOnDraw(new Runnable() {
            @Override
            public void run() {
                GLES20.glUniform1f(location, floatValue);
            }
        });
    }

    protected void setFloatVec2(final int location, final float[] arrayValue) {
        runOnDraw(new Runnable() {
            @Override
            public void run() {
                GLES20.glUniform2fv(location, 1, FloatBuffer.wrap(arrayValue));
            }
        });
    }

    protected void setFloatVec3(final int location, final float[] arrayValue) {
        runOnDraw(new Runnable() {
            @Override
            public void run() {
                GLES20.glUniform3fv(location, 1, FloatBuffer.wrap(arrayValue));
            }
        });
    }

    protected void setFloatVec4(final int location, final float[] arrayValue) {
        runOnDraw(new Runnable() {
            @Override
            public void run() {
                GLES20.glUniform4fv(location, 1, FloatBuffer.wrap(arrayValue));
            }
        });
    }

    protected void setFloatArray(final int location, final float[] arrayValue) {
        runOnDraw(new Runnable() {
            @Override
            public void run() {
                GLES20.glUniform1fv(location, arrayValue.length, FloatBuffer.wrap(arrayValue));
            }
        });
    }

    protected void setPoint(final int location, final PointF point) {
        runOnDraw(new Runnable() {

            @Override
            public void run() {
                float[] vec2 = new float[2];
                vec2[0] = point.x;
                vec2[1] = point.y;
                GLES20.glUniform2fv(location, 1, vec2, 0);
            }
        });
    }

    protected void setUniformMatrix3f(final int location, final float[] matrix) {
        runOnDraw(new Runnable() {

            @Override
            public void run() {
                GLES20.glUniformMatrix3fv(location, 1, false, matrix, 0);
            }
        });
    }

    protected void setUniformMatrix4f(final int location, final float[] matrix) {
        runOnDraw(new Runnable() {

            @Override
            public void run() {
                GLES20.glUniformMatrix4fv(location, 1, false, matrix, 0);
            }
        });
    }

    protected void runOnDraw(final Runnable runnable) {
        synchronized (mRunOnDraw) {
            mRunOnDraw.addLast(runnable);
        }
    }

    public static String loadShader(String file, Context context) {
        try {
            AssetManager assetManager = context.getAssets();
            InputStream ims = assetManager.open(file);

            String re = convertStreamToString(ims);
            ims.close();
            return re;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    public void adjustBrigness(float brigness) {

    }

    public static String convertStreamToString(InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }


    public void onChangedRotation(int degress) {
        this.rotation = degress;
        switch (rotation) {
            case 0:
                mGLTextureBuffer = ByteBuffer.allocateDirect(TextureRotationUtil.TEXTURE_NO_ROTATION.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
                mGLTextureBuffer.put(TextureRotationUtil.TEXTURE_NO_ROTATION);
                break;
            case 90:
                mGLTextureBuffer = ByteBuffer.allocateDirect(TextureRotationUtil.TEXTURE_ROTATED_90.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
                mGLTextureBuffer.put(TextureRotationUtil.TEXTURE_ROTATED_90);
                break;
            case 180:
                mGLTextureBuffer = ByteBuffer.allocateDirect(TextureRotationUtil.TEXTURE_ROTATED_180.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
                mGLTextureBuffer.put(TextureRotationUtil.TEXTURE_ROTATED_180);
                break;
            case 270:
                mGLTextureBuffer = ByteBuffer.allocateDirect(TextureRotationUtil.TEXTURE_ROTATED_270.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
                mGLTextureBuffer.put(TextureRotationUtil.TEXTURE_ROTATED_270);
                break;
            default:
                mGLTextureBuffer = ByteBuffer.allocateDirect(TextureRotationUtil.TEXTURE_NO_ROTATION.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
                mGLTextureBuffer.put(TextureRotationUtil.TEXTURE_NO_ROTATION);
                break;
        }
    }

    public void onChangedRotation(int degress, boolean flipH, boolean flipV) {
        this.rotation = degress;
        switch (rotation) {
            case 0:
                mGLTextureBuffer = ByteBuffer.allocateDirect(TextureRotationUtil.TEXTURE_NO_ROTATION.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
                mGLTextureBuffer.put(TextureRotationUtil.getRotation(Rotation.NORMAL, flipH, flipV));
                break;
            case 90:
                mGLTextureBuffer = ByteBuffer.allocateDirect(TextureRotationUtil.TEXTURE_ROTATED_90.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
                mGLTextureBuffer.put(TextureRotationUtil.getRotation(Rotation.ROTATION_90, flipH, flipV));
                break;
            case 180:
                mGLTextureBuffer = ByteBuffer.allocateDirect(TextureRotationUtil.TEXTURE_ROTATED_180.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
                mGLTextureBuffer.put(TextureRotationUtil.getRotation(Rotation.ROTATION_180, flipH, flipV));
                break;
            case 270:
                mGLTextureBuffer = ByteBuffer.allocateDirect(TextureRotationUtil.TEXTURE_ROTATED_270.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
                mGLTextureBuffer.put(TextureRotationUtil.getRotation(Rotation.ROTATION_270, flipH, flipV));
                break;
            default:
                mGLTextureBuffer = ByteBuffer.allocateDirect(TextureRotationUtil.TEXTURE_NO_ROTATION.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
                mGLTextureBuffer.put(TextureRotationUtil.getRotation(Rotation.NORMAL, flipH, flipV));
                break;
        }
    }



    // FBO的宽高，可能跟输入的纹理大小不一致
    protected int mFrameWidth = -1;
    protected int mFrameHeight = -1;

    // FBO
    protected int[] mFrameBuffers;
    protected int[] mFrameBufferTextures;
    /**
     * 创建FBO
     * @param width
     * @param height
     */
    public void initFrameBuffer(int width, int height) {
        Log.d(TAG, "initFrameBuffer: width = "+width+" , height = "+height);
        if (!isInitialized()) {
            return;
        }
        if (mFrameBuffers != null && (mFrameWidth != width || mFrameHeight != height)) {
            destroyFrameBuffer();
        }
        if (mFrameBuffers == null) {
            mFrameWidth = width;
            mFrameHeight = height;
            mFrameBuffers = new int[1];
            mFrameBufferTextures = new int[1];
            OpenGLUtils.createFrameBuffer(mFrameBuffers, mFrameBufferTextures, width, height);
        }
    }

    /**
     * 销毁纹理
     */
    public void destroyFrameBuffer() {
        if (!mIsInitialized) {
            return;
        }
        if (mFrameBufferTextures != null) {
            GLES30.glDeleteTextures(1, mFrameBufferTextures, 0);
            mFrameBufferTextures = null;
        }

        if (mFrameBuffers != null) {
            GLES30.glDeleteFramebuffers(1, mFrameBuffers, 0);
            mFrameBuffers = null;
        }
        mFrameWidth = -1;
        mFrameWidth = -1;
    }

    public void bindFrameBuffer() {
        // 绑定FBO
        GLES30.glViewport(0, 0, mFrameWidth, mFrameHeight);
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, mFrameBuffers[0]);
        // 使用当前的program
        GLES30.glUseProgram(mGLProgId);
        // 运行延时任务，这个要放在glUseProgram之后，要不然某些设置项会不生效
        runPendingOnDrawTasks();
    }

    public int unBindFrameBuffer() {
        GLES30.glUseProgram(0);
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, 0);
        return mFrameBufferTextures[0];
    }

    public void setCustomMatrix(final float[] matrix) {
        //        customMatrix = matrix;
        float[] tempMatrix = new float[16];
        float[] result = new float[16];
        Matrix.setIdentityM(tempMatrix, 0);
        Matrix.setIdentityM(result, 0);
        Matrix.multiplyMM(result, 0, tempMatrix, 0, matrix, 0);
        setUniformMatrix4f(mGLCustomMatrix, result);
    }

    private void multiplyMatrix() {
        Matrix.setIdentityM(calculateMatrix, 0);
        Matrix.multiplyMM(calculateMatrix, 0, calculateMatrix, 0, rotateMatrix, 0);
        Matrix.multiplyMM(calculateMatrix, 0, calculateMatrix, 0, scaleMatrix, 0);
        setCustomMatrix(calculateMatrix);
    }

    public void setScale(float scaleX, float scaleY) {
        Matrix.setIdentityM(calculateMatrix, 0);
        Matrix.scaleM(calculateMatrix, 0, scaleX, scaleY, 1.0f);
        Matrix.multiplyMM(scaleMatrix, 0, unitMatrix, 0, calculateMatrix, 0);
        multiplyMatrix();
    }

    public void setRotate(float degress) {
        Matrix.setIdentityM(calculateMatrix, 0);
        Matrix.rotateM(calculateMatrix, 0, degress, 0.0f, 0.0f, 1.0f);
        Matrix.multiplyMM(rotateMatrix, 0, unitMatrix, 0, calculateMatrix, 0);
        multiplyMatrix();
    }

}
