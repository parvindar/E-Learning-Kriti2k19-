package com.ankuringale.courses;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.ankuringale.courses.Recycler_Adapters.CourseClass;
import com.ankuringale.courses.Recycler_Adapters.CourseClassAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.security.Key;
import java.util.ArrayList;
import java.util.List;

public class SelectCourseActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private EditText search;

    private RecyclerView mRecycler;
    private CourseClassAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<CourseClass> myDataset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_course);


        db = FirebaseFirestore.getInstance();
        search = findViewById(R.id.videoCSearch);
        mRecycler = findViewById(R.id.videoCourseRecycler);
        myDataset = new ArrayList<CourseClass>();
        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecycler.setLayoutManager(mLayoutManager);
        mAdapter = new CourseClassAdapter(myDataset, getApplicationContext());
        mRecycler.setAdapter(mAdapter);

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable p) {
                String s = search.getText().toString();
                if(s.equals(""))
                    return;
                String st = s.substring(0 , s.length() - 1) , end = s.substring(s.length() - 1 , s.length());
                end = st + Character.toString((char)((int)end.charAt(end.length() - 1) + 1));

                db.collection("VideoCourses").whereGreaterThanOrEqualTo("course",
                       s.toUpperCase()).whereLessThan("course" , end.toUpperCase()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        myDataset.clear();
                        for(QueryDocumentSnapshot qds : queryDocumentSnapshots){
                            myDataset.add(new CourseClass(qds.get("course").toString() , (long)qds.get("count")));
                            Log.d("DEBUG" , qds.getId());
                            mAdapter.set(myDataset);
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SelectCourseActivity.this, "Failed to load resources.", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
        
    }
}
