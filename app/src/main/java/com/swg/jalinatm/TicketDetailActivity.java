package com.swg.jalinatm;

import android.content.DialogInterface;
import android.content.Intent;
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

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.swg.jalinatm.POJO.ATM;
import com.swg.jalinatm.POJO.Ticket;
import com.swg.jalinatm.POJO.Vendor;
import com.swg.jalinatm.Utils.InternetCheck;

import org.parceler.Parcels;

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
    @BindView(R.id.tv_summary)
    TextView tv_summary;
    @BindView(R.id.btn_accept)
    Button btn_accept;
    @BindView(R.id.btn_finish)
    Button btn_finish;
    @BindView(R.id.btn_update_location)
    Button btn_update_location;
    @BindView(R.id.gap)
    View gap;
    @BindView(R.id.layout_accept_finish)
    LinearLayout layout_accept_finish;
    @BindView(R.id.root_layout)
    LinearLayout root_layout;

    private final static String TAG = "TicketDetailActivity";

    private AlertDialog alertDialog;

    private Ticket ticket;
    private ATM atm;
    private Vendor vendor;
    private int triggerButton = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_detail);
        ButterKnife.bind(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        btn_accept.setOnClickListener(this);
        btn_finish.setOnClickListener(this);
        btn_update_location.setOnClickListener(this);
        mapFragment.getMapAsync(this);

        alertDialog = new AlertDialog.Builder(TicketDetailActivity.this).create();

        ticket_layout.setBackground(getDrawable(R.drawable.rounded_layout));
//        notes_layout.setBackground(getDrawable(R.drawable.rounded_layout));
        location_layout.setBackground(getDrawable(R.drawable.rounded_layout));

        vendor = (Vendor) Parcels.unwrap(getIntent().getParcelableExtra("vendor"));
        if(vendor==null){
            Log.e(TAG, "error vendor null");
            finish();
        }
        atm = (ATM) Parcels.unwrap(getIntent().getParcelableExtra("atm"));
        if(atm==null){
            Log.e(TAG, "error atm null");
            finish();
        }
        ticket = (Ticket) Parcels.unwrap(getIntent().getParcelableExtra("ticket"));
        if(ticket != null){
            tv_ticket.setText(ticket.getTicketNumber());
            tv_atmid.setText(ticket.getMachineNumber());
            tv_problem.setText(ticket.getDescription());
            tv_summary.setText(ticket.getSummary());
            if(ticket.getTicketState() != null) {
                if (ticket.getTicketState().equals("1")) { //accepted
                    gap.setVisibility(View.GONE);
                    btn_accept.setVisibility(View.GONE);
                    layout_accept_finish.setGravity(Gravity.CENTER);
                } else {
                    layout_accept_finish.setVisibility(View.GONE);
                }
            } else {
                layout_accept_finish.setVisibility(View.VISIBLE);
                layout_accept_finish.setGravity(Gravity.CENTER);
                btn_accept.setVisibility(View.VISIBLE);
                btn_finish.setVisibility(View.GONE);
                gap.setVisibility(View.GONE);
            }
        } else {
            Log.e(TAG, "error ticket null");
            finish();
        }
    }

    private void setAlertDialog(String title, String body){
        alertDialog.setTitle(title);
        alertDialog.setMessage(body);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getResources().getString(R.string.alert_yes_button),
                (dialog, which) -> {
                    dialog.dismiss();
                    setResult(1);
                    finish();
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getResources().getString(R.string.alert_no_button),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    @Override
    public void onClick(View v) {
        if(triggerButton==0) {
            triggerButton=1;
            new InternetCheck(internet -> {
                if (internet) {
                    switch (v.getId()) {
                        case R.id.btn_accept:
                            triggerButton=0;
                            setAlertDialog(getResources().getString(R.string.accept_alert_title), getResources().getString(R.string.accept_alert_body));
                            break;
                        case R.id.btn_finish:
                            triggerButton=0;
                            Intent intent = new Intent(TicketDetailActivity.this, FeedbackActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putParcelable("vendor", Parcels.wrap(vendor));
                            intent.putExtras(bundle);
                            startActivityForResult(intent, 0);
                            break;
                        case R.id.btn_update_location:
                            triggerButton=0;
                            break;
                    }
                } else {
                    triggerButton=0;
                    Log.e(TAG, getResources().getString(R.string.no_internet_connection));
                    Toast.makeText(this, getResources().getString(R.string.no_internet_connection_toast), Toast.LENGTH_SHORT).show();
                }
            });
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==1){
            setResult(1);
            finish();
        }
    }
}
