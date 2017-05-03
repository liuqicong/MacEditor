/**
 * Copyright (C) Guangzhou YouDianShang information technology co., LTD.
 *
 * @Author LiuQiCong
 * 2016年2月1日 下午4:20:28
 */
package com.lqc.maceditor.common.util;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;

public class AppUtils {

    // 全部程序包
    public static void queryFilterAppInfo(Context context) {
        PackageManager pm = context.getPackageManager();
        // 查询所有已经安装的应用程序
        List<ApplicationInfo> listAppcations = pm.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
        // GET_UNINSTALLED_PACKAGES代表已删除，但还有安装目录的
        for (ApplicationInfo app : listAppcations) {
            String packageName=app.packageName;
        }

    }

    // 获得所有系统的
    public static void querySysAppInfo(Context context) {
        PackageManager pm = context.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        // 通过查询，获得所有ResolveInfo对象.
        List<ResolveInfo> resolveInfos = pm.queryIntentActivities(intent,PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo reInfo : resolveInfos) {
            String activityName = reInfo.activityInfo.name; // 获得该应用程序的启动Activity的name
            String pkgName = reInfo.activityInfo.packageName; // 获得应用程序的包名
            String appLabel = (String) reInfo.loadLabel(pm); // 获得应用程序的Label
            Drawable icon = reInfo.loadIcon(pm); // 获得应用程序图标
        }

    }

    public static boolean isInstalled(Context context, String packageName) {
        try {
            PackageManager pm = context.getPackageManager();
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
        } catch (NameNotFoundException e) {
            return false;
        }
        return true;
    }

    public static void getInstallPackage(Context context) {
        PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> packageInfoList = packageManager.getInstalledPackages(0);
        for (int i = 0; i < packageInfoList.size(); i++) {
            PackageInfo pi = packageInfoList.get(i);
            System.out.println((i + 1) + ":" + pi.toString());
        }
    }

    public static void srartApp(Context context, String packageName) {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        if (intent != null){
            // 已安装包 直接启动
            context.startActivity(intent);
        }
    }

    public static int getVersionCode(Context context,String packageName) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(packageName, 0);
            //String version = info.versionName;
            return info.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static void installApp(Context mContext,String appAllPath){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.parse("file://" + appAllPath),"application/vnd.android.package-archive");
        mContext.startActivity(intent);
    }

    public static void launchApp(Context mContext,String packageName){
        if(AppUtils.isInstalled(mContext, packageName)){
            Intent intent = new Intent();
            PackageManager packageManager = mContext.getPackageManager();
            intent = packageManager.getLaunchIntentForPackage(packageName);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
                    | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            mContext.startActivity(intent);
        }
    }

}
