package com.zehava.cityforest.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
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
import com.zehava.cityforest.FirebasePointsListAdapter;
import com.zehava.cityforest.FirebaseUtils;
import com.zehava.cityforest.Models.PointOfInterest;
import com.zehava.cityforest.Models.Track;
import com.zehava.cityforest.R;

import java.util.HashMap;
import java.util.Map;

import static com.zehava.cityforest.Constants.PICK_POINTS_DONE;
import static com.zehava.cityforest.Constants.TRACK_EDIT;


/**
 * Created by avigail on 12/04/18.
 */

public class ChoosePointsOfInterestForTrack extends AppCompatActivity {

    private  HashMap<String, String> tracksPointsOfInterest;
    FirebasePointsListAdapter adapter;
    String track_key;
    private Track edit_track;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent i= getIntent();
        track_key = i.getStringExtra(TRACK_EDIT);

        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_choose_points_for_track);


        tracksPointsOfInterest = new  HashMap<>();


        Query query = FirebaseUtils.getPointsRef();
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
        FirebaseUtils.getTracksRef().addListenerForSingleValueEvent(new ValueEventListener() {
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

                Intent intent = getIntent();
                intent.putExtra("points", tracksPointsOfInterest);
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
