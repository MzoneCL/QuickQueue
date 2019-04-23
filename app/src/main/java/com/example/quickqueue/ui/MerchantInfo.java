package com.example.quickqueue.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.quickqueue.R;

public class MerchantInfo extends AppCompatActivity {

    TextView merhcant_name;
    TextView merchant_address;
    TextView merchant_statement;
    TextView merchant_account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchant_info);

        merhcant_name = (TextView)findViewById(R.id.merchant_info_name);
        merchant_address = (TextView)findViewById(R.id.merchant_info_address);
        merchant_statement = (TextView)findViewById(R.id.merchant_info_statement);
        merchant_account = (TextView)findViewById(R.id.merchant_info_account);

        initMerchantInfoUI();

        Button call = (Button)findViewById(R.id.call_merchant);
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(Intent.ACTION_DIAL);
                intent1.setData(Uri.parse("tel:" + MainActivity.merchant.get("account")));
                startActivity(intent1);
            }
        });

    }

    public void initMerchantInfoUI(){

        if (MainActivity.merchant == null){
            // 先要做这个判断，不然会有空指针错误
        }else {
            merchant_account.setText(MainActivity.merchant.get("account").toString());
            merhcant_name.setText(MainActivity.merchant.get("merchant_name").toString());
            merchant_address.setText(MainActivity.merchant.get("address").toString());
            merchant_statement.setText(MainActivity.merchant.get("description").toString());
        }

    }
}
