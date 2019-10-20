package com.ankuringale.courses;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ankuringale.courses.Recycler_Adapters.UserInfo;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnRenderListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class PdfViewerActivity extends AppCompatActivity {

    String url,name,author,club,fileid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_viewer);

        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setTitle("Loading file...");
        progressDialog.setProgress(0);
        progressDialog.show();
        final FirebaseStorage storage= FirebaseStorage.getInstance();
        club = getIntent().getStringExtra("club");
        fileid = getIntent().getStringExtra("id");
        url = getIntent().getStringExtra("url");
        name = getIntent().getStringExtra("name");
        author = getIntent().getStringExtra("author");
        final PDFView pdfView = findViewById(R.id.pdfView);

        final StorageReference storageReference = storage.getReference().child("books").child(club).child(fileid);


        final File file;
        try {
            file = File.createTempFile(fileid,"pdf");

            storageReference.getFile(file).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {

                    long c1 = taskSnapshot.getBytesTransferred();
                    long c2 = taskSnapshot.getTotalByteCount();
                    int currentprogress = (int)((100*c1) / c2);
                    progressDialog.setProgress(currentprogress);
                    Log.d("DEBUG","progress  -->  "+currentprogress);


                }
            }).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                    pdfView.fromFile(file)
                            .defaultPage(0)
                            .enableSwipe(true)

                            .swipeHorizontal(false)
                            .enableAnnotationRendering(true)
                            .scrollHandle(new DefaultScrollHandle(PdfViewerActivity.this))
                            .load();

                    progressDialog.dismiss();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(PdfViewerActivity.this,"Could not download!",Toast.LENGTH_LONG).show();
                }
            });








            Button savebtn = findViewById(R.id.btn_save);
            Button ratebtn = findViewById(R.id.btn_rate);

            ratebtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(!UserInfo.logined)
                    {
                        Toast.makeText(PdfViewerActivity.this,"You need to login first",Toast.LENGTH_LONG).show();
                        UserInfo.instantLogin(PdfViewerActivity.this);
                        return;
                    }

                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(PdfViewerActivity.this);
                    LayoutInflater inflater = getLayoutInflater();
                    View dialogView = inflater.inflate(R.layout.dialog_rate, null);
                    dialogBuilder.setView(dialogView);
                    final AlertDialog b = dialogBuilder.create();
                    b.show();
                    TextView nametxt = dialogView.findViewById(R.id.tv_name);
                    TextView authortxt = dialogView.findViewById(R.id.tv_author);
                    final TextView ratetxt = dialogView.findViewById(R.id.tv_rating);
                    SeekBar ratebar = dialogView.findViewById(R.id.rate_bar);
                    Button submitbtn = dialogView.findViewById(R.id.btn_submit);
//                    Button cancelbtn = dialogView.findViewById(R.id.btn_cancel);

                    nametxt.setText(name);
                    authortxt.setText(author);


                    ratebar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                            ratetxt.setText(String.valueOf((double)progress/2.0));

                            Log.d("debug","ratebar = "+progress);

                        }

                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar) {

                        }

                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {

                        }
                    });

                    submitbtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final Double rating = Double.valueOf(ratetxt.getText().toString());
                            final FirebaseFirestore db = FirebaseFirestore.getInstance();

                            db.collection("Books").document("Category").collection(club).document(fileid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {

                                    if(documentSnapshot!=null)
                                    {
                                        DecimalFormat dec = new DecimalFormat("#0.00");
                                        Double totalrate = Double.parseDouble( documentSnapshot.get("rating").toString());
                                        Long num = (Long)documentSnapshot.get("n");
                                        Double newrate = (totalrate*num + rating)/ (num+1);

                                        num++;

                                        Log.d("debug ","prevrate "+totalrate+" num = "+num+" newrate= "+newrate);

                                        dec.format(rating);
                                        Map<String,Object> book  = new HashMap<>();
                                        book = documentSnapshot.getData();
                                        book.put("rating",newrate);
                                        book.put("n",num);

                                        db.collection("Books").document("Category").collection(club).document(fileid).set(book);

                                    }


                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(PdfViewerActivity.this,"Error in rating",Toast.LENGTH_LONG).show();
                                }
                            });


                        }
                    });




                }
            });

            savebtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


//                    FileOutputStream fileOutputStream = null;
//                    File newfile = new File(getExternalFilesDir("/Learn"), name);
//
//                    try {
//                        fileOutputStream = new FileOutputStream(newfile,true);
//
//
//
//                    } catch (FileNotFoundException e) {
//                        e.printStackTrace();
//                    }
//


                }
            });












        } catch (IOException e) {
            e.printStackTrace();
        }
        // pdfView.fromUri(Uri.parse(url)).load();

//        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED)
//        {
//
//            new getteamtask().execute();
//        }
//        else {
//            ActivityCompat.requestPermissions(PdfViewer.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 10);
//        }



//        try {
//            final File file = File.createTempFile(name,author);
//            storage.getReferenceFromUrl(url).getFile(file).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
//                @Override
//                public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
//                    pdfView.fromFile(file).load();
//                }
//            });
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }






    }


    class getteamtask extends AsyncTask<String, Boolean, Boolean> {



        @Override
        protected Boolean doInBackground(String... params) {
            //Do Stuff that takes ages (background thread)


            File dir = new File("content://documents/E-Learn");
//            FileDownloader.downloadFile(url,dir);


            return true;
        }


        @Override
        protected void onPostExecute(Boolean result) {
            //Call your next task (ui thread)




        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==10&& grantResults[0]==PackageManager.PERMISSION_GRANTED)
        {
            new getteamtask().execute();
        }
        else
        {
            Toast.makeText(getApplicationContext(),"Please provide permissions",Toast.LENGTH_LONG).show();
        }
    }




}




