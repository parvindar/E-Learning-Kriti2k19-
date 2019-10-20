package com.ankuringale.courses;

import android.Manifest;
import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.ankuringale.courses.Recycler_Adapters.UserInfo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UploadVideoActivity extends AppCompatActivity {

    private EditText courseID , title, desc, inst;
    private Button select, upload;
    private TextView name;
    private MediaStore.Video video;
    private FirebaseStorage storage;
    private FirebaseFirestore db;
    private Uri videoURI;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_video);


        courseID = findViewById(R.id.videoCourseID);
        title = findViewById(R.id.videoTitle);
        desc = findViewById(R.id.videoDesc);
        inst = findViewById(R.id.videoInstructor);
        select = findViewById(R.id.videoSelect);
        upload = findViewById(R.id.videoUpload);
        name = findViewById(R.id.videoNameDisp);
        storage = FirebaseStorage.getInstance();
        db = FirebaseFirestore.getInstance();

        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("video/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent , 0);
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(courseID.getText().toString().equals("") || title.getText().toString().equals("") || desc.getText().toString().equals("")){
                    Toast.makeText(UploadVideoActivity.this , "Please fill all fields.", Toast.LENGTH_LONG).show();
                }
                else if(videoURI == null){
                    Toast.makeText(UploadVideoActivity.this , "File was not chosen.", Toast.LENGTH_LONG).show();
                }
                else {
                    uploadVideo(videoURI);
                }
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(resultCode == RESULT_OK && data!=null)
        {
            videoURI = data.getData();
            name.setText(queryName(getContentResolver() , videoURI));
        }
        else
        {
            Toast.makeText(this,"Please select a video.",Toast.LENGTH_LONG).show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private String queryName(ContentResolver resolver, Uri uri) {
        Cursor returnCursor =
                resolver.query(uri, null, null, null, null);
        assert returnCursor != null;
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        returnCursor.moveToFirst();
        String name = returnCursor.getString(nameIndex);
        returnCursor.close();
        return name;
    }

    private void uploadVideo(Uri uri){

        if(UserInfo.logined == false){
            Toast.makeText(this, "You need to login first!" , Toast.LENGTH_LONG).show();
            UserInfo.instantLogin(this);
            return;
        }
        final ProgressDialog progressDialog = new ProgressDialog(UploadVideoActivity.this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setTitle("Uploading Video...");
        progressDialog.setProgress(0);
        progressDialog.show();

        final String fileid = System.currentTimeMillis() + "";
        final String course = courseID.getText().toString();
        StorageReference storageReference = storage.getReference();

        storageReference.child("Videos").child(fileid).putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Log.d("DEBUG" , "Cool");

                        String url = taskSnapshot.getMetadata().getReference().getDownloadUrl().toString();
                        Map<String,Object> video = new HashMap<>();
                        video.put("fileID",fileid);
                        video.put("url",url);
                        video.put("title",title.getText().toString());
                        video.put("description",desc.getText().toString());
                        video.put("instructor", inst.getText().toString());
                        video.put("likes", new ArrayList<String>());
                        video.put("dislikes",new ArrayList<String>());
                        video.put("course" , course.toLowerCase());
                        video.put("number_of_likes" , 0);
                        video.put("number_of_dislikes" , 0);
                        video.put("username" , UserInfo.username);
                        db.collection("Videos").
                        document(fileid).set(video).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(UploadVideoActivity.this, "Video Uploaded Sucessfully.",Toast.LENGTH_LONG).show();
                                title.setText("");
                                desc.setText("");
                                name.setText("");
                                videoURI = null;
                                progressDialog.dismiss();
                            }
                        });

                        final DocumentReference d = db.collection("VideoCourses").document(course.toLowerCase());
                        d.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if(documentSnapshot.exists()){
                                    if(documentSnapshot.get("count") != null){
                                        d.update("count" , FieldValue.increment(1));
                                    }
                                    else{
                                        d.set(new HashMap<String , Object>(){{
                                            put("count" , 1);
                                            put("course" , course.toUpperCase());
                                        }});
                                    }
                                }
                                else {
                                    d.set(new HashMap<String, Object>() {{
                                        put("count", 1);
                                        put("course" , course.toUpperCase());
                                    }});
                                }
                            }
                        });

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UploadVideoActivity.this,"Video Couldn't be uploaded. Please try again.",Toast.LENGTH_LONG).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                long up = taskSnapshot.getBytesTransferred();
                long fs = taskSnapshot.getTotalByteCount();
                long currentprogress = (100 * up)/fs;
                progressDialog.setProgress((int)currentprogress);
                Log.d("DEBUG","progress  -->  "+currentprogress);
            }
        });
    }

}
