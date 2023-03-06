package com.example.wifidemo1.oksocket.server.impl;


import com.example.wifidemo1.oksocket.common.interfaces.common_interfacies.dispatcher.IRegister;
import com.example.wifidemo1.oksocket.common.interfaces.common_interfacies.server.IServerActionListener;
import com.example.wifidemo1.oksocket.common.interfaces.common_interfacies.server.IServerManager;
import com.example.wifidemo1.oksocket.core.iocore.interfaces.IStateSender;
import com.example.wifidemo1.oksocket.server.action.ServerActionDispatcher;

import java.io.Serializable;

public class AbsServerRegisterProxy implements IRegister<IServerActionListener, IServerManager>, IStateSender {

    protected ServerActionDispatcher mServerActionDispatcher;

    private IServerManager<OkServerOptions> mManager;

    protected void init(IServerManager<OkServerOptions> serverManager) {
        mManager = serverManager;
        mServerActionDispatcher = new ServerActionDispatcher(mManager);
    }

    @Override
    public IServerManager<OkServerOptions> registerReceiver(IServerActionListener socketActionListener) {
        return mServerActionDispatcher.registerReceiver(socketActionListener);
    }

    @Override
    public IServerManager<OkServerOptions> unRegisterReceiver(IServerActionListener socketActionListener) {
        return mServerActionDispatcher.unRegisterReceiver(socketActionListener);
    }

    @Override
    public void sendBroadcast(String action, Serializable serializable) {
        mServerActionDispatcher.sendBroadcast(action, serializable);
    }

    @Override
    public void sendBroadcast(String action) {
        mServerActionDispatcher.sendBroadcast(action);
    }
}
