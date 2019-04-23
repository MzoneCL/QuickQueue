package com.example.quickqueue.ui;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.quickqueue.R;

public class UserInfoInQueue extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info_in_queue);

        TextView user_name = (TextView)findViewById(R.id.user_info_name);
        TextView user_account = (TextView)findViewById(R.id.user_info_account);

        final Intent intent = getIntent();
        String userName = intent.getStringExtra("nickname");
        final String userAccount = intent.getStringExtra("account");

        user_name.setText(userName);
        user_account.setText(userAccount);

        Button call = (Button)findViewById(R.id.call);
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(Intent.ACTION_DIAL);
                intent1.setData(Uri.parse("tel:" + userAccount));
                startActivity(intent1);
            }
        });
    }
}
