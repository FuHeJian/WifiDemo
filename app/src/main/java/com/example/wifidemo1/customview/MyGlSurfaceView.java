package com.example.wifidemo1.customview;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.SurfaceHolder;

import com.example.wifidemo1.opengl.OpenGLUtils;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import tv.danmaku.ijk.media.example.widget.media.TextureRenderView;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * @author: fuhejian
 * @date: 2023/3/22
 */
public class MyGlSurfaceView extends GLSurfaceView {

    private IMediaPlayer mIjkMediaPlayer;

    public MyGlSurfaceView(Context context) {
        this(context, null);
    }

    public MyGlSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.setEGLContextClientVersion(3);
        IjkMediaPlayer.loadLibrariesOnce(null);
        mIjkMediaPlayer = new IjkMediaPlayer();
        ((IjkMediaPlayer)mIjkMediaPlayer).setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 1);
        ((IjkMediaPlayer)mIjkMediaPlayer).setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "start-on-prepared", 1);
        try {

            mIjkMediaPlayer.setDataSource("http://v26-web.douyinvod.com/d8b11a4e080f9f03fda5932bdcf6f71b/642ab65a/video/tos/cn/tos-cn-ve-15c001-alinc2/oILAcBypRgevh7nAfYAIlIUzaglCBJgBtcNgFM/?a=6383&ch=26&cr=3&dr=0&lr=all&cd=0%7C0%7C0%7C3&cv=1&br=3938&bt=3938&cs=0&ds=4&ft=bvTKJbQQqUYqfJEZao0OW_EklpPiXc7VzMVJERXpwrbPD-I&mime_type=video_mp4&qs=0&rc=ZDVmZjRnaGU3NWY4Nzg3NUBpamlsdGY6ZndqajMzNGkzM0BjLmI1NWA2NmExLzY2MTIwYSNqLl9ncjRnLjJgLS1kLS9zcw%3D%3D&l=20230403181718154A83FD1B38FA098EEB&btag=28000&testst=1680517043865");

        } catch (IOException e) {

        }

        setRenderer(new MyRender(this,mIjkMediaPlayer));

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        super.surfaceCreated(holder);
        mIjkMediaPlayer.setDisplay(holder);
        mIjkMediaPlayer.prepareAsync();
    }

    private static class MyRender implements Renderer {
        //      "#version 300 es\n" +
        private static final String vertex_source =
                "#version 300 es\n" +
                        "layout(location = 0) in vec4 vPosition;\n" +
                        "layout(location = 1) in vec4 vColor;\n" +
                        "out vec4 color;\n" +
                        "void main(){\n" +
                        "    gl_Position = vPosition;\n" +
                        "    color = vColor;\n" +
                        "}";

        private static final String fragment_source =
                "#version 300 es\n" +
                        "precision mediump float;\n" +
                        "in vec4 color;\n" +
                        "\n" +
                        "out vec4 fragColor;\n" +
                        "\n" +
                        "void main(){\n" +
                        "    fragColor = color;\n" +
                        "}";
        int program;

        private static final float[] vertex_position = {
                0.0f, 0.5f, 0.0f, 1f,     // top
                -0.5f, -0.5f, 0.0f, 1f, // bottom left
                0.5f, -0.5f, 0.0f, 1f
        };

        private static final float[] fragment_color = {
                1f, 0f, 0f, 1.0f
        };

        private WeakReference<GLSurfaceView> mSurfaceView;

        private WeakReference<TextureRenderView>  mTextureRenderView;
        private WeakReference<IMediaPlayer> mIjkMediaPlayer;

        public MyRender(GLSurfaceView surfaceView,IMediaPlayer ijkMediaPlayer) {
            mSurfaceView = new WeakReference<>(surfaceView);
            mIjkMediaPlayer = new WeakReference<>(ijkMediaPlayer);
        }

        FloatBuffer floatBuffer;

        FloatBuffer colorFloatBuffer;
        private SurfaceTexture mSurfaceTexture;

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {

            int[] texture = new int[1];
            OpenGLUtils.glGenTextures(texture);

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
                GLES30.glUseProgram(program);


                floatBuffer = ByteBuffer.allocateDirect(vertex_position.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
                floatBuffer.put(vertex_position);
                floatBuffer.position(0);

                colorFloatBuffer = ByteBuffer.allocateDirect(fragment_color.length * Float.BYTES).order(ByteOrder.nativeOrder()).asFloatBuffer();
                colorFloatBuffer.put(fragment_color);
                colorFloatBuffer.position(0);

            } catch (Exception e) {

            }

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
        }

        @Override
        public void onDrawFrame(GL10 gl) {
  /*          GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT);
            GLES30.glVertexAttribPointer(0, 4, GLES30.GL_FLOAT, false, 0, floatBuffer);
            GLES30.glVertexAttribPointer(1, 4, GLES30.GL_FLOAT, false, 0, colorFloatBuffer);
            GLES30.glEnableVertexAttribArray(0);
            GLES30.glEnableVertexAttribArray(1);
            GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, 3);
            GLES30.glDisableVertexAttribArray(0);
            GLES30.glDisableVertexAttribArray(1);

            mSurfaceTexture.updateTexImage();*/

        }

    }

}
