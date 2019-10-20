package com.ankuringale.courses;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ankuringale.courses.Recycler_Adapters.UserInfo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    String username, password;
    public static Button loginBtn;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_login);
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        progressDialog= new ProgressDialog(LoginActivity.this);

        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        TextView text_reg = (TextView) findViewById(R.id.tv_registerText);
        text_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        final EditText usernameText = findViewById(R.id.et_username);
        final EditText passwordText = findViewById(R.id.et_oldpassword);
        loginBtn = findViewById(R.id.btn_login);


        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = usernameText.getText().toString();
                password = passwordText.getText().toString();
                if(username.isEmpty()||password.isEmpty())
                {
                    Toast.makeText(LoginActivity.this,"Enter you credentials to login",Toast.LENGTH_LONG).show();
                    return;
                }

                progressDialog.show();
                db.collection("Users").document(username).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Map<String, Object> user = new HashMap<>();
                                user = document.getData();
                                if(user.get("Password").equals(password)){
                                    Toast.makeText(LoginActivity.this, "Welcome "+ user.get("Name"), Toast.LENGTH_LONG).show();
                                    UserInfo.login(user.get("Username").toString(),user.get("Name").toString(),user.get("UserType").toString(),user.get("Email").toString(),user.get("Department").toString());

                                    SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("app",Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPref.edit();
                                    editor.putString("username",username);
                                    editor.putString("password",password);
                                    editor.putBoolean("logined",true);
                                    editor.commit();


                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    progressDialog.dismiss();
                                    finish();
                                }
                                else{
                                    progressDialog.dismiss();
                                    Toast.makeText(LoginActivity.this, "Incorrect username and password !", Toast.LENGTH_LONG).show();
                                }

                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(LoginActivity.this, "Username doesn't exist !", Toast.LENGTH_LONG).show();
                            }
                        }
                        progressDialog.dismiss();
                    }
                });
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }


}
