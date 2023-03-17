package com.example.wifidemo1.utils;

import android.util.Log;

import com.example.wifidemo1.model.BatteryModel;
import com.example.wifidemo1.model.BroadcastActionEvent;
import com.example.wifidemo1.model.CameraInfoModel;
import com.example.wifidemo1.model.CameraParamerIndex;
import com.example.wifidemo1.model.ExtraDevVersionInfo;
import com.example.wifidemo1.model.FirmWareVersionInfo;
import com.example.wifidemo1.model.MemoryModel;
import com.example.wifidemo1.model.PolarisVersion;
import com.example.wifidemo1.model.RecordStateModel;
import com.example.wifidemo1.model.ResetAppPasswordModel;
import com.example.wifidemo1.model.ResponseModel;
import com.example.wifidemo1.model.SensorValuesModle;
import com.example.wifidemo1.model.StateRecoveryModel;
import com.example.wifidemo1.singleton.*;
import com.example.wifidemo1.socket.SocketUtil;
import com.example.wifidemo1.socket.sendable.OrderDataSendable;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;


/**
 * Created by HLW on 2019/6/26.
 */

public class OrderCommunication {
    private static final String TAG = "OrderCommunication";
    private static OrderCommunication mOrderCommunication = null;


    public int svFlag;

    private BatteryModel batteryModel;
    private MemoryModel memoryModel;

    public long lastGetCanmeraInfoTime;
    public String connectedDeviceName;
    public boolean touristsMode;
    public boolean wifi5G;
    public boolean isNoLongerPromptShootingHelp;
    public boolean isNoLongerPromptSettingPaAngel;
    public int controlMode; //0:usb模式,1:快门线模式
    public int exTime;

    public String socketIP;
    public int socketPORT;
    public String resourceAddress;
    public String streamAddress;

    private OrderCommunication() {

    }

    public static OrderCommunication getInstance() {
        if (mOrderCommunication == null) {
            synchronized (OrderCommunication.class) {
                if (mOrderCommunication == null) {
                    mOrderCommunication = new OrderCommunication();
                }
            }
        }
        return mOrderCommunication;
    }

    public void initConnectAddress(boolean is4G, String socketIP, int socketPORT, String resourceAddress, String streamAddress) {
        Log.d(TAG, "adsfdasfs initConnectAddress: is4G =" + is4G + ",socketIP =" + socketIP + ",socketPORT =" + socketPORT + ",resourceAddress =" + resourceAddress + ",streamAddress =" + streamAddress);
        if (is4G) {
            this.socketIP = socketIP;
            this.socketPORT = socketPORT;
            this.resourceAddress = resourceAddress + "/";
            this.streamAddress = streamAddress;
        } else {
            this.socketIP = "192.168.0.1";
            this.socketPORT = 9090;
            this.resourceAddress = "http://192.168.0.1/";
            this.streamAddress = "http://192.168.0.1:8080";
        }
    }

    public void setWifiConnectState(boolean connect) {
        Log.d(TAG, "setWifiConnectState: connect =" + connect);
        batteryModel = null;
        memoryModel = null;
        svFlag = 0;
        if (connect) {
            SP_GIMBAL_EX_AXIS_STA();
            SP_SET_SYSTEM_TIME();
            startBatteryRxTimer();
            startMemoryRxTimer();
            SP_PUSH_MODE_STATE();
            SP_GET_WIFI_BAND();
        } else {
            stopBatteryRxTimer();
            stopMemoryRxTimer();
        }
    }


    public void getCanmeraInfo(boolean needCheckTime) {
        Log.e(TAG, "getCanmeraInfo: needCheckTime =" + needCheckTime);
        if (needCheckTime) {
            if (System.currentTimeMillis() - lastGetCanmeraInfoTime < 2000) return;
        }
        lastGetCanmeraInfoTime = System.currentTimeMillis();
        SP_GET_ISO_INFO();
        SP_GET_WB_INFO();
        SP_GET_EV_INFO();
        SP_GET_SHUTTER_INFO();
        SP_GET_FNUM_INFO();
    }

    public void ijkPreviewStart(boolean needGetFileCount) {
        Log.d(TAG, "ijkPreviewStart: needGetFileCount =" + needGetFileCount);
//        if (needGetFileCount){
//            MediaFileLoad.getInstance().releaseData(true, true, null);
//            MediaFileLoad.getInstance().getYuntaifileCount();
//        }
    }


    private RxTimer batteryRxTimer;
    private RxTimer memoryRxTimer;

    public void startBatteryRxTimer() {
        stopBatteryRxTimer();
        SP_GET_BAT_STATE();
        batteryRxTimer = new RxTimer();
        batteryRxTimer.interval(2000, new RxTimer.RxAction() {
            @Override
            public void action(long number) {
                SP_GET_BAT_STATE();
                if (number > 10) stopBatteryRxTimer();
            }
        });
    }

    public void stopBatteryRxTimer() {
        if (batteryRxTimer != null) batteryRxTimer.cancel();
        batteryRxTimer = null;
    }

    public void startMemoryRxTimer() {
        stopMemoryRxTimer();
        SP_GET_SD_INFO();
        memoryRxTimer = new RxTimer();
        memoryRxTimer.interval(2000, new RxTimer.RxAction() {
            @Override
            public void action(long number) {
                SP_GET_SD_INFO();
                if (number > 10) stopMemoryRxTimer();
            }
        });
    }

    public void stopMemoryRxTimer() {
        if (memoryRxTimer != null) memoryRxTimer.cancel();
        memoryRxTimer = null;
    }

    public BatteryModel getBatteryModel() {
        return batteryModel;
    }

    public MemoryModel getMemoryModel() {
        if (memoryModel == null) memoryModel = new MemoryModel();
        return memoryModel;
    }


    //设置ISO
    public boolean SP_SET_ISO(String msg) {
        return sendOrder(CMD.SP_SET_ISO, 1, "iso:" + msg + ";");
    }

    //设置快门时间
    public boolean SP_SET_SHUTTER(String msg) {
        return sendOrder(CMD.SP_SET_SHUTTER, 1, "s:" + msg + ";");
    }

    //设置对焦值
    public boolean SP_SET_FOCUS(String mode, String msg) {
        return sendOrder(CMD.SP_SET_FOCUS, 1, "mod:" + mode + ";f:" + msg + ";");
    }

    //设置曝光补偿
    public boolean SP_SET_EV(String msg) {
        return sendOrder(CMD.SP_SET_EV, 1, "ev:" + msg + ";");
    }

    //设置白平衡
    public boolean SP_SET_WB(String msg) {
        return sendOrder(CMD.SP_SET_WB, 1, "wb:" + msg + ";");
    }


    //设置拍照录制状态
    //c:1 打开连拍模式，0：关闭连拍模式；-1：不使用连拍的其他状态
    public boolean SP_SET_PHOTO_RECORD_STATUS(String msg, String bulb, String c) {
        return sendOrder(CMD.SP_SET_PHOTO_RECORD_STATUS, 2, "state:" + msg + ";bulb:" + bulb + ";c:" + c + ";");
    }

    //设置拍照录制状态
    public boolean SP_SET_VIDEO_RECORD_STATUS(boolean start) {
        return sendOrder(CMD.SP_SET_VIDEO_RECORD_STATUS, 2, "state:" + (start ? "1" : "0") + ";");
    }


    public boolean SP_GET_ISO_INFO() {
        return sendOrder(CMD.SP_GET_ISO_INFO, 1, null);
    }

    public boolean SP_GET_WB_INFO() {
        return sendOrder(CMD.SP_GET_WB_INFO, 1, null);
    }

    public boolean SP_GET_EV_INFO() {
        return sendOrder(CMD.SP_GET_EV_INFO, 1, null);
    }

    public boolean SP_GET_SHUTTER_INFO() {
        return sendOrder(CMD.SP_GET_SHUTTER_INFO, 1, null);
    }

    public boolean SP_GET_FNUM_INFO() {
        return sendOrder(CMD.SP_GET_FNUM_INFO, 1, null);
    }

    public boolean SP_SET_FNUM(String msg) {
        return sendOrder(CMD.SP_SET_FNUM, 1, "fNum:" + msg + ";");
    }

    //step:1;interval:%d;startTime:%d-%d;endTime:%d-%d;
    public boolean SP_SUN_SHOT_START(String parameter) {
        return sendOrder(CMD.SP_SUN_SHOT, 2, parameter);
    }

    public boolean SP_SUN_SHOT_CANCLE() {
        return sendOrder(CMD.SP_SUN_SHOT, 2, "step:2;");
    }

    public boolean SP_SUN_SHOT_END() {
        return sendOrder(CMD.SP_SUN_SHOT, 2, "step:3;");
    }

    public boolean SP_SUN_SHOT_COMPLETE() {
        return sendOrder(CMD.SP_SUN_SHOT, 2, "step:4;");
    }

    public boolean SP_SUN_SHOT_APPOINTMENT_END() {
        return sendOrder(CMD.SP_SUN_SHOT, 2, "step:5;");
    }


    public boolean SP_FOCUS_STACK_START(String msg, boolean compound) {
        if (compound) {
            msg = msg + "isp:" + 1 + ";";
        } else {
            msg = msg + "isp:" + 0 + ";";
        }
        msg = "step:2;" + msg;
        return sendOrder(CMD.SP_FOCUS_STACK, 2, msg);
    }


    public boolean SP_FOCUS_STACK_STOP() {
        return sendOrder(CMD.SP_FOCUS_STACK, 2, "step:3;");
    }

    public boolean SP_FOCUS_STACK_COMPLETION() {
        return sendOrder(CMD.SP_FOCUS_STACK, 2, "step:4;");
    }

    public boolean SP_FOCUS_STACK_CANCLE() {
        return sendOrder(CMD.SP_FOCUS_STACK, 2, "step:5;");
    }

    public boolean SP_FOCUS_STACK_START_PREVIEW(String msg) {
        return sendOrder(CMD.SP_FOCUS_STACK, 2, "step:7;" + msg);
    }

    public boolean SP_FOCUS_STACK_PREVIEW_NUM() {
        return sendOrder(CMD.SP_FOCUS_STACK, 2, "step:8;");
    }


    public boolean SP_FOCUS_STACK_PREVIEW_END() {
        return sendOrder(CMD.SP_FOCUS_STACK, 2, "step:9;");
    }

    public boolean SP_FOCUS_STACK_PREVIEW_CANCLE() {
        return sendOrder(CMD.SP_FOCUS_STACK, 2, "step:10;");
    }


    public boolean SP_PANORAMIC_START(String msg, int num, boolean compound, boolean removePeople, int bulb) {
        if (compound) {
            msg = msg + "isp:" + 1 + ";";
        } else {
            msg = msg + "isp:" + 0 + ";";
        }

        if (removePeople) {
            msg = msg + "bgsem:" + 15 + ";";
        } else {
            msg = msg + "bgsem:" + 0 + ";";
        }

        msg = msg + "num:" + num + ";" + "bulb:" + bulb + ";";

        return sendOrder(CMD.SP_PANORAMIC, 2, msg);
    }

    public boolean SP_PANORAMIC_START(String msg, int num, boolean compound, boolean removePeople, int bulb, boolean isPano720, boolean isTakeVer) {
        if (compound) {
            msg = msg + "isp:" + 1 + ";";
        } else {
            msg = msg + "isp:" + 0 + ";";
        }

        if (removePeople) {
            msg = msg + "bgsem:" + 15 + ";";
        } else {
            msg = msg + "bgsem:" + 0 + ";";
        }

        msg = msg + "num:" + num + ";" + "bulb:" + bulb + ";";

        if (isPano720) {
            msg = msg + "dir:" + (isTakeVer ? "1" : "0");
            msg = msg + ";";
        }

        return sendOrder(CMD.SP_PANORAMIC, 2, msg);
    }

    public boolean SP_PANORAMIC_COMPLETION_NUMBER() {
        return sendOrder(CMD.SP_PANORAMIC, 2, "step:3;");
    }

    public boolean SP_PANORAMIC_END() {
        return sendOrder(CMD.SP_PANORAMIC, 2, "step:4;");
    }


    public boolean SP_PANORAMIC_COMPLETION() {
        return sendOrder(CMD.SP_PANORAMIC, 2, "step:5;");
    }

    public boolean SP_PANORAMIC_CANCLE() {
        return sendOrder(CMD.SP_PANORAMIC, 2, "step:6;");
    }


//    public boolean SP_SKY_PANORAMIC_START(String msg) {
//        return sendOrder(CMD.SP_SKY_PANORAMIC, 2, msg);
//    }
//
//    public boolean SP_SKY_PANORAMIC_NUMBER() {
//        return sendOrder(CMD.SP_SKY_PANORAMIC, 2, "step:2;");
//    }
//
//    public boolean SP_SKY_PANORAMIC_COMPLETION() {
//        return sendOrder(CMD.SP_SKY_PANORAMIC, 2, "step:3;");
//    }
//
//    public boolean SP_SKY_PANORAMIC_CANCLE() {
//        return sendOrder(CMD.SP_SKY_PANORAMIC, 2, "step:4;");
//    }


    public boolean SP_PRECOMPILE_SHOT_START() {
        return sendOrder(CMD.SP_PLC, 2, "step:1;");
    }

    public boolean SP_PRECOMPILE_SHOT_SEND_PARAMETER(String parameter) {
        return sendOrder(CMD.SP_PLC, 2, parameter);
    }

    public boolean SP_PRECOMPILE_SHOT_END_POINT(String parameter) {
        return sendOrder(CMD.SP_PLC, 2, "step:3;" + parameter);
    }

    public boolean SP_PRECOMPILE_SHOT_SET_APPOINTMENT_TIME(String time) {
        return sendOrder(CMD.SP_PLC, 2, "step:4;time:" + time + ";");
    }

    public boolean SP_PRECOMPILE_SHOT_RUNTIME() {
        return sendOrder(CMD.SP_PLC, 2, "step:5;");
    }

    public boolean SP_PRECOMPILE_SHOT_CANCLE() {
        return sendOrder(CMD.SP_PLC, 2, "step:6;");
    }


    public boolean SP_HDR_START(String msg, boolean compound) {
        String msgs = "step:1;" + (compound ? "isp:1;" : "isp:0;") + msg;
        return sendOrder(CMD.SP_HDR, 2, msgs);
    }

    public boolean SP_HDR_END() {
        return sendOrder(CMD.SP_HDR, 2, "step:2;");
    }

    public boolean SP_HDR_COMPLETE() {
        return sendOrder(CMD.SP_HDR, 2, "step:3;");
    }

    public boolean SP_HDR_CANCLE() {
        return sendOrder(CMD.SP_HDR, 2, "step:4;");
    }


    public boolean SP_DELAY_SHOT_START() {
        return sendOrder(CMD.SP_DELAY_SHOT, 2, "step:1;");
    }

    //step:2;point:%d;gimbal:%0.3f, %0.3f,%0.3f;para:%d,%d; bulb:%d;
    public boolean SP_DELAY_SHOT_SEND_POINT(String msg) {
        return sendOrder(CMD.SP_DELAY_SHOT, 2, msg);
    }

