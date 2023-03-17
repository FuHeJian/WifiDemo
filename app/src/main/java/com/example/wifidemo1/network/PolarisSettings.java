package com.example.wifidemo1.network;

import androidx.navigation.PopUpToBuilder;

/**
 * @author: fuhejian
 * @date: 2023/3/13
 */
public class PolarisSettings {

    public static String DownloadedFileNamePath;
    public static String RomFileName;
    public static String SoftWarePolarisRomVersion;
    public static String SoftWarePolarisRomMD5;
    public static String SoftWarePolarisRomUrl;

    public static String HardWarePolarisRomVersion;

    public static boolean RomUpgradeIsSuccess = false;

    public static String LatestSoftWarePolarisRomVersion;

    public static boolean uploading = false;
    public enum key{

        DownloadedFileNamePath,

        SoftWarePolarisRomVersion,

        HardWarePolarisRomVersion,

        RomUpgradeIsSuccess

    }

}
