package com.zehava.cityforest.Activitys;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.zehava.cityforest.Models.Track;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zehava.cityforest.R;

import java.util.HashMap;
import java.util.Map;

import static com.zehava.cityforest.Constants.TRACK_EDIT;
import static com.zehava.cityforest.Constants.TRACK_EDITED;

public class EditTrackActivity extends AppCompatActivity {

    private FirebaseDatabase database;
    private DatabaseReference tracks;
    private String track_db_key;
    private Track edited_track;

    private EditText track_name_field;
    private Spinner starting_point;
    private Spinner ending_point;
    private EditText duration_field;
    private EditText distance_field;
    private Spinner track_level;
    private EditText additional_info;

    private Button update_button;
    private Button cancel_button;

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
        track_level = findViewById((R.id.trackLevel));
        additional_info = findViewById(R.id.trackSummaryField);
        update_button = findViewById(R.id.updateButton);
        cancel_button = findViewById(R.id.cancelButton);

        update_button.setOnClickListener(new ClickListener());
        cancel_button.setOnClickListener(new ClickListener());

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


                edited_track = new Track((String)track.get("route"),
                        track_db_key,
                        (String)track.get("track_name"),
                        (String)track.get("starting_point"),
                        (String)track.get("ending_point"),
                        Double.parseDouble(track.get("duration").toString()),
                        Double.parseDouble(track.get("length").toString()),
                        (String)track.get("additional_info"),
                        (String)track.get("starting_point_json_latlng"),
                        (String)track.get("ending_point_json_latlng"));
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
                boolean canSave = checkFields();
                if(canSave){
                    updateTrack();
                    Intent i = new Intent(EditTrackActivity.this, EditTracksActivity.class);
                    setResult(TRACK_EDITED);
                    startActivity(i);
                }
            }
            if(v.getId() == cancel_button.getId()){
                onBackPressed();
            }
        }
    }

    private void updateTrack() {

        double duration = Double.parseDouble(duration_field.getText().toString());
        double distance = Double.parseDouble(distance_field.getText().toString());

        edited_track.setTrack_name(track_name_field.getText().toString());
        edited_track.setStarting_point(starting_point.getSelectedItem().toString());
        edited_track.setEnding_point(ending_point.getSelectedItem().toString());
        edited_track.setDuration(duration);
        edited_track.setLength(distance);
        edited_track.setAdditional_info(additional_info.getText().toString());

        /*Converting our track object to a map, that makes
        * the track ready to be entered to the JSON tree*/
        Map<String, Object> trackMap = edited_track.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(track_db_key, trackMap);
        tracks.updateChildren(childUpdates);
    }

    private boolean checkFields(){
        if(starting_point.getSelectedItem().toString().equals(getResources().getString(R.string.choose_a_station))
                || ending_point.getSelectedItem().toString().equals(getResources().getString(R.string.choose_a_station))){
            Toast.makeText(this, R.string.choose_station_empty, Toast.LENGTH_LONG).show();
            return false;
        }

        if(distance_field.getText().toString().equals("")){
            Toast.makeText(this, R.string.choose_distance_empty, Toast.LENGTH_LONG).show();
            return false;
        }

        if(track_level.getSelectedItem().toString().equals(getResources().getString(R.string.choose_a_level))){
            Toast.makeText(this, R.string.choose_level_empty, Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

}
