package com.example.wifidemo1.opengl;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * openGL 常用方法工具
 * Created by panbin on 2017/9/26.
 */

public class OpenGLUtils {

    private static final String TAG="OpenGLUtils";
    // 从初始化失败
    public static final int GL_NOT_INIT = -1;
    // 没有Texture
    public static final int GL_NOT_TEXTURE = -1;

    public static final float[] IDENTITY_MATRIX;
    static {
        IDENTITY_MATRIX = new float[16];
        Matrix.setIdentityM(IDENTITY_MATRIX, 0);
    }


    public static final float CUBE[] = {
            -1.0f, -1.0f,
            1.0f, -1.0f,
            -1.0f, 1.0f,
            1.0f, 1.0f,
    };

    private static final int SIZEOF_FLOAT = 4;
    private static final int SIZEOF_SHORT = 2;

    public static final int NO_TEXTURE = -1;

    //
    public static int loadShader(final String strSource, final int iType) {
        int[] compiled = new int[1];
        int iShader = GLES20.glCreateShader(iType);
        GLES20.glShaderSource(iShader, strSource);
        GLES20.glCompileShader(iShader);
        GLES20.glGetShaderiv(iShader, GLES20.GL_COMPILE_STATUS, compiled, 0);
        if (compiled[0] == 0) {
            Log.d("Load Shader Failed", "Compilation\n" + GLES20.glGetShaderInfoLog(iShader));
            return 0;
        }
        return iShader;
    }

    //根据输入  vertex(顶点着色器) 以及 fragement（片元着色器） 加载 GLES 渲染管道
    public static int loadProgram(final String strVSource, final String strFSource) {
        int iVShader;
        int iFShader;
        int iProgId;
        int[] link = new int[1];
        iVShader = loadShader(strVSource, GLES20.GL_VERTEX_SHADER);
        if (iVShader == 0) {
            Log.d("Load Program", "Vertex Shader Failed");
            return 0;
        }
        iFShader = loadShader(strFSource, GLES20.GL_FRAGMENT_SHADER);
        if (iFShader == 0) {
            Log.d("Load Program", "Fragment Shader Failed");
            return 0;
        }

        iProgId = GLES20.glCreateProgram();

        GLES20.glAttachShader(iProgId, iVShader);
        GLES20.glAttachShader(iProgId, iFShader);

        GLES20.glLinkProgram(iProgId);

        GLES20.glGetProgramiv(iProgId, GLES20.GL_LINK_STATUS, link, 0);
        if (link[0] <= 0) {
            Log.d("Load Program", "Linking Failed");
            return 0;
        }
        GLES20.glDeleteShader(iVShader);
        GLES20.glDeleteShader(iFShader);
        return iProgId;
    }


