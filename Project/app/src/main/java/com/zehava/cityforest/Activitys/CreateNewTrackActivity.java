package com.zehava.cityforest.Activitys;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Spinner;
import android.widget.Toast;

import com.zehava.cityforest.Managers.JsonParserManager;
import com.zehava.cityforest.Models.Track;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mapbox.mapboxsdk.annotations.Polyline;
import com.mapbox.services.directions.v5.models.DirectionsRoute;
import com.zehava.cityforest.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.zehava.cityforest.Constants.CHOSEN_TRACK;
import static com.zehava.cityforest.Constants.TRACK_CREATED;
import static com.zehava.cityforest.Constants.TRACK_ENDING_POINT;
import static com.zehava.cityforest.Constants.TRACK_ENDING_POINT_NAME;
import static com.zehava.cityforest.Constants.TRACK_STARTING_POINT;
import static com.zehava.cityforest.Constants.TRACK_STARTING_POINT_NAME;



public class CreateNewTrackActivity extends AppCompatActivity {

    private FirebaseDatabase database;
    private DatabaseReference tracks;
    private DirectionsRoute current_route;

    private EditText track_name_field;
    private TextView starting_point;
    private TextView ending_point;
    private TextView duration_field;
    private TextView distance_field;
  
    private EditText additional_info;
    private String starting_point_JsonLatLng;
    private String ending_point_JsonLatLng;
    private String starting_point_name;
    private String ending_point_name;

    private Button save_button;
    private Button cancel_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_track);

        database = FirebaseDatabase.getInstance();
        tracks = database.getReference("tracks");

        Intent i = getIntent();
        current_route = JsonParserManager.getInstance().retreiveRouteFromJson(i.getStringExtra(CHOSEN_TRACK));
        starting_point_JsonLatLng = i.getStringExtra(TRACK_STARTING_POINT);
        ending_point_JsonLatLng = i.getStringExtra(TRACK_ENDING_POINT);
        starting_point_name = i.getStringExtra(TRACK_ENDING_POINT_NAME);
        ending_point_name = i.getStringExtra(TRACK_STARTING_POINT_NAME);

        track_name_field = findViewById(R.id.trackNameField);
        starting_point = findViewById(R.id.startingPoint);
        ending_point = findViewById(R.id.endingPoint);
        duration_field = findViewById(R.id.durationField);
        distance_field = findViewById(R.id.distanceField);

        additional_info = findViewById(R.id.trackSummaryField);
        save_button = findViewById(R.id.saveButton);
        cancel_button = findViewById(R.id.cancelButton);


        double temp_duration = current_route.getDuration()/3600;
        double temp_distance = current_route.getDistance()/1000;
        duration_field.setText(""+Math.floor(temp_duration * 100) / 100);
        distance_field.setText(""+Math.floor(temp_distance * 100) / 100);

        ending_point.setText(ending_point_name);
        starting_point.setText(starting_point_name);

        save_button.setOnClickListener(new MyClickListener());
        cancel_button.setOnClickListener(new MyClickListener());
    }

    private class MyClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            if(v.getId() == save_button.getId()){
//                boolean canSave = checkFields();
//                if(canSave){
                    writeNewTrack();

                    Intent i = new Intent(CreateNewTrackActivity.this, EditorPanelActivity.class);
                    setResult(TRACK_CREATED);
                    startActivity(i);
//                }
            }
            if(v.getId() == cancel_button.getId()){
                onBackPressed();
            }
        }
    }



    private void writeNewTrack(){

        String key = tracks.push().getKey();

        double duration = Double.parseDouble(duration_field.getText().toString());
        double distance = Double.parseDouble(distance_field.getText().toString());

        Track track = new Track(
                JsonParserManager.getInstance().castRouteToJson(this.current_route),
                key,
                track_name_field.getText().toString(),
                starting_point.getText().toString(),
                ending_point.getText().toString(),
                duration,
                distance,
                additional_info.getText().toString(),
                starting_point_JsonLatLng,
                ending_point_JsonLatLng);


        /*Converting our track object to a map, that makes
        * the track ready to be entered to the JSON tree*/
        Map<String, Object> trackMap = track.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(key, trackMap);
        tracks.updateChildren(childUpdates);
    }

}
