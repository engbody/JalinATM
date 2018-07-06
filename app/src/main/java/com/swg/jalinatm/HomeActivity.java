package com.swg.jalinatm;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.swg.jalinatm.Adapter.TicketListAdapter;
import com.swg.jalinatm.Adapter.TicketListFirebaseAdapter;
import com.swg.jalinatm.POJO.ATM;
import com.swg.jalinatm.POJO.Ticket;
import com.swg.jalinatm.POJO.Vendor;
import com.swg.jalinatm.POJO.VendorFirebase;
import com.swg.jalinatm.Utils.InternetCheck;
import com.swg.jalinatm.Utils.MyFirebaseMessagingService;
import com.swg.jalinatm.Utils.Preferences;
import com.swg.jalinatm.Utils.Tracker;

import org.parceler.Parcels;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity {

    @BindView(R.id.list_tickets)
    RecyclerView list_tickets;
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

    TicketListFirebaseAdapter ticketListAdapter;
    ArrayList<Ticket> ticketList;
    ArrayList<ATM> atmList;

    private Tracker tracker;
//    private Vendor vendor;
    private VendorFirebase vendorFirebase;
    private int triggerTouchMenu = 0;
    private DatabaseReference databaseEngineers, databaseTickets, databaseATM;
    private ChildEventListener ticketsListener;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private String userKey;
    private ArrayList<Long> atms;
    private Long atm_id;

    private final static String TAG = "HomeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "Create state");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        Log.i(TAG, "MAPS_API_KEY: " + getResources().getString(R.string.MAPS_API_KEY));

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_drawer_white);

        if(Preferences.getDetail(getApplicationContext(), "devicetoken") == null){
            setPrefDeviceToken();
        }

