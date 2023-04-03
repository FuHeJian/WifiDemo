package com.example.wifidemo1.opengl;

import android.opengl.GLES30;
import android.text.TextUtils;

import java.nio.FloatBuffer;

public class GPUImagePointsFilter extends GPUImageFilter {
    private static final String TAG = "GPUImageFacePoints";

    private static final String VertexShader = "" +
            "attribute vec4 aPosition;\n" +
            "void main() {\n" +
            "    gl_Position = aPosition;\n" +
            "    gl_PointSize = 64.0;\n" +
            "}";

    private static final String FragmentShader = "" +
            "precision mediump float;\n" +
            "uniform vec4 color;\n" +
            "void main() {\n" +
            "    gl_FragColor = color;\n" +
            "}";

    private final float color[] = {1.0f, 0.0f, 0.0f, 1.0f};
    private final float color_point[] = {1.0f, 1.0f, 0.0f, 1.0f};

    private boolean isPoints = false;

    private int mColorHandle;
    private int mPointCount = 114;
    private float[] mPoints;
    private FloatBuffer mPointVertexBuffer;

    public GPUImagePointsFilter() {
        this(VertexShader, FragmentShader);
    }

    public GPUImagePointsFilter(String vertexShader, String fragmentShader) {
        super(vertexShader, fragmentShader);
//        mPoints = new float[mPointCount * 2];
//        mPointVertexBuffer = OpenGLUtils.createFloatBuffer(mPoints);
    }

    public void setPoints(boolean value) {
        isPoints = value;
    }

    @Override
    public void onInitialized() {
        super.onInitialized();
        if (!TextUtils.isEmpty(mVertexShader) && !TextUtils.isEmpty(mFragmentShader)) {
            mGLProgId = OpenGLUtils.createProgram(mVertexShader, mFragmentShader);
            mGLAttribPosition = GLES30.glGetAttribLocation(mGLProgId, "aPosition");
            OpenGLUtils.checkGlError("glGetAttribLocation");
            mColorHandle = GLES30.glGetUniformLocation(mGLProgId, "color");
            mIsInitialized = true;
        } else {
            mGLAttribPosition = OpenGLUtils.GL_NOT_INIT;

            mColorHandle = OpenGLUtils.GL_NOT_INIT;
            mIsInitialized = false;
        }
        mGLAttribTextureCoordinate = OpenGLUtils.GL_NOT_INIT;
        mGLUniformTexture = OpenGLUtils.GL_NOT_TEXTURE;
    }

    private int pointsLength;

    @Override
    public void onDraw(int textureId, FloatBuffer cubeBuffer, FloatBuffer textureBuffer) {
        if (!mIsInitialized) {
            return;
        }
        GLES30.glViewport(0, 0, mOutputWidth, mOutputHeight);
        GLES30.glUseProgram(mGLProgId);
        runPendingOnDrawTasks();
        GLES30.glEnableVertexAttribArray(mGLAttribPosition);
        if (isPoints) {
            GLES30.glUniform4fv(mColorHandle, 1, color_point, 0);
        } else {
            GLES30.glUniform4fv(mColorHandle, 1, color, 0);
        }
        onDrawFrameBegin();

        onDrawFrameAfter();
        GLES30.glDisableVertexAttribArray(mGLAttribPosition);
    }

    /**
     * 调用glDrawArrays/glDrawElements之前，方便添加其他属性
     */
    public void onDrawFrameBegin() {

    }

    /**
     * glDrawArrays/glDrawElements调用之后，方便销毁其他属性
     */
    public void onDrawFrameAfter() {

    }
}
