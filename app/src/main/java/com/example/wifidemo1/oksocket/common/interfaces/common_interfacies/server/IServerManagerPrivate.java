package com.example.wifidemo1.oksocket.common.interfaces.common_interfacies.server;


import com.example.wifidemo1.oksocket.core.iocore.interfaces.IIOCoreOptions;


public interface IServerManagerPrivate<E extends IIOCoreOptions> extends IServerManager<E> {
    void initServerPrivate(int serverPort);
}
