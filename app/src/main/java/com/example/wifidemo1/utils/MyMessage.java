package com.example.wifidemo1.utils;

/**
 * Created by HLW on 2019/7/10.
 */

public class MyMessage {

    public static final String ACTION_WIFI_CLOSE = "action_wifi_close";
    public static final String ACTION_WIFI_OPEN = "action_wifi_open";
    public static final String ACTION_WIFI_OPENING = "action_wifi_opening";
    public static final String ACTION_WIFI_CONNETING_FLAG = "action_wifi_conneting_flag";

    public static final String ACTION_HINT_CLOSE_WIFI_AP = "action_hint_close_wifi_ap";
    public static final String ACTION_OPEN_WIFI_ACTIVITY = "action_open_wifi_activity";
    public static final String ACTION_UPDATE_LOCAL_DEVICE_LIST = "action_update_local_device_list";

    public static final String ACTION_REMOTE_DEVICE_STATE = "action_remote_device_state";//1：设备在线，2设备不在线，3网络异常

    public static final String ACTION_APPLICATION_IS_ONFORGROUND = "action_application_is_onforground";//  应用是否在前台

    public static final String ACTION_PRODUCT_WIFI_NETWORK_AVAILABLE = "action_product_wifi_network_available";
    public static final String ACTION_PRODUCT_WIFI_NETWORK_LOST = "action_wifi_network_lost";


    public static final String ACTION_WIFI_ON_UNAVAILABLE = "action_wifi_on_unavailable";
    public static final String ACTION_NETWORK_AVAILABLE = "action_network_available";
    public static final String ACTION_NETWORK_LOST = "action_network_lost";

    public static final String ACTION_SP_ADD_FILE = "action_sp_add_file";

    public static final String ACTION_SP_CALIBRATE_START = "action_sp_calibrate_start";

    public static final String SP_SET_YAW = "sp_set_yaw";
    public static final String SP_CAMERA_INFO = "sp_camera_info";

    public static final String ACTION_REQUS_GPS = "action_requs_gps";
    public static final String ACTION_WIFI_CONNECT_ERROR = "action_wifi_connect_error";
    public static final String ACTION_UPDATE_CANCEL_WAITING = "action_update_cancel_waiting";

    public static final String ACTION_CANCLE_SYNCRONIZ = "action_cancle_syncroniz";//取消同步

    public static final String ACTION_DELETE_SINGLE_YUNTAI_FILE_COMPLETE = "action_delete_single_yuntai_file_complete";//删除单个云台文件

    public static final String ACTION_LOACAL_FILE_LOAD_COMPLETE = "action_loacal_file_load_complete";//本地文件加载完成
    public static final String ACTION_YUNTAI_FILE_LOAD_COMPLETE = "action_yuntai_file_load_complete";//云台文件加载完成
    public static final String ACTION_YUNTAI_CONFIG_FILE_LOAD_COMPLETE = "action_yuntai_config_file_load_complete";//云台配置文件加载完成


    public static final String ACTION_SP_PUSH_SD_HINT_ID = "action_sp_push_sd_hint_id";//云台主动推送SD提示ID


    public static final String ACTION_SOCKET_CONNECT = "action_socket_connect";


    public static final String ACTION_YUNTAI_DOWNLOAD_UI_CHANGE = "action_yuntai_download_ui_change"; //顶部下载进度UI改变 p1:下载总数 p2:下载完成的数量 p3:是否是初始化


    public static final String ACTION_YUNTAI_DOWNLOAD_MEDIA_UPDATE = "action_yuntai_download_media_update"; //顶部下载进度UI改变 p1:下载总数 p2:下载完成的数量 p3:是否是初始化


    public static final String ACTION_START_GET_FILE_COUNT_RETURN = "action_start_get_file_count_return";

    public static final String ACTION_PHONE_IS_OUT_OF_MEMORY = "action_phone_is_out_of_memory";


    public static final String ACTION_DEVICE_LOCATION_CHANG = "action_device_location_chang";//gps位置变化，包括手动输入

    public static final String ACTION_LOCATION_CHANG_IN_DEVICE = "action_location_chang_in_device"; //gps位置变化，不包括手动输入