//        vendor = new Vendor("ID001", "Thomas", "BNI");

        new InternetCheck(internet -> {
            Log.e(TAG, String.valueOf(internet));
            if(internet) {
                tracker = new Tracker(this, vendorFirebase);
                checkUser();
                getUserId();
                if(!tracker.isGoogleApiConnected()) tracker.connectGoogleApi();
            } else {
                setVisibilityNoInternet();
            }
        });

        getIntentData();

        nav_view.setNavigationItemSelectedListener(item -> {
            item.setCheckable(false);
            if(triggerTouchMenu==0) {
                triggerTouchMenu = 1;
                switch (item.getItemId()) {
//                    case R.id.nav_update_location_atm:
//                        triggerTouchMenu = 0;
//                        Intent intent = new Intent(HomeActivity.this, ATMListActivity.class);
//                        Bundle bundle = new Bundle();
//                        bundle.putParcelable("vendor", Parcels.wrap(vendor));
//                        intent.putExtras(bundle);
//                        startActivity(intent);
//                        break;
                    case R.id.nav_log_out:
                        triggerTouchMenu = 0;
                        new InternetCheck(internet -> {
                            if(internet){
                                FirebaseAuth.getInstance().signOut();
                                Intent intent_login = new Intent(HomeActivity.this, LoginActivity.class);
                                startActivity(intent_login);
                                finish();
                            } else {
                                Toast.makeText(this, getResources().getString(R.string.no_internet_connection_toast), Toast.LENGTH_LONG).show();
                                Log.e(TAG, "no internet connection");
                            }
                        });
                        break;
                }
            } else {
                Toast.makeText(this, getResources().getString(R.string.please_wait), Toast.LENGTH_LONG).show();
                Log.e(TAG, "user touch more than once");
            }
            drawerLayout.closeDrawers();
            return true;
        });

    }

    private void setPrefDeviceToken(){
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(this, new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String deviceToken = instanceIdResult.getToken();
                Preferences.setDetail(getApplicationContext(), "devicetoken", deviceToken);
            }
        });
    }

    private void getUserId(){
        databaseEngineers = FirebaseDatabase.getInstance().getReference().child("engineers");
        databaseEngineers.orderByChild("email").equalTo(currentUser.getEmail()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot child: dataSnapshot.getChildren()) {
                    userKey = child.getKey();
                    Log.e(TAG, "userKey: " + userKey);
                    vendorFirebase = new VendorFirebase();
                    if(tracker!=null && userKey!=null){
                        tracker.setUserKey(userKey);
                        vendorFirebase.setKey(userKey);
                        String devicetoken = Preferences.getDetail(getApplicationContext(), "devicetoken");
                        databaseEngineers.child(userKey).child("notificationTokens").child(devicetoken).setValue(true);
                    }
                    ArrayList<Long> center = new ArrayList<Long>();
                    for(DataSnapshot grandChild: child.child("center").getChildren()){
                        center.add(grandChild.getValue(Long.class));
                    }
                    vendorFirebase.setName(child.child("name").getValue(String.class));
                    vendorFirebase.setEmail(child.child("email").getValue(String.class));
                    vendorFirebase.setStatus(child.child("status").getValue(Long.class));
                    vendorFirebase.setTimestamp(child.child("timestamp").getValue(Long.class));
                    vendorFirebase.setZoom_level(child.child("zoom_level").getValue(Long.class));
                    vendorFirebase.setCenter(center);
                    LinearLayoutManager manager = new LinearLayoutManager(HomeActivity.this);
                    DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(HomeActivity.this, manager.getOrientation());
                    ticketList = new ArrayList<Ticket>();
                    ticketListAdapter = new TicketListFirebaseAdapter(HomeActivity.this, ticketList, vendorFirebase);
                    list_tickets.setLayoutManager(manager);
                    list_tickets.addItemDecoration(dividerItemDecoration);
                    list_tickets.setAdapter(ticketListAdapter);
                    reloadDataandView();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "loadKey:onCancelled", databaseError.toException());
            }
        });
    }

    private void checkUser(){
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        if(currentUser==null){
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void addTicketsListener(){
        ticketsListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.exists()){
                    Log.e(TAG, "snapshotAdded: " + dataSnapshot.toString());

                    Ticket ticket = new Ticket(dataSnapshot.getKey(), (Long) dataSnapshot.child("code").getValue(), (String) dataSnapshot.child("incidentName").getValue(), (String) dataSnapshot.child("description").getValue(), (Long) dataSnapshot.child("atm_id").getValue(), (Long) dataSnapshot.child("status").getValue(), (Long) dataSnapshot.child("reportedTime").getValue());
                    if(ticket.getStatus()==11L || ticket.getStatus()==2L) {
                        if (ticketListAdapter.isItemExist(dataSnapshot.getKey())) {
                            ticketListAdapter.spillAdapter(dataSnapshot.getKey());
                        }
                        ticketListAdapter.refillAdapter(ticket);
                    } else {
                        if (ticketListAdapter.isItemExist(dataSnapshot.getKey())) {
                            ticketListAdapter.spillAdapter(dataSnapshot.getKey());
                        }
                    }

                    if(ticketListAdapter.getItemCount()>0){
                        setVisibilityDataAvailable();
                    } else {
                        setVisibilityNoData();
                    }

//                        Log.e(TAG, "ticket: " + ticket.getErrorCode() + " " + ticket.getDescription() + " " + ticket.getAtm_id());
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.exists()){
                    Log.e(TAG, "snapshotChanged: " + dataSnapshot.toString());

                    Ticket ticket = new Ticket(dataSnapshot.getKey(), (Long) dataSnapshot.child("code").getValue(), (String) dataSnapshot.child("incidentName").getValue(), (String) dataSnapshot.child("description").getValue(), (Long) dataSnapshot.child("atm_id").getValue(), (Long) dataSnapshot.child("status").getValue(), (Long) dataSnapshot.child("reportedTime").getValue());
                    if(ticket.getStatus()==11L || ticket.getStatus()==2L) {
                        if (ticketListAdapter.isItemExist(dataSnapshot.getKey())) {
                            ticketListAdapter.spillAdapter(dataSnapshot.getKey());
                        }
                        ticketListAdapter.refillAdapter(ticket);
                    } else {
                        if (ticketListAdapter.isItemExist(dataSnapshot.getKey())) {
                            ticketListAdapter.spillAdapter(dataSnapshot.getKey());
                        }
                    }

                    if(ticketListAdapter.getItemCount()>0){
                        setVisibilityDataAvailable();
                    } else {
                        setVisibilityNoData();
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                ticketListAdapter.spillAdapter(dataSnapshot.getKey());
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "loadKey:onCancelled", databaseError.toException());
            }
        };
    }

    private void reloadDataandView(){
        Log.e(TAG, "masuk reloadDataandView");
        databaseTickets = FirebaseDatabase.getInstance().getReference().child("tickets");
        databaseATM = FirebaseDatabase.getInstance().getReference().child("ATMs");
        addTicketsListener();
        Log.e(TAG, "vendorKey: " + vendorFirebase.getKey());
        databaseATM.orderByChild("engineer_id").equalTo(Long.parseLong(vendorFirebase.getKey())).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot child: dataSnapshot.getChildren()){
                    Log.e(TAG, child.getKey());
                    databaseTickets.orderByChild("atm_id").equalTo(Long.parseLong(child.getKey())).addChildEventListener(ticketsListener);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "loadKey:onCancelled", databaseError.toException());
            }
        });

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (ticketListAdapter.getItemCount() == 0) {
                setVisibilityNoData();
            } else {
                setVisibilityDataAvailable();
            }
        }, 2000);
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
                if(triggerTouchMenu==0) {
                    triggerTouchMenu=1;
                    setVisibilityLoading();
                    new InternetCheck(internet -> {
                        if (internet) {
                            currentUser = mAuth.getCurrentUser();
                            if(currentUser==null){
                                Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                databaseTickets.removeEventListener(ticketsListener);
                                ticketListAdapter.tickets = new ArrayList<Ticket>();
                                reloadDataandView();
                            }
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
        }
        return super.onOptionsItemSelected(item);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==1){
            if(ticketListAdapter.getItemCount()<1){
                setVisibilityNoData();
            }
        }
    }
}
