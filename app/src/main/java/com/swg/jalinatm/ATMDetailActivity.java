package com.swg.jalinatm;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import com.swg.jalinatm.POJO.ATM;
import com.swg.jalinatm.POJO.Vendor;
import com.swg.jalinatm.Utils.CurrencyFormatter;
import com.swg.jalinatm.Utils.InternetCheck;
import com.swg.jalinatm.Utils.Tracker;

import org.parceler.Parcels;

import java.text.NumberFormat;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ATMDetailActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener {

    @BindView(R.id.ticket_summary)
    LinearLayout ticket_layout;
    @BindView(R.id.location_summary)
    LinearLayout location_layout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tv_atmid)
    TextView tv_atmid;
    @BindView(R.id.tv_address)
    TextView tv_address;
    @BindView(R.id.tv_atmbalance)
    TextView tv_atmbalance;
    @BindView(R.id.tv_atmdesc)
    TextView tv_atmadesc;
    @BindView(R.id.btn_update_location)
    Button btn_update_location;
    @BindView(R.id.btn_open_location)
    Button btn_open_location;
    @BindView(R.id.root_layout)
    LinearLayout root_layout;

    private final static String TAG = "ATMDetailActivity";
    private final static int PLACE_PICKER_REQUEST = 5;

    private ATM atm;
    private Vendor vendor;
    private Tracker tracker;
    private int triggerTouchButton = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atmdetail);
        ButterKnife.bind(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        btn_update_location.setOnClickListener(this);
        btn_open_location.setOnClickListener(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        ticket_layout.setBackground(getDrawable(R.drawable.rounded_layout));
        location_layout.setBackground(getDrawable(R.drawable.rounded_layout));

        atm = (ATM) Parcels.unwrap(getIntent().getParcelableExtra("atm"));
        vendor = (Vendor) Parcels.unwrap(getIntent().getParcelableExtra("vendor"));
        if(atm!=null || vendor!=null) {
            Log.i(TAG, atm.getId());

            tv_atmadesc.setText(atm.getDescription());
            tv_atmid.setText(atm.getId());
            tv_address.setText(atm.getAddress());
            tv_atmbalance.setText(NumberFormat.getNumberInstance(getResources().getConfiguration().locale).format(10000));
            tracker = new Tracker(this, vendor);
            if(!tracker.isGoogleApiConnected()) tracker.connectGoogleApi();
        } else {
            Log.e(TAG, "error atm or vendor null");
            finish();
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
        if(atm != null){
            if(atm.getLoc()!= null){
                LatLng markerLoc = new LatLng(atm.getLoc().latitude, atm.getLoc().longitude);
                CameraPosition position = new CameraPosition.Builder().target(markerLoc).zoom(17).build();
                googleMap.addMarker(new MarkerOptions().position(markerLoc));
                googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(position));
                if(atm.getAddress()!=null && !atm.getAddress().equals("")) tv_address.setText(atm.getAddress());
                else tv_address.setText(getResources().getString(R.string.unknown_place));
            }
        }
    }

    @Override
    public void onClick(View v) {
        if(triggerTouchButton==0){
            triggerTouchButton=1;
            switch (v.getId()) {
                case R.id.btn_update_location:
                    new InternetCheck(internet -> {
                        if(internet) {
                            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                            try {
                                startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
                            } catch (GooglePlayServicesRepairableException e) {
                                e.printStackTrace();
                                Log.e(TAG, getResources().getString(R.string.failed_to_open_place) + " cause" + e.getCause());
                                Toast.makeText(this, getResources().getString(R.string.failed_to_open_place), Toast.LENGTH_LONG).show();
                            } catch (GooglePlayServicesNotAvailableException e) {
                                e.printStackTrace();
                                Log.e(TAG, getResources().getString(R.string.failed_to_open_place) + " cause" + e.getCause());
                                Toast.makeText(this, getResources().getString(R.string.failed_to_open_place), Toast.LENGTH_LONG).show();
                            }
                            triggerTouchButton = 0;
                        } else {
                            triggerTouchButton=0;
                            Log.e(TAG, getResources().getString(R.string.no_internet_connection));
                            Toast.makeText(this, getResources().getString(R.string.no_internet_connection_toast), Toast.LENGTH_SHORT).show();
                        }
                    });
                case R.id.btn_open_location:
                    triggerTouchButton = 0;
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);
                String toastMsg = String.format("Address: %s", place.getAddress() + " Vendor Location Latitude: " + tracker.getVendor().getLoc().latitude);
                Log.e(TAG, "device location: " + tracker.getVendor().getLoc().latitude + " " + tracker.getVendor().getLoc().longitude);
                Log.e(TAG, "place location: " + place.getLatLng().latitude + " " + place.getLatLng().longitude);
                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
                setResult(1);
                finish();
            }
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
