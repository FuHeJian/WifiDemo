package com.example.wifidemo1.socket.sendable;


import com.example.wifidemo1.oksocket.core.iocore.interfaces.ISendable;

import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;

import kotlin.jvm.internal.Intrinsics;


/**
 * @author: fuhejian
 * @date: 2023/3/2
 */
public class OrderDataSendable implements ISendable {

    private final String mMsg;

    public OrderDataSendable(@NotNull String msg) {
        mMsg = msg;
    }

    @NotNull
    @Override
    public byte[] parse() {
        return mMsg.getBytes(StandardCharsets.UTF_8);
    }

}
