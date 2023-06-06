package com.example.wifidemo1;

import android.app.Application;
import android.content.Context;

import com.example.wifidemo1.activity.base.BaseActivity;
import com.example.wifidemo1.wifi.WifiUtilHelper;

import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author: fuhejian
 * @date: 2023/3/11
 */
public class App extends Application {

    public static class GlobalManager {
        public static GlobalManager INSTANCE;

        static {
            INSTANCE = new GlobalManager();
        }

        public Application app;

        public WeakHashMap<BaseActivity, String> activitys = new WeakHashMap();

        /**
         * 如果不存在name的activity,则返回任何一个context
         * @param name
         * @return
         */
        public Context getContext(String name) {
            AtomicReference<BaseActivity> needActivity = new AtomicReference<>();
            if (activitys.containsValue(name)) {
               activitys.forEach((k, v) -> {
                   if(v.equals(name)&&k!=null){
                       needActivity.set(k);
                   }
               });
            }
            return needActivity.get()!=null?needActivity.get():getAnyContext();
        }

        private Context getAnyContext(){
            if(activitys.size()!=0){
                for (BaseActivity baseActivity : activitys.keySet()) {
                    if(baseActivity == null){
                        activitys.remove(null);
                    }else {
                        return baseActivity;
                    }
                }
                return (Context) activitys.keySet().toArray()[0];
            }else {
                return app;
            }
        }

    }

    @Override
    public void onCreate() {
        super.onCreate();
        GlobalManager.INSTANCE.app = this;
        WifiUtilHelper.INSTANCE.updateNetWorkForNet(this);

    }

}
