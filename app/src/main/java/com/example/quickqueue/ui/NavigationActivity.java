package com.example.quickqueue.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.example.quickqueue.R;

import java.util.ArrayList;
import java.util.List;

public class NavigationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        if (isAvilible(NavigationActivity.this, "com.baidu.BaiduMap")){
            // 用百度地图导航
            navigattionByBaiduMapApp();
        }else if (isAvilible(NavigationActivity.this, "com.autonavi.minimap")){
            // 用高德地图导航
            navigationByGaoDeMap();
        }
        else{
            // 网页导航
            navigationByWeb();
        }
    }

    /**
     * 检查手机上是否安装了指定的软件
     * @param context
     * @param packageName：应用包名
     * @return
     */
    private boolean isAvilible(Context context, String packageName){
        //获取packagemanager
        final PackageManager packageManager = context.getPackageManager();
        //获取所有已安装程序的包信息
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
        //用于存储所有已安装程序的包名
        List<String> packageNames = new ArrayList<String>();
        //从pinfo中将包名字逐一取出，压入pName list中
        if(packageInfos != null){
            for(int i = 0; i < packageInfos.size(); i++){
                String packName = packageInfos.get(i).packageName;
                packageNames.add(packName);
            }
        }
        //判断packageNames中是否有目标程序的包名，有TRUE，没有FALSE
        return packageNames.contains(packageName);
    }

    // 用百度地图网页打开导航
    private void navigationByWeb(){
        Intent intent = getIntent();
        String latitudeToGo = intent.getStringExtra("LatitudeToGo");
        String longitudeToGo = intent.getStringExtra("LongitudeToGo");
        String positionNameToGo = intent.getStringExtra("PositionNameToGo");

        SharedPreferences preferences = getSharedPreferences("MyNowLocation", MODE_PRIVATE);
        String myLatitude = preferences.getString("latitude", "");
        String myLongitude = preferences.getString("longitude", "");

        WebView webView = (WebView)findViewById(R.id.web_view);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl("http://api.map.baidu.com/direction?origin=latlng:" + myLatitude + "," + myLongitude + "|name:我的位置" +
                "&destination=" + positionNameToGo + /*"latlng:" + latitudeToGo + "," + longitudeToGo + */"&mode=walking&region=成都&output=html&src=yourCompanyName|yourAppName");
    }

    // 用百度地图app导航
    private void navigattionByBaiduMapApp(){
        Intent intent = getIntent();
        String latitudeToGo = intent.getStringExtra("LatitudeToGo");
        String longitudeToGo = intent.getStringExtra("LongitudeToGo");
        String positionNameToGo = intent.getStringExtra("PositionNameToGo");

        SharedPreferences preferences = getSharedPreferences("MyNowLocation", MODE_PRIVATE);
        String myLatitude = preferences.getString("latitude", "");
        String myLongitude = preferences.getString("longitude", "");

        Toast.makeText(NavigationActivity.this, "即将使用百度地图导航", Toast.LENGTH_SHORT).show();

        Intent i1 = new Intent();

        i1.setData(Uri.parse("baidumap://map/direction?" +
                "destination=name:"+positionNameToGo+"|latlng:"+latitudeToGo + "," + longitudeToGo+
                "&sy=3&index=0&target=1"));

        startActivity(i1);
    }

    // 用高德地图app导航
    private void navigationByGaoDeMap(){
        Toast.makeText(NavigationActivity.this,"即将用高德地图打开导航",Toast.LENGTH_SHORT).show();

        Intent intent1 = getIntent();
        String latitudeToGo = intent1.getStringExtra("LatitudeToGo");
        String longitudeToGo = intent1.getStringExtra("LongitudeToGo");
        String positionNameToGo = intent1.getStringExtra("PositionNameToGo");
        Log.d("看这里！！！！", latitudeToGo + "  "+ longitudeToGo + "  " + positionNameToGo);

        SharedPreferences preferences = getSharedPreferences("MyNowLocation", MODE_PRIVATE);
        String myLatitude = preferences.getString("latitude", "");
        String myLongitude = preferences.getString("longitude", "");

        Log.d("还有这里！！！！！", myLatitude + "   " + myLongitude);

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        String url = "androidamap://route?sourceApplication=amap&slat="+myLatitude+"&slon="+myLongitude
                +"&sname=我的位置&dlat="+latitudeToGo+"&dlon="+longitudeToGo+"&dname="+positionNameToGo+"&dev=0&t=1";
        Uri uri = Uri.parse(url);
        //将功能Scheme以URI的方式传入data
        intent.setData(uri);
        //启动该页面即可
        startActivity(intent);
    }
}