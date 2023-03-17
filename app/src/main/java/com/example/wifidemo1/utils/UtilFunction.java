package com.example.wifidemo1.utils;

import android.app.Activity;
import android.app.usage.StorageStatsManager;
import android.content.ContentResolver;
import android.content.Context;
import android.media.ExifInterface;
import android.os.Build;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.WindowManager;

import androidx.annotation.RequiresApi;

import com.example.wifidemo1.model.MyNetwork;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;


/**
 * Created by HLW on 2019/7/6.
 */

public class UtilFunction {


    public static final String DEFAULT_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private static final int GB = 1024 * 1024 * 1024;
    //定义MB的计算常量
    private static final int MB = 1024 * 1024;
    //定义KB的计算常量
    private static final int KB = 1024;
    private static ThreadLocal<Map<String, DateFormat>> local = new ThreadLocal<Map<String, DateFormat>>() {
        @Override
        protected Map<String, DateFormat> initialValue() {
            return new HashMap<String, DateFormat>();
        }
    };
    private static String[] units = {"B", "KB", "MB", "GB", "TB"};

    public static int dip2px(Context context, int dip) {
        if (context == null) {
            return dip;
        }
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) ((float) dip * scale + 0.5F);
    }

    //格式化文件大小
    public static String formatFileSize(long fSize) {
        DecimalFormat decimalFormat = new DecimalFormat("#.00");
        String fileSizeString = "";
        if (fSize < 1024) {
            fileSizeString = decimalFormat.format((double) fSize) + "B";
        } else if (fSize < 1048576) {
            fileSizeString = decimalFormat.format((double) fSize / 1024) + "KB";
        } else if (fSize < 1073741824) {
            fileSizeString = decimalFormat.format((double) fSize / 1048576) + "MB";
        } else {
            fileSizeString = decimalFormat.format((double) fSize / 1073741824) + "GB";
        }
        return fileSizeString;
    }

    public static String bytes2kb(long bytes) {

        if (bytes / GB >= 1) {
            return String.format(Locale.getDefault(), " %.1f %s", (bytes * 1.0f / GB), "GB");
        } else if (bytes / MB >= 1) {
            return String.format(Locale.getDefault(), " %.1f %s", (bytes * 1.0f / MB), "MB");
        } else if (bytes / KB >= 1) {
            return String.format(Locale.getDefault(), " %.1f %s", (bytes * 1.0f / KB), "KB");
        } else {
            return bytes + "B";
        }
    }

    public static String mb2kb(long bytes) {
        if (bytes / 1024 >= 1) {
            return String.format(Locale.getDefault(), " %.1f %s", (bytes * 1.0f / 1024), "GB");
        } else {
            return bytes + "MB";
        }
    }

    public static int getExifOrientation(String filepath) {
        int degree = 0;
        if (filepath == null)
            return 0;
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(filepath);
        } catch (IOException ex) {

        }
        if (exif != null) {
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
            if (orientation != -1) {
                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        degree = 90;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        degree = 180;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        degree = 270;
                        break;
                    default:
                        break;
                }
            }
        }
        return degree;
    }

    public static String formatetime(int milliseconds) {
        String hour = String.valueOf(milliseconds / 3600000);
        String minute = String.valueOf((milliseconds % 3600000) / 60000);
        int a = Math.round(((milliseconds % 3600000) % 60000) * 1.0f / 1000 * 1.0f);
        if (a == 60)
            a = 59;
        String second = String.valueOf(a);

        hour = deal(hour);
        minute = deal(minute);
        second = deal(second);
        if (hour == "00") {
            return minute + ":" + second;
        }
        return hour + ":" + minute + ":" + second;
    }

    public static String formatetimeHourMinute(long milliseconds) {
        String hour = String.valueOf(milliseconds / 3600000);
        String minute = String.valueOf((milliseconds % 3600000) / 60000);
        hour = deal(hour);
        minute = deal(minute);
        return hour + ":" + minute;
    }

    public static String formatetime(long milliseconds) {
        String hour = String.valueOf(milliseconds / 3600000);
        String minute = String.valueOf((milliseconds % 3600000) / 60000);
        int a = Math.round(((milliseconds % 3600000) % 60000) * 1.0f / 1000 * 1.0f);
        if (a == 60)
            a = 59;
        String second = String.valueOf(a);

        hour = deal(hour);
        minute = deal(minute);
        second = deal(second);
        if (hour == "00") {
            return minute + ":" + second;
        }
        return hour + ":" + minute + ":" + second;
    }

    private static String deal(String time) {
        if (time.length() == 1) {
            if ("0".equals(time)) {
                time = "00";
            } else {
                time = "0" + time;
            }
        }
        return time;
    }

    public static String getDateString(long time) throws Exception {
        return getDateFormat("yyyy-MM-dd").format(new Date(time));
    }

    public static String getDateStringCh(long time) throws Exception {
        return getDateFormat("yyyy年MM月dd日").format(new Date(time));
    }

    public static String getDateStringEn(long time) throws Exception {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM d, yyyy",Locale.ENGLISH);
        return simpleDateFormat.format(new Date(time));
    }

    public static String getDateStringForAppointment(long time) throws Exception {
        return getDateFormat("yyyy,MM,dd,HH,mm,ss").format(new Date(time));
    }


    public static String getDatePANOString(long time) throws Exception {
        return getDateFormat("yyyy-MM-dd_HH:mm:ss").format(new Date(time));
    }

    public static String getDetailedDateString(long time) throws Exception {
        return getDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date(time));
    }

    public static String getDateFileNameString(long time) throws Exception {
        return getDateFormat("yyyyMMddHHmm").format(new Date(time));
    }

    private static DateFormat getDateFormat(String pattern) {
        String p;
        if (pattern == null || pattern.isEmpty())
            p = DEFAULT_PATTERN;
        else
            p = pattern;
        Map<String, DateFormat> formatMap = local.get();
        DateFormat df = formatMap.get(p);
        if (df == null) {
            df = new SimpleDateFormat(p);
            formatMap.put(p, df);
        }
        return df;
    }

    /**
     * 获得sd卡剩余容量，即可用大小
     *
     * @return
     */
    public static long getSDAvailableSize() {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
        if (path == null)
            return 0;
        File sdcard_filedir = new File(path);
        long availablesize = sdcard_filedir.getUsableSpace();//获取文件目录对象剩余空间
        return availablesize;
    }

    /**
     * 获得SD卡总大小
     *
     * @return
     */
    public static long getSDTotalSize() {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
        if (path == null)
            return 0;
        File sdcard_filedir = new File(path);
        long totalSpace = sdcard_filedir.getTotalSpace();

        return totalSpace;
    }

    /**
     * API 26 android O
     * 获取总共容量大小，包括系统大小
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static long getTotalSize(String fsUuid, Context context) {
        try {
            UUID id;
            if (fsUuid == null) {
                id = StorageManager.UUID_DEFAULT;
            } else {
                id = UUID.fromString(fsUuid);
            }
            StorageStatsManager stats = context.getSystemService(StorageStatsManager.class);
            return stats.getTotalBytes(id);
        } catch (NoSuchFieldError | NoClassDefFoundError | NullPointerException | IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * 单位转换
     */
    public static String getUnit(float size, float unit) {
        int index = 0;
        while (size > 1024 && index < 4) {
            size = size / unit;
            index++;
        }
        return String.format(Locale.getDefault(), " %.2f %s", size, units[index]);
    }


    public static void setLight(Activity activity, int brightness) {
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.screenBrightness = Float.valueOf(brightness) * (1f / 255f);
        activity.getWindow().setAttributes(lp);
    }

    public static int getScreenBrightness(Activity activity) {
        if (activity == null)
            return 100;
        int value = 0;
        ContentResolver cr = activity.getContentResolver();
        try {
            value = Settings.System.getInt(cr, Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return value;
    }

    public static int dp2px(Context context, float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, context.getResources().getDisplayMetrics());

    }


    public static int sp2px(Context context, float spVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                spVal, context.getResources().getDisplayMetrics());

    }


    //格式化时间
    public static String stringForTime(int time) {

        StringBuilder mFormatBuilder = new StringBuilder();
        Formatter mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
        int seconds = time % 60;
        int minutes = (time / 60) % 60;
        int hours = time / 3600;
        mFormatBuilder.setLength(0);
        return mFormatter.format("%02d:%02d:%02d", hours % 24, minutes, seconds).toString();
    }

    public static String stringForTimeForFloatSec(float timeInSec) {
        StringBuilder mFormatBuilder = new StringBuilder();
        Formatter mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
        int seconds = (int)timeInSec % 60;
        int minutes = ((int)timeInSec / 60) % 60;
        int hours = (int)timeInSec / 3600;
        int millsec = (int)((timeInSec - (int)timeInSec)*100);
        mFormatBuilder.setLength(0);
        return mFormatter.format("%02d:%02d:%02d.%02d", hours % 24, minutes, seconds, millsec).toString();
    }