    public boolean SP_DELAY_SHOT_SEND_END(int pointSize, int totalTime, int photoCnt, int preview) {
        return sendOrder(CMD.SP_DELAY_SHOT, 2, "step:3;point:" + pointSize + ";time:" + totalTime + ";photoCnt:" + photoCnt + ";" + ";preview:" + preview + ";");
    }

    public boolean SP_DELAY_SHOT_SHOOTING_NUM() {
        return sendOrder(CMD.SP_DELAY_SHOT, 2, "step:4;");
    }

    public boolean SP_REMOVE_PEOPLE_SHOT_START(int bulb, int num) {
        return sendOrder(CMD.SP_REMOVE_PEOPLE_SHOT, 2, "step:1;bulb:" + bulb + ";num:" + num + ";");
    }


    public boolean SP_REMOVE_PEOPLE_SHOT_NUM() {
        return sendOrder(CMD.SP_REMOVE_PEOPLE_SHOT, 2, "step:2;");
    }

    public boolean SP_REMOVE_PEOPLE_SHOT_END() {
        return sendOrder(CMD.SP_REMOVE_PEOPLE_SHOT, 2, "step:3;");
    }

    public boolean SP_REMOVE_PEOPLE_SHOT_COMPLITE() {
        return sendOrder(CMD.SP_REMOVE_PEOPLE_SHOT, 2, "step:4;");
    }

    public boolean SP_REMOVE_PEOPLE_SHOT_CANCLE() {
        return sendOrder(CMD.SP_REMOVE_PEOPLE_SHOT, 2, "step:5;");
    }


    public boolean SP_DELAY_SHOT_SHOOTING_COMPLETE() {
        return sendOrder(CMD.SP_DELAY_SHOT, 2, "step:5;");
    }

    public boolean SP_DELAY_SHOT_PROCESS_COMPLETE() {
        return sendOrder(CMD.SP_DELAY_SHOT, 2, "step:6;");
    }

    public boolean SP_DELAY_SHOT_CANCEL() {
        return sendOrder(CMD.SP_DELAY_SHOT, 2, "step:7;");
    }

    public boolean SP_DELAY_SHOT_END_BACK_SETTING(int state) {
        return sendOrder(CMD.SP_DELAY_SHOT, 2, "step:8;state:" + state + ";");
    }

    public boolean SP_DELAY_SHOT_GET_END_BACK_SETTING_STATE() {
        return sendOrder(CMD.SP_DELAY_SHOT, 2, "step:9" + ";");
    }


    public boolean SP_PUSH_MODE_STATE() {
        return sendOrder(CMD.SP_PUSH_MODE_STATE, 2, "");
    }

    public boolean SP_SET_MODE_STATE(int model) {
        return sendOrder(CMD.SP_SET_MODE_STATE, 2, "mode:" + model + ";");
    }


    public boolean SP_GIMBAL_HADJ_SPEED(String speed) {
        return sendOrder(CMD.SP_GIMBAL_HADJ_SPEED, 3, "speed:" + speed + ";");
    }

    public boolean SP_GIMBAL_HADJ_SPEED(int x, int y) {
        return sendOrder(CMD.SP_GIMBAL_HADJ_SPEED, 3, "x:" + x + ";y:" + y + ";");
    }

    public boolean SP_GIMBAL_VADJ_SPEED(int x, int y) {
        return sendOrder(CMD.SP_GIMBAL_VADJ_SPEED, 3, "x:" + x + ";y:" + y + ";");
    }

    public boolean SP_GIMBAL_ROLL_SPEED(String speed) {
        return sendOrder(CMD.SP_GIMBAL_HADJ_SPEED, 3, "speed:" + speed + ";");
    }


    public boolean SP_GIMBAL_VADJ_SPEED(String speed) {
        return sendOrder(CMD.SP_GIMBAL_VADJ_SPEED, 3, "speed:" + speed + ";");
    }

    public boolean SP_GIMBAL_RADJ_SPEED(String speed) {
        return sendOrder(CMD.SP_GIMBAL_RADJ_SPEED, 3, "speed:" + speed + ";");
    }


    public boolean SP_SET_TRACK_AU_STATE(int state, int speed) {
        Log.d(TAG, "SP_SET_TRACK_AU_STATE: state=" + state + " speed=" + speed);
        return sendOrder(CMD.SP_SET_TRACK_AU_STATE, 3, "state:" + state + ";speed:" + speed + ";");
    }


    public boolean SP_SET_GOTO_AU_STATE(int state, float yaw, float pitch, float lat, int track, int speed, float lng) {
        Log.d(TAG, "SP_SET_GOTO_AU_STATE: state=" + state + " speed=" + speed);
        return sendOrder(CMD.SP_SET_GOTO_AU_STATE, 3, "state:" + state + ";yaw:" + yaw + ";pitch:" + pitch + ";lat:" + lat + ";track:" + track + ";speed:" + speed + ";lng:" + lng + ";");
    }

    //设置指南针角度
    public boolean SP_SET_YAW(String msg, float lat, float lng) {
        return sendOrder(CMD.SP_SET_YAW, 3, "compass:" + msg + ";lat:" + lat + ";lng:" + lng + ";");
    }

    public boolean SP_CALIBRATE_START(int step, float yaw, float pitch, float lat, int num, float lng) {

        return sendOrder(CMD.SP_CALIBRATE_START, 3, "step:" + step + ";yaw:" + yaw + ";pitch:" + pitch + ";lat:" + lat + ";num:" + num + ";lng:" + lng + ";");
    }

    public boolean SP_GET_CELLULAR_STATE() {
        return sendOrder(CMD.SP_GET_CELLULAR_STATE, 2, null);
    }


    public boolean SP_SET_AHRS_STATE(int state) {
        return sendOrder(CMD.SP_SET_AHRS_STATE, 2, "state:" + state + ";");
    }

    public boolean SP_GIMBAL_HADJ_ANGLE(String angle) {
        return sendOrder(CMD.SP_GIMBAL_HADJ_ANGLE, 3, "angle:" + angle + ";");
    }

    public boolean SP_GIMBAL_HADJ_Speed(int x) {
        return sendOrder(CMD.SP_GIMBAL_HADJ_ANGLE, 3, "x:" + x + ";y=0;");
    }


    public boolean SP_GIMBAL_VADJ_ANGLE(String angle) {
        return sendOrder(CMD.SP_GIMBAL_VADJ_ANGLE, 3, "angle:" + angle + ";");
    }

    public boolean SP_GIMBAL_RADJ_ANGLE(String angle) {
        return sendOrder(CMD.SP_GIMBAL_RADJ_ANGLE, 3, "angle:" + angle + ";");
    }


    public boolean SP_SET_ROCKER_ADJUST(int type, int key, int state, int speedLevel) {
        return sendOrder(type, 3, "key:" + key + ";state:" + state + ";level:" + speedLevel + ";");
    }

    public boolean SP_GIMBAL_POS_RESET(int axis) {
        return sendOrder(CMD.SP_GIMBAL_POS_RESET, 3, "axis:" + axis + ";");
    }

    public boolean SP_GIMBAL_EX_AXIS_STA() {
        return sendOrder(CMD.SP_GIMBAL_EX_AXIS_STA, 3, null);
    }


    public boolean SP_GET_GIMBAL_POS() {
        return sendOrder(CMD.SP_GET_GIMBAL_POS, 3, null);
    }

    public boolean SP_SET_GIMBAL_POS(String yaw, String pitch, String roll) {
        return sendOrder(CMD.SP_SET_GIMBAL_POS, 3, "yaw:" + yaw + ";pitch:" + pitch + ";roll:" + roll + ";");
    }

    public boolean SP_GET_FILE_COUNT() {
        return sendOrder(CMD.SP_GET_FILE_COUNT, 2, null);
    }

    public boolean SP_GET_FILE_LIST(String msg) {
        return sendOrder(CMD.SP_GET_FILE_LIST, 2, msg);
    }

    public boolean SP_DEL_FILE(String msg) {
        return sendOrder(CMD.SP_DEL_FILE, 2, msg);
    }

    public boolean SP_GET_CLASS_FILE_COUNT(String msg) {
        return sendOrder(CMD.SP_GET_CLASS_FILE_COUNT, 2, msg);
    }

    public boolean SP_DEL_CLASS(String msg) {
        return sendOrder(CMD.SP_DEL_CLASS, 2, msg);
    }

    public boolean SP_APP_ADD_FILE(String type, String path, String appTime) {
        return sendOrder(CMD.SP_APP_ADD_FILE, 2, "type:" + type + ";path:" + path + ";appTime:" + appTime);
    }

    public boolean SP_GET_ISP_CFG_FILE(String msg) {
        return sendOrder(CMD.SP_GET_ISP_CFG_FILE, 2, msg);
    }


    public boolean SP_SD_FORMAT() {
        return sendOrder(CMD.SP_SD_FORMAT, 2, null);
    }

    public boolean SP_GET_SD_INFO() {
        return sendOrder(CMD.SP_GET_SD_INFO, 2, null);
    }

    public boolean SP_GET_BAT_STATE() {
        return sendOrder(CMD.SP_GET_BAT_STATE, 2, null);
    }

    public boolean SP_GET_DEVICE_VERSION() {
        return sendOrder(CMD.SP_GET_DEVICE_VERSION, 2, null);
    }

    public boolean SP_GET_SYSTEM_TIME() {
        return sendOrder(CMD.SP_GET_SYSTEM_TIME, 2, null);
    }

    public boolean SP_SET_SYSTEM_TIME() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date phoneData = new Date();
        String formatString = dateFormat.format(phoneData);
        int rawOffset = TimeZone.getDefault().getRawOffset() / 1000;

        String zone;
        if (rawOffset >= 0) zone = "+" + rawOffset;
        else zone = "" + rawOffset;

