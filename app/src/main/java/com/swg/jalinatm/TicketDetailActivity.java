package com.swg.jalinatm;

import android.content.DialogInterface;
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

import com.swg.jalinatm.POJO.Ticket;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TicketDetailActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.ticket_summary)
    LinearLayout ticket_layout;
    @BindView(R.id.notes_layout)
    LinearLayout notes_layout;
    @BindView(R.id.location_summary)
    LinearLayout location_layout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tv_ticket)
    TextView tv_ticket;
    @BindView(R.id.tv_atmid)
    TextView tv_atmid;
    @BindView(R.id.tv_problem)
    TextView tv_problem;
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

    private final static String TAG = "TicketDetailActivity";

    private AlertDialog alertDialog;

    private Ticket ticket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_detail);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        btn_accept.setOnClickListener(this);
        btn_finish.setOnClickListener(this);
        btn_update_location.setOnClickListener(this);

        alertDialog = new AlertDialog.Builder(TicketDetailActivity.this).create();

        ticket_layout.setBackground(getDrawable(R.drawable.rounded_layout));
        notes_layout.setBackground(getDrawable(R.drawable.rounded_layout));
        location_layout.setBackground(getDrawable(R.drawable.rounded_layout));

        ticket = (Ticket) Parcels.unwrap(getIntent().getParcelableExtra("ticket"));
        if(ticket != null){
            tv_ticket.setText(ticket.getTicketNumber());
            tv_atmid.setText(ticket.getMachineNumber());
            tv_problem.setText(ticket.getDescription());
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
                btn_accept.setVisibility(View.VISIBLE);
                btn_finish.setVisibility(View.VISIBLE);
            }
        } else {
            finish();
        }
    }

    private void setAlertDialog(String title, String body){
        alertDialog.setTitle(title);
        alertDialog.setMessage(body);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getResources().getString(R.string.alert_yes_button),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
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
        switch (v.getId()){
            case R.id.btn_accept:
                Log.e(TAG, "btn_accept pressed");
                setAlertDialog(getResources().getString(R.string.accept_alert_title), getResources().getString(R.string.accept_alert_body));
                break;
            case R.id.btn_finish:
                setAlertDialog(getResources().getString(R.string.finish_alert_title), getResources().getString(R.string.finish_alert_body));
                break;
            case R.id.btn_update_location:
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.e(TAG, "option item pressed");
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}