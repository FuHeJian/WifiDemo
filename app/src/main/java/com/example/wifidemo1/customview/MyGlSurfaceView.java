package com.example.wifidemo1.customview;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.Surface;

import com.example.wifidemo1.opengl.OpenGLUtils;

import java.io.IOException;
import java.lang.ref.WeakReference;
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
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.setEGLContextClientVersion(3);
        IjkMediaPlayer.loadLibrariesOnce(null);
        setRenderer(new MyRender(this));
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

        public MyRender(GLSurfaceView surfaceView) {
            mSurfaceView = new WeakReference<>(surfaceView);
        }

        FloatBuffer floatBuffer;

        FloatBuffer colorFloatBuffer;
        IjkMediaPlayer mIjkMediaPlayer;
        private SurfaceTexture mSurfaceTexture;

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {

            mIjkMediaPlayer = createPlayer();
            int[] texture = new int[1];
            OpenGLUtils.glGenTextures(texture);
            mSurfaceTexture = new SurfaceTexture(texture[0]);
            mIjkMediaPlayer.setSurface(new Surface(mSurfaceTexture));

            try {
                mIjkMediaPlayer.setDataSource("http://v26-web.douyinvod.com/974127525cae883ff062580e6ed8bb2b/6426b2ba/video/tos/cn/tos-cn-ve-15c001-alinc2/oUaNJH6nbACUhAn5BBfz8BoA6e7BYegcmEDQBy/?a=6383&ch=26&cr=3&dr=0&lr=all&cd=0%7C0%7C0%7C3&cv=1&br=1823&bt=1823&cs=0&ds=4&ft=bvTKJbQQqUYqfJEZao0OW_EklpPiXHxV7MVJEjDpwrbPD-I&mime_type=video_mp4&qs=0&rc=NTc4ODU7aDw2aDk8aGk0NUBpamdvOzk6ZmhyaTMzNGkzM0BiMWEwNTBfX2AxMDVjMC0xYSNgMzBkcjRfcnBgLS1kLS9zcw%3D%3D&l=202303311711545909F92CF29DBD001F96&btag=28000&testst=1680253919748");
                mIjkMediaPlayer.prepareAsync();
            } catch (IOException e) {
                mIjkMediaPlayer.release();
            }

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
            GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT);
            GLES30.glVertexAttribPointer(0, 4, GLES30.GL_FLOAT, false, 0, floatBuffer);
            GLES30.glVertexAttribPointer(1, 4, GLES30.GL_FLOAT, false, 0, colorFloatBuffer);
            GLES30.glEnableVertexAttribArray(0);
            GLES30.glEnableVertexAttribArray(1);
            GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, 3);
            GLES30.glDisableVertexAttribArray(0);
            GLES30.glDisableVertexAttribArray(1);

            mSurfaceTexture.updateTexImage();

        }

        public IjkMediaPlayer createPlayer() {

            if (mIjkMediaPlayer != null) return mIjkMediaPlayer;

            IjkMediaPlayer ijkMediaPlayer = null;
            ijkMediaPlayer = new IjkMediaPlayer();

            ijkMediaPlayer.native_setLogLevel(IjkMediaPlayer.IJK_LOG_INFO);
            //是否开启 Mediacodec 硬解，1 为开启，0 为关闭
            ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 1);
            //
            ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-hevc", 0);
            // 设置视频显示格式
            ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "overlay-format", IjkMediaPlayer.SDL_FCC_RV32);

            ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec_ndk", 0);//use ndk mediacodec
            //0 : audio; 1 : video; 2 : external clock
            ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "av_sync_index", 2);

//            ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "open_retry", openMJpegDraw ? 0 : 1);

            ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "av_sync_video_no_limit", 0);

            ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec_release_output", 0);

            ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "analyzeduration", 1);
            ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "analyzemaxduration", 100L);
            ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "flush_packets", 1L);
            ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "packet-buffering", 0L);
            ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "framedrop", 1L);
            ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "dns_cache_clear", 1);
//          ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "rtsp_transport", "tcp");
            mIjkMediaPlayer = ijkMediaPlayer;
            return mIjkMediaPlayer;
        }

    }

}
