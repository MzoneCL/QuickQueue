package com.example.quickqueue.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;
import com.avos.avoscloud.SignUpCallback;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.example.quickqueue.R;
import com.example.quickqueue.util.Utility;

import java.util.HashMap;
import java.util.List;

public class MerchantRegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText name;
    private EditText account;
    private EditText password;

    public LocationClient locationClient;

    private double latitude;
    private double longitude;

    private String address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        locationClient = new LocationClient(getApplicationContext());
        locationClient.registerLocationListener(new MerchantRegisterActivity.MyLocationListener());

        setContentView(R.layout.activity_merchant_register);

        requestLocation();

        ImageButton back = (ImageButton)findViewById(R.id.back_to_start1);
        name = (EditText)findViewById(R.id.merchant_register_name);
        account = (EditText)findViewById(R.id.merchant_register_account);
        password = (EditText)findViewById(R.id.merchant_register_password);
        Button register_button = (Button)findViewById(R.id.merchant_register_button);

        back.setOnClickListener(this);
        register_button.setOnClickListener(this);


    }

    private void requestLocation(){
        initLocation();
        locationClient.start();
    }

    private void initLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setIsNeedAddress(true);
        locationClient.setLocOption(option);
    }

    public class MyLocationListener implements BDLocationListener{
        @Override
        public void onReceiveLocation(BDLocation location){
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            address = location.getAddrStr() + "";

            Log.d("纬度", latitude + "");
            Log.d("经度", longitude + "");
            Log.d("地址", address);
        }
    }


    // 处理点击事件
    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.back_to_start1:
                Intent intent = new Intent(MerchantRegisterActivity.this, TheStartActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.merchant_register_button:
                attemptRegister();
                break;
            default:
                break;
        }
    }

    public void attemptRegister(){

        if(!Utility.isNetworkConnected(MerchantRegisterActivity.this)){
            Toast.makeText(MerchantRegisterActivity.this, "网络未连接", Toast.LENGTH_SHORT).show();
            return;
        }

        String account_str = account.getText().toString();
        String password_str = password.getText().toString();
        String merchantname = name.getText().toString();

        if (merchantname.equals("")){
            Toast.makeText(MerchantRegisterActivity.this, "请输入店名", Toast.LENGTH_SHORT).show();
            return;
        }
        if (merchantname.contains(" ")){
            Toast.makeText(MerchantRegisterActivity.this, "店名不能包含空格", Toast.LENGTH_SHORT).show();
            return;
        }
        else if (Utility.isSpecialChar(merchantname)){
            Toast.makeText(MerchantRegisterActivity.this, "店名不能包含特殊字符", Toast.LENGTH_SHORT).show();
            return;
        }
        if (account_str.equals("")){
            Toast.makeText(MerchantRegisterActivity.this, "请输入手机号", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Utility.isPhoneNumber(account_str)){
            Toast.makeText(MerchantRegisterActivity.this, "请输入正确的手机号", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password_str.equals("")){
            Toast.makeText(MerchantRegisterActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Utility.isPasswordOK(password_str)){
            Toast.makeText(MerchantRegisterActivity.this, "密码不能包含特殊字符，且为 8 - 16 位", Toast.LENGTH_SHORT).show();
            return;
        }

        AVObject merchant = new AVObject("Merchant");
        merchant.put("account", account_str);
        merchant.put("password", password_str);
        merchant.put("merchant_name", merchantname);
        merchant.put("address", address);
        merchant.put("latitude", latitude);
        merchant.put("longitude", longitude);
        merchant.put("description", "");
        merchant.put("has_a_queue", 0);
        merchant.put("max_num", 0);
        merchant.put("service_time_once", 0);

        // 默认头像
        AVFile file = new AVFile("headsculpture.jpg", "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1540832555700&di=836c629b9cab22fa314c65e88b6d36ab&imgtype=0&src=http%3A%2F%2Fku.90sjimg.com%2Felement_origin_min_pic%2F01%2F30%2F84%2F84573b26b76af68.jpg", new HashMap<String, Object>());
        merchant.put("headsculpture", file);

        merchant.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                if (e == null) {
                    startActivity(new Intent(MerchantRegisterActivity.this, MerchantLoginActivity.class));
                    Toast.makeText(MerchantRegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                    MerchantRegisterActivity.this.finish();
                } else {
                    Toast.makeText(MerchantRegisterActivity.this, e.getMessage() + e.getCode(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
