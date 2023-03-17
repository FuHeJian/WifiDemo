package com.example.wifidemo1.utils;


import android.media.ExifInterface;

import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DateUtils {

    public static final String DEFAULT_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final String Z_PATTERN = "yyyy-MM-dd HH:mm:ss Z";
    private static ThreadLocal<Map<String, DateFormat>> local = new ThreadLocal<Map<String, DateFormat>>() {
        @Override
        protected Map<String, DateFormat> initialValue() {
            return new HashMap<String, DateFormat>();
        }
    };

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


    public static String formatDate(Date date) throws Exception {
        return getDateFormat(DEFAULT_PATTERN).format(date);
    }

    public static Date parse(String date, String pattern) throws Exception {
        return getDateFormat(pattern).parse(date);
    }

    public static String format(Date date, String pattern) throws Exception {
        return getDateFormat(pattern).format(date);
    }

    public static String getCurrentDateString() throws Exception {
        return getDateFormat("yyyyMMddHHmmss").format(new Date());
    }

    public static String getTodayString() throws Exception {
        return getDateFormat("yyyyMMdd").format(new Date());
    }

    public static String getDateString(long time) throws Exception {
        return getDateFormat("yyyy-MM-dd").format(new Date(time));
    }


    public static void main(String[] args) throws Exception {

        String code = "=\"D15E2XV\"";
        String c = code.substring(code.indexOf("\"") + 1, code.lastIndexOf("\""));
        System.out.println(c);
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


    private static final int GB = 1024 * 1024 * 1024;
    //定义MB的计算常量
    private static final int MB = 1024 * 1024;
    //定义KB的计算常量
    private static final int KB = 1024;

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




    public static String getDatePANOString(long time) throws Exception {
        return getDateFormat("yyyy-MM-dd_HH:mm:ss").format(new Date(time));
    }

    public static String getDetailedDateString(long time) throws Exception {
        return getDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date(time));
    }

    public static String getDateFileNameString(long time) throws Exception {
        return getDateFormat("yyyyMMddHHmm").format(new Date(time));
    }



    public static String getTimestamp(){
        try {
            return formatDate(new Date());
        }catch (Exception e){
            e.printStackTrace();
            return "";
        }
    }

    public static String formatetimeHMS(long milliseconds) {
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
            return "00" + ":" + minute + ":" + second;
        }
        return hour + ":" + minute + ":" + second;
    }

}
