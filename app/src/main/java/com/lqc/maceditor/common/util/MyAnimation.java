package com.lqc.maceditor.common.util;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;

public class MyAnimation {

	public static void clickAnim(final View v,final MyAnimListner Listener){
		ScaleAnimation scaleAnim=new ScaleAnimation(0.9f,1.1f,0.9f,1.1f,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
		scaleAnim.setDuration(200);
		scaleAnim.setInterpolator(new LinearInterpolator());
		scaleAnim.setFillBefore(true);
		scaleAnim.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {}
			
			@Override
			public void onAnimationRepeat(Animation animation) {}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				Listener.animEnd(v);
			}
		});
		v.startAnimation(scaleAnim);
	}
	
	
	public static void showImgAnim(View v){
		ScaleAnimation scaleAnim=new ScaleAnimation(0.0f,1.0f,0.0f,1.0f,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
		scaleAnim.setDuration(500);
		scaleAnim.setInterpolator(new LinearInterpolator());
		scaleAnim.setFillBefore(true);
		v.startAnimation(scaleAnim);
	}
	
	
	public interface MyAnimListner{
		public void animEnd(View v);
	} 
	
}
