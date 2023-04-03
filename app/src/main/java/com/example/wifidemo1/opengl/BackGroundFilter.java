package com.example.wifidemo1.opengl;

import android.opengl.GLES20;
import android.text.TextUtils;

import java.nio.FloatBuffer;

public class BackGroundFilter extends GPUImageFilter{
    private static final String TAG = "BackGroundFilter";
    private static final String VertexShader = "" +
            "attribute vec4 position;\n" +
            "attribute vec4 inputTextureCoordinate;\n" +
            " \n" +
            "varying vec2 textureCoordinate;\n" +
            " \n" +
            "void main()\n" +
            "{\n" +
            "    gl_Position = position;\n" +
            "    textureCoordinate = inputTextureCoordinate.xy;\n" +
            "}";

    private static final String FragmentShader = "" +
            "precision lowp float;\n" +
            "varying highp vec2 textureCoordinate;\n" +
            " \n" +
            "uniform sampler2D inputTexture;\n" +
            "uniform float uColorAdjust;\n" +
            "uniform float imageAlpha;\n" +
            "uniform vec4 color;\n" +
            " \n" +
            "void main()\n" +
            "{\n" +
            "           vec4 temp = color;\n" +
            "           gl_FragColor = vec4(temp.rgb,temp.a*imageAlpha);\n"+
            "}";

    private float color[] = {0.8f, 0.6f, 1.0f, 0.5f};
    private int mColorHandle;
    private int mImageAlphaHandle;
    private FloatBuffer mPointVertexBuffer;

    public BackGroundFilter() {
        this(VertexShader, FragmentShader);
    }

    public BackGroundFilter(String vertexShader, String fragmentShader) {
        super(vertexShader, fragmentShader);
//        mPoints = new float[mPointCount * 2];
//        mPointVertexBuffer = OpenGLUtils.createFloatBuffer(mPoints);
    }

    @Override
    public void onInitialized() {
        super.onInitialized();
        if (!TextUtils.isEmpty(mVertexShader) && !TextUtils.isEmpty(mFragmentShader)) {
            mGLProgId = OpenGLUtils.createProgram(mVertexShader, mFragmentShader);
//            mGLAttribPosition = GLES20.glGetAttribLocation(mGLProgId, "aPosition");
//            OpenGLUtils.checkGlError("glGetAttribLocation");
            mColorHandle = GLES20.glGetUniformLocation(mGLProgId, "color");
            OpenGLUtils.checkGlError("glGetUniformLocation");
            mImageAlphaHandle = GLES20.glGetUniformLocation(mGLProgId, "imageAlpha");
            OpenGLUtils.checkGlError("glGetUniformLocation");
            mIsInitialized = true;
        } else {
            mGLAttribPosition = OpenGLUtils.GL_NOT_INIT;

            mColorHandle = OpenGLUtils.GL_NOT_INIT;
            mImageAlphaHandle = OpenGLUtils.GL_NOT_INIT;
            mIsInitialized = false;
        }

//        GLES20.glUniform1f(mGLUniforColorAdjust, colorAdjust);
        setFilterAlpha(1.0f);
    }

    private int pointsLength;
    private int frameCount = 0;
    public void onDraw() {
        if (!mIsInitialized) {
            return;
        }
        GLES20.glViewport(0, 0, mOutputWidth, mOutputHeight);
        GLES20.glUseProgram(mGLProgId);
        runPendingOnDrawTasks();
        GLES20.glEnableVertexAttribArray(mGLAttribPosition);
        OpenGLUtils.checkGlError("glEnableVertexAttribArray");
        onDrawFrameBegin();

        synchronized (this) {


            int rgba = 0;
            float a = (rgba >> 24) & 0xff;
            float b = (rgba >> 16) & 0xff;
            float g = (rgba >> 8) & 0xff;
            float r = rgba & 0xff;
//            color[3] = a/255.0f;
//                color[2] = b/255.0f;
//                color[1] = g/255.0f;
//                color[0] = r/255.0f;

//            color[3] = 0.23f;
//            color[2] = 0.66f;
//            color[1] = 0.66f;
//            color[0] = 0.66f;
            GLES20.glUniform4fv(mColorHandle, 1, color, 0);
            OpenGLUtils.checkGlError("glUniform4fv");

            GLES20.glEnable(GLES20.GL_BLEND);
            OpenGLUtils.checkGlError("glEnable");
            GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
            OpenGLUtils.checkGlError("glBlendFunc");


            float[] temp = new float[]{
                    -1.0f, -1.0f,
                    1.0f, -1.0f,
                    -1.0f, 1.0f,
                    1.0f, 1.0f,
            };

            if (mPointVertexBuffer == null) {
                mPointVertexBuffer = OpenGLUtils.createFloatBuffer(temp);
            } else if (pointsLength != temp.length) {
                mPointVertexBuffer = OpenGLUtils.createFloatBuffer(temp);
            }

            pointsLength = temp.length;

            mPointVertexBuffer.clear();
            mPointVertexBuffer.put(temp, 0, temp.length);
            mPointVertexBuffer.position(0);
            GLES20.glVertexAttribPointer(mGLAttribPosition, 2,
                    GLES20.GL_FLOAT, false, 0, mPointVertexBuffer);
            OpenGLUtils.checkGlError("glVertexAttribPointer");

            OpenGLUtils.checkGlError("glLineWidth");
            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
            OpenGLUtils.checkGlError("glDrawArrays");

            GLES20.glDisable(GLES20.GL_BLEND);
            OpenGLUtils.checkGlError("glDisable");

        }
        onDrawFrameAfter();
        GLES20.glDisableVertexAttribArray(mGLAttribPosition);
        OpenGLUtils.checkGlError("glDisableVertexAttribArray");
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

    public void setFilterAlpha(float alpha){
        float value = alpha;
        if (alpha < 0) {
            value = 0;
        } else if (alpha > 1) {
            value = 1;
        }
        setFloat(mImageAlphaHandle,value);
    }

    public void setColor(int color){
        int alpha = color >>> 24;
        int r = ( color & 0xff0000 ) >> 16;
        int g = ( color & 0xff00 ) >> 8;
        int b = color & 0xff;

        if (this.color == null) {
            this.color = new float[4];
        }
//        Log.d(TAG, "setColor: "+alpha+" , "+r+" , "+g+" , "+b);
        this.color[0] = r/255.0f;
        this.color[1] = g/255.0f;
        this.color[2] = b/255.0f;
        this.color[3] = alpha/255.0f;

//        Log.d(TAG, "setColor: "+this.color[3]+" , "+this.color[0]+" , "+this.color[1]+" , "+this.color[2]);
    }

}
