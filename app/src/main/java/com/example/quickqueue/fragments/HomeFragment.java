package com.example.quickqueue.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.avos.avoscloud.AVUser;
import com.example.quickqueue.R;
import com.example.quickqueue.ui.CustomScanActivity;
import com.example.quickqueue.util.Utility;
import com.google.zxing.integration.android.IntentIntegrator;

/**
 * Created by Congli Ma on 2018/4/26.
 */

public class HomeFragment extends Fragment {
    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.home_fragment, container, false);
        return view;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        ImageButton scan = (ImageButton)view.findViewById(R.id.add_into_queue);
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!Utility.isNetworkConnected(getActivity())){
                    Toast.makeText(getActivity(), "网络未连接", Toast.LENGTH_SHORT).show();
                    return;
                }
                customScan();
            }
        });

        RelativeLayout userInQueue = (RelativeLayout)view.findViewById(R.id.home_fragment_user_in_queue);
        RelativeLayout userNotInQueue = (RelativeLayout)view.findViewById(R.id.home_fragment_user_not_in_queue);

        if (AVUser.getCurrentUser() == null){

        }else {
            if (AVUser.getCurrentUser().get("status").toString().equals("inQueue")){
                userInQueue.setVisibility(View.VISIBLE);
            }else {
                userNotInQueue.setVisibility(View.VISIBLE);
            }
        }
    }

    public void customScan(){
        new IntentIntegrator(getActivity())
                .setOrientationLocked(false)
                .setPrompt("请将二维码置于取景框内") // 设置提示语
                .setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)  // 仅扫描二维码
                .setCaptureActivity(CustomScanActivity.class) // 设置自定义的activity是CustomActivity
                .initiateScan(); // 初始化扫描
    }
}

