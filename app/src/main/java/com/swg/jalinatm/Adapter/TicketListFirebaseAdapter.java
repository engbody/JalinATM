package com.swg.jalinatm.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.swg.jalinatm.LoginActivity;
import com.swg.jalinatm.POJO.Ticket;
import com.swg.jalinatm.POJO.VendorFirebase;
import com.swg.jalinatm.R;
import com.swg.jalinatm.TicketDetailActivity;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by user on 7/4/2018.
 */

public class TicketListFirebaseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity activity;
    public ArrayList<Ticket> tickets;
    private VendorFirebase vendor;

    private static final int VIEW_TYPE = 0;

    public TicketListFirebaseAdapter(Activity activity, ArrayList<Ticket> tickets, VendorFirebase vendor){
        this.activity = activity;
        this.tickets = tickets;
        this.vendor = vendor;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(R.layout.list_item_ticket, parent, false);
        viewHolder =  new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        configureView(viewHolder, position);
    }

    public ArrayList<Ticket> getTickets() {
        return tickets;
    }

    public void setTickets(ArrayList<Ticket> tickets) {
        this.tickets = tickets;
    }

    private void configureView(ViewHolder viewHolder, int position){
        TextView ticket_title = (TextView) viewHolder.getTicket_title();
        TextView ticket_status = (TextView) viewHolder.getTicket_status();
        TextView ticket_desc = (TextView) viewHolder.getTicket_desc();

        Ticket ticket = tickets.get(position);

        if(ticket.getTicketNumber() != null) {
            String concatTitle = activity.getString(R.string.ticket_label) + " " + ticket.getTicketNumber();
            ticket_title.setText(concatTitle);
        }

        if(ticket.getStatus() != 0) {
            if(ticket.getStatus()==2L){
                ticket_status.setText(activity.getResources().getString(R.string.ongoing));
                ticket_status.setTextColor(activity.getResources().getColor(R.color.md_orange_800));
            } else if(ticket.getStatus()==4L){
                ticket_status.setText(activity.getResources().getString(R.string.finished));
                ticket_status.setTextColor(activity.getResources().getColor(R.color.md_green_800));
            } else {
                ticket_status.setText("");
            }
        }
        else ticket_status.setText("");
        if(ticket.getIncidentName() != null && !ticket.getIncidentName().isEmpty()) ticket_desc.setText(ticket.getIncidentName());
    }

    @Override
    public int getItemCount() {
        return this.tickets.size();
    }

    @Override
    public int getItemViewType(int position) {
        return VIEW_TYPE;
    }

    public void refillAdapter(Ticket ticket){
        this.tickets.add(ticket);
        Collections.sort(tickets, new Comparator<Ticket>() {
            @Override
            public int compare(Ticket o1, Ticket o2) {
                if(o1.getStatus().equals(o2.getStatus())){
                    return o1.getReportedTime().compareTo(o2.getReportedTime());
                } else {
                    return o1.getStatus().compareTo(o2.getStatus());
                }
            }
        });
        notifyDataSetChanged();
    }

    public void spillAdapter(String key){
        for(int x=0;x<tickets.size();x++){
            if(tickets.get(x).getTicketNumber().equals(key)){
                tickets.remove(x);
            }
        }
        notifyDataSetChanged();
    }

    public boolean isItemExist(String key){
        for(int x=0;x<tickets.size();x++){
            if(tickets.get(x).getTicketNumber().equals(key)){
                return true;
            }
        }
        return false;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.ticket_list_title)
        TextView ticket_title;
        @BindView(R.id.ticket_status)
        TextView ticket_status;
        @BindView(R.id.ticket_desc)
        TextView ticket_desc;

        private FirebaseAuth mAuth;
        private FirebaseUser currentUser;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        public TextView getTicket_title() {
            return ticket_title;
        }

        public void setTicket_title(TextView ticket_title) {
            this.ticket_title = ticket_title;
        }

        public TextView getTicket_status() {
            return ticket_status;
        }

        public void setTicket_status(TextView ticket_status) {
            this.ticket_status = ticket_status;
        }

        public TextView getTicket_desc() {
            return ticket_desc;
        }

        public void setTicket_desc(TextView ticket_desc) {
            this.ticket_desc = ticket_desc;
        }

        @Override
        public void onClick(View v) {
            mAuth = FirebaseAuth.getInstance();
            currentUser = mAuth.getCurrentUser();
            if(currentUser==null){
                Intent intent = new Intent(activity, LoginActivity.class);
                activity.startActivity(intent);
                activity.finish();
            } else {
                int checkworking = 0;
                for (int a = 0; a < tickets.size(); a++) {
                    if (tickets.get(a).getStatus() == 2L) checkworking = 1;
                }
                int itempos = getAdapterPosition();
                Intent intent = new Intent(activity, TicketDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("ticket", Parcels.wrap(tickets.get(itempos)));
//            bundle.putParcelable("vendor", Parcels.wrap(vendor));
                intent.putExtras(bundle);
                intent.putExtra("vendorkey", vendor.getKey());
                intent.putExtra("ticketid", tickets.get(itempos).getTicketNumber());
                intent.putExtra("atmid", tickets.get(itempos).getAtm_id());
                intent.putExtra("ticketcheck", checkworking);
                activity.startActivityForResult(intent, 0);
            }
        }
    }
}
