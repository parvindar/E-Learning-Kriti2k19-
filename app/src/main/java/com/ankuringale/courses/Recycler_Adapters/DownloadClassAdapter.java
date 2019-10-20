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

import com.ankuringale.courses.PlayVideoActivity;
import com.ankuringale.courses.R;
import com.ankuringale.courses.SelectVideoActivity;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;

public class DownloadClassAdapter extends RecyclerView.Adapter<DownloadClassAdapter.MyViewHolder> {
    private List<DownloadedClass> mDataset;
    private Context context;


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder


    public  class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case

        private TextView title , likes , dislikes;


        public MyViewHolder(View v) {
            super(v);
            title = v.findViewById(R.id.videoRecyclerTitle);
            likes = v.findViewById(R.id.videoRecyclerUpvotes);
            dislikes = v.findViewById(R.id.videoRecyclerDownvotes);
            likes.setVisibility(View.INVISIBLE);
            dislikes.setVisibility(View.INVISIBLE);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent in = new Intent(context , PlayVideoActivity.class);
                    int itempos = getAdapterPosition();
                    in.putExtra("download" , mDataset.get(itempos));
                    in.putExtra("online" , 0);
                    context.startActivity(in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                }
            });
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public DownloadClassAdapter(List<DownloadedClass> myDataset , Context context) {
        mDataset = myDataset;
        this.context = context;
    }

    public void set(List<DownloadedClass> myDataset){
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public DownloadClassAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                              int viewType) {
        // create a new vie
        View v =
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.video_video_view, parent, false);

        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    //   the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        DownloadedClass cc = mDataset.get(position);
        holder.title.setText(cc.getTitle());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() { return mDataset.size(); }

}