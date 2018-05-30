package com.swg.jalinatm;

import android.content.Intent;
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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.swg.jalinatm.Adapter.ATMListAdapter;
import com.swg.jalinatm.POJO.ATM;
import com.swg.jalinatm.POJO.DataWrapper;

import org.parceler.Parcels;

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
//            wrapper = (DataWrapper) getIntent().getSerializableExtra("atmlist");
//            atmList = wrapper.getAtmList();
            atmList = getIntent().getExtras().getParcelableArrayList("atmlist");
            Log.i(TAG, atmList.get(0).getId());
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

        list_atm.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ATMListActivity.this, ATMDetailActivity.class);
                intent.putExtra("atm", Parcels.wrap(atmList.get(position)));
                startActivity(intent);
            }
        });
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
                atmList.add(new ATM("ATM5", "ATM area Jakarta Utara", "PIK", new LatLng(-6.2018064, 106.7794037)));
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
        Log.i(TAG, "SaveInstace");
        super.onSaveInstanceState(outState);
    }
}
