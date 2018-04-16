package com.zehava.cityforest;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListOptions;
import com.zehava.cityforest.Models.Track;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.Query;


public class FirebaseTrackListAdapter extends FirebaseListAdapter<Track> {


    public FirebaseTrackListAdapter(FirebaseListOptions options) {
        super(options);
    }

    @Override
    protected void populateView(View v, Track track, int position) {

        // Lookup view for data population
        TextView trackName = (TextView)v.findViewById(R.id.trackName);
        TextView startingPoint = (TextView)v.findViewById(R.id.startingPoint);
        TextView endingPoint = (TextView)v.findViewById(R.id.endingPoint);
        TextView trackLevel = (TextView)v.findViewById(R.id.trackLevel);
        TextView trackDistance = (TextView)v.findViewById(R.id.trackDistance);

        String track_name = track.getTrack_name();
        if(track_name.equals(""))
            trackName.setText(R.string.no_track_name);
        else trackName.setText(track.getTrack_name());

        startingPoint.setText(track.getStarting_point());
        endingPoint.setText(track.getEnding_point());
//        trackLevel.setText(track.getLevel());

        String distance = "" + track.getLength();
        trackDistance.setText(distance);
    }
}
