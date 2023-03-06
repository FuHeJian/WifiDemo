package com.example.wifidemo1.oksocket.core.iocore;

import com.example.wifidemo1.oksocket.core.exceptions.WriteException;
import com.example.wifidemo1.oksocket.core.iocore.interfaces.IIOCoreOptions;
import com.example.wifidemo1.oksocket.core.iocore.interfaces.IOAction;
import com.example.wifidemo1.oksocket.core.iocore.interfaces.IPulseSendable;
import com.example.wifidemo1.oksocket.core.iocore.interfaces.ISendable;
import com.example.wifidemo1.oksocket.core.iocore.interfaces.IStateSender;
import com.example.wifidemo1.oksocket.core.iocore.interfaces.IWriter;
import com.example.wifidemo1.oksocket.core.utils.SLog;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by xuhao on 2017/5/31.
 */

public class WriterImpl implements IWriter<IIOCoreOptions> {

    private volatile IIOCoreOptions mOkOptions;

    private IStateSender mStateSender;

    private OutputStream mOutputStream;

    private LinkedBlockingQueue<ISendable> mQueue = new LinkedBlockingQueue<>();

    private boolean isClose;

    private byte[] writeBytes;

    @Override
    public void initialize(OutputStream outputStream, IStateSender stateSender) {
        mStateSender = stateSender;
        mOutputStream = outputStream;
        isClose = false;
    }

    @Override
    public boolean write() throws RuntimeException {
        if (isClose) {
            SLog.e("WriterImpl write() isClose = true");
            return false;
        }
        ISendable sendable = null;
        try {
            sendable = mQueue.take();
        } catch (InterruptedException e) {
            //ignore;
            e.printStackTrace();
            throw new WriteException(e);
        }
        if (sendable != null) {
            try {
                writeBytes = sendable.parse();
                mOutputStream.write(writeBytes);
                mOutputStream.flush();
//                int packageSize = mOkOptions.getWritePackageBytes();
//                int remainingCount = sendBytes.length;
//                ByteBuffer writeBuf = ByteBuffer.allocate(packageSize);
//                writeBuf.order(mOkOptions.getWriteByteOrder());
//                int index = 0;
//                while (remainingCount > 0) {
//                    int realWriteLength = Math.min(packageSize, remainingCount);
//                    writeBuf.clear();
//                    writeBuf.rewind();
//                    writeBuf.put(sendBytes, index, realWriteLength);
//                    writeBuf.flip();
//                    byte[] writeArr = new byte[realWriteLength];
//                    writeBuf.get(writeArr);
//                    mOutputStream.write(writeArr);
//                    mOutputStream.flush();
//
//                    if (SLog.isDebug()) {
//                        byte[] forLogBytes = Arrays.copyOfRange(sendBytes, index, index + realWriteLength);
//                        SLog.i("write bytes: " + BytesUtils.toHexStringForLog(forLogBytes));
//                        SLog.i("bytes write length:" + realWriteLength);
//                    }
//
//                    index += realWriteLength;
//                    remainingCount -= realWriteLength;
//                }
                if (sendable instanceof IPulseSendable) {
                    mStateSender.sendBroadcast(IOAction.ACTION_PULSE_REQUEST, sendable);
                } else {
                    mStateSender.sendBroadcast(IOAction.ACTION_WRITE_COMPLETE, sendable);
                }
            } catch (Exception e) {
                throw new WriteException(e);
            }
            return true;
        }
        return false;
    }

    @Override
    public void setOption(IIOCoreOptions option) {
        mOkOptions = option;
    }

    @Override
    public void offer(ISendable sendable) {
        mQueue.offer(sendable);
    }

    @Override
    public void close() {
        if (mOutputStream != null) {
            try {
                mOutputStream.close();
            } catch (IOException e) {
                //ignore
            }
        }
        isClose = true;
    }


}
