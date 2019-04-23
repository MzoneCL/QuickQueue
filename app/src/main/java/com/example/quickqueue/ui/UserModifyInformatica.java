package com.example.quickqueue.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.UpdatePasswordCallback;
import com.example.quickqueue.R;
import com.example.quickqueue.util.Utility;

public class UserModifyInformatica extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_modify_informatica);

        final EditText new_name_edittext = (EditText)findViewById(R.id.modify_nickname_user_edittext);
        final EditText old_password_edittext = (EditText)findViewById(R.id.user_modify_password_old);
        final EditText new_password_edittext = (EditText)findViewById(R.id.user_modify_password_new);

        Button submit = (Button)findViewById(R.id.user_modify_submit);

        String nickname = AVUser.getCurrentUser().get("nickname").toString();

        new_name_edittext.setText(nickname);

       submit.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {

               String new_name = new_name_edittext.getText().toString();
               String old_password = old_password_edittext.getText().toString();
               String new_password = new_password_edittext.getText().toString();

               if (new_name.equals("") || new_name.contains(" ")){
                   Toast.makeText(UserModifyInformatica.this, "昵称不能为空且不含空格", Toast.LENGTH_SHORT).show();
                   return;
               }else {
                    AVUser.getCurrentUser().put("nickname", new_name);
                    AVUser.getCurrentUser().saveInBackground();
               }

               if (!Utility.isPasswordOK(new_password)){
                   Toast.makeText(UserModifyInformatica.this, "新密码不合法，请重新输入", Toast.LENGTH_SHORT).show();
                   return;
               }

               AVUser.getCurrentUser().updatePasswordInBackground(old_password, new_password, new UpdatePasswordCallback() {
                   @Override
                   public void done(AVException e) {
                       if (e == null){
                            Toast.makeText(UserModifyInformatica.this, "修改成功,请重新登录！", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(UserModifyInformatica.this, LoginActivity.class));
                            UserModifyInformatica.this.finish();
                       }else {
                           Toast.makeText(UserModifyInformatica.this, "原密码输入错误", Toast.LENGTH_SHORT).show();
                       }
                   }
               });
           }
       });

    }
}
