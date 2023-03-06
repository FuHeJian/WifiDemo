package com.example.wifidemo1.socket.sendable;


import com.example.wifidemo1.oksocket.core.iocore.interfaces.ISendable;

/**
 * @author: fuhejian
 * @date: 2023/3/2
 */
public class OkSocketSendData implements ISendable {

    @Override
    public byte[] parse() {
        return new byte[0];
    }

}
