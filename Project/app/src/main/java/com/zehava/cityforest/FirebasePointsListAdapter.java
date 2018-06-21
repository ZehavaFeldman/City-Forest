package com.zehava.cityforest;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.mapbox.services.commons.models.Position;
import com.zehava.cityforest.Models.PointOfInterest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;

/**
 * Created by avigail on 10/04/18.
 */

public class FirebasePointsListAdapter extends FirebaseListAdapter<PointOfInterest> {

    private HashMap<String,String> selectedIndex;
    public FirebasePointsListAdapter(FirebaseListOptions<PointOfInterest> options) {
        super(options);
        selectedIndex = new HashMap<>();
    }

    public void setSelectedIndex(String ind, String val)
    {
        if(selectedIndex.containsKey(ind))
           selectedIndex.remove(ind);
        else
            selectedIndex.put(ind,val);

        notifyDataSetChanged();
    }

    public void setSelectedIndex(Map<String,String> indexes)
    {

        Iterator it = indexes.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            String v = (String) pair.getKey();

            selectedIndex.put(v, v);
        }

        notifyDataSetChanged();
    }

    @Override
    protected void populateView(final View v, PointOfInterest point, int position) {

        RowViewHolder holder = new RowViewHolder(v);
        v.setTag(holder);
        // Lookup view for data population

        holder.pointOfInterest = point;
        String track_name = holder.pointOfInterest.getTitle();
        if(track_name.equals(""))
            holder.pointName.setText(R.string.no_track_name);
        else holder.pointName.setText(track_name);

        Position pos = holder.pointOfInterest.getPosition();
        if(pos != null) {
            holder.lat.setText((float)pos.getLatitude()+","+(float)pos.getLongitude());
        }

        String pointkey = getMarkerHashKey(holder.pointOfInterest.getPosition().getLongitude());
        if(!selectedIndex.isEmpty() && selectedIndex.containsKey(pointkey))
        {
            holder.view.setBackgroundColor(Color.LTGRAY);
        }
        else
        {
           holder.view.setBackgroundColor(Color.TRANSPARENT);
        }

//        trackLevel.setText(track.getLevel());

        String pointdis = "" + holder.pointOfInterest.getSnippet();
        holder.pointDis.setText(pointdis);

    }

    private String getMarkerHashKey(final double key) {
        double longitude = key;
        //double latitude = chosenCoordinateLatLng.getLatitude();

        int hash = (int) (10000000*longitude);
        return "" + hash;
    }

    private static class RowViewHolder extends RecyclerView.ViewHolder {
//        implements View.OnClickListener{

        TextView pointName;
        TextView lat;
        TextView pointDis;
        PointOfInterest pointOfInterest;
        View view;
        public RowViewHolder(View v) {
            super(v);

            pointName = (TextView) v.findViewById(R.id.pointName);
            lat = (TextView) v.findViewById(R.id.lat);
            pointDis = (TextView) v.findViewById(R.id.pointDis);
            view = v;

        }
    }
}
