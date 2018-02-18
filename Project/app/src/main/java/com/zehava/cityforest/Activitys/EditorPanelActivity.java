package com.zehava.cityforest.Activitys;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PointF;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.RatingBar;
import android.widget.LinearLayout;

//import com.mapbox.mapboxsdk.Mapbox;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.JsonElement;
import com.mapbox.mapboxsdk.MapboxAccountManager;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.services.commons.geojson.Feature;
import com.mapbox.services.geocoding.v5.GeocodingCriteria;
import com.mapbox.services.geocoding.v5.MapboxGeocoding;
import com.mapbox.services.geocoding.v5.models.CarmenFeature;
import com.mapbox.services.geocoding.v5.models.GeocodingResponse;
import com.zehava.cityforest.DragAndDrop;
import com.zehava.cityforest.ICallback;
import com.zehava.cityforest.MakeOwnTrackActivity;
import com.zehava.cityforest.Managers.JsonParserManager;
import com.zehava.cityforest.Managers.LocaleManager;
import com.zehava.cityforest.Models.Coordinate;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerView;
import com.mapbox.mapboxsdk.annotations.MarkerViewOptions;
import com.mapbox.mapboxsdk.annotations.Polyline;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.constants.Style;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.services.android.telemetry.location.LocationEngine;
import com.mapbox.services.android.telemetry.location.LocationEngineListener;
import com.mapbox.services.android.telemetry.permissions.PermissionsListener;
import com.mapbox.services.android.telemetry.permissions.PermissionsManager;
import com.mapbox.services.commons.ServicesException;
import com.mapbox.services.commons.geojson.LineString;
import com.mapbox.services.commons.models.Position;
import com.mapbox.services.directions.v5.DirectionsCriteria;
import com.mapbox.services.directions.v5.MapboxDirections;
import com.mapbox.services.directions.v5.models.DirectionsResponse;
import com.mapbox.services.directions.v5.models.DirectionsRoute;
import com.zehava.cityforest.Models.PointOfInterest;
import com.zehava.cityforest.MoveablePointActivity;
import com.zehava.cityforest.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.zehava.cityforest.Constants.ADD_COORDINATE_MODE;
import static com.zehava.cityforest.Constants.ADD_TRACK_MODE;
import static com.zehava.cityforest.Constants.CHOSEN_COORDINATE;
import static com.zehava.cityforest.Constants.CHOSEN_TRACK;
import static com.zehava.cityforest.Constants.COORDINATE_CREATED;
import static com.zehava.cityforest.Constants.COORDINATE_EDITED;
import static com.zehava.cityforest.Constants.COORDINATE_KEY;
import static com.zehava.cityforest.Constants.CREATED_COORDINATE_FOR_ZOOM;
import static com.zehava.cityforest.Constants.CREATED_UPDATE_FOR_ZOOM;
import static com.zehava.cityforest.Constants.CURRENT_USER_UID;
import static com.zehava.cityforest.Constants.DEFAULT_JERUSALEM_COORDINATE;
import static com.zehava.cityforest.Constants.DELETE_COORDINATE_MODE;
import static com.zehava.cityforest.Constants.EDITED_COORDINATE_FOR_ZOOM;
import static com.zehava.cityforest.Constants.EDIT_COORDINATE;
import static com.zehava.cityforest.Constants.EDIT_COORDINATE_MODE;
import static com.zehava.cityforest.Constants.FINISH_EDIT_TRACK_MODE;
import static com.zehava.cityforest.Constants.MAX_NUM_OF_TRACK_COORDINATES;
import static com.zehava.cityforest.Constants.MOVE_MARKER;
import static com.zehava.cityforest.Constants.NEW_COORDINATE;
import static com.zehava.cityforest.Constants.NEW_TRACK;
import static com.zehava.cityforest.Constants.NEW_USER_UPDATE;
import static com.zehava.cityforest.Constants.ROUTE_LINE_WIDTH;
import static com.zehava.cityforest.Constants.SHOW_DETAILS_POPUP;
import static com.zehava.cityforest.Constants.TRACK_EDIT;
import static com.zehava.cityforest.Constants.TRACK_ENDING_POINT;
import static com.zehava.cityforest.Constants.TRACK_ENDING_POINT_NAME;
import static com.zehava.cityforest.Constants.TRACK_STARTING_POINT;
import static com.zehava.cityforest.Constants.TRACK_STARTING_POINT_NAME;
import static com.zehava.cityforest.Constants.UPDATE_POSITION;
import static com.zehava.cityforest.Constants.USER_UPDATE_CREATED;
import static com.zehava.cityforest.Constants.ZOOM_LEVEL_CURRENT_LOCATION;
import static com.zehava.cityforest.Constants.ZOOM_LEVEL_MARKER_CLICK;
import static com.mapbox.services.android.telemetry.location.AndroidLocationEngine.getLocationEngine;

public class EditorPanelActivity extends AppCompatActivity implements PermissionsListener , ICallback {

    private MapView mapView;
    private MapboxMap map;
    private DirectionsRoute currentRoute;
    private PolylineOptions routeLine;
    private FirebaseDatabase database;
    private DatabaseReference coordinates;
    private DatabaseReference tracks;
    private DatabaseReference points_of_interest;
    private DatabaseReference user_updates;
    private ImageButton add_coordinate_button;
    private ImageButton delete_coordinate_button;
    private ImageButton edit_coordinate_button;
    private ImageButton add_track_button;
    private Button finish_edit_track_butt;
    private Button save_track;
    private Button continue_editing;
    private ImageButton edit_tracks_button;
    private TextView counter_coordinates;
    private TextView likes_count;

    /*Count how many coordinates selected for creating a track (max of 25 coordinates)*/
    private int count_coordinates_selected = 0;
    private int markerIndex;
    private ArrayList<Double> track_coordinates = new ArrayList<>();
    private ArrayList<Marker> track_markers = new ArrayList<>();
    private ArrayList<Position> track_positions = new ArrayList<>();

    private FloatingActionButton floatingActionButton;
    private FloatingActionButton createUserUpdate;
    private LocationEngine locationEngine;
    private LocationEngineListener locationEngineListener;
    private PermissionsManager permissionsManager;

    private GoogleApiClient mGoogleApiClient;

    private ProgressBar loading_map_progress_bar;
    private PopupWindow mPopupWindow;
    private RelativeLayout mRelativeLayout;

    /*marker and imageview nedded for fragging points on map*/
    private Marker droppedMarker;
    private DragAndDrop hoveringMarker;

    private Marker featureMarker;

    String uid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*hide status bar and app label in action bar*/
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        /*Mapbox initializations*/
        MapboxAccountManager.start(this,getString(R.string.access_token));
        setContentView(R.layout.activity_editor_panel);

        // Get the location engine object for later use.
        locationEngine = getLocationEngine(this);
        locationEngine.activate();

