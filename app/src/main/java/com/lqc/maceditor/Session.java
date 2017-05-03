package com.lqc.maceditor;

import java.io.File;
import java.util.HashMap;
import java.util.Observable;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.lqc.maceditor.common.util.Utils;
import com.lqc.maceditor.ui.activity.MainActivity;


@SuppressLint("SdCardPath")
@TargetApi(Build.VERSION_CODES.KITKAT)
public class Session extends Observable{

    private Context mContext;
    private static Session mInstance;

    private String versionName;
    private int versionCode;
    private String packageName;
    private boolean bathIng;

    private int sWidth=-1;
    private int sHeight=-1;
    private int mUserNums=0;

    private Session(Context context) {

        synchronized (this) {
            mContext = context;
            readSettings();
        }
    }

    public static Session getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new Session(context.getApplicationContext());
        }
        return mInstance;
    }

    private void readSettings(){
        new Thread() {
            public void run() {
                getApplicationInfo();
            };
        }.start();
    }


    /**
     * 在这里获取AndroidManifest.xml数据
     * 如：<meta-data android:name="gfan_cid" android:value="9" />
     */
    private void getApplicationInfo() {
        PackageManager pm = (PackageManager) mContext.getPackageManager();
        try {
            packageName = mContext.getPackageName();
            PackageInfo pi = pm.getPackageInfo(packageName, 0);
            versionName = pi.versionName;
            versionCode = pi.versionCode;

            ApplicationInfo ai = pm.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
            //没有应用数据
            //channelID=ai.metaData.get("UMENG_CHANNEL").toString();
            //====================================================================================
            /*TelephonyManager telMgr = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
            imei = telMgr.getDeviceId();
            sim = telMgr.getSimSerialNumber();*/
            //====================================================================================
            SharedPreferences sp = mContext.getSharedPreferences(packageName,Context.MODE_PRIVATE);

        } catch (NameNotFoundException e) {
            Utils.e("Session", "met some error when get application info");
        }
    }

    //====================================================================================
    public int getVersionCode(){
        if(versionCode<=0){
            getApplicationInfo();
        }
        return versionCode;
    }


    public String getVersionName(){
        if(TextUtils.isEmpty(versionName)){
            getApplicationInfo();
        }
        return versionName;
    }

    //=====================================================================================
    public int getScreenWidth(){
        if (sWidth < 0) {
            DisplayMetrics dm = new DisplayMetrics();
            WindowManager windowManager=(WindowManager)mContext.getSystemService(Context.WINDOW_SERVICE);
            windowManager.getDefaultDisplay().getMetrics(dm);
            sWidth = dm.widthPixels;
        }
        return sWidth;
    }

    public int getScreenHeight(){
        if (sHeight < 0) {
            DisplayMetrics dm = new DisplayMetrics();
            WindowManager windowManager=(WindowManager)mContext.getSystemService(Context.WINDOW_SERVICE);
            windowManager.getDefaultDisplay().getMetrics(dm);
            sHeight = dm.heightPixels;
        }
        return sHeight;
    }

    public int getDrawableId(String name){
        if(TextUtils.isEmpty(packageName)){
            getApplicationInfo();
        }
        return mContext.getResources().getIdentifier(name, "drawable", packageName);
    }

    private void notifyData(String key,Object data){
        final HashMap<String, Object> notify=new HashMap<String, Object>(1);
        notify.put(key,data);
        super.setChanged();
        super.notifyObservers(notify);
    }

    //==============set and get==========================================================
    public void addUserNum(){
        ++mUserNums;
        if(mUserNums>=50){
            bathIng=false;
        }
    }

    public int getUserNum(){
        return mUserNums;
    }

    public void setBathing(boolean bath){
        bathIng=bath;
    }

    public boolean getBathing(){
        return bathIng;
    }

    public void backMain(boolean isInstall){
        if(isInstall){
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    startApp();
                }
            }, 4000);
            return;
        }
        startApp();
    }

    public void startApp(){
        Intent intent=new Intent(mContext,MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Constants.INTENT_DATA, "");
        mContext.startActivity(intent);
    }

    public void hasInstallApp(){
        notifyData(Constants.NOTIFY_INSTALLED,0);
    }

}
