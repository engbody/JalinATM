package com.swg.jalinatm;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.swg.jalinatm.Adapter.ATMListAdapter;
import com.swg.jalinatm.POJO.ATM;
import com.swg.jalinatm.POJO.Vendor;
import com.swg.jalinatm.Utils.InternetCheck;

import org.parceler.Parcels;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
    @BindView(R.id.root_layout)
    LinearLayout root_layout;

    private ArrayList<ATM> atmList;
    private ATMListAdapter adapter;
    private Vendor vendor;
    private int triggerTouchMenu = 0;

    private final static String TAG = "ATMListActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atmlist);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        vendor = (Vendor) Parcels.unwrap(getIntent().getParcelableExtra("vendor"));
        if(vendor== null){
            Log.e(TAG, "error vendor is null");
            finish();
        }

        new InternetCheck(internet -> {
            Log.e(TAG, String.valueOf(internet));
            if(internet) {
                reloadData();
            } else {
                setVisibilityNoInternet();
            }
        });

        list_atm.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(ATMListActivity.this, ATMDetailActivity.class);
            Bundle bundle = new Bundle();
            bundle.putParcelable("atm", Parcels.wrap(atmList.get(position)));
            bundle.putParcelable("vendor", Parcels.wrap(vendor));
            intent.putExtras(bundle);
            startActivity(intent);
        });
    }

    private void reloadData(){
        atmList = new ArrayList<ATM>();
        atmList.add(new ATM("ATM1", "Di dalam gedung Grand Indonesia", null, new LatLng(-6.1950034, 106.81978660000004)));
        atmList.add(new ATM("ATM2", "Di dalam gedung Epiwalk", null, new LatLng(-6.2182575, 106.83518779999997)));
        atmList.add(new ATM("ATM3", "Di dalam gedung Mall Kelapa Gading", null, new LatLng(-6.157519, 106.90802080000003)));
        atmList.add(new ATM("ATM4", "Di dalam gedung Universitas Bina Nusantara Anggrek", null, new LatLng(-6.2018064, 106.78159240000002)));

        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        for (ATM atm : atmList) {
            try {
                List<Address> addresses = geocoder.getFromLocation(atm.getLoc().latitude, atm.getLoc().longitude, 1);
                Address address = addresses.get(0);
                atm.setAddress(address.getAddressLine(0));

            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, e.getMessage());
            }
        }

        adapter = new ATMListAdapter(this, atmList);

        list_atm.setAdapter(adapter);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (adapter.getCount() == 0) {
                setVisibilityNoData();
            } else {
                setVisibilityDataAvailable();
            }
        }, 1000);
    }

    private void setVisibilityNoInternet(){
        Toast.makeText(this, getResources().getString(R.string.no_internet_connection_toast), Toast.LENGTH_LONG).show();
        empty_view.setVisibility(View.VISIBLE);
        empty_view.setText(getResources().getString(R.string.no_internet_connection));
        list_atm.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
    }

    private void setVisibilityNoData(){
        empty_view.setVisibility(View.VISIBLE);
        empty_view.setText(getResources().getString(R.string.no_data_available));
        list_atm.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
    }

    private void setVisibilityDataAvailable(){
        empty_view.setVisibility(View.GONE);
        list_atm.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }

    private void setVisibilityLoading(){
        empty_view.setVisibility(View.GONE);
        list_atm.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
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
                if(triggerTouchMenu==0) {
                    triggerTouchMenu=1;
                    setVisibilityLoading();
                    new InternetCheck(internet -> {
                        if (internet) {
                            reloadData();
                        } else {
                            setVisibilityNoInternet();
                        }
                        triggerTouchMenu=0;
                    });
                } else {
                    Toast.makeText(this, getResources().getString(R.string.please_wait), Toast.LENGTH_LONG).show();
                    Log.e(TAG, "user touch more than once");
                }
                return true;
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
