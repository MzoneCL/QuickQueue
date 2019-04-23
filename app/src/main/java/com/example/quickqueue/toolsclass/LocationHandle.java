package com.example.quickqueue.toolsclass;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.example.quickqueue.util.MyApplication;

/**
 * Created by Congli Ma on 2018/5/31.
 */

public class LocationHandle {

    public LocationClient locationClient;

    public void initLocationClient(){
        locationClient = new LocationClient(MyApplication.getContext());
        locationClient.registerLocationListener(new MainLocationListener());
    }

    public class MainLocationListener implements BDLocationListener{

        @Override
        public void onReceiveLocation(BDLocation location){

        }
    }

}
