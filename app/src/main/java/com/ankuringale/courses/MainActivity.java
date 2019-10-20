package com.ankuringale.courses;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.ankuringale.courses.Recycler_Adapters.UserInfo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(getApplicationContext());
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        db.setFirestoreSettings(settings);


        findViewById(R.id.toBooks).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this , BookActivity.class);
                startActivity(i);
            }
        });
        findViewById(R.id.toVideos).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this , VideoActivity.class);
                startActivity(i);
            }
        });



        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("app", Context.MODE_PRIVATE);
        Boolean islogined =sharedPref.getBoolean("logined",false);
        if(islogined)
        {
            final String username = sharedPref.getString("username",null);
            final String password = sharedPref.getString("password",null);
            if(username!=null && password!=null)
            {
                db.collection("Users").document(username).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Map<String, Object> user = new HashMap<>();
                                user = document.getData();
                                if(user.get("Password").equals(password)){
                                    Toast.makeText(MainActivity.this, "Welcome "+ user.get("Name"), Toast.LENGTH_LONG).show();
//                                    UserInfo.login(user.get("UserType").toString(),user.get("Username").toString(),user.get("Name").toString(),user.get("Email").toString(),Double.parseDouble(user.get("Cash").toString()),Double.parseDouble(user.get("Winnings").toString()),Double.parseDouble(user.get("xp").toString()));
                                    UserInfo.login(user.get("Username").toString(),user.get("Name").toString(),user.get("UserType").toString(),user.get("Email").toString(),user.get("Department").toString());
                                }
                                else{
                                    Toast.makeText(MainActivity.this, "Auto-login failed. Login manually", Toast.LENGTH_LONG).show();
                                }

                            }
                        }
                    }
                });

            }
        }
    }
}
