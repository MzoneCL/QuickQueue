package com.example.quickqueue.ui;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.GetDataCallback;
import com.avos.avoscloud.SaveCallback;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.example.quickqueue.R;
import com.example.quickqueue.fragments.HistoryFragment;
import com.example.quickqueue.fragments.HomeFragment;
import com.example.quickqueue.fragments.QueueFragment;
import com.example.quickqueue.util.Utility;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener{

    SharedPreferences preferences;
    SharedPreferences.Editor prefEditor;

    private RadioGroup mRadioGroup;
    private List<Fragment> fragments = new ArrayList<>();
    private Fragment fragment;
    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;
    private RadioButton RB_home, RB_queue;
    private DrawerLayout mDrawerLayout;

    private TextView nickname;

    private double latitude;
    private double longitude;
    private String address;

    public static AVObject merchant = null; // 用户扫码进入的商家

    CircleImageView headSculpture;
    private byte[] headSculptureImageBytes = null; // 存取头像图片的字节数组

    public LocationClient locationClient;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        locationClient = new LocationClient(getApplicationContext());
        locationClient.registerLocationListener(new MyLocationListener());

        requestLocation();

        setContentView(R.layout.activity_main);

        Utility.getUserFootmark(preferences.getString("account_user", ""));

        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        final NavigationView navigationView = (NavigationView)findViewById(R.id.navigation_view);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.user);
        }

        View viewheader = navigationView.inflateHeaderView(R.layout.navagation_header); // navigation_header的View

        nickname = (TextView)viewheader.findViewById(R.id.nick_name);
        nickname.setText(AVUser.getCurrentUser().get("nickname").toString());
        headSculpture = (CircleImageView)viewheader.findViewById(R.id.head_sculpture_image);

        AVFile file = AVUser.getCurrentUser().getAVFile("headsculpture");
        file.getDataInBackground(new GetDataCallback() {
            @Override
            public void done(byte[] bytes, AVException e) {
                if (e == null){
                    if (headSculpture != null)
                        headSculpture.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                }else {
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 点击头像换头像
        headSculpture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 42);
            }
        });

        navigationView.setItemIconTintList(null);  // 使图标显示原来的颜色
        //为navigation注册点击事件
        //navigationView.setCheckedItem(R.id.my_message);  //默认选中“我的消息”
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.my_message:
                        break;
                    case R.id.settings: // 修改个人信息
                        startActivity(new Intent(MainActivity.this, UserModifyInformatica.class));
                        break;
                    case R.id.about:
                        startActivity(new Intent(MainActivity.this, AboutActivity.class));
                        break;
                    case R.id.update:
                        startActivity(new Intent(MainActivity.this, CheckUpdateActivity.class));
                        break;
                    case R.id.feedback:
                        Intent intent = new Intent(MainActivity.this, FeedbackActivity.class);
                        intent.putExtra("flag", "user");
                        startActivity(intent);
                        break;
                    default:
                }
                return true;
            }
        });

        initView();//初始化组件
        mRadioGroup.setOnCheckedChangeListener(this); //点击事件
        fragments = getFragments();  //添加布局
        normalFragment(); //设置默认布局(首页)
    }

    private void requestLocation(){
        initLocation();
        locationClient.start();
    }

    //初始化组件的函数
    private void initView(){
        mRadioGroup = (RadioGroup)findViewById(R.id.main_activity_radiogroup);
        RB_home = (RadioButton)findViewById(R.id.radioButton_home);
        RB_queue = (RadioButton)findViewById(R.id.radioButton_queue);
    }

    // 初始化定位
    private void initLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setIsNeedAddress(true);
        locationClient.setLocOption(option);
    }

    //默认布局
    private void normalFragment() {
        fragmentManager=getSupportFragmentManager();
        RB_queue.setChecked(true); //坑啊  把这行代码放 fragmentManager=getSupportFragmentManager(); 后面就没问题了  真坑
        transaction=fragmentManager.beginTransaction();
        fragment=fragments.get(1);
        transaction.replace(R.id.main_activity_fragment,fragment);
        transaction.commit();
    }

    //设置groupbutton点击事件
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        fragmentManager=getSupportFragmentManager();
        transaction=fragmentManager.beginTransaction();
        switch (checkedId){
            case R.id.radioButton_home:
                fragment=fragments.get(0);
                transaction.replace(R.id.main_activity_fragment, fragment);
                break;
            case R.id.radioButton_queue:
                fragment=fragments.get(1);
                transaction.replace(R.id.main_activity_fragment,fragment);
                break;
            case R.id.radioButton_history:
                fragment=fragments.get(2);
                transaction.replace(R.id.main_activity_fragment,fragment);
                break;
        }
        transaction.commit();
    }


    //向fragments列表中添加fragment
    public List<Fragment> getFragments() {
        fragments.add(new HomeFragment());
        fragments.add(new QueueFragment());
        fragments.add(new HistoryFragment());
        return fragments;
    }

    //下面这个函数的作用是; 在当前活动界面按下返回键直接返回到桌面
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


    //创建菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    //菜单menu响应事件处理
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.exit_item:
                 {
                     AlertDialog alert=new AlertDialog.Builder(MainActivity.this).create();
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

                             if (Utility.userIsInQueue){
                                 Toast.makeText(MainActivity.this, "退出前请先取消排队！", Toast.LENGTH_SHORT).show();
                                 return;
                             }

                             Utility.current_user_account = "";

                             AVUser.getCurrentUser().logOut();
                             prefEditor = preferences.edit();
                             prefEditor.putBoolean("user_has_login", false);
                             prefEditor.apply();

                             startActivity(new Intent(MainActivity.this, WelcomeActivity.class));
                             MainActivity.this.finish();
                         }
                     });
                     alert.show();
                 }
                break;
            case R.id.cancel_item: // 注销账号
                {
                    AlertDialog alert=new AlertDialog.Builder(MainActivity.this).create();
                    alert.setMessage("确定要注销账号吗？");
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

                        }
                    });
                    alert.show();
                }
                break;
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            default:
        }
        return true;
    }

    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location){
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            address = location.getAddrStr() + "";
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        locationClient.stop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 42 && resultCode == RESULT_OK) {
            try {
                headSculpture.setImageBitmap(MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData()));
                headSculptureImageBytes = getBytes(getContentResolver().openInputStream(data.getData()));
                AVFile file1 = new AVFile("headsculpture.jpg", headSculptureImageBytes);
                AVUser.getCurrentUser().put("headsculpture", file1);
                AVUser.getCurrentUser().saveInBackground(new SaveCallback() {
                    @Override
                    public void done(AVException e) {
                        if (e == null){
                            // 更换头像成功
                        }else{
                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if(intentResult != null) {
            if(intentResult.getContents() == null) {
                Toast.makeText(this,"扫描失败",Toast.LENGTH_LONG).show();
            } else {
                final String ScanResult = intentResult.getContents();

                Log.d("扫码结果：" , ScanResult);

                Utility.userInQueue(ScanResult, MainActivity.this);
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
}