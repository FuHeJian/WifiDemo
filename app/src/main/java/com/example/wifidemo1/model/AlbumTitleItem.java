package com.example.wifidemo1.model;

import com.litesuits.orm.db.annotation.Table;

@Table("AlbumTitleItem")
public class AlbumTitleItem extends BaseModel{
    private String text;
    private int yuntaiFileType;

    public AlbumTitleItem(String text, int yuntaiFileType) {
        this.text = text;
        this.yuntaiFileType = yuntaiFileType;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getYuntaiFileType() {
        return yuntaiFileType;
    }

    public void setYuntaiFileType(int yuntaiFileType) {
        this.yuntaiFileType = yuntaiFileType;
    }

    @Override
    public String toString() {
        return "AlbumTitleItem{" +
                "text='" + text + '\'' +
                ", yuntaiFileType=" + yuntaiFileType +
                '}';
    }

}
