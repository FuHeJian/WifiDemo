package com.example.wifidemo1.network.NetCallBack;

import android.annotation.SuppressLint;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;

import androidx.annotation.NonNull;

import java.lang.ref.Cleaner;
import java.util.Collections;
import java.util.List;

import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class NetWorkCallbackAsync extends ConnectivityManager.NetworkCallback {

    AvailableNetworkListener mListener;

    private List<Integer> mCapabilities = Collections.emptyList();

    private Match mMatch;

    private Scheduler.Worker mWorker = Schedulers.io().createWorker();

    public NetWorkCallbackAsync(@NonNull AvailableNetworkListener listener, @NonNull Match match) {
        mListener = listener;
/*        if (capabilities != null && capabilities.size() > 0) {
            mCapabilities = capabilities;
        }*/
        mMatch = match;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onAvailable(@NonNull Network network) {

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                boolean matched = true;

                matched = matched && mMatch.match(network);

                if (mListener.mNetworkIsEmpty && matched) {
                    mListener.onSuccess(network);
                    mListener.mNetworkIsEmpty = false;
                    mListener.mNetWorkIsMatched = true;
                } else {
                    if (!matched) {
                        mListener.onNotMatchNeedNetWork(network);
                    } else {
                        mListener.onUpdate(network);
                    }
                }
            }
        };

        //异步执行
        Disposable schedule = mWorker.schedule(runnable);

        //销毁runnable的执行
        //schedule.dispose();

    }

    /**
     * 用于ConnectivityManager.NetworkCallback中onAvailable的NetWork状态监听
     */
    static public abstract class AvailableNetworkListener {

        boolean mNetworkIsEmpty = true;

        boolean mNetWorkIsMatched;

        /**
         * 获取到满足的网络，只会调用一次
         *
         * @param network 首次满足条件的NetWork
         */
        abstract public void onSuccess(Network network);

        /**
         * 网络更新，匹配到了更好的网络，会多次调用
         *
         * @param network 更新后的NetWork
         */
        public void onUpdate(Network network){

        };

        /**
         * 找到的网络与要求的不匹配
         *
         * @param network 不匹配的网络，但是目前最好的
         */
        public void onNotMatchNeedNetWork(Network network){

        };

    }

    public interface Match {
        public boolean match(Network network);
    }

}
