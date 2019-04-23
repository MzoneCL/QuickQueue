package com.example.quickqueue.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;
import com.example.quickqueue.R;
import com.example.quickqueue.util.Utility;

import java.util.List;

public class MerchantInitQueue extends AppCompatActivity {

    EditText service_time;

    EditText max_num;

    private SharedPreferences preferences;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchant_init_queue);

        service_time = (EditText)findViewById(R.id.edittext_service_time);
        max_num = (EditText)findViewById(R.id.edit_text_maxnum);

        preferences = PreferenceManager.getDefaultSharedPreferences(MerchantInitQueue.this);

        service_time.setText(preferences.getString("service_time_once", ""));
        max_num.setText(preferences.getString("max_num",""));

        Button commit = (Button)findViewById(R.id.commit);
        commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initQueue();
            }
        });
    }

    // 商家创建新队列
    public void initQueue(){
        final String service_time_text = service_time.getText().toString();
        final String max_num_text = max_num.getText().toString();

        if (!Utility.isNumeric(service_time_text) || service_time_text.equals("")){
            Toast.makeText(MerchantInitQueue.this, "单次时长必须为整数！", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Utility.isNumeric(max_num_text) || max_num_text.equals("")){
            Toast.makeText(MerchantInitQueue.this, "最大排队人数必须为整数！", Toast.LENGTH_SHORT).show();
            return;
        }

        AVQuery<AVObject> query = new AVQuery<>("Merchant");
        query.whereEqualTo("account", Utility.current_merchant_account);

        Log.d("当前登录商家", Utility.current_merchant_account);

        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null){

                    AVObject merchant = list.get(0);
                    merchant.put("max_num", Integer.parseInt(max_num_text));

                    Utility.max_num_in_queue = Integer.parseInt(max_num_text);

                    merchant.put("service_time_once", Integer.parseInt(service_time_text));


                    SharedPreferences.Editor preferencesEditor;
                    preferencesEditor = preferences.edit();
                    preferencesEditor.putString("max_num",max_num_text );
                    preferencesEditor.putString("service_time_once", service_time_text);
                    preferencesEditor.apply();

                    merchant.put("has_a_queue", 1);
                    merchant.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(AVException e) {
                            if (e == null){
                                Utility.merchantHasAQueue = true;
                                startActivity(new Intent(MerchantInitQueue.this, MerchantActivity.class));

                                /*MerchantActivity merchantActivity = new MerchantActivity();
                                MerchantActivity.RequestForUsersThread thread = merchantActivity.new RequestForUsersThread();
                                thread.start();*/

                                MerchantInitQueue.this.finish();
                            }else {
                                Toast.makeText(MerchantInitQueue.this, "创建队列失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }else {
                    Toast.makeText(MerchantInitQueue.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
