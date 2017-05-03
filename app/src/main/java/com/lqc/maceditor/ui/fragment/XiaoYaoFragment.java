package com.lqc.maceditor.ui.fragment;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.lqc.maceditor.R;
import com.lqc.maceditor.common.base.BaseFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class XiaoYaoFragment extends BaseFragment {



    private final int TM_IMEI=1;
    private final int TM_PHONE=2;
    private final int TM_IMSI=3;
    private final int TM_SIMSERIAL=4;

    private EditText macEdit,imeiEdit,phoneEdit,imsiEdit,simserialEdit;
    private EditText lunchTimeEdit;
    private Button bathBtn;
    private ProgressDialog mDialog;

    private ImageView switchView;
    private final String targetPackName="com.yds.courier";
    private boolean bathEnable;
    private int noticeIndex=0;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_main, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

}
