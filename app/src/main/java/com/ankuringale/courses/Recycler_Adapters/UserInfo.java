package com.ankuringale.courses.Recycler_Adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ankuringale.courses.R;
import com.ankuringale.courses.RegisterActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class UserInfo {

    public static  boolean logined=false;
    public static  String fullname;
    public static  String username;
    public static  String usertype;
    public static  String email;
    public static  String department;

    static ProgressDialog progressDialog;

    public static void login(String _username, String _fullname, String _usertype, String _email, String _department)
    {
        fullname = _fullname;
        username = _username;
        usertype = _usertype;
        email = _email;
        department = _department;

        logined = true;

    }



    public static void instantLogin(final Context context)
    {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.activity_login, null);
        dialogBuilder.setView(dialogView);
        final AlertDialog b = dialogBuilder.create();
        b.show();


        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        TextView text_reg = (TextView) dialogView.findViewById(R.id.tv_registerText);
        text_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, RegisterActivity.class);
                context.startActivity(intent);
            }
        });

        final EditText usernameText = dialogView.findViewById(R.id.et_username);
        final EditText passwordText = dialogView.findViewById(R.id.et_oldpassword);
        Button loginBtn = dialogView.findViewById(R.id.btn_login);


        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username,password;
                username = usernameText.getText().toString();
                password = passwordText.getText().toString();

                if(username.isEmpty() || password.isEmpty())
                {
                    Toast.makeText(context,"Enter username and password to login.", Toast.LENGTH_LONG).show();
                    return;
                }
                progressDialog = new ProgressDialog(context);
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
                                    Toast.makeText(context, "Welcome "+ user.get("Name"), Toast.LENGTH_LONG).show();
                                    UserInfo.login(user.get("Username").toString(),user.get("Name").toString(),user.get("UserType").toString(),user.get("Email").toString(),user.get("Department").toString());

                                    SharedPreferences sharedPref = context.getSharedPreferences("app",Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPref.edit();
                                    editor.putString("username",username);
                                    editor.putString("password",password);
                                    editor.putBoolean("logined",true);
                                    editor.commit();


//                                    Intent intent = new Intent(context, HomeActivity.class);
//                                    startActivity(intent);

                                    b.dismiss();
                                    progressDialog.dismiss();
                                }
                                else{
                                    progressDialog.dismiss();
                                    Toast.makeText(context, "Incorrect username and password !", Toast.LENGTH_LONG).show();
                                }

                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(context, "Username doesn't exist !", Toast.LENGTH_LONG).show();
                            }
                        }
                        progressDialog.dismiss();
                    }
                });
            }
        });




    }



    private static final UserInfo ourInstance = new UserInfo();

    public static UserInfo getInstance() {
        return ourInstance;
    }

    private UserInfo() {
    }
}
