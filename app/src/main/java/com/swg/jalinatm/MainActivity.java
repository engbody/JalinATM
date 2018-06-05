package com.swg.jalinatm;

import android.Manifest;
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
import android.widget.Toast;

import com.swg.jalinatm.Utils.InternetCheck;
import com.swg.jalinatm.Utils.Preferences;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int MULTIPLE_PERMISSIONS = 1;
    private static final String TAG = "MainActivity";

    String[] permissions= new String[]{Manifest.permission.ACCESS_FINE_LOCATION};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String locale = getResources().getConfiguration().locale.getCountry();
        Log.i(TAG, "locale: " + locale + " default: " + Locale.getDefault());

        if(Build.VERSION.SDK_INT >= 23) {
            if(checkPermissions(this)){
                doLoginWithCheckInet();
            }
        } else {
            doLoginWithCheckInet();
        }
    }

    private void doLoginWithCheckInet(){
        new InternetCheck(internet -> {
            Log.e(TAG, String.valueOf(internet));
            if(internet) {
                if(Preferences.checkDetail(getApplicationContext(), "login")){
                    Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            } else {
                Toast.makeText(this, getResources().getString(R.string.no_internet_connection_toast), Toast.LENGTH_LONG).show();
                finishAffinity();
            }
        });
    }

    private void doLogin(){
        if(Preferences.checkDetail(getApplicationContext(), "login")){
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        } else {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private boolean checkPermissions(Activity activity) {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p:permissions) {
            result = ContextCompat.checkSelfPermission(activity,p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(activity, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),MULTIPLE_PERMISSIONS );
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case MULTIPLE_PERMISSIONS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    doLoginWithCheckInet();
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, getResources().getString(R.string.check_permission), Toast.LENGTH_LONG).show();
                    finishAffinity();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }
}
