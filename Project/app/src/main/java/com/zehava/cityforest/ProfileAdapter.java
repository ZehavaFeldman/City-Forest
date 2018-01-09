package com.zehava.cityforest;

/**
 * Created by avigail on 04/01/18.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class ProfileAdapter extends ArrayAdapter<String> {

    Context context;
    List<String> actionlist;
    int selectedIndex;


    public ProfileAdapter(Context context, List<String> actionslist) {

        super(context, android.R.layout.simple_spinner_item, actionslist);

        this.context = context;
        this.actionlist = actionslist;
    }

    public void setSelectedIndex(int ind)
    {
        selectedIndex = ind;
        notifyDataSetChanged();
    }

    @Override
    public View getDropDownView(final int position, View convertView, ViewGroup parent) {
        // 1. Create inflater
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View row = inflater.inflate(R.layout.spinner_item, parent, false);
        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });

        return  row;
    }



}