        String[] datas = formatString.split("\\s");
        String data = datas[0].trim();
        String time = datas[1].trim();
        String msg = "date:" + data + ";time:" + time + ";zone:" + zone;
        return sendOrder(CMD.SP_SET_SYSTEM_TIME, 2, msg);
    }

    public boolean SP_SET_UPGRADE_START() {
        return sendOrder(CMD.SP_SET_UPGRADE_START, 2, null);
    }

    public boolean SP_LOAD_UPGRADE_FW_STATE(int state) {
        return sendOrder(CMD.SP_LOAD_UPGRADE_FW_STATE, 2, "state:" + state + ";");
    }

    public boolean SP_PUSH_UPGRADE_STATUS(int state) {
        return sendOrder(CMD.SP_PUSH_UPGRADE_STATUS, 2, "state:" + state + ";");
    }

    public boolean SP_EXDEV_UPGRADE_START(int devId) {
        return sendOrder(CMD.SP_EXDEV_UPGRADE_START, 2, "devId:" + devId + ";");
    }

    public boolean SP_LOAD_EXDEV_FW_STATE(int devId, int state) {
        return sendOrder(CMD.SP_LOAD_EXDEV_FW_STATE, 2, "devId:" + devId + ";state:" + state + ";");
    }

    public boolean SP_PUSH_EXDEV_STATUS(String devId, int state) {
        return sendOrder(CMD.SP_PUSH_EXDEV_STATUS, 2, "devId:" + devId + ";state:" + state + ";");
    }


    public boolean SP_CAMERA_INFO() {
        return sendOrder(CMD.SP_CAMERA_INFO, 4, "");
    }

    public boolean SP_APP_PASSWORD_INFO_GET_PASSWORD() {
        return sendOrder(CMD.SP_APP_PASSWORD_INFO, 2, "step:1;");
    }


    public String sendPassword = null;
    public String sendSecurityQ = null;
    public String sendSecurityA = null;
    private boolean changePassword = true;

    public boolean SP_APP_PASSWORD_INFO_SET_PASSWORD(String password, String securityQ, String securityA, boolean isPassword) {
        sendPassword = password;
        sendSecurityQ = securityQ;
        sendSecurityA = securityA;
        changePassword = isPassword;
        return sendOrder(CMD.SP_APP_PASSWORD_INFO, 2, "step:2;password:" + UtilFunction.encryptPassword(password) + ";securityQ:" + securityQ + ";securityA:" + UtilFunction.encryptPassword(securityA) + ";");
    }


    public boolean SP_APP_PASSWORD_INFO_RESET() {
        return sendOrder(CMD.SP_APP_PASSWORD_INFO, 2, "step:5;");
    }


    public boolean SP_GET_LOG_LIST() {
        return sendOrder(CMD.SP_GET_LOG_LIST, 2, "step:5;");
    }

    public boolean SP_GET_WIFI_BAND() {
        return sendOrder(CMD.SP_GET_WIFI_BAND, 2, null);
    }

    public boolean SP_SET_WIFI_BAND(boolean wifi5G) {
        OrderCommunication.getInstance().wifi5G = wifi5G;
        String msg = wifi5G ? "band:1;" : "band:0;";
        return sendOrder(CMD.SP_SET_WIFI_BAND, 2, msg);
    }

    public boolean SP_GET_WARNING_TONE_STATE() {
        return sendOrder(CMD.SP_GET_WARNING_TONE_STATE, 2, null);
    }

    public boolean SP_SET_WARNING_TONE_STATE(String msg) {
        return sendOrder(CMD.SP_SET_WARNING_TONE_STATE, 2, msg);
    }

    public boolean SP_SET_TRACK_HALF_SPEED(boolean allSpeed) {
        return sendOrder(CMD.SP_SET_TRACK_HALF_SPEED, 3, (allSpeed ? "halfSpeed:0;" : "halfSpeed:1;"));
    }

    public boolean SP_SET_CAMERA_PREVIEW(boolean open) {
        return sendOrder(CMD.SP_SET_CAMERA_PREVIEW, 2, (open ? "state:1;" : "state:0;"));
    }

    public boolean SP_GET_CAMERA_PREVIEW() {
        return sendOrder(CMD.SP_GET_CAMERA_PREVIEW, 2, null);
    }


    public boolean SP_SOCKET_CLIENT_TYPE(boolean remoteModel) {
        return sendOrder(CMD.SP_SOCKET_CLIENT_TYPE, 2, (remoteModel ? "type:1;" : "type:0;"));
    }

    public boolean SP_TEST_CABLE_RELEASE(int ms1, int ms2) {
        return sendOrder(CMD.SP_TEST, 2, "step:1;A:" + ms1 + ";B:" + ms2 + ";");
    }

    public boolean SP_TEST_HDMI_1() {
        return sendOrder(CMD.SP_TEST, 2, "step:2;");
    }

    public boolean SP_TEST_4G(int state) {
        return sendOrder(CMD.SP_TEST, 2, "step:3;state:" + state + ";");
    }

    public boolean SP_TEST_4GAT(String str) {
        return sendOrder(CMD.SP_TEST, 2, "step:4;AT:" + str + ";");
    }

    public boolean SP_TEST_URAT_DEBUG(int state) {
        return sendOrder(CMD.SP_TEST, 2, "step:5;state:" + state + ";");
    }

    public boolean SP_TEST_SYSTEM_SLEEP() {
        return sendOrder(CMD.SP_TEST, 2, "step:6" + ";");
    }

    public boolean SP_TEST_4G_RESET() {
        return sendOrder(CMD.SP_TEST, 2, "step:7" + ";");
    }

    public boolean SP_TEST_BATERRY_WARN() {
        return sendOrder(CMD.SP_TEST, 2, "step:8" + ";");
    }

    //全景拍照转角
    public boolean SP_PANORAMA_ROTATE_ANGLE(float hAngle, float vAngle) {
        return sendOrder(CMD.SP_PANORAMIC, 2, "step:7;para:" + hAngle + "," + vAngle + ";");
    }

    //全景拍照开始选点，发当前起点
    public boolean SP_PANORAMA_START_POINT(String x, String y, String z) {
        return sendOrder(CMD.SP_PANORAMIC, 2, "step:8;gimbal:" + x + "," + y + "," + z + ";");
    }

    //全景拍照结束选点
    public boolean SP_PANORAMA_CANCEL_SELECT_POINT() {
        return sendOrder(CMD.SP_PANORAMIC, 2, "step:9" + ";");
    }

    //设置全景返回起点
    public boolean SP_PANORAMA_SET_BACK_START_POINT(int state) {
        return sendOrder(CMD.SP_PANORAMIC, 2, "step:10" + ";" + "state:" + state + ";");
    }

    //获取全景返回起点状态
    public boolean SP_PANORAMA_GET_BACK_START_POINT_STATE() {
        return sendOrder(CMD.SP_PANORAMIC, 2, "step:11" + ";");
    }

    //设置蜂窝网络apn
    public boolean SP_SET_CELLULAR_APN(int opt, String apn, String username, String passwd, int auth) {
        return sendOrder(CMD.SP_SET_CELLULAR_APN, 2, "opt:" + opt + ";apn:" + apn + ";username:" + username + ";passwd:" + passwd + ";auth:" + auth + ";");
    }

    //获取蜂窝网络imsi
    public boolean SP_GET_CELLULAR_IMSI() {
        return sendOrder(CMD.SP_GET_CELLULAR_IMSI, 2, null);
    }

    //获取水平状态
    public boolean SP_GET_TILT_STATE() {
        return sendOrder(CMD.SP_GET_TILT_STATE, 3, null);
    }

    //设置水平状态
    public boolean SP_SET_TILT_STATE(int state) {
        return sendOrder(CMD.SP_SET_TILT_STATE, 3, "state:" + state + ";");
    }

    //获取抖动开启状态
    public boolean SP_GET_DITHER_STATE() {
        return sendOrder(CMD.SP_GET_DITHER_STATE, 3, null);
    }

    //设置抖动状态
    public boolean SP_SET_DITHER_STATE(int state) {
        return sendOrder(CMD.SP_SET_DITHER_STATE, 3, "state:" + state + ";");
    }

    //获取北极星IMEI码
    public boolean SP_GET_CELLULAR_IMEI() {
        return sendOrder(CMD.SP_GET_CELLULAR_IMEI, 2, null);
    }

    //获取北极星硬件版本
    public boolean SP_GET_CELLULAR_HV() {
        return sendOrder(CMD.SP_GET_CELLULAR_HV, 2, null);
    }

    //设置北极星USB模式
    public boolean SP_SET_CELLULAR_COMUSB(int usbMode) {
        return sendOrder(CMD.SP_SET_CELLULAR_COMUSB, 2, "usbmode:" + usbMode + ";");
    }

    //获取图片格式
    public boolean SP_GET_IMG_FORMAT() {
        return sendOrder(CMD.SP_GET_IMG_FORMAT, 4, null);
    }

    //设置云台开放角度限位
    public boolean SP_SET_LIMIT_STATE(int state) {
        return sendOrder(CMD.SP_SET_LIMIT_STATE, 3, "state:" + state + ";");
    }

    //查询云台开放角度限位
    public boolean SP_GET_LIMIT_STATE() {
        return sendOrder(CMD.SP_GET_LIMIT_STATE, 3, null);
    }

    //获取稳定时间
    public boolean SP_GET_SETTLING_TIME() {
        return sendOrder(CMD.SP_GET_SETTLING_TIME, 3, null);
    }

    //设置稳定时间
    public boolean SP_SET_SETTLING_TIME(int time) {
        return sendOrder(CMD.SP_SET_SETTLING_TIME, 3, "time:" + time + ";");
    }

    //获取控制模式
    public boolean SP_GET_CONTROL_MODE() {
        return sendOrder(CMD.SP_GET_CONTROL_MODE, 2, null);
    }

    //设置控制模式 0:usb控制，1：快门线控制
    public boolean SP_SET_CONTROL_MODE(int mode) {
        return sendOrder(CMD.SP_SET_CONTROL_MODE, 2, "mode:" + mode + ";");
    }

    //获取曝光时间
    public boolean SP_GET_EX_TIME() {
        return sendOrder(CMD.SP_GET_EX_TIME, 2, null);
    }

    //设置曝光时间
    public boolean SP_SET_EX_TIME(int exTime) {
        return sendOrder(CMD.SP_SET_EX_TIME, 2, "ExTime:" + exTime + ";");
    }

    //设置拍摄方向
    public boolean SP_SET_CAMERA_DIR(int dir) {
        return sendOrder(CMD.SP_SET_CAMERA_DIR, 3, "dir:" + dir + ";");
    }

    //查询拍摄方向
    public boolean SP_GET_CAMERA_DIR() {
        return sendOrder(CMD.SP_GET_CAMERA_DIR, 3, null);
    }

    //获取自动关机状态， 0：关闭功能，1：10分钟后关闭，2：30分钟后关闭
    public boolean SP_GET_AUTO_OFF_SW() {
        return sendOrder(CMD.SP_GET_AUTO_OFF_SW, 2, null);
    }

    //设置自动关机状态， 0：关闭功能，1：10分钟后关闭，2：30分钟后关闭
    public boolean SP_SET_AUTO_OFF_SW(int state) {
        return sendOrder(CMD.SP_SET_AUTO_OFF_SW, 2, "sw:" + state + ";");
    }

    //获取自动水平功能开关状态， en 0：关，1：开
    public boolean SP_GET_AUTO_LEVEL_EN() {
        return sendOrder(CMD.SP_GET_AUTO_LEVEL_EN, 3, null);
    }

    //设置自动水平功能开关状态， en 0：关，1：开
    public boolean SP_SET_AUTO_LEVEL_EN(boolean enable) {
        int en = enable ? 1 : 0;
        return sendOrder(CMD.SP_SET_AUTO_LEVEL_EN, 3, "en:" + en + ";");
    }

    //设置自动水平,0=取消水平调整，1=开启水平调整
    public boolean SP_SET_AUTO_LEVEL_STATE(int state) {
        return sendOrder(CMD.SP_SET_AUTO_LEVEL_STATE, 3, "state:" + state + ";");
    }

    //获取是否支持hmdi
    public boolean SP_GET_HDMI_SUPPORT() {
        return sendOrder(CMD.SP_GET_HDMI_SUPPORT, 2, null);
    }

    //设置hdmi状态 state: 1=打开，0=关闭
    public boolean SP_SET_HDMI_STATE(int state) {
        return sendOrder(CMD.SP_SET_HDMI_STATE, 2, "state:" + state + ";");
    }

    //确认重启设备
    public boolean SP_REBOOT_CONFIRM_MODE(boolean reboot) {
        int confirm = reboot ? 1 : 0;
        return sendOrder(CMD.SP_REBOOT_CONFIRM_MODE, 2, "confirm:" + confirm + ";");
    }

    //获取hdmi状态 state: 1=打开，0=关闭
    public boolean SP_GET_HDMI_STATE() {
        return sendOrder(CMD.SP_GET_HDMI_STATE, 2, null);
    }

    //请求推送hdmi流状态 state: 1=有流，0=无流
    public boolean SP_PUSH_HDMI_STREAM_STATE() {
        return sendOrder(CMD.SP_PUSH_HDMI_STREAM_STATE, 2, null);
    }

    public synchronized boolean sendOrder(int code, int sptype, String msg) {
        return SocketUtil.INSTANCE.sendOrder(code, sptype, msg);
    }

    public synchronized void parseSocketCMD(int returncode, String returnmsg) {
        if (returncode != CMD.SP_PUSH_ROTATE_VECTOR && returncode != 525)
            Log.i(TAG, "指令 指令回复 ------: code =" + returncode + ",msg =" + returnmsg);
        if (returncode == -1 || returnmsg == null) {
            return;
        }


        returnmsg = returnmsg.trim();
        switch (returncode) {
            case CMD.SP_SET_FOCUS:
                parseSP_SET_FOCUS(returnmsg);
                break;
            case CMD.SP_SET_SHUTTER:
                parseSP_SET_SHUTTER(returnmsg);
                break;
            case CMD.SP_SET_FNUM:
                parseSP_SET_FNUM(returnmsg);
                break;
            case CMD.SP_SET_EV:
                parseSP_SET_EV(returnmsg);
                break;
            case CMD.SP_SET_ISO:
                parseSP_SET_ISO(returnmsg);
                break;
            case CMD.SP_SET_WB:
                parseSP_SET_WB(returnmsg);
                break;
            case CMD.SP_GET_SHUTTER_INFO:

                parseSP_GET_SHUTTER_INFO(returnmsg);
                break;
            case CMD.SP_GET_FNUM_INFO:

                parseSP_GET_FNUM_INFO(returnmsg);
                break;
            case CMD.SP_GET_EV_INFO:

                parseSP_GET_EV_INFO(returnmsg);
                break;
            case CMD.SP_GET_ISO_INFO:

                parseSP_GET_ISO_INFO(returnmsg);
                break;
            case CMD.SP_GET_WB_INFO:

                parseSP_GET_WB_INFO(returnmsg);
                break;
            case CMD.SP_SET_PHOTO_RECORD_STATUS:
                parseSP_SET_PHOTO_RECORD_STATUS(returnmsg);
                break;
            case CMD.SP_SET_VIDEO_RECORD_STATUS:
                parseSP_SET_VIDEO_RECORD_STATUS(returnmsg);
                break;
            case CMD.SP_FOCUS_STACK:
                parseSP_FOCUS_STACK(returnmsg);
                break;
            case CMD.SP_PANORAMIC:
                parseSP_PANORAMIC(returnmsg);
                break;
//            case CMD.SP_SKY_PANORAMIC:
//                parseSP_SKY_PANORAMIC(returnmsg);
//                break;
            case CMD.SP_DELAY_SHOT:
                parseSP_DELAY_SHOT(returnmsg);
                break;
            case CMD.SP_REMOVE_PEOPLE_SHOT:
                parseSP_REMOVE_PEOPLE_SHOT(returnmsg);
                break;
            case CMD.SP_SUN_SHOT:
                parseSP_SUN_SHOT(returnmsg);
                break;
            case CMD.SP_PLC:
                parseSP_PLC(returnmsg);
                break;
            case CMD.SP_PUSH_MODE_STATE:
                parseSP_PUSH_MODE_STATE(returnmsg);
                break;
            case CMD.SP_SET_MODE_STATE:
                parseSP_SET_MODE_STATE(returnmsg);
                break;
            case CMD.SP_HDR:
                parseSP_HDR(returnmsg);
                break;
            case CMD.SP_GET_GIMBAL_POS:
                parseSP_GET_GIMBAL_POS(returnmsg);
                break;
            case CMD.SP_SET_GIMBAL_POS:
                parseSP_SET_GIMBAL_POS(returnmsg);
                break;
            case CMD.SP_PUSH_ROTATE_VECTOR:
                parseSP_PUSH_ROTATE_VECTOR(returnmsg);
                break;
            case CMD.SP_CALIBRATE_START:
                parseSP_CALIBRATE_START(returnmsg);
                break;
            case CMD.SP_SET_YAW:
                parseSP_SET_YAW(returnmsg);
                break;
            case CMD.SP_SET_AHRS_STATE:
                parseSP_SET_AHRS_STATE(returnmsg);
                break;
            case CMD.SP_SET_GOTO_AU_STATE:
                parseSP_SET_GOTO_AU_STATE(returnmsg);
                break;
            case CMD.SP_SET_TRACK_AU_STATE:
                parseSP_SET_TRACK_AU_STATE(returnmsg);
                break;
            case CMD.SP_GET_FILE_COUNT:
                parseSP_GET_FILE_COUNT(returnmsg);
                break;
            case CMD.SP_GET_FILE_LIST:
                parseSP_GET_FILE_LIST(returnmsg);
                break;
            case CMD.SP_DEL_FILE:
                parseSP_DEL_FILE(returnmsg);
                break;
            case CMD.SP_ADD_FILE:
                parseSP_ADD_FILE(returnmsg);
                break;
            case CMD.SP_SD_FORMAT:
                parseSP_SD_FORMAT(returnmsg);
                break;
            case CMD.SP_GET_SD_INFO:
                parseSP_GET_SD_INFO(returnmsg);
                break;
            case CMD.SP_PUSH_SD_INFO:
                parseSP_PUSH_SD_INFO(returnmsg);
                break;
            case CMD.SP_PUSH_SD_HINT_ID:
                parseSP_PUSH_SD_HINT_ID(returnmsg);
                break;
            case CMD.SP_GET_BAT_STATE:
            case CMD.SP_PUSH_BAT_STATE:
                parseSP_GET_BAT_STATE(returnmsg);
                break;
            case CMD.SP_GET_DEVICE_VERSION:
                parseSP_GET_DEVICE_VERSION(returnmsg);
                break;
            case CMD.SP_SET_UPGRADE_START:
                parseSP_SET_UPGRADE_START(returnmsg);
                break;
            case CMD.SP_LOAD_UPGRADE_FW_STATE:
                parseSP_LOAD_UPGRADE_FW_STATE(returnmsg);
                break;
            case CMD.SP_PUSH_UPGRADE_STATUS:
                parseSP_PUSH_UPGRADE_STATUS(returnmsg);
                break;
            case CMD.SP_GIMBAL_EX_AXIS_STA:
                parseSP_GIMBAL_EX_AXIS_STA(returnmsg);
                break;
            case CMD.SP_GET_CLASS_FILE_COUNT:
                parseSP_GET_CLASS_FILE_COUNT(returnmsg);
                break;
            case CMD.SP_DEL_CLASS:
                parseSP_DEL_CLASS(returnmsg);
                break;
            case CMD.SP_APP_ADD_FILE:
                parseSP_APP_ADD_FILE(returnmsg);
                break;
            case CMD.SP_APP_PASSWORD_INFO:
                parseSP_APP_PASSWORD_INFO(returnmsg);
                break;
            case CMD.SP_CAMERA_INFO:
                parseSP_CAMERA_INFO(returnmsg);
                break;
            case CMD.SP_EXDEV_UPGRADE_START:
                parseSP_EXDEV_UPGRADE_START(returnmsg);
                break;
            case CMD.SP_LOAD_EXDEV_FW_STATE:
                parseSP_LOAD_EXDEV_FW_STATE(returnmsg);
                break;
            case CMD.SP_PUSH_EXDEV_STATUS:
                parseSP_PUSH_EXDEV_STATUS(returnmsg);
                break;
            case CMD.SP_GET_ISP_CFG_FILE:
                parse_SP_GET_ISP_CFG_FILE(returnmsg);
                break;
            case CMD.SP_GET_LOG_LIST:
                parse_SP_GET_LOG_LIST(returnmsg);
                break;
            case CMD.SP_ERROR_CODE:
                parse_SP_ERROR_CODE(returnmsg);
                break;
            case CMD.SP_GET_WIFI_BAND:
                parseSP_GET_WIFI_BAND(returnmsg);
                break;
            case CMD.SP_SET_WIFI_BAND:
                parseSP_SET_WIFI_BAND(returnmsg);
                break;
            case CMD.SP_GET_WARNING_TONE_STATE:
                parseSP_GET_WARNING_TONE_STATE(returnmsg);
                break;
            case CMD.SP_SET_WARNING_TONE_STATE:
                parseSP_SET_WARNING_TONE_STATE(returnmsg);
                break;
            case CMD.SP_GET_CELLULAR_STATE:
                parseSP_GET_CELLULAR_STATE(returnmsg);
                break;
            case CMD.SP_SET_TRACK_HALF_SPEED:
                parseSP_SET_TRACK_HALF_SPEED(returnmsg);
                break;
            case CMD.SP_SET_CAMERA_PREVIEW:
                parseSP_SET_CAMERA_PREVIEW(returnmsg);
                break;
            case CMD.SP_GET_CAMERA_PREVIEW:
                parseSP_GET_CAMERA_PREVIEW(returnmsg);
                break;
            case CMD.SP_SOCKET_CLIENT_TYPE:
                parseSP_SOCKET_CLIENT_TYPE(returnmsg);
                break;
            case CMD.SP_TEST:
                parseSP_TEST(returnmsg);
                break;
            case CMD.SP_SET_CELLULAR_APN:
                parseSP_SET_CELLULAR_APN(returnmsg);
                break;
            case CMD.SP_GET_CELLULAR_IMSI:
                parseSP_GET_CELLULAR_IMSI(returnmsg);
                break;
            case CMD.SP_GET_TILT_STATE:
                parseSP_GET_TILT_STATE(returnmsg);
                break;
            case CMD.SP_SET_TILT_STATE:
                parseSP_SET_TILT_STATE(returnmsg);
                break;
            case CMD.SP_GET_DITHER_STATE:
                parseSP_GET_DITHER_STATE(returnmsg);
                break;
            case CMD.SP_SET_DITHER_STATE:
                parseSP_SET_DITHER_STATE(returnmsg);
                break;
            case CMD.SP_GET_CELLULAR_IMEI:
                parseSP_GET_CELLULAR_IMEI(returnmsg);
                break;
            case CMD.SP_GET_CELLULAR_HV:
                parseSP_GET_CELLULAR_HV(returnmsg);
                break;
            case CMD.SP_SET_CELLULAR_COMUSB:
                parseSP_SET_CELLULAR_COMUSB(returnmsg);
                break;
            case CMD.SP_GET_IMG_FORMAT:
                parseSP_GET_IMG_FORMAT(returnmsg);
                break;
            case CMD.SP_SET_LIMIT_STATE:
                break;
            case CMD.SP_GET_LIMIT_STATE:
                parseSP_GET_LIMIT_STATE(returnmsg);
                break;
            case CMD.SP_GET_SETTLING_TIME:
                parseSP_GET_SETTLING_TIME(returnmsg);
                break;
            case CMD.SP_SET_SETTLING_TIME:
                parseSP_SET_SETTLING_TIME(returnmsg);
                break;
            case CMD.SP_GET_CONTROL_MODE:
                parseSP_GET_CONTROL_MODE(returnmsg);
                break;
            case CMD.SP_SET_CONTROL_MODE:
                parseSP_SET_CONTROL_MODE(returnmsg);
                break;
            case CMD.SP_GET_EX_TIME:
                parseSP_GET_EX_TIME(returnmsg);
                break;
            case CMD.SP_SET_EX_TIME:
                parseSP_SET_EX_TIME(returnmsg);
                break;
            case CMD.SP_GET_CAMERA_DIR:
                parseSP_GET_CAMERA_DIR(returnmsg);
                break;
            case CMD.SP_SET_CAMERA_DIR:
                parseSP_SET_CAMERA_DIR(returnmsg);
                break;
            case CMD.SP_GET_AUTO_OFF_SW:
                parseSP_GET_AUTO_OFF_SW(returnmsg);
                break;
            case CMD.SP_SET_AUTO_OFF_SW:
                parseSP_SET_AUTO_OFF_SW(returnmsg);
                break;
            case CMD.SP_GET_AUTO_LEVEL_EN:
                parseSP_GET_AUTO_LEVEL_EN(returnmsg);
                break;
            case CMD.SP_SET_AUTO_LEVEL_EN:
                parseSP_SET_AUTO_LEVEL_EN(returnmsg);
                break;
            case CMD.SP_SET_AUTO_LEVEL_STATE:
                parseSP_SET_AUTO_LEVEL_STATE(returnmsg);
                break;
            case CMD.SP_GET_HDMI_SUPPORT:
                parseSP_GET_HDMI_SUPPORT(returnmsg);
                break;
            case CMD.SP_SET_HDMI_STATE:
                parseSP_SET_HDMI_STATE(returnmsg);
                break;
            case CMD.SP_REBOOT_CONFIRM_MODE:
                parseSP_REBOOT_CONFIRM_MODE(returnmsg);
                break;
            case CMD.SP_GET_HDMI_STATE:
                parseSP_GET_HDMI_STATE(returnmsg);
                break;
            case CMD.SP_PUSH_HDMI_STREAM_STATE:
                parseSP_PUSH_HDMI_STREAM_STATE(returnmsg);
                break;

        }

        if (AppGlobalDataMgr.getInstance().isTestMode) {
            EventBus.getDefault().post(new ResponseModel(returncode, returnmsg));
        }
    }


    private void parseSP_SET_ISO(String msg) {
        String[] data = msg.split(";");
        String infoValue = null;
        String infoResult = null;
        for (String string : data) {
            string = string.trim();
            String value = string.substring(string.lastIndexOf(":") + 1).trim();
            if (string.startsWith("iso:")) {
                infoValue = value;
            } else if (string.startsWith("ret:")) {
                infoResult = value;
            }
        }

        CameraInfoModel cameraInfoModel = ParameterDataLoader.getInstance().getIsoCameraInfoModel();
        if (infoValue != null && infoResult != null && cameraInfoModel != null) {
            int index = cameraInfoModel.getSelectIndex();
            try {
                index = Integer.parseInt(infoValue);
            } catch (NumberFormatException e) {

            }

            if (CMD.SUCCEED_FLAG.equals(infoResult)) cameraInfoModel.setSelectIndex(index);
            EventBus.getDefault().post(new BroadcastActionEvent(MyMessage.SP_SET_ISO, infoResult));
        }
    }

    private void parseSP_SET_WB(String msg) {
        String[] data = msg.split(";");
        String infoValue = null;
        String infoResult = null;
        for (String string : data) {
            string = string.trim();
            String value = string.substring(string.lastIndexOf(":") + 1).trim();

            if (string.startsWith("wb:")) {
                infoValue = value;
            } else if (string.startsWith("ret:")) {
                infoResult = value;
            }
        }
        CameraInfoModel cameraInfoModel = ParameterDataLoader.getInstance().getWbCameraInfoModel();
        if (infoValue != null && infoResult != null && cameraInfoModel != null) {
            int index = cameraInfoModel.getSelectIndex();
            try {
                index = Integer.parseInt(infoValue);
            } catch (NumberFormatException e) {

            }
            if (CMD.SUCCEED_FLAG.equals(infoResult)) cameraInfoModel.setSelectIndex(index);
            EventBus.getDefault().post(new BroadcastActionEvent(MyMessage.SP_SET_WB, infoResult));
        }
    }

    private void parseSP_SET_FNUM(String msg) {
        String[] data = msg.split(";");
        String infoValue = null;
        String infoResult = null;
        for (String string : data) {
            string = string.trim();
            String value = string.substring(string.lastIndexOf(":") + 1).trim();

            if (string.startsWith("fNum:")) {
                infoValue = value;
            } else if (string.startsWith("ret:")) {
                infoResult = value;
            }
        }
        CameraInfoModel cameraInfoModel = ParameterDataLoader.getInstance().getfCameraInfoModel();
        if (infoValue != null && infoResult != null && cameraInfoModel != null) {
            int index = cameraInfoModel.getSelectIndex();
            try {
                index = Integer.parseInt(infoValue);
            } catch (NumberFormatException e) {

            }
            if (CMD.SUCCEED_FLAG.equals(infoResult)) cameraInfoModel.setSelectIndex(index);
            EventBus.getDefault().post(new BroadcastActionEvent(MyMessage.SP_SET_FNUM, infoResult));
        }
    }

    private void parseSP_SET_FOCUS(String msg) {
        String[] data = msg.split(";");
        String ret = null;

        for (String string : data) {
            string = string.trim();
            String value = string.substring(string.lastIndexOf(":") + 1).trim();

            if (string.startsWith("ret:")) {
                ret = value;
            }
        }
        EventBus.getDefault().post(new BroadcastActionEvent(MyMessage.SP_SET_FOCUS, ret));
    }

    private void parseSP_SET_SHUTTER(String msg) {
        String[] data = msg.split(";");
        String infoValue = null;
        String infoResult = null;
        for (String string : data) {
            string = string.trim();
            String value = string.substring(string.lastIndexOf(":") + 1).trim();

            if (string.startsWith("s:")) {
                infoValue = value;
            } else if (string.startsWith("ret:")) {
                infoResult = value;
            }
        }
        CameraInfoModel cameraInfoModel = ParameterDataLoader.getInstance().getsCameraInfoModel();
        if (infoValue != null && infoResult != null && cameraInfoModel != null) {
            int index = cameraInfoModel.getSelectIndex();
            try {
                index = Integer.parseInt(infoValue);
            } catch (NumberFormatException e) {
            }
            if (CMD.SUCCEED_FLAG.equals(infoResult)) cameraInfoModel.setSelectIndex(index);
            EventBus.getDefault().post(new BroadcastActionEvent(MyMessage.SP_SET_SHUTTER, infoResult));
        }
    }


    private void parseSP_SET_EV(String msg) {
        String[] data = msg.split(";");
        String infoValue = null;
        String infoResult = null;
        for (String string : data) {
            string = string.trim();
            String value = string.substring(string.lastIndexOf(":") + 1).trim();

            if (string.startsWith("ev:")) {
                infoValue = value;
            } else if (string.startsWith("ret:")) {
                infoResult = value;
            }
        }


        CameraInfoModel cameraInfoModel = ParameterDataLoader.getInstance().getEvCameraInfoModel();
        if (infoValue != null && infoResult != null && cameraInfoModel != null) {
            int index = cameraInfoModel.getSelectIndex();
            try {
                index = Integer.parseInt(infoValue);
            } catch (NumberFormatException e) {

            }
            if (CMD.SUCCEED_FLAG.equals(infoResult)) cameraInfoModel.setSelectIndex(index);
            EventBus.getDefault().post(new BroadcastActionEvent(MyMessage.SP_SET_EV, infoResult));
        }
    }


    private void parseSP_SET_PHOTO_RECORD_STATUS(String msg) {
        String[] data = msg.split(";");
        String state = null;

        for (String string : data) {
            string = string.trim();
            String value = string.substring(string.lastIndexOf(":") + 1).trim();
            if (string.startsWith("state:")) {
                state = value;
            }
        }
        if (state != null) {
            EventBus.getDefault().post(new BroadcastActionEvent(MyMessage.SP_SET_PHOTO_RECORD_STATUS, state));
        }
    }

    private void parseSP_GET_SHUTTER_INFO(String msg) {
        CameraInfoModel cameraInfoModel = ParameterDataLoader.getInstance().getsCameraInfoModel();
        String[] data = msg.split(";");
        if (cameraInfoModel == null) cameraInfoModel = new CameraInfoModel();
        int shutterPosition = 0;
        boolean available = false;

        for (String string : data) {
            string = string.trim();
            String value = string.substring(string.lastIndexOf(":") + 1).trim();
            if (string.startsWith("RD:")) {
                available = value.equals(CMD.SUCCEED_FLAG);
            } else if (string.startsWith("V:")) {
                try {
                    shutterPosition = Integer.parseInt(value);
                } catch (Exception e) {

                }
            } else if (string.startsWith("R:")) {
                String[] parameterList = value.split(",");
                List<CameraParamerIndex> infoList = new ArrayList<>();
                boolean hasBulb = false;
                cameraInfoModel.setCameraHasBuld(false);
                for (int i = 0; i < parameterList.length; i++) {
                    CameraParamerIndex cameraParamerIndex = new CameraParamerIndex(i, parameterList[i]);
                    infoList.add(cameraParamerIndex);
                    if (parameterList[i].toLowerCase().contains("bulb")) {
                        hasBulb = true;
                        cameraInfoModel.setCameraHasBuld(true);
                    }

                }

                if (!hasBulb) {
                    CameraParamerIndex cameraParamerIndex = new CameraParamerIndex(parameterList.length, "Bulb");
                    infoList.add(cameraParamerIndex);
                }

                cameraInfoModel.setInfoList(infoList);
            }
        }
        cameraInfoModel.setAvailable(available);
        cameraInfoModel.setSelectIndex(shutterPosition);
        ParameterDataLoader.getInstance().setsCameraInfoModel(cameraInfoModel);
        EventBus.getDefault().post(new BroadcastActionEvent(MyMessage.SP_GET_SHUTTER_INFO));
    }


    private void parseSP_GET_FNUM_INFO(String msg) {
        CameraInfoModel cameraInfoModel = ParameterDataLoader.getInstance().getfCameraInfoModel();
        String[] data = msg.split(";");
        if (cameraInfoModel == null) cameraInfoModel = new CameraInfoModel();

        int focusingPosition = 0;
        boolean available = false;
        for (String string : data) {
            string = string.trim();
            String value = string.substring(string.lastIndexOf(":") + 1).trim();
            if (string.startsWith("RD:")) {
                available = value.equals(CMD.SUCCEED_FLAG);
            } else if (string.startsWith("V:")) {
                try {
                    focusingPosition = Integer.parseInt(value);
                } catch (Exception e) {

                }
            } else if (string.startsWith("R:")) {
                String[] parameterList = value.split(",");
                List<CameraParamerIndex> infoList = new ArrayList<>();
                for (int i = 0; i < parameterList.length; i++) {
                    CameraParamerIndex cameraParamerIndex = new CameraParamerIndex(i, parameterList[i]);
                    infoList.add(cameraParamerIndex);
                }
                cameraInfoModel.setInfoList(infoList);
            }
        }
        cameraInfoModel.setAvailable(available);
        cameraInfoModel.setSelectIndex(focusingPosition);
        ParameterDataLoader.getInstance().setfCameraInfoModel(cameraInfoModel);
        EventBus.getDefault().post(new BroadcastActionEvent(MyMessage.SP_GET_FNUM_INFO));
    }


    private void parseSP_GET_EV_INFO(String msg) {
        CameraInfoModel cameraInfoModel = ParameterDataLoader.getInstance().getEvCameraInfoModel();
        String[] data = msg.split(";");
        if (cameraInfoModel == null) cameraInfoModel = new CameraInfoModel();

        int evPosition = 0;
        boolean available = false;

        for (String string : data) {
            string = string.trim();
            String value = string.substring(string.lastIndexOf(":") + 1).trim();
            if (string.startsWith("RD:")) {
                available = value.equals(CMD.SUCCEED_FLAG);
            } else if (string.startsWith("V:")) {
                try {
                    evPosition = Integer.parseInt(value);
                } catch (Exception e) {

                }
            } else if (string.startsWith("R:")) {
                String[] parameterList = value.split(",");
                List<CameraParamerIndex> infoList = new ArrayList<>();
                for (int i = 0; i < parameterList.length; i++) {
                    CameraParamerIndex cameraParamerIndex = new CameraParamerIndex(i, parameterList[i]);
                    infoList.add(cameraParamerIndex);
                }
                cameraInfoModel.setInfoList(infoList);
            }
        }
        cameraInfoModel.setAvailable(available);
        cameraInfoModel.setSelectIndex(evPosition);
        ParameterDataLoader.getInstance().setEvCameraInfoModel(cameraInfoModel);
        EventBus.getDefault().post(new BroadcastActionEvent(MyMessage.SP_GET_EV_INFO));
    }


    private void parseSP_GET_ISO_INFO(String msg) {
        CameraInfoModel cameraInfoModel = ParameterDataLoader.getInstance().getIsoCameraInfoModel();
        String[] data = msg.split(";");
        if (cameraInfoModel == null) cameraInfoModel = new CameraInfoModel();
        int isoPosition = 0;
        boolean available = false;
        for (String string : data) {
            string = string.trim();
            String value = string.substring(string.lastIndexOf(":") + 1).trim();
            if (string.startsWith("RD:")) {
                available = value.equals(CMD.SUCCEED_FLAG);
            } else if (string.startsWith("V:")) {
                try {
                    isoPosition = Integer.parseInt(value);
                } catch (Exception e) {

                }
            } else if (string.startsWith("R:")) {
                String[] parameterList = value.split(",");
                List<CameraParamerIndex> infoList = new ArrayList<>();
                for (int i = 0; i < parameterList.length; i++) {
                    if (parameterList[i].toLowerCase().contains("auto")) {
                        parameterList[i] = "Auto";
                    }
                    CameraParamerIndex cameraParamerIndex = new CameraParamerIndex(i, parameterList[i]);
                    infoList.add(cameraParamerIndex);
                }
                cameraInfoModel.setInfoList(infoList);
            }
        }
        cameraInfoModel.setAvailable(available);
        cameraInfoModel.setSelectIndex(isoPosition);
        ParameterDataLoader.getInstance().setIsoCameraInfoModel(cameraInfoModel);
        EventBus.getDefault().post(new BroadcastActionEvent(MyMessage.SP_GET_ISO_INFO));
    }

    private void parseSP_GET_WB_INFO(String msg) {
        CameraInfoModel cameraInfoModel = ParameterDataLoader.getInstance().getWbCameraInfoModel();
        String[] data = msg.split(";");
        if (cameraInfoModel == null) cameraInfoModel = new CameraInfoModel();

        int wbPosition = 0;
        boolean available = false;
        for (String string : data) {
            string = string.trim();
            String value = string.substring(string.lastIndexOf(":") + 1).trim();
            if (string.startsWith("RD:")) {
                available = value.equals(CMD.SUCCEED_FLAG);
            } else if (string.startsWith("V:")) {
                try {
                    wbPosition = Integer.parseInt(value);
                } catch (Exception e) {

                }
            } else if (string.startsWith("R:")) {
                String[] parameterList = value.split(",");
                List<CameraParamerIndex> infoList = new ArrayList<>();
                for (int i = 0; i < parameterList.length; i++) {
                    CameraParamerIndex cameraParamerIndex = new CameraParamerIndex(i, parameterList[i]);
                    infoList.add(cameraParamerIndex);
                }
                cameraInfoModel.setInfoList(infoList);
            }
        }
        cameraInfoModel.setAvailable(available);
        cameraInfoModel.setSelectIndex(wbPosition);
        ParameterDataLoader.getInstance().setWbCameraInfoModel(cameraInfoModel);
        EventBus.getDefault().post(new BroadcastActionEvent(MyMessage.SP_GET_WB_INFO));
    }


    private void parseSP_PANORAMIC(String msg) {
        String[] data = msg.split(";");
        String step = null;
        String ret = null;
        String num = null;
        String state = null;
        int remainNum = -1;

        for (String string : data) {
            string = string.trim();
            String value = string.substring(string.lastIndexOf(":") + 1).trim();
            if (string.startsWith("step:")) {
                step = value;
            } else if (string.startsWith("ret:")) {
                ret = value;
            } else if (string.startsWith("num:")) {
                num = value;
            } else if (string.startsWith("state:")) {
                state = value;
            }
        }

        if (step == null) return;

        String recordState;
        switch (step) {
            case "2":
                recordState = CMD.recordStart;
                break;
            case "3":
                try {
                    if (ret == null) remainNum = Integer.parseInt(num);
                    else remainNum = Integer.parseInt(ret);
                } catch (Exception e) {

                }
                recordState = CMD.recordRemainNumber;
                SP_PANORAMIC_COMPLETION_NUMBER();
                break;
            case "4":
                recordState = CMD.recordStop;
                SP_PANORAMIC_END();
                break;
            case "5":
                recordState = CMD.recordComplete;
                SP_PANORAMIC_COMPLETION();
                break;
            case "6":
                recordState = CMD.recordCancle;
                break;
            case "10":
                EventBus.getDefault().post(new BroadcastActionEvent(MyMessage.SP_PANORAMA_SET_BACK_START_POINT, CMD.SUCCEED_FLAG.equals(ret)));
                return;
            case "11":
                EventBus.getDefault().post(new BroadcastActionEvent(MyMessage.SP_PANORAMA_GET_BACK_START_POINT_STATE, state));
                return;
            default:
                return;
        }

        RecordStateModel recordStateModel = new RecordStateModel();
        recordStateModel.recordState = recordState;
        recordStateModel.ret = ret;
        recordStateModel.remainNum = remainNum;
        recordStateModel.code = CMD.SP_PANORAMIC;
        EventBus.getDefault().post(recordStateModel);

    }


