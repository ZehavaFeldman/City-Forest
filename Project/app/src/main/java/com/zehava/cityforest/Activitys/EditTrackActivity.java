package com.zehava.cityforest.Activitys;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Spinner;
import android.widget.Toast;

import com.mapbox.api.directions.v5.models.DirectionsWaypoint;
import com.zehava.cityforest.Models.Track;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zehava.cityforest.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

import static com.zehava.cityforest.Constants.PICK_POINTS_DONE;
import static com.zehava.cityforest.Constants.PICK_POINTS_REQUEST;
import static com.zehava.cityforest.Constants.TRACK_EDIT;
import static com.zehava.cityforest.Constants.TRACK_EDITED;

public class EditTrackActivity extends AppCompatActivity {

    private FirebaseDatabase database;
    private DatabaseReference tracks;
    private String track_db_key;
    private Track edited_track;

    private EditText track_name_field;
    private EditText starting_point;
    private EditText ending_point;
    private TextView duration_field;
    private TextView distance_field;
    private EditText additional_info;

    private Button update_button;
    private Button cancel_button;
    private Button addPoints;

    Map<String,String> tracksPointsOfInterest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_track);

        database = FirebaseDatabase.getInstance();
        tracks = database.getReference("tracks");

        Intent i = getIntent();
        track_db_key = i.getStringExtra(TRACK_EDIT);

        track_name_field = findViewById(R.id.trackNameField);
        starting_point = findViewById(R.id.startingPoint);
        ending_point = findViewById(R.id.endingPoint);
        duration_field = findViewById(R.id.durationField);
        distance_field = findViewById(R.id.distanceField);
        additional_info = findViewById(R.id.trackSummaryField);
        update_button = findViewById(R.id.updateButton);
        cancel_button = findViewById(R.id.cancelButton);

        update_button.setOnClickListener(new ClickListener());
        cancel_button.setOnClickListener(new ClickListener());

        tracksPointsOfInterest = new  HashMap<>();

        addPoints = findViewById(R.id.addPointsButton);
        addPoints.setOnClickListener(new ClickListener());


        initiateScreenValues();
    }

    private void initiateScreenValues() {
        tracks.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> tracksMap = (Map<String, Object>)dataSnapshot.getValue();
                Map<String, Object> track = (Map<String, Object>)tracksMap.get(track_db_key);

                track_name_field.setText((String)track.get("track_name"));
                duration_field.setText(track.get("duration").toString());
                distance_field.setText(track.get("length").toString());
                additional_info.setText((String)track.get("additional_info"));
                starting_point.setText((String)track.get("starting_point"));
                ending_point.setText((String)track.get("endinging_point"));
                additional_info.clearFocus();

                Map jArray = (HashMap<String,String>)track.get("points");
                if (jArray != null)
                    tracksPointsOfInterest.putAll(jArray);

                String userhash = (String) track.get("user_hashkey");
                userhash = userhash == null? "":userhash;
                edited_track = new Track((String)track.get("route"),
                        (List<String>) track.get("waypoints"),
                        track_db_key,
                        (String)track.get("track_name"),
                        (String)track.get("starting_point"),
                        (String)track.get("ending_point"),
                        Double.parseDouble(track.get("duration").toString()),
                        Double.parseDouble(track.get("length").toString()),
                        (String)track.get("additional_info"),
                        (String)track.get("starting_point_json_latlng"),
                        (String)track.get("ending_point_json_latlng"),
                        tracksPointsOfInterest,
                        userhash
                        );

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

   
    private class ClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            if(v.getId() == update_button.getId()){
                    updateTrack();
                    Intent i = new Intent(EditTrackActivity.this, EditorPanelActivity.class);
                    startActivity(i);
            }
            if(v.getId() == cancel_button.getId()){
                onBackPressed();
            }
            if(v.getId() == addPoints.getId()){
                Intent i = new Intent(EditTrackActivity.this,ChoosePointsOfInterestForTrack.class);
                i.putExtra(TRACK_EDIT, track_db_key);
                startActivityForResult(i,PICK_POINTS_REQUEST);
            }
        }
    }



    private void updateTrack() {

        edited_track.setTrack_name(track_name_field.getText().toString());
        edited_track.setAdditional_info(additional_info.getText().toString());
        edited_track.setPoints_of_interest(tracksPointsOfInterest);
        edited_track.setStarting_point(starting_point.getText().toString());
        edited_track.setEnding_point(starting_point.getText().toString());
        edited_track.setPoints_of_interest(tracksPointsOfInterest);

        /*Converting our track object to a map, that makes
        * the track ready to be entered to the JSON tree*/
        Map<String, Object> trackMap = edited_track.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(track_db_key, trackMap);
        tracks.updateChildren(childUpdates);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == PICK_POINTS_REQUEST && resultCode == PICK_POINTS_DONE) {
                ArrayList<String> keys = data.getStringArrayListExtra("points");
                ArrayList<String> types = data.getStringArrayListExtra("types");
                tracksPointsOfInterest.clear();
                for(int i = 0; i< keys.size(); i++){
                    tracksPointsOfInterest.put(keys.get(i),types.get(i));
                }

            }

        } catch (Exception ex) {Toast.makeText(EditTrackActivity.this, ex.toString(),
                Toast.LENGTH_SHORT).show();
        }
    }


}
