package com.ankuringale.courses.Recycler_Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ankuringale.courses.R;
import com.ankuringale.courses.SelectVideoActivity;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;

public class CourseClassAdapter extends RecyclerView.Adapter<CourseClassAdapter.MyViewHolder> {
    private List<CourseClass> mDataset;
    private Context context;


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder


    public  class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case

        private TextView coursename , counter;


        public MyViewHolder(View v) {
            super(v);
            coursename = v.findViewById(R.id.videoRecyclerCourseName);
            counter = v.findViewById(R.id.videoRecyclerVideosCount);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent in = new Intent(context , SelectVideoActivity.class);
                    int itempos = getAdapterPosition();
                    in.putExtra("course" , mDataset.get(itempos).getCoursename());
                    context.startActivity(in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                }
            });
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public CourseClassAdapter(List<CourseClass> myDataset , Context context) {
        mDataset = myDataset;
        this.context = context;
    }

    public void set(List<CourseClass> myDataset){
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public CourseClassAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                            int viewType) {
        // create a new vie
        View v =
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.video_course_view, parent, false);

        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    //   the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        CourseClass cc = mDataset.get(position);
        holder.coursename.setText(cc.getCoursename());
        holder.counter.setText("Number of Videos : " + cc.getNumberOfVideos());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() { return mDataset.size(); }

}