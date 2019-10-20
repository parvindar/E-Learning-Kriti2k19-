package com.ankuringale.courses;

import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.ankuringale.courses.Recycler_Adapters.DownloadedClass;
import com.ankuringale.courses.Recycler_Adapters.UserInfo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Permission;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PlayVideoActivity extends AppCompatActivity {

    private MediaController mc;
    private VideoView videoView;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private File file;
    private SharedPreferences sp;
    private Gson gson;

    private TextView title,desc,inst;
    private Button like,dislike,download;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_video);

        title = findViewById(R.id.videoTitle);
        desc = findViewById(R.id.videoPDesc);
        like = findViewById(R.id.videoLike);
        dislike = findViewById(R.id.videoDislike);
        download = findViewById(R.id.Download);
        inst = findViewById(R.id.videoInstructor);
        sp = this.getSharedPreferences("Videos" , Context.MODE_PRIVATE);
        gson = new Gson();

        videoView = findViewById(R.id.videoPlayer);

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                    @Override
                    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                        mc = new MediaController(PlayVideoActivity.this);

                        videoView.setMediaController(mc);
                        mc.setAnchorView(videoView);
                    }
                });
            }
        });

        if (getIntent().getIntExtra("online", 0) == 0) {
            DownloadedClass dc = (DownloadedClass) getIntent().getSerializableExtra("download");
            title.setText(dc.getTitle());
            inst.setText(dc.getInst());
            desc.setText(dc.getDesc());
            like.setText("+" + dc.getLikes());
            dislike.setText("-" + dc.getDislikes());

            like.setClickable(false);
            dislike.setClickable(false);
            download.setClickable(false);

            download.setText("Downloaded");
            download.setTextColor(getColor(R.color.white));
            download.setBackgroundColor(Color.parseColor("#000000"));

            loadVideoFromInternalStorage(dc.getFileUri());
            videoView.start();
        }
        else {

            if(ContextCompat.checkSelfPermission(this , Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.INTERNET} , 0);
            }


            storage = FirebaseStorage.getInstance();
            db = FirebaseFirestore.getInstance();

            final String id = getIntent().getStringExtra("videoID");

            db = FirebaseFirestore.getInstance();
            db.collection("Videos").document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {

                    like.setText(("+" + documentSnapshot.get("number_of_likes").toString()));
                    dislike.setText(("-" + documentSnapshot.get("number_of_dislikes").toString()));
                    title.setText(documentSnapshot.get("title").toString());
                    desc.setText(documentSnapshot.get("description").toString());
                    inst.setText(documentSnapshot.get("instructor").toString());
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(PlayVideoActivity.this, "Failed to load Data.", Toast.LENGTH_LONG).show();
                }
            });


            try {
                file = File.createTempFile(id, "mp4");
                storage.getReference().child("Videos").child(id).getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        Log.d("DEBUG", "Download Complete");
                        videoView.setVideoURI(Uri.fromFile(file));
                        videoView.start();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }

            like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(UserInfo.logined == false){
                        UserInfo.instantLogin(PlayVideoActivity.this);
                        return;
                    }
                    like.setClickable(false);
                    dislike.setClickable(false);
                    db.collection("Videos").document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            ArrayList<String> dislikes = (ArrayList<String>)documentSnapshot.get("dislikes");
                            if(dislikes.contains(UserInfo.username)){
                                dislikes.remove(UserInfo.username);
                                Map<String , Object> upd = new HashMap<>();
                                final long k = (long)documentSnapshot.get("number_of_dislikes") - 1;
                                upd.put("dislikes" , dislikes);
                                upd.put("number_of_dislikes" , k);
                                db.collection("Videos").document(id).update(upd).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        dislike.setText("-" + k);
                                    }
                                });
                            }
                            ArrayList<String> likes = (ArrayList<String>)documentSnapshot.get("likes");
                            if(likes.contains(UserInfo.username)){
                                likes.remove(UserInfo.username);
                                Map<String , Object> upd = new HashMap<>();
                                final long k = (long)documentSnapshot.get("number_of_likes") - 1;
                                upd.put("likes" , likes);
                                upd.put("number_of_likes" , k);
                                db.collection("Videos").document(id).update(upd).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        like.setText("+" + k);
                                    }
                                });
                            }
                            else{
                                likes.add(UserInfo.username);
                                Map<String , Object> upd = new HashMap<>();
                                upd.put("likes" , likes);
                                final long k = (long)documentSnapshot.get("number_of_likes") + 1;
                                upd.put("number_of_likes" , k);
                                db.collection("Videos").document(id).update(upd).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        like.setText("+" + k);
                                    }
                                });

                            }
                            like.setClickable(true);
                            dislike.setClickable(true);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
                }
            });

            dislike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(UserInfo.logined == false){
                        UserInfo.instantLogin(PlayVideoActivity.this);
                        return;
                    }
                    dislike.setClickable(false);
                    like.setClickable(false);
                    db.collection("Videos").document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            ArrayList<String> likes = (ArrayList<String>)documentSnapshot.get("likes");
                            if(likes.contains(UserInfo.username)){
                                likes.remove(UserInfo.username);
                                Map<String , Object> upd = new HashMap<>();
                                final long k = (long)documentSnapshot.get("number_of_likes") - 1;
                                upd.put("likes" , likes);
                                upd.put("number_of_likes" , k);
                                db.collection("Videos").document(id).update(upd).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        like.setText("+" + k);
                                    }
                                });
                            }
                            ArrayList<String> dislikes = (ArrayList<String>)documentSnapshot.get("dislikes");
                            if(dislikes.contains(UserInfo.username)){
                                dislikes.remove(UserInfo.username);
                                Map<String , Object> upd = new HashMap<>();
                                final long k = (long)documentSnapshot.get("number_of_dislikes") - 1;
                                upd.put("dislikes" , dislikes);
                                upd.put("number_of_dislikes" , k);
                                db.collection("Videos").document(id).update(upd).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        dislike.setText("-" + k);
                                    }
                                });
                            }
                            else{
                                dislikes.add(UserInfo.username);
                                Map<String , Object> upd = new HashMap<>();
                                upd.put("dislikes" , dislikes);
                                final long k = (long)documentSnapshot.get("number_of_dislikes") + 1;
                                upd.put("number_of_dislikes" , k);
                                db.collection("Videos").document(id).update(upd).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        dislike.setText("-" + k);
                                    }
                                });

                            }
                            dislike.setClickable(true);
                            like.setClickable(true);
                        }
                    });
                }
            });
            download.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(file != null){
                        try {
                            saveVideoToInternalStorage();
                        }
                        catch (Exception fe){
                            fe.printStackTrace();
                        }
                    }
                }
            });
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
//            videoPlayerLayout.setBackgroundColor(getResources().getColor(R.color.black));

        }
        else{
//            videoPlayerLayout.setBackgroundColor(getResources().getColor(R.color.white));

        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//        outState.putString(videoView.);
    }


    private void saveVideoToInternalStorage () {

        File newfile;

        try {


            String fileName = file.getName();

            ContextWrapper cw = new ContextWrapper(getApplicationContext());
            File directory = cw.getDir("Videos", Context.MODE_PRIVATE);


            newfile = new File(directory, fileName);
            Log.d("DEBUG" , file.exists() + "");
            if(file.exists()){

                InputStream in = new FileInputStream(file);
                OutputStream out = new FileOutputStream(newfile);

                // Copy the bits from instream to outstream
                byte[] buf = new byte[1024];
                int len;

                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }

                in.close();
                out.close();

                Log.d("DEBUG", "Video file saved successfully.");

            }else{
                Log.d("DEBUG", "Video saving failed. Source file missing.");
            }

            DownloadedClass dc = new DownloadedClass("" , title.getText().toString() , desc.getText().toString() ,
                    inst.getText().toString() ,  Long.parseLong(like.getText().toString()) ,  Long.parseLong(dislike.getText().toString()));
            dc.setFileUri(newfile.getAbsolutePath());

            SharedPreferences.Editor spe = sp.edit();

            if(sp.getString(file.getName(), null) != null){
                Toast.makeText(this,"Video already present.",Toast.LENGTH_LONG).show();
            }
            else {
                spe.putString(file.getName(), gson.toJson(dc));
                spe.commit();
                Log.d("DEBUG", "Saved");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void loadVideoFromInternalStorage(String filePath){

        Uri uri = Uri.parse(filePath);
        videoView.setVideoURI(uri);

    }

}
