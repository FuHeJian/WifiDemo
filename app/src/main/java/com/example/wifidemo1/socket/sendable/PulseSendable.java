package com.example.wifidemo1.socket.sendable;

import com.example.wifidemo1.oksocket.core.iocore.interfaces.IPulseSendable;
import com.example.wifidemo1.socket.RequestCommands;


import java.nio.charset.StandardCharsets;

import okio.ByteString;

/**
 * @author: fuhejian
 * @date: 2023/3/2
 */
public class PulseSendable implements IPulseSendable {
    @Override
    public byte[] parse() {
        return RequestCommands.Pulse;
    }
}
