package com.ankuringale.courses;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ankuringale.courses.Recycler_Adapters.UserInfo;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

public class BookListActivity extends AppCompatActivity {


    FirebaseStorage storage;
    String club;
    Button uploadfile;
    FirebaseFirestore db;
    ProgressDialog progressDialog;
    ListView lv;
    Uri pdfUri;
    TextView selectedbookname;
    EditText search;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseApp.initializeApp(this);
        storage = FirebaseStorage.getInstance();
        db = FirebaseFirestore.getInstance();
        setContentView(R.layout.activity_book_list);
        search = findViewById(R.id.et_searchbook);
        lv = findViewById(R.id.lv_booklist);

        club= getIntent().getStringExtra("club");

        Log.d("DEBUG : "," dep/club --> "+club);

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
                db.collection("Books").document("Category").collection(club).whereGreaterThanOrEqualTo("name",s).whereLessThan("name",end).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        ArrayList<Book> bookArrayList = new ArrayList<>();
                        for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots)
                        {
                            Book book=new Book(documentSnapshot.get("name").toString(),documentSnapshot.get("author").toString(),documentSnapshot.get("tag").toString(),documentSnapshot.get("rating").toString(),documentSnapshot.get("url").toString(),documentSnapshot.get("id").toString());
                            bookArrayList.add(book);
                        }

                        BookListAdaptor bookListAdaptor = new BookListAdaptor(BookListActivity.this,R.layout.booklist_elem,bookArrayList);

                        lv.setAdapter(bookListAdaptor);

                    }



                });
            }
        });



        uploadfile = findViewById(R.id.btn_upload_file);

        uploadfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(UserInfo.logined == false){
                    Toast.makeText(BookListActivity.this,"You need to login first!" , Toast.LENGTH_LONG).show();
                    UserInfo.instantLogin(BookListActivity.this);
                    return;
                }
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(BookListActivity.this);
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.dialog_upload_pdf, null);
                dialogBuilder.setView(dialogView);
                final AlertDialog b = dialogBuilder.create();
                b.show();

                final EditText bookname = dialogView.findViewById(R.id.et_bookname);
                final EditText author = dialogView.findViewById(R.id.et_author);
                final EditText tag = dialogView.findViewById(R.id.et_tag);
                selectedbookname = dialogView.findViewById(R.id.tv_selected_filename);
                Button btn_selectbook = dialogView.findViewById(R.id.btn_browse);
                Button submit = dialogView.findViewById(R.id.btn_upload);



                btn_selectbook.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onClick(View v) {

                        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED)
                        {
                            selectpdf();

                        }
                        else {
                            requestPermissions( new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 9);
                        }

                    }
                });


                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(bookname.getText().toString().isEmpty()||tag.getText().toString().isEmpty())
                        {
                            Toast.makeText(getApplicationContext(),"Fill the details first",Toast.LENGTH_LONG).show();
                            return;
                        }

                        if(pdfUri!=null)
                            uploadFile(pdfUri,bookname.getText().toString() +".pdf",author.getText().toString(),tag.getText().toString());
                        else
                            Toast.makeText(BookListActivity.this,"Please select a file",Toast.LENGTH_LONG).show();
                    }
                });





            }
        });


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

    void uploadFile(final Uri pdfUri, final String name, final String author, final String tag) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setTitle("Uploading File...");
        progressDialog.setProgress(0);
        progressDialog.show();

        final String fileid = System.currentTimeMillis() + "";
        final StorageReference storageReference = storage.getReference().child("books").child(club).child(fileid);
        storageReference.putFile(pdfUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                long up = task.getResult().getBytesTransferred();
                long fs = task.getResult().getTotalByteCount();
                long currentprogress = (100 * up)/fs;
                progressDialog.setProgress((int)currentprogress);

                Log.d("DEBUG","progress  -->  "+currentprogress);

                // Continue with the task to get the download URL
                return storageReference.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    String url = downloadUri.toString();

                    Map<String, Object> book = new HashMap<>();
                    book.put("url", url);
                    book.put("name", name);
                    book.put("author", author);
                    book.put("tag", tag);
                    book.put("rating", 0);
                    book.put("id",fileid);
                    book.put("n", 0);
                    book.put("users",new HashMap<String , Double>());
                    db.collection("Books").document("Category").collection(club).document(fileid).set(book);
                    progressDialog.dismiss();
                } else {
                    // Handle failures
                    // ...
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(BookListActivity.this, "File did not uploaded.", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==9&& grantResults[0]==PackageManager.PERMISSION_GRANTED)
        {
            selectpdf();
        }
        else
        {
            Toast.makeText(getApplicationContext(),"Please provide permissions",Toast.LENGTH_LONG).show();
        }
    }

    void selectpdf()
    {
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,86);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(requestCode==86&&resultCode==RESULT_OK && data!=null)
        {
            pdfUri = data.getData();
            selectedbookname.setText(queryName(getContentResolver(),pdfUri));
        }
        else
        {
            Toast.makeText(this,"Please select a file",Toast.LENGTH_LONG).show();
        }
    }

    class Book {

        String name;
        String author;
        String tag;
        String rating;
        String url;
        String id;

        Book()
        {

        }

        public Book(String name, String author, String tag, String rating,String url,String id) {
            this.name = name;
            this.author = author;
            this.tag = tag;
            this.rating = rating;
            this.url = url;
            this.id = id;
        }
    }



    private class BookListAdaptor extends ArrayAdapter<Book> {
        private static final String TAG = "PlayerListAdaptor";
        private Context mContext;
        private int mResource;

        public BookListAdaptor(Context context, int resource, List<Book> objects) {
            super(context, resource, objects);
            this.mContext = context;
            this.mResource = resource;
        }




        @Override
        public View getView(final int position, View convertView, final ViewGroup parent) {
            if(getItem(position)!=null) {
                LayoutInflater inflater = LayoutInflater.from(mContext);
                convertView = inflater.inflate(mResource, parent, false);

                TextView name = convertView.findViewById(R.id.tv_bookname);
                TextView author = convertView.findViewById(R.id.tv_authorname);
                TextView rating = convertView.findViewById(R.id.tv_rating);

                name.setText(getItem(position).name);
                author.setText(getItem(position).author);
                rating.setText(getItem(position).rating);


                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(getItem(position)!=null)
                        {
                            Intent intent = new Intent(BookListActivity.this,PdfViewerActivity.class);
                            intent.putExtra("name",getItem(position).name);
                            intent.putExtra("author",getItem(position).author);
                            intent.putExtra("rating",getItem(position).rating);
                            intent.putExtra("url",getItem(position).url);
                            intent.putExtra("id",getItem(position).id);
                            intent.putExtra("club",club);
                            startActivity(intent);

                        }

                    }
                });


            }
            return convertView;

        }



    }




}
