package com.swg.jalinatm;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.swg.jalinatm.POJO.Vendor;
import com.swg.jalinatm.Utils.InternetCheck;
import com.swg.jalinatm.Utils.Tracker;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FeedbackActivity extends Activity implements AdapterView.OnItemSelectedListener, View.OnClickListener{

    @BindView(R.id.status_spinner)
    Spinner status_spinner;
    @BindView(R.id.status_layout)
    LinearLayout status_layout;
    @BindView(R.id.cancel_button)
    Button cancel_button;
    @BindView(R.id.submit_button)
    Button submit_button;
    @BindView(R.id.et_feedback)
    EditText et_feedback;

    private int statusSelected;
    private String statusValueSelected;
    private String feedback;
    private Tracker tracker;
    private Vendor vendor;
    private int triggerButton = 0;

    private static final String TAG = "FeedbackActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        ButterKnife.bind(this);

        status_layout.setBackground(getDrawable(R.drawable.rounded_layout));

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.status_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        status_spinner.setAdapter(adapter);

        status_spinner.setOnItemSelectedListener(this);
        submit_button.setOnClickListener(this);
        cancel_button.setOnClickListener(this);

        vendor = (Vendor) Parcels.unwrap(getIntent().getParcelableExtra("vendor"));
        if(vendor==null){
            Log.e(TAG, "error vendor null");
            finish();
        }

        tracker = new Tracker(this, vendor);
        if(!tracker.isGoogleApiConnected()) tracker.connectGoogleApi();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        statusSelected = position;
        statusValueSelected = (String) parent.getItemAtPosition(position);
        Log.i(TAG, statusValueSelected);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        statusSelected = -1;
        statusValueSelected = "";
    }

    @Override
    public void onClick(View v) {
        if(triggerButton==0) {
            triggerButton=1;
            switch (v.getId()) {
                case R.id.submit_button:
                    new InternetCheck(internet -> {
                        if (internet) {
                            triggerButton=0;
                            if (et_feedback.getText() == null) feedback = "";
                            feedback = et_feedback.getText().toString();
                            Log.i(TAG, statusSelected + " " + statusValueSelected + " " + feedback + " " + vendor.getLoc().latitude + " " + vendor.getLoc().longitude);
                            setResult(1);
                            finish();
                        } else {
                            triggerButton=0;
                            Log.e(TAG, getResources().getString(R.string.no_internet_connection));
                            Toast.makeText(this, getResources().getString(R.string.no_internet_connection_toast), Toast.LENGTH_SHORT).show();
                        }
                    });
                    break;
                case R.id.cancel_button:
                    triggerButton=0;
                    finish();
                    break;
            }
        } else {
            Toast.makeText(this, getResources().getString(R.string.please_wait), Toast.LENGTH_LONG).show();
            Log.e(TAG, "user touch more than once");
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "Start state");
        if(tracker!=null && !tracker.isGoogleApiConnected()) tracker.connectGoogleApi();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e(TAG, "Stop state");
        if(tracker!=null && tracker.isGoogleApiConnected()) tracker.disconnectGoogleApi();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "Pause state");
        if(tracker!=null && tracker.isGoogleApiConnected()) tracker.stopLocationUpdate();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "Resume state");
        if(tracker!=null && tracker.isGoogleApiConnected()) tracker.startLocationUpdate();
    }
}
