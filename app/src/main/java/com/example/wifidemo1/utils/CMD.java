package com.example.wifidemo1.utils;

/**
 * Created by HLW on 2019/7/24.
 */

public class CMD {
    public static final String initStar = "0";
    public static final String trackingStar = "1";
    public static final String pauseStar = "2";
    public static final String unAlignStar = "3";

    public static final String DEFAULT_PASSWORD = "0000";
    public static final int FILE_TYPE_ALL = 0;
    public static final int FILE_TYPE_NORMAL = 1;
    public static final int FILE_TYPE_LAPSE = 2;
    public static final int FILE_TYPE_FOCUS = 3;
    public static final int FILE_TYPE_PAN = 4;
    public static final int FILE_TYPE_SUN = 5;
    public static final int FILE_TYPE_HDR = 6;
    public static final int FILE_TYPE_STAR_SKY_STACK = 7;

    public static final String NORMAL = "normal";
    public static final String LAPSE = "Lapse";
    public static final String FOCUS_STACK = "focusStack";
    public static final String PANORAMA = "panorama";
    public static final String SUN = "sun";
    public static final String HDR = "HDR";
    public static final String STAR_SKY_STACK = "starSkyStack";

    public static final String recordStart = "recordStart";
    public static final String recordStop = "recordStop";
    public static final String recordComplete = "recordComplete";
    public static final String recordCancle = "recordCancle";
    public static final String recordRemainNumber = "recordRemainNumber";

    public static final String recordSendPointIndex = "recordSendPointIndex";
    public static final String recordSendPointEnd = "recordSendPointEnd";
    public static final String recordSetAppointmentTime = "recordSetAppointmentTime";
    public static final String recordAppointmentState = "recordAppointmentState";


    public static final String BEING_PROCESSED_FLAG = "1";//处理中
    public static final String TIME_OUT_FLAG = "-100000";// 超时

    public static final String SUCCEED_FLAG = "0";


    public static final String PHOTO_SEND_START = "1";


    public static final String WifiContainPolaris = "polaris_";


//    public static final String CMDIP = "47.101.174.70";
//    public static final int PORT = 6341;
//    public static final String CMDPREVIEW_ADDRESS = "http://stream623c06.petycare.cn:8088/?action=stream";
//

    public static final String EMPTY_CONTENT = "-100";


    public static final int SP_SET_ISO = 258;//设置ISO 指令内容：ISO  发：iso:%d;  收：iso:%d;ret:%d;
    public static final int SP_SET_WB = 259;//设置白平衡 指令内容：白平衡具体值 发：wb:%d;;  收：wb:%d;ret:%d;
    public static final int SP_SET_EV = 260;//设置曝光补偿 指令内容：焦点值 发：ev:%d;   收：ev:%d;ret:%d;
    public static final int SP_SET_SHUTTER = 261;//设置快门时间 指令内容：快门时间具体值 发：s:%d;  收：s:%d;ret:%d;
    public static final int SP_SET_FOCUS = 262;//设置对焦 发：mode:%d;f:%d;  收：model:%d;f:%d;ret:%d;
    public static final int SP_SET_FNUM = 276;

    public static final int SP_GET_ISO_INFO = 265;// 发：""  收：info:%s;
    public static final int SP_GET_WB_INFO = 266;// 发：""  收：info:%s;
    public static final int SP_GET_EV_INFO = 267;// 发：""  收：info:%s;
    public static final int SP_GET_SHUTTER_INFO = 268;// 发：""  收：info:%s;
    public static final int SP_GET_FNUM_INFO = 275;

    public static final int SP_SET_PHOTO_RECORD_STATUS = 264;// 发：state:%d;(0=停止；1=开始)  收：state:1=处理中；0=拍照完成；-1=失败
    public static final int SP_SET_VIDEO_RECORD_STATUS = 263;// 发：state:%d;(0=停止；1=开始)  收：state:1=处理中；0=拍照完成；-1=失败
    public static final int SP_FOCUS_STACK = 270;// 焦点堆叠
    public static final int SP_PANORAMIC = 271;// 全景拍摄
    //public static final int SP_SKY_PANORAMIC = 290;// 星空全景拍摄
    public static final int SP_SUN_SHOT = 277;//日出日落拍摄指令表
    public static final int SP_HDR = 280;//HDR
    public static final int SP_GET_IMG_FORMAT = 282; //获取图片格式
    public static final int SP_PLC = 283;//预编译拍摄指令表
    public static final int SP_DELAY_SHOT = 272;//延迟拍摄指令表
    public static final int SP_REMOVE_PEOPLE_SHOT = 289;//人像移除

    public static final int SP_PUSH_MODE_STATE = 284;// 获取云台当前状态
    public static final int SP_SET_MODE_STATE = 285;// 设置云台当前状态拍摄模式
    public static final int SP_GET_CONTROL_MODE = 296; //获取控制模式
    public static final int SP_SET_CONTROL_MODE = 297; //设置控制模式
    public static final int SP_GET_EX_TIME = 298; //获取曝光时间
    public static final int SP_SET_EX_TIME = 299; //设置曝光时间

