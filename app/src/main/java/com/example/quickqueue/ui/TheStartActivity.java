package com.example.quickqueue.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.quickqueue.R;

public class TheStartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_the_start);

        Button button_register = (Button)findViewById(R.id.button_to_register_user);
        Button button_login = (Button)findViewById(R.id.button_to_login);
        Button button_merchant_register = (Button)findViewById(R.id.button_to_register_merchant) ;
        Button button_merchant_login = (Button)findViewById(R.id.button_merchant_to_login);
        button_merchant_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TheStartActivity.this, MerchantLoginActivity.class);
                startActivity(intent);
            }
        });


        button_merchant_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TheStartActivity.this, MerchantRegisterActivity.class);
                startActivity(intent);
            }
        });


        button_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TheStartActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });


        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TheStartActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    //返回到桌面
    @Override
    public void onBackPressed(){
        Intent intent=new Intent();
        intent.setAction("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addCategory("android.intent.category.MONKEY");
        startActivity(intent);
    }
}
