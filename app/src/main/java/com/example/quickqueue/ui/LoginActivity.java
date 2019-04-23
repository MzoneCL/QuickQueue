package com.example.quickqueue.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogInCallback;
import com.example.quickqueue.R;
import com.example.quickqueue.util.Utility;

/**
  *   用户注册
 */

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private SharedPreferences preferences;
    private SharedPreferences.Editor preferencesEditor;

    private EditText accountEdit;
    private EditText passwordEdit;
    private Button login;
    private Button forget;
    private ImageButton back;
    private CheckBox rememberPassword; // 记住密码

    View mLoginFormView;
    ProgressBar mProgressView;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        accountEdit = (EditText)findViewById(R.id.account);
        passwordEdit = (EditText)findViewById(R.id.password);
        login = (Button)findViewById(R.id.button_login);
        forget = (Button)findViewById(R.id.button_forget);
        back = (ImageButton)findViewById(R.id.back_to_start);
        rememberPassword = (CheckBox)findViewById(R.id.remember_password);

        mLoginFormView = (View)findViewById(R.id.login_form);
        mProgressView = (ProgressBar)findViewById(R.id.progress_bar_login);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isRemember = preferences.getBoolean("remember_password_user", false);

        if (isRemember){
            String account = preferences.getString("account_user", "");
            String password = preferences.getString("password_user", "");
            accountEdit.setText(account);
            passwordEdit.setText(password);
            rememberPassword.setChecked(true);
        }

        login.setOnClickListener(this);
        back.setOnClickListener(this);
    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(LoginActivity.this, TheStartActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.button_login:
                attemptLogin();
                break;
            case R.id.back_to_start:
                Intent intent = new Intent(LoginActivity.this, TheStartActivity.class);
                startActivity(intent);
                finish();
                break;
             default:
                 break;
        }
    }

    void attemptLogin(){ // 登录函数

        if(!Utility.isNetworkConnected(LoginActivity.this)){
            Toast.makeText(LoginActivity.this, "网络未连接", Toast.LENGTH_SHORT).show();
            return;
        }

        final String account = accountEdit.getText().toString();
        final String pswd = passwordEdit.getText().toString();

        boolean cancel = false;

        if(TextUtils.isEmpty(account)){
            Toast.makeText(LoginActivity.this, "请输入账号", Toast.LENGTH_SHORT).show();
            cancel = true;
            return;
        }
        if (TextUtils.isEmpty(pswd)){
            Toast.makeText(LoginActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
            cancel = true;
            return;
        }

        if (cancel){

        }else{
            showProgress(true);

            AVUser.logInInBackground(account, pswd, new LogInCallback<AVUser>() {
                @Override
                public void done(AVUser avUser, AVException e) {
                    if(e == null){
                            // 记住密码
                            preferencesEditor = preferences.edit();
                            preferencesEditor.putBoolean("remember_password_user", true);
                            preferencesEditor.putString("account_user", account);
                            preferencesEditor.putString("password_user", pswd);
                            preferencesEditor.putBoolean("user_has_login", true);
                            preferencesEditor.apply();

                            Utility.current_user_account = account;

                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            LoginActivity.this.finish();
                    }else{
                        showProgress(false);
                        if (e.getCode() == 211){
                            Toast.makeText(LoginActivity.this, "该账号不存在", Toast.LENGTH_SHORT).show();
                        }else if (e.getCode() == 210){
                            Toast.makeText(LoginActivity.this, "账号和密码不匹配", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(LoginActivity.this, e.getMessage() + "错误编码：" + e.getCode(), Toast.LENGTH_SHORT).show();
                        }
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

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}
