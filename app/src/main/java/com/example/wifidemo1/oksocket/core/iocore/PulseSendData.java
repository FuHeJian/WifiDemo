package com.example.wifidemo1.oksocket.core.iocore;

import com.example.wifidemo1.oksocket.core.iocore.interfaces.IPulseSendable;

import java.nio.charset.Charset;

/**
 * Created by LQF on 2020/1/9.
 */

public class PulseSendData implements IPulseSendable {
    private String str = "h" + "#";
    private byte[] body;

    @Override
    public byte[] parse() {
        body = str.getBytes(Charset.forName("utf-8"));
        return body;
    }
}
