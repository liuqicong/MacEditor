package com.lqc.maceditor.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.lqc.maceditor.R;
import com.lqc.maceditor.common.base.BaseActivity;
import com.lqc.maceditor.common.util.MyAnimation;
import com.lqc.maceditor.common.util.RootCmd;
import com.lqc.maceditor.common.util.Utils;

/****
 * 命令表路径：/etc/init.microvirt.sh
 * @author Administrator
 *
 */
@SuppressLint("SdCardPath")
public class MainActivity extends BaseActivity implements OnClickListener{


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);


		if(RootCmd.haveRoot()){
            findViewById(R.id.main_btn_xiaoyao).setOnClickListener(this);
            findViewById(R.id.main_btn_yeshen).setOnClickListener(this);

            startActivity(new Intent(appContext, YeShenActivity.class));
		}else{
			Utils.show(appContext, "请先root手机");
			finish();
		}
	}


    @Override
    public void onClick(View v) {
        MyAnimation.clickAnim(v, new MyAnimation.MyAnimListner() {

            @Override
            public void animEnd(View v) {
                switch (v.getId()) {
                    case R.id.main_btn_xiaoyao:
                        mSession.setBathing(false);
                        startActivity(new Intent(appContext, XiaoYaoActivity.class));
                        break;

                    case R.id.main_btn_yeshen:
                        startActivity(new Intent(appContext, YeShenActivity.class));
                        break;
                }
            }
        });
    }
}