    /**
     * Creates a new program from the supplied vertex and fragment shaders.
     *
     * @return A handle to the program, or 0 on failure.
     */
    public static int createProgram(String vertexSource, String fragmentSource) {
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexSource);
        if (vertexShader == 0) {
            return 0;
        }
        int pixelShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource);
        if (pixelShader == 0) {
            return 0;
        }

        int program = GLES20.glCreateProgram();
        checkGlError("glCreateProgram");
        if (program == 0) {
            Log.e(TAG, "Could not create program");
        }
        GLES20.glAttachShader(program, vertexShader);
        checkGlError("glAttachShader");
        GLES20.glAttachShader(program, pixelShader);
        checkGlError("glAttachShader");
        GLES20.glLinkProgram(program);
        int[] linkStatus = new int[1];
        GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);
        if (linkStatus[0] != GLES20.GL_TRUE) {
            Log.e(TAG, "Could not link program: ");
            Log.e(TAG, GLES20.glGetProgramInfoLog(program));
            GLES20.glDeleteProgram(program);
            program = 0;
        }
        return program;
    }

    /**
     * Compiles the provided shader source.
     *
     * @return A handle to the shader, or 0 on failure.
     */
    public static int loadShader(int shaderType, String source) {
        int shader = GLES20.glCreateShader(shaderType);
        checkGlError("glCreateShader type=" + shaderType);
        GLES20.glShaderSource(shader, source);
        GLES20.glCompileShader(shader);
        int[] compiled = new int[1];
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
        if (compiled[0] == 0) {
            Log.e(TAG, "Could not compile shader " + shaderType + ":");
            Log.e(TAG, " " + GLES20.glGetShaderInfoLog(shader));
            GLES20.glDeleteShader(shader);
            shader = 0;
        }
        return shader;
    }


    /**
     * Checks to see if a GLES error has been raised.
     */
    public static void checkGlError(String op) {
        int error = GLES20.glGetError();
        if (error != GLES20.GL_NO_ERROR) {
            String msg = op + ": glError 0x" + Integer.toHexString(error);
            Log.e(TAG, msg);
            throw new RuntimeException(msg);
        }
    }

    /**
     * Checks to see if the location we obtained is valid.  GLES returns -1 if a label
     * could not be found, but does not set the GL error.
     * <p>
     * Throws a RuntimeException if the location is invalid.
     */
    public static void checkLocation(int location, String label) {
        if (location < 0) {
            throw new RuntimeException("Unable to locate '" + label + "' in program");
        }
    }
    /**
     * Allocates a direct float buffer, and populates it with the float array data.
     */
    public static FloatBuffer createFloatBuffer(float[] coords) {
        // Allocate a direct ByteBuffer, using 4 bytes per float, and copy coords into it.
        ByteBuffer bb = ByteBuffer.allocateDirect(coords.length * SIZEOF_FLOAT);
        bb.order(ByteOrder.nativeOrder());
        FloatBuffer fb = bb.asFloatBuffer();
        fb.put(coords);
        fb.position(0);
        return fb;
    }

    public static FloatBuffer createFloatBuffer(float[] coords, float minusScale) {
        // Allocate a direct ByteBuffer, using 4 bytes per float, and copy coords into it.
        ByteBuffer bb = ByteBuffer.allocateDirect(coords.length * SIZEOF_FLOAT);
        bb.order(ByteOrder.nativeOrder());
        FloatBuffer fb = bb.asFloatBuffer();
        float scale = (1.0f - minusScale)/2;
        float[] result = new float[]{
                flipScale(coords[0],scale), flipScale(coords[1],scale),
                flipScale(coords[2],scale), flipScale(coords[3],scale),
                flipScale(coords[4],scale), flipScale(coords[5],scale),
                flipScale(coords[6],scale), flipScale(coords[7],scale),
        };
        fb.put(coords);
        fb.position(0);
        return fb;
    }

    private static float flipScale(float value, float scale) {
        if (value - scale > 0f) {
            return value - scale;
        }
        return scale;
    }

    public static FloatBuffer getTextureFloatBuffer(Rotation rotation){
        FloatBuffer textureBuffer=null;
        switch (rotation){
            case NORMAL:
                textureBuffer = ByteBuffer.allocateDirect(TextureRotationUtil.TEXTURE_NO_ROTATION.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
                textureBuffer.put(TextureRotationUtil.TEXTURE_NO_ROTATION);
                break;
            case ROTATION_90:
                textureBuffer = ByteBuffer.allocateDirect(TextureRotationUtil.TEXTURE_ROTATED_90.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
                textureBuffer.put(TextureRotationUtil.TEXTURE_ROTATED_90);
                break;
            case ROTATION_180:
                textureBuffer = ByteBuffer.allocateDirect(TextureRotationUtil.TEXTURE_ROTATED_180.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
                textureBuffer.put(TextureRotationUtil.TEXTURE_ROTATED_180);
                break;
            case ROTATION_270:
                textureBuffer = ByteBuffer.allocateDirect(TextureRotationUtil.TEXTURE_ROTATED_270.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
                textureBuffer.put(TextureRotationUtil.TEXTURE_ROTATED_270);
                break;
            default:
                textureBuffer = ByteBuffer.allocateDirect(TextureRotationUtil.TEXTURE_NO_ROTATION.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
                textureBuffer.put(TextureRotationUtil.TEXTURE_NO_ROTATION);
                break;
        }
        return textureBuffer;
    }

    public static FloatBuffer getCubeBuffer(){
        FloatBuffer mGLCubeBuffer = ByteBuffer.allocateDirect(OpenGLUtils.CUBE.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mGLCubeBuffer.put(OpenGLUtils.CUBE).position(0);
        return mGLCubeBuffer;
    }

    public static int loadTexture(final Bitmap img, final int usedTexId, final boolean recycle) {
        int textures[] = new int[1];
        if (usedTexId == NO_TEXTURE) {
            GLES20.glGenTextures(1, textures, 0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0]);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, img, 0);
        } else {
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, usedTexId);
            GLUtils.texSubImage2D(GLES20.GL_TEXTURE_2D, 0, 0, 0, img);
            textures[0] = usedTexId;
        }
        if (recycle) {
            img.recycle();
        }
        return textures[0];
    }

    public static int createTextureObj(){
        int[] textures = new int[1];
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        checkGlError("glActiveTexture");
        GLES20.glGenTextures(1, textures, 0);
        checkGlError("glGenTextures");
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textures[0]);
        checkGlError("glBindTexture " + textures[0]);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        checkGlError("glTexParameter");
        return textures[0];
    }

    public static int createTextureObj(int id){
        int[] textures = new int[2];
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        checkGlError("glActiveTexture");
        GLES20.glGenTextures(id, textures, 0);
        checkGlError("glGenTextures");
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textures[0]);
        checkGlError("glBindTexture " + textures[0]);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        checkGlError("glTexParameter");
        return textures[1];
    }

    public static int[] createTextureObjs(int count) {
        int[] textures = new int[count];
        GLES20.glGenTextures(count, textures, 0);
        checkGlError("glGenTextures");
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        checkGlError("glTexParameter");
        return textures;
    }

    /**
     * 创建Sampler2D的Framebuffer 和 Texture
     * @param frameBuffer
     * @param frameBufferTexture
     * @param width
     * @param height
     */
    public static void createFrameBuffer(int[] frameBuffer, int[] frameBufferTexture,
                                         int width, int height) {
        GLES30.glGenFramebuffers(frameBuffer.length, frameBuffer, 0);
        GLES30.glGenTextures(frameBufferTexture.length, frameBufferTexture, 0);
        for (int i = 0; i < frameBufferTexture.length; i++) {
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, frameBufferTexture[i]);
            GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D, 0, GLES30.GL_RGBA, width, height, 0,
                    GLES30.GL_RGBA, GLES30.GL_UNSIGNED_BYTE, null);
            GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,
                    GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
            GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,
                    GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR);
            GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,
                    GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE);
            GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,
                    GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE);
            GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, frameBuffer[i]);
            GLES30.glFramebufferTexture2D(GLES30.GL_FRAMEBUFFER, GLES30.GL_COLOR_ATTACHMENT0,
                    GLES30.GL_TEXTURE_2D, frameBufferTexture[i], 0);
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0);
            GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, 0);
        }
        checkGlError("createFrameBuffer");
    }

    /**
     * 加载mipmap纹理
     * @param context
     * @param name
     * @return
     */
    public static int createTextureFromAssets(Context context, String name) {
        int[] textureHandle = new int[1];
        GLES30.glGenTextures(1, textureHandle, 0);
        if (textureHandle[0] != 0) {
            Bitmap bitmap = BitmapUtils.getImageFromAssetsFile(context, name);
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureHandle[0]);

            GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,
                    GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
            GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,
                    GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR);
            GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,
                    GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE);
            GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,
                    GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE);
            GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, bitmap, 0);
            bitmap.recycle();
        }
        if (textureHandle[0] == 0) {
            throw new RuntimeException("Error loading texture.");
        }
        return textureHandle[0];
    }

    /**
     * 加载mipmap纹理
     * @param bitmap bitmap图片
     * @return
     */
    public static int createTexture(Bitmap bitmap) {
        int[] texture = new int[1];
        if (bitmap != null && !bitmap.isRecycled()) {
            //生成纹理
            GLES30.glGenTextures(1, texture, 0);
            checkGlError("glGenTexture");
            //生成纹理
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, texture[0]);
            //设置缩小过滤为使用纹理中坐标最接近的一个像素的颜色作为需要绘制的像素颜色
            GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,
                    GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_NEAREST);
            //设置放大过滤为使用纹理中坐标最接近的若干个颜色，通过加权平均算法得到需要绘制的像素颜色
            GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,
                    GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
            //设置环绕方向S，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
            GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,
                    GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE);
            //设置环绕方向T，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
            GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,
                    GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE);
            //根据以上指定的参数，生成一个2D纹理
            GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, bitmap, 0);
            return texture[0];
        }
        return 0;
    }

    /**
     * 创建ShortBuffer
     * @param coords
     * @return
     */
    public static ShortBuffer createShortBuffer(short[] coords) {
        ByteBuffer bb = ByteBuffer.allocateDirect(coords.length * SIZEOF_SHORT);
        bb.order(ByteOrder.nativeOrder());
        ShortBuffer sb = bb.asShortBuffer();
        sb.put(coords);
        sb.position(0);
        return sb;
    }

    /**
     * 从Assets文件夹中读取shader字符串
     * @param context
     * @param path      shader相对路径
     * @return
     */
    public static String getShaderFromAssets(Context context, String path) {
        InputStream inputStream = null;
        try {
            inputStream = context.getResources().getAssets().open(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return getShaderStringFromStream(inputStream);
    }

    /**
     * 从输入流中读取shader字符创
     * @param inputStream
     * @return
     */
    private static String getShaderStringFromStream(InputStream inputStream) {
        if (inputStream == null) {
            return null;
        }
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line).append("\n");
            }
            reader.close();
            return builder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //获取旋转、平移、缩放矩阵
    public static float[] getAdjustMatrix(Rotation rotation, ScaleModel scaleM,ScaleModel translateM){
        float[] transformM=new float[16];
        boolean flag=false;
        if(rotation == null){
            if(scaleM != null){
                Matrix.scaleM(transformM,scaleM.getmOffset(),scaleM.getX(),scaleM.getY(),scaleM.getZ());
                flag=true;
            }
            if(translateM != null){
                Matrix.translateM(transformM,scaleM.getmOffset(),scaleM.getX(),scaleM.getY(),scaleM.getZ());
                flag=true;
            }
        }else{
            switch (rotation){
                case ROTATION_90:
                    flag=true;
                    Matrix.setRotateM(transformM, 0, 90, 0, 0, 1.0f);
                    if(scaleM != null){
                        Matrix.scaleM(transformM,scaleM.getmOffset(),scaleM.getX(),scaleM.getY(),scaleM.getZ());
                    }
                    if(translateM != null){
                        Matrix.translateM(transformM,scaleM.getmOffset(),scaleM.getX(),scaleM.getY(),scaleM.getZ());
                    }
                    break;
                case ROTATION_180:
                    flag=true;
                    Matrix.setRotateM(transformM, 0, 180, 0, 0, 1.0f);
                    if(scaleM != null){
                        Matrix.scaleM(transformM,scaleM.getmOffset(),scaleM.getX(),scaleM.getY(),scaleM.getZ());
                    }
                    if(translateM != null){
                        Matrix.translateM(transformM,scaleM.getmOffset(),scaleM.getX(),scaleM.getY(),scaleM.getZ());
                    }
                    break;
                case ROTATION_270:
                    flag=true;
                    Matrix.setRotateM(transformM, 0, 270, 0, 0, 1.0f);
                    if(scaleM != null){
                        Matrix.scaleM(transformM,scaleM.getmOffset(),scaleM.getX(),scaleM.getY(),scaleM.getZ());
                    }
                    if(translateM != null){
                        Matrix.translateM(transformM,scaleM.getmOffset(),scaleM.getX(),scaleM.getY(),scaleM.getZ());
                    }
                    break;
                case NORMAL:
                    flag=true;
                    Matrix.setRotateM(transformM, 0, 0, 0, 0, 1.0f);
                    if(scaleM != null){
                        Matrix.scaleM(transformM,scaleM.getmOffset(),scaleM.getX(),scaleM.getY(),scaleM.getZ());
                    }
                    if(translateM != null){
                        Matrix.translateM(transformM,scaleM.getmOffset(),scaleM.getX(),scaleM.getY(),scaleM.getZ());
                    }
                    break;
                default:
                    break;
            }
        }
        if(flag){
            return transformM;
        }
        return null;
    }

    /**
     * 绑定纹理
     * @param location  句柄
     * @param texture   纹理id
     * @param index     索引
     */
    public static void bindTexture(int location, int texture, int index) {
        bindTexture(location, texture, index, GLES30.GL_TEXTURE_2D);
    }

    /**
     * 绑定纹理
     * @param location  句柄
     * @param texture   纹理值
     * @param index     绑定的位置
     * @param textureType 纹理类型
     */
    public static void bindTexture(int location, int texture, int index, int textureType) {
        // 最多支持绑定32个纹理
        if (index > 31) {
            throw new IllegalArgumentException("index must be no more than 31!");
        }
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0 + index);
        GLES30.glBindTexture(textureType, texture);
        GLES30.glUniform1i(location, index);
    }

    /**
     * 创建纹理并配置
     */
    public static void glGenTextures(int[] textures){
        //创建
        GLES20.glGenTextures(textures.length, textures, 0);
        //配置
        for (int i = 0; i < textures.length; i++) {
            // opengl的操作 面向过程的操作
            //bind 就是绑定 ，表示后续的操作就是在这一个 纹理上进行
            // 后面的代码配置纹理，就是配置bind的这个纹理
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,textures[i]);
            /**
             * 过滤参数
             *  当纹理被使用到一个比他大 或者比他小的形状上的时候 该如何处理
             */
            // 放大
            // GLES20.GL_LINEAR  : 使用纹理中坐标附近的若干个颜色，通过平均算法 进行放大
            // GLES20.GL_NEAREST : 使用纹理坐标最接近的一个颜色作为放大的要绘制的颜色
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);

            /*设置纹理环绕方向*/
            //纹理坐标 一般用st表示，其实就是x y
            //纹理坐标的范围是0-1。超出这一范围的坐标将被OpenGL根据GL_TEXTURE_WRAP参数的值进行处理
            //GL_TEXTURE_WRAP_S, GL_TEXTURE_WRAP_T 分别为x，y方向。
            //GL_REPEAT:平铺
            //GL_MIRRORED_REPEAT: 纹理坐标是奇数时使用镜像平铺
            //GL_CLAMP_TO_EDGE: 坐标超出部分被截取成0、1，边缘拉伸
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);

            //解绑
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,0);
        }
    }
}
