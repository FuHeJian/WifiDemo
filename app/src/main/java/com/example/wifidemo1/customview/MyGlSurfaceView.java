package com.example.wifidemo1.customview;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * @author: fuhejian
 * @date: 2023/3/22
 */
public class MyGlSurfaceView extends GLSurfaceView {

    public MyGlSurfaceView(Context context) {
        this(context, null);
    }

    public MyGlSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setEGLContextClientVersion(3);
        setRenderer(new MyRender());
        IjkMediaPlayer.loadLibrariesOnce(null);
    }

    private static class MyRender implements Renderer {

//        "#version 300 es\n" +
        private static final String vertex_source = "#version 300 es\n" +
                "layout(location = 0) vec4 aPosition;\n" +
                "layout(location = 1) vec4 aColor;\n" +
                "out vec4 color;\n" +
                "void main(){\n" +
                "    gl_Position = aPosition;\n" +
                "    color = aColor;\n" +
                "}";

        private static final String fragment_source = "#version 300 es\n" +
                "precision mediump float;\n" +
                "in vec4 color;\n" +
                "out vec4 fragColor;\n" +
                "void main(){\n" +
                "    fragColor = color;\n" +
                "}";

        int program;

        private static final float[] vertex_position = {
                -0.5f, -0.5f, 0f, 1f,
                0f, 1f, 0f, 1f,
                0.5f, -0.5f, 0f, 1f
        };

        private static final float[] fragment_color = {
                1f, 1f, 0f, 1f
        };

        FloatBuffer floatBuffer;

        FloatBuffer colorFloatBuffer;

        IjkMediaPlayer ijkMediaPlayer = new IjkMediaPlayer();
        SurfaceTexture surfaceTexture;

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            try {

                int vertex = GLES30.glCreateShader(GLES30.GL_VERTEX_SHADER);
                int fragment = GLES30.glCreateShader(GLES30.GL_FRAGMENT_SHADER);

                GLES30.glShaderSource(vertex, vertex_source);
                GLES30.glCompileShader(vertex);
                GLES30.glShaderSource(fragment, fragment_source);
                GLES30.glCompileShader(fragment);

                program = GLES30.glCreateProgram();
                GLES30.glAttachShader(program, vertex);
                GLES30.glAttachShader(program, fragment);
                GLES30.glLinkProgram(program);

                GLES30.glDeleteShader(vertex);
                GLES30.glDeleteShader(fragment);

                floatBuffer = ByteBuffer.allocateDirect(vertex_position.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
                floatBuffer.put(vertex_position);
                floatBuffer.position(0);

                colorFloatBuffer = ByteBuffer.allocateDirect(fragment_color.length * Float.BYTES).order(ByteOrder.nativeOrder()).asFloatBuffer();
                colorFloatBuffer.put(fragment_color);
                colorFloatBuffer.position(0);

                GLES30.glEnableVertexAttribArray(0);
                GLES30.glEnableVertexAttribArray(1);

            } catch (Exception e) {

            }

            int[] texture = new int[1];

/*            GLES30.glGenTextures(1, texture, 0);

            surfaceTexture = new SurfaceTexture(texture[0]);*/

//            ijkMediaPlayer.setSurface(new Surface(surfaceTexture));
/*            try {
                ijkMediaPlayer.setDataSource("http://v5-web.douyinvod.com/145b6c0454c75a68f15a55904a5bd311/641ae2c4/video/tos/cn/tos-cn-ve-15c001-alinc2/o4ZBGbxLMAB7njeogjFDAeCEjBleQS8HHKdzNn/?a=6383&ch=5&cr=3&dr=0&lr=all&cd=0%7C0%7C0%7C3&cv=1&br=1595&bt=1595&cs=0&ds=4&ft=7r3GT6WwwZRcZseFo3PDS6kFgAX1tG953Iq9eFF2XxJr12nz&mime_type=video_mp4&qs=0&rc=ZWk7ODxnaDU1OWU1PGU6ZkBpajg5cjU6ZmlyaTMzNGkzM0BeLl81XjRiXzExLjRiYmEwYSMuYHJwcjRnazJgLS1kLS9zcw%3D%3D&l=2023032218103298140DC8C2DD9E04D53D&btag=28000");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }*/
/*            ijkMediaPlayer.prepareAsync();
            ijkMediaPlayer.setOnPreparedListener(new IMediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(IMediaPlayer iMediaPlayer) {
                    ijkMediaPlayer.start();
                }
            });*/

        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            GLES30.glViewport(0, 0, width, height);
            GLES30.glUseProgram(program);
//            GLES30.glClearColor(0f, 0f, 1f, 1f);
        }

        @Override
        public void onDrawFrame(GL10 gl) {

            GLES30.glVertexAttribPointer(0, 4, GLES30.GL_FLOAT, false, 0, floatBuffer);

            GLES30.glVertexAttribPointer(1, 4, GLES30.GL_FLOAT, false, 0, colorFloatBuffer);

            GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, 3);

            GLES30.glFlush();

        }

    }

}
