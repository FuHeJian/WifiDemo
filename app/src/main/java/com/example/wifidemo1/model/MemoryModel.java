package com.example.wifidemo1.model;

import java.io.Serializable;

public class MemoryModel implements Serializable {


    public static final int SDCardNotMounted = 0;
    public static final int SDCardMounted = 1;
    public static final int SDCardErroNeedFormat = 2;
    public static final int SDCardPrepareComplete = 3;
    public static final int SDCardFull = 4;
    public static final int SDCardPopup = 5;
    public static final int SDCardNotExit = 6;


    public int status;
    public long totalspace;
    public long freespace;
    public long usespace;

    @Override
    public String toString() {
        return
                "status='" + status + '\'' +
                        ", totalspace='" + totalspace + '\'' +
                        ", freespace='" + freespace + '\'' +
                        ", usespace='" + usespace + '\''
                ;
    }


}
