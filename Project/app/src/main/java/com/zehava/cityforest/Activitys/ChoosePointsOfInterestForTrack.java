package com.zehava.cityforest.Activitys;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Button;

import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.zehava.cityforest.FirebasePointsListAdapter;
import com.zehava.cityforest.Models.PointOfInterest;
import com.zehava.cityforest.Models.Track;
import com.zehava.cityforest.R;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.Iterator;

import android.widget.LinearLayout;

import static com.zehava.cityforest.Constants.PICK_POINTS_CANCALED;
import static com.zehava.cityforest.Constants.PICK_POINTS_DONE;
import static com.zehava.cityforest.Constants.TRACK_EDIT;


/**
 * Created by avigail on 12/04/18.
 */

public class ChoosePointsOfInterestForTrack extends AppCompatActivity {

    private  HashMap<String, String> tracksPointsOfInterest;
    FirebasePointsListAdapter adapter;
    private FirebaseDatabase database;
    private DatabaseReference tracks, points_of_interest;
    String track_key;
    private Track edit_track;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent i= getIntent();
        track_key = i.getStringExtra(TRACK_EDIT);

        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_chhose_points_for_track);

        database = FirebaseDatabase.getInstance();
        tracks = database.getReference("tracks");
//        points_of_interest = database.getReference("points_of_interest");

        tracksPointsOfInterest = new  HashMap<>();


        Query query = database.getReference("points_of_interest");
        FirebaseListOptions<PointOfInterest> options =
                new FirebaseListOptions.Builder<PointOfInterest>()
                        .setQuery(query, PointOfInterest.class)
                        .setLayout(R.layout.points_list_item)
                        .build();

        adapter = new FirebasePointsListAdapter(options);
        final ListView points_list = (ListView) findViewById(R.id.pointsList);


        points_list.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PointOfInterest pointOfInterest = (PointOfInterest) parent.getItemAtPosition(position);
                String pointkey = getMarkerHashKey(pointOfInterest.getPosition().getLongitude());

                if (tracksPointsOfInterest.containsKey(pointkey))
                    tracksPointsOfInterest.remove(pointkey);

                else
                    tracksPointsOfInterest.put(pointkey,pointOfInterest.getType());


                adapter.setSelectedIndex(pointkey,pointkey);



            }
        });
        points_list.setAdapter(adapter);

    if(!track_key.equalsIgnoreCase("NO_NAME")) {
        tracks.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> tracksMap = (Map<String, Object>) dataSnapshot.getValue();
                Map<String, Object> track = (Map<String, Object>) tracksMap.get(track_key);


                Map jArray = (HashMap<String, String>) track.get("points");
                if (jArray != null) {
                    adapter.setSelectedIndex(jArray);

                    tracksPointsOfInterest.putAll(jArray);

                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    } else{

        HashMap<String,String> t = (HashMap<String,String>)i.getSerializableExtra("points");
        if(t!=null){
            adapter.setSelectedIndex(t);
            tracksPointsOfInterest.putAll(t);
        }

    }


        Button button = findViewById(R.id.updateButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//
//                ArrayList<String> Keylist = new ArrayList<>();
//                ArrayList<String> Typelist = new ArrayList<>();
//
//                Iterator<Map.Entry<String, String>> iterator = tracksPointsOfInterest.entrySet().iterator();
//                while (iterator.hasNext()) {
//                    Map.Entry<String, String> key = iterator.next();
//                    Keylist.add(key.getKey());
//                    Typelist.add(key.getValue());
//                }

                Intent intent = getIntent();
                intent.putExtra("points", tracksPointsOfInterest);
//                intent.putStringArrayListExtra("types", Typelist);
                setResult(PICK_POINTS_DONE, intent);
                finish();
            }
        });
        Button buttonCnl = findViewById(R.id.cancelButton);
        buttonCnl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onBackPressed();
            }
        });
    }
    private String getMarkerHashKey(final double key) {
        double longitude = key;
        //double latitude = chosenCoordinateLatLng.getLatitude();

        int hash = (int) (10000000*longitude);
        return "" + hash;
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.startListening();
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

}
