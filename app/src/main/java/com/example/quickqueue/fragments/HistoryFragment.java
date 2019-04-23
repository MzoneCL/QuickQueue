package com.example.quickqueue.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.quickqueue.R;
import com.example.quickqueue.toolsclass.FootmarkListViewAdapter;
import com.example.quickqueue.util.Utility;


/**
 * Created by Congli Ma on 2018/4/26.
 */

public class HistoryFragment extends Fragment {

    View view;

    ListView list_view_footmark;

    TextView no_footmark;

    FootmarkListViewAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.history_fragment, container, false);
        list_view_footmark = (ListView)view.findViewById(R.id.history_fragment_list_view);
        no_footmark = (TextView)view.findViewById(R.id.history_fragment_text_view);

        initView();

        return view;
    }

    void initView(){
        if (Utility.user_has_footmark){
            adapter = new FootmarkListViewAdapter(getActivity(), R.layout.footmark_listview_item, Utility.footmarks);
            list_view_footmark.setAdapter(adapter);
            list_view_footmark.setVisibility(View.VISIBLE);
        }else{
            no_footmark.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
    }
}

