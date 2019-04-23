package com.example.quickqueue.toolsclass;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.GetDataCallback;
import com.example.quickqueue.R;
import com.example.quickqueue.ui.MainActivity;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class QueueListViewAdapter extends ArrayAdapter<User> {

    private int rid;

    public QueueListViewAdapter(Context context, int resourceID, List<User> users){
        super(context, resourceID, users);
        rid = resourceID;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        User user = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(rid, parent, false);
        final CircleImageView head_sculpture = (CircleImageView)view.findViewById(R.id.head_sculpture_in_queue_list);
        TextView username = (TextView)view.findViewById(R.id.merchant_queue_item_username);
        TextView seq_number = (TextView)view.findViewById(R.id.seq_number_in_queue_list);
        username.setText(user.getNickname());
        seq_number.setText(user.getSeq_number() + "");
        user.getHead_sculpture().getDataInBackground(new GetDataCallback() {
            @Override
            public void done(byte[] bytes, AVException e) {
                if (e == null){
                    if (head_sculpture != null)
                        head_sculpture.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                }else {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }
}
