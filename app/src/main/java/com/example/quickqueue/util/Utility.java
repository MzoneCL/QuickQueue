package com.example.quickqueue.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.avos.avoscloud.AVCloudQueryResult;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.CloudQueryCallback;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;
import com.example.quickqueue.R;
import com.example.quickqueue.toolsclass.Footmark;
import com.example.quickqueue.toolsclass.User;
import com.example.quickqueue.ui.MainActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * 全局的一个工具类
 */

public class Utility {

    public static int alreadyWaitingTime = 0; // 用户已等待时间，按秒计

    public static String time_in_queue = "00:00"; // 加入排队时间戳

    public static String seq_number = ""; // 排队号

    public static String waiting_time = "0"; // 总共需排队等待时间

    public static boolean userIsInQueue = false; // 用户的排队状态，false表示未在排队

    public static boolean merchantHasAQueue = false; // 商家是否已创建队列，false表示没有队列

    public static String current_user_account = ""; // 当前登录用户的账号

    public static String current_merchant_account = ""; // 当前登录商家的账号

    public static int max_num_in_queue = 0; // 商家设置的最大排队人数

    public static  boolean user_has_footmark = false; // 用户是否有排队记录


    public static List<User> users = new ArrayList<>(); // 当前队列中的用户

    public static List<Footmark> footmarks = new ArrayList<>();


    // 判断当前网络状况
    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    // 检查字符串是否是纯数字
    public static boolean isNumeric(String str){
        for (int i = str.length(); --i >= 0 ; ){
            if (!Character.isDigit(str.charAt(i))){
                return false;
            }
        }
        return true;
    }

    // 判断字符串是否是电话号码
    public static boolean isPhoneNumber(String str){
        if (isNumeric(str) && str.length() == 11)
            return true;
        return false;
    }

    // 判断密码是否合法
    public static boolean isPasswordOK(String pswd){
        if (pswd.length() < 8 || pswd.length() > 16)
            return false;
        else if (isSpecialChar(pswd)) // 包含特殊字符
            return false;
        else if (pswd.contains(" "))
            return false;
        else
          return true;
    }

    // 判断一个字符串是否包含特殊字符
    public static boolean isSpecialChar(String str) {
        String regEx = "[ _`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]|\n|\r|\t";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.find();
    }

    // 用户加入队列
    public static void userInQueue(final String ScanResult, final Context context){
        AVQuery<AVObject> queryMerchant = new AVQuery<>("Merchant");
        queryMerchant.whereEqualTo("account", ScanResult);
        queryMerchant.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null){
                    MainActivity.merchant = list.get(0);

                    final int num_in_queue = Integer.parseInt(MainActivity.merchant.get("max_num").toString());

                    final String merchant_name = list.get(0).get("merchant_name").toString();

                    if (Integer.parseInt(MainActivity.merchant.get("has_a_queue").toString()) == 0){
                        Toast.makeText(MyApplication.getContext(), "当前商家没有打开队列", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // ScanResult 为获取到的字符串，即商家的账号
                    final String account_merchant = ScanResult;

                    AVQuery<AVObject> query = new AVQuery<>("Queue");
                    query.whereEqualTo("account_merchant", ScanResult);
                    query.findInBackground(new FindCallback<AVObject>() {
                        @Override
                        public void done(List<AVObject> list, AVException e) {
                            if (e == null){

                                if (num_in_queue == list.size()){
                                    Toast.makeText(context, "当前队列已满，无法加入队列", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                int max = 0; // 找出指定商家队列中最大序号

                                for (int i = 0; i < list.size(); i++){
                                    if (Integer.parseInt(list.get(i).get("Seq_number").toString()) > max){
                                        max = Integer.parseInt(list.get(i).get("Seq_number").toString());
                                    }
                                }

                                Log.d("队列最大", max + "");

                                AVObject queue = new AVObject("Queue");
                                queue.put("account_merchant", account_merchant);

                                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext());

                                queue.put("account_user", preferences.getString("account_user", ""));
                                queue.put("Seq_number", max+1);
                                queue.put("waiting_time", (list.size() + 1) * Integer.parseInt(MainActivity.merchant.get("service_time_once").toString()));
                                queue.put("merchant_name",merchant_name);

                                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
                                String time = df.format(new Date());// 当前时间

                                queue.put("time_in_queue", time);

                                queue.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(AVException e) {
                                        if (e == null){
                                            // 进队成功
                                            AVUser.getCurrentUser().put("status", "inQueue");
                                            AVUser.getCurrentUser().saveInBackground(new SaveCallback() {
                                                @Override
                                                public void done(AVException e) {
                                                    if (e == null){
                                                        Utility.userIsInQueue = true;
                                                        context.startActivity(new Intent(context, MainActivity.class));
                                                    }else {
                                                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                        Log.d("进队失败0", e.getMessage());
                                                    }
                                                }
                                            });

                                        }else {
                                            Log.d("进队失败1", e.getMessage());
                                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                            }else {
                                // 查询失败
                            }
                        }
                    });

                }else {

                }
            }
        });
    }

    // 用户退出排队
    public static void userQuitQueue(final Context context){

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        AVQuery<AVObject> query = new AVQuery<>("Queue");
        query.whereEqualTo("account_user", preferences.getString("account_user",""));
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null){
                    String objectID = list.get(0).getObjectId();

                    // 上传排队记录
                    AVObject queue = list.get(0);
                    String account_user = queue.get("account_user").toString();
                    String account_merchant = queue.get("account_merchant").toString();
                    String merchant_name = queue.get("merchant_name").toString();
                    String waited_time = queue.get("waiting_time").toString();
                    String waited_number = queue.get("Seq_number").toString();

                    AVObject footmark = new AVObject("Footmark");
                    footmark.put("account_user", account_user);
                    footmark.put("account_merchant", account_merchant);
                    footmark.put("merchant_name", merchant_name);
                    footmark.put("waited_time", waited_time);
                    footmark.put("waited_number", waited_number);

                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
                    String time = df.format(new Date());// 当前时间

                    footmark.put("date", time);

                    footmark.saveInBackground();

                    AVQuery.doCloudQueryInBackground("delete from Queue where objectId=" + "'" + objectID + "'", new CloudQueryCallback<AVCloudQueryResult>() {
                        @Override
                        public void done(AVCloudQueryResult avCloudQueryResult, AVException e) {
                            if (e == null){ // 退出成功

                                Utility.seq_number = "";
                                Utility.time_in_queue = "";
                                Utility.waiting_time = "";

                                AVUser.getCurrentUser().put("status", "notInQueue");
                                AVUser.getCurrentUser().saveInBackground();

                                Utility.userIsInQueue = false;

                                context.startActivity(new Intent(context, MainActivity.class));
                            }else {
                                Toast.makeText(context,"退出失败", Toast.LENGTH_SHORT).show();
                                Log.d("退出失败", e.getMessage());
                            }
                        }
                    });

                }else {
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // 发送通知
    public static void notifyUser(Context context){
        NotificationManager manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new NotificationCompat.Builder(context)
                .setContentTitle("快排")
                .setContentText("亲爱的用户，您的排队结束啦！")
                .setDefaults(Notification.DEFAULT_SOUND) // 设置提示音为默认提示音
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.launcher)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.launcher))
                .build();
        if (manager != null) {
            manager.notify(1, notification);
        }
    }

    // 秒数转分钟（xx min xx ses）
    public static String secToMin(int sec){
        int min = sec / 60;
        sec = sec - 60 * min;
        return min + "分" + sec  + "秒";
    }

    // 获取指定账号用户的排队记录
    public static void getUserFootmark(String account_user){
        AVQuery<AVObject> query = new AVQuery<>("Footmark");
        query.whereEqualTo("account_user", account_user);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null){

                    footmarks.clear();

                    if (list.size() > 0){
                        Utility.user_has_footmark = true;
                    }

                    for (int i = 0; i < list.size(); i++){

                        //(String account_user, String account_merchant, String date, String waited_number, String waited_time, String merchant_name)

                        AVObject object = list.get(i);
                        Footmark footmark = new Footmark(object.get("account_user").toString(),
                                object.get("account_merchant").toString(), object.get("date").toString(),
                                object.get("waited_number").toString(), object.get("waited_time").toString(),
                                object.get("merchant_name").toString());

                        footmarks.add(footmark);
                    }
                }else {
                    Log.d("获取足迹错误", e.getMessage());
                }
            }
        });
    }

    // 隔段时间更新Queue表时间
    public static void updateTimeInQueue(String account_user, final long time){
        AVQuery<AVObject> query = new AVQuery<>("Queue");
        query.whereEqualTo("account_user", account_user);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null){

                    // 第一参数是 className,第二个参数是 objectId
                    AVObject queue = AVObject.createWithoutData("Queue", list.get(0).getObjectId());

                    // 修改 content
                    queue.put("waiting_time", time);
                    // 保存到云端
                    queue.saveInBackground();
                }else {

                }
            }
        });
    }
}
