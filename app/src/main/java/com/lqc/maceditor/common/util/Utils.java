package com.lqc.maceditor.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.util.TypedValue;
import android.widget.Toast;

public class Utils {

    public static final boolean APP_DEBUG=true;
    private final static String APP_MD5="62e6f11dc9e3c2c130a74036adc8724e";

    public static void e(String tag,String msg){
        if(APP_DEBUG) Log.e(tag, msg);
    }

    public static void show(Context context,String text){//3.5s
        Toast.makeText(context, text, Toast.LENGTH_LONG).show();
    }

    public static void showShort(Context context,String text){//2s
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    public static void show(Context context,String text,int delayMillis){
        final Toast mToast=Toast.makeText(context, text, Toast.LENGTH_LONG);
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                if(mToast != null){
                    mToast.cancel();
                }
            }
        }, delayMillis);
        //Toast会自动排序一个一个显示
        while(delayMillis>3500){
            show(context,text);
            delayMillis-=3500;
        }
        mToast.show();
    }

    /**
     * Get MD5 Code
     */
    public static String getMD5(String text) {
        try {
            byte[] byteArray = text.getBytes("utf8");
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(byteArray, 0, byteArray.length);
            return convertToHex(md.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }


    /**
     * Convert byte array to Hex string
     */
    private static String convertToHex(byte[] data) {
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < data.length; i++) {
            int halfbyte = (data[i] >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                if ((0 <= halfbyte) && (halfbyte <= 9))
                    buf.append((char) ('0' + halfbyte));
                else
                    buf.append((char) ('a' + (halfbyte - 10)));
                halfbyte = data[i] & 0x0F;
            } while (two_halfs++ < 1);
        }
        return buf.toString();
    }


    public static boolean isWiFi(Context context){
        ConnectivityManager connectMgr = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectMgr.getActiveNetworkInfo();
        if(info.getType() == ConnectivityManager.TYPE_WIFI){
            return true;
        }else if(info !=null && info.getType() ==  ConnectivityManager.TYPE_MOBILE){
        }
        return false;
    }


    public static void callPhone(Context context,String number){
        try{
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+ number));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
			
			/*Intent intent = new Intent();
			intent.setComponent(new ComponentName("com.android.phone","com.android.phone.PhoneGlobals$NotificationBroadcastReceiver"));
			intent.setAction("com.android.phone.ACTION_CALL_BACK_FROM_NOTIFICATION");
			intent.setData(Uri.parse("tel:15920103056"));
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			sendBroadcast(intent);*/
        }catch(Exception e){}
    }

    /**
     * 调起系统发短信功能
     * @param phoneNumber
     * @param message
     */
    public static void sendSMS(Context context,String phoneNumber,String message){
        if(PhoneNumberUtils.isGlobalPhoneNumber(phoneNumber)){
            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:"+phoneNumber));
            intent.putExtra("sms_body", message);
            context.startActivity(intent);
        }
    }


    @SuppressWarnings({ "resource", "unused" })
    public static void copyFile(String oldPath, String newPath) {
        try {
            File oldfile = new File(oldPath);
            if (oldfile.exists()) { // 文件存在时
                InputStream inStream = new FileInputStream(oldPath); // 读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                int bytesum = 0;
                int byteread = 0;
                byte[] buffer = new byte[1024*10];
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; // 字节数 文件大小
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void deleteFile(File file) {
        if (file.exists()) {
            if (file.isFile()) {
                file.delete();
            } else if (file.isDirectory()) {
                File files[] = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    deleteFile(files[i]);
                }
            }
            file.delete();
        }
    }

    public static void authenticate(Context context){
        if(!APP_DEBUG){
			/*try {
	            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
	            Signature[] signatures = packageInfo.signatures;
	            Signature signature=signatures[0];      
	            String signMd5 = MD5.getMessageDigest(signature.toByteArray());
	            if(!APP_MD5.equals(signMd5)){
	            	android.os.Process.killProcess(android.os.Process.myPid());
	            }
	        } catch (NameNotFoundException e) {
	            android.os.Process.killProcess(android.os.Process.myPid());
	        }*/
        }
    }

    private static long lastClickTime=0;
    public static boolean clickByMistake(){
        long time = System.currentTimeMillis();
        long gap=time - lastClickTime;
        if ( gap < 800 && gap>-800) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    //=================================================================================
    private static TypedValue mTmpValue = new TypedValue();
    public static int getXmlDef(Context context, int id) {
        synchronized (mTmpValue) {
            TypedValue value = mTmpValue;
            context.getResources().getValue(id, value, true);
            return (int) TypedValue.complexToFloat(value.data);
        }
    }

    public static long getLong(String value) {
        if (value == null)
            return 0L;

        try {
            return Long.parseLong(value.trim());
        } catch (NumberFormatException e) {
            return 0L;
        }
    }

    /**
     * judge service is running
     * @param mContext
     * @param className
     * @return
     */
    public static boolean isServiceRunning(Context mContext,String className) {
        ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        for (RunningServiceInfo service : activityManager.getRunningServices(Integer.MAX_VALUE)) {
            if (className.equals(service.service.getClassName()))   return true;
        }
        return false;
    }

    /**
     * 通过类的字符串名字创建类对象
     * @param className:要带上包名
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static Class getClassByName(String className){
        try {
            return Class.forName(className);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressWarnings("deprecation")
    public static String getActivityName(Activity activity){
        ActivityManager activityManager=(ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
        String runningActivity=activityManager.getRunningTasks(1).get(0).topActivity.getClassName();
        return runningActivity;
    }


    public static void delete(File file) {
        if (file.isFile()) {
            file.delete();
        }else if(file.isDirectory()){
            File[] childFiles = file.listFiles();
            if (childFiles == null || childFiles.length == 0) {
                file.delete();
            }else{
                for (int i = 0; i < childFiles.length; i++) {
                    delete(childFiles[i]);
                }
                file.delete();
            }
        }
    }


    //====================================================================================
    public static boolean checkMacAddress(String inputMac) {
        if (inputMac.length() != 17) {
            return false;
        }
        inputMac = inputMac.replace("-", "");
        String regString = "[a-f0-9A-F]{12}";
        return Pattern.matches(regString, inputMac);
    }


}
