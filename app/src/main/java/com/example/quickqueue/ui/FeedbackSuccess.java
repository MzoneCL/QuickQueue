package com.example.quickqueue.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.quickqueue.R;

public class FeedbackSuccess extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_success);

        Button back = (Button)findViewById(R.id.back_from_feedback);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = getIntent();
                if (intent.getStringExtra("flag").equals("merchant")){
                    startActivity(new Intent(FeedbackSuccess.this, MerchantActivity.class));
                }
                if (intent.getStringExtra("flag").equals("user")){
                    startActivity(new Intent(FeedbackSuccess.this, MainActivity.class));
                }

                finish();
            }
        });
    }
}
