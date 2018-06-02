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
        switch(v.getId()){
            case R.id.submit_button:
                if(et_feedback.getText() == null) feedback = "";
                feedback = et_feedback.getText().toString();
                Log.e(TAG, statusSelected + " " + statusValueSelected + " " + feedback);
                setResult(1);
                finish();
                break;
            case R.id.cancel_button:
                finish();
                break;
        }
    }
}
