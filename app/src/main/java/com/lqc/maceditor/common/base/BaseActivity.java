package com.lqc.maceditor.common.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.lqc.maceditor.Session;
import com.lqc.maceditor.common.MyApplication;
import com.lqc.maceditor.common.ResponseCacheManager;
import com.lqc.maceditor.common.util.MyAnimation;
import com.lqc.maceditor.common.util.MyAnimation.MyAnimListner;
import com.lqc.maceditor.common.util.Utils;

public class BaseActivity  extends Activity{

	protected Session mSession;
	protected Context appContext;
	protected MyApplication app;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		System.gc();
		super.onCreate(savedInstanceState);
		
		appContext = getApplicationContext();
		mSession = Session.getInstance(appContext);
		app = (MyApplication) getApplication();
		
		Utils.authenticate(appContext);
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		appContext = getApplicationContext();
		mSession = Session.getInstance(appContext);
		app = (MyApplication) getApplication();

	}

    
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        ResponseCacheManager.getInstance().clear();
        //ImageLoader.getInstance().cleanCache();
    }
	
	
	public void topBarBack(View v) {
		if(Utils.clickByMistake()) return;
		MyAnimation.clickAnim(v, new MyAnimListner() {
			
			@Override
			public void animEnd(View v) {
				finish();
			}
		});
	}
	
	
	protected void hindInput() {
		try {
			((InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE))
					.hideSoftInputFromWindow(
							getCurrentFocus().getWindowToken(),
							InputMethodManager.HIDE_NOT_ALWAYS);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	@Override
	public void finish() {
		hindInput();
		System.gc();
		super.finish();
	}
	
	
	
}
