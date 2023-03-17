package com.example.wifidemo1.singleton;

import com.example.wifidemo1.model.CameraInfoModel;
import com.example.wifidemo1.model.CameraInfoModel;

public class ParameterDataLoader {
    private static ParameterDataLoader mHelper = null;


    private CameraInfoModel sCameraInfoModel;
    private CameraInfoModel fCameraInfoModel;
    private CameraInfoModel evCameraInfoModel;
    private CameraInfoModel isoCameraInfoModel;
    private CameraInfoModel wbCameraInfoModel;


    private ParameterDataLoader() {

    }

    public static ParameterDataLoader getInstance() {
        if (mHelper == null) {
            synchronized (ParameterDataLoader.class) {
                if (mHelper == null) {
                    mHelper = new ParameterDataLoader();
                }
            }
        }
        return mHelper;
    }


    public CameraInfoModel getsCameraInfoModel() {
        return sCameraInfoModel;
    }

    public void resetInfo(){
        sCameraInfoModel=null;
        fCameraInfoModel=null;
        evCameraInfoModel=null;
        isoCameraInfoModel=null;
        wbCameraInfoModel=null;
    }

    public void setsCameraInfoModel(CameraInfoModel sCameraInfoModel) {
        this.sCameraInfoModel = sCameraInfoModel;
    }

    public CameraInfoModel getfCameraInfoModel() {
        return fCameraInfoModel;
    }

    public void setfCameraInfoModel(CameraInfoModel fCameraInfoModel) {
        this.fCameraInfoModel = fCameraInfoModel;
    }


    public CameraInfoModel getEvCameraInfoModel() {
        return evCameraInfoModel;
    }

    public void setEvCameraInfoModel(CameraInfoModel evCameraInfoModel) {
        this.evCameraInfoModel = evCameraInfoModel;
    }

    public CameraInfoModel getIsoCameraInfoModel() {
        return isoCameraInfoModel;
    }

    public void setIsoCameraInfoModel(CameraInfoModel isoCameraInfoModel) {
        this.isoCameraInfoModel = isoCameraInfoModel;
    }

    public CameraInfoModel getWbCameraInfoModel() {
        return wbCameraInfoModel;
    }

    public void setWbCameraInfoModel(CameraInfoModel wbCameraInfoModel) {
        this.wbCameraInfoModel = wbCameraInfoModel;
    }
}
