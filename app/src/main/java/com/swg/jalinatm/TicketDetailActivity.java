package com.swg.jalinatm;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.swg.jalinatm.POJO.ATM;
import com.swg.jalinatm.POJO.Ticket;
import com.swg.jalinatm.POJO.Vendor;
import com.swg.jalinatm.POJO.VendorFirebase;
import com.swg.jalinatm.Utils.InternetCheck;
import com.swg.jalinatm.Utils.Tracker;

import org.parceler.Parcels;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TicketDetailActivity extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback {

    @BindView(R.id.ticket_summary)
    LinearLayout ticket_layout;
//    @BindView(R.id.notes_layout)
//    LinearLayout notes_layout;
    @BindView(R.id.location_summary)
    LinearLayout location_layout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tv_ticket)
    TextView tv_ticket;
    @BindView(R.id.tv_atmid)
    TextView tv_atmid;
    @BindView(R.id.tv_address)
    TextView tv_address;
    @BindView(R.id.tv_problem)
    TextView tv_problem;
    @BindView(R.id.tv_code)
    TextView tv_code;
    @BindView(R.id.btn_accept_giveup)
    Button btn_accept_giveup;
    @BindView(R.id.btn_reject_finish)
    Button btn_reject_finish;
//    @BindView(R.id.btn_update_location)
//    Button btn_update_location;
    @BindView(R.id.btn_open_location)
    Button btn_open_location;
    @BindView(R.id.gap)
    View gap;
    @BindView(R.id.layout_accept_reject_finish)
    LinearLayout layout_accept_reject_finish;
    @BindView(R.id.root_layout)
    LinearLayout root_layout;

    private final static String TAG = "TicketDetailActivity";
    private final static int PLACE_PICKER_REQUEST = 5;

    private AlertDialog alertDialog;

    private Ticket ticket;
    private String ticketid;
    private ATM atm;
    private Long atmid;
    private Double atmLatitude = 0d;
    private Double atmLongitude = 0d;
    private VendorFirebase vendor;
    private String vendorKey;
    private Tracker tracker;
    private int triggerButton = 0;
    private int reject = 0;
    private int accept = 0;
    private GoogleMap googleMap;

    private ChildEventListener ticketListener;
    private ChildEventListener atmListener;
    private DatabaseReference databaseEngineers, databaseTickets, databaseATM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_detail);
        ButterKnife.bind(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        btn_accept_giveup.setOnClickListener(this);
        btn_reject_finish.setOnClickListener(this);
//        btn_update_location.setOnClickListener(this);
        btn_open_location.setOnClickListener(this);
        mapFragment.getMapAsync(this);

        alertDialog = new AlertDialog.Builder(TicketDetailActivity.this).create();

        ticket_layout.setBackground(getDrawable(R.drawable.rounded_layout));
//        notes_layout.setBackground(getDrawable(R.drawable.rounded_layout));
        location_layout.setBackground(getDrawable(R.drawable.rounded_layout));

//        vendor = (VendorFirebase) Parcels.unwrap(getIntent().getParcelableExtra("vendor"));
        vendorKey = (String) getIntent().getStringExtra("vendorkey");
        if(vendorKey==null || vendorKey.isEmpty()){
            Log.e(TAG, "error vendor null");
            finish();
        }
        atmid = (Long) getIntent().getLongExtra("atmid", 0L);
        if(atmid==0L){
            Log.e(TAG, "error atm null");
            finish();
        }
        int ticketcheckint = (int) getIntent().getIntExtra("ticketcheck", -1);
        ticket = (Ticket) Parcels.unwrap(getIntent().getParcelableExtra("ticket"));
        ticketid = (String) getIntent().getStringExtra("ticketid");

        databaseEngineers = FirebaseDatabase.getInstance().getReference().child("engineers");
        databaseTickets = FirebaseDatabase.getInstance().getReference().child("tickets");
        databaseATM = FirebaseDatabase.getInstance().getReference().child("ATMs");

        if(ticketid != null){
            tv_ticket.setText(ticket.getTicketNumber());
            tv_atmid.setText(ticket.getAtm_id().toString());
            tv_problem.setText(ticket.getDescription());
            tv_code.setText(ticket.getErrorCode().toString());
            if(ticket.getStatus() != 0L) {
                if (ticket.getStatus() == 2L) { //ongoing
                    btn_accept_giveup.setText(getResources().getString(R.string.giveup));
                    btn_reject_finish.setText(getResources().getString(R.string.finish));
                    reject=0;
                    accept=0;
                } else {
                    if(ticketcheckint==1){
                        btn_accept_giveup.setVisibility(View.GONE);
                        gap.setVisibility(View.GONE);
                        layout_accept_reject_finish.setGravity(Gravity.CENTER);
                        btn_reject_finish.setText(getResources().getString(R.string.reject));
                        reject=1;
                    } else {
                        btn_accept_giveup.setText(getResources().getString(R.string.accept));
                        btn_reject_finish.setText(getResources().getString(R.string.reject));
                        reject=1;
                        accept=1;
                    }
                }
            } else {
                layout_accept_reject_finish.setVisibility(View.GONE);
            }
            tracker = new Tracker(this, vendor);
            if(!tracker.isGoogleApiConnected()) tracker.connectGoogleApi();
        } else {
            Log.e(TAG, "error ticket null");
            setResult(1);
            finish();
        }
    }



    private void addATMListener(){
        databaseATM.child(atmid.toString()).child("position").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.e(TAG, dataSnapshot.toString());
                ArrayList<Double> atmLocation = new ArrayList<Double>();
                for (DataSnapshot childSbapshot : dataSnapshot.getChildren()){
                    atmLocation.add(childSbapshot.getValue(Double.class));
                }
                atmLatitude = atmLocation.get(0);
                atmLongitude = atmLocation.get(1);

                if(googleMap!=null) {
                    LatLng markerLoc = new LatLng(atmLatitude, atmLongitude);
                    CameraPosition position = new CameraPosition.Builder().target(markerLoc).zoom(17).build();
                    googleMap.addMarker(new MarkerOptions().position(markerLoc));
                    googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(position));

                    Geocoder geocoder = new Geocoder(getApplicationContext(), getResources().getConfiguration().locale);
                    String addressLine = "";
                    try {
                        Log.e(TAG, atmLatitude + " " + atmLongitude);
                        List<Address> addresses = geocoder.getFromLocation(atmLatitude, atmLongitude, 1);
                        Address address = addresses.get(0);
                        addressLine = address.getAddressLine(0);

                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e(TAG, e.getMessage());
                    }

                    if (addressLine != null && !addressLine.equals(""))
                        tv_address.setText(addressLine);
                    else tv_address.setText(getResources().getString(R.string.unknown_place));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void addTicketListener(){
        ticketListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
    }

    private void setAlertDialog(String title, String body, int flag){
        alertDialog.setTitle(title);
        alertDialog.setMessage(body);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getResources().getString(R.string.alert_yes_button),
                (dialog, which) -> {
                    dialog.dismiss();

                    if(flag==1){
                        databaseTickets.child(ticketid).child("status").setValue(2);
                    } else if(flag==2){
                        databaseTickets.child(ticketid).child("status").setValue(15);
                    } else {
                        databaseTickets.child(ticketid).child("status").setValue(4);
                    }

                    databaseTickets.child(ticketid).child("lastUpdatedBy").setValue(vendorKey);
                    databaseTickets.child(ticketid).child("lastUpdatedTime").setValue(System.currentTimeMillis());

                    setResult(1);
                    finish();
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getResources().getString(R.string.alert_no_button),
                (dialog, which) -> dialog.dismiss());
        alertDialog.show();
    }

    @Override
    public void onClick(View v) {
        if(triggerButton==0) {
            triggerButton=1;
            switch (v.getId()) {
                case R.id.btn_accept_giveup:
                    triggerButton=0;
                    if(accept==1) {
                        setAlertDialog(getResources().getString(R.string.accept_alert_title), getResources().getString(R.string.accept_alert_body), 1);
                    } else {
                        setAlertDialog(getResources().getString(R.string.giveup_alert_ticket), getResources().getString(R.string.giveup_alert_body), 2);
                    }
                    break;
                case R.id.btn_reject_finish:
                    triggerButton=0;
                    if(reject==0) {
                        setAlertDialog(getResources().getString(R.string.finish_alert_title), getResources().getString(R.string.finish_alert_body), 3);
                    } else {
                        setAlertDialog(getResources().getString(R.string.reject_alert_title), getResources().getString(R.string.reject_alert_body), 2);
                    }
                    break;
//                case R.id.btn_update_location:
//                    new InternetCheck(internet -> {
//                        if (internet) {
//                            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
//                            try {
//                                startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
//                            } catch (GooglePlayServicesRepairableException e) {
//                                e.printStackTrace();
//                                Log.e(TAG, getResources().getString(R.string.failed_to_open_place) + " cause" + e.getCause());
//                                Toast.makeText(this, getResources().getString(R.string.failed_to_open_place), Toast.LENGTH_LONG).show();
//                            } catch (GooglePlayServicesNotAvailableException e) {
//                                e.printStackTrace();
//                                Log.e(TAG, getResources().getString(R.string.failed_to_open_place) + " cause" + e.getCause());
//                                Toast.makeText(this, getResources().getString(R.string.failed_to_open_place), Toast.LENGTH_LONG).show();
//                            }
//                            triggerButton=0;
//                        } else {
//                            triggerButton=0;
//                            Log.e(TAG, getResources().getString(R.string.no_internet_connection));
//                            Toast.makeText(this, getResources().getString(R.string.no_internet_connection_toast), Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                    break;
                case R.id.btn_open_location:
                    triggerButton=0;
                    String uri = String.format(Locale.US, "geo:%f,%f?q=%f,%f", atm.getLoc().latitude, atm.getLoc().longitude, atm.getLoc().latitude, atm.getLoc().longitude);
                    Log.e(TAG, uri);
                    Intent intentMaps = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                    startActivity(intentMaps);
                    break;
            }
        } else {
            Toast.makeText(this, getResources().getString(R.string.please_wait), Toast.LENGTH_LONG).show();
            Log.e(TAG, "user touch more than once");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i(TAG, "option item pressed");
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        addATMListener();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==1){
            setResult(1);
            finish();
        }
//        else if (requestCode == PLACE_PICKER_REQUEST) {
//            if (resultCode == RESULT_OK) {
//                Place place = PlacePicker.getPlace(this, data);
//                String toastMsg = String.format("Address: %s", place.getAddress() + " Vendor Location Latitude: " + tracker.getVendor().getLoc().latitude);
//                Log.e(TAG, "device location: " + tracker.getVendor().getLoc().latitude + " " + tracker.getVendor().getLoc().longitude);
//                Log.e(TAG, "place location: " + place.getLatLng().latitude + " " + place.getLatLng().longitude);
//                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
//                setResult(1);
//                finish();
//            }
//        }
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
