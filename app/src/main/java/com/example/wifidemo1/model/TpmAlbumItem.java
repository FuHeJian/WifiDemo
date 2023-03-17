package com.example.wifidemo1.model;

import com.litesuits.orm.db.annotation.Table;

import java.util.Objects;


/**
 * Created by aq on 2018/7/7.
 */

@Table("TpmAlbumItem")
public class TpmAlbumItem extends BaseModel {

    private int filePathID;
    private long lastModifiedTime;//文件修改时间
    private int fileTotalSize;
    private int FilecurentSize;
    private boolean isSelect;

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public int getFilePathID() {
        return filePathID;
    }

    public void setFilePathID(int filePathID) {
        this.filePathID = filePathID;
    }


    public long getLastModifiedTime() {
        return lastModifiedTime;
    }

    public void setLastModifiedTime(long lastModifiedTime) {
        this.lastModifiedTime = lastModifiedTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TpmAlbumItem item = (TpmAlbumItem) o;
        return filePathID == item.filePathID ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(filePathID, lastModifiedTime, fileTotalSize, FilecurentSize, isSelect);
    }

    @Override
    public String toString() {
        return "TpmAlbumItem{" +
                "filePathID=" + filePathID +
                ", lastModifiedTime=" + lastModifiedTime +
                ", fileTotalSize=" + fileTotalSize +
                ", FilecurentSize=" + FilecurentSize +
                ", isSelect=" + isSelect +
                '}';
    }

    public int getFileTotalSize() {
        return fileTotalSize;
    }

    public void setFileTotalSize(int fileTotalSize) {
        this.fileTotalSize = fileTotalSize;
    }

    public int getFilecurentSize() {
        return FilecurentSize;
    }

    public void setFilecurentSize(int filecurentSize) {
        FilecurentSize = filecurentSize;
    }


}