//    private void parseSP_SKY_PANORAMIC(String msg) {
//        String[] data = msg.split(";");
//        String step = null;
//        String ret = null;
//        int remainNum = -1;
//
//        for (String string : data) {
//            string = string.trim();
//            String value = string.substring(string.lastIndexOf(":") + 1).trim();
//            if (string.startsWith("step:")) {
//                step = value;
//            } else if (string.startsWith("ret:")) {
//                ret = value;
//            }
//        }
//
//        if (step == null)
//            return;
//
//        String recordState;
//        switch (step) {
//            case "1":
//                recordState = CMD.recordStart;
//                break;
//            case "2":
//                try {
//                    remainNum = Integer.parseInt(ret);
//                } catch (Exception e) {
//
//                }
//                recordState = CMD.recordRemainNumber;
//                SP_SKY_PANORAMIC_NUMBER();
//                break;
//            case "3":
//                recordState = CMD.recordComplete;
//                SP_SKY_PANORAMIC_COMPLETION();
//                break;
//            case "4":
//                recordState = CMD.recordCancle;
//                break;
//            default:
//                return;
//        }
//
//        RecordStateModel recordStateModel = new RecordStateModel();
//        recordStateModel.recordState = recordState;
//        recordStateModel.ret = ret;
//        recordStateModel.remainNum = remainNum;
//        recordStateModel.code = CMD.SP_SKY_PANORAMIC;
//        EventBus.getDefault().post(recordStateModel);
//
//    }


    private void parseSP_SET_VIDEO_RECORD_STATUS(String msg) {
        String[] data = msg.split(";");
        String state = null;
        String ret = null;
        for (String string : data) {
            string = string.trim();
            String value = string.substring(string.lastIndexOf(":") + 1).trim();
            if (string.startsWith("state:")) {
                state = value;
            }
        }
        if (state == null) return;

        String recordState = "";
        switch (state) {
            case "1":
                recordState = CMD.recordStart;
                ret = CMD.SUCCEED_FLAG;
                break;
            case "0":
                recordState = CMD.recordComplete;
                ret = CMD.SUCCEED_FLAG;
                break;
            case "-1":
                recordState = CMD.recordStart;
                ret = "-1";
                break;
        }
        RecordStateModel recordStateModel = new RecordStateModel();
        recordStateModel.recordState = recordState;
        recordStateModel.ret = ret;
        recordStateModel.code = CMD.SP_SET_VIDEO_RECORD_STATUS;
        EventBus.getDefault().post(recordStateModel);
    }

    private void parseSP_FOCUS_STACK(String msg) {
        String[] data = msg.split(";");
        String step = null;
        String ret = null;
        int remainNum = -1;

        for (String string : data) {
            string = string.trim();
            String value = string.substring(string.lastIndexOf(":") + 1).trim();
            if (string.startsWith("step:")) {
                step = value;
            } else if (string.startsWith("ret:")) {
                ret = value;
            }
        }

        if (step == null) return;
        String recordState;
        switch (step) {
            case "2":
                recordState = CMD.recordStart;
                break;
            case "3":
                recordState = CMD.recordStop;
                SP_FOCUS_STACK_STOP();
                break;
            case "4":
                recordState = CMD.recordComplete;
                SP_FOCUS_STACK_COMPLETION();
                break;
            case "5":
                recordState = CMD.recordCancle;
                break;
            case "6":
                try {
                    remainNum = Integer.parseInt(ret);
                } catch (Exception e) {

                }
                recordState = CMD.recordRemainNumber;
                break;
            case "7":
                EventBus.getDefault().post(new BroadcastActionEvent(MyMessage.SP_FOCUS_STACK_PREVIEW_MSG, msg));
                return;
            case "8":
                SP_FOCUS_STACK_PREVIEW_NUM();
                EventBus.getDefault().post(new BroadcastActionEvent(MyMessage.SP_FOCUS_STACK_PREVIEW_MSG, msg));
                return;
            case "9":
                SP_FOCUS_STACK_PREVIEW_END();
                EventBus.getDefault().post(new BroadcastActionEvent(MyMessage.SP_FOCUS_STACK_PREVIEW_MSG, msg));
                return;
            case "10":
                EventBus.getDefault().post(new BroadcastActionEvent(MyMessage.SP_FOCUS_STACK_PREVIEW_MSG, msg));
                return;
            default:
                return;
        }

        RecordStateModel recordStateModel = new RecordStateModel();
        recordStateModel.recordState = recordState;
        recordStateModel.ret = ret;
        recordStateModel.remainNum = remainNum;
        recordStateModel.code = CMD.SP_FOCUS_STACK;
        EventBus.getDefault().post(recordStateModel);

    }


    private void parseSP_DELAY_SHOT(String msg) {
        String[] data = msg.split(";");
        String step = null;
        String ret = null;
        String state = null;
        int remainNum = -1;
        int photoNum = 0; //
        for (String string : data) {
            string = string.trim();
            String value = string.substring(string.lastIndexOf(":") + 1).trim();
            if (string.startsWith("step:")) {
                step = value;
            } else if (string.startsWith("ret:")) {
                ret = value;
            } else if (string.startsWith("state:")) {
                state = value;
            } else if (string.startsWith("photoNum:")) {
                try {
                    photoNum = Integer.parseInt(value);
                } catch (Exception e) {

                }
            }
        }

        if (step == null) return;
        int sendPointIndex = -1;

        if (AppGlobalDataMgr.getInstance().isDynamicLapsePreviewing) {
            switch (step) {
                case "1":
                    EventBus.getDefault().post(new BroadcastActionEvent(MyMessage.SP_DELAY_SHOT_START, CMD.SUCCEED_FLAG.equals(ret)));
                    break;
                case "2":
                    sendPointIndex = Integer.parseInt(ret);
                    EventBus.getDefault().post(new BroadcastActionEvent(MyMessage.SP_DELAY_SHOT_SEND_POINT, sendPointIndex));
                    break;
                case "3":
                    EventBus.getDefault().post(new BroadcastActionEvent(MyMessage.SP_DELAY_SHOT_SEND_POINT_END, ret));
                    break;
                case "5":
                    SP_DELAY_SHOT_SHOOTING_COMPLETE();
                    EventBus.getDefault().post(new BroadcastActionEvent(MyMessage.SP_DELAY_SHOT_SHOOTING_COMPLETE, ret));
                    break;
                case "7":
                    EventBus.getDefault().post(new BroadcastActionEvent(MyMessage.SP_DELAY_SHOT_CANCEL, CMD.SUCCEED_FLAG.equals(ret)));
                    break;
            }
        } else {
            String recordState;
            switch (step) {
                case "1":
                    recordState = CMD.recordStart;
                    break;
                case "2":
                    try {
                        sendPointIndex = Integer.parseInt(ret);
                    } catch (Exception e) {

                    }
                    recordState = CMD.recordSendPointIndex;
                    break;
                case "3":
                    recordState = CMD.recordSendPointEnd;
                    break;
                case "4":
                    try {
                        remainNum = Integer.parseInt(ret);
                    } catch (Exception e) {

                    }
                    recordState = CMD.recordRemainNumber;
                    SP_DELAY_SHOT_SHOOTING_NUM();
                    break;
                case "5":
                    recordState = CMD.recordStop;
                    SP_DELAY_SHOT_SHOOTING_COMPLETE();
                    break;
                case "6":
                    recordState = CMD.recordComplete;
                    SP_DELAY_SHOT_PROCESS_COMPLETE();
                    break;
                case "7":
                    recordState = CMD.recordCancle;
                    break;
                case "8":
                    EventBus.getDefault().post(new BroadcastActionEvent(MyMessage.SP_DELAY_SHOT_END_BACK, "0".equals(ret)));
                    return;
                case "9":
                    EventBus.getDefault().post(new BroadcastActionEvent(MyMessage.SP_DELAY_SHOT_GET_END_BACK_SETTING_STATE, state));
                    return;
                default:
                    return;
            }
            RecordStateModel recordStateModel = new RecordStateModel();
            recordStateModel.recordState = recordState;
            recordStateModel.ret = ret;
            recordStateModel.sendPointIndex = sendPointIndex;
            recordStateModel.remainNum = remainNum;
            recordStateModel.photoNum = photoNum;
            recordStateModel.code = CMD.SP_DELAY_SHOT;
            EventBus.getDefault().post(recordStateModel);
        }

    }


    private void parseSP_REMOVE_PEOPLE_SHOT(String msg) {
        String[] data = msg.split(";");
        String step = null;
        String ret = null;
        int remainNum = -1;

        for (String string : data) {
            string = string.trim();
            String value = string.substring(string.lastIndexOf(":") + 1).trim();
            if (string.startsWith("step:")) {
                step = value;
            } else if (string.startsWith("ret:")) {
                ret = value;
            }
        }

        if (step == null) return;

        String recordState;
        switch (step) {
            case "1":
                recordState = CMD.recordStart;
                break;

            case "2":
                try {
                    remainNum = Integer.parseInt(ret);
                } catch (Exception e) {

                }
                recordState = CMD.recordRemainNumber;
                SP_REMOVE_PEOPLE_SHOT_NUM();
                break;
            case "3":
                recordState = CMD.recordStop;
                SP_REMOVE_PEOPLE_SHOT_END();
                break;
            case "4":
                recordState = CMD.recordComplete;
                SP_REMOVE_PEOPLE_SHOT_COMPLITE();
                break;
            case "6":
                recordState = CMD.recordCancle;
                break;
            default:
                return;
        }

        RecordStateModel recordStateModel = new RecordStateModel();
        recordStateModel.recordState = recordState;
        recordStateModel.ret = ret;
        recordStateModel.remainNum = remainNum;
        recordStateModel.code = CMD.SP_REMOVE_PEOPLE_SHOT;
        EventBus.getDefault().post(recordStateModel);

    }


    private void parseSP_SUN_SHOT(String msg) {
        String[] data = msg.split(";");
        String step = null;
        String ret = null;

        for (String string : data) {
            string = string.trim();
            String value = string.substring(string.lastIndexOf(":") + 1).trim();
            if (string.startsWith("step:")) {
                step = value;
            } else if (string.startsWith("ret:")) {
                ret = value;
            }
        }

        if (step == null) return;

        String recordState;

        switch (step) {
            case "1":
                recordState = CMD.recordStart;
                break;
            case "2":
                recordState = CMD.recordCancle;
                break;
            case "3":
                recordState = CMD.recordStop;
                SP_SUN_SHOT_END();
                break;
            case "4":
                recordState = CMD.recordComplete;
                SP_SUN_SHOT_COMPLETE();
                break;
            case "5":
                recordState = CMD.recordAppointmentState;
                SP_SUN_SHOT_APPOINTMENT_END();
                break;
            default:
                return;
        }

        RecordStateModel recordStateModel = new RecordStateModel();
        recordStateModel.recordState = recordState;
        recordStateModel.ret = ret;
        recordStateModel.appointmentStateStart = 2;
        recordStateModel.code = CMD.SP_SUN_SHOT;
        EventBus.getDefault().post(recordStateModel);

    }

    private void parseSP_PLC(String msg) {
        String[] msgData = msg.split(";");
        String step = null;
        String ret = null;


        for (String string : msgData) {
            string = string.trim();
            String value = string.substring(string.lastIndexOf(":") + 1).trim();
            if (string.startsWith("step:")) {
                step = value;
            } else if (string.startsWith("ret:")) {
                ret = value;
            }
        }
        if (step == null) return;
        int sendPointIndex = -1;
        int state = 0;
        String recordState;
        switch (step) {
            case "1":
                recordState = CMD.recordStart;
                break;
            case "2"://参数传输
                try {
                    sendPointIndex = Integer.parseInt(ret);
                } catch (Exception e) {

                }
                recordState = CMD.recordSendPointIndex;
                break;
            case "3"://参数传输完成
                recordState = CMD.recordSendPointEnd;
                break;
            case "4"://设置预约时间
                recordState = CMD.recordSetAppointmentTime;
                break;
            case "5":
                recordState = CMD.recordAppointmentState;
                try {
                    state = Integer.parseInt(ret);
                } catch (Exception e) {

                }
                break;
            case "6":
                recordState = CMD.recordCancle;
                break;
            default:
                return;
        }


        RecordStateModel recordStateModel = new RecordStateModel();
        recordStateModel.recordState = recordState;
        recordStateModel.ret = ret;
        recordStateModel.sendPointIndex = sendPointIndex;
        recordStateModel.appointmentStateStart = state;
        recordStateModel.code = CMD.SP_PLC;
        EventBus.getDefault().post(recordStateModel);

    }

    public void parseSP_PUSH_MODE_STATE(String msg) {
        String[] datas = msg.split(";");

        int mode = 1;
        int state = 0;
        int remNum = 0;
        int runTime = 0;
        int remTime = 0;
        String track = CMD.unAlignStar;
        int speed = 0;
        int halfSpeed = 0;
        String startTime = "0";
        String endTime = "0";
        int interval = 1;
        boolean sunRise = false;
        int photoNum = 0;

        for (String string : datas) {
            string = string.trim();
            String value = string.substring(string.lastIndexOf(":") + 1).trim();
            if (value.equals("")) continue;

            if (string.startsWith("mode")) {
                mode = Integer.parseInt(value);
            } else if (string.startsWith("state")) {
                state = Integer.parseInt(value);
            } else if (string.startsWith("remNum")) {
                remNum = Integer.parseInt(value);
            } else if (string.startsWith("runTime")) {
                try {
                    runTime = Integer.parseInt(value);
                } catch (Exception e) {
                }
            } else if (string.startsWith("remTime")) {
                try {
                    remTime = Integer.parseInt(value);
                } catch (Exception e) {
                }
            } else if (string.startsWith("track")) {
                track = value;
            } else if (string.startsWith("speed")) {
                speed = Integer.parseInt(value);
            } else if (string.startsWith("halfSpeed")) {
                halfSpeed = Integer.parseInt(value);
            } else if (string.startsWith("startTime")) {
                startTime = value;
            } else if (string.startsWith("endTime")) {
                endTime = value;
            } else if (string.startsWith("interval")) {
                interval = Integer.parseInt(value);
            } else if (string.startsWith("sun")) {
                sunRise = "0".equals(value);
            } else if (string.startsWith("photoNum")) {
                photoNum = Integer.parseInt(value);
            }
        }

        StateRecoveryModel stateRecoveryModel = new StateRecoveryModel();
        stateRecoveryModel.mode = mode;
        stateRecoveryModel.state = state;
        stateRecoveryModel.remNum = remNum;
        stateRecoveryModel.runTime = runTime;
        stateRecoveryModel.remTime = remTime;
        stateRecoveryModel.track = track;
        stateRecoveryModel.speed = speed;
        stateRecoveryModel.halfSpeed = halfSpeed;
        stateRecoveryModel.startTime = startTime;
        stateRecoveryModel.endTime = endTime;
        stateRecoveryModel.interval = interval;
        stateRecoveryModel.sunRise = sunRise;
        stateRecoveryModel.photoNum = photoNum;

        EventBus.getDefault().post(stateRecoveryModel);
    }

    private void parseSP_SET_MODE_STATE(String msg) {
        String[] data = msg.split(";");
        int mode = 1;
        String setModelResult = null;

        for (String string : data) {
            string = string.trim();
            String value = string.substring(string.lastIndexOf(":") + 1).trim();
            if (string.startsWith("mode:")) {
                mode = Integer.parseInt(value);
            } else if (string.startsWith("ret:")) {
                setModelResult = value;
            }
        }
        EventBus.getDefault().post(new BroadcastActionEvent(MyMessage.SET_MODE_STATE, setModelResult, mode));
    }

    private void parseSP_HDR(String msg) {
        String[] data = msg.split(";");
        String step = null;
        String ret = null;
        int remainNum = -1;
        for (String string : data) {
            string = string.trim();
            String value = string.substring(string.lastIndexOf(":") + 1).trim();
            if (string.startsWith("step:")) {
                step = value;
            } else if (string.startsWith("ret:")) {
                ret = value;
            }
        }
        if (step == null) return;
        String recordState;
        switch (step) {
            case "1":
                recordState = CMD.recordStart;
                break;
            case "2":
                recordState = CMD.recordStop;
                SP_HDR_END();
                break;
            case "3":
                recordState = CMD.recordComplete;
                SP_HDR_COMPLETE();
                break;
            case "4":
                recordState = CMD.recordCancle;
                break;
            case "5":
                try {
                    remainNum = Integer.parseInt(ret);
                } catch (Exception e) {

                }
                recordState = CMD.recordRemainNumber;
                break;
            default:
                return;
        }
        RecordStateModel recordStateModel = new RecordStateModel();
        recordStateModel.recordState = recordState;
        recordStateModel.ret = ret;
        recordStateModel.code = CMD.SP_HDR;
        recordStateModel.remainNum = remainNum;
        EventBus.getDefault().post(recordStateModel);
    }


    //yaw:%f;pitch:%f;roll:%f;
    private void parseSP_GET_GIMBAL_POS(String msg) {
        String[] data = msg.split(";");
        String x = "0";
        String y = "0";
        String z = "0";

        for (String string : data) {
            string = string.trim();
            String value = string.substring(string.lastIndexOf(":") + 1).trim();
            if (string.startsWith("yaw:")) {
                x = value;
            } else if (string.startsWith("pitch:")) {
                y = value;
            } else if (string.startsWith("roll:")) {
                z = value;
            }
        }
        EventBus.getDefault().post(new BroadcastActionEvent(MyMessage.SP_GET_GIMBAL_POS, x, y, z));
    }

    private void parseSP_SET_GIMBAL_POS(String msg) {
        String[] data = msg.split(";");
        String ret = null;
        for (String string : data) {
            string = string.trim();
            String value = string.substring(string.lastIndexOf(":") + 1).trim();
            if (string.startsWith("ret:")) {
                ret = value;
            }
        }
        EventBus.getDefault().post(new BroadcastActionEvent(MyMessage.SP_SET_GIMBAL_POS, ret));
    }

    private void parseSP_CALIBRATE_START(String msg) {
        String[] data = msg.split(";");
        String calibrateRet = null;
        String step = null;
        for (String string : data) {
            string = string.trim();
            String value = string.substring(string.lastIndexOf(":") + 1).trim();
            if (string.startsWith("ret:")) {
                calibrateRet = value;
            } else if (string.startsWith("step:")) {
                step = value;
            }
        }
        EventBus.getDefault().post(new BroadcastActionEvent(MyMessage.ACTION_SP_CALIBRATE_START, calibrateRet, step));
    }

    private void parseSP_PUSH_ROTATE_VECTOR(String msg) {
        String[] data = msg.split(";");
        boolean hasInitx = false;
        boolean hasInity = false;
        boolean hasInitz = false;
        boolean hasInitw = false;
        float[] values = new float[4];
        double compass = 0f;
        double alt = 0f;


        for (String string : data) {
            string = string.trim();
            String value = string.substring(string.lastIndexOf(":") + 1).trim();
            if (string.startsWith("w:")) {
                if (hasInitw == true) continue;
                values[3] = Float.parseFloat(value);
                hasInitw = true;
            } else if (string.startsWith("x:")) {
                if (hasInitx == true) continue;
                values[0] = Float.parseFloat(value);
                hasInitx = true;
            } else if (string.startsWith("y:")) {
                if (hasInity == true) continue;
                values[1] = Float.parseFloat(value);
                hasInity = true;
            } else if (string.startsWith("z:")) {
                if (hasInitz == true) continue;
                values[2] = Float.parseFloat(value);
                hasInitz = true;
            } else if (string.startsWith("compass:")) {
                compass = Double.parseDouble(value);
            } else if (string.startsWith("alt:")) {
                alt = Double.parseDouble(value);
            }
        }

        EventBus.getDefault().post(new SensorValuesModle(values, compass, alt));
    }


    private void parseSP_SET_YAW(String msg) {
        String[] data = msg.split(";");
        boolean showDemarcate = true;
        for (String string : data) {
            string = string.trim();
            String value = string.substring(string.lastIndexOf(":") + 1).trim();
            if (string.startsWith("ret")) {
                showDemarcate = Integer.parseInt(value) == 1;
            }
        }
        EventBus.getDefault().post(new BroadcastActionEvent(MyMessage.SP_SET_YAW, showDemarcate));

    }


    private void parseSP_SET_GOTO_AU_STATE(String msg) {
        String[] data = msg.split(";");
        String gotoRet = null;
        boolean track = true;
        for (String string : data) {
            string = string.trim();
            String value = string.substring(string.lastIndexOf(":") + 1).trim();
            if (string.startsWith("ret:")) {
                gotoRet = value;
            } else if (string.startsWith("track:")) {
                track = value.equals("1");
            }
        }

        EventBus.getDefault().post(new BroadcastActionEvent(MyMessage.parseSP_SET_GOTO_AU_STATE, gotoRet, track));
    }

    private void parseSP_SET_TRACK_AU_STATE(String msg) {
        String[] data = msg.split(";");
        String ret = "";
        for (String string : data) {
            string = string.trim();
            String value = string.substring(string.lastIndexOf(":") + 1).trim();
            if (string.startsWith("ret:")) {
                ret = value;
            }
        }
        EventBus.getDefault().post(new BroadcastActionEvent(MyMessage.SP_SET_TRACK_AU_STATE, ret));

    }


    private void parseSP_SET_AHRS_STATE(String msg) {
        String value = msg.substring(msg.lastIndexOf(":") + 1).trim();
        EventBus.getDefault().post(new BroadcastActionEvent(MyMessage.SP_SET_AHRS_STATE, value.equals(CMD.SUCCEED_FLAG)));
    }


    //path:%s;size:%d;cTime:%s;duration:%d;type:%d;
    private void parseSP_ADD_FILE(String msg) {
        MediaFileLoad.getInstance().getFileListFromResponse(msg, true);
        String[] data = msg.split(";");
        String path = null;
        String size = null;
        String cTime = null;
        String duration = null;
        String type = null;

        for (String string : data) {
            string = string.trim();
            String value = string.substring(string.lastIndexOf(":") + 1).trim();
            if (string.startsWith("path:")) {
                if (value.length() < 5) return;
                path = value;
            } else if (string.startsWith("size:")) {
                size = value;
            } else if (string.startsWith("cTime:")) {
                cTime = value;
            } else if (string.startsWith("duration:")) {
                duration = value;
            } else if (string.startsWith("type:")) {
                type = value;
            }
        }

        EventBus.getDefault().post(new BroadcastActionEvent(MyMessage.ACTION_SP_ADD_FILE, type, path));
    }

    private void parseSP_GET_FILE_COUNT(String msg) {
        MediaFileLoad.getInstance().getfileCountResponse(msg);
    }

    private void parseSP_GET_FILE_LIST(String msg) {
        Log.d(TAG, "parseSP_GET_FILE_LIST: " + msg);
        MediaFileLoad.getInstance().getFileListFromResponse(msg, false);
    }

    private void parseSP_DEL_FILE(String msg) {
        MediaFileLoad.getInstance().deletedFileResponse(msg);
    }


    public void parseSP_SD_FORMAT(String msg) {
        String[] data = msg.split(";");
        String ret = null;
        for (String string : data) {
            string = string.trim();
            String value = string.substring(string.lastIndexOf(":") + 1).trim();
            if (string.startsWith("ret:")) {
                ret = value;
            }
        }

        EventBus.getDefault().post(new BroadcastActionEvent(MyMessage.SP_SD_FORMAT, ret));
    }

    //status:%d;totalspace:%lld;freespace:%lld;usespace:%lld;
    public void parseSP_GET_SD_INFO(String msg) {
        String[] data = msg.split(";");
        int status = 0;
        long totalspace = 0;
        long freespace = 0;
        long usespace = 0;


        try {
            for (String string : data) {
                string = string.trim();
                String value = string.substring(string.lastIndexOf(":") + 1).trim();
                if (string.startsWith("status:")) {
                    status = Integer.parseInt(value);
                } else if (string.startsWith("totalspace:")) {
                    totalspace = Long.parseLong(value);
                } else if (string.startsWith("freespace:")) {
                    freespace = Long.parseLong(value);
                } else if (string.startsWith("usespace:")) {
                    usespace = Long.parseLong(value);
                }
            }
        } catch (Exception e) {

        }


        if (memoryModel == null) memoryModel = new MemoryModel();

        memoryModel.status = status;
        memoryModel.totalspace = totalspace;
        memoryModel.freespace = freespace;
        memoryModel.usespace = usespace;

        EventBus.getDefault().post(memoryModel);
        stopMemoryRxTimer();
    }

    public void parseSP_PUSH_SD_INFO(String msg) {
        parseSP_GET_SD_INFO("status:" + MemoryModel.SDCardMounted + ";" + msg);
    }

    public void parseSP_PUSH_SD_HINT_ID(String msg) {
        int hintId = 0;
        String[] data = msg.split(";");
        try {
            for (String string : data) {
                string = string.trim();
                String value = string.substring(string.lastIndexOf(":") + 1).trim();
                if (string.startsWith("hintId:")) {
                    hintId = Integer.parseInt(value);
                }
            }
        } catch (Exception e) {

        }
        if (memoryModel == null) memoryModel = new MemoryModel();

        if (hintId == 1) {
            hintId = MemoryModel.SDCardNotExit;
            memoryModel.freespace = 0;
        } else if (hintId == 2 || hintId == 5) {
            memoryModel.freespace = 0;
        }
        memoryModel.status = hintId;
        EventBus.getDefault().post(memoryModel);
    }

    public void parseSP_GET_BAT_STATE(String msg) {
        String[] data = msg.split(";");
        int capacity = 0;
        int charge = 0;
        try {
            for (String string : data) {
                string = string.trim();
                String value = string.substring(string.lastIndexOf(":") + 1).trim();
                if (string.startsWith("capacity:")) {
                    capacity = Integer.parseInt(value);
                } else if (string.startsWith("charge:")) {
                    charge = Integer.parseInt(value);
                }
            }
        } catch (Exception e) {
        }

        if (batteryModel == null) batteryModel = new BatteryModel();
        batteryModel.capacity = capacity;
        batteryModel.charge = charge;
        EventBus.getDefault().post(batteryModel);
        if (capacity != 0) {
            stopBatteryRxTimer();
        }
    }


    public void parseSP_GET_DEVICE_VERSION(String msg) {
        PolarisVersion polarisVersion = null;
        FirmWareVersionInfo currentVmateVersionInfo = null;
        ExtraDevVersionInfo extraDevVersionInfo = null;
        polarisVersion = new PolarisVersion();
        int sv = 0;
        String[] data = msg.split(";");
        for (String string : data) {
            string = string.trim();
            String value = string.substring(string.lastIndexOf(":") + 1).trim();
            if (string.startsWith("hw:")) {
                if (currentVmateVersionInfo == null)
                    currentVmateVersionInfo = new FirmWareVersionInfo();
                currentVmateVersionInfo.setHardwareVersion(value);
            } else if (string.startsWith("sw:")) {
                if (currentVmateVersionInfo == null)
                    currentVmateVersionInfo = new FirmWareVersionInfo();
                currentVmateVersionInfo.setSoftwareVersion(value);
//                currentVmateVersionInfo.setSoftwareVersion("4.0.0.26");
            } else if (string.startsWith("exAxis:")) {
                if (value != null && !value.equals("")) {
                    if (extraDevVersionInfo == null)
                        extraDevVersionInfo = new ExtraDevVersionInfo();
                    extraDevVersionInfo.setExAxisVersion(value);
//                    extraDevVersionInfo.setExAxisVersion("1.0.0.6");
                }
            } else if (string.startsWith("sv:")) {
                try {
                    svFlag = Integer.parseInt(value);
                } catch (Exception e) {

                }
            }
        }

        Log.e(TAG, "parseSP_GET_DEVICE_VERSION: svFlag =" + svFlag);

        polarisVersion.firmWareVersionInfo = currentVmateVersionInfo;
        polarisVersion.extraDevVersionInfo = extraDevVersionInfo;

        EventBus.getDefault().post(polarisVersion);
    }


    public void parseSP_SET_UPGRADE_START(String msg) {
        String[] data = msg.split(";");
        String ret = null;
        for (String string : data) {
            string = string.trim();
            String value = string.substring(string.lastIndexOf(":") + 1).trim();
            if (string.startsWith("ret:")) {
                ret = value;
            }
        }
        EventBus.getDefault().post(new BroadcastActionEvent(MyMessage.SP_SET_UPGRADE_START, ret));
    }

    public void parseSP_LOAD_UPGRADE_FW_STATE(String msg) {

        String[] data = msg.split(";");
        String ret = null;
        for (String string : data) {
            string = string.trim();
            String value = string.substring(string.lastIndexOf(":") + 1).trim();
            if (string.startsWith("ret:")) {
                ret = value;
            }
        }

        EventBus.getDefault().post(new BroadcastActionEvent(MyMessage.SP_LOAD_UPGRADE_FW_STATE, ret));
    }

    public void parseSP_PUSH_UPGRADE_STATUS(String msg) {
        String[] data = msg.split(";");
        int update = -1;
        for (String string : data) {
            string = string.trim();
            String value = string.substring(string.lastIndexOf(":") + 1).trim();
            if (string.startsWith("state:")) {
                update = Integer.parseInt(value);
            }
        }
        EventBus.getDefault().post(new BroadcastActionEvent(MyMessage.SP_PUSH_UPGRADE_STATUS, update));
    }


    public void parseSP_GIMBAL_EX_AXIS_STA(String msg) {
        String state = msg.substring(msg.lastIndexOf(":") + 1).trim();
        boolean triaxial = "1;".equals(state);
        EventBus.getDefault().post(new BroadcastActionEvent(MyMessage.SP_GIMBAL_EX_AXIS_STA, triaxial));
        SP_GET_DEVICE_VERSION();
    }

    private void parseSP_GET_CLASS_FILE_COUNT(String msg) {
        MediaFileLoad.getInstance().getFileClassCountResponse(msg);
    }

    private void parseSP_DEL_CLASS(String msg) {
        MediaFileLoad.getInstance().getDeletedFileClassResponse(msg);
    }

    private void parseSP_APP_ADD_FILE(String msg) {
        MediaFileLoad.getInstance().getAppAddFileResponse(msg);
    }

    private void parseSP_APP_PASSWORD_INFO(String msg) {
        String[] data = msg.split(";");
        String step = null;
        String ret = null;
        String password = null;
        String securityQ = null;
        String securityA = null;

        for (String string : data) {
            string = string.trim();
            String value = string.substring(string.lastIndexOf(":") + 1).trim();
            if (string.startsWith("step:")) {
                step = value;
            } else if (string.startsWith("ret:")) {
                ret = value;
            } else if (string.startsWith("password:")) {
                password = value;
            } else if (string.startsWith("securityQ:")) {
                securityQ = value;
            } else if (string.startsWith("securityA:")) {
                securityA = value;
            }
        }

        if (step == null) return;
        ResetAppPasswordModel resetAppPasswordModel = new ResetAppPasswordModel();

        resetAppPasswordModel.step = step;
        resetAppPasswordModel.ret = ret;
        resetAppPasswordModel.password = UtilFunction.decryptPassword(password);
        resetAppPasswordModel.securityQ = securityQ;
        resetAppPasswordModel.securityA = UtilFunction.decryptPassword(securityA);
        resetAppPasswordModel.changePassword = changePassword;

        EventBus.getDefault().post(resetAppPasswordModel);
    }


    public void parseSP_CAMERA_INFO(String msg) {
        String[] data = msg.split(";");
        String manufacturer = null;
        String model = null;
        String state = null;
        String storage = null;
        for (String string : data) {
            string = string.trim();
            String value = string.substring(string.lastIndexOf(":") + 1).trim();
            if (string.startsWith("manufacturer:")) {
                manufacturer = value;
            } else if (string.startsWith("model:")) {
                model = value;
            } else if (string.startsWith("state:")) {
                state = value;
            } else if (string.startsWith("storage:")) {
                storage = value;
            }
        }
        EventBus.getDefault().post(new BroadcastActionEvent(MyMessage.SP_CAMERA_INFO, manufacturer, model, state, storage));

    }

    public void parseSP_EXDEV_UPGRADE_START(String msg) {
        String[] data = msg.split(";");
        String devId = null;
        String ret = null;
        for (String string : data) {
            string = string.trim();
            String value = string.substring(string.lastIndexOf(":") + 1).trim();
            if (string.startsWith("devId:")) {
                devId = value;
            } else if (string.startsWith("ret:")) {
                ret = value;
            }
        }
        if (devId != null) {
            try {
                EventBus.getDefault().post(new BroadcastActionEvent(MyMessage.SP_EXDEV_UPGRADE_START, ret, Integer.parseInt(devId)));
            } catch (Exception e) {

            }
        }
    }

    public void parseSP_LOAD_EXDEV_FW_STATE(String msg) {
        String[] data = msg.split(";");
        String devId = null;
        String ret = null;
        for (String string : data) {
            string = string.trim();
            String value = string.substring(string.lastIndexOf(":") + 1).trim();
            if (string.startsWith("devId:")) {
                devId = value;
            } else if (string.startsWith("ret:")) {
                ret = value;
            }
        }
        if (devId != null) {
            try {
                EventBus.getDefault().post(new BroadcastActionEvent(MyMessage.SP_LOAD_EXDEV_FW_STATE, ret, Integer.parseInt(devId)));
            } catch (Exception e) {

            }
        }
    }

    public void parseSP_PUSH_EXDEV_STATUS(String msg) {
        String[] data = msg.split(";");
        String devId = null;
        String state = null;
        for (String string : data) {
            string = string.trim();
            String value = string.substring(string.lastIndexOf(":") + 1).trim();
            if (string.startsWith("devId:")) {
                devId = value;
            } else if (string.startsWith("state:")) {
                state = value;
            }
        }
        if (devId != null) {
            try {
                int stateI = Integer.parseInt(state);

                if (stateI == 0 || stateI == -1) {
                    SP_PUSH_EXDEV_STATUS(devId, stateI);
                }
                EventBus.getDefault().post(new BroadcastActionEvent(MyMessage.SP_PUSH_EXDEV_STATUS, Integer.parseInt(devId), stateI));
            } catch (Exception e) {

            }
        }
    }

    public void parse_SP_GET_ISP_CFG_FILE(String msg) {
        MediaFileLoad.getInstance().getConfigFileResponse(msg);
    }


    public void parse_SP_GET_LOG_LIST(String msg) {
        EventBus.getDefault().post(new BroadcastActionEvent(MyMessage.SP_GET_LOG_LIST, msg));
    }

    public void parse_SP_ERROR_CODE(String msg) {
        String errorCode = null;
        String[] data = msg.split(";");
        for (String string : data) {
            string = string.trim();
            String value = string.substring(string.lastIndexOf(":") + 1).trim();
            if (string.startsWith("errorCode:")) {
                errorCode = value;
            }
        }
        EventBus.getDefault().post(new BroadcastActionEvent(MyMessage.SP_ERROR_CODE, errorCode));
    }

    public void parseSP_GET_WIFI_BAND(String msg) {
        String[] data = msg.split(";");
        String band = null;
        for (String string : data) {
            string = string.trim();
            String value = string.substring(string.lastIndexOf(":") + 1).trim();
            if (string.startsWith("band:")) {
                band = value;
            }
        }
        wifi5G = "1".equals(band);
        EventBus.getDefault().post(new BroadcastActionEvent(MyMessage.SP_GET_WIFI_BAND, wifi5G));
    }

    public void parseSP_SET_WIFI_BAND(String msg) {
        String[] data = msg.split(";");
        String ret = null;
        for (String string : data) {
            string = string.trim();
            String value = string.substring(string.lastIndexOf(":") + 1).trim();
            if (string.startsWith("ret:")) {
                ret = value;
            }
        }
        EventBus.getDefault().post(new BroadcastActionEvent(MyMessage.SP_SET_WIFI_BAND, "0".equals(ret)));
    }

    public void parseSP_GET_WARNING_TONE_STATE(String msg) {
        String[] data = msg.split(";");
        String state = null;
        for (String string : data) {
            string = string.trim();
            String value = string.substring(string.lastIndexOf(":") + 1).trim();
            if (string.startsWith("state:")) {
                state = value;
            }
        }
        EventBus.getDefault().post(new BroadcastActionEvent(MyMessage.SP_GET_WARNING_TONE_STATE, "1".equals(state)));
    }

    public void parseSP_SET_WARNING_TONE_STATE(String msg) {
        String[] data = msg.split(";");
        String state = null;
        for (String string : data) {
            string = string.trim();
            String value = string.substring(string.lastIndexOf(":") + 1).trim();
            if (string.startsWith("state:")) {
                state = value;
            }
        }
        EventBus.getDefault().post(new BroadcastActionEvent(MyMessage.SP_SET_WARNING_TONE_STATE, "1".equals(state)));
    }

    public void parseSP_GET_CELLULAR_STATE(String msg) {
        String[] data = msg.split(";");
        String state = null;
        for (String string : data) {
            string = string.trim();
            String value = string.substring(string.lastIndexOf(":") + 1).trim();
            if (string.startsWith("ret:")) {
                state = value;
            }
        }
        EventBus.getDefault().post(new BroadcastActionEvent(MyMessage.SP_GET_CELLULAR_STATE, state));
    }

    public void parseSP_SET_TRACK_HALF_SPEED(String msg) {
        String[] data = msg.split(";");
        String state = null;
        for (String string : data) {
            string = string.trim();
            String value = string.substring(string.lastIndexOf(":") + 1).trim();
            if (string.startsWith("ret:")) {
                state = value;
            }
        }
        EventBus.getDefault().post(new BroadcastActionEvent(MyMessage.SP_SET_TRACK_HALF_SPEED, "0".equals(state)));
    }

    public void parseSP_SET_CAMERA_PREVIEW(String msg) {
        String[] data = msg.split(";");
        String state = null;
        String ret = null;
        for (String string : data) {
            string = string.trim();
            String value = string.substring(string.lastIndexOf(":") + 1).trim();
            if (string.startsWith("state:")) {
                state = value;
            } else if (string.startsWith("ret:")) {
                ret = value;
            }
        }
        EventBus.getDefault().post(new BroadcastActionEvent(MyMessage.SP_SET_CAMERA_PREVIEW, "0".equals(state), CMD.SUCCEED_FLAG.equals(ret)));
    }

    public void parseSP_GET_CAMERA_PREVIEW(String msg) {
        String[] data = msg.split(";");
        String state = null;

        for (String string : data) {
            string = string.trim();
            String value = string.substring(string.lastIndexOf(":") + 1).trim();
            if (string.startsWith("state:")) {
                state = value;
            }
        }
        EventBus.getDefault().post(new BroadcastActionEvent(MyMessage.SP_GET_CAMERA_PREVIEW, "0".equals(state)));
    }

    public void parseSP_SOCKET_CLIENT_TYPE(String msg) {
        String[] data = msg.split(";");
        String ret = null;

        for (String string : data) {
            string = string.trim();
            String value = string.substring(string.lastIndexOf(":") + 1).trim();
            if (string.startsWith("ret:")) {
                ret = value;
            }
        }
        EventBus.getDefault().post(new BroadcastActionEvent(MyMessage.SP_SOCKET_CLIENT_TYPE, CMD.SUCCEED_FLAG.equals(ret)));
    }

    private void parseSP_TEST(String msg) {
        EventBus.getDefault().post(new BroadcastActionEvent(MyMessage.SP_TEST, msg));
    }

    public void parseSP_SET_CELLULAR_APN(String msg) {
        String[] data = msg.split(";");
        String ret = null;

        for (String string : data) {
            string = string.trim();
            String value = string.substring(string.lastIndexOf(":") + 1).trim();
            if (string.startsWith("ret:")) {
                ret = value;
            }
        }
        EventBus.getDefault().post(new BroadcastActionEvent(MyMessage.SP_SET_CELLULAR_APN, CMD.SUCCEED_FLAG.equals(ret)));
    }

    public void parseSP_GET_CELLULAR_IMSI(String msg) {
        String[] data = msg.split(";");
        String imsi = null;
        String stat = null;

        for (String string : data) {
            string = string.trim();
            String value = string.substring(string.lastIndexOf(":") + 1).trim();
            if (string.startsWith("imsi:")) {
                imsi = value;
            } else if (string.startsWith("stat:")) {
                stat = value;
            }
        }
        EventBus.getDefault().post(new BroadcastActionEvent(MyMessage.SP_GET_CELLULAR_IMSI, imsi, stat));
    }

    public void parseSP_GET_TILT_STATE(String msg) {
        String[] data = msg.split(";");
        String state = null;

        for (String string : data) {
            string = string.trim();
            String value = string.substring(string.lastIndexOf(":") + 1).trim();
            if (string.startsWith("state:")) {
                state = value;
            }
        }
        EventBus.getDefault().post(new BroadcastActionEvent(MyMessage.SP_GET_TILT_STATE, state));
    }

    public void parseSP_SET_TILT_STATE(String msg) {
        String[] data = msg.split(";");
        String ret = null;

        for (String string : data) {
            string = string.trim();
            String value = string.substring(string.lastIndexOf(":") + 1).trim();
            if (string.startsWith("ret:")) {
                ret = value;
            }
        }
        EventBus.getDefault().post(new BroadcastActionEvent(MyMessage.SP_SET_TILT_STATE, CMD.SUCCEED_FLAG.equals(ret)));
    }

    public void parseSP_GET_DITHER_STATE(String msg) {
        String[] data = msg.split(";");
        String state = null;

        for (String string : data) {
            string = string.trim();
            String value = string.substring(string.lastIndexOf(":") + 1).trim();
            if (string.startsWith("state:")) {
                state = value;
            }
        }
        EventBus.getDefault().post(new BroadcastActionEvent(MyMessage.SP_GET_DITHER_STATE, state));
    }

    public void parseSP_SET_DITHER_STATE(String msg) {
        String[] data = msg.split(";");
        String ret = null;

        for (String string : data) {
            string = string.trim();
            String value = string.substring(string.lastIndexOf(":") + 1).trim();
            if (string.startsWith("ret:")) {
                ret = value;
            }
        }
        EventBus.getDefault().post(new BroadcastActionEvent(MyMessage.SP_SET_DITHER_STATE, CMD.SUCCEED_FLAG.equals(ret)));
    }

    public void parseSP_GET_CELLULAR_IMEI(String msg) {
        String[] data = msg.split(";");
        String imei = null;

        for (String string : data) {
            string = string.trim();
            String value = string.substring(string.lastIndexOf(":") + 1).trim();
            if (string.startsWith("imei:")) {
                imei = value;
            }
        }
        EventBus.getDefault().post(new BroadcastActionEvent(MyMessage.SP_GET_CELLULAR_IMEI, imei));
    }

    public void parseSP_GET_CELLULAR_HV(String msg) {
        String[] data = msg.split(";");
        String cellular = null;
        String cellhwver = null;
        String usbmode = null;

        for (String string : data) {
            string = string.trim();
            String value = string.substring(string.lastIndexOf(":") + 1).trim();
            if (string.startsWith("cellular:")) {
                cellular = value;
            } else if (string.startsWith("cellhwver:")) {
                cellhwver = value;
            } else if (string.startsWith("usbmode:")) {
                usbmode = value;
            }
        }
        EventBus.getDefault().post(new BroadcastActionEvent(MyMessage.SP_GET_CELLULAR_HV, cellular, cellhwver, usbmode));
    }

    public void parseSP_GET_IMG_FORMAT(String msg) {
        String[] data = msg.split(";");
        String format = null;

        for (String string : data) {
            string = string.trim();
            String value = string.substring(string.lastIndexOf(":") + 1).trim();
            if (string.startsWith("format:")) {
                format = value;
            }
        }
        EventBus.getDefault().post(new BroadcastActionEvent(MyMessage.SP_GET_IMG_FORMAT, format));
    }


    public void parseSP_SET_CELLULAR_COMUSB(String msg) {
        String[] data = msg.split(";");
        String ret = null;
        String usbmode = null;

        for (String string : data) {
            string = string.trim();
            String value = string.substring(string.lastIndexOf(":") + 1).trim();
            if (string.startsWith("ret:")) {
                ret = value;
            } else if (string.startsWith("usbmode:")) {
                usbmode = value;
            }
        }
        EventBus.getDefault().post(new BroadcastActionEvent(MyMessage.SP_SET_CELLULAR_COMUSB, usbmode, CMD.SUCCEED_FLAG.equals(ret)));
    }


    public void parseSP_GET_LIMIT_STATE(String msg) {
        String[] data = msg.split(";");
        String state = null;

        for (String string : data) {
            string = string.trim();
            String value = string.substring(string.lastIndexOf(":") + 1).trim();
            if (string.startsWith("state:")) {
                state = value;
            }
        }
        EventBus.getDefault().post(new BroadcastActionEvent(MyMessage.SP_GET_LIMIT_STATE, state));
    }

    public void parseSP_GET_SETTLING_TIME(String msg) {
        String[] data = msg.split(";");
        String time = null;

        for (String string : data) {
            string = string.trim();
            String value = string.substring(string.lastIndexOf(":") + 1).trim();
            if (string.startsWith("time:")) {
                time = value;
            }
        }
        EventBus.getDefault().post(new BroadcastActionEvent(MyMessage.SP_GET_SETTLING_TIME, time));
    }

    public void parseSP_SET_SETTLING_TIME(String msg) {
        String[] data = msg.split(";");
        String ret = null;

        for (String string : data) {
            string = string.trim();
            String value = string.substring(string.lastIndexOf(":") + 1).trim();
            if (string.startsWith("ret:")) {
                ret = value;
            }
        }
        EventBus.getDefault().post(new BroadcastActionEvent(MyMessage.SP_SET_SETTLING_TIME, CMD.SUCCEED_FLAG.equals(ret)));
    }

    public void parseSP_GET_CONTROL_MODE(String msg) {
        String[] data = msg.split(";");
        String mode = null;

        for (String string : data) {
            string = string.trim();
            String value = string.substring(string.lastIndexOf(":") + 1).trim();
            if (string.startsWith("mode:")) {
                mode = value;
            }
        }
        EventBus.getDefault().post(new BroadcastActionEvent(MyMessage.SP_GET_CONTROL_MODE, mode));
    }

    public void parseSP_SET_CONTROL_MODE(String msg) {
        String[] data = msg.split(";");
        String ret = null;

        for (String string : data) {
            string = string.trim();
            String value = string.substring(string.lastIndexOf(":") + 1).trim();
            if (string.startsWith("ret:")) {
                ret = value;
            }
        }
        EventBus.getDefault().post(new BroadcastActionEvent(MyMessage.SP_SET_CONTROL_MODE, ret));
    }

    public void parseSP_GET_EX_TIME(String msg) {
        String[] data = msg.split(";");
        String exTime = null;

        for (String string : data) {
            string = string.trim();
            String value = string.substring(string.lastIndexOf(":") + 1).trim();
            if (string.startsWith("ExTime:")) {
                exTime = value;
            }
        }
        EventBus.getDefault().post(new BroadcastActionEvent(MyMessage.SP_GET_EX_TIME, exTime));
    }

    public void parseSP_SET_EX_TIME(String msg) {
        String[] data = msg.split(";");
        String ret = null;

        for (String string : data) {
            string = string.trim();
            String value = string.substring(string.lastIndexOf(":") + 1).trim();
            if (string.startsWith("ret:")) {
                ret = value;
            }
        }
        EventBus.getDefault().post(new BroadcastActionEvent(MyMessage.SP_SET_EX_TIME, CMD.SUCCEED_FLAG.equals(ret)));
    }

    public void parseSP_GET_CAMERA_DIR(String msg) {
        String[] data = msg.split(";");
        String dir = null;

        for (String string : data) {
            string = string.trim();
            String value = string.substring(string.lastIndexOf(":") + 1).trim();
            if (string.startsWith("dir:")) {
                dir = value;
            }
        }

        EventBus.getDefault().post(new BroadcastActionEvent(MyMessage.SP_GET_CAMERA_DIR, dir));
    }

    public void parseSP_SET_CAMERA_DIR(String msg) {
        String[] data = msg.split(";");
        String ret = null;

        for (String string : data) {
            string = string.trim();
            String value = string.substring(string.lastIndexOf(":") + 1).trim();
            if (string.startsWith("ret:")) {
                ret = value;
            }
        }
        EventBus.getDefault().post(new BroadcastActionEvent(MyMessage.SP_SET_CAMERA_DIR, CMD.SUCCEED_FLAG.equals(ret)));
    }

    public void parseSP_GET_AUTO_OFF_SW(String msg) {
        String[] data = msg.split(";");
        String sw = null;

        for (String string : data) {
            string = string.trim();
            String value = string.substring(string.lastIndexOf(":") + 1).trim();
            if (string.startsWith("sw:")) {
                sw = value;
            }
        }

        EventBus.getDefault().post(new BroadcastActionEvent(MyMessage.SP_GET_AUTO_OFF_SW, sw));
    }

    public void parseSP_SET_AUTO_OFF_SW(String msg) {
        String[] data = msg.split(";");
        String ret = null;

        for (String string : data) {
            string = string.trim();
            String value = string.substring(string.lastIndexOf(":") + 1).trim();
            if (string.startsWith("ret:")) {
                ret = value;
            }
        }

        EventBus.getDefault().post(new BroadcastActionEvent(MyMessage.SP_SET_AUTO_OFF_SW, ret));
    }

    public void parseSP_GET_AUTO_LEVEL_EN(String msg) {
        String[] data = msg.split(";");
        String en = null;

        for (String string : data) {
            string = string.trim();
            String value = string.substring(string.lastIndexOf(":") + 1).trim();
            if (string.startsWith("en:")) {
                en = value;
            }
        }

        EventBus.getDefault().post(new BroadcastActionEvent(MyMessage.SP_GET_AUTO_LEVEL_EN, "1".equals(en)));
    }

    public void parseSP_SET_AUTO_LEVEL_EN(String msg) {
        String[] data = msg.split(";");
        String ret = null;

        for (String string : data) {
            string = string.trim();
            String value = string.substring(string.lastIndexOf(":") + 1).trim();
            if (string.startsWith("ret:")) {
                ret = value;
            }
        }

        EventBus.getDefault().post(new BroadcastActionEvent(MyMessage.SP_SET_AUTO_LEVEL_EN, ret));
    }

    public void parseSP_SET_AUTO_LEVEL_STATE(String msg) {
        String[] data = msg.split(";");
        String ret = null;

        for (String string : data) {
            string = string.trim();
            String value = string.substring(string.lastIndexOf(":") + 1).trim();
            if (string.startsWith("ret:")) {
                ret = value;
            }
        }

        EventBus.getDefault().post(new BroadcastActionEvent(MyMessage.SP_SET_AUTO_LEVEL_STATE, ret));
    }

    public void parseSP_GET_HDMI_SUPPORT(String msg) {
        String[] data = msg.split(";");
        String hdmi = null;

        for (String string : data) {
            string = string.trim();
            String value = string.substring(string.lastIndexOf(":") + 1).trim();
            if (string.startsWith("hdmi:")) {
                hdmi = value;
            }
        }

        EventBus.getDefault().post(new BroadcastActionEvent(MyMessage.SP_GET_HDMI_SUPPORT, "1".equals(hdmi)));
    }

    public void parseSP_SET_HDMI_STATE(String msg) {
        String[] data = msg.split(";");
        String ret = null;

        for (String string : data) {
            string = string.trim();
            String value = string.substring(string.lastIndexOf(":") + 1).trim();
            if (string.startsWith("ret:")) {
                ret = value;
            }
        }

        EventBus.getDefault().post(new BroadcastActionEvent(MyMessage.SP_SET_HDMI_STATE, CMD.SUCCEED_FLAG.equals(ret)));
    }

    public void parseSP_REBOOT_CONFIRM_MODE(String msg) {
        String[] data = msg.split(";");
        String ret = null;

        for (String string : data) {
            string = string.trim();
            String value = string.substring(string.lastIndexOf(":") + 1).trim();
            if (string.startsWith("ret:")) {
                ret = value;
            }
        }

        EventBus.getDefault().post(new BroadcastActionEvent(MyMessage.SP_REBOOT_CONFIRM_MODE, CMD.SUCCEED_FLAG.equals(ret)));
    }

    public void parseSP_GET_HDMI_STATE(String msg) {
        String[] data = msg.split(";");
        String state = null;

        for (String string : data) {
            string = string.trim();
            String value = string.substring(string.lastIndexOf(":") + 1).trim();
            if (string.startsWith("state:")) {
                state = value;
            }
        }

        EventBus.getDefault().post(new BroadcastActionEvent(MyMessage.SP_GET_HDMI_STATE, state));
    }

    public void parseSP_PUSH_HDMI_STREAM_STATE(String msg) {
        String[] data = msg.split(";");
        String state = null;

        for (String string : data) {
            string = string.trim();
            String value = string.substring(string.lastIndexOf(":") + 1).trim();
            if (string.startsWith("state:")) {
                state = value;
            }
        }

        EventBus.getDefault().post(new BroadcastActionEvent(MyMessage.SP_PUSH_HDMI_STREAM_STATE, state));
    }


}
