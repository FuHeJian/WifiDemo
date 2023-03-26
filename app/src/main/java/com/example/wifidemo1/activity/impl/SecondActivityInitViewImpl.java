package com.example.wifidemo1.activity.impl;

import androidx.lifecycle.LifecycleOwner;

import com.example.wifidemo1.activity.i.InitView;
import com.example.wifidemo1.databinding.SecondActivityBinding;
import com.example.wifidemo1.log.MyLog;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkServerCmd;
import tv.danmaku.ijk.media.player.IjkTimedText;

/**
 * @author: fuhejian
 * @date: 2023/3/17
 */
public class SecondActivityInitViewImpl implements InitView<SecondActivityBinding> {

    @Override
    public void initView(SecondActivityBinding binding, LifecycleOwner lifecycleOwner) {

/*        //加载so库
        IjkMediaPlayer.loadLibrariesOnce(null);
        IjkMediaPlayer.native_profileBegin("libijkplayer.so");
        //打开日志
        IjkMediaPlayer.native_setLogLevel(IjkMediaPlayer.IJK_LOG_DEBUG);


        MyGlSurfaceView myGlSurfaceView = new MyGlSurfaceView(binding.getRoot().getContext());
        myGlSurfaceView.setLayoutParams(new ConstraintLayout.LayoutParams(300, 300));

        //初始化IjkMediaPlayer
        IjkMediaPlayer ijkMediaPlayer = new IjkMediaPlayer();
        ijkMediaPlayer.setOnAllListener(this);
        //设置数据源
//        ijkMediaPlayer.setDataSource();
        SurfaceHolder surfaceHolder = myGlSurfaceView.getHolder();
        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder holder) {

            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
                AssetFileDescriptor sfd;
                try {
                    sfd = binding.getRoot().getContext().getAssets().openFd("test.mp4");

                    FileDescriptor fd = sfd.getFileDescriptor();

                    ijkMediaPlayer.setDataSource(fd);
                    MyLog.printLog("当前类:SecondActivityInitViewImpl,当前方法：surfaceChanged,当前线程:"+ Thread.currentThread().getName()+",信息:" + fd);
                    //设置图像缓冲区
                    ijkMediaPlayer.setDisplay(surfaceHolder);
                    //保存tag,方便之后获取
                    myGlSurfaceView.setTag(ijkMediaPlayer);

                    ijkMediaPlayer.prepareAsync();
                    MyLog.printLog("当前类:SecondActivityInitViewImpl,当前方法：surfaceChanged,当前线程:" + Thread.currentThread().getName() + ",信息:准备完成");

                } catch (IOException e) {
                    MyLog.printLog("当前类:SecondActivityInitViewImpl,当前方法：surfaceChanged,当前线程:" + Thread.currentThread().getName() + ",信息:错误" + e.getMessage());
                }
            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder holder) {

            }
        });

        IntBuffer intBuffer = IntBuffer.allocate(binding.flow.getReferencedIds().length + 1);
        intBuffer.put(binding.flow.getReferencedIds());
        int id = View.generateViewId();
        intBuffer.put(id);
        myGlSurfaceView.setId(id);
        ((ViewGroup) binding.getRoot()).addView(myGlSurfaceView);
        binding.flow.setReferencedIds(intBuffer.array());*/

        //TODO TEST START


        //TODO TEST END


    }
}
