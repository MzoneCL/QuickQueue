package com.example.quickqueue.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.avos.avoscloud.AVUser;
import com.example.quickqueue.R;

public class WelcomeActivity extends Activity{

    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        if (preferences.getBoolean("user_has_login", false)){
            startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
            Log.d("WelcomeActivity", "已登录");
            WelcomeActivity.this.finish();
        } else if (preferences.getBoolean("merchant_has_login", false)){
            startActivity(new Intent(WelcomeActivity.this, MerchantActivity.class));
            WelcomeActivity.this.finish();
        } else{
                /**
                 * 延迟1秒进入主界面
                 */
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent=new Intent(WelcomeActivity.this,TheStartActivity.class);
                        startActivity(intent);
                        WelcomeActivity.this.finish();
                    }
                },1000*1);
            }
        }
}
