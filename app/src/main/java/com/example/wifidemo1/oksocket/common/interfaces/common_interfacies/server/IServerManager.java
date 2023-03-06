package com.example.wifidemo1.oksocket.common.interfaces.common_interfacies.server;

import com.example.wifidemo1.oksocket.core.iocore.interfaces.IIOCoreOptions;

public interface IServerManager<E extends IIOCoreOptions> extends IServerShutdown {

    void listen();

    void listen(E options);

    boolean isLive();

    IClientPool<String, IClient> getClientPool();
}
