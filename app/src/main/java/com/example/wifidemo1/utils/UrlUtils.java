package com.example.wifidemo1.utils;

/**
 * Created by Administrator on 2017/1/13.
 */

public class UrlUtils {

    /**
     * 查询app最新版本
     */
    public static final String CHECK_LATEST_APP_VERSION = "https://www.snoppa.com/snoppa/user/auth.do?cmd=queryappversion";
    public static final String APP_PRODUCT_ID = "110301000000";
    public static final String CHECK_DEVICE = "http://service.benro-service.com/frp/queryurl.do";
    public static final String GET_APN_INFO = "http://service.benro-service.com/server/apn/query.do";
    public static final String APP_PROMOTE = "https://service.snoppa.com/app/benro/api/ad-page-info/polaris/query.do";

    /**
     * 查询云台固件最新版本
     */
    public static final String CHECK_LATEST_FIRMWARE_VERSION = "https://service.snoppa.com/snoppa/user/auth.do?cmd=queryromversion";
    public static final String FIRMWARE_PRODUCT_ID = "110301200000";
    public static final String POLARIS_EXTRADEV_ID = "110301200100";


    public static final String POLARIS_UPLOAD_LOG = "https://service.snoppa.com/auth/operation/uploadlogattachment.do";
    public static final String POLARIS_COURSE = "https://www.benro-polaris.com/product/polaris-manual.html";
    public static final String POLARIS_PRIVACY = "https://www.benro-polaris.com/product/privacy.html";


    public static final String FOLDER_PREFIX = "Polaris";//文件夹前缀
    public static final String PATH_PREFIX = "polaris";//路径前缀
    public static final String LogPolaris = "LogPolaris";//日志压缩路径
    public static final String LogDownload = "LogDownload";//日志下载路径
    public static final String firmware = "firmware";//固件路径
    public static final String extraDevFirmware = "extraDevFirmware";//第三轴固件路径


}
