package com.example.quickqueue.ui;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetDataCallback;
import com.example.quickqueue.R;
import com.example.quickqueue.toolsclass.QueueListViewAdapter;
import com.example.quickqueue.toolsclass.User;
import com.example.quickqueue.util.Utility;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MerchantActivity extends AppCompatActivity {

    private SharedPreferences preferences;
    private SharedPreferences.Editor prefEditor;
    private DrawerLayout drawerLayout_merchant;

    private String account;
    private TextView merchantName;

    private ListView merchant_queue_list_view;

    CircleImageView headSculptureMerchant;
    byte[] headSculptureImageBytes = null;

    private QueueListViewAdapter adapter;

    Handler handler;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchant);
        preferences= PreferenceManager.getDefaultSharedPreferences(this);

        drawerLayout_merchant = (DrawerLayout)findViewById(R.id.drawer_layout_merchant);
        NavigationView navigationView_merchant = (NavigationView)findViewById(R.id.navigation_view_merchant);
        navigationView_merchant.setItemIconTintList(null); // 使图标显示原来的颜色

        merchant_queue_list_view = (ListView)findViewById(R.id.merchant_queue_list_view);

        View viewHeader = navigationView_merchant.inflateHeaderView(R.layout.navagation_header);
        merchantName = (TextView)viewHeader.findViewById(R.id.nick_name);
        headSculptureMerchant = (CircleImageView)viewHeader.findViewById(R.id.head_sculpture_image);
        initView();

        // 点击头像换头像
        headSculptureMerchant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 42);
            }
        });

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.merchant);
        }

        ImageButton create_queue = (ImageButton)findViewById(R.id.merchant_create_queue);
        create_queue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MerchantActivity.this, MerchantInitQueue.class));
            }
        });

        Button close_queue = (Button)findViewById(R.id.close_queue);
        // 关闭队列按钮监听事件
        close_queue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog alert=new AlertDialog.Builder(MerchantActivity.this).create();
                alert.setMessage("确定关闭队列吗？");
                //添加取消按钮
                alert.setButton(DialogInterface.BUTTON_NEGATIVE,"不",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                });
                //添加"确定"按钮
                alert.setButton(DialogInterface.BUTTON_POSITIVE,"是的", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        merchantCloseQueue();
                    }
                });
                alert.show();
            }
        });

        // 为NavigationView 的 menu 设置点击事件
        navigationView_merchant.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.settings_merchant:
                        startActivity(new Intent(MerchantActivity.this, MerchantModifyInformatica.class));
                        break;
                    case R.id.exit_merchant:
                        merchantLogOut();
                        break;
                    case R.id.about_merchant:
                        startActivity(new Intent(MerchantActivity.this, AboutActivity.class));
                        break;
                    case R.id.update_merchant:
                        startActivity(new Intent(MerchantActivity.this, CheckUpdateActivity.class));
                        break;
                    case R.id.feedback_merchant:
                        Intent intent = new Intent(MerchantActivity.this, FeedbackActivity.class);
                        intent.putExtra("flag", "merchant");
                        startActivity(intent);
                        break;
                    default:
                }
                return true;
            }
        });

        LinearLayout hasQueue = (LinearLayout) findViewById(R.id.merchant_layout_has_queue);
        RelativeLayout notHasQueue = (RelativeLayout)findViewById(R.id.merchant_layout_not_has_queue);

        if(Utility.merchantHasAQueue){
            hasQueue.setVisibility(View.VISIBLE);
        }else {
            notHasQueue.setVisibility(View.VISIBLE);
        }

        final TextView num_of_queue = (TextView)findViewById(R.id.num_of_queue);

        merchant_queue_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int position, long l) {
                User user = Utility.users.get(position);

                Intent intent = new Intent(MerchantActivity.this, UserInfoInQueue.class);
                intent.putExtra("account", user.getAccount());
                intent.putExtra("nickname", user.getNickname());
                startActivity(intent);
            }
        });



        new Thread(new Runnable() {
            @Override
            public void run() {
                // 商家每隔一段时间请求一次
                while(true) {
                    try {
                        Thread.sleep(5 * 1000); //设置暂停的时间 5 秒

                        requestForUsers();

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        handler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 1) {
                    // 必须在主线程更新UI，而adapter数据更新会更新UI
                    adapter.notifyDataSetChanged();
                    num_of_queue.setText(Utility.users.size() + " / " + Utility.max_num_in_queue);
                }else{

                }
            }
        };
        adapter = new QueueListViewAdapter(MerchantActivity.this, R.layout.merchant_queue_item, Utility.users);
        merchant_queue_list_view.setAdapter(adapter);
    }

    public void initView(){
        AVQuery<AVObject> query = new AVQuery<>("Merchant");
        query.whereEqualTo("account", preferences.getString("merchant_account_login", ""));
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null){
                    merchantName.setText(list.get(0).get("merchant_name").toString());

                    AVFile file = list.get(0).getAVFile("headsculpture");
                    file.getDataInBackground(new GetDataCallback() {
                        @Override
                        public void done(byte[] bytes, AVException e) {
                            if (e == null){
                                if (headSculptureMerchant != null)
                                    headSculptureMerchant.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                            }else {
                                Toast.makeText(MerchantActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }else {

                }
            }
        });
    }

    //下面这个函数的作用是: 在当前活动界面按下返回键直接返回到桌面
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent home = new Intent(Intent.ACTION_MAIN);
            home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            home.addCategory(Intent.CATEGORY_HOME);
            startActivity(home);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.merchant_zxing, menu);
        return true;
    }

    //菜单menu响应事件处理
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                drawerLayout_merchant.openDrawer(GravityCompat.START);
                break;

            case R.id.zxing:
                startActivity(new Intent(MerchantActivity.this, ShowZxing.class));
                break;

            default:
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 42 && resultCode == RESULT_OK) {
            try {
                headSculptureMerchant.setImageBitmap(MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData()));
                headSculptureImageBytes = getBytes(getContentResolver().openInputStream(data.getData()));
                final AVFile file1 = new AVFile("headsculpture.jpg", headSculptureImageBytes);
                AVQuery<AVObject> query = new AVQuery<>("Merchant");
                query.whereEqualTo("account", preferences.getString("merchant_account_login", ""));
                query.findInBackground(new FindCallback<AVObject>() {
                    @Override
                    public void done(List<AVObject> list, AVException e) {
                        list.get(0).put("headsculpture", file1);
                        list.get(0).saveInBackground();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, len);
        }
        return byteArrayOutputStream.toByteArray();
    }

    // 商家关闭队列
    public void merchantCloseQueue(){
        final AVQuery<AVObject> query = new AVQuery<>("Queue");
        query.whereEqualTo("account_merchant", Utility.current_merchant_account);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null){
                    if (list.size() == 0){

                        AVQuery<AVObject> query1 = new AVQuery<>("Merchant");
                        query1.whereEqualTo("account", Utility.current_merchant_account);
                        query1.findInBackground(new FindCallback<AVObject>() {
                            @Override
                            public void done(List<AVObject> list, AVException e) {
                                if (e == null){
                                    AVObject merchant = list.get(0);
                                    merchant.put("has_a_queue", 0);
                                    merchant.put("max_num", 0);
                                    merchant.put("service_time_once", 0);
                                    merchant.saveInBackground();
                                    Utility.merchantHasAQueue = false;
                                    startActivity(new Intent(MerchantActivity.this, MerchantActivity.class));
                                }else {
                                    Toast.makeText(MerchantActivity.this, "关闭队列错误0：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }else {
                        Toast.makeText(MerchantActivity.this, "当前还有人在排队中，无法关闭队列！", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }else {
                    Toast.makeText(MerchantActivity.this, "关闭队列错:1：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // 商家退出登录
    public void merchantLogOut(){
        AlertDialog alert=new AlertDialog.Builder(MerchantActivity.this).create();
        alert.setMessage("确定要退出登录吗？");
        //添加取消按钮
        alert.setButton(DialogInterface.BUTTON_NEGATIVE,"不",new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
            }
        });
        //添加"确定"按钮
        alert.setButton(DialogInterface.BUTTON_POSITIVE,"是的", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {

                AVQuery<AVObject> query = new AVQuery<>("Queue");
                query.whereEqualTo("account_merchant", Utility.current_merchant_account);
                query.findInBackground(new FindCallback<AVObject>() {
                    @Override
                    public void done(List<AVObject> list, AVException e) {
                        if (e == null){
                            if (list.size() > 0){
                                Toast.makeText(MerchantActivity.this, "当前还有人在排队中，无法退出登录！", Toast.LENGTH_SHORT).show();
                            }else {
                                prefEditor = preferences.edit();
                                prefEditor.putBoolean("merchant_has_login", false);
                                prefEditor.apply();

                                Utility.current_merchant_account = "";

                                startActivity(new Intent(MerchantActivity.this, WelcomeActivity.class));
                                MerchantActivity.this.finish();
                            }
                        }else {
                            Toast.makeText(MerchantActivity.this, "无法退出：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        alert.show();
    }

    public void requestForUsers(){
        final AVQuery<AVObject> query = new AVQuery<>("Queue");
        query.whereEqualTo("account_merchant", Utility.current_merchant_account);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null){

                    Log.d("大小",list.size() + "  " + Utility.users.size());

                    if (list.size() == Utility.users.size()){
                        Message message = handler.obtainMessage();
                        message.what = 1;  // 表示未更新
                        handler.sendMessage(message);
                    }else {
                        Utility.users.clear();

                        for (int i = 0; i < list.size(); i++){

                            final String account_user = list.get(i).get("account_user").toString();
                            final int seq_number = Integer.parseInt(list.get(i).get("Seq_number").toString());
                            //   final int waiting_time = Integer.parseInt(list.get(i).get("waiting_time").toString());

                            AVQuery<AVObject> query1 = new AVQuery<>("_User");
                            query1.whereEqualTo("username", account_user);
                            query1.findInBackground(new FindCallback<AVObject>() {
                                @Override
                                public void done(List<AVObject> list, AVException e) {
                                    if (e == null){
                                        User user = new User();
                                        user.setAccount(account_user);
                                        user.setSeq_number(seq_number);
                                        //  user.setWaiting_time(waiting_time);
                                        user.setNickname(list.get(0).get("nickname").toString());
                                        user.setHead_sculpture(list.get(0).getAVFile("headsculpture"));
                                        Utility.users.add(user);

                                        Message message = handler.obtainMessage();
                                        message.what = 1;  // 表示更新
                                        handler.sendMessage(message);
                                    }else {

                                    }
                                }
                            });
                        }
                    }

                }else {
                    Toast.makeText(MerchantActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
