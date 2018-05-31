package com.swg.jalinatm.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.swg.jalinatm.POJO.Ticket;
import com.swg.jalinatm.R;

import java.util.ArrayList;

/**
 * Created by Andrew Widjaja on 5/25/2018.
 */

public class TicketListAdapter extends BaseAdapter {

    private Activity activity;
    private ArrayList<Ticket> data;
    private LayoutInflater inflater;

    public TicketListAdapter(Activity activity, ArrayList<Ticket> data) {
        this.activity = activity;
        this.data = data;
        this.inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView = inflater.inflate(R.layout.list_item_ticket, parent, false);
        }
        Ticket ticket = data.get(position);

        TextView ticket_title = (TextView) convertView.findViewById(R.id.ticket_list_title);
        TextView ticket_status = (TextView) convertView.findViewById(R.id.ticket_status);
        TextView ticket_desc = (TextView) convertView.findViewById(R.id.ticket_desc);

        String concatTitle = activity.getString(R.string.ticket_label) + " " + ticket.getTicketNumber();
        if(ticket.getTicketNumber() != null && !ticket.getTicketNumber().isEmpty()) ticket_title.setText(concatTitle);
        if(ticket.getTicketState() != null && !ticket.getTicketState().isEmpty()) {
            switch(ticket.getTicketState()){
                case "1":
                    ticket_status.setText(activity.getResources().getString(R.string.ongoing));
                    ticket_status.setTextColor(activity.getResources().getColor(R.color.md_orange_800));
                    break;
                case "2":
                    ticket_status.setText(activity.getResources().getString(R.string.finished));
                    ticket_status.setTextColor(activity.getResources().getColor(R.color.md_green_800));
                    break;
            }
        }
        else ticket_status.setText("");
        if(ticket.getSummary() != null && !ticket.getSummary().isEmpty()) ticket_desc.setText(ticket.getSummary());

        return convertView;
    }

    private void clearAll(){
        this.data = new ArrayList<>();
    }


}
