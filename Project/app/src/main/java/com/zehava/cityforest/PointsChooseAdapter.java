package com.zehava.cityforest;

/**
 * Created by avigail on 27/04/18.
 */

import android.content.Context;
import android.databinding.adapters.ImageViewBindingAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ImageView;

import com.zehava.cityforest.Managers.IconManager;

import java.util.List;

public class PointsChooseAdapter extends ArrayAdapter<String> {

    private Context context;
    private List<String> pointsList;

    public PointsChooseAdapter(Context context, List<String> pointsList) {

        super(context, R.layout.spinner_item, pointsList);

        this.context = context;
        this.pointsList = pointsList;
    }
    @Override
    public View getDropDownView(final int position, View convertView, ViewGroup parent) {
        // 1. Create inflater
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View row = inflater.inflate(R.layout.spinner_item, parent, false);
        TextView title = (TextView) row.findViewById(R.id.point_title);

        title.setText(pointsList.get(position));

        ImageView image = (ImageView) row.findViewById(R.id.point_ic);
        int resource = IconManager.getInstance().getResourceFrType(pointsList.get(position));
        if(resource != -1)
            image.setImageResource(resource);

        return  row;
    }
//
//
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View row = inflater.inflate(R.layout.spinner_item, parent, false);
        TextView title = (TextView) row.findViewById(R.id.point_title);
        ImageView image = (ImageView) row.findViewById(R.id.point_ic);
        int resource = IconManager.getInstance().getResourceFrType(pointsList.get(position));
        if(resource != -1)
            image.setImageResource(resource);
        title.setText(pointsList.get(position));

        return row;
    }

}

