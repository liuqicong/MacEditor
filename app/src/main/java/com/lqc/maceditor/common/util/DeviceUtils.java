package com.lqc.maceditor.common.util;

import android.text.TextUtils;

/**
 * Created by Administrator on 2016/3/2.
 */
public class DeviceUtils {


    public static String getRandomMacAddress(){
        String macAddress="";
        for(int i=0;i<6;++i){
            String hex=Integer.toHexString((int)Math.abs(Math.random()*255));
            if(hex.length()==1){
                hex=("0"+hex);
            }
            if(TextUtils.isEmpty(macAddress)){
                macAddress+=hex;
            }else{
                macAddress+=(":"+hex);
            }
        }
        return macAddress;
    }

    public static  String getRoandomIMEI(){
        String imei="";
        for(int i=0;i<15;++i){
            imei+=((int)Math.abs(Math.random()*9));
        }
        return imei;
    }

    public static  String getRoandomPhoneNum(){
        String phone="+861";
        for(int i=0;i<10;++i){
            phone+=((int)Math.abs(Math.random()*9));
        }
        return phone;
    }

    public static  String getRoandomSMSI(){
        String smsi="460000000";
        for(int i=0;i<6;++i){
            smsi+=((int)Math.abs(Math.random()*9));
        }
        return smsi;
    }

    public static  String getRoandomSimNum(){
        String simNum="";
        for(int i=0;i<20;++i){
            simNum+=((int)Math.abs(Math.random()*9));
        }
        return simNum;
    }
}
