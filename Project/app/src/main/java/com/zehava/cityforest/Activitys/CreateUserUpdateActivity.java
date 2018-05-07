package com.zehava.cityforest.Activitys;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.ViewFlipper;
import android.widget.Button;


import com.google.firebase.database.DatabaseError;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.zehava.cityforest.Managers.IconManager;
import com.zehava.cityforest.Managers.JsonParserManager;
import com.zehava.cityforest.Managers.PMethods;
import com.zehava.cityforest.Models.UserUpdate;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.zehava.cityforest.R;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static com.zehava.cityforest.Constants.CREATED_COORDINATE_FOR_ZOOM;
import static com.zehava.cityforest.Constants.CREATED_UPDATE_FOR_ZOOM;
import static com.zehava.cityforest.Constants.CURRENT_USER_NAME;
import static com.zehava.cityforest.Constants.CURRENT_USER_UID;
import static com.zehava.cityforest.Constants.UPDATE_POSITION;
import static com.zehava.cityforest.Constants.USER_UPDATE_CREATED;

/**
 * Created by avigail on 12/12/17.
 */

public class CreateUserUpdateActivity extends AppCompatActivity {

    private Intent i;

    private String userhash;
    private String type;
    private String snippet;
    private ViewFlipper vf;

    private FirebaseDatabase database;
    private DatabaseReference user_updates;

    private LatLng chosenCoordinateLatLng;

    private EditText snippetView;
    private Button send_report;
    int i1;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_notifications);
        IconManager.getInstance().generateIcons(IconFactory.getInstance(this));
        database = FirebaseDatabase.getInstance();
        user_updates = database.getReference("user_updates");

        i = getIntent();

        vf = (ViewFlipper)findViewById(R.id.vf);

        TableLayout tableLayout = (TableLayout) findViewById(R.id.table_layout);

        myClickListener clickListener = new myClickListener();

        for(int i = 0; i < tableLayout.getChildCount(); i++ )
            if( tableLayout.getChildAt( i ) instanceof TableRow) {
                TableRow row = (TableRow) tableLayout.getChildAt(i);
                for (int j = 0; j < row.getChildCount(); j++) {
                    row.getChildAt(j).setOnClickListener(clickListener);
                }
            }

        snippetView = (EditText) findViewById(R.id.snippetTxt);
        send_report = (Button) findViewById(R.id.end_report);
        send_report.setOnClickListener(clickListener);

    }



    private void writeNewUserUpdate() {

        UserUpdate user_update = new UserUpdate(
                userhash,
                type,
                snippet,
                chosenCoordinateLatLng.getLongitude(),
                chosenCoordinateLatLng.getLatitude()
        );

        String key = hashFunction();

        /*Converting our coordinate object to a map, that makes
        * the coordinate ready to be entered to the JSON tree*/
        Map<String, Object> coordinateMap = user_update.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(key, coordinateMap);
        user_updates.updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    System.out.println("Data could not be saved " + databaseError.getMessage());
                } else {

                    Intent intent = getIntent();
                    intent.putExtra(CREATED_UPDATE_FOR_ZOOM, JsonParserManager.getInstance().castLatLngToJson(chosenCoordinateLatLng));
                    intent.putExtra("id",i1);
                    setResult(USER_UPDATE_CREATED, intent);
                    finish();
                }
            }
        });



    }


    private String hashFunction() {

        double longitude = chosenCoordinateLatLng.getLongitude();
        return PMethods.getInstance().getUserUpdateHashKey(longitude, type);

    }

    private class myClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.end_report) {
                snippet = snippetView.getText().toString();

                writeNewUserUpdate();

//
            }
            else if(v instanceof TextView) {
                type = ((TextView) v).getText().toString();
                userhash = i.getStringExtra(CURRENT_USER_NAME);
                chosenCoordinateLatLng = JsonParserManager.getInstance().retreiveLatLngFromJson(i.getStringExtra(UPDATE_POSITION));

                vf.setDisplayedChild(1);
            }



        }
    }
}
