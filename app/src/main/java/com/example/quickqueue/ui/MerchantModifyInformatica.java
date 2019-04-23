package com.example.quickqueue.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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


public class MerchantModifyInformatica extends AppCompatActivity {

    SharedPreferences preferences;

    EditText merchant_name_edittext;
    EditText merchant_old_password_eddittext;
    EditText merchant_new_password_eddittext;
    EditText merchant_modify_address;
    EditText merchant_modify_statement;

    String old_pswd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchant_modify_informatica);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        merchant_name_edittext = (EditText)findViewById(R.id.modify_name_merchant_edittext);
        merchant_old_password_eddittext = (EditText)findViewById(R.id.merchant_modify_password_old);
        merchant_new_password_eddittext = (EditText)findViewById(R.id.merchant_modify_password_new);
        merchant_modify_address = (EditText)findViewById(R.id.merchant_modify_address_eddittext);
        merchant_modify_statement = (EditText)findViewById(R.id.merchant_modify_statement_edittext);

        initView();

        final Button submit = (Button)findViewById(R.id.merchant_modify_submit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });

    }

    public void initView(){
        AVQuery<AVObject> query = new AVQuery<>("Merchant");
        query.whereEqualTo("account", preferences.getString("merchant_account_login", ""));
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null){
                    AVObject merchant = list.get(0);
                    merchant_name_edittext.setText(merchant.get("merchant_name").toString());
                    merchant_modify_address.setText(merchant.get("address").toString());
                    merchant_modify_statement.setText(merchant.get("description").toString());
                    old_pswd = merchant.get("password").toString();
                }else {
                    Toast.makeText(MerchantModifyInformatica.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void submit(){
        final String merchant_name = merchant_name_edittext.getText().toString();
        final String address = merchant_modify_address.getText().toString();
        final String description = merchant_modify_statement.getText().toString();
        String pswd_old = merchant_old_password_eddittext.getText().toString();
        final String pewd_new = merchant_new_password_eddittext.getText().toString();

        if (!pswd_old.equals(old_pswd)){
            Toast.makeText(MerchantModifyInformatica.this, "原密码不正确", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Utility.isPasswordOK(pewd_new)){
            Toast.makeText(MerchantModifyInformatica.this, "新密码不合法，请重新输入", Toast.LENGTH_SHORT).show();
            return;
        }

        AVQuery<AVObject> query = new AVQuery<>("Merchant");
        query.whereEqualTo("account", preferences.getString("merchant_account_login", ""));
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null){
                    AVObject merchant = list.get(0);
                    merchant.put("description", description);
                    merchant.put("address", address);
                    merchant.put("password", pewd_new);
                    merchant.put("merchant_name", merchant_name);
                    merchant.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(AVException e) {
                            if (e == null){
                                Toast.makeText(MerchantModifyInformatica.this, "修改成功，请重新登录！", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(MerchantModifyInformatica.this, MerchantLoginActivity.class));
                                MerchantModifyInformatica.this.finish();
                            }else {
                                Toast.makeText(MerchantModifyInformatica.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else {
                    Toast.makeText(MerchantModifyInformatica.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
