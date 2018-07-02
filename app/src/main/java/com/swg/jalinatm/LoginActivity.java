package com.swg.jalinatm;

import android.*;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.swg.jalinatm.Utils.Hash;
import com.swg.jalinatm.Utils.InternetCheck;
import com.swg.jalinatm.Utils.Preferences;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

//    Button btn_login = (Button) findViewById(R.id.btn_login);

    @BindView(R.id.btn_login)
    Button btn_login;
    @BindView(R.id.input_email)
    EditText input_email;
    @BindView(R.id.input_password)
    EditText input_password;
    @BindView(R.id.root_layout)
    LinearLayout root_layout;

    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    private static final String TAG = "LoginActivity";

    private int TRIGGER_BUTTON = 0;
    private static final int MULTIPLE_PERMISSIONS = 1;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    String[] permissions= new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();

        btn_login.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(TRIGGER_BUTTON==0) {
            TRIGGER_BUTTON=1;
            switch (v.getId()) {
                case R.id.btn_login:
                    if (input_email != null && !input_email.getText().toString().isEmpty() && !input_email.getText().toString().equals("")) {
                        if (input_email.getText().toString().matches(emailPattern)) {
                            if (input_password != null && !input_password.getText().toString().isEmpty() && !input_password.getText().toString().equals("")) {
                                Log.d(TAG,"logging in...");
                                Toast.makeText(LoginActivity.this, getResources().getString(R.string.please_wait), Toast.LENGTH_LONG).show();
                                mAuth.signInWithEmailAndPassword(input_email.getText().toString(), input_password.getText().toString()).addOnCompleteListener(this, task -> {
                                    if(task.isSuccessful()){
                                        Log.d(TAG,"signInWithEmail:Success");
                                        TRIGGER_BUTTON=0;
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        Toast.makeText(LoginActivity.this, getResources().getString(R.string.login_failed), Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Log.e(TAG,"signInWithEmail:Failed " + task.getException());
                                        TRIGGER_BUTTON=0;
                                        Toast.makeText(LoginActivity.this, getResources().getString(R.string.login_failed), Toast.LENGTH_SHORT).show();
                                    }
                                });
//                                Preferences.setDetail(getApplicationContext(), "login", "1");
//                                Preferences.setDetail(getApplicationContext(), "email", input_email.getText().toString());
//                                Preferences.setDetail(getApplicationContext(), "password", Hash.md5(input_password.getText().toString()));
//                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
//                                startActivity(intent);
//                                finish();
                            } else {
                                TRIGGER_BUTTON=0;
                                input_password.setError(getResources().getString(R.string.error_password_empty));
                            }
                        } else {
                            TRIGGER_BUTTON=0;
                            input_email.setError(getResources().getString(R.string.error_email_format));
                        }
                    } else {
                        TRIGGER_BUTTON=0;
                        input_email.setError(getResources().getString(R.string.error_email_empty));
                    }
                    break;
            }
        } else {
            Toast.makeText(this, getResources().getString(R.string.please_wait), Toast.LENGTH_LONG).show();
            Log.e(TAG, "user touch more than once");
        }
    }
}
