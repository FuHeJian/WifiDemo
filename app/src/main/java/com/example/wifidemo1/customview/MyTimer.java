package com.example.wifidemo1.customview;

import com.example.wifidemo1.adapter.BaseDataBindingAdapter;
import com.example.wifidemo1.log.MyLog;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * @author: fuhejian
 * @date: 2023/3/10
 */
public class MyTimer {
    public static MyTimer INSTANCE;
    /**
     * dispose后就不能再添加任务了，添加了也无法执行
     */
    private Scheduler.Worker mWorker = Schedulers.io().createWorker();
    static {
        INSTANCE = new MyTimer();
    }

    /**
     * 循环执行，间隔period
     * @param period
     * @param run
     */
    public void schedule(long period,Runnable run) {
        initWorker();
        mWorker.schedulePeriodically(run, 0, period, TimeUnit.MILLISECONDS);
    }

    public void singleSchedule(long initialTime,Runnable run){
        initWorker();
        mWorker.schedule(run,initialTime,TimeUnit.MILLISECONDS);
    }

    public void stopAll(){
        if(!mWorker.isDisposed())
        {
            mWorker.dispose();
        }
        mWorker=null;
    }

    public void  initWorker(){
        if(mWorker == null){
            mWorker = Schedulers.io().createWorker();
        }
    }

}
