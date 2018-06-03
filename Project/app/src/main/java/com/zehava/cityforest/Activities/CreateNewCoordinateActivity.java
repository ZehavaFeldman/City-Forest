package com.zehava.cityforest.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.zehava.cityforest.Managers.JsonParserManager;
import com.zehava.cityforest.Models.Coordinate;
import com.zehava.cityforest.Models.PointOfInterest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.zehava.cityforest.PointsChooseAdapter;
import com.zehava.cityforest.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.zehava.cityforest.Constants.CHOSEN_COORDINATE;
import static com.zehava.cityforest.Constants.COORDINATE_CREATED;
import static com.zehava.cityforest.Constants.CREATED_COORDINATE_FOR_ZOOM;
import static com.zehava.cityforest.Constants.CURRENT_USER_NAME;
import static com.zehava.cityforest.Constants.FROM_ADD_TRACK;

public class CreateNewCoordinateActivity extends AppCompatActivity {

    private static final String TAG = "db_on_change";
    private FirebaseDatabase database;
    private DatabaseReference coordinates;
    private DatabaseReference points_of_interest;
    private LatLng chosenCoordinateLatLng;
    private EditText titleField;
    private EditText snippetField;
    private Button saveButt;
    private Button cancelButt;
    private CheckBox isPointOfInterest;
    private Spinner typeOfPoint;
    private String userhash;
    private boolean fromAddTrack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_coordinate);

        database = FirebaseDatabase.getInstance();
        coordinates = database.getReference("coordinates");
        points_of_interest = database.getReference("points_of_interest");

        Intent i = getIntent();
        chosenCoordinateLatLng = JsonParserManager.getInstance().retreiveLatLngFromJson(i.getStringExtra(CHOSEN_COORDINATE));
        userhash = i.getStringExtra(CURRENT_USER_NAME);
        titleField = (EditText)findViewById(R.id.titleField);
        snippetField = (EditText)findViewById(R.id.SummaryField);
        isPointOfInterest = (CheckBox)findViewById(R.id.isPointOfInterestCheckbox);
        typeOfPoint = (Spinner)findViewById(R.id.typeOfPoint);
        saveButt = (Button)findViewById(R.id.saveButt);
        cancelButt = (Button)findViewById(R.id.cancelButt);
        saveButt.setOnClickListener(new MyClickListener());
        cancelButt.setOnClickListener(new MyClickListener());

        initiateSpinner(typeOfPoint, R.array.points_of_interest);
        isPointOfInterest.setOnCheckedChangeListener(new CheckboxListener());
    }

    private void initiateSpinner(Spinner spinner,  int spinner_type){
        ArrayList<String> temp = new ArrayList<>(Arrays.asList(getResources().getStringArray(spinner_type)));
        PointsChooseAdapter adapter = new PointsChooseAdapter(this,temp);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // Create an ArrayAdapter using the string array and a default spinner layout
//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
//                spinner_type, R.layout.spinner_item);
//        // Specify the layout to use when the list of choices appears
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        // Apply the adapter to the spinner
//        spinner.setAdapter(adapter);
    }

    private class CheckboxListener implements CompoundButton.OnCheckedChangeListener{
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(isChecked){
                typeOfPoint.setVisibility(View.VISIBLE);
                Toast.makeText(CreateNewCoordinateActivity.this, "Please choose " +
                        "the type of your point", Toast.LENGTH_SHORT).show();
            }
            else
                typeOfPoint.setVisibility(View.INVISIBLE);
        }
    }


    private class MyClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            if(v.getId() == saveButt.getId()){

                if(isPointOfInterest.isChecked()){
                    boolean canSave = checkFields();
                    if(canSave){
                        writeNewPointOfInterest();
                    }
                    else
                        return;
                }
                else
                    writeNewCoordinate();


                Intent intent = getIntent();
                intent.putExtra(CREATED_COORDINATE_FOR_ZOOM, JsonParserManager.getInstance().castLatLngToJson(chosenCoordinateLatLng));
                intent.putExtra(FROM_ADD_TRACK, intent.getBooleanExtra(FROM_ADD_TRACK,false));
                setResult(COORDINATE_CREATED, intent);
                finish();
            }
            // else, go back to last page you came from
            if(v.getId() == cancelButt.getId()){
                onBackPressed();
            }
        }
    }



    private boolean checkFields() {
        if(titleField.getText().toString().equals("")){
            Toast.makeText(this, R.string.title_field_empty, Toast.LENGTH_SHORT).show();
            return false;
        }
        if(typeOfPoint.getSelectedItem().toString().equals(getResources().getString(R.string.choose_type_of_point))){
            Toast.makeText(this, R.string.choose_type_of_point_empty, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }




    /*Method writes to the Firebase db a new coordinate
    * with all the details*/
    private void writeNewCoordinate(){
        String key = hashFunction();

        Coordinate coordinate = new Coordinate(
                chosenCoordinateLatLng.getLongitude(),
                chosenCoordinateLatLng.getLatitude(),
                titleField.getText().toString(),
                snippetField.getText().toString());

        /*Converting our coordinate object to a map, that makes
        * the coordinate ready to be entered to the JSON tree*/
        Map<String, Object> coordinateMap = coordinate.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(key, coordinateMap);
        coordinates.updateChildren(childUpdates);
    }

    private void writeNewPointOfInterest() {
        String key = hashFunction();
        PointOfInterest point_of_interest = new PointOfInterest(
                chosenCoordinateLatLng.getLatitude(),
                chosenCoordinateLatLng.getLongitude(),
                titleField.getText().toString(),
                snippetField.getText().toString(),
                typeOfPoint.getSelectedItem().toString(),
                userhash);

        /*Converting our coordinate object to a map, that makes
        * the coordinate ready to be entered to the JSON tree*/
        Map<String, Object> coordinateMap = point_of_interest.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(key, coordinateMap);
        points_of_interest.updateChildren(childUpdates);
    }


    private String hashFunction() {
        double longitude = chosenCoordinateLatLng.getLongitude();
        //double latitude = chosenCoordinateLatLng.getLatitude();



        int hash = ((int) (10000000*longitude));
        return "" + hash;
    }
}
