package com.swg.jalinatm;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.swg.jalinatm.POJO.ATM;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ATMDetailActivity extends AppCompatActivity implements OnMapReadyCallback {

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
    @BindView(R.id.tv_atmdesc)
    TextView tv_atmadesc;
    @BindView(R.id.btn_update_location)
    Button btn_update_location;

    private final static String TAG = "ATMDetailActivity";

    private ATM atm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atmdetail);
        ButterKnife.bind(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        ticket_layout.setBackground(getDrawable(R.drawable.rounded_layout));
        location_layout.setBackground(getDrawable(R.drawable.rounded_layout));

        atm = (ATM) Parcels.unwrap(getIntent().getParcelableExtra("atm"));
        if(atm!=null) {
            Log.i(TAG, atm.getId());

            tv_atmadesc.setText(atm.getDescription());
            tv_atmid.setText(atm.getId());
            tv_address.setText(atm.getAddress());
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
}
