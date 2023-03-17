package com.example.wifidemo1.model.precompile;

import com.litesuits.orm.db.annotation.Table;
import com.example.wifidemo1.model.BaseModel;

@Table("PhotoModel")
public class PhotoModel extends BaseModel {
    public int time;
    public int interval;
    public boolean isEditing;

    public PhotoModel(int time, int invalter) {
        this.time = time;
        this.interval = invalter;

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass())
            return false;
        PhotoModel that = (PhotoModel) o;
        return time == that.time;
    }

    @Override
    public String toString() {
        return "PhotoModel{" +
                "time=" + time +
                ", invalter=" + interval +
                '}';
    }
}