    public static final int SP_GET_HDMI_SUPPORT = 300; //获取是否支持hmdi
    public static final int SP_SET_HDMI_STATE = 301; //设置hdmi状态
    public static final int SP_REBOOT_CONFIRM_MODE = 302; //
    public static final int SP_GET_HDMI_STATE = 303;
    public static final int SP_PUSH_HDMI_STREAM_STATE = 304;


    public static final int SP_GIMBAL_HADJ_SPEED = 513;// speed:%d; Speed【-2000，2000】云台水平方向调整,速度模式
    public static final int SP_GIMBAL_VADJ_SPEED = 514;// speed:%d; Speed【-2000，2000】云台俯仰方向调整,速度模式；
    public static final int SP_GIMBAL_HADJ_ANGLE = 515;//angle:%f; 云台水平方向调整,角度模285式；
    public static final int SP_GIMBAL_VADJ_ANGLE = 516;//angle:%f; 云台俯仰方向调整,角度模式；
    public static final int SP_GET_GIMBAL_POS = 517;//yaw:%f;pitch:%f;roll:%f;
    public static final int SP_PUSH_ROTATE_VECTOR = 518;//val w:%0.7f;x:%0.7f;y:%0.7f;z:%0.7f;w:%0.7f;x:%0.7f;y:%0.7f;z:%0.7f;前半段为安卓数据；后半段为IOS数据
    public static final int SP_SET_GOTO_AU_STATE = 519;//yaw:%0.3f;pitch%0.3f;  APP推送天体方位
    public static final int SP_SET_AHRS_STATE = 520;//state:%d; ret:%d; 姿态导航状态开关；state:0=关；1=开； APP用到姿态信息时，打开。不用时关闭；
    public static final int SP_GIMBAL_RADJ_SPEED = 521;// speed:%d; Speed【-2000，2000】云台水平方向调整,速度模式
    public static final int SP_GIMBAL_RADJ_ANGLE = 522;///angle:%f; 云台三轴方向调整,角度模式；
    public static final int SP_GIMBAL_POS_RESET = 523;// axis:%d; 云台回中 1水平2俯仰3第三轴
    public static final int SP_GIMBAL_EX_AXIS_STA = 524;// state:%d;  1:第三轴连接上 0 第三轴没连接
    public static final int SP_TEST = 526;//测试指令
    public static final int SP_SET_GIMBAL_POS = 535;// //yaw:%f;pitch:%f;roll:%f;
    public static final int SP_GET_TILT_STATE = 537; //获取水平状态
    public static final int SP_SET_TILT_STATE = 538; //设置水平状态
    public static final int SP_GET_DITHER_STATE = 539; //获取抖动状态
    public static final int SP_SET_DITHER_STATE = 540; //设置抖动状态
    public static final int SP_SET_LIMIT_STATE = 542; //设置云台开放角度限位
    public static final int SP_GET_LIMIT_STATE = 541; //北极星向App推送该指令回复限位开闭状态
    public static final int SP_GET_SETTLING_TIME = 543; //获取稳定时间
    public static final int SP_SET_SETTLING_TIME = 544; //设置稳定时间
    public static final int SP_GET_CAMERA_DIR = 545; //拍摄方向
    public static final int SP_SET_CAMERA_DIR = 546; //拍摄方向








    public static final int SP_SET_YAW = 527;//compass:%f;

    public static final int SP_CALIBRATE_START = 530;//标定行星
    public static final int SP_SET_TRACK_AU_STATE = 531;// 开始抵消自转追星
    public static final int SP_YAW_KEY = 532;
    public static final int SP_PITCH_KEY = 533;
    public static final int SP_ROLL_KEY = 534;
    public static final int KEY_PLUS = 0;
    public static final int KEY_MINUS = 1;
    public static final int STATE_UP = 0;
    public static final int STATE_DOWN = 1;
    public static final int STATE_LONG_CLICK = 2;


    public static final int SP_GET_FILE_COUNT = 770;//normal:%d;lapse:%d;focus:%d;pan:%d;sun:%d;
    public static final int SP_GET_FILE_LIST = 771;//send:start:%d;end:%d;  respon:path:%s;size:%d;cTime:%s;duration:%d;type:%d;
    public static final int SP_DEL_FILE = 772;//send :type:%d;path:%s; respon:path:%s;ret:%d;
    public static final int SP_ADD_FILE = 773;//path:%s;size:%d;cTime:%s;duration:%d;type:%d;

    public static final int SP_SD_FORMAT = 774;//ret:%d; 格式化SD卡,ret:0=成功，1=失败；
    // 备注： status = 存储卡挂载状态 0=未挂载 1=挂载； totalspace = 总空间  单位 unit bytes freespace = 剩余空间  单位 unit bytes usespace = 使用空间  单位 unit bytes
    public static final int SP_GET_SD_INFO = 775;    //status:%d;totalspace:%lld;freespace:%lld;usespace:%lld;
    public static final int SP_PUSH_SD_INFO = 776;//totalspace:%lld;freespace:%lld;usespace:%lld;

