package com.example.wifidemo1.Executors.MyShadow;

import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.util.List;

/**
 * @author: fuhejian
 * @date: 2023/5/23
 */
public class AppUtil {

    public static AppUtil INSTANCE = new AppUtil();

    public void  getAllPackages(Context context){

        ActivityManager activities = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);

        activities.getAppTasks();

        activities.getRunningAppProcesses();

        AppOpsManager appOpsManager = (AppOpsManager)context.getSystemService(Context.APP_OPS_SERVICE);

        PackageManager packageManager = context.getPackageManager();

        //获取共享库
//        List<ApplicationInfo> installedApplications = packageManager.getInstalledApplications(PackageManager.GET_SHARED_LIBRARY_FILES);

        //获取activities和服务
        List<PackageInfo> installedPackages = packageManager.getInstalledPackages(PackageManager.GET_ACTIVITIES | PackageManager.GET_SERVICES);

        System.out.println();

    }




}