    public static final String ACTION_DOWNLOAD_COMPLETED = "action_download_completed";

    public static final String ACTION_PANORAMA_COMPLETED = "action_panorama_completed";

    public static final String ACTION_PRO_PANO_SINGLE_POINT_PIC_COUNT_CHANGED = "action_pro_pano_single_point_pic_count_changed";

    public static final String ACTION_NOR_PANO_SINGLE_POINT_PIC_COUNT_CHANGED = "action_nor_pano_single_point_pic_count_changed";

    public static final String ACTION_SHOW_SKY_STAR_HELP_Dialog = "sp_set_cellular_comusb";

    public static final String GPS_ON_CHANGE = "gps_on_change";

    public static final String ACTION_START_SCANNING = "action_start_scanning";

    public static final String ACTION_STOP_SCANNING = "action_stop_scanning";


    public static final String SP_SET_FOCUS = "sp_set_focus";
    public static final String SP_SET_ISO = "sp_set_iso";
    public static final String SP_SET_WB = "sp_set_wb";
    public static final String SP_SET_EV = "sp_set_ev";
    public static final String SP_SET_SHUTTER = "sp_set_shutter";
    public static final String SP_SET_FNUM = "sp_set_fnum";


    public static final String SP_GET_ISO_INFO = "sp_get_iso_info";
    public static final String SP_GET_WB_INFO = "sp_get_wb_info";
    public static final String SP_GET_EV_INFO = "sp_get_ev_info";
    public static final String SP_GET_SHUTTER_INFO = "sp_get_shutter_info";
    public static final String SP_GET_FNUM_INFO = "sp_get_fnum_info";

    public static final String SP_SET_PHOTO_RECORD_STATUS = "sp_set_photo_record_status";

    public static final String SP_GET_GIMBAL_POS = "sp_get_gimbal_pos";
    public static final String SP_SET_GIMBAL_POS = "sp_set_gimbal_pos";
    public static final String SP_SET_AHRS_STATE = "sp_set_ahrs_state";

    public static final String SP_SD_FORMAT = "sp_sd_format";

    public static final String SP_SET_UPGRADE_START = "sp_set_upgrade_start";
    public static final String SP_LOAD_UPGRADE_FW_STATE = "sp_load_upgrade_fw_state";
    public static final String SP_PUSH_UPGRADE_STATUS = "sp_push_upgrade_status";
    public static final String SP_GIMBAL_EX_AXIS_STA = "sp_gimbal_ex_axis_sta";
    public static final String parseSP_SET_GOTO_AU_STATE = "parsesp_set_goto_au_state";
    public static final String SP_SET_TRACK_AU_STATE = "sp_set_track_au_state";
    public static final String SP_EXDEV_UPGRADE_START = "sp_exdev_upgrade_start";
    public static final String SP_LOAD_EXDEV_FW_STATE = "sp_load_exdev_fw_state";
    public static final String SP_PUSH_EXDEV_STATUS = "sp_push_exdev_status";
    public static final String SET_MODE_STATE = "set_mode_state";
    public static final String SP_GET_LOG_LIST = "sp_get_log_list";
    public static final String SP_ERROR_CODE = "sp_error_code";

    public static final String MEDIA_FILE_SYNTHETIC_COMPLETED = "media_file_synthetic_completed";

    public static final String SP_FOCUS_STACK_PREVIEW_MSG = "sp_focus_stack_preview_msg";

    public static final String SP_GET_WIFI_BAND = "sp_get_wifi_band";
    public static final String SP_SET_WIFI_BAND = "sp_set_wifi_band";
    public static final String SP_GET_WARNING_TONE_STATE = "sp_get_warning_tone_state";
    public static final String SP_SET_WARNING_TONE_STATE = "sp_set_warning_tone_state";


