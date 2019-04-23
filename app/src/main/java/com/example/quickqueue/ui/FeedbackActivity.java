package com.example.quickqueue.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.avos.avoscloud.AVObject;
import com.example.quickqueue.R;
import com.example.quickqueue.util.Utility;

public class FeedbackActivity extends AppCompatActivity {

    private EditText feedback_edittext;

    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        feedback_edittext = (EditText)findViewById(R.id.feedback_edit_text);

        preferences = PreferenceManager.getDefaultSharedPreferences(FeedbackActivity.this);

        ImageButton back = (ImageButton)findViewById(R.id.back_to_mainactivity_from_feedback);
        Button send = (Button)findViewById(R.id.feedback_button_send);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = feedback_edittext.getText().toString();
                if (content.equals("")){
                    Toast.makeText(FeedbackActivity.this, "请输入反馈信息！", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = getIntent();
                String flag = intent.getStringExtra("flag");
                if (flag.equals("merchant")){
                    flag = "Merchant" + preferences.getString("merchant_account_login", "");
                }if (flag.equals("user")){
                    flag = "_User" + preferences.getString("account_user", "");
                }
                AVObject feedback = new AVObject("Feedback");
                feedback.put("flag", flag);
                feedback.put("content", content);
                feedback.saveInBackground();

                Intent intent1 = new Intent(FeedbackActivity.this, FeedbackSuccess.class);
                if (flag.contains("Merchant")){
                    intent1.putExtra("flag", "merchant");
                }
                if (flag.contains("User")){
                    intent1.putExtra("flag", "user");
                }
                startActivity(intent1);
                finish();
            }
        });
    }
}
