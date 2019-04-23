package com.example.quickqueue.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.quickqueue.R;

public class MerchantRegisterSuccessActivity extends AppCompatActivity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchant_register_success);

        Button success = (Button)findViewById(R.id.button_merchant_success_regist_to_login);
        success.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MerchantRegisterSuccessActivity.this, MerchantLoginActivity.class);
                startActivity(intent);
            }
        });
    }
}
