package com.ankuringale.courses;

import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.ankuringale.courses.Recycler_Adapters.CourseClass;
import com.ankuringale.courses.Recycler_Adapters.CourseClassAdapter;
import com.ankuringale.courses.Recycler_Adapters.VideoClass;
import com.ankuringale.courses.Recycler_Adapters.VideoClassAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class SelectVideoActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private TextView title;

    private RecyclerView mRecycler;
    private VideoClassAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<VideoClass> myDataset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_video);


        String CourseName = getIntent().getStringExtra("course");
        db = FirebaseFirestore.getInstance();
        title = findViewById(R.id.videoVideoTitle);
        title.setText(CourseName);


        mRecycler = findViewById(R.id.videoRecyclerVideos);
        myDataset = new ArrayList<VideoClass>();
        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecycler.setLayoutManager(mLayoutManager);
        mAdapter = new VideoClassAdapter(myDataset, getApplicationContext());
        mRecycler.setAdapter(mAdapter);

        Log.d("DEBUG" , "Entering");
        db.collection("Videos").whereEqualTo("course" , CourseName.toLowerCase()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(QueryDocumentSnapshot qds : queryDocumentSnapshots){
                    String title , id;
                    long likes , dislikes;
                    title = qds.get("title").toString();
                    Log.d("DEBUG" , title);
                    likes = (long)qds.get("number_of_likes");
                    dislikes = (long)qds.get("number_of_dislikes");
                    id = qds.getId();
                    VideoClass vc = new VideoClass(title , id, likes, dislikes);
                    myDataset.add(vc);
                    mAdapter.set(myDataset);
                    mAdapter.notifyDataSetChanged();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SelectVideoActivity.this , "Failed to load videos." , Toast.LENGTH_LONG).show();
            }
        });

    }
}
