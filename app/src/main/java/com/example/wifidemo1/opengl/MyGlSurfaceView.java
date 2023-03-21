package com.example.wifidemo1.opengl;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.opengl.GLES31;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Surface;

import androidx.annotation.RequiresApi;

import com.example.wifidemo1.App;
import com.example.wifidemo1.R;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringBufferInputStream;
import java.io.StringReader;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * @author: fuhejian
 * @date: 2023/3/17
 */
public class MyGlSurfaceView extends GLSurfaceView {

    public MyGlSurfaceView(Context context) {
        this(context, null);
    }

    public MyGlSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setRenderer(new MyRender());
    }

    private static class MyRender implements Renderer {

        IjkMediaPlayer ijkMediaPlayer = new IjkMediaPlayer();

        SurfaceTexture surfaceTexture;

        float[] VERTEX_POSITION={
                0f,1f,0f,
                -1f,0f,0f,
                1f,0f,0f
        };

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            IjkMediaPlayer.loadLibrariesOnce(null);
            int program = GLES31.glCreateProgram();
            int vertexShader = GLES31.glCreateShader(GLES31.GL_VERTEX_SHADER);
            int fragmentShader = GLES31.glCreateShader(GLES31.GL_FRAGMENT_SHADER);
            byte[] buff = new byte[8192];
            BufferedInputStream inputStream = new BufferedInputStream(App.GlobalManager.INSTANCE.getContext(null).getResources().openRawResource(R.raw.vertex));
            StringBuilder stringBuilder = new StringBuilder();
            int le;
            try {
                while( (le = inputStream.read(buff))>0){
                    stringBuilder.append(new String(buff,0,le));
                }
                GLES31.glShaderSource(vertexShader, stringBuilder.toString());
            } catch (Exception e) {

            }

            inputStream = new BufferedInputStream(App.GlobalManager.INSTANCE.getContext(null).getResources().openRawResource(R.raw.fragment));
            stringBuilder = new StringBuilder();
            try {
                while( (le = inputStream.read(buff))>0){
                    stringBuilder.append(new String(buff,0,le));
                }
                GLES31.glShaderSource(fragmentShader, stringBuilder.toString());
            } catch (Exception e) {

            }
            ByteBuffer allocate = ByteBuffer.allocate(VERTEX_POSITION.length);
            FloatBuffer floatBuffer = allocate.asFloatBuffer();
            floatBuffer.put(VERTEX_POSITION);
            GLES31.glVertexAttribPointer(0,3,GLES31.GL_FLOAT_VEC4,true,0,floatBuffer);

            GLES31.glCompileShader(vertexShader);
            GLES31.glCompileShader(fragmentShader);
            GLES31.glAttachShader(program,vertexShader);
            GLES31.glAttachShader(program,fragmentShader);
            GLES31.glLinkProgram(program);

            //LinkProgram后就不需要到shader了，因为编译好的shader已经保存在program了，释放内存
            GLES31.glDeleteShader(vertexShader);
            GLES31.glDeleteShader(fragmentShader);
            int[] names = new int[1];
            GLES31.glGenTextures(1,names,0);
            ijkMediaPlayer.setSurface(new Surface(surfaceTexture));
            surfaceTexture = new SurfaceTexture(names[0]);
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
                GLES31.glViewport(0,0,width,height);
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            //清除之前画面并设置界面为蓝色
            GLES31.glClearColor(0F,0F,1F,1F);

            //更新画面
//            surfaceTexture.updateTexImage();

        }

    }

}
