package com.zehava.cityforest;

import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.mapbox.services.commons.models.Position;
import com.zehava.cityforest.Models.PointOfInterest;

/**
 * Created by avigail on 10/04/18.
 */

public class FirebasePointsListAdapter extends FirebaseListAdapter<PointOfInterest> {


    public FirebasePointsListAdapter(FirebaseListOptions<PointOfInterest> options) {
        super(options);
    }

    @Override
    protected void populateView(View v, PointOfInterest point, int position) {

        // Lookup view for data population
        TextView pointName = (TextView)v.findViewById(R.id.pointName);
        TextView lat = (TextView)v.findViewById(R.id.lat);
        TextView pointDis = (TextView)v.findViewById(R.id.pointDis);

        String track_name = point.getTitle();
        if(track_name.equals(""))
            pointName.setText(R.string.no_track_name);
        else pointName.setText(track_name);

        Position pos = point.getPosition();
        if(pos != null) {
            lat.setText((float)point.getPosition().getLatitude()+","+(float)point.getPosition().getLongitude());
        }
//        trackLevel.setText(track.getLevel());

        String pointdis = "" + point.getSnippet();
        pointDis.setText(pointdis);


    }
}
