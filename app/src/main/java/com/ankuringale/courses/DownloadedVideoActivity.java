package com.ankuringale.courses;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.ankuringale.courses.Recycler_Adapters.CourseClass;
import com.ankuringale.courses.Recycler_Adapters.CourseClassAdapter;
import com.ankuringale.courses.Recycler_Adapters.DownloadClassAdapter;
import com.ankuringale.courses.Recycler_Adapters.DownloadedClass;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DownloadedVideoActivity extends AppCompatActivity {
    private RecyclerView mRecycler;
    private DownloadClassAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<DownloadedClass> myDataset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downloaded_video);

        mRecycler = findViewById(R.id.downloadRecycler);
        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecycler.setLayoutManager(mLayoutManager);
        myDataset = new ArrayList<>();
        mAdapter = new DownloadClassAdapter(myDataset , this);
        mRecycler.setAdapter(mAdapter);

        SharedPreferences sp = getSharedPreferences("Videos" , Context.MODE_PRIVATE);
        Gson gson = new Gson();

        for(Object val : sp.getAll().values()){
            String s = (String)val;
            Log.d("DEBUG" , s);
            DownloadedClass dc = gson.fromJson(s , DownloadedClass.class);
            Log.d("DEBUG",dc.getDislikes() + "");
            myDataset.add(dc);
            mAdapter.set(myDataset);
            mAdapter.notifyDataSetChanged();
        }

    }
}
