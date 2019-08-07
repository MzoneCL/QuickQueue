package com.example.quickqueue.util;

import android.app.Application;
import android.content.Context;

import com.avos.avoscloud.AVOSCloud;

import org.litepal.LitePal;

/**
 * Created by Congli Ma on 2018/5/17.
 */

public class MyApplication extends Application {

    private static Context context;

    @Override
    public void onCreate(){
        super.onCreate();
        AVOSCloud.initialize(this,"3rFUuCtvWb7GdeWqqkMSBNId-gzGzoz","feSBeXd7tuVLyPVlcPea6s");
        AVOSCloud.setDebugLogEnabled(true);
        context = getApplicationContext();
        LitePal.initialize(context);
    }

    public static Context getContext(){
        return context;
    }
}
