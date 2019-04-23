package com.example.quickqueue.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVCloudQueryResult;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.CloudQueryCallback;
import com.avos.avoscloud.FindCallback;
import com.example.quickqueue.R;
import com.example.quickqueue.ui.MainActivity;
import com.example.quickqueue.ui.MerchantInfo;
import com.example.quickqueue.ui.NavigationAndLocationActivity;
import com.example.quickqueue.util.Utility;

import java.util.List;

/**
 * Created by Congli Ma on 2018/4/26.
 */

public class QueueFragment extends Fragment implements View.OnClickListener {

    CountDownTimer timer;

    TextView queue_fragment_merchant_name;
    TextView queue_fragment_merchant_text;
    TextView queue_fragment_time_get_number;
    TextView merchant_address;
    TextView queue_fragment_wating_number;
    TextView queue_fragment_waiting_time;

    Button cancel_queue;
    Button buttonWhereToGo;

    SharedPreferences preferences;

    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.queue_fragment, container, false);
        return view;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        buttonWhereToGo = (Button)view.findViewById(R.id.queue_fragment_button_go_where);
        buttonWhereToGo.setOnClickListener(this);
        cancel_queue = (Button)view.findViewById(R.id.queue_fragment_button_cancel_queue);
        cancel_queue.setOnClickListener(this);

        TextView notInQueue = (TextView)view.findViewById(R.id.queue_when_not_in_queue);
        LinearLayout inQueue = (LinearLayout)view.findViewById(R.id.queue_when_in_queue);

        queue_fragment_merchant_name = (TextView)view.findViewById(R.id.queue_fragment_merchant_text_title);
        queue_fragment_merchant_text = (TextView)view.findViewById(R.id.queue_fragment_merchant_text);
        queue_fragment_time_get_number = (TextView)view.findViewById(R.id.queue_fragment_time_get_number);
        merchant_address = (TextView)view.findViewById(R.id.queue_fragment_merchant_location);
        queue_fragment_wating_number = (TextView)view.findViewById(R.id.queue_fragment_waiting_number);
        queue_fragment_waiting_time = (TextView)view.findViewById(R.id.queue_fragment_waiting_time);


        if (AVUser.getCurrentUser() == null){

        }else {
            if (AVUser.getCurrentUser().get("status").toString().equals("inQueue")){
                initQueueUI();
                inQueue.setVisibility(View.VISIBLE);
            }else {
                notInQueue.setVisibility(View.VISIBLE);
            }
        }

        queue_fragment_merchant_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MerchantInfo.class));
            }
        });
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.queue_fragment_button_go_where:
                final Intent intent = new Intent(getActivity(), NavigationAndLocationActivity.class);
                startActivity(intent);
                break;

            case R.id.queue_fragment_button_cancel_queue:
                AlertDialog alert=new AlertDialog.Builder(getActivity()).create();
                alert.setMessage("确定要取消排队吗？");
                //添加取消按钮
                alert.setButton(DialogInterface.BUTTON_NEGATIVE,"不",new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                    }
                });
                //添加"确定"按钮
                alert.setButton(DialogInterface.BUTTON_POSITIVE,"是的", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        Utility.userQuitQueue(getActivity());
                    }
                });
                alert.show();

            default:
                break;
        }
    }

    public void initQueueUI(){
        if (MainActivity.merchant == null){
            // 先要做这个判断，不然会有空指针错误
        }else {
            queue_fragment_merchant_name.setText(MainActivity.merchant.get("merchant_name").toString());
            queue_fragment_merchant_text.setText(MainActivity.merchant.get("merchant_name").toString());
            merchant_address.setText(MainActivity.merchant.get("address").toString());

            if (!Utility.seq_number.equals("")){
                queue_fragment_time_get_number.setText(Utility.time_in_queue);
                queue_fragment_wating_number.setText(Utility.seq_number);
            //    queue_fragment_waiting_time.setText(Utility.waiting_time + "min");
                return;
            }

            AVQuery<AVObject> query = new AVQuery<>("Queue");
            query.whereEqualTo("account_user", preferences.getString("account_user",""));
            query.findInBackground(new FindCallback<AVObject>() {
                @Override
                public void done(List<AVObject> list, AVException e) {
                    if (e == null){
                        queue_fragment_time_get_number.setText(list.get(0).get("time_in_queue").toString());
                        Utility.time_in_queue = list.get(0).get("time_in_queue").toString();
                        queue_fragment_wating_number.setText(list.get(0).get("Seq_number").toString());
                        Utility.seq_number = list.get(0).get("Seq_number").toString();
              //          queue_fragment_waiting_time.setText(list.get(0).get("waiting_time").toString() + "min");
                        Utility.waiting_time = list.get(0).get("waiting_time").toString();
                        timer = new CountDownTimer(Integer.parseInt(Utility.waiting_time) * 60 * 1000, 1000) {
                            /**
                             * 固定间隔被调用,就是每隔countDownInterval会回调一次方法onTick
                             * @param millisUntilFinished
                             */
                            @Override
                            public void onTick(long millisUntilFinished) {
                                queue_fragment_waiting_time.setText(formatTime(millisUntilFinished));
                            }

                            /**
                             * 倒计时完成时被调用
                             */
                            @Override
                            public void onFinish() {
                                queue_fragment_waiting_time.setText("00:00");
                                Utility.notifyUser(getActivity());
                                Utility.userQuitQueue(getActivity());
                            }
                        };
                        timerStart();
                    }else {
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    /**
     * 倒数计时器
     */


    /**
     * 将毫秒转化为 分钟：秒 的格式
     *
     * @param millisecond 毫秒
     * @return
     */
    public String formatTime(long millisecond) {
        int minute;//分钟
        int second;//秒数
        minute = (int) ((millisecond / 1000) / 60);
        second = (int) ((millisecond / 1000) % 60);
        if (minute < 10) {
            if (second < 10) {
                return "0" + minute + ":" + "0" + second;
            } else {
                return "0" + minute + ":" + second;
            }
        }else {
            if (second < 10) {
                return minute + ":" + "0" + second;
            } else {
                return minute + ":" + second;
            }
        }
    }

    /**
     * 取消倒计时
     */
    public void timerCancel() {
        timer.cancel();
    }

    /**
     * 开始倒计时
     */
    public void timerStart() {
        timer.start();
    }
}

