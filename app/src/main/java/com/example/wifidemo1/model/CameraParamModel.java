package com.example.wifidemo1.model;

import com.litesuits.orm.db.annotation.Table;

@Table("CameraParamModel")
public class CameraParamModel extends BaseModel {
    //public boolean selectBulb;
    public String cameraName;
    public CameraParamerIndex shutterTimeParamIndex;
    public CameraParamerIndex focusParamIndex;
    public CameraParamerIndex evParamIndex;
    public CameraParamerIndex isoParamIndex;
    public CameraParamerIndex wbParamIndex;

//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        ProPanoParamModel that = (ProPanoParamModel) o;
//        return overlapRatioPosition == that.overlapRatioPosition && useSetingFocus == that.useSetingFocus && Float.compare(that.setingFocusValue, setingFocusValue) == 0 && focusPosition == that.focusPosition && isVertical == that.isVertical && sensorSizePosition == that.sensorSizePosition && Objects.equals(startPosition, that.startPosition) && Objects.equals(endPosition, that.endPosition) && Objects.equals(picRowCountAngleStr, that.picRowCountAngleStr) && Objects.equals(horVerTotalAngleStr, that.horVerTotalAngleStr) && Objects.equals(senorSizeStr, that.senorSizeStr) && Objects.equals(focalStr, that.focalStr) && Objects.equals(overlapRatioStr, that.overlapRatioStr);
//    }
}