//    public static String stringForTime(int time) {
//
//        StringBuilder mFormatBuilder = new StringBuilder();
//        Formatter mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
//        int seconds = time % 60;
//        int minutes = (time / 60) % 60;
//        int hours = time / 3600;
//        mFormatBuilder.setLength(0);
//        return mFormatter.format("%02d:%02d:%02d", hours % 24, minutes, seconds).toString();
//    }

    public static String stringForTimeAndDay(int time) {

        StringBuilder mFormatBuilder = new StringBuilder();
        Formatter mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
        int seconds = time % 60;
        int minutes = (time / 60) % 60;
        int hours = time / 3600;
        mFormatBuilder.setLength(0);
        if (hours == 0) {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        } else {
            if (hours / 24 == 0) {
                return mFormatter.format("%02d:%02d:%02d", hours % 24, minutes, seconds).toString();
            } else {
                return hours / 24 + "+" + mFormatter.format("%02d:%02d:%02d", hours % 24, minutes, seconds).toString();
            }
        }
    }


    //格式化时间
    public static String stringForTime(long time) {

        StringBuilder mFormatBuilder = new StringBuilder();
        Formatter mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
        int seconds = (int) (time % 60);
        int minutes = (int) ((time / 60) % 60);
        int hours = (int) (time / 3600);
        mFormatBuilder.setLength(0);
        return mFormatter.format("%02d:%02d:%02d", hours, minutes, seconds).toString();
    }


    public static String precompileFormatTime(int time) {

        StringBuilder mFormatBuilder = new StringBuilder();
        Formatter mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
        int seconds = time % 60;
        int minutes = (time / 60) % 60;
        int hours = time / 3600;
        mFormatBuilder.setLength(0);
        if (hours == 0) {
            return mFormatter.format("%02d'%02d\"", minutes, seconds).toString();
        } else {
            if (hours / 24 == 0) {
                return mFormatter.format("%02d:%02d'%02d\"", hours % 24, minutes, seconds).toString();
            } else {
                return hours / 24 + "+" + mFormatter.format("%02d:%02d'%02d\"", hours % 24, minutes, seconds).toString();
            }
        }
    }

    public static String precompileFormatTime2(int time) {

        StringBuilder mFormatBuilder = new StringBuilder();
        Formatter mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
        int seconds = time % 60;
        int minutes = (time / 60) % 60;
        int hours = time / 3600;
        mFormatBuilder.setLength(0);

        if (hours / 24 == 0) {
            return mFormatter.format("%02d:%02d'%02d\"", hours % 24, minutes, seconds).toString();
        } else {
            return hours / 24 + "+" + mFormatter.format("%02d:%02d'%02d\"", hours % 24, minutes, seconds).toString();
        }
    }

    public static String stringForTimeMinutesSeconds(int time) {

        StringBuilder mFormatBuilder = new StringBuilder();
        Formatter mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
        int seconds = time % 60;
        int minutes = (time / 60) % 60;
        mFormatBuilder.setLength(0);
        return mFormatter.format(" %02d:%02d", minutes, seconds).toString();
    }

    public static String stringForTimeMinutesSecondsWithoutMinutesCarry(int time) {
        StringBuilder mFormatBuilder = new StringBuilder();
        Formatter mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
        int seconds = time % 60;
        int minutes = (time / 60);
        mFormatBuilder.setLength(0);
        return mFormatter.format(" %02d:%02d", minutes, seconds).toString();
    }

    public static String stringForTimeMinutesSecondsWithoutMinutesCarry(float time) {
        StringBuilder mFormatBuilder = new StringBuilder();
        Formatter mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
        int seconds = (int)time % 60;
        int minutes = ((int)time / 60);
        int millsec = (int)((time - (int)time)*100);
        mFormatBuilder.setLength(0);
        return mFormatter.format(" %02d:%02d.%02d", minutes, seconds, millsec).toString();
    }

    //格式化时间
    public static String stringForTimeHoursMunutes(long time) {
        StringBuilder mFormatBuilder = new StringBuilder();
        Formatter mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
        long seconds = time % 60;
        long minutes = (time / 60) % 60;
        long hours = time / 3600;
        mFormatBuilder.setLength(0);
        return mFormatter.format("%02d:%02d", hours, minutes).toString();
    }


    public static String getCurrentTimeZone() {
        TimeZone tz = TimeZone.getDefault();
        return createGmtOffsetString(true, true, tz.getRawOffset());
    }

    public static String createGmtOffsetString(boolean includeGmt,
                                               boolean includeMinuteSeparator, int offsetMillis) {
        int offsetMinutes = offsetMillis / 60000;
        char sign = '+';
        if (offsetMinutes < 0) {
            sign = '-';
            offsetMinutes = -offsetMinutes;
        }
        StringBuilder builder = new StringBuilder(9);
        if (includeGmt) {
            builder.append("GMT");
        }
        builder.append(sign);
        appendNumber(builder, 2, offsetMinutes / 60);
        if (includeMinuteSeparator) {
            builder.append(':');
        }
        appendNumber(builder, 2, offsetMinutes % 60);
        return builder.toString();
    }

    private static void appendNumber(StringBuilder builder, int count, int value) {
        String string = Integer.toString(value);
        for (int i = 0; i < count - string.length(); i++) {
            builder.append('0');
        }
        builder.append(string);
    }

    public static String encryptPassword(String clearText) {
        try {
            String encrypedPwd = Base64.encodeToString(clearText.getBytes("UTF-8"), Base64.DEFAULT);
            encrypedPwd = encrypedPwd.replace("\n", "");
            Log.e("adfadfas", "encryptPassword: " + encrypedPwd.trim());
            return encrypedPwd.trim();
        } catch (Exception e) {
            Log.e("adfadfas", "encryptPassword: e =" + e);
        }
        return clearText;
    }

    public static String decryptPassword(String encryptedPwd) {
        if (encryptedPwd == null || "".equals(encryptedPwd)) {
            return null;
        }
        try {
            byte[] encryptedWithoutB64 = Base64.decode(encryptedPwd, Base64.DEFAULT);
            Log.d("adfadfas", "decryptPassword: " + new String(encryptedWithoutB64));
            return new String(encryptedWithoutB64);
        } catch (Exception e) {
            Log.d("adfadfas", "decryptPassword: e =" + e);
        }
        return encryptedPwd;
    }


    public static Runnable getWifiSignalRunnable(int wifiSignal) {
        Log.d("TAG", "getWifiSignalRunnable: wifiSignal =" + wifiSignal);
        return new Runnable() {
            @Override
            public void run() {
                try {
                    URL downloadurl = new URL("http://192.168.0.1:8080/?action=signal&" + wifiSignal);
                    HttpURLConnection urlcon = (HttpURLConnection) downloadurl.openConnection();
                    urlcon.setConnectTimeout(2000);
                    urlcon.setRequestMethod("GET");
                    urlcon.connect();
                    int ResponseCode = urlcon.getResponseCode();
                } catch (Exception e) {
                    Log.e("getWifiSignalRunnable", " run: Exception =" + e);
                }
            }
        };
    }


    public static Runnable getImageQualityRunnable(int wifiSignal, MyNetwork myNetwork) {

        wifiSignal = 40 * wifiSignal / 100 + 10;
        String url = OrderCommunication.getInstance().streamAddress + "/?action=signal&" + wifiSignal;


        Log.d("TAG", "getImageQualityRunnable: url =" + url);
        return new Runnable() {
            @Override
            public void run() {
                try {
                    URL downloadurl = new URL(url);
                    HttpURLConnection urlcon = (HttpURLConnection) myNetwork.network.openConnection(downloadurl);
                    urlcon.setConnectTimeout(2000);
                    urlcon.setRequestMethod("GET");
                    urlcon.connect();
                    int ResponseCode = urlcon.getResponseCode();
                    Log.d("getImageQualityRunnable", "ResponseCode: " + ResponseCode);
                } catch (Exception e) {
                    Log.e("getImageQualityRunnable", " run: Exception =" + e);
                }
            }
        };
    }
}
