package com.example.wifidemo1.oksocket.client.impl.client;

import com.example.wifidemo1.oksocket.client.impl.exceptions.DogDeadException;
import com.example.wifidemo1.oksocket.client.sdk.client.OkSocketOptions;
import com.example.wifidemo1.oksocket.client.sdk.client.bean.IPulse;
import com.example.wifidemo1.oksocket.client.sdk.client.connection.IConnectionManager;
import com.example.wifidemo1.oksocket.common.interfaces.basic.AbsLoopThread;
import com.example.wifidemo1.oksocket.core.iocore.interfaces.IPulseSendable;
import com.example.wifidemo1.oksocket.core.utils.SLog;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by xuhao on 2017/5/18.
 */

public class PulseManager implements IPulse {
    /**
     * 数据包发送器
     */
    private volatile IConnectionManager mManager;
    /**
     * 心跳数据包
     */
    private IPulseSendable mSendable;
    /**
     * 连接参数
     */
    private volatile OkSocketOptions mOkOptions;
    /**
     * 当前频率
     */
    private volatile long mCurrentFrequency;
    /**
     * 当前的线程模式
     */
    private volatile OkSocketOptions.IOThreadMode mCurrentThreadMode;
    /**
     * 是否死掉
     */
    private volatile boolean isDead = false;
    /**
     * 是否暂停
     */
    private volatile boolean isPause = false;
    /**
     * 允许遗漏的次数
     */
    private volatile AtomicInteger mLoseTimes = new AtomicInteger(-1);

    private PulseThread mPulseThread = new PulseThread();

    PulseManager(IConnectionManager manager, OkSocketOptions okOptions) {
        mManager = manager;
        mOkOptions = okOptions;
        mCurrentThreadMode = mOkOptions.getIOThreadMode();
    }

    public synchronized IPulse setPulseSendable(IPulseSendable sendable) {
        if (sendable != null) {
            mSendable = sendable;
        }
        return this;
    }

    public IPulseSendable getPulseSendable() {
        return mSendable;
    }

    @Override
    public synchronized void pulse() {
        privateDead();
        updateFrequency();
        if (mCurrentThreadMode != OkSocketOptions.IOThreadMode.SIMPLEX) {
            if (mPulseThread.isShutdown()) {
                mPulseThread.start();
                isPause = false;
            }
        }
    }

    @Override
    public synchronized void trigger() {
        if (isDead) {
            return;
        }
        if (mCurrentThreadMode != OkSocketOptions.IOThreadMode.SIMPLEX && mManager != null && mSendable != null) {
            mManager.send(mSendable);
        }
    }

    public synchronized void dead() {
        mLoseTimes.set(0);
        isDead = true;
        privateDead();
    }

    public synchronized void pause() {
        isPause = true;
    }

    public synchronized void resume() {
        isPause = false;
    }

    private synchronized void updateFrequency() {
        if (mCurrentThreadMode != OkSocketOptions.IOThreadMode.SIMPLEX) {
            mCurrentFrequency = mOkOptions.getPulseFrequency();
            mCurrentFrequency = mCurrentFrequency < 1000 ? 1000 : mCurrentFrequency;//间隔最小为一秒
        } else {
            privateDead();
        }
    }

    @Override
    public synchronized void feed() {
        SLog.i("feed(): mLoseTimes.get() = " + mLoseTimes.get());
        mLoseTimes.set(-1);
        SLog.i("feed(): mLoseTimes.get() = " + mLoseTimes.get()
                + " , mOkOptions.getPulseFeedLoseTimes() = " + mOkOptions.getPulseFeedLoseTimes()
                + " , mLoseTimes = " + mLoseTimes.hashCode());
    }

    private void privateDead() {
        if (mPulseThread != null) {
            mPulseThread.shutdown();
        }
    }

    public int getLoseTimes() {
        return mLoseTimes.get();
    }

    protected synchronized void setOkOptions(OkSocketOptions okOptions) {
        mOkOptions = okOptions;
        mCurrentThreadMode = mOkOptions.getIOThreadMode();
        updateFrequency();
    }

    private class PulseThread extends AbsLoopThread {

        @Override
        protected void runInLoopThread() throws Exception {
            if (isDead) {
                shutdown();
                return;
            } else if (isPause) {
                Thread.sleep(mCurrentFrequency);
                return;
            }

            if (mManager != null && mSendable != null) {
                SLog.i("runInLoopThread: mLoseTimes.get() = " + mLoseTimes.get()
                        + " , mOkOptions.getPulseFeedLoseTimes() = " + mOkOptions.getPulseFeedLoseTimes()
                        + " , mLoseTimes = " + mLoseTimes.hashCode());
                if (mOkOptions.getPulseFeedLoseTimes() != -1 && mLoseTimes.incrementAndGet() >= mOkOptions.getPulseFeedLoseTimes()) {
                    mManager.disconnect(new DogDeadException("you need feed dog on time,otherwise he will die"));
                } else {
                    mManager.send(mSendable);
                }
            }

            //not safety sleep.
            Thread.sleep(mCurrentFrequency);
        }

        @Override
        protected void loopFinish(Exception e) {
        }
    }


}
