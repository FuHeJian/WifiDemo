package com.example.wifidemo1.oksocket.core.iocore;

import com.example.wifidemo1.oksocket.core.iocore.interfaces.ISendable;

public class ByteSendData implements ISendable {
    private byte[] body;

    public ByteSendData(byte[] data) {
        this.body = data;
    }

    @Override
    public byte[] parse() {
        return body;
    }
}
