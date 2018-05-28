package com.swg.jalinatm.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.swg.jalinatm.POJO.ATM;
import com.swg.jalinatm.R;

import java.util.ArrayList;

/**
 * Created by Andrew Widjaja on 5/28/2018.
 */

public class ATMListAdapter extends BaseAdapter{

    private Activity activity;
    private ArrayList<ATM> data;
    private LayoutInflater inflater;

    public ATMListAdapter(Activity activity, ArrayList<ATM> data) {
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
            convertView = inflater.inflate(R.layout.list_item_atm, parent, false);
        }

        ATM atm = data.get(position);

        TextView atm_title = (TextView) convertView.findViewById(R.id.atm_list_title);
        TextView atm_address = (TextView) convertView.findViewById(R.id.atm_address);

        if(atm.getId()!=null || !atm.getId().isEmpty()) atm_title.setText(atm.getId());
        if(atm.getAddress()!=null || !atm.getId().isEmpty()) atm_address.setText(atm.getAddress());
        else atm_address.setText("");

        return convertView;
    }
}
