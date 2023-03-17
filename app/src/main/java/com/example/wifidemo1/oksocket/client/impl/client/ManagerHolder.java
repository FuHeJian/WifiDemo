package com.example.wifidemo1.oksocket.client.impl.client;

import com.example.wifidemo1.log.MyLog;
import com.example.wifidemo1.oksocket.client.impl.client.abilities.IConnectionSwitchListener;
import com.example.wifidemo1.oksocket.client.sdk.client.ConnectionInfo;
import com.example.wifidemo1.oksocket.client.sdk.client.OkSocketOptions;
import com.example.wifidemo1.oksocket.client.sdk.client.connection.IConnectionManager;
import com.example.wifidemo1.oksocket.common.interfaces.common_interfacies.server.IServerManager;
import com.example.wifidemo1.oksocket.common.interfaces.common_interfacies.server.IServerManagerPrivate;
import com.example.wifidemo1.oksocket.common.interfaces.utils.SPIUtils;
import com.example.wifidemo1.oksocket.core.utils.SLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by xuhao on 2017/5/16.
 */
public class ManagerHolder {

    private volatile Map<ConnectionInfo, IConnectionManager> mConnectionManagerMap = new HashMap<>();

    private volatile Map<Integer, IServerManagerPrivate> mServerManagerMap = new HashMap<>();

    private static class InstanceHolder {
        private static final ManagerHolder INSTANCE = new ManagerHolder();
    }

    public static ManagerHolder getInstance() {
        return InstanceHolder.INSTANCE;
    }

    private ManagerHolder() {
        mConnectionManagerMap.clear();
    }

    public IServerManager getServer(int localPort) {
        IServerManagerPrivate manager = mServerManagerMap.get(localPort);
        if (manager == null) {
            manager = (IServerManagerPrivate) SPIUtils.load(IServerManager.class);
            if (manager == null) {
                String err = "Oksocket.Server() load error. Server plug-in are required!" +
                        " For details link to https://github.com/xuuhaoo/OkSocket";
                SLog.e(err);
                throw new IllegalStateException(err);
            } else {
                synchronized (mServerManagerMap) {
                    mServerManagerMap.put(localPort, manager);
                }
                manager.initServerPrivate(localPort);
                return manager;
            }
        }
        return manager;
    }

    public  void removeCache(ConnectionInfo info)
    {
        if(info!=null)
        {
            mConnectionManagerMap.remove(info);
        }
    }

    public IConnectionManager getConnection(ConnectionInfo info) {
        IConnectionManager manager = mConnectionManagerMap.get(info);
        if (manager == null) {
            MyLog.printLog("当前类:ManagerHolder,当前方法：getConnection,当前线程:"+ Thread.currentThread().getName()+",信息:缓存中不存在该socket，重新创建");
            return getConnection(info, OkSocketOptions.getDefault());
        } else {
            return getConnection(info, manager.getOption());
        }
    }

    public boolean hasCache(ConnectionInfo info){
        IConnectionManager manager = mConnectionManagerMap.get(info);
        return  manager!=null;
    }
    /**
     * 对源码稍作修改，使其可以马上连接其他局域网重名地址的Socket
     * @param info
     * @param okOptions
     * @return
     */
    public IConnectionManager getConnection(ConnectionInfo info, OkSocketOptions okOptions) {
        IConnectionManager manager = mConnectionManagerMap.get(info);
        if (manager != null) {
            if (!okOptions.isConnectionHolden()) {
                synchronized (mConnectionManagerMap) {
                    mConnectionManagerMap.remove(info);
                }
                return createNewManagerAndCache(info, okOptions);
            } else {
                manager.option(okOptions);
            }
            return manager;
        } else {
            return createNewManagerAndCache(info, okOptions);
        }
    }

    /**
     * 修改源码，重载
     * @param info
     * @param cache
     * @return
     */
    public IConnectionManager getConnection(ConnectionInfo info,boolean cache) {
        IConnectionManager manager = mConnectionManagerMap.get(info);
        if (manager == null) {
            return getConnection(info, OkSocketOptions.getDefault(),cache);
        } else {
            return getConnection(info, manager.getOption(),cache);
        }
    }

    /**
     * 修改源码，重载
     * @param info
     * @param okOptions
     * @param cache
     * @return
     */
    public IConnectionManager getConnection(ConnectionInfo info, OkSocketOptions okOptions,boolean cache) {
        if(cache){
            IConnectionManager manager = mConnectionManagerMap.get(info);
            if (manager != null) {
                if (!okOptions.isConnectionHolden()) {
                    synchronized (mConnectionManagerMap) {
                        mConnectionManagerMap.remove(info);
                    }
                    return createNewManagerAndCache(info, okOptions);
                } else {
                    manager.option(okOptions);
                }
                return manager;
            } else {
                return createNewManagerAndCache(info, okOptions);
            }
        }
        else {
            IConnectionManager manager = mConnectionManagerMap.get(info);
            if (manager != null) {
                manager.disconnect();
                mConnectionManagerMap.remove(info);
            }
            return createNewManagerAndCache(info, okOptions);
        }
    }
    private IConnectionManager createNewManagerAndCache(ConnectionInfo info, OkSocketOptions okOptions) {
        AbsConnectionManager manager = new ConnectionManagerImpl(info);
        manager.option(okOptions);
        manager.setOnConnectionSwitchListener(new IConnectionSwitchListener() {
            @Override
            public void onSwitchConnectionInfo(IConnectionManager manager, ConnectionInfo oldInfo,
                                               ConnectionInfo newInfo) {
                synchronized (mConnectionManagerMap) {
                    mConnectionManagerMap.remove(oldInfo);
                    mConnectionManagerMap.put(newInfo, manager);
                }
            }
        });
        synchronized (mConnectionManagerMap) {
            mConnectionManagerMap.put(info, manager);
        }
        return manager;
    }

    protected List<IConnectionManager> getList() {
        List<IConnectionManager> list = new ArrayList<>();

        Map<ConnectionInfo, IConnectionManager> map = new HashMap<>(mConnectionManagerMap);
        Iterator<ConnectionInfo> it = map.keySet().iterator();
        while (it.hasNext()) {
            ConnectionInfo info = it.next();
            IConnectionManager manager = map.get(info);
            if (!manager.getOption().isConnectionHolden()) {
                it.remove();
                continue;
            }
            list.add(manager);
        }
        return list;
    }


}
