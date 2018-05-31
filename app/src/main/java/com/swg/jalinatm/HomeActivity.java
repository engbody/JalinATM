package com.swg.jalinatm;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
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
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.swg.jalinatm.Adapter.TicketListAdapter;
import com.swg.jalinatm.POJO.ATM;
import com.swg.jalinatm.POJO.Ticket;
import com.swg.jalinatm.Utils.InternetCheck;
import com.swg.jalinatm.Utils.Preferences;

import org.parceler.Parcels;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity {

    @BindView(R.id.list_tickets)
    ListView list_tickets;
    @BindView(R.id.empty_view)
    TextView empty_view;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.root_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.nav_view)
    NavigationView nav_view;

    TicketListAdapter ticketListAdapter;
    ArrayList<Ticket> ticketList;
    ArrayList<ATM> atmList;

    private final static String TAG = "HomeActivity";

    private Bundle savedInstanceState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "Create state");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        Log.e(TAG, "MAPS_API_KEY: " + getResources().getString(R.string.MAPS_API_KEY));

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_drawer_white);

        new InternetCheck(internet -> {
            Log.e(TAG, String.valueOf(internet));
            if(internet) {
                reloadDataandView();
            } else {
                setVisibilityNoInternet();
            }
        });

        list_tickets.setOnItemClickListener((parent, view, position, id) -> {
            Ticket ticket = (Ticket) parent.getItemAtPosition(position);
            Log.i(TAG, "Ticket no: " + ticket.getTicketNumber());

            ATM atmTicket = new ATM();
            for (ATM atm : atmList) {
                if (atm.getId().equals(ticket.getMachineNumber())) {
                    atmTicket = atm;
                    break;
                }
            }

            Intent intent = new Intent(HomeActivity.this, TicketDetailActivity.class);
            intent.putExtra("ticket", Parcels.wrap(ticket));
            intent.putExtra("atm", Parcels.wrap(atmTicket));
            startActivity(intent);
        });

        getIntentData();

        nav_view.setNavigationItemSelectedListener(item -> {
            item.setCheckable(false);
            switch (item.getItemId()) {
                case R.id.nav_update_location_atm:
                    Intent intent = new Intent(HomeActivity.this, ATMListActivity.class);
                    startActivity(intent);
                    break;
                case R.id.nav_log_out:
                    Preferences.deleteKey(getApplicationContext(), "login");
                    Intent intent_login = new Intent(HomeActivity.this, LoginActivity.class);
                    startActivity(intent_login);
                    finish();
                    break;
            }
            drawerLayout.closeDrawers();
            return true;
        });

    }

    private void reloadDataandView(){
        ticketList = new ArrayList<Ticket>();
        atmList = new ArrayList<ATM>();
        ticketList.add(new Ticket("#1", "Cash Handler Fatal Error", "ATM tidak mengeluarkan uang, dan tidak respond", "ATM1", null));
        ticketList.add(new Ticket("#2", "Outstanding Down", "ATM tidak ada koneksi", "ATM2", "1")); //accepted
        ticketList.add(new Ticket("#3", "Cash Out", "Uang di ATM habis", "ATM3", "2")); //finished
        atmList.add(new ATM("ATM1", "Di dalam gedung Grand Indonesia", null, new LatLng(-6.1950034, 106.81978660000004)));
        atmList.add(new ATM("ATM2", "Di dalam gedung Epiwalk", null, new LatLng(-6.2182575, 106.83518779999997)));
        atmList.add(new ATM("ATM3", "Di dalam gedung Mall Kelapa Gading", null, new LatLng(-6.157519, 106.90802080000003)));
        atmList.add(new ATM("ATM4", "Di dalam gedung Universitas Bina Nusantara Anggrek", null, new LatLng(-6.2018064, 106.78159240000002)));

        ticketListAdapter = new TicketListAdapter(this, ticketList);
        list_tickets.setAdapter(ticketListAdapter);

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

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (ticketListAdapter.getCount() == 0) {
                setVisibilityNoData();
            } else {
                setVisibilityDataAvailable();
            }
        }, 1000);
    }

    private void getIntentData(){
        // ATTENTION: This was auto-generated to handle app links.
        Intent appLinkIntent = getIntent();
        String appLinkAction = appLinkIntent.getAction();
        Uri appLinkData = appLinkIntent.getData();
        if(appLinkData!=null) {
            Log.i(TAG, appLinkData.toString());
            Log.i(TAG, appLinkData.getQueryParameter("key"));
            if(appLinkData.getQueryParameter("key")!=null){
                //do something
            }
        }
    }

    private void setVisibilityNoInternet(){
        Toast.makeText(this, getResources().getString(R.string.no_internet_connection_toast), Toast.LENGTH_LONG).show();
        empty_view.setVisibility(View.VISIBLE);
        empty_view.setText(getResources().getString(R.string.no_internet_connection));
        list_tickets.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
    }

    private void setVisibilityNoData(){
        empty_view.setVisibility(View.VISIBLE);
        empty_view.setText(getResources().getString(R.string.no_data_available));
        list_tickets.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
    }

    private void setVisibilityDataAvailable(){
        empty_view.setVisibility(View.GONE);
        list_tickets.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }

    private void setVisibilityLoading(){
        empty_view.setVisibility(View.GONE);
        list_tickets.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.action_refresh:
                setVisibilityLoading();
                new InternetCheck(internet -> {
                    if(internet) {
                        reloadDataandView();
                    } else {
                        setVisibilityNoInternet();
                    }
                });
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        outState.putParcelableArrayList("ticketList", ticketList);
//        outState.putParcelableArrayList("atmList", atmList);
//        Log.i(TAG, "SaveInstance");
//        super.onSaveInstanceState(outState);
//    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        Log.e(TAG, "Start state");
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        Log.e(TAG, "Stop state");
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        Log.e(TAG, "Destroy state");
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        Log.e(TAG, "Pause state");
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        Log.e(TAG, "Resume state");
//    }
}
