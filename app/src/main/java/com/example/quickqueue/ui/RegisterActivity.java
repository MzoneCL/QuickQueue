package com.example.quickqueue.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SignUpCallback;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.example.quickqueue.R;
import com.example.quickqueue.util.Utility;

import java.util.HashMap;


public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    public LocationClient locationClient;

    EditText register_account;
    EditText register_password;
    EditText register_nickname;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        locationClient = new LocationClient(getApplicationContext());
        locationClient.registerLocationListener(new RegisterActivity.MyLocationListener());

        setContentView(R.layout.activity_register);

        requestLocation();

        ImageButton button_back = (ImageButton)findViewById(R.id.back_to_login);
        register_account = (EditText)findViewById(R.id.register_account);
        register_password = (EditText)findViewById(R.id.register_password);
        register_nickname = (EditText)findViewById(R.id.register_nickname_user);
        Button register_button = (Button)findViewById(R.id.register_button);

        button_back.setOnClickListener(this);
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

    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location){
        }
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.back_to_login:
                Intent intent = new Intent(RegisterActivity.this, TheStartActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.register_button:
                attemptRegister();
            default:
                break;
        }
    }

    public void attemptRegister(){

        if(!Utility.isNetworkConnected(RegisterActivity.this)){
            Toast.makeText(RegisterActivity.this, "网络未连接", Toast.LENGTH_SHORT).show();
            return;
        }

        String username = register_account.getText().toString();
        String password = register_password.getText().toString();
        String nickname = register_nickname.getText().toString();
        if (nickname.equals("")){
            Toast.makeText(RegisterActivity.this, "请输入昵称", Toast.LENGTH_SHORT).show();
            return;
        }
        else if (nickname.contains(" ")){
            Toast.makeText(RegisterActivity.this, "昵称不能包含空格", Toast.LENGTH_SHORT).show();
            return;
        }
        else if (Utility.isSpecialChar(nickname)){
            Toast.makeText(RegisterActivity.this, "昵称不能包含特殊字符", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Utility.isPhoneNumber(username)){
            Toast.makeText(RegisterActivity.this, "请输入正确的电话号码", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Utility.isPasswordOK(password)){
            Toast.makeText(RegisterActivity.this, "密码不能包含特殊字符，且为 8 - 16 位", Toast.LENGTH_SHORT).show();
            return;
        }

        AVUser user = new AVUser();// 新建 AVUser 对象实例
        user.setUsername(username);// 设置用户名
        user.setPassword(password);// 设置密码
        user.put("nickname", nickname); // 设置nickname
        // user.put("flag", "user"); // 设置标记，"user"表示普通用户
        user.put("status", "notInQueue");
        AVFile file = new AVFile("headsculpture.jpg", "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1540833429847&di=29b2a0e260bd5af7eb6880a26b9fbb2e&imgtype=0&src=http%3A%2F%2Fbpic.588ku.com%2Felement_origin_min_pic%2F01%2F31%2F87%2F96573b585a7c9c4.jpg", new HashMap<String, Object>());
        user.put("headsculpture", file);

        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(AVException e) {
                if (e == null) {
                    // 注册成功，把用户对象赋值给当前用户 AVUser.getCurrentUser()
                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                    Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                    RegisterActivity.this.finish();
                } else {
                    // 失败的原因可能有多种，常见的是用户名已经存在。
                    Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
