package com.zehava.cityforest.Activitys;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
//import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.services.android.telemetry.location.LocationEngine;
import com.mapbox.services.android.telemetry.location.LocationEngineListener;
import com.mapbox.services.android.telemetry.permissions.PermissionsManager;
import com.zehava.cityforest.R;

import java.util.Map;
import java.util.ArrayList;
import java.util.List;

import static com.zehava.cityforest.Constants.CHOSEN_TRACK;
import static com.zehava.cityforest.Constants.SELECTED_TRACK;
import static com.zehava.cityforest.Constants.TRACK_WAYPOINTS;

public class SelectedTrackDetailsActivity extends AppCompatActivity {

    private FirebaseDatabase database;
    private DatabaseReference tracks;
    private DatabaseReference points_of_interest;
    private String track_db_key;

    private FloatingActionButton floatingActionButton;
    private LocationEngine locationEngine;
    private LocationEngineListener locationEngineListener;
    private PermissionsManager permissionsManager;

   private ArrayList<String> waypointsString;
    String routeString;

    private TextView track_name_field;
    private TextView starting_point_field;
    private TextView ending_point_field;
    private TextView distance_field;
    private TextView duration_field;

    private TextView summary_field;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_selected_track);


        final Intent i = getIntent();
        track_db_key = i.getStringExtra(SELECTED_TRACK);

        track_name_field = (TextView) findViewById(R.id.trackNameField);
        starting_point_field = (TextView) findViewById(R.id.startingPointField);
        ending_point_field = (TextView) findViewById(R.id.endingPointField);
        distance_field = (TextView) findViewById(R.id.distanceField);
        duration_field = (TextView) findViewById(R.id.durationField);

        summary_field = (TextView) findViewById(R.id.summaryField);

        floatingActionButton = (FloatingActionButton) findViewById(R.id.location_toggle_fab);
        floatingActionButton.setEnabled(false);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            Intent intent = new Intent(SelectedTrackDetailsActivity.this, MyNavigationActivity.class);
            intent.putExtra(TRACK_WAYPOINTS, waypointsString);
            intent.putExtra(SELECTED_TRACK, track_db_key);
            startActivity(intent);


            }
        });


        initiateScreenValues();

    }


    private void initiateScreenValues() {



        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference tracks = "https://fir-cityforest.firebaseio.com/tracks";
        DatabaseReference tracks = database.getReference("tracks");

        tracks.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> tracksMap = (Map<String, Object>) dataSnapshot.getValue();
                Map<String, Object> track = (Map<String, Object>) tracksMap.get(track_db_key);


                track_name_field.setText((String) track.get("track_name"));
                starting_point_field.setText((String) track.get("starting_point"));
                ending_point_field.setText((String) track.get("ending_point"));
                distance_field.setText(track.get("length").toString());
                duration_field.setText(track.get("duration").toString());
                summary_field.setText((String) track.get("additional_info"));
                routeString = (String) track.get("route");
                waypointsString = new ArrayList<>((List<String>) track.get("waypoints"));

                floatingActionButton.setEnabled(true);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.app_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent i;
        switch(item.getItemId()){
            case R.id.aboutActivity:
                i = new Intent(this, AboutUsActivity.class);
                startActivity(i);
                return true;

            case R.id.contactUsActivity:
                i = new Intent(this, ContactUsActivity.class);
                startActivity(i);
                return true;


            case R.id.userGuideActivity:
                i = new Intent(this, UserGuideActivity.class);
                startActivity(i);
                return true;

            case R.id.searchTracksActivity:
                i = new Intent(this, AdvancedSearchTracksActivity.class);
                startActivity(i);
                return true;



            case R.id.tracksActivity:
                i = new Intent(this, TracksActivity.class);
                startActivity(i);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

