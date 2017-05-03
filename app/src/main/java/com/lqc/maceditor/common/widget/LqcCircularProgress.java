package com.lqc.maceditor.common.widget;

import com.lqc.maceditor.R;
import com.lqc.maceditor.R.anim;
import com.lqc.maceditor.R.id;
import com.lqc.maceditor.R.layout;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

public class LqcCircularProgress {

    public static ProgressDialog show(Context context){
        ProgressDialog myPD=ProgressDialog.show(context,null, null, true, false);
        myPD.setContentView(R.layout.lqc_progress_view);
        myPD.setCancelable(false);
        //旋转动画
        ImageView imgView=(ImageView) myPD.findViewById(R.id.progress_img);
        Animation operatingAnim = AnimationUtils.loadAnimation(context, R.anim.lqc_rotating);
        LinearInterpolator lin = new LinearInterpolator();
        operatingAnim.setInterpolator(lin);
        imgView.startAnimation(operatingAnim);
		/*Session mSession = Session.getInstance(context);
		
		myPD.setContentView(R.layout.layout_coming_soon);
		myPD.setCancelable(false);
		View view=myPD.findViewById(R.id.comingsoon_root);
		view.getLayoutParams().height=mSession.getScreenHeight();
		view.getLayoutParams().width=mSession.getScreenWidth();
		
		ImageView xmlView = (ImageView) myPD.findViewById(R.id.coming_soon_point);
		AnimationDrawable xmlAnim=(AnimationDrawable) context.getResources().getDrawable(R.anim.anim_comingsoon);
		xmlView.setImageDrawable(xmlAnim);
		xmlAnim.start();*/
        return myPD;
    }


}
