package com.example.wifidemo1.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

import com.example.wifidemo1.log.MyLog;

import java.io.IOException;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * @author: fuhejian
 * @date: 2023/4/3
 */
public class MySurfaceView extends SurfaceView implements SurfaceHolder.Callback2 {

    private IMediaPlayer mIjkMediaPlayer;

    public MySurfaceView(Context context) {
        this(context,null);
    }

    public MySurfaceView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public MySurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr,0);

    }

    public MySurfaceView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.getHolder().addCallback(this);

    }

    @Override
    public void surfaceRedrawNeeded(@NonNull SurfaceHolder holder) {

    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        IjkMediaPlayer.loadLibrariesOnce(null);
        mIjkMediaPlayer = new IjkMediaPlayer();
        IjkMediaPlayer.native_setLogLevel(IjkMediaPlayer.IJK_LOG_DEBUG);
        ((IjkMediaPlayer)mIjkMediaPlayer).setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 1);
        ((IjkMediaPlayer)mIjkMediaPlayer).setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "start-on-prepared", 1);
        MyLog.printLog("当前类:MySurfaceView,当前方法：surfaceCreated,当前线程:"+ Thread.currentThread().getName()+",信息:");
        try {

            mIjkMediaPlayer.setDataSource("http://v26-web.douyinvod.com/9bc1da753b72cf58198a5ac6b33f5c5f/642ac71d/video/tos/cn/tos-cn-ve-15c001-alinc2/oUnw3NQsIhdSv2DA3DyAnzwBEUZhDDkNgAnfce/?a=6383&ch=26&cr=3&dr=0&lr=all&cd=0%7C0%7C0%7C3&cv=1&br=4099&bt=4099&cs=0&ds=4&ft=bvTKJbQQqUYqfJEZao0OW_EklpPiXkHIzMVJERXpwrbPD-I&mime_type=video_mp4&qs=0&rc=Ozs7Nmk5aDs6Njs1OWVkNEBpM3c7cmc6ZnJpajMzNGkzM0A0Ml4yLjMzX2AxNGNeNi8vYSNqM15ncjQwa2dgLS1kLTBzcw%3D%3D&l=20230403193118DD1B97F459BD1305F3B1&btag=8000&testst=1680521484498");

        } catch (IOException e) {

        }

        mIjkMediaPlayer.setDisplay(holder);
        mIjkMediaPlayer.prepareAsync();

    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {

    }



}
