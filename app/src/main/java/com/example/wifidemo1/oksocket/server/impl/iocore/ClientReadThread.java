package com.example.wifidemo1.oksocket.server.impl.iocore;

import com.example.wifidemo1.oksocket.common.interfaces.basic.AbsLoopThread;
import com.example.wifidemo1.oksocket.core.iocore.interfaces.IReader;
import com.example.wifidemo1.oksocket.core.iocore.interfaces.IStateSender;
import com.example.wifidemo1.oksocket.core.utils.SLog;
import com.example.wifidemo1.oksocket.server.action.IAction;
import com.example.wifidemo1.oksocket.server.exceptions.InitiativeDisconnectException;

import java.io.IOException;

/**
 * Created by xuhao on 2017/5/17.
 */

public class ClientReadThread extends AbsLoopThread {
    private IStateSender mClientStateSender;

    private IReader mReader;

    public ClientReadThread(IReader reader, IStateSender clientStateSender) {
        super("server_client_read_thread");
        this.mClientStateSender = clientStateSender;
        this.mReader = reader;
    }

    @Override
    protected void beforeLoop() {
        mClientStateSender.sendBroadcast(IAction.Client.ACTION_READ_THREAD_START);
    }

    @Override
    protected void runInLoopThread() throws IOException {
        mReader.read();
    }

    @Override
    public synchronized void shutdown(Exception e) {
        mReader.close();
        super.shutdown(e);
    }

    @Override
    protected void loopFinish(Exception e) {
        e = e instanceof InitiativeDisconnectException ? null : e;
        if (e != null) {
            SLog.e("duplex read error,thread is dead with exception:" + e.getMessage());
        }
        mClientStateSender.sendBroadcast(IAction.Client.ACTION_READ_THREAD_SHUTDOWN, e);
    }
}
