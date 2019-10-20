package com.ankuringale.courses;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {


    String name, email, username, password, confirmPassword;
    Boolean username_status = false;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_register);
        progressDialog = new ProgressDialog(RegisterActivity.this);
        FirebaseApp.initializeApp(RegisterActivity.this);
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        super.onCreate(savedInstanceState);
        TextView text_reg = (TextView) findViewById(R.id.tv_loginText);
        text_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
        final EditText nameText = findViewById(R.id.et_name);
        final EditText emailText = findViewById(R.id.et_email);
        final EditText usernameText = findViewById(R.id.et_username);
        final EditText passwordText = findViewById(R.id.et_oldpassword);
        final EditText confirmPasswordText = findViewById(R.id.et_confirm_password);
        Button registerButton = findViewById(R.id.btn_register);

        passwordText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                password = passwordText.getText().toString();
                confirmPassword = confirmPasswordText.getText().toString();
                TextView passwordStatus = findViewById(R.id.tv_passwordStatus);
                if(password.isEmpty())
                {
                    passwordStatus.setVisibility(View.GONE);
                }

                if(password.length() < 6){
                    passwordStatus.setVisibility(View.VISIBLE);
                    passwordStatus.setText("Password must be at least 6 characters long!");
                    passwordStatus.setTextColor(Color.RED);
                }
                /*else if(!password.equals(confirmPassword)){
                    password_status = false;
                    passwordStatus.setVisibility(View.VISIBLE);
                    passwordStatus.setText("Passwords do not match!");
                    passwordStatus.setTextColor(Color.RED);
                }*/
                else{
                    passwordStatus.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        confirmPasswordText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                password = passwordText.getText().toString();
                confirmPassword = confirmPasswordText.getText().toString();
                TextView passwordStatus = findViewById(R.id.tv_passwordStatus);
                if(confirmPassword.isEmpty())
                {
                    passwordStatus.setVisibility(View.GONE);
                }
                if(!password.equals(confirmPassword)){

                    passwordStatus.setVisibility(View.VISIBLE);
                    passwordStatus.setText("Passwords do not match!");
                    passwordStatus.setTextColor(Color.RED);
                }
                else{
                    passwordStatus.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        usernameText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                username = usernameText.getText().toString();
                if(username.isEmpty())
                {
                    TextView usernameStatus = findViewById(R.id.tv_usernameStatus);
                    usernameStatus.setVisibility(View.GONE);
                }
                if(!username.isEmpty()) {

                    db.collection("Users").document(username).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                TextView usernameStatus = findViewById(R.id.tv_usernameStatus);
                                if (document.exists()) {
                                    // username already exists !
                                    usernameStatus.setVisibility(View.VISIBLE);
                                    usernameStatus.setText("Username already exists!");
                                    usernameStatus.setTextColor(Color.RED);
                                    username_status = false;

                                    // resolving back problem
                                    if(username.isEmpty()){
                                        usernameStatus.setVisibility(View.GONE);
                                    }
                                } else {
                                    // username available
                                    usernameStatus.setVisibility(View.VISIBLE);
                                    usernameStatus.setText("Username is available!");
                                    usernameStatus.setTextColor(Color.GREEN);
                                    username_status = true;

                                    // resolving back problem
                                    if(username.isEmpty()){
                                        usernameStatus.setVisibility(View.GONE);
                                    }
                                }
                            }
                        }
                    });
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = nameText.getText().toString();
                email = emailText.getText().toString();
                username = usernameText.getText().toString();
                password = passwordText.getText().toString();
                confirmPassword = confirmPasswordText.getText().toString();
                if(name.isEmpty() || email.isEmpty() || username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()){
                    Toast.makeText(RegisterActivity.this, "please fill all the fields !", Toast.LENGTH_LONG).show();
                    return;
                }
                if(!password.equals(confirmPassword)) {
                    Toast.makeText(RegisterActivity.this, "Passwords do not match !", Toast.LENGTH_LONG).show();
                    return;
                }
                if(password.length()<6) {
                    Toast.makeText(RegisterActivity.this, "Password is too short ! (At least 6 characters)", Toast.LENGTH_LONG).show();
                    return;
                }
                if(!username_status){
                    Toast.makeText(RegisterActivity.this, "Username already exists !", Toast.LENGTH_LONG).show();
                    return;
                }

                progressDialog.show();
                Map<String, Object> user = new HashMap<>();
                user.put("Name", name);
                user.put("Email", email);
                user.put("Username", username);
                user.put("Password", password);
                user.put("Department","any");
                user.put("UserType","user");
                user.put("Name_insensitive",name.toLowerCase());
                user.put("Username_insensitive",username.toLowerCase());


                db.collection("Users")
                        .document(username)
                        .set(user)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(RegisterActivity.this, "Registered Successfully !", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                startActivity(intent);
                                progressDialog.dismiss();
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(RegisterActivity.this, "Check your internet connection !", Toast.LENGTH_LONG).show();
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

