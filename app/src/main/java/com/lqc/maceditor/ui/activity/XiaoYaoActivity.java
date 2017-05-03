package com.lqc.maceditor.ui.activity;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.lqc.maceditor.R;
import com.lqc.maceditor.common.base.BaseActivity;
import com.lqc.maceditor.common.util.AppUtils;
import com.lqc.maceditor.common.util.DeviceUtils;
import com.lqc.maceditor.common.util.MyAnimation;
import com.lqc.maceditor.common.util.RootCmd;
import com.lqc.maceditor.common.util.Utils;
import com.lqc.maceditor.common.widget.LqcCircularProgress;

import java.io.File;

public class XiaoYaoActivity extends BaseActivity implements View.OnClickListener {

    private final int TM_IMEI=1;
    private final int TM_PHONE=2;
    private final int TM_IMSI=3;
    private final int TM_SIMSERIAL=4;

    private EditText macEdit,imeiEdit,phoneEdit,imsiEdit,simserialEdit;
    private EditText lunchTimeEdit;
    private Button bathBtn;
    private ProgressDialog mDialog;

    private final String targetPackName="com.yds.courier";
    private boolean bathEnable;
    private int noticeIndex=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xiaoyao);

        macEdit=(EditText) findViewById(R.id.xiaoyao_mac_show);
        macEdit.setText(app.getCurMacAddress());
        findViewById(R.id.xiaoyao_btn_mac).setOnClickListener(this);

        imeiEdit=(EditText) findViewById(R.id.xiaoyao_imei_show);
        imeiEdit.setText(getTMInfo(TM_IMEI));
        findViewById(R.id.xiaoyao_btn_imei).setOnClickListener(this);

        phoneEdit=(EditText) findViewById(R.id.xiaoyao_linenum_show);
        phoneEdit.setText(getTMInfo(TM_PHONE));
        findViewById(R.id.xiaoyao_btn_linenum).setOnClickListener(this);

        imsiEdit=(EditText) findViewById(R.id.xiaoyao_imsi_show);
        imsiEdit.setText(getTMInfo(TM_IMSI));
        findViewById(R.id.xiaoyao_btn_imsi).setOnClickListener(this);

        simserialEdit=(EditText) findViewById(R.id.xiaoyao_simserial_show);
        simserialEdit.setText(getTMInfo(TM_SIMSERIAL));
        findViewById(R.id.xiaoyao_btn_simserial).setOnClickListener(this);

        lunchTimeEdit=(EditText) findViewById(R.id.xiaoyao_brush_time);
        bathBtn=(Button) findViewById(R.id.xiaoyao_btn_brush_sber_batch);
        bathBtn.setOnClickListener(this);

        findViewById(R.id.xiaoyao_btn_onekey).setOnClickListener(this);
        findViewById(R.id.xiaoyao_btn_changeui_system).setOnClickListener(this);
        findViewById(R.id.xiaoyao_btn_changeui_app).setOnClickListener(this);

    }




    @Override
    protected void onResume() {
        super.onResume();

        //5~10秒后开始刷下一个
        if(mSession.getBathing()){
            bathBtn.setText("已刷："+mSession.getUserNum());
            int times=5000+(int)Math.abs(Math.random()*1);
            handler.sendEmptyMessageDelayed(6, times);
        }

    }



    private void changeWiFiInfo(){
        String randomMac= DeviceUtils.getRandomMacAddress();
        RootCmd.execRootCmd("setprop wifi.interface.mac '" + randomMac + "'");
        //RootCmd.execRootCmd("./ /etc/init.microvirt.sh");
        checkNewMacAddress(randomMac);
    }

    private void changeTMinfo(int type){
        String radomeInfo="";
        switch (type) {
            case TM_IMEI:
                radomeInfo=DeviceUtils.getRoandomIMEI();
                RootCmd.execRootCmd("setprop microvirt.imei '"+radomeInfo+"'");
                break;

            case TM_PHONE:
                radomeInfo=DeviceUtils.getRoandomPhoneNum();
                RootCmd.execRootCmd("setprop microvirt.linenum '"+radomeInfo+"'");
                break;

            case TM_IMSI:
                radomeInfo=DeviceUtils.getRoandomSMSI();
                RootCmd.execRootCmd("setprop microvirt.imsi '"+radomeInfo+"'");
                break;

            case TM_SIMSERIAL:
                radomeInfo=DeviceUtils.getRoandomSimNum();
                RootCmd.execRootCmd("setprop microvirt.simserial '"+radomeInfo+"'");
                break;
            default:
                break;
        }
        checkNewInfo(radomeInfo,type);
    }

    //================================获取当前数据============================================
    private String getTMInfo(int type){
        TelephonyManager telephonyManager= (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        switch (type) {
            case TM_IMEI:
                return telephonyManager.getDeviceId();//获取智能设备唯一编号

            case TM_PHONE:
                return telephonyManager.getLine1Number();//获取本机号码

            case TM_IMSI:
                return telephonyManager.getSubscriberId();//得到用户Id,SIM卡编号

            case TM_SIMSERIAL:
                return telephonyManager.getSimSerialNumber();//获得SIM卡的序号

            default:
                return "";
        }
    }

    //=====================================验证结果========================================================
    private void checkNewMacAddress(String randomMac){
        WifiManager wifiManager=(WifiManager)super.getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(false);
        wifiManager.setWifiEnabled(true);
        wifiManager.disconnect();
        wifiManager.reconnect();

        mDialog= LqcCircularProgress.show(this);
        Message msg=new Message();
        msg.what=0;
        msg.obj=randomMac;
        msg.arg1=8;
        handler.sendMessageDelayed(msg, 1000);
    }


    private void checkNewInfo(String newInfo,int type){
        mDialog=LqcCircularProgress.show(this);
        Message msg=new Message();
        msg.what=type;
        msg.obj=newInfo;
        msg.arg1=8;
        handler.sendMessageDelayed(msg, 1000);
    }

    private void checkDelay(String newInfo,int type,int delay){
        --delay;
        if(delay>0){
            Message newMsg=new Message();
            newMsg.what=type;
            newMsg.obj=newInfo;
            newMsg.arg1=delay;
            handler.sendMessageDelayed(newMsg, 1000);
        }else{
            mDialog.dismiss();
            Utils.show(appContext, "修改失败");
        }
    }

    @SuppressLint("HandlerLeak")
    private Handler handler=new Handler(){

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    String randomMac=(String) msg.obj;
                    if(app.getCurMacAddress().equals(randomMac)){
                        mDialog.dismiss();
                        macEdit.setText(randomMac);
                        Utils.show(appContext, "修改成功");
                    }else{
                        checkDelay(randomMac,0,msg.arg1);
                    }
                    break;

                case 1:
                    String randomImei=(String) msg.obj;
                    if(getTMInfo(TM_IMEI).equals(randomImei)){
                        mDialog.dismiss();
                        imeiEdit.setText(randomImei);
                        Utils.show(appContext, "修改成功");
                        if(bathEnable){
                            //启动送呗
                            handler.sendEmptyMessageDelayed(5, 100);
                        }

                    }else{
                        checkDelay(randomImei,TM_IMEI,msg.arg1);
                    }
                    break;


                case 2:
                    String randomPhoneNum=(String) msg.obj;
                    if(getTMInfo(TM_PHONE).equals(randomPhoneNum)){
                        mDialog.dismiss();
                        phoneEdit.setText(randomPhoneNum);
                        Utils.show(appContext, "修改成功");
                    }else{
                        checkDelay(randomPhoneNum,TM_PHONE,msg.arg1);
                    }
                    break;

                case 3:
                    String randomSMSI=(String) msg.obj;
                    if(getTMInfo(TM_IMSI).equals(randomSMSI)){
                        mDialog.dismiss();
                        imsiEdit.setText(randomSMSI);
                        Utils.show(appContext, "修改成功");
                    }else{
                        checkDelay(randomSMSI,TM_IMSI,msg.arg1);
                    }
                    break;

                case 4:
                    String randomSimNum=(String) msg.obj;
                    if(getTMInfo(TM_SIMSERIAL).equals(randomSimNum)){
                        mDialog.dismiss();
                        simserialEdit.setText(randomSimNum);
                        Utils.show(appContext, "修改成功");
                    }else{
                        checkDelay(randomSimNum,TM_SIMSERIAL,msg.arg1);
                    }
                    break;

                case 5:
                    //启动送呗
                    AppUtils.launchApp(appContext, targetPackName);
                    //timeStr毫秒后关闭送呗
                    String timeStr=lunchTimeEdit.getText().toString();
                    int timeInt;
                    try{
                        timeInt=Integer.parseInt(timeStr);
                    }catch(Exception e){
                        timeInt=3000;
                    }
                    if(timeInt<3000){
                        timeInt=3000;
                    }
                    handler.postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            mSession.addUserNum();
                            mSession.startApp();
                            finish();
                        }
                    }, timeInt);
                    break;

                case 6:
                    batchApp();
                    break;

                default:
                    break;
            }
        }

    };

    @Override
    public void onClick(View v) {

        MyAnimation.clickAnim(v, new MyAnimation.MyAnimListner() {

            @Override
            public void animEnd(View v) {
                switch (v.getId()) {
                    case R.id.xiaoyao_btn_mac:
                        changeWiFiInfo();
                        break;

                    case R.id.xiaoyao_btn_imei:
                        changeTMinfo(TM_IMEI);
                        break;

                    case R.id.xiaoyao_btn_linenum:
                        changeTMinfo(TM_PHONE);
                        break;

                    case R.id.xiaoyao_btn_imsi:
                        changeTMinfo(TM_IMSI);
                        break;

                    case R.id.xiaoyao_btn_simserial:
                        changeTMinfo(TM_SIMSERIAL);
                        break;

                    case R.id.xiaoyao_btn_onekey:
                        changeWiFiInfo();
                        changeTMinfo(TM_IMEI);
                        changeTMinfo(TM_PHONE);
                        changeTMinfo(TM_IMSI);
                        changeTMinfo(TM_SIMSERIAL);
                        break;

                    case R.id.xiaoyao_btn_changeui_system:
                        String uiPath = "/sdcard/SpeedSoftware/Archives/SystemUI.apk";
                        if (new File(uiPath).exists()) {
                            String sysPath = "/system/app/SystemUI.apk";

                            RootCmd.execRootCmd("mount -o remount,rw -t yaffs2 /dev/block/mtdblock3 /system");
                            RootCmd.execRootCmd("dd if=" + uiPath + " of=" + sysPath);
                            RootCmd.execRootCmd("mount -o remount,ro -t yaffs2 /dev/block/mtdblock3 /system");
                        }
                        break;

                    case R.id.xiaoyao_btn_changeui_app:
                        {
                            //声明通知（消息）管理器  初始化NotificationManager对象
                            NotificationManager m_NotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                            //点击通知时转移内容
                            Intent m_Intent = new Intent();
                            //主要是设置点击通知时显示内容的类
                            PendingIntent m_PendingIntent = PendingIntent.getActivity(XiaoYaoActivity.this, 0, m_Intent, 0);
                            //构造Notification对象
                            Notification m_Notification = new Notification();
                            //设置通知在状态栏显示的图标
                            m_Notification.icon = R.mipmap.ic_launcher;
                            //当我们点击通知时显示的内容
                            m_Notification.tickerText = "Button1通知内容...";
                            //通知时发出默认的声音
                            m_Notification.defaults = Notification.DEFAULT_SOUND;
                            //设置通知显示的参数
                            //m_Notification.setLatestEventInfo(this, "", "", m_PendingIntent);
                            //可以理解为执行这个通知
                            m_NotificationManager.notify(noticeIndex, m_Notification);
                            ++noticeIndex;
                        }
                        break;

                    case R.id.xiaoyao_btn_brush_sber_batch:
                        if (mSession.getBathing()) {
                            mSession.setBathing(false);
                        } else {
                            mSession.setBathing(true);
                            batchApp();
                        }
                        break;

                    default:
                        break;
                }
            }
        });
    }

    private void batchApp(){
        bathEnable=true;
        if(AppUtils.isInstalled(appContext, targetPackName)){
            //清除应用数据和缓存
            RootCmd.execRootCmd("pm clear "+targetPackName);
            //更改IMEI
            changeTMinfo(TM_IMEI);

        }else if(new File("/sdcard/Download/Courierv5.0.1-XiaoMi.apk").exists()){
            AppUtils.installApp(appContext, "/sdcard/Download/Courierv5.0.1-XiaoMi.apk");
        }else{
            Utils.show(appContext, "先安装送呗呀，大佬！");
        }
    }


    @Override
    public void finish() {
        handler.removeCallbacksAndMessages(null);
        super.finish();
    }




}
