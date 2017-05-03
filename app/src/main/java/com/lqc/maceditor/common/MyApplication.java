package com.lqc.maceditor.common;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.LinkedList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.lqc.maceditor.Session;
import com.lqc.maceditor.common.download.LqcDownLoadToCache;
import com.lqc.maceditor.common.util.Utils;
import com.lqc.maceditor.ui.activity.MainActivity;


@SuppressLint({ "InflateParams", "NewApi" })
public class MyApplication extends Application {

    public void onCreate() {
        super.onCreate();
        Utils.authenticate(this);
        Session.getInstance(this);
        LqcDownLoadToCache.get(this);

        //抓捕异常
        Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {

            @Override
            public void uncaughtException(Thread thread, Throwable ex) {
                Utils.e("异常了==>重启", ex.toString());
                restartApplication();
            }
        });

    }

    public void backMain(){
        try {
            for (Activity activity:mList) {
                if (activity != null){
                    if(!(activity instanceof MainActivity)){
                        activity.finish();
                    }
                }
            }
        } catch (Exception e) {
            System.exit(0);
        }
    }

    //========================================异常处理=================================================
    private List<Activity> mList = new LinkedList<Activity>();
    public void addActivity(Activity activity) {
        mList.add(activity);
        for(int i=0;i<mList.size();++i){
            if(mList.get(i) == null){
                mList.remove(i);
                --i;
            }
        }
    }

    public void closeAllActivity() {
        try {
            for (Activity activity:mList) {
                if (activity != null) activity.finish();
            }
        } catch (Exception e) {
            System.exit(0);
        }
    }

    private void restartApplication() {
        closeAllActivity();
        Intent k = getBaseContext().getPackageManager()
                .getLaunchIntentForPackage(getBaseContext().getPackageName());
        k.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        k.addCategory("restart");
        startActivity(k);
        System.exit(0);
    }

    //=================================上下文公有方法==============================================
    public String getCurMacAddress(){
        WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        return info.getMacAddress();
    }


}