    /*备注： SD卡拔出 =  1； SD卡插入 = 2； SD已挂载 = 3； SD挂载失败 = 4； 没有SD卡 =  5； SD卡错误 =  6； SD卡错误,请格式化SD卡 = 7； 
    正在准备SD卡 = 8； SD准备完成，可以使用 =  9； SD卡满 = 10； 请插拔SD卡  = 11； 请更换SD卡  = 12；*/
    public static final int SP_PUSH_SD_HINT_ID = 777;//hintId:%d;
    public static final int SP_GET_BAT_STATE = 778;//capacity:%d;charge:%d;  备注：电量[0,100];charge：0=不充电，1=充电中；2=充满
    public static final int SP_PUSH_BAT_STATE = 779;//capacity:%d;charge:%d; 状态有变化推送一次
    public static final int SP_GET_DEVICE_VERSION = 780;//hw:%s;sw:%s; exAxis:%s;;  硬件版本号，软件版本号；
    public static final int SP_GET_SYSTEM_TIME = 781;//date:%s;time:%s; 备注：e.g.(date:2019-01-01;time:20:01)
    public static final int SP_SET_SYSTEM_TIME = 782;//date:%s;time:%s;zone:%d;   e.g.(date:2019-01-01;time:20:01;zone:+3600;)
    public static final int SP_SET_UPGRADE_START = 783;//
    public static final int SP_LOAD_UPGRADE_FW_STATE = 784;//state:%d; 备注：0=固件上传完成；1=固件上传中；-1=固件上传失败；
    public static final int SP_PUSH_UPGRADE_STATUS = 785;//state:%d;备注：0=成功；1=升级中；（-1或error code）= 失败
    public static final int SP_APP_ADD_FILE = 788;//type:%d;path:%s;

    //type:
    //1=null;
    //2=lapse;
    //3=focus;
    //4=pan;
    //5=sun;
    //6=hdr;
    //7=star_sky_stack
    //class=组号；如class=1,获取lapse/class_1/目录下文件数量
    public static final int SP_GET_CLASS_FILE_COUNT = 786;//send: type:%d;class:%d;  respon: type:%d;class:%d;count:%d;
    public static final int SP_DEL_CLASS = 787;//send: type:%d;class:%d;start:%d;end:%d;  respon: type:%d;class:%d;path:%s;size:%d;cTime:%s;duration:%d;


    public static final int SP_APP_PASSWORD_INFO = 790;//password:%s;
    public static final int SP_EXDEV_UPGRADE_START = 791;
    public static final int SP_LOAD_EXDEV_FW_STATE = 792;
    public static final int SP_PUSH_EXDEV_STATUS = 793;
    public static final int SP_CAMERA_INFO = 286;

    public static final int SP_GET_ISP_CFG_FILE = 796;//send: type:%d;class:%d; respon: type:%d;class:%d;count:%d;index:%d;path:%s;
    public static final int SP_GET_LOG_LIST = 798;//ret:%d;count%d;index;%path:%s;
    public static final int SP_ERROR_CODE = 797;//ret:%d;count%d;index;%path:%s;

    public static final int SP_GET_WIFI_BAND = 802;
    public static final int SP_SET_WIFI_BAND = 803;
    public static final int SP_GET_WARNING_TONE_STATE = 804;
    public static final int SP_SET_WARNING_TONE_STATE = 805;
    public static final int SP_GET_CELLULAR_STATE = 799;
    public static final int SP_SET_TRACK_HALF_SPEED = 536;//0=全速，1=半速
    public static final int SP_SET_CAMERA_PREVIEW = 291;//设置相机预览状态；state:1=打开,0=关闭；
    public static final int SP_GET_CAMERA_PREVIEW = 292;//获取相机预览状态;state:1=打开,0=关闭；
    public static final int SP_SOCKET_CLIENT_TYPE = 808;
    public static final int SP_SET_CELLULAR_APN = 809;//设置蜂窝网络apn SP_GET_CELLULAR_IMSI
    public static final int SP_GET_CELLULAR_IMSI = 811;//获取蜂窝网络IMSI
    public static final int SP_GET_CELLULAR_IMEI = 812; //获取北极星IMEI码
    public static final int SP_SET_CELLULAR_COMUSB = 813; //设置北极星USB模式
    public static final int SP_GET_CELLULAR_HV = 814; //获取北极星硬件版本
    public static final int SP_GET_AUTO_OFF_SW = 815; //获取自动关机状态
    public static final int SP_SET_AUTO_OFF_SW = 816; //设置自动关机状态
    public static final int SP_GET_AUTO_LEVEL_EN = 547; //获取自动水平功能开关状态
    public static final int SP_SET_AUTO_LEVEL_EN = 548; //设置自动水平功能开关状态
    public static final int SP_SET_AUTO_LEVEL_STATE = 549; //设置自动水平



}
