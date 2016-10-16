package com.example.renaigu.dctv.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.renaigu.dctv.R;
import com.example.renaigu.dctv.VideoViewActivity;
import com.example.renaigu.dctv.utils.MediaItem;


//import static android.R.attr.autoStart;


public class OneFragment extends Fragment implements View.OnClickListener {

    public OneFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_one, container, false);
        View v = inflater.inflate(R.layout.fragment_one, container, false);
        LinearLayout view = (LinearLayout) v.findViewById(R.id.dctvRow);

        view.setOnClickListener(this);
        return v;
    }

    @Override
    public void onClick(View v) {
        MediaItem media = new MediaItem();
        media.setUrl("http://ingest.diamondclub.tv/hls2/dctv.m3u8");
        media.setTitle("Diamond Club TV 24/7");
        media.addImage("http://i.imgur.com/GVeytTB.png");
        media.setContentType("application/x-mpegURL");

        Intent intent = new Intent(getActivity(), VideoViewActivity.class);
        intent.putExtra("media", media.toBundle());
        intent.putExtra("shouldStart", true);
        startActivity(intent);

    }
}