package com.lqc.maceditor.common.download;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;

import com.lqc.maceditor.common.util.Utils;

@SuppressLint("NewApi")
public class LqcDownLoadManager {

    private Context mContext;
    private static LqcDownLoadManager mInstance;
    public final Uri CONTENT_URI = Uri.parse("content://downloads/my_downloads");

    private DownloadManager downloadManager;
    private ArrayList<Long> idList;
    private HashMap<String, DownLoadTask> taskMap;


    private LqcDownLoadManager(Context context) {
        mContext = context.getApplicationContext();
        idList=new ArrayList<Long>();
        taskMap=new HashMap<String, DownLoadTask>();

        downloadManager=(DownloadManager)mContext.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadChangeObserver downloadObserver = new DownloadChangeObserver(null);
        mContext.getContentResolver().registerContentObserver(CONTENT_URI, true, downloadObserver);

    }

    public static LqcDownLoadManager get(Context context) {
        if (mInstance == null) {
            mInstance = new LqcDownLoadManager(context);
        }
        return mInstance;
    }

    public void downloadStart(DownLoadManagerCallback listener,int taskID,String weburl){
        String allPath=mContext.getCacheDir()+"/"+Utils.getMD5(weburl);
        if(new File(allPath).exists()){
            listener.onDownLoadSuccess(taskID, allPath);
        }else{
            try{
                DownloadManager.Request request = new Request(Uri.parse(weburl));
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
                long downloadID =downloadManager.enqueue(request);
                idList.add(downloadID);
                taskMap.put(""+downloadID, new DownLoadTask(taskID,listener,allPath));
            }catch(Exception e){
                listener.onDownLoadFail(taskID, "url error");
            }
        }
    }



    class DownloadChangeObserver extends ContentObserver {

        public DownloadChangeObserver(Handler handler) {
            super(handler);
        }


        @Override
        public void onChange(boolean selfChange) {
            DownloadManager.Query query = new DownloadManager.Query();
            Cursor cursor = downloadManager.query(query);
            if(cursor!=null && cursor.moveToFirst()) {
                int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                switch(status) {
                    case DownloadManager.STATUS_PAUSED:break;
                    case DownloadManager.STATUS_PENDING:break;
                    case DownloadManager.STATUS_RUNNING:
                        //进度处理
                        break;
                    case DownloadManager.STATUS_SUCCESSFUL:

                        final long downloadID=cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_ID));
                        if(idList.contains(downloadID)){
                            idList.remove(downloadID);

                            DownLoadTask dlTask=taskMap.get(""+downloadID);
                            String newPath=dlTask.getCachePath();
                            String oldPath=cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME));
                            Utils.copyFile(oldPath, newPath);
                            new File(oldPath).delete();

                            Message msg=new Message();
                            msg.what=0;
                            msg.obj=dlTask;
                            handler.sendMessage(msg);
                        }
                        break;
                    case DownloadManager.STATUS_FAILED:
                        //清除已下载的内容，重新下载
                        break;
                }
            }
            cursor.close();
        }

    }

    @SuppressLint("HandlerLeak")
    private Handler handler=new Handler(){

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    DownLoadTask dlTask=(DownLoadTask) msg.obj;
                    dlTask.getCallback().onDownLoadSuccess(dlTask.getTaskID(),dlTask.getCachePath());
                    break;

                default:
                    break;
            }
        }
    };

    //========================================================================================
    class DownLoadTask{
        private int taskID;
        private String cachePath;
        private DownLoadManagerCallback callback;

        DownLoadTask(int taskID,DownLoadManagerCallback callback,String cachePath){
            this.taskID=taskID;
            this.callback=callback;
            this.cachePath=cachePath;
        }

        public int getTaskID(){
            return taskID;
        }


        public DownLoadManagerCallback getCallback(){
            return callback;
        }

        public String getCachePath(){
            return cachePath;
        }

    }

    public interface DownLoadManagerCallback{
        public void onDownLoadSuccess(int imgID,String backPath);
        public void onDownLoadFail(int imgID,String reason);
    }


}
