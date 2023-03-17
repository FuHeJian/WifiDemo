package com.example.wifidemo1.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Log;


import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;

/**
 * Created by Administrator on 2017/1/9.
 */

public class UpdateVersionUtil {
    private static String TAG = "UpdateVersionUtil";



    /**
     * 检测固件是否需要升级  0,不需要升级 1，强制升级 2，普通升级
     */
    public static int checkAppUpdate(String oldVersion, String latestVersion) {
        if (TextUtils.isEmpty(oldVersion) || TextUtils.isEmpty(latestVersion)) {
            return 0;
        }

        Log.e(TAG, "checkAppUpdate: oldVersion =" + oldVersion + ",latestVersion =" + latestVersion);


        oldVersion = oldVersion.replace("v", "");
        latestVersion = latestVersion.replace("v", "");

        oldVersion = oldVersion.replace("V", "");
        latestVersion = latestVersion.replace("V", "");

        String[] splitOld = oldVersion.split("\\.");
        String[] splitLatest = latestVersion.split("\\.");

        //如果长度不是3
        if (splitOld.length != 3 || splitLatest.length != 3) {
            return 0;
        }
        try {
            if (Integer.parseInt(splitLatest[0]) > Integer.parseInt(splitOld[0])) {
                return 1;
            } else if (Integer.parseInt(splitLatest[0]) < Integer.parseInt(splitOld[0])) {
                return 0;
            } else {
                if (Integer.parseInt(splitLatest[1]) > Integer.parseInt(splitOld[1])) {
                    return 2;
                } else if (Integer.parseInt(splitLatest[1]) < Integer.parseInt(splitOld[1])) {
                    return 0;
                } else {
                    if (Integer.parseInt(splitLatest[2]) > Integer.parseInt(splitOld[2])) {
                        return 2;
                    } else if (Integer.parseInt(splitLatest[1]) <= Integer.parseInt(splitOld[1])) {
                        return 0;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }


    /**
     * @return 当前程序的版本号
     */
    public static int getVersionCode(Context context) {
        int version;
        if (context == null)
            return 1;
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), 0);
            version =(int)packageInfo.getLongVersionCode();
        } catch (Exception e) {
            e.printStackTrace();
            version = 0;
        }
        return version;
    }

    /**
     * @return 当前程序的版本名
     */
    public static String getVersionName(Context context) {
        String version;
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), 0);
            version = packageInfo.versionName;

        } catch (Exception e) {
            e.printStackTrace();
            version = "";
        }
        return version;
    }


    /**
     * 获取md5
     */
    public static String getMD5Str(String str) {
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(str.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            System.out.println("NoSuchAlgorithmException caught!");
            System.exit(-1);
        }
        assert messageDigest != null;
        byte[] byteArray = messageDigest.digest();
        StringBuilder md5StrBuff = new StringBuilder();
        for (byte b : byteArray) {
            if (Integer.toHexString(0xFF & b).length() == 1)
                md5StrBuff.append("0").append(Integer.toHexString(0xFF & b));
            else
                md5StrBuff.append(Integer.toHexString(0xFF & b));
        }
        return md5StrBuff.toString();
    }


    /**
     * 获取时间戳
     */
    public static String getTimeStamp() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        return sdf.format(System.currentTimeMillis());
    }

    /**
     * 获取时间戳
     */
    public static String getTimeStampNew() throws Exception {
        return DateUtils.getCurrentDateString();
    }
}
