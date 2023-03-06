package com.example.wifidemo1.oksocket.common.interfaces.common_interfacies.server;


import com.example.wifidemo1.oksocket.core.iocore.interfaces.ISendable;

public interface IClientPool<T, K> {

    void cache(T t);

    T findByUniqueTag(K key);

    int size();

    void sendToAll(ISendable sendable);
}
