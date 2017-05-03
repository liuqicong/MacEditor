package com.lqc.maceditor.common.download;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.lqc.maceditor.common.util.Utils;

public class LqcDownLoadAPP {

	private Context mContext;
	private static LqcDownLoadAPP mInstance;
	
	private ArrayList<DownLoadTask> taskList;
	private HashMap<String, DownLoadTask> taskMap;
	
	private LqcDownLoadAPP(Context context) {
		mContext = context.getApplicationContext();
		taskList=new ArrayList<DownLoadTask>();
		taskMap=new HashMap<String, DownLoadTask>();
	}

	public static LqcDownLoadAPP get(Context context) {
		if (mInstance == null) {
			mInstance = new LqcDownLoadAPP(context);
		}
		return mInstance;
	}

	public void downloadStart(DownAppCallback listener,int taskID,final String weburl){
		String allPath;
		if(mContext.getExternalCacheDir().exists()){
			allPath=mContext.getExternalCacheDir()+"/"+Utils.getMD5(weburl)+".apk";
		}else{
			allPath=mContext.getCacheDir()+"/"+Utils.getMD5(weburl)+".apk";
		}
		if(new File(allPath).exists()){
			listener.onDownLoadSuccess(taskID, allPath);
		}else{
			if(taskMap.containsKey(weburl)){
				DownLoadTask task=taskMap.get(weburl);
				taskList.add(new DownLoadTask(task.getTaskID(),listener,allPath));
			}else{
				DownLoadTask task=new DownLoadTask(taskID,listener,allPath);
				taskMap.put(weburl, task);
				taskList.add(task);
				new MyDownLoad(mContext, weburl).start();
			}
		}
	}
	
    
	@SuppressLint("HandlerLeak")
	private Handler handler=new Handler(){

		@Override
		public void handleMessage(Message msg) {
			try{
				String key=(String)msg.obj;
				if(!taskMap.containsKey(key)) return;
				DownLoadTask dlTask=taskMap.get(key);
				int taskid=dlTask.getTaskID();
				String path=dlTask.getCachePath();
				
				for(int i=0;i<taskList.size();++i){
					DownLoadTask task=taskList.get(i);
					if(taskid==task.getTaskID()){
						if(msg.what==0){
							task.getCallback().onDownLoadSuccess(taskid,path);
						}else{
							task.getCallback().onDownLoadFail(taskid, "download fail");
						}
						taskList.remove(task);
						--i;
					}
				}
				taskMap.remove(key);
			}catch(Exception e){}
		}
	};
    
    //========================================================================================
    class DownLoadTask{
    	private int taskID;
    	private String cachePath;
    	private DownAppCallback callback;
    	
    	DownLoadTask(int taskID,DownAppCallback callback,String cachePath){
    		this.taskID=taskID;
    		this.callback=callback;
    		this.cachePath=cachePath;
    	}
    
    	public int getTaskID(){
    		return taskID;
    	}
    	
    	
    	public DownAppCallback getCallback(){
    		return callback;
    	}
    	
    	public String getCachePath(){
    		return cachePath;
    	}
    	
    }
	
	public interface DownAppCallback{
		public void onDownLoadSuccess(int imgID,String backPath);
		public void onDownLoadFail(int imgID,String reason);
	}
	
	
	
	//=================================================================
	private class MyDownLoad extends Thread {
		
		private String weburl;
		private String allPath;
		
		public MyDownLoad(Context context,String weburl) {
			this.weburl = weburl;
			
			if(mContext.getExternalCacheDir().exists()){
				this.allPath=mContext.getExternalCacheDir()+"/"+Utils.getMD5(weburl)+".apk";
			}else{
				this.allPath=mContext.getCacheDir()+"/"+Utils.getMD5(weburl)+".apk";
			}
		}

		@Override
		public void run() {
			File file = new File(allPath);
			try {
				String urlStr = weburl.replace("https://", "http://");
				URL url = new URL(urlStr);
				HttpURLConnection con = (HttpURLConnection) url.openConnection();
				con.setConnectTimeout(12000);  
	            con.setReadTimeout(15000);  
				con.setDoInput(true);
	            //con.setDoOutput(true);  //get will fail
				con.connect();
				
				InputStream is = con.getInputStream();
				FileOutputStream dst = new FileOutputStream(file);
				byte[] buffer = new byte[1024*40];
		        int len = 0;
		        while ((len = is.read(buffer)) > 0) {
		            dst.write(buffer, 0, len);
		        }
		        is.close();
		        dst.close();
				con.disconnect();
				
				Message msg=new Message();
				msg.what=0;
				msg.obj=weburl;
				handler.sendMessage(msg);
				return;
			} catch (MalformedURLException e) {
				e.printStackTrace();
				
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			Message msg=new Message();
			msg.what=1;
			msg.obj=weburl;
			handler.sendMessage(msg);
		}
		
	}
	
	
}