        mapView = (MapView) findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new myOnMapReadyCallback());

        /*initalizing draggable marker and adding to map*/
        hoveringMarker = new DragAndDrop(this,this);
        hoveringMarker.setImageResource(R.drawable.blue_marker);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
        hoveringMarker.setLayoutParams(params);
        hoveringMarker.setVisibility(View.INVISIBLE);
        mapView.addView(hoveringMarker);

        /*firebase initialization*/
        database = FirebaseDatabase.getInstance();
        coordinates = database.getReference("coordinates");
        points_of_interest = database.getReference("points_of_interest");
        tracks = database.getReference("tracks");
        user_updates = database.getReference("user_updates");

        /*Buttons for different edit map modes*/
        add_coordinate_button = (ImageButton) findViewById(R.id.addCoordinateButt);
        add_track_button = (ImageButton) findViewById(R.id.addTrackButt);
        loading_map_progress_bar = (ProgressBar) findViewById(R.id.loadingMapProgress);
        finish_edit_track_butt = (Button) findViewById(R.id.finishEditTrack);
        save_track = (Button) findViewById(R.id.saveTrack);
        continue_editing = (Button) findViewById(R.id.continueEditTrack);
        counter_coordinates = (TextView) findViewById(R.id.counterCoordinates);
        mRelativeLayout = (RelativeLayout) findViewById(R.id.mapLayout);

        ClickListener clickListener = new ClickListener();
        add_coordinate_button.setOnClickListener(clickListener);
        add_track_button.setOnClickListener(clickListener);
        finish_edit_track_butt.setOnClickListener(clickListener);
        save_track.setOnClickListener(clickListener);
        continue_editing.setOnClickListener(clickListener);


        ADD_COORDINATE_MODE = false;
        DELETE_COORDINATE_MODE = false;
        EDIT_COORDINATE_MODE = false;
        ADD_TRACK_MODE = false;
        FINISH_EDIT_TRACK_MODE = false;
        SHOW_DETAILS_POPUP = false;
        MOVE_MARKER = false;

        counter_coordinates.setVisibility(View.INVISIBLE);
        finish_edit_track_butt.setVisibility(View.INVISIBLE);
        save_track.setVisibility(View.INVISIBLE);
        continue_editing.setVisibility(View.INVISIBLE);

        floatingActionButton = (FloatingActionButton) findViewById(R.id.location_toggle_fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (map != null) {
                    toggleGps(!map.isMyLocationEnabled());
                }
            }
        });

        createUserUpdate = (FloatingActionButton) findViewById(R.id.user_update_fab);
        createUserUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(EditorPanelActivity.this, CreateUserUpdateActivity.class);
                i.putExtra(CURRENT_USER_UID, uid);

                if (ActivityCompat.checkSelfPermission(EditorPanelActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(EditorPanelActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }

                Location lastLocation = locationEngine.getLastLocation();

                i.putExtra(UPDATE_POSITION, JsonParserManager.getInstance().castLatLngToJson(new LatLng(lastLocation)));
                startActivityForResult(i, NEW_USER_UPDATE);

            }
        });
        loading_map_progress_bar.setVisibility(View.VISIBLE);

        initPopupWindow();

    }

    /*In order to be able to sign out from the logged in account, I have to
    * check who is the logged in user.*/
    @Override
    protected void onStart() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mGoogleApiClient.connect();
        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser != null)
           uid = currentUser.getUid();
        super.onStart();
    }

    /* owveriding ICallback methods-
    we use icallback for detecting dragable states and user update*/

    @Override
    public void onDraggableNotify(DRAGGABLE_CALSS draggableIcall) {
        //user stoped draging, time to update marker position
        if(draggableIcall == DRAGGABLE_CALSS.VIEW_MOVED){
            if (map != null) {

                float coordinateX = hoveringMarker.getX() + (hoveringMarker.getWidth() / 2);
                float coordinateY = hoveringMarker.getY()+(hoveringMarker.getHeight());
                float[] coords = new float[]{coordinateX, coordinateY};
                final LatLng latLng = map.getProjection().fromScreenLocation(new PointF(coords[0], coords[1]));
                hoveringMarker.setVisibility(View.INVISIBLE);


                // Create the marker icon the dropped marker will be using.
                //  Icon icon = IconFactory.getInstance(MoveablePointActivity.this).fromResource(R.drawable.blue_marker);

                // Placing the marker on the mapboxMap as soon as possible causes the illusion
                // that the hovering marker and dropped marker are the same.
                droppedMarker = map.addMarker(new MarkerViewOptions().position(latLng));
                IconFactory iconFactory = IconFactory.getInstance(EditorPanelActivity.this);
                Icon icon = iconFactory.fromResource(R.drawable.blue_marker);
                droppedMarker.setIcon(icon);
//                addTrackPoint(latLng, droppedMarker);
                updateTrackPoint(markerIndex,droppedMarker);


                // Finally we get the geocoding information
                //reverseGeocode(latLng);
            }

        }
        //user started draging, time to remve marker and set imageview to new position
        if(draggableIcall == DRAGGABLE_CALSS.VIEW_TOUCHED){
            if (map != null && droppedMarker!=null) {


                // Lastly, set the hovering marker back to visible.
                PointF point = map.getProjection().toScreenLocation(droppedMarker.getPosition());
                map.removeMarker(droppedMarker);
                hoveringMarker.setX(point.x-(hoveringMarker.getWidth()/2));
                hoveringMarker.setY(point.y-hoveringMarker.getHeight());
                hoveringMarker.setVisibility(View.VISIBLE);
                droppedMarker = null;
            }

        }
    }

    @Override
    public void onUserUpdateNotify(USER_UPDATES_CLASS userUpdatesClass, ArrayList<Marker> markers) {
        //new user updates added to database, add markers to map

        //remove old updates from map
    }


    private class ClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            /*Handling the clicks on different modes, updating different modes too*/
            if(v.getId() == add_coordinate_button.getId()) {
                if(ADD_COORDINATE_MODE){
                    ADD_COORDINATE_MODE = false;
                    add_coordinate_button.setBackgroundResource(R.drawable.not_selected_btn);
                }
                else{
                    ADD_COORDINATE_MODE = true;
                    ADD_TRACK_MODE = false;
                    SHOW_DETAILS_POPUP = false;

                    add_coordinate_button.setBackgroundResource(R.drawable.selected_btn);
                    add_track_button.setBackgroundResource(R.drawable.not_selected_btn);
                }
            }

            if(v.getId() == add_track_button.getId()){
                if(ADD_TRACK_MODE){
                    ADD_TRACK_MODE = false;
                    add_track_button.setBackgroundResource(R.drawable.not_selected_btn);

                    /*
                    Passing on track_coordinates markers
                    * and change their color back to red.
                    * And clearing both arrays for the next time building a new track*/
                    for(int i=0; i<track_markers.size(); i++){
                        addMarkerForCoordinate(track_markers.get(i).getPosition(), track_markers.get(i).getTitle(),
                                track_markers.get(i).getSnippet());
                        map.removeMarker(track_markers.get(i));
                    }
                    track_coordinates.clear();
                    track_markers.clear();
                    track_positions.clear();
                    count_coordinates_selected = 0;

                    counter_coordinates.setVisibility(View.INVISIBLE);
                    finish_edit_track_butt.setVisibility(View.INVISIBLE);
                    save_track.setVisibility(View.INVISIBLE);
                    continue_editing.setVisibility(View.INVISIBLE);

                    add_coordinate_button.setClickable(true);

                }
                else{
                    ADD_COORDINATE_MODE = false;
                    SHOW_DETAILS_POPUP = false;
                    ADD_TRACK_MODE = true;

                    add_coordinate_button.setBackgroundResource(R.drawable.not_selected_btn);
                    add_track_button.setBackgroundResource(R.drawable.selected_btn);


                    updateScreenCounter();
                    counter_coordinates.setVisibility(View.VISIBLE);
                    finish_edit_track_butt.setVisibility(View.VISIBLE);
                    add_coordinate_button.setClickable(false);

                }
            }
