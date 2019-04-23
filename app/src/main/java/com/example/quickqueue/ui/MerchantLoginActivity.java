package com.example.quickqueue.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.LogInCallback;
import com.example.quickqueue.R;
import com.example.quickqueue.util.Utility;

import java.util.List;

public class MerchantLoginActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText accountEdit;
    private EditText passwordEdit;
    private Button login;
    private Button forget;
    private ImageButton back;
    private CheckBox rememberPassword;

    private SharedPreferences preferences;
    private SharedPreferences.Editor preferencesEditor;

    private String account;
    private String password;

    View merchantLoginFormView;
    ProgressBar merchantLoginProgressView;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchant_login);
        //在加载布局文件前判断是否登陆过
        preferences= PreferenceManager.getDefaultSharedPreferences(this);

        accountEdit = (EditText)findViewById(R.id.account_merchant_login);
        passwordEdit = (EditText)findViewById(R.id.password_merchant_login);
        login = (Button)findViewById(R.id.button_login_merchant);
        forget = (Button)findViewById(R.id.button_forget_merchant_login);
        back = (ImageButton)findViewById(R.id.back_to_start_from_merchant_login);
        rememberPassword = (CheckBox)findViewById(R.id.remember_password_merchant);

        merchantLoginFormView = (View)findViewById(R.id.login_form_merchant);
        merchantLoginProgressView = (ProgressBar)findViewById(R.id.progress_bar_login_merchant);

        boolean isRemember = preferences.getBoolean("remember_password_merchant", false);
        if(isRemember){
            account = preferences.getString("account_merchant","");
            password = preferences.getString("password_merchant", "");
            accountEdit.setText(account);
            passwordEdit.setText(password);
            rememberPassword.setChecked(true);
        }
        login.setOnClickListener(this);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MerchantLoginActivity.this, TheStartActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(MerchantLoginActivity.this, TheStartActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.button_login_merchant:
                attemptLogin();
                break;
            case R.id.back_to_start_from_merchant_login:
                startActivity(new Intent(MerchantLoginActivity.this, TheStartActivity.class));
                finish();
                break;
        }
    }

    void attemptLogin(){ // 登录函数

        if(!Utility.isNetworkConnected(MerchantLoginActivity.this)){
            Toast.makeText(MerchantLoginActivity.this, "网络未连接", Toast.LENGTH_SHORT).show();
            return;
        }

        final String account = accountEdit.getText().toString();
        final String pswd = passwordEdit.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if(TextUtils.isEmpty(account)){
            Toast.makeText(MerchantLoginActivity.this, "请输入账号", Toast.LENGTH_SHORT).show();
            cancel = true;
            return;
        }
        if (TextUtils.isEmpty(pswd)){
            Toast.makeText(MerchantLoginActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
            cancel = true;
            return;
        }

        if (cancel){

        }else{
            showProgress(true);

            AVQuery<AVObject> query = new AVQuery<>("Merchant");
            query.whereEqualTo("account", account);
            query.findInBackground(new FindCallback<AVObject>() {
                @Override
                public void done(List<AVObject> list, AVException e) {
                    if (e == null){
                        if (list.isEmpty()){
                            Toast.makeText(MerchantLoginActivity.this, "账号不存在", Toast.LENGTH_SHORT).show();
                            showProgress(false);
                        }else {
                            if (list.get(0).get("password").toString().equals(pswd)){

                                // 记住密码
                                preferencesEditor = preferences.edit();
                                if (rememberPassword.isChecked()){
                                    preferencesEditor.putBoolean("remember_password_merchant", true);
                                    preferencesEditor.putString("account_merchant", account);
                                    preferencesEditor.putString("password_merchant", pswd);
                                }else {
                                    preferencesEditor.remove("remember_password_merchant");
                                    preferencesEditor.remove("account_merchant");
                                    preferencesEditor.remove("password_merchant");
                                }
                                preferencesEditor.putString("merchant_account_login", account);
                                preferencesEditor.putBoolean("merchant_has_login", true);
                                preferencesEditor.apply();

                                Utility.current_merchant_account = account;

                                startActivity(new Intent(MerchantLoginActivity.this, MerchantActivity.class));
                                MerchantLoginActivity.this.finish();
                            }else {
                                Toast.makeText(MerchantLoginActivity.this, "账号和密码不匹配", Toast.LENGTH_SHORT).show();
                                showProgress(false);
                            }
                        }
                    }else {
                        showProgress(false);
                        Toast.makeText(MerchantLoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            merchantLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            merchantLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    merchantLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            merchantLoginProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            merchantLoginProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    merchantLoginProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            merchantLoginProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            merchantLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}
