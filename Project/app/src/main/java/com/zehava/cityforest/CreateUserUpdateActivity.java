package com.zehava.cityforest;

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


import com.zehava.cityforest.Models.UserUpdate;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mapbox.mapboxsdk.geometry.LatLng;

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

    private String uid;
    private String uname;
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

    private LatLng retreiveLatLngFromJson(String stringExtra) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.serializeSpecialFloatingPointValues();

        Gson gson = gsonBuilder.create();
        LatLng obj = gson.fromJson(stringExtra, LatLng.class);
        return obj;
    }

    private String castLatLngToJson(LatLng coordinate) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.serializeSpecialFloatingPointValues();

        Gson gson = gsonBuilder.create();
        String json = gson.toJson(coordinate, LatLng.class);
        return json;
    }


    private void writeNewUserUpdate() {

        UserUpdate user_update = new UserUpdate(
                uid,
                uname,
                i1,
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
        user_updates.updateChildren(childUpdates);
    }


    private String hashFunction() {
        double longitude = chosenCoordinateLatLng.getLongitude();
        //double latitude = chosenCoordinateLatLng.getLatitude();
        i1 = UserUpdate.generateIdByType(type);
        Log.d("--!!!!",uid+" "+uname);

        int hash = ((int) (10000000*longitude))+i1;

        return "" + hash;
    }

    private class myClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.end_report) {
                snippet = snippetView.getText().toString();

                writeNewUserUpdate();

                Intent intent = getIntent();
                intent.putExtra(CREATED_UPDATE_FOR_ZOOM, castLatLngToJson(chosenCoordinateLatLng));
                intent.putExtra("id",i1);
                setResult(USER_UPDATE_CREATED, intent);
                finish();
            }
            else if(v instanceof TextView) {
                type = ((TextView) v).getText().toString();
                uid = i.getStringExtra(CURRENT_USER_UID);
                uname= i.getStringExtra(CURRENT_USER_NAME);
                chosenCoordinateLatLng = retreiveLatLngFromJson(i.getStringExtra(UPDATE_POSITION));

                vf.setDisplayedChild(1);
            }



        }
    }
}
