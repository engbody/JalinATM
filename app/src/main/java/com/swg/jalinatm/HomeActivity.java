package com.swg.jalinatm;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.Intent;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.swg.jalinatm.Adapter.TicketListAdapter;
import com.swg.jalinatm.POJO.ATM;
import com.swg.jalinatm.POJO.DataWrapper;
import com.swg.jalinatm.POJO.Ticket;
import com.swg.jalinatm.Utils.Preferences;

import org.parceler.Parcels;

import java.util.ArrayList;

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
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.nav_view)
    NavigationView nav_view;

    TicketListAdapter ticketListAdapter;
    ArrayList<Ticket> ticketList;
    ArrayList<ATM> atmList;

    private final static String TAG = "HomeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e(TAG, "Create state");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_drawer_white);

        ticketList = new ArrayList<Ticket>();
        atmList = new ArrayList<ATM>();

        if(Preferences.checkDetail(getApplicationContext(), "ticketList")){

        } else {
            if(savedInstanceState == null || !savedInstanceState.containsKey("ticketList")) {
                Log.e(TAG, "create ticketList");
                ticketList = new ArrayList<Ticket>();
                ticketList.add(new Ticket("#1", "Cash Handler Fatal Error", "ATM tidak mengeluarkan uang, dan tidak respond", "M0001", null));
                ticketList.add(new Ticket("#2", "Outstanding Down", "ATM tidak ada koneksi", "M0002", "1")); //accepted
                ticketList.add(new Ticket("#3", "Cash Out", "Uang di ATM habis", "M0003", "2")); //finished
            } else {
                Log.e(TAG, "getTicketList from savedinstance");
                ticketList = savedInstanceState.getParcelableArrayList("ticketList");
//                Preferences.setDetail(getApplicationContext(), ticketList);
            }
        }

        if(savedInstanceState == null || !savedInstanceState.containsKey("atmList")){
            Log.e(TAG, "create atmList");
            atmList.add(new ATM("ATM1", "ATM area Jakarta Pusat", "Grand Indonesia"));
            atmList.add(new ATM("ATM2", "ATM area Jakarta Selatan", "Epicentrum"));
            atmList.add(new ATM("ATM3", "ATM area Jakarta Timur", "Kelapa Gading Mall"));
            atmList.add(new ATM("ATM4", "ATM area Jakarta Barat", "Binus"));
        } else {
            Log.e(TAG, "getAtmList from savedinstance");
            atmList = savedInstanceState.getParcelableArrayList("atmList");
        }

        ticketListAdapter = new TicketListAdapter(this, ticketList);

        ticketListAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                Log.e(TAG, "datasetchanged");
                if (ticketListAdapter.getCount() == 0) {
                    empty_view.setVisibility(View.VISIBLE);
                    list_tickets.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                } else {
                    empty_view.setVisibility(View.GONE);
                    list_tickets.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

        list_tickets.setAdapter(ticketListAdapter);

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if (ticketListAdapter.getCount() == 0) {
                    empty_view.setVisibility(View.VISIBLE);
                    list_tickets.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                } else {
                    empty_view.setVisibility(View.GONE);
                    list_tickets.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }
            }
        }, 1000);

        list_tickets.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Ticket ticket = (Ticket) parent.getItemAtPosition(position);
                Log.e(TAG, "Ticket no: " + ticket.getTicketNumber());

                Intent intent = new Intent(HomeActivity.this, TicketDetailActivity.class);
                intent.putExtra("ticket", Parcels.wrap(ticket));
                startActivity(intent);
            }
        });

        getIntentData();

        nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                item.setCheckable(false);
                switch(item.getItemId()){
                    case R.id.nav_update_location_atm:
                        Intent intent = new Intent(HomeActivity.this, ATMListActivity.class);
                        intent.putExtra("atmlist", new DataWrapper(atmList));
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
            }
        });
    }

    private void getIntentData(){
        // ATTENTION: This was auto-generated to handle app links.
        Intent appLinkIntent = getIntent();
        String appLinkAction = appLinkIntent.getAction();
        Uri appLinkData = appLinkIntent.getData();
        if(appLinkData!=null) {
            Log.e(TAG, appLinkData.toString());
            Log.e(TAG, appLinkData.getQueryParameter("key"));
            if(appLinkData.getQueryParameter("key")!=null){
                //do something
            }
        }
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
                ticketList.add(new Ticket("#4", "Receipt Printer Paper Out", "Kertas habis", "M0004", "2")); //finished
                empty_view.setVisibility(View.GONE);
                list_tickets.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                ticketListAdapter.notifyDataSetChanged();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("ticketList", ticketList);
        outState.putParcelableArrayList("atmList", atmList);
        Log.e(TAG, "SaveInstance");
        super.onSaveInstanceState(outState);
    }
}
