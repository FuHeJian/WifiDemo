package com.example.wifidemo1.model.precompile;

import com.litesuits.orm.db.annotation.Table;
import com.example.wifidemo1.model.BaseModel;
import com.example.wifidemo1.model.CameraParamerIndex;

@Table("CameraParameter")
public class CameraParameter extends BaseModel {
    public int time;
    public boolean isLineModel;
    public boolean isEditing;
    public int exposureTime;
    public boolean isBubl;

    public CameraParamerIndex sPosition;
    public CameraParamerIndex fPosition;
    public CameraParamerIndex evPosition;
    public CameraParamerIndex isoPosition;
    public CameraParamerIndex wbPosition;


    public CameraParameter(boolean isBubl, int exposureTime, CameraParamerIndex sPosition, CameraParamerIndex fPosition, CameraParamerIndex evPosition, CameraParamerIndex isoPosition, CameraParamerIndex wbPosition) {
        this.isBubl = isBubl;
        this.exposureTime = exposureTime;
        this.sPosition = sPosition;
        this.fPosition = fPosition;
        this.evPosition = evPosition;
        this.isoPosition = isoPosition;
        this.wbPosition = wbPosition;
    }


    public CameraParameter() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CameraParameter that = (CameraParameter) o;
        return time == that.time;
    }


    @Override
    public String toString() {
        return "CameraParameter{" +
                "isLineModel=" + isLineModel +
                "sPosition=" + sPosition +
                ", fPosition=" + fPosition +
                ", evPosition=" + evPosition +
                ", isoPosition=" + isoPosition +
                ", wbPosition=" + wbPosition +
                '}';
    }
}
