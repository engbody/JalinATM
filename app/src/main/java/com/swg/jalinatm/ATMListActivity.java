package com.swg.jalinatm;

import android.database.DataSetObserver;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.swg.jalinatm.Adapter.ATMListAdapter;
import com.swg.jalinatm.POJO.ATM;
import com.swg.jalinatm.POJO.DataWrapper;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ATMListActivity extends AppCompatActivity implements View.OnClickListener{

    @BindView(R.id.list_atm)
    ListView list_atm;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.empty_view)
    TextView empty_view;

    ArrayList<ATM> atmList;
    DataWrapper wrapper;
    ATMListAdapter adapter;

    private final static String TAG = "ATMListActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atmlist);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        if(savedInstanceState == null || !savedInstanceState.containsKey("atmList")){
            wrapper = (DataWrapper) getIntent().getSerializableExtra("atmlist");
            atmList = wrapper.getAtmList();
            Log.e(TAG, atmList.get(0).getId());
        } else {
            atmList = savedInstanceState.getParcelableArrayList("atmList");
        }

        adapter = new ATMListAdapter(this, atmList);

        adapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                Log.e(TAG, "datasetchanged");
                if (adapter.getCount() == 0) {
                    empty_view.setVisibility(View.VISIBLE);
                    list_atm.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                } else {
                    empty_view.setVisibility(View.GONE);
                    list_atm.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

        list_atm.setAdapter(adapter);

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if (adapter.getCount() == 0) {
                    empty_view.setVisibility(View.VISIBLE);
                    list_atm.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                } else {
                    empty_view.setVisibility(View.GONE);
                    list_atm.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }
            }
        }, 1000);
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onClick(View v) {
        //do something
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                atmList.add(new ATM("ATM5", "ATM area Jakarta Utara", "PIK"));
                empty_view.setVisibility(View.GONE);
                list_atm.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                adapter.notifyDataSetChanged();
                return true;
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("atmList", atmList);
        Log.e(TAG, "SaveInstace");
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore UI state from the savedInstanceState.
        // This bundle has also been passed to onCreate.
        atmList = savedInstanceState.getParcelableArrayList("atmList");
        Log.e(TAG, "RestoreInstace");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e(TAG, "Start state");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e(TAG, "Stop state");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "Destroy state");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e(TAG, "Pause state");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "Resume state");
    }
}
