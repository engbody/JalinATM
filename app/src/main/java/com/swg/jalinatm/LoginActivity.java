package com.swg.jalinatm;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.swg.jalinatm.Utils.Hash;
import com.swg.jalinatm.Utils.Preferences;

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

    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        btn_login.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_login:
                if(input_email != null && !input_email.getText().toString().isEmpty() && !input_email.getText().toString().equals("")){
                    if(input_email.getText().toString().matches(emailPattern)) {
                        if (input_password != null && !input_password.getText().toString().isEmpty() && !input_password.getText().toString().equals("")) {
                            Preferences.setDetail(getApplicationContext(), "login", "1");
                            Preferences.setDetail(getApplicationContext(), "email", input_email.getText().toString());
                            Preferences.setDetail(getApplicationContext(), "password", Hash.md5(input_password.getText().toString()));
                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            input_password.setError(getResources().getString(R.string.error_password_empty));
                        }
                    } else {
                        input_email.setError(getResources().getString(R.string.error_email_format));
                    }
                } else {
                    input_email.setError(getResources().getString(R.string.error_email_empty));
                }
                break;
        }
    }
}
