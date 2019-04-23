package com.example.quickqueue.toolsclass;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.GetDataCallback;
import com.example.quickqueue.R;
import com.example.quickqueue.ui.MerchantInfoInFootmark;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FootmarkListViewAdapter extends ArrayAdapter<Footmark> {

    private int rid;

    public FootmarkListViewAdapter(Context context, int resourceID, List<Footmark> users){
        super(context, resourceID, users);
        rid = resourceID;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        final Footmark footmark = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(rid, parent, false);
        TextView tv_mechant_name = (TextView)view.findViewById(R.id.merchant_name_in_footmark);
        TextView tv_waited_time = (TextView)view.findViewById(R.id.waited_time_in_footmark);
        TextView tv_number = (TextView)view.findViewById(R.id.waited_number_in_footmark);
        TextView tv_date = (TextView)view.findViewById(R.id.date_in_footmark);

        tv_mechant_name.setText(footmark.getMerchant_name());
        tv_date.setText(footmark.getDate());
        tv_number.setText(footmark.getWaited_number());
        tv_waited_time.setText(footmark.getWaited_time() + " min");

        tv_mechant_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), MerchantInfoInFootmark.class);
                intent.putExtra("account_merchant", footmark.getAccount_merchant());
                getContext().startActivity(intent);
            }
        });

        return view;
    }

}