    public static final String SP_GET_CELLULAR_STATE = "sp_get_cellular_state";
    public static final String SP_SET_TRACK_HALF_SPEED = "sp_set_track_half_speed";
    public static final String SP_SET_CAMERA_PREVIEW = "sp_set_camera_preview";
    public static final String SP_GET_CAMERA_PREVIEW = "sp_get_camera_preview";
    public static final String SP_SOCKET_CLIENT_TYPE = "sp_socket_client_type";
    public static final String SP_SET_CELLULAR_ON = "sp_set_cellular_on";
    public static final String SP_SET_CELLULAR_OFF = "sp_set_cellular_off";

    public static final String SP_TEST = "sp_test";
    public static final String SP_TEST_CELLULAR = "sp_test_cellular";
    public static final String SP_TEST_HDMI = "sp_test_hdmi";

    public static final String SP_SET_CELLULAR_APN = "sp_set_cellular_apn";
    public static final String SP_GET_CELLULAR_IMSI = "sp_get_cellular_imsi";
    public static final String SP_GET_TILT_STATE = "sp_get_tilt_state";
    public static final String SP_SET_TILT_STATE = "sp_set_tilt_state";
    public static final String SP_GET_DITHER_STATE = "sp_get_dither_state";
    public static final String SP_SET_DITHER_STATE = "sp_set_dither_state";
    public static final String SP_GET_CELLULAR_IMEI = "sp_get_cellular_imei";
    public static final String SP_GET_CELLULAR_HV = "sp_get_cellular_hv";
    public static final String SP_SET_CELLULAR_COMUSB = "sp_set_cellular_comusb";
    public static final String SP_DELAY_SHOT_END_BACK = "sp_delay_shot_end_back";
    public static final String SP_DELAY_SHOT_GET_END_BACK_SETTING_STATE = "sp_delay_shot_get_end_back_setting_state";
    public static final String SP_GET_IMG_FORMAT = "sp_get_img_format";
    public static final String SP_GET_LIMIT_STATE = "sp_get_limit_state";
    public static final String SP_DELAY_SHOT_START = "sp_delay_shot_start";
    public static final String SP_DELAY_SHOT_SEND_POINT = "sp_delay_shot_send_point";
    public static final String SP_DELAY_SHOT_SEND_POINT_END = "sp_delay_shot_send_point_end";
    public static final String SP_DELAY_SHOT_CANCEL = "sp_delay_shot_cancel";
    public static final String SP_DELAY_SHOT_SHOOTING_COMPLETE = "sp_delay_shot_shooting_complete";
    public static final String SP_GET_SETTLING_TIME = "sp_get_settling_time";
    public static final String SP_SET_SETTLING_TIME = "sp_set_settling_time";
    public static final String SP_GET_CONTROL_MODE = "sp_get_control_mode";
    public static final String SP_SET_CONTROL_MODE = "sp_set_control_mode";
    public static final String SP_GET_EX_TIME = "sp_get_ex_time";
    public static final String SP_SET_EX_TIME = "sp_set_ex_time";
    public static final String SP_PANORAMA_SET_BACK_START_POINT = "sp_panorama_set_back_start_point";
    public static final String SP_PANORAMA_GET_BACK_START_POINT_STATE = "sp_panorama_get_back_start_point_state";
    public static final String SP_SET_CAMERA_DIR = "sp_set_camera_dir";
    public static final String SP_GET_CAMERA_DIR = "sp_get_camera_dir";
    public static final String SP_GET_AUTO_OFF_SW = "sp_get_auto_off_sw";
    public static final String SP_SET_AUTO_OFF_SW = "sp_set_auto_off_sw";
    public static final String SP_GET_AUTO_LEVEL_EN = "sp_get_auto_level_en";
    public static final String SP_SET_AUTO_LEVEL_EN = "sp_set_auto_level_en";
    public static final String SP_SET_AUTO_LEVEL_STATE = "sp_set_auto_level_state";
    public static final String SP_GET_HDMI_SUPPORT = "sp_get_hdmi_support";
    public static final String SP_SET_HDMI_STATE = "sp_set_hdmi_state";
    public static final String SP_REBOOT_CONFIRM_MODE = "sp_reboot_confirm_mode";
    public static final String SP_GET_HDMI_STATE = "sp_get_hdmi_state";
    public static final String SP_PUSH_HDMI_STREAM_STATE = "sp_push_hdmi_stream_state";
}
