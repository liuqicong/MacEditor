package com.lqc.maceditor.common.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import com.lqc.maceditor.Session;
import com.lqc.maceditor.common.MyApplication;
import com.lqc.maceditor.common.ResponseCacheManager;
import com.lqc.maceditor.common.util.Utils;

public class BaseFragment extends Fragment{

	protected Activity fActivity;
	protected View rootView;
	
	protected Session mSession;
	protected Context appContext;
	protected MyApplication app;
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		System.gc();
		super.onActivityCreated(savedInstanceState);
		
		fActivity=getActivity();
        rootView=getView();

		appContext = fActivity.getApplicationContext();
		mSession = Session.getInstance(appContext);
		app=(MyApplication)fActivity.getApplication();
		Utils.authenticate(appContext);
		
	}
	

    
    @Override
	public void onResume() {
		super.onResume();

		fActivity=getActivity();
        rootView=getView();

		appContext = fActivity.getApplicationContext();
		mSession = Session.getInstance(appContext);
		app=(MyApplication)fActivity.getApplication();
		Utils.authenticate(appContext);
	}

	@Override
    public void onLowMemory() {
        super.onLowMemory();
        ResponseCacheManager.getInstance().clear();
    }
	
	
	
}
