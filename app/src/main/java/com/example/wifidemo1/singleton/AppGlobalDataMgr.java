package com.example.wifidemo1.singleton;

import com.example.wifidemo1.ApplicationConstants;
import com.example.wifidemo1.oksocket.units.LatLong;
import com.example.wifidemo1.utils.OrderCommunication;

public class AppGlobalDataMgr {
    private static AppGlobalDataMgr INSTANCE;
    private AppGlobalDataMgr() {
    }

    public static AppGlobalDataMgr getInstance() {
        if (INSTANCE == null) {
            synchronized (AppGlobalDataMgr.class) {
                if (INSTANCE == null) {
                    INSTANCE = new AppGlobalDataMgr();
                }
            }
        }
        return INSTANCE;
    }

    //是否为测试模式
    public boolean isTestMode = false;
    public boolean isCameraSDCardUnknowHintNoLongerShow = false;

    public LatLong manualLocate;
    public boolean isManualSettingGpsInfo = false;
    public boolean isGpsUnitDegree = false;
    public boolean isDynamicLapsePreviewing = false;
    public String cameraType;//相机型号
    public String cameraManufacturer;
    //public String appPromoteUrl = null;
    public boolean autoLevelEnable = false;
    public boolean isSupportHdmi = false;
    public boolean isHdmiOpened = false;

    public boolean isHdmiValid(){
        return (OrderCommunication.getInstance().controlMode == ApplicationConstants.CONTROL_MODE_CABLE_RELEASE) && isHdmiOpened;
    }
}