//            /*If editor clicked this button, we need to show him the resulted track by creating
//            * the route object and save it as the class's property*/
            if(v.getId() == finish_edit_track_butt.getId()){
                if(track_markers.size() < 2){
                    Toast.makeText(EditorPanelActivity.this, R.string.not_enough_coordinates_selected, Toast.LENGTH_SHORT).show();
                    return;
                }

                final String last_marker_key = getMarkerHashKey(track_markers.get(track_markers.size()-1));
                points_of_interest.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Map<String, Object> pointsMap = (Map<String, Object>)dataSnapshot.getValue();
                        if(pointsMap == null) {
                            Toast.makeText(EditorPanelActivity.this, R.string.toast_ending_point, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Map<String, Object> point = ((Map<String, Object>)pointsMap.get(last_marker_key));

                        if(point == null){
                            Toast.makeText(EditorPanelActivity.this, R.string.toast_ending_point, Toast.LENGTH_SHORT).show();
                            return;
                        }

                        String type = (String)point.get("type");
                        if(! type.equals("תחנת רכבת")){
                            Toast.makeText(EditorPanelActivity.this, R.string.toast_ending_point, Toast.LENGTH_SHORT).show();
                            return;
                        }

                        //here we turn off ADD_TRACK_MODE
                        ADD_TRACK_MODE = false;
                        SHOW_DETAILS_POPUP = false;
                        add_track_button.setClickable(false);
                        FINISH_EDIT_TRACK_MODE = true;
                        finish_edit_track_butt.setVisibility(View.INVISIBLE);
                        save_track.setVisibility(View.VISIBLE);
                        continue_editing.setVisibility(View.VISIBLE);

                        try {
                            getRoute();
                        } catch (ServicesException servicesException) {
                            servicesException.printStackTrace();
                        }

                        LatLngBounds latLngBounds = new LatLngBounds.Builder()
                                .include(track_markers.get(0).getPosition())
                                .include(track_markers.get(track_markers.size()-1).getPosition())
                                .build();

                        map.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 200), 100);


                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
            if(v.getId() == continue_editing.getId()){
                //here we turn on ADD_TRACK_MODE
                ADD_TRACK_MODE = true;
                add_track_button.setClickable(true);
                save_track.setVisibility(View.INVISIBLE);
                continue_editing.setVisibility(View.INVISIBLE);
                finish_edit_track_butt.setVisibility(View.VISIBLE);


                map.removePolyline(routeLine.getPolyline());
            }
            if(v.getId() == save_track.getId()){
                Intent i = new Intent(EditorPanelActivity.this, CreateNewTrackActivity.class);
                i.putExtra(CHOSEN_TRACK, JsonParserManager.getInstance().castRouteToJson(currentRoute));
                i.putExtra(TRACK_STARTING_POINT, JsonParserManager.getInstance().castLatLngToJson(track_markers.get(0).getPosition()));
                i.putExtra(TRACK_ENDING_POINT, JsonParserManager.getInstance().castLatLngToJson(track_markers.get(track_markers.size()-1).getPosition()));
                i.putExtra(TRACK_STARTING_POINT_NAME, track_markers.get(0).getTitle());
                i.putExtra(TRACK_ENDING_POINT_NAME, track_markers.get(track_markers.size()-1).getTitle());
                startActivityForResult(i, NEW_TRACK);
            }

        }
    }

    private class myOnMapReadyCallback implements OnMapReadyCallback {
        @Override
        public void onMapReady(MapboxMap mapboxMap) {
            map = mapboxMap;
            map.setOnMapClickListener(new MyOnMapClickListener());
            map.setOnMarkerClickListener(new MyOnMarkerClickListener());
            map.setStyleUrl(Style.OUTDOORS);
            showDefaultLocation();
          //  showAllCoordinates();
            showAllUserUpdates();
            showAllPointsOfInterest();
            showAllTracks();
        }
    }

    // details window shown to user when clicked on marker or route

    private void initPopupWindow(){

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);


        mPopupWindow = new PopupWindow(
                inflater.inflate(R.layout.coordinate_details_popup_view, null),
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        if(Build.VERSION.SDK_INT>=21){
            mPopupWindow.setElevation(5.0f);
        }

        delete_coordinate_button = (ImageButton)mPopupWindow.getContentView().findViewById(R.id.deleteCoordinateButt);
        edit_coordinate_button = (ImageButton)mPopupWindow.getContentView().findViewById(R.id.editCoordinateButt);

        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                SHOW_DETAILS_POPUP=false;
            }
        });


    }

    //track popup
    private void initTrackPopup(final  Map<String, Object> track){
        TextView title = (TextView)mPopupWindow.getContentView().findViewById(R.id.main_content);
        title.setText(track.get("track_name").toString());

        final Button read_more = (Button) mPopupWindow.getContentView().findViewById(R.id.read_more);
        read_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        final TextView discreption = (TextView)mPopupWindow.getContentView().findViewById(R.id.minor_content);
        discreption.setText(track.get("additional_info").toString());
        read_more.setVisibility(View.GONE);

        discreption.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onGlobalLayout() {

                //check is latest line fully visible
                if (discreption.getLineCount() >2 ) {
                    // TODO you text is cut
                    read_more.setVisibility(View.VISIBLE);
                }
            }
        });



        //add click listeners for editing and removing track
        delete_coordinate_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogDeleteTrack(track);
            }
        });
        edit_coordinate_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogEditTrack(track);
            }
        });

        RatingBar ratingBar = (RatingBar)mPopupWindow.getContentView().findViewById(R.id.ratingBar);
        ratingBar.setVisibility(View.VISIBLE);

        //editor can only view current rating
        ratingBar.setIsIndicator(true);
        Float d = Float.valueOf(track.get("star_count").toString());
        Object o = (Object) d;
        ratingBar.setRating((float) o);


        LinearLayout likesWrp = (LinearLayout) mPopupWindow.getContentView().findViewById(R.id.like_wrp);
        likesWrp.setVisibility(View.VISIBLE);

        likes_count = (TextView) mPopupWindow.getContentView().findViewById(R.id.like_count);
        likes_count.setText(String.valueOf( track.get("like_count")));

        mPopupWindow.showAtLocation(mRelativeLayout, Gravity.BOTTOM, 0, 0);
    }

    //point of interest popup
    private void initMarkerPopup(final Marker marker){
        TextView title = (TextView)mPopupWindow.getContentView().findViewById(R.id.main_content);
        title.setText(marker.getTitle());

        TextView discreption = (TextView)mPopupWindow.getContentView().findViewById(R.id.minor_content);
        discreption.setText(marker.getSnippet());

        Button read_more = (Button) mPopupWindow.getContentView().findViewById(R.id.read_more);
        read_more.setVisibility(View.GONE);

        //add click listeners for editing and removing point of intereset
        delete_coordinate_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogDeleteCoordinate(marker);
            }
        });
        edit_coordinate_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogEditCoordinate(marker);
            }
        });

        mPopupWindow.showAtLocation(mRelativeLayout, Gravity.BOTTOM, 0, 0);

    }

    private void showAllTracks() {
        tracks.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> tracksMap = (Map<String, Object>)dataSnapshot.getValue();
                if(tracksMap == null) {
                    loading_map_progress_bar.setVisibility(View.INVISIBLE);
                    return;
                }

                /*Iterating all the coordinates in the list*/
                for (Map.Entry<String, Object> entry : tracksMap.entrySet()) {
                    /*For each coordinate in the database, we want to create a new marker
                    * for it and to show the marker on the map*/
                    Map<String, Object> track = ((Map<String, Object>) entry.getValue());

                    String route_st = (String)track.get("route");
                    DirectionsRoute route = JsonParserManager.getInstance().retrieveRouteFromJson(route_st);
                    drawRoute(route, false);
                }
                loading_map_progress_bar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                loading_map_progress_bar.setVisibility(View.INVISIBLE);
            }
        });
    }




    private void showAllPointsOfInterest() {
        /*Reading one time from the database, we get the points of interest map list*/
        points_of_interest.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> pointsMap = (Map<String, Object>)dataSnapshot.getValue();
                if(pointsMap == null) {
                    loading_map_progress_bar.setVisibility(View.INVISIBLE);
                    return;
                }
                /*Iterating all the coordinates in the list*/
                for (Map.Entry<String, Object> entry : pointsMap.entrySet())
                {
                    /*For each coordinate in the database, we want to create a new marker
                    * for it and to show the marker on the map*/
                    Map<String, Object> point = ((Map<String, Object>) entry.getValue());
                    /*Now the object 'cor' holds a *map* for a specific coordinate*/
                    String positionJSON = (String) point.get("position");
                    Position position = JsonParserManager.getInstance().retrievePositionFromJson(positionJSON);

                    /*Creating the marker on the map*/
                    LatLng latlng = new LatLng(
                            position.getLongitude(),
                            position.getLatitude());

                    addMarkerForPointOfInterest(latlng, (String)point.get("title"), (String)point.get("snippet"), (String)point.get("type"));
                }
                loading_map_progress_bar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                loading_map_progress_bar.setVisibility(View.INVISIBLE);
            }

        });
    }

    private void showAllUserUpdates() {
        /*Reading one time from the database, we get the points of interest map list*/
        user_updates.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> pointsMap = (Map<String, Object>)dataSnapshot.getValue();
                if(pointsMap == null) {
                    loading_map_progress_bar.setVisibility(View.INVISIBLE);
                    return;
                }
                /*Iterating all the coordinates in the list*/
                for (Map.Entry<String, Object> entry : pointsMap.entrySet())
                {
                    /*For each coordinate in the database, we want to create a new marker
                    * for it and to show the marker on the map*/
                    Map<String, Object> point = ((Map<String, Object>) entry.getValue());
                    /*Now the object 'cor' holds a *map* for a specific coordinate*/
                    String positionJSON = (String) point.get("position");
                    Position position = JsonParserManager.getInstance().retrievePositionFromJson(positionJSON);

                    /*Creating the marker on the map*/
                    LatLng latlng = new LatLng(
                            position.getLongitude(),
                            position.getLatitude());

                    long logo = (long)point.get("logo");
                    addMarkerForUserUpdate(latlng, (String)point.get("title"), (String)point.get("snippet"), (int) logo);
                }
                loading_map_progress_bar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                loading_map_progress_bar.setVisibility(View.INVISIBLE);
            }

        });
    }

    private void showAllCoordinates() {
        /*Reading one time from the database, we get the coordinates map list*/
        coordinates.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> coordinatesMap = (Map<String, Object>)dataSnapshot.getValue();
                if(coordinatesMap == null){
                    return;
                }

                /*Iterating all the coordinates in the list*/
                for (Map.Entry<String, Object> entry : coordinatesMap.entrySet())
                {
                    /*For each coordinate in the database, we want to create a new marker
                    * for it and to show the marker on the map*/
                    Map<String, Object> cor = ((Map<String, Object>) entry.getValue());
                    /*Now the object 'cor' holds a *map* for a specific coordinate*/
                    String positionJSON = (String) cor.get("position");
                    Position position = JsonParserManager.getInstance().retrievePositionFromJson(positionJSON);

                    /*Creating the marker on the map*/
                    LatLng latlng = new LatLng(
                            position.getLongitude(),
                            position.getLatitude());
                    addMarkerForCoordinate(latlng, (String)cor.get("title"), (String)cor.get("snippet"));
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}

        });


    }

    private void getRoute() throws ServicesException {
        MapboxDirections client = new MapboxDirections.Builder()
                .setCoordinates(track_positions)
                .setProfile(DirectionsCriteria.PROFILE_WALKING)
//                .setAccessToken(Mapbox.getInstance(this, getString(R.string.access_token)).getAccessToken())
                .setAccessToken(MapboxAccountManager.getInstance().getAccessToken())
                .build();

        client.enqueueCall(new Callback<DirectionsResponse>() {
            @Override
            public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                // You can get the generic HTTP info about the response

                if (response.body() == null) {
                    return;
                } else if (response.body().getRoutes().size() < 1) {
                    return;
                }

                // Print some info about the route
                currentRoute = response.body().getRoutes().get(0);
                Toast.makeText(
                        EditorPanelActivity.this,
                        "Route is " + currentRoute.getDistance() + " meters long.",
                        Toast.LENGTH_LONG).show();

                // Draw the route on the map
                drawRoute(currentRoute,true);
            }

            @Override
            public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
                Toast.makeText(EditorPanelActivity.this, "Error: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void drawRoute(DirectionsRoute route, boolean newPolyline) {
        // Convert LineString coordinates into LatLng[]
        LineString lineString = LineString.fromPolyline(route.getGeometry(), com.mapbox.services.Constants.OSRM_PRECISION_V5);
        List<Position> coordinates = lineString.getCoordinates();
        LatLng[] points = new LatLng[coordinates.size()];
        for (int i = 0; i < coordinates.size(); i++) {
            points[i] = new LatLng(
                    coordinates.get(i).getLatitude(),
                    coordinates.get(i).getLongitude());
        }

        // Draw Points on MapView
        if(newPolyline){
            routeLine = new PolylineOptions()
                    .add(points)
                    .color(Color.DKGRAY)
                    .width(ROUTE_LINE_WIDTH);
        }
        else if(!newPolyline) {
            routeLine = new PolylineOptions()
                    .add(points)
                    .color(Color.RED)
                    .width(ROUTE_LINE_WIDTH);
        }
        map.addPolyline(routeLine);

    }

    private Polyline getPolyLineFromRoute(DirectionsRoute route){
        LineString lineString = LineString.fromPolyline(route.getGeometry(), com.mapbox.services.Constants.OSRM_PRECISION_V5);
        List<Position> coordinates = lineString.getCoordinates();
        LatLng[] points = new LatLng[coordinates.size()];
        for (int i = 0; i < coordinates.size(); i++) {
            points[i] = new LatLng(
                    coordinates.get(i).getLatitude(),
                    coordinates.get(i).getLongitude());
        }

        // Draw Points on MapView
        routeLine = new PolylineOptions()
                .add(points)
                .color(Color.RED)
                .width(ROUTE_LINE_WIDTH);
        return routeLine.getPolyline();
    }


    private void showDefaultLocation(){
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(DEFAULT_JERUSALEM_COORDINATE.getLatitude(),
                        DEFAULT_JERUSALEM_COORDINATE.getLongitude()), 12));
    }

    private class MyOnMapClickListener implements MapboxMap.OnMapClickListener{
        @Override
        public void onMapClick(@NonNull final LatLng point) {






            if(SHOW_DETAILS_POPUP){
                mPopupWindow.dismiss();

            }

            if(ADD_COORDINATE_MODE)
                dialogAddNewCoordinate(point);
            /*When clicking on a location (which is not a marker!) in the map while
            * being in ADD_COORDINATE_MODE,
            * we want to ask the editor if he wants to add a new coordinate
            * in the database for this specific location*/

            if(ADD_TRACK_MODE && count_coordinates_selected < MAX_NUM_OF_TRACK_COORDINATES) {
                if(count_coordinates_selected == 0){
                    Toast.makeText(EditorPanelActivity.this, R.string.toast_starting_point, Toast.LENGTH_SHORT).show();
                    return;
                }
                else{
                    writeGenericCoordinateToDB(point);
                    MarkerView marker = addMarkerForCoordinate(point, "", "");
                    IconFactory iconFactory = IconFactory.getInstance(EditorPanelActivity.this);
                    Icon icon = iconFactory.fromResource(R.drawable.blue_marker);
                    marker.setIcon(icon);

                    addTrackPoint(point, marker);

                    count_coordinates_selected++;
                    updateScreenCounter();
                }

            }
            else if(ADD_TRACK_MODE && count_coordinates_selected >= MAX_NUM_OF_TRACK_COORDINATES){
                Toast.makeText(EditorPanelActivity.this, R.string.reached_limit_of_coordinates, Toast.LENGTH_SHORT).show();
            }
            else if(!ADD_TRACK_MODE){
                tracks.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Map<String, Object> tracksMap = (Map<String, Object>)dataSnapshot.getValue();
                        if(tracksMap == null) {
                            return;
                        }

                        for (Map.Entry<String, Object> entry : tracksMap.entrySet()) {

                            Map<String, Object> track = ((Map<String, Object>) entry.getValue());

                            String route_st = (String)track.get("route");
                            DirectionsRoute route = JsonParserManager.getInstance().retrieveRouteFromJson(route_st);
                            Polyline polyline = getPolyLineFromRoute(route);


                            for (LatLng polyCoords : polyline.getPoints()) {
                                float[] results = new float[1];
                                Location.distanceBetween(point.getLatitude(), point.getLongitude(),
                                        polyCoords.getLatitude(), polyCoords.getLongitude(), results);

                                if (results[0] < 100 && !SHOW_DETAILS_POPUP) {
                                    // If distance is less than 100 meters, this is your polyline
//                                    Log.e(TAG, "Found @ "+clickCoords.latitude+" "+clickCoords.longitude);
                                    SHOW_DETAILS_POPUP = true;
                                    initTrackPopup(track);
                                }

                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }

        }
    }

    private void removeTrackPoint(Marker marker){
        track_coordinates.remove(marker.getPosition().getLongitude());
        track_coordinates.remove(marker.getPosition().getLatitude());
        track_markers.remove(marker);
        Position temp = Position.fromCoordinates(marker.getPosition().getLongitude(), marker.getPosition().getLatitude());
        track_positions.remove(temp);


    }

    private void updateTrackPoint(int index, Marker marker){
        Marker temp = track_markers.get(index);
        track_markers.set(index,marker);
        for(int i=0; i<track_coordinates.size();i++){
            if(track_coordinates.get(i)==temp.getPosition().getLatitude())
                track_coordinates.set(i,marker.getPosition().getLatitude());
            else if(track_coordinates.get(i)==temp.getPosition().getLongitude())
                track_coordinates.set(i,marker.getPosition().getLongitude());
        }
        Position tempP = Position.fromCoordinates(temp.getPosition().getLongitude(), temp.getPosition().getLatitude());
        Position newP = Position.fromCoordinates(marker.getPosition().getLongitude(), marker.getPosition().getLatitude());
        for(int i=0; i<track_positions.size();i++){
            if(track_positions.get(i).toString().equals(tempP.toString()))
                track_positions.set(i,newP);

        }


    }

    private void addTrackPoint(LatLng point, Marker marker){
        track_coordinates.add(point.getLongitude());
        track_coordinates.add(point.getLatitude());
        track_markers.add(marker);
        Position temp = Position.fromCoordinates(point.getLongitude(), point.getLatitude());
        track_positions.add(temp);
    }

    private MarkerView addMarkerForCoordinate(LatLng point, String title, String snippet) {
        MarkerViewOptions markerViewOptions = new MarkerViewOptions()
                .position(point)
                .title(title)
                .snippet(snippet);
        map.addMarker(markerViewOptions);
        return markerViewOptions.getMarker();
    }

    private MarkerView addMarkerForPointOfInterest(LatLng point, String title, String snippet, String type){
        MarkerViewOptions markerViewOptions = new MarkerViewOptions()
                .position(point)
                .title(title)
                .snippet(snippet);
        map.addMarker(markerViewOptions);

        int logo = (int) (long) PointOfInterest.whatIsTheLogoForType(type);

        if(logo != -1) {
            IconFactory iconFactory = IconFactory.getInstance(EditorPanelActivity.this);
            Icon icon = iconFactory.fromResource(logo);
            markerViewOptions.getMarker().setIcon(icon);
        }
        return markerViewOptions.getMarker();
    }

    private MarkerView addMarkerForUserUpdate(LatLng point, String title, String snippet, int logo){
        MarkerViewOptions markerViewOptions = new MarkerViewOptions()
                .position(point)
                .title(title)
                .snippet(snippet);
        map.addMarker(markerViewOptions);

        if(logo != -1) {
            IconFactory iconFactory = IconFactory.getInstance(EditorPanelActivity.this);
            Icon icon = iconFactory.fromResource(logo);
            markerViewOptions.getMarker().setIcon(icon);
        }
        return markerViewOptions.getMarker();
    }

    private void writeGenericCoordinateToDB(LatLng point) {
        String key = getCoordinateHashKey(point);

        Coordinate coordinate = new Coordinate(
                point.getLongitude(),
                point.getLatitude(),
                "",
                "");

        /*Converting our coordinate object to a map, that makes
        * the coordinate ready to be entered to the JSON tree*/
        Map<String, Object> coordinateMap = coordinate.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(key, coordinateMap);
        coordinates.updateChildren(childUpdates);

    }



    /*Marker clicked listener. We can delete/edit a coordinate*/
    private class MyOnMarkerClickListener implements MapboxMap.OnMarkerClickListener{
        @Override
        public boolean onMarkerClick(@NonNull final Marker marker) {

            if(!ADD_TRACK_MODE) {
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(marker.getPosition().getLatitude(),
                        marker.getPosition().getLongitude()), ZOOM_LEVEL_MARKER_CLICK));

                final String key = getMarkerHashKey(marker);
                points_of_interest.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Map<String, Object> pointsMap = (Map<String, Object>)dataSnapshot.getValue();
                        Map<String, Object> point = ((Map<String, Object>)pointsMap.get(key));
                        if(point != null) {
                            String type = (String) point.get("type");
                            if (!type.equals("תחנת רכבת")) {
                                SHOW_DETAILS_POPUP = true;
                                initMarkerPopup(marker);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                return true;
            }


            if(ADD_TRACK_MODE) {
                for(int i=0; i<track_markers.size(); i++) {


                    /*If the editor clicked a marker that is already part of the track
                    * we want to remove it from the track coordinates array*/
                    if(track_markers.get(i).toString().equals(marker.toString())){
                        dialogEditMarker(marker);
                        markerIndex=i;

                        return true;
                    }
                }

                /*If we arrived this point, we know that the clicked marker wasn't chosen before,
                * that's why we need to add him now to the track coordinates*/
                if(count_coordinates_selected >= MAX_NUM_OF_TRACK_COORDINATES){
                    Toast.makeText(EditorPanelActivity.this, R.string.reached_limit_of_coordinates, Toast.LENGTH_SHORT).show();
                    return true;
                }

                /*We want to make sure that the first chosen coordinate is train station*/
                if(count_coordinates_selected == 0){
                    final String key = getMarkerHashKey(marker);
                    points_of_interest.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Map<String, Object> pointsMap = (Map<String, Object>)dataSnapshot.getValue();
                            if(pointsMap == null) {
                                Toast.makeText(EditorPanelActivity.this, R.string.toast_starting_point, Toast.LENGTH_SHORT).show();
                                return;
                            }
                            Map<String, Object> point = ((Map<String, Object>)pointsMap.get(key));


                            if(point == null){
                                Toast.makeText(EditorPanelActivity.this, R.string.toast_starting_point, Toast.LENGTH_SHORT).show();
                                return;
                            }

                            String type = (String)point.get("type");
                            if(! type.equals("תחנת רכבת")){
                                Toast.makeText(EditorPanelActivity.this, R.string.toast_starting_point, Toast.LENGTH_SHORT).show();
                                return;
                            }

                            IconFactory iconFactory = IconFactory.getInstance(EditorPanelActivity.this);
                            Icon icon = iconFactory.fromResource(R.drawable.blue_marker);

                            addTrackPoint(marker.getPosition(), marker);
                            count_coordinates_selected++;
                            updateScreenCounter();

                            marker.setIcon(icon);


                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    return true;
                }
                else{
                    IconFactory iconFactory = IconFactory.getInstance(EditorPanelActivity.this);
                    Icon icon = iconFactory.fromResource(R.drawable.blue_marker);

                    addTrackPoint(marker.getPosition(),marker);
                    count_coordinates_selected++;
                    updateScreenCounter();

                    marker.setIcon(icon);
                    return true;
                }

            }

            return true;
        }
    }


    private void updateScreenCounter(){
        StringBuilder s = new StringBuilder().append(getResources().getString(R.string.coordinates_selected_txt)).
                append(" "+count_coordinates_selected);
        counter_coordinates.setText(s.toString());
    }


    private void dialogEditMarker(final Marker marker){
        AlertDialog.Builder builder = new AlertDialog.Builder(EditorPanelActivity.this);
        builder.setMessage(getResources().getString(R.string.dialog_change_point));

        builder.setPositiveButton(R.string.dialog_remove_point, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                removePointFromTrack(marker);
            }
        });
        builder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            return;
            }
        });

        builder.setNeutralButton(R.string.dialog_move_point, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                movePointOnMap(marker);
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }




    private void dialogEditTrack(final Map<String,Object> track){
        AlertDialog.Builder builder = new AlertDialog.Builder(EditorPanelActivity.this);
        builder.setMessage(getResources().getString(R.string.dialog_edit_coordinate));

        builder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                Intent i = new Intent(EditorPanelActivity.this, EditTrackActivity.class);
                i.putExtra(TRACK_EDIT, (String)track.get("db_key"));
                startActivity(i);
            }
        });
        builder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                return;
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    /*Method gets the track to delete from the database, gets it's key in
* the db and erase it.*/

    private void dialogDeleteTrack(final Map<String,Object> track){
        AlertDialog.Builder builder = new AlertDialog.Builder(EditorPanelActivity.this);
        builder.setMessage(getResources().getString(R.string.dialog_delete_track));

        builder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                String key = (String) track.get("db_key");
                String route_st = (String)track.get("route");
                DirectionsRoute route = JsonParserManager.getInstance().retrieveRouteFromJson(route_st);
                for(Polyline polyline: map.getPolylines()){
                    if (polyline.getPoints().toString().equals(getPolyLineFromRoute(route).getPoints().toString()))
                    {
                        map.removePolyline(polyline);
                        tracks.child(key).removeValue();
                        return;
                    }
                }
                mPopupWindow.dismiss();
            }
        });
        builder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                return;
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void dialogEditCoordinate(final Marker marker) {
        AlertDialog.Builder builder = new AlertDialog.Builder(EditorPanelActivity.this);
        builder.setMessage(getResources().getString(R.string.dialog_edit_coordinate));

        builder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                /*Sending the key for the edit coordinate activity
                * to be able to update this coordinate in the database*/
                String key = getMarkerHashKey(marker);
                Intent i = new Intent(EditorPanelActivity.this, EditCoordinateActivity.class);
                i.putExtra(COORDINATE_KEY, key);
                startActivityForResult(i, EDIT_COORDINATE);
            }
        });
        builder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                return;
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private String getMarkerHashKey(final Marker marker) {
        double longitude = marker.getPosition().getLongitude();
        //double latitude = chosenCoordinateLatLng.getLatitude();

        int hash = (int) (10000000*longitude);
        return "" + hash;
    }

    private String getCoordinateHashKey(LatLng point) {
        double longitude = point.getLongitude();
        //double latitude = chosenCoordinateLatLng.getLatitude();
        int hash = (int) (10000000*longitude);
        return "" + hash;
    }

    private void dialogDeleteCoordinate(final Marker marker){
        AlertDialog.Builder builder = new AlertDialog.Builder(EditorPanelActivity.this);
        builder.setMessage(getResources().getString(R.string.dialog_delete_coordinate));

        builder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteCoordinateFromDb(marker, true);
            }
        });
        builder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                return;
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /*Method gets the marker to delete from the database, gets it's key in
    * the db and erase it.*/
    private void deleteCoordinateFromDb(final Marker marker, boolean point_of_interest) {
        String key = getMarkerHashKey(marker);
        coordinates.child(key).removeValue();
        if(point_of_interest)
            points_of_interest.child(key).removeValue();
        map.removeMarker(marker);
    }

    /*Dialog function to ask the editor if he wants to add new coordinate with details.
    * if yes, we are directed to the creating new coordinate activity*/
    private void dialogAddNewCoordinate(final LatLng point) {
        AlertDialog.Builder builder = new AlertDialog.Builder(EditorPanelActivity.this);
        builder.setMessage(getResources().getString(R.string.dialog_add_new_coordinate));


        builder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent i = new Intent(EditorPanelActivity.this, CreateNewCoordinateActivity.class);
                i.putExtra(CHOSEN_COORDINATE, JsonParserManager.getInstance().castLatLngToJson(point));
                startActivityForResult(i, NEW_COORDINATE);
            }
        });
        builder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                return;
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }



    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(this, "This app needs location permissions in order to show its functionality.",
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            enableLocation(true);
        } else {
            Toast.makeText(this, "You didn't grant location permissions.",
                    Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void toggleGps(boolean enableGps) {
        if (enableGps) {
            // Check if user has granted location permission
            permissionsManager = new PermissionsManager(this);
            if (!PermissionsManager.areLocationPermissionsGranted(this)) {
                permissionsManager.requestLocationPermissions(this);
            } else {
                enableLocation(true);
            }
        } else {
            enableLocation(false);
            showDefaultLocation();
        }
    }

    private void enableLocation(boolean enabled) {
        if (enabled) {
            // If we have the last location of the user, we can move the camera to that position.
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            Location lastLocation = locationEngine.getLastLocation();
            if (lastLocation != null) {
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lastLocation), ZOOM_LEVEL_CURRENT_LOCATION));
            }

            locationEngineListener = new LocationEngineListener() {
                @Override
                public void onConnected() {
                    // No action needed here.
                }

                @Override
                public void onLocationChanged(Location location) {
                    if (location != null) {
                        // Move the map camera to where the user location is and then remove the
                        // listener so the camera isn't constantly updating when the user location
                        // changes. When the user disables and then enables the location again, this
                        // listener is registered again and will adjust the camera once again.
                        map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location), ZOOM_LEVEL_CURRENT_LOCATION));
                        locationEngine.removeLocationEngineListener(this);
                    }
                }
            };
            locationEngine.addLocationEngineListener(locationEngineListener);
            floatingActionButton.setImageResource(R.drawable.ic_location_disabled_24dp);
        } else {
            floatingActionButton.setImageResource(R.drawable.ic_my_location_24dp);
        }
        // Enable or disable the location layer on the map
        map.setMyLocationEnabled(enabled);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        final MenuInflater inflater = getMenuInflater();

        //user not loged in show login button
        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null)
            inflater.inflate(R.menu.not_signedin_menu, menu);
        else {

            inflater.inflate(R.menu.app_menu, menu);

            MenuItem item = menu.findItem(R.id.spinner);
            Spinner spinner = (Spinner) MenuItemCompat.getActionView(item);

            ArrayList<String> temp = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.spinner_list_item_array)));

            temp.remove(1);


            String uname = currentUser.getDisplayName();
            uid = currentUser.getUid();
            temp.add(0, uname);

            String carArr[] = temp.toArray(new String[temp.size()]);


            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    R.layout.propfile_spinner_item, carArr);
            adapter.setDropDownViewResource(R.layout.propfile_spinner_item);
            spinner.setAdapter(adapter);
            spinner.setSelection(0, false);

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                public void onItemSelected(
                        AdapterView<?> parent, View view, int position, long id) {

                    //user name  nothing to do on click
                    if (position == 0) {

                    }

                    if (position == 1) {
                        signOut();
                    }


                }

                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
        return true;


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent i;
        switch(item.getItemId()){
            case R.id.aboutActivity:
                i = new Intent(this, MoveablePointActivity.class);
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
                i = new Intent(this, AlgoliaSearchActivity.class);
                startActivity(i);
                return true;

            case R.id.makeOwnTrackActivity:
                i = new Intent(this, MakeOwnTrackActivity.class);
                startActivity(i);
                return true;

            case R.id.tracksActivity:
                i = new Intent(this, TracksActivity.class);
                startActivity(i);
                return true;

            case R.id.languageActivity:

                toggleLanguage();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void toggleLanguage() {

        LocaleManager.toggaleLang(this);


        Intent intent = getIntent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        startActivity(intent);

    }



    /*Method signs out user's google account*/
    private void signOut() {
        FirebaseAuth.getInstance().signOut();
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        Intent i = new Intent(EditorPanelActivity.this, HomeActivity.class);
                        startActivity(i);
                    }});
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);

            if(requestCode == NEW_COORDINATE && resultCode == COORDINATE_CREATED){
                LatLng createdCoordinateLatLng = JsonParserManager.getInstance().retreiveLatLngFromJson(data.getStringExtra(CREATED_COORDINATE_FOR_ZOOM));

                String key = hashFunction(createdCoordinateLatLng.getLongitude());
                updateScreenCoordinates(key, createdCoordinateLatLng, NEW_COORDINATE);

                map.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(createdCoordinateLatLng.getLatitude(),
                                createdCoordinateLatLng.getLongitude()), ZOOM_LEVEL_MARKER_CLICK));

            }
            if(requestCode == EDIT_COORDINATE && resultCode == COORDINATE_EDITED){
                LatLng createdCoordinateLatLng = JsonParserManager.getInstance().retreiveLatLngFromJson(data.getStringExtra(EDITED_COORDINATE_FOR_ZOOM));
                String key = hashFunction(createdCoordinateLatLng.getLatitude());
                updateScreenCoordinates(key, createdCoordinateLatLng, EDIT_COORDINATE);

                map.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(createdCoordinateLatLng.getLongitude(),
                                createdCoordinateLatLng.getLatitude()), ZOOM_LEVEL_MARKER_CLICK));
            }

            if(requestCode == NEW_USER_UPDATE && resultCode == USER_UPDATE_CREATED){
                LatLng createdUpdateCoordinateLatLang = JsonParserManager.getInstance().retreiveLatLngFromJson(data.getStringExtra(CREATED_UPDATE_FOR_ZOOM));
                String key = userUpdateHashFunction(createdUpdateCoordinateLatLang.getLatitude(),data.getIntExtra("id",0));
                updateScreenUserUpdatesValue(key, createdUpdateCoordinateLatLang, NEW_USER_UPDATE);

                map.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(createdUpdateCoordinateLatLang.getLongitude(),
                                createdUpdateCoordinateLatLang.getLatitude()), ZOOM_LEVEL_MARKER_CLICK));
            }

        } catch (Exception ex) {
            Toast.makeText(EditorPanelActivity.this, ex.toString(),
                    Toast.LENGTH_SHORT).show();
        }



    }

    private void updateScreenCoordinates(final String key, final LatLng createdCoordinate, final int mode) {
        coordinates.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> coordinatesMap = (Map<String, Object>)dataSnapshot.getValue();
                if(coordinatesMap == null){
                    updateScreenPointOfInterestValue(key, createdCoordinate, mode);
                    return;
                }

                Map<String, Object> cor = ((Map<String, Object>)coordinatesMap.get(key));

                /*if cor is not null, it means that it is a regular coordinate*/
                if(cor != null){
                    if(mode == NEW_COORDINATE){
                        addMarkerForCoordinate(createdCoordinate,
                                (String)cor.get("title"), (String)cor.get("snippet"));
                    }
                    else if(mode == EDIT_COORDINATE){
                        List<Marker> markers = map.getMarkers();
                        for(int i=0; i<markers.size(); i++){
                            if(markers.get(i).getPosition().getLatitude() == createdCoordinate.getLongitude()
                                    && markers.get(i).getPosition().getLongitude() == createdCoordinate.getLatitude()){

                                map.removeMarker(markers.get(i));
                            }
                            LatLng tempLatLng = new LatLng(createdCoordinate.getLongitude(), createdCoordinate.getLatitude());
                            addMarkerForCoordinate(tempLatLng,
                                    (String)cor.get("title"), (String)cor.get("snippet"));
                        }

                    }

                }
                /*If cor is null, we know for sure that it's a point of interest and we should
                * check for it in the other set in database for points*/
                else{
                    updateScreenPointOfInterestValue(key, createdCoordinate, mode);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void updateScreenPointOfInterestValue(final String key, final LatLng createdCoordinate, final int mode) {
        points_of_interest.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> pointsMap = (Map<String, Object>)dataSnapshot.getValue();
                if(pointsMap == null)
                    return;


                Map<String, Object> point = ((Map<String, Object>)pointsMap.get(key));
                if(point == null){
                    Toast.makeText(EditorPanelActivity.this, "point is null", Toast.LENGTH_SHORT).show();
                }

                long logo = (long)point.get("logo");
                if(mode == NEW_COORDINATE){
                    addMarkerForPointOfInterest(createdCoordinate,
                            (String)point.get("title"), (String)point.get("snippet"),
                            (String)point.get("type"));
                }
                else if(mode == EDIT_COORDINATE){
                    List<Marker> markers = map.getMarkers();
                    for(int i=0; i<markers.size(); i++){
                        if(markers.get(i).getPosition().getLatitude() == createdCoordinate.getLongitude()
                                && markers.get(i).getPosition().getLongitude() == createdCoordinate.getLatitude()){

                            map.removeMarker(markers.get(i));
                        }
                        LatLng tempLatLng = new LatLng(createdCoordinate.getLongitude(), createdCoordinate.getLatitude());
                        addMarkerForPointOfInterest(tempLatLng,
                                (String)point.get("title"), (String)point.get("snippet"),
                                (String)point.get("type"));
                    }
                }



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void updateScreenUserUpdatesValue(final String key, final LatLng createdCoordinate, final int mode) {
        user_updates.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> pointsMap = (Map<String, Object>)dataSnapshot.getValue();
                if(pointsMap == null)
                    return;

                Map<String, Object> point = ((Map<String, Object>)pointsMap.get(key));

                if(point == null){
                    Toast.makeText(EditorPanelActivity.this, "point is null", Toast.LENGTH_SHORT).show();
                }

                long logo = (long)point.get("logo");
                if(mode == NEW_USER_UPDATE){
                    addMarkerForUserUpdate(createdCoordinate,
                            (String)point.get("title"), (String)point.get("snippet"),
                            (int)logo);
                }

                map.setInfoWindowAdapter(new MapboxMap.InfoWindowAdapter() {
                    @Nullable
                    @Override
                    public View getInfoWindow(@NonNull Marker marker) {

                        LayoutInflater inflater = (LayoutInflater)   getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View view = inflater.inflate(R.layout.user_update_infowindow, null);

                        return view;
                    }
                });
//                else if(mode == EDIT_COORDINATE){
//                    List<Marker> markers = map.getMarkers();
//                    for(int i=0; i<markers.size(); i++){
//                        if(markers.get(i).getPosition().getLatitude() == createdCoordinate.getLongitude()
//                                && markers.get(i).getPosition().getLongitude() == createdCoordinate.getLatitude()){
//
//                            map.removeMarker(markers.get(i));
//                        }
//                        LatLng tempLatLng = new LatLng(createdCoordinate.getLongitude(), createdCoordinate.getLatitude());
//                        addMarkerForPointOfInterest(tempLatLng,
//                                (String)point.get("title"), (String)point.get("snippet"),
//                                (int)logo);
//                    }
//                }



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void reverseGeocode(final LatLng point) {
        // This method is used to reverse geocode where the user has dropped the marker.
        try {
            MapboxGeocoding client = new MapboxGeocoding.Builder()
                    .setAccessToken(getString(R.string.access_token))
                    .setCoordinates(Position.fromCoordinates(point.getLongitude(), point.getLatitude()))
                    .setGeocodingType(GeocodingCriteria.TYPE_ADDRESS)
                    .build();

            client.enqueueCall(new Callback<GeocodingResponse>() {
                @Override
                public void onResponse(Call<GeocodingResponse> call, Response<GeocodingResponse> response) {

                    List<CarmenFeature> results = response.body().getFeatures();
                    if (results.size() > 0) {
                        CarmenFeature feature = results.get(0);
                        // If the geocoder returns a result, we take the first in the list and update
                        // the dropped marker snippet with the information. Lastly we open the info
                        // window.
                        if (droppedMarker != null) {
                            droppedMarker.setSnippet(feature.getPlaceName());
                            //   mapboxMap.selectMarker(droppedMarker);
                        }

                    } else {
                        if (droppedMarker != null) {
                            droppedMarker.setSnippet("");

                        }
                    }
                }

                @Override
                public void onFailure(Call<GeocodingResponse> call, Throwable throwable) {
                    Log.e("Moveable", "Geocoding Failure: " + throwable.getMessage());
                }
            });
        } catch (ServicesException servicesException) {
            Log.e("Moveable", "Error geocoding: " + servicesException.toString());
            servicesException.printStackTrace();
        }
    }


    private void movePointOnMap(Marker marker){
        PointF point = map.getProjection().toScreenLocation(marker.getPosition());

        hoveringMarker.setX(point.x-(hoveringMarker.getWidth()/2));
        hoveringMarker.setY(point.y-hoveringMarker.getHeight());

        hoveringMarker.setVisibility(View.VISIBLE);
        droppedMarker = marker;
    }


    private void removePointFromTrack(Marker marker){
        removeTrackPoint(marker);
        count_coordinates_selected--;
        updateScreenCounter();
        addMarkerForCoordinate(marker.getPosition(), marker.getTitle(), marker.getSnippet());

        map.removeMarker(marker);
    }

    private String hashFunction(double value) {
        //double latitude = chosenCoordinateLatLng.getLatitude();

        int hash = (int) (10000000*value);
        return "" + hash;
    }

    private String userUpdateHashFunction(double value, int id) {
        //double latitude = chosenCoordinateLatLng.getLatitude();

        int hash = ((int) (10000000*value))+id;
        return "" + hash;
    }

    @Override
    public void onBackPressed() {
        if(SHOW_DETAILS_POPUP)
        {

            mPopupWindow.dismiss();
//            mPopupWindow = null;
            showDefaultLocation();
        }
        else {

            new AlertDialog.Builder(this)
                    .setTitle(R.string.dialog_exit_app_title)
                    .setMessage(R.string.dialog_exit_app_body)
                    .setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                           /*Exits the application*/
                            Intent intent = new Intent(Intent.ACTION_MAIN);
                            intent.addCategory(Intent.CATEGORY_HOME);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);

                        }
                    })

                    .setNegativeButton(R.string.dialog_cancel, null)
                    .show();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }




}
