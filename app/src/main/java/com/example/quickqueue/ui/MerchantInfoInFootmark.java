package com.example.quickqueue.ui;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.example.quickqueue.R;

import java.util.List;

public class MerchantInfoInFootmark extends AppCompatActivity {

    TextView merhcant_name;
    TextView merchant_address;
    TextView merchant_statement;
    TextView merchant_account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchant_info_in_footmark);

        merhcant_name = (TextView)findViewById(R.id.merchant_info_name_footmark);
        merchant_address = (TextView)findViewById(R.id.merchant_info_address_footmark);
        merchant_statement = (TextView)findViewById(R.id.merchant_info_statement_footmark);
        merchant_account = (TextView)findViewById(R.id.merchant_info_account_footmark);

        initMerchantInfoUI();

        Button call = (Button)findViewById(R.id.call_merchant_footmark);
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = getIntent();
                String account_merchant = intent.getStringExtra("account_merchant");

                Intent intent1 = new Intent(Intent.ACTION_DIAL);
                intent1.setData(Uri.parse("tel:" + account_merchant));
                startActivity(intent1);
            }
        });

    }

    public void initMerchantInfoUI(){

        Intent intent = getIntent();
        String account_merchant = intent.getStringExtra("account_merchant");
        AVQuery<AVObject> query = new AVQuery<>("Merchant");
        query.whereEqualTo("account", account_merchant);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null){
                    merhcant_name.setText(list.get(0).get("merchant_name").toString());
                    merchant_address.setText(list.get(0).get("address").toString());
                    merchant_account.setText(list.get(0).get("account").toString());
                    merchant_statement.setText(list.get(0).get("description").toString());
                }else {

                }
            }
        });

    }
}
