package com.example.wifidemo1.oksocket.core.iocore;

import com.example.wifidemo1.oksocket.core.iocore.interfaces.ISendable;

import java.io.UnsupportedEncodingException;

/**
 * Created by LQF on 2020/1/9.
 */

public class MessageSendData implements ISendable {
    private String content = "";
    private byte[] body;

    public MessageSendData(String content) {
        this.content = content;
    }

    @Override
    public byte[] parse() {
        body = new byte[0];
        try {
            body = content.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return body;
    }
}
