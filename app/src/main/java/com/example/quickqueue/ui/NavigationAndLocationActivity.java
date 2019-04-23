package com.example.quickqueue.ui;

import android.Manifest;
import android.bluetooth.BluetoothClass;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;

import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.poi.PoiSortType;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.example.quickqueue.R;
import com.example.quickqueue.toolsclass.LocNearListViewAddressAdapter;

import java.util.ArrayList;
import java.util.List;

public class NavigationAndLocationActivity extends AppCompatActivity implements OnGetPoiSearchResultListener {

    private MapView mapView;
    private BaiduMap mBaiduMap;
    private PoiSearch poiSearch;

    LatLng myLocation;

    public LocationClient locationClient;

    private ListView listView;

    List<PoiInfo> nearLocationList = new ArrayList<>();

    LocNearListViewAddressAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locationClient = new LocationClient(getApplicationContext());
        locationClient.registerLocationListener(new MyLocationListener());

        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_navigation_and_location);

        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(NavigationAndLocationActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(NavigationAndLocationActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(NavigationAndLocationActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        } else {
            requestLocation();
        }

        listView = (ListView)findViewById(R.id.list_view);

        mapView = (MapView) findViewById(R.id.mapView);
        mBaiduMap = mapView.getMap();
        mBaiduMap.setMaxAndMinZoomLevel(18,19);  // 设置最小和最大缩放级别

        locationClient.start();
        initLocation();

        // listView 注册点击事件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int position, long l) {
                    AlertDialog alert=new AlertDialog.Builder(NavigationAndLocationActivity.this).create();
                    alert.setMessage("开启导航？");
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
                            PoiInfo poiInfo = nearLocationList.get(position);
                            Intent intent = new Intent(NavigationAndLocationActivity.this, NavigationActivity.class);
                            intent.putExtra("LatitudeToGo", "" + poiInfo.location.latitude);
                            intent.putExtra("LongitudeToGo", "" + poiInfo.location.longitude);
                            intent.putExtra("PositionNameToGo", poiInfo.name);
                            Log.d("这里", poiInfo.name);
                            startActivity(intent);
                        }
                    });
                    alert.show();
            }
        });
    }

    private void requestLocation(){
        locationClient.start();
        initLocation();
    }

    private void initLocation(){

        LocationClientOption option = new LocationClientOption();

        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备

        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系

        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要

        option.setOpenGps(true);//可选，默认false,设置是否使用gps

        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果

        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”

        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到

        option.setIgnoreKillProcess(false);//可选，默认false，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认杀死

        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集

        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要

        option.setScanSpan(10000);  //每10秒钟更新一下位置

        locationClient.setLocOption(option);

    }


    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(final BDLocation location) {
            if (location == null || mapView == null) {
                return;
            }

            SharedPreferences.Editor editor = getSharedPreferences("MyNowLocation", MODE_PRIVATE).edit();
            editor.putString("latitude", "" + location.getLatitude());
            editor.putString("longitude", "" + location.getLongitude());
            editor.putString("address", location.getAddrStr());
            editor.apply();

            myLocation = new LatLng(location.getLatitude(), location.getLongitude());

            LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());


            // 构造定位数据
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);

            //画标志
            CoordinateConverter converter = new CoordinateConverter();
            converter.coord(ll);
            converter.from(CoordinateConverter.CoordType.COMMON);
            LatLng convertLatLng = converter.convert();

            OverlayOptions ooA = new MarkerOptions().position(ll).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker));
            Marker mCurrentMarker = (Marker) mBaiduMap.addOverlay(ooA);


            MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(convertLatLng, 17.0f);
            mBaiduMap.animateMapStatus(u);

            //画当前定位标志
            MapStatusUpdate uc = MapStatusUpdateFactory.newLatLng(ll);
            mBaiduMap.animateMapStatus(uc);
            mapView.showZoomControls(false);
            //poi 搜索周边
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Looper.prepare();
                    searchNearBy(location);
                    Looper.loop();
                }
            }).start();
        }
    }

    private void searchNearBy(BDLocation location){
        // POI初始化搜索模块，注册搜索事件监听
        searchByKeywords("KTV", location);
        searchByKeywords("美容美发", location);
        searchByKeywords("电影院", location);
        searchByKeywords("餐馆",location);
        searchByKeywords("公园",location);
    }

    public void searchByKeywords(String s, BDLocation location){
        poiSearch = PoiSearch.newInstance();
        poiSearch.setOnGetPoiSearchResultListener(this);
        PoiNearbySearchOption poiNearbySearchOption = new PoiNearbySearchOption();

        poiNearbySearchOption.keyword(s);

        poiNearbySearchOption.location(new LatLng(location.getLatitude(), location.getLongitude()));

        poiNearbySearchOption.radius(2000);  // 检索半径，单位是米

        poiNearbySearchOption.pageCapacity(1000);  // 默认每页1000条

        poiSearch.searchNearby(poiNearbySearchOption);  // 发起附近检索请求

    }

    @Override
    public void onGetPoiResult(PoiResult result) {
        // 获取POI检索结果
        if (result == null || result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {// 没有找到检索结果
           // Toast.makeText(NavigationAndLocationActivity.this, "请打开GPS或检查位置权限是否设为允许",Toast.LENGTH_LONG).show();
            return;
        }

        if (result.error == SearchResult.ERRORNO.NO_ERROR) {// 检索结果正常返回
            if(result != null){
                if(result.getAllPoi()!= null && result.getAllPoi().size()>0){
                    nearLocationList.addAll(result.getAllPoi());
                    adapter = new LocNearListViewAddressAdapter(NavigationAndLocationActivity.this, R.layout.lvlocnear_item, nearLocationList, myLocation);
                    listView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }

    @Override
    public void onGetPoiDetailResult(PoiDetailResult result){}
    @Override
    public void onGetPoiIndoorResult(PoiIndoorResult result){}
    @Override
    public void onDestroy(){
        super.onDestroy();
        locationClient.stop();
        mapView.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        switch (requestCode){
            case 1:
                if (grantResults.length > 0){
                    for (int result : grantResults){
                        if (result != PackageManager.PERMISSION_GRANTED){
                            Toast.makeText(this, "必须同意所有权限才能使用本应用", Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    }
                    requestLocation();
                } else {
                    Toast.makeText(this, "发生未知错误", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
        }
    }
}
