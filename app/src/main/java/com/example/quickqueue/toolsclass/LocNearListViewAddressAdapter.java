package com.example.quickqueue.toolsclass;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.example.quickqueue.R;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by Congli Ma on 2018/5/17.
 */

public class LocNearListViewAddressAdapter extends ArrayAdapter<PoiInfo> {

    private int resourceId;

    private LatLng myLocation;

    public LocNearListViewAddressAdapter(Context context, int textViewResourceId, List<PoiInfo> objects, LatLng myLocation){
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
        this.myLocation = myLocation;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        PoiInfo location = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
        TextView name = (TextView)view.findViewById(R.id.position_name);
        TextView address = (TextView)view.findViewById(R.id.position_address);
        TextView distance = (TextView)view.findViewById(R.id.position_distance);
        name.setText(location.name);
        address.setText(location.address);
        distance.setText(getDistance(myLocation, location.location) + "m");

        return view;
    }

    /**
     * 计算两点之间距离
     * @param start
     * @param end
     * @return 米
     */
    public String getDistance(LatLng start, LatLng end){
        double lat1 = (Math.PI/180)*start.latitude;
        double lat2 = (Math.PI/180)*end.latitude;

        double lon1 = (Math.PI/180)*start.longitude;
        double lon2 = (Math.PI/180)*end.longitude;

//      double Lat1r = (Math.PI/180)*(gp1.getLatitudeE6()/1E6);
//      double Lat2r = (Math.PI/180)*(gp2.getLatitudeE6()/1E6);
//      double Lon1r = (Math.PI/180)*(gp1.getLongitudeE6()/1E6);
//      double Lon2r = (Math.PI/180)*(gp2.getLongitudeE6()/1E6);

        //地球半径 km
        double R = 6371;

        //两点间距离 km，如果想要米的话，结果*1000就可以了
        double d =  Math.acos(Math.sin(lat1)*Math.sin(lat2)+Math.cos(lat1)*Math.cos(lat2)*Math.cos(lon2-lon1))*R;

        DecimalFormat df = new DecimalFormat("#"); // 保留整数
        return df.format(d*1000);  // 米
    }
}
