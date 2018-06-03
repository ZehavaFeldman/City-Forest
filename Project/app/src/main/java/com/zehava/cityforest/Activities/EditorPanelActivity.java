package com.zehava.cityforest.Activities;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PointF;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateUtils;
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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.RatingBar;
import android.widget.LinearLayout;

//import com.mapbox.mapboxsdk.Mapbox;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.login.LoginManager;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.MapboxDirections;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.api.directions.v5.models.DirectionsWaypoint;
import com.mapbox.geojson.Point;
//import com.mapbox.mapboxsdk.MapboxAccountManager;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;


import com.mapbox.services.api.ServicesException;
import com.mapbox.services.api.geocoding.v5.GeocodingCriteria;
import com.mapbox.services.api.geocoding.v5.MapboxGeocoding;
import com.mapbox.services.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.services.api.geocoding.v5.models.GeocodingResponse;
import com.zehava.cityforest.DragAndDrop;
import com.zehava.cityforest.ICallback;
import com.zehava.cityforest.Managers.IconManager;
import com.zehava.cityforest.Managers.JsonParserManager;
import com.zehava.cityforest.Managers.LocaleManager;
import com.zehava.cityforest.Managers.PMethods;
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
import com.mapbox.services.commons.geojson.LineString;
import com.mapbox.services.commons.models.Position;

import com.zehava.cityforest.Models.Image;
import com.zehava.cityforest.Models.PointOfInterest;
import com.zehava.cityforest.MoveablePointActivity;
import com.zehava.cityforest.R;
import com.zehava.cityforest.UpdatesManagerService;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
import static com.zehava.cityforest.Constants.CURRENT_USER_NAME;
import static com.zehava.cityforest.Constants.DEFAULT_JERUSALEM_COORDINATE;
import static com.zehava.cityforest.Constants.DELETE_COORDINATE_MODE;
import static com.zehava.cityforest.Constants.EDITED_COORDINATE_FOR_ZOOM;
import static com.zehava.cityforest.Constants.EDIT_COORDINATE;
import static com.zehava.cityforest.Constants.EDIT_COORDINATE_MODE;
import static com.zehava.cityforest.Constants.FINISH_EDIT_TRACK_MODE;
import static com.zehava.cityforest.Constants.FROM_ADD_TRACK;
import static com.zehava.cityforest.Constants.IMAGE_NAME;
import static com.zehava.cityforest.Constants.LAST_LOCATION_SAVED;
import static com.zehava.cityforest.Constants.MAX_NUM_OF_TRACK_COORDINATES;
import static com.zehava.cityforest.Constants.MOVE_MARKER;
import static com.zehava.cityforest.Constants.NEW_COORDINATE;
import static com.zehava.cityforest.Constants.NEW_TRACK;
import static com.zehava.cityforest.Constants.NEW_USER_UPDATE;
import static com.zehava.cityforest.Constants.PICK_IMAGE_REQUEST;
import static com.zehava.cityforest.Constants.ROUTE_LINE_WIDTH;
import static com.zehava.cityforest.Constants.SET_FROM_PREFS;
import static com.zehava.cityforest.Constants.SHOW_DETAILS_POPUP;
import static com.zehava.cityforest.Constants.SHOW_TRACK_POPUP;
import static com.zehava.cityforest.Constants.TRACK_EDIT;
import static com.zehava.cityforest.Constants.TRACK_ENDING_POINT;
import static com.zehava.cityforest.Constants.TRACK_ENDING_POINT_NAME;
import static com.zehava.cityforest.Constants.TRACK_STARTING_POINT;
import static com.zehava.cityforest.Constants.TRACK_STARTING_POINT_NAME;
import static com.zehava.cityforest.Constants.TRACK_WAYPOINTS;
import static com.zehava.cityforest.Constants.UPDATE_POSITION;
import static com.zehava.cityforest.Constants.USER_UPDATE_CREATED;
import static com.zehava.cityforest.Constants.ZOOM_LEVEL_CURRENT_LOCATION;
import static com.zehava.cityforest.Constants.ZOOM_LEVEL_MARKER_CLICK;
import static com.mapbox.services.android.telemetry.location.AndroidLocationEngine.getLocationEngine;

public class EditorPanelActivity extends AppCompatActivity implements PermissionsListener , ICallback {

    private MapView mapView;
    private MapboxMap map;
    private DirectionsRoute currentRoute;
    private List<DirectionsWaypoint> currentRouteWaypoints;
    private FirebaseDatabase database;
    private DatabaseReference coordinates;
    private DatabaseReference tracks;
    private DatabaseReference points_of_interest;
    private DatabaseReference user_updates, usersRef, images;
    private ImageButton add_coordinate_button;
    private ImageButton delete_coordinate_button;
    private ImageButton edit_coordinate_button;
    private ImageButton add_track_button;
    private Button finish_edit_track_butt;
    private Button cancale_track_butt;
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
    private ArrayList<LatLng> track_positions = new ArrayList<>();
    Map<String,String> dirUpdates;

    private FloatingActionButton floatingActionButton;
    private FloatingActionButton createUserUpdate;
    private FloatingActionButton camera, cancle;
    private FloatingActionButton uploadImage, showAllImages;
    private LocationEngine locationEngine;
    private LocationEngineListener locationEngineListener;
    private PermissionsManager permissionsManager;

    private GoogleApiClient mGoogleApiClient;

    private ProgressBar loading_map_progress_bar;
    private PopupWindow mPopupWindow, mTrackPopupWindow;
    private RelativeLayout mRelativeLayout;

    /*marker and imageview nedded for fragging points on map*/
    private Marker droppedMarker;
    private DragAndDrop hoveringMarker;

    private Marker featureMarker;
    String touchedmarkertype;
    boolean markerIsPointOfInterest;

    private HashMap<String, String> stations = new HashMap();
    private HashMap<String,String> tracksPointsOfInterest = new HashMap();
    //private HashMap<String,String> points_for_track = new HashMap();

    String uid,uname,userhash, owner;

    private PolylineOptions currRouteLine, routeLine;
    DirectionsRoute currRoute;
    TextView title;
    TextView update_owner;
    TextView discreption;
    TextView username;
    Button track_details;
    Button read_more;
    ImageView pointOfInterestImageView;
    Uri filePath;
    Map<String,Map<String,Marker>> points_for_track;
    Map<String,Object> u_update;

    Boolean expand=false;
    Boolean expandable=false;

    Intent serviceIntent;
    UpdatesManagerService myService;
    boolean mBound = false;

    private StorageReference reallImgref;
    private FirebaseStorage storage;
    private StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*hide status bar and app label in action bar*/
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        /*Mapbox initializations*/
        //MapboxAccountManager.start(this,getString(R.string.access_token));
        Mapbox.getInstance(this, getString(R.string.access_token));
        setContentView(R.layout.activity_editor_panel);

        // Get the location engine object for later use.
        locationEngine = getLocationEngine(this);
        locationEngine.activate();

        IconManager.getInstance().generateIcons(IconFactory.getInstance(this));


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
        usersRef = database.getReference("users");
        images = database.getReference("storage_images");

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();


        /*Buttons for different edit map modes*/
        add_coordinate_button = findViewById(R.id.addCoordinateButt);
        add_track_button = findViewById(R.id.addTrackButt);
        loading_map_progress_bar = findViewById(R.id.loadingMapProgress);
        finish_edit_track_butt = findViewById(R.id.finishEditTrack);
        cancale_track_butt = findViewById(R.id.cancleTrack);
        save_track = findViewById(R.id.saveTrack);
        continue_editing = findViewById(R.id.continueEditTrack);
        counter_coordinates = findViewById(R.id.counterCoordinates);
        mRelativeLayout = findViewById(R.id.mapLayout);

        ClickListener clickListener = new ClickListener();
        add_coordinate_button.setOnClickListener(clickListener);
        add_track_button.setOnClickListener(clickListener);
        finish_edit_track_butt.setOnClickListener(clickListener);
        cancale_track_butt.setOnClickListener(clickListener);
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
        cancale_track_butt.setVisibility(View.INVISIBLE);
        continue_editing.setVisibility(View.INVISIBLE);

        floatingActionButton = (FloatingActionButton) findViewById(R.id.location_toggle_fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (map != null) {
                    showCurrentLocation(false);
                }
            }
        });

        createUserUpdate = (FloatingActionButton) findViewById(R.id.user_update_fab);
        createUserUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(EditorPanelActivity.this, CreateUserUpdateActivity.class);
                i.putExtra(CURRENT_USER_NAME, userhash);

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
        points_for_track = new HashMap<>();
        dirUpdates = new HashMap<>();
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
        if(currentUser != null) {
            uid = currentUser.getUid();
            userhash = uid;
        }

        serviceIntent = new Intent(EditorPanelActivity.this, UpdatesManagerService.class);
        bindService(serviceIntent, mConnection, Context.BIND_AUTO_CREATE); //Binding to the service!
//        else{
//            uid = "ZsZQiCJB1ORBJZF5EhTbHj2UpYm2";
//            userhash = uid;
//        }

        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(mConnection);
        mBound = false;
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
                droppedMarker.setIcon(IconManager.getInstance().getIconForEdit());
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
    public void onUserUpdateNotify(USER_UPDATES_CLASS userUpdatesClass, ArrayList<Marker> updateMarkers) {
        //new user updates added to database, add markers to map
        if(userUpdatesClass == USER_UPDATES_CLASS.UPDATE_CREATED) {
            for (Marker marker: updateMarkers) {

                MarkerViewOptions markerViewOptions = new MarkerViewOptions()
                        .position(marker.getPosition())
                        .title(marker.getTitle())
                        .snippet(marker.getSnippet());
                map.addMarker(markerViewOptions);

                markerViewOptions.getMarker().setIcon(marker.getIcon());

            }
        }
        //remove old updates from map
        else if(userUpdatesClass == USER_UPDATES_CLASS.UPDATE_REMOVED){

            if(map != null) {
                Iterator<Marker> allMarkersIterator = map.getMarkers().iterator();

                while (allMarkersIterator.hasNext()) {
                    Marker markerItemAll = allMarkersIterator.next();

                    for (Marker markerItemRemove : updateMarkers) {
                        if (markerItemAll.toString().equals(markerItemRemove.toString())) {
                            allMarkersIterator.remove();
                            map.removeMarker(markerItemAll);
                        }
                    }
                }
            }

        }

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
                    //ADD_TRACK_MODE = false;
                    SHOW_DETAILS_POPUP = false;
                    SHOW_TRACK_POPUP = false;

                    add_coordinate_button.setBackgroundResource(R.drawable.selected_btn);
                    //add_track_button.setBackgroundResource(R.drawable.not_selected_btn);
                }
            }

            if(v.getId() == add_track_button.getId()){
                if(ADD_TRACK_MODE){
                    cancaleTrack();
                }
                else{
                    ADD_COORDINATE_MODE = false;
                    SHOW_DETAILS_POPUP = false;
                    SHOW_TRACK_POPUP = false;
                    ADD_TRACK_MODE = true;

                    add_coordinate_button.setBackgroundResource(R.drawable.not_selected_btn);
                    add_track_button.setBackgroundResource(R.drawable.selected_btn);


                    updateScreenCounter();
                    counter_coordinates.setVisibility(View.VISIBLE);
                    finish_edit_track_butt.setVisibility(View.VISIBLE);
                    cancale_track_butt.setVisibility(View.VISIBLE);
                    //add_coordinate_button.setClickable(false);

                }
            }
//            /*If editor clicked this button, we need to show him the resulted track by creating
//            * the route object and save it as the class's property*/
            if(v.getId() == finish_edit_track_butt.getId()){
                ADD_COORDINATE_MODE = false;
                if(track_markers.size() < 2){
                    Toast.makeText(EditorPanelActivity.this, R.string.not_enough_coordinates_selected, Toast.LENGTH_SHORT).show();
                    return;
                }

//                final String last_marker_key = getMarkerHashKey(track_markers.get(track_markers.size()-1));
//                points_of_interest.addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        Map<String, Object> pointsMap = (Map<String, Object>)dataSnapshot.getValue();
//                        if(pointsMap == null) {
//                            Toast.makeText(EditorPanelActivity.this, R.string.toast_ending_point, Toast.LENGTH_SHORT).show();
//                            return;
//                        }
//                        Map<String, Object> point = ((Map<String, Object>)pointsMap.get(last_marker_key));
//
//                        if(point == null){
//                            Toast.makeText(EditorPanelActivity.this, R.string.toast_ending_point, Toast.LENGTH_SHORT).show();
//                            return;
//                        }
//
//                        String type = (String)point.get("type");
//                        if(!checkIfTransportaionSation(type)){
//                            Toast.makeText(EditorPanelActivity.this, R.string.toast_ending_point, Toast.LENGTH_SHORT).show();
//                            return;
//                        }

                        //here we turn off ADD_TRACK_MODE
                        ADD_TRACK_MODE = false;
                        SHOW_DETAILS_POPUP = false;
                        SHOW_TRACK_POPUP = false;
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


//                    }
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
              //  });
            }
            if(v.getId() == continue_editing.getId()){
                //here we turn on ADD_TRACK_MODE
                ADD_TRACK_MODE = true;
                ADD_COORDINATE_MODE = false;
                add_track_button.setClickable(true);
                save_track.setVisibility(View.INVISIBLE);
                continue_editing.setVisibility(View.INVISIBLE);
                finish_edit_track_butt.setVisibility(View.VISIBLE);
                cancale_track_butt.setVisibility(View.VISIBLE);


                map.removePolyline(routeLine.getPolyline());
            }
            if(v.getId() == cancale_track_butt.getId()){

                cancaleTrack();
            }
            if(v.getId() == save_track.getId()){


                Intent i = new Intent(EditorPanelActivity.this, CreateNewTrackActivity.class);
                i.putExtra(CHOSEN_TRACK, JsonParserManager.getInstance().castRouteToJson(currentRoute));
                i.putExtra(TRACK_STARTING_POINT, JsonParserManager.getInstance().castLatLngToJson(track_markers.get(0).getPosition()));
                i.putExtra(TRACK_ENDING_POINT, JsonParserManager.getInstance().castLatLngToJson(track_markers.get(track_markers.size()-1).getPosition()));
                i.putExtra(TRACK_STARTING_POINT_NAME, track_markers.get(0).getTitle());
                i.putExtra(TRACK_ENDING_POINT_NAME, track_markers.get(track_markers.size()-1).getTitle());
                i.putExtra(CURRENT_USER_NAME, userhash);
                i.putStringArrayListExtra(TRACK_WAYPOINTS,JsonParserManager.getInstance().castListToJson(currentRouteWaypoints));
                i.putExtra("points", tracksPointsOfInterest);
                startActivityForResult(i, NEW_TRACK);
            }

        }
    }

    private void cancaleTrack(){
        ADD_TRACK_MODE = false;
        ADD_COORDINATE_MODE = false;
        add_track_button.setBackgroundResource(R.drawable.not_selected_btn);

                    /*
                    Passing on track_coordinates markers
                    * and change their color back to red.
                    * And clearing both arrays for the next time building a new track*/
        tracksPointsOfInterest.clear();

        for(int i=0; i<track_markers.size(); i++){
            final Marker marker = track_markers.get(i);
            if(stations.containsKey(marker.getTitle()))
                marker.setIcon(IconManager.getInstance().getIconForType(stations.get(marker.getTitle())));

            else{
                DatabaseReference chldRef = points_of_interest.child(getMarkerHashKey(marker));
                chldRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Map<String, Object> point = (Map<String, Object>)dataSnapshot.getValue();
                        if(point == null)
                            marker.setIcon(IconManager.getInstance().getIconForType("default"));
                        else marker.setIcon(IconManager.getInstance().getIconForType((String)point.get("type")));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }

        }
        track_coordinates.clear();
        track_markers.clear();
        track_positions.clear();
        count_coordinates_selected = 0;

        if(continue_editing.getVisibility() == View.VISIBLE)
            map.removePolyline(routeLine.getPolyline());

        counter_coordinates.setVisibility(View.INVISIBLE);
        finish_edit_track_butt.setVisibility(View.INVISIBLE);
        save_track.setVisibility(View.INVISIBLE);
        cancale_track_butt.setVisibility(View.INVISIBLE);
        continue_editing.setVisibility(View.INVISIBLE);

        add_coordinate_button.setClickable(true);
        add_track_button.setClickable(true);


    }

    private class myOnMapReadyCallback implements OnMapReadyCallback {
        @Override
        public void onMapReady(MapboxMap mapboxMap) {
            map = mapboxMap;
            map.setOnMapClickListener(new MyOnMapClickListener());
            map.getMarkerViewManager().setOnMarkerViewClickListener(new MyOnMarkerClickListener());
            map.setStyleUrl(Style.OUTDOORS);
//            showDefaultLocation();
            //showCurrentLocation(!map.isMyLocationEnabled());
            setCameraPositionFromPrefs();
           // showAllUserUpdates();
            showAllPointsOfInterest();
            showAllTracks();

            if(mBound) {
                myService.startCounter();
            }
        }
    }

    //detect when UserManagerService is connected and bind activity to service
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
//            Toast.makeText(EditorPanelActivity.this, "onServiceConnected called", Toast.LENGTH_SHORT).show();
            // We've binded to LocalService, cast the IBinder and get LocalService instance
            UpdatesManagerService.LocalBinder binder = (UpdatesManagerService.LocalBinder) service;
            myService = binder.getServiceInstance(); //Get instance of your service!
            myService.registerClient(EditorPanelActivity.this); //Activity register in the service as client for callabcks!
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            //Toast.makeText(EditorPanelActivity.this, "onServiceDisconnected called dis", Toast.LENGTH_SHORT).show();
            mBound = false;
        }
    };

    // details window shown to user when clicked on marker or route

    private void initPopupWindow(){

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);


        mTrackPopupWindow = new PopupWindow(
                inflater.inflate(R.layout.track_details_popup, null),
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        if(Build.VERSION.SDK_INT>=21){
            mTrackPopupWindow.setElevation(5.0f);
        }

        mTrackPopupWindow.setOutsideTouchable(true);
        mTrackPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if(!SHOW_DETAILS_POPUP) {
                    for (Object o : points_for_track.entrySet()) {
                        Map.Entry pair = (Map.Entry) o;
                        HashMap<String,Marker> child = (HashMap<String,Marker>)pair.getValue();
                        for(Object c: child.entrySet()) {
                            Map.Entry cpair = (Map.Entry) c;
                            Marker marker = (Marker) cpair.getValue();
                            marker.setIcon(IconManager.getInstance().getIconForType((String)pair.getKey()));
                        }

                    }
                    map.removePolyline(currRouteLine.getPolyline());
                    drawRoute(currRoute,Color.RED);

                    SHOW_TRACK_POPUP = false;
                    //showDefaultLocation();
                    showCurrentLocation(true);
                }

            }
        });


        mPopupWindow = new PopupWindow(
                inflater.inflate(R.layout.coordinate_details_popup, null),
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        if(Build.VERSION.SDK_INT>=21){
            mPopupWindow.setElevation(5.0f);
        }

        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {

                SHOW_DETAILS_POPUP = false;
                if(!points_for_track.isEmpty()){
                    //SHOW_TRACK_POPUP = true;
                    mTrackPopupWindow.showAtLocation(mRelativeLayout, Gravity.BOTTOM, 0, 0);
                }
                //showDefaultLocation();
                showCurrentLocation(true);

            }
        });

    }


    private void initTrackPopup(final  Map<String, Object> track, final String userHashkey){

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.track_details_popup, null);
        mTrackPopupWindow.setContentView(view);

        title = mTrackPopupWindow.getContentView().findViewById(R.id.main_content);
        discreption = mTrackPopupWindow.getContentView().findViewById(R.id.minor_content);
//        track_details = mTrackPopupWindow.getContentView().findViewById(R.id.goto_full_des);
        read_more = mTrackPopupWindow.getContentView().findViewById(R.id.read_more);

        title.setText(track.get("track_name").toString());
        discreption.setText(track.get("additional_info").toString());


//        track_details.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(EditorPanelActivity.this, SelectedTrackDetailsActivity.class);
//                intent.putExtra(SELECTED_TRACK, String.valueOf(track.get("db_key")));
//                startActivity(intent);
//            }
//        });

        delete_coordinate_button = (ImageButton)mTrackPopupWindow.getContentView().findViewById(R.id.deleteCoordinateButt);
        edit_coordinate_button = (ImageButton)mTrackPopupWindow.getContentView().findViewById(R.id.editCoordinateButt);

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


        read_more.setVisibility(View.INVISIBLE);
        read_more.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (!expand) {
                    expand = true;
                    ObjectAnimator animation = ObjectAnimator.ofInt(discreption, "maxLines", 40);
                    animation.setDuration(100).start();
                    read_more.setText(getString(R.string.read_less_butt));
                } else {
                    expand = false;
                    ObjectAnimator animation = ObjectAnimator.ofInt(discreption, "maxLines",2);
                    animation.setDuration(100).start();
                    read_more.setText(getString(R.string.read_more_butt));
                }

            }
        });


        discreption.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if(expandable) {
                    expandable = false;
                    if (discreption.getLineCount() > 2) {
                        read_more.setVisibility(View.VISIBLE);
                        ObjectAnimator animation = ObjectAnimator.ofInt(discreption, "maxLines", 2);
                        animation.setDuration(0).start();
                    }
                }
            }
        });

        username = mTrackPopupWindow.getContentView().findViewById(R.id.user_id);
        DatabaseReference childref = usersRef.child(userHashkey);

        childref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> userMap = (Map<String, Object>)dataSnapshot.getValue();
                if(userMap == null) {
                    return;
                }


                username.setText((String) userMap.get("email"));
                username.setOnClickListener(new View.OnClickListener(){
                    public void onClick(View v){
                        Intent facebookIntent = getOpenFacebookIntent(userHashkey);
                        startActivity(facebookIntent);
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        RatingBar ratingBar = (RatingBar)mTrackPopupWindow.getContentView().findViewById(R.id.ratingBar);
        ratingBar.setVisibility(View.VISIBLE);

        //editor can only view current rating
        ratingBar.setIsIndicator(true);
        Float d = Float.valueOf(track.get("star_count").toString());
        Object o = (Object) d;
        ratingBar.setRating((float) o);


        LinearLayout likesWrp = (LinearLayout) mTrackPopupWindow.getContentView().findViewById(R.id.like_wrp);
        likesWrp.setVisibility(View.VISIBLE);

        likes_count = (TextView) mTrackPopupWindow.getContentView().findViewById(R.id.like_count);
        likes_count.setText(String.valueOf( track.get("like_count")));

        mTrackPopupWindow.showAtLocation(mRelativeLayout, Gravity.BOTTOM, 0, 0);
    }

    private void initMarkerPopup(final Marker marker, final String type, final String imageUUID, final String userHashkey){
        expand=false;
        expandable=true;

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.coordinate_details_popup, null);
        mPopupWindow.setContentView(view);

        title = mPopupWindow.getContentView().findViewById(R.id.main_content);
        discreption = mPopupWindow.getContentView().findViewById(R.id.minor_content);
        username = mPopupWindow.getContentView().findViewById(R.id.user_id);
        read_more = mPopupWindow.getContentView().findViewById(R.id.read_more);
        camera = mPopupWindow.getContentView().findViewById(R.id.camera);
        cancle = mPopupWindow.getContentView().findViewById(R.id.cancle);
        showAllImages = mPopupWindow.getContentView().findViewById(R.id.show_all_imges);
        uploadImage = mPopupWindow.getContentView().findViewById(R.id.upload_imge);
        pointOfInterestImageView = mPopupWindow.getContentView().findViewById(R.id.firebase_storage_image);

        final String imageName = PMethods.getInstance().getMarkerHashKey(marker);
        if(imageUUID != null) {
            reallImgref = storageReference.child(imageUUID);
        }

        if(cancle != null && imageUUID != null) {
            cancle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Glide.with(EditorPanelActivity.this /* context */)
                            .using(new FirebaseImageLoader())
                            .load(reallImgref)
                            .listener(new RequestListener<StorageReference, GlideDrawable>() {
                                @Override
                                public boolean onException(Exception e, StorageReference model, Target<GlideDrawable> target, boolean isFirstResource) {
                                    mPopupWindow.showAtLocation(mRelativeLayout, Gravity.BOTTOM, 0, 0);
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(GlideDrawable resource, StorageReference model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                    mPopupWindow.showAtLocation(mRelativeLayout, Gravity.BOTTOM, 0, 0);
                                    return false;
                                }
                            })
                            .into(pointOfInterestImageView);
                    cancle.setVisibility(View.INVISIBLE);
                    uploadImage.setVisibility(View.INVISIBLE);
                    showAllImages.setVisibility(View.VISIBLE);
                    camera.setVisibility(View.VISIBLE);
                    showAllImages.setVisibility(View.VISIBLE);
                }

            });
        }

        if(camera != null) {
            camera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
                }
            });
        }

        if(showAllImages != null){
            showAllImages.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(EditorPanelActivity.this, PointOfInterestGallary.class);
                    intent.putExtra(IMAGE_NAME, PMethods.getInstance().getMarkerHashKey(marker));
                    startActivity(intent);
                }
            });
        }

        if(uploadImage != null) {
            uploadImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PointOfInterest pointOfInterest = new PointOfInterest(
                            marker.getPosition().getLongitude(),
                            marker.getPosition().getLatitude(),
                            marker.getTitle(),
                            marker.getSnippet(),
                            type,
                            userHashkey);
                    uploadImage(PMethods.getInstance().getMarkerHashKey(marker), pointOfInterest);
                }
            });
        }
        title.setText(marker.getTitle());
        discreption.setText(marker.getSnippet());

        DatabaseReference childref = usersRef.child(userHashkey);

        childref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> userMap = (Map<String, Object>)dataSnapshot.getValue();
                if(userMap == null) {
                    return;
                }


                username.setText((String) userMap.get("email"));
                username.setOnClickListener(new View.OnClickListener(){
                    public void onClick(View v){
                        Intent facebookIntent = getOpenFacebookIntent(userHashkey);
                        startActivity(facebookIntent);
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        read_more.setVisibility(View.INVISIBLE);
        read_more.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (!expand) {
                    expand = true;
                    ObjectAnimator animation = ObjectAnimator.ofInt(discreption, "maxLines", 40);
                    animation.setDuration(100).start();
                    read_more.setText(getString(R.string.read_less_butt));
                } else {
                    expand = false;
                    ObjectAnimator animation = ObjectAnimator.ofInt(discreption, "maxLines",2);
                    animation.setDuration(100).start();
                    read_more.setText(getString(R.string.read_more_butt));
                }

            }
        });


        discreption.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if(expandable) {
                    expandable = false;
                    if (discreption.getLineCount() > 2) {
                        read_more.setVisibility(View.VISIBLE);
                        ObjectAnimator animation = ObjectAnimator.ofInt(discreption, "maxLines", 2);
                        animation.setDuration(0).start();
                    }
                }
            }
        });

        delete_coordinate_button = mPopupWindow.getContentView().findViewById(R.id.deleteCoordinateButt);
        edit_coordinate_button = mPopupWindow.getContentView().findViewById(R.id.editCoordinateButt);

        //add click listeners for editing and removing point of intereset
        delete_coordinate_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogDeleteCoordinate(marker, type);
            }
        });
        edit_coordinate_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogEditCoordinate(marker);
            }
        });


        if(imageUUID != null) {
            Glide.with(EditorPanelActivity.this /* context */)
                    .using(new FirebaseImageLoader())
                    .load(reallImgref)
                    .listener(new RequestListener<StorageReference, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, StorageReference model, Target<GlideDrawable> target, boolean isFirstResource) {
                            mPopupWindow.showAtLocation(mRelativeLayout, Gravity.BOTTOM, 0, 0);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, StorageReference model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            mPopupWindow.showAtLocation(mRelativeLayout, Gravity.BOTTOM, 0, 0);
                            return false;
                        }
                    })
                    .into(pointOfInterestImageView);
        }
        else{
            if(pointOfInterestImageView != null)
                pointOfInterestImageView.setImageResource(android.R.color.transparent);
            mPopupWindow.showAtLocation(mRelativeLayout, Gravity.BOTTOM, 0, 0);
        }

        if(pointOfInterestImageView != null) {
            pointOfInterestImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent imagePreview = new Intent(EditorPanelActivity.this, ImageFullScreenPreview.class);
                    imagePreview.putExtra(IMAGE_NAME, imageUUID);
                    startActivity(imagePreview);

                }
            });
        }

//        mPopupWindow.showAtLocation(mRelativeLayout, Gravity.BOTTOM, 0, 0);

    }


    private void initUpdateInfoWindow(final Marker update){

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.user_update_infowindow, null);
        mPopupWindow.setContentView(view);

        final String key = PMethods.getInstance().getUserUpdateHashKey(update);
        final String dirkey = PMethods.getInstance().getMarkerHashKey(update);
        user_updates.child(dirkey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> updates = (Map<String, Object>)dataSnapshot.getValue();

                u_update = (Map<String,Object>)updates.get(key);
                if(u_update!=null){

                    long time;
                    TextView title = mPopupWindow.getContentView().findViewById(R.id.update_main_content);
                    title.setText(update.getTitle());

                    TextView update_created = mPopupWindow.getContentView().findViewById(R.id.created);
                    update_owner= mPopupWindow.getContentView().findViewById(R.id.owner);
                    if(u_update.get("uuid")!=null ) {

                        DatabaseReference childref = usersRef.child((String) u_update.get("uuid"));

                        childref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Map<String, Object> userMap = (Map<String, Object>) dataSnapshot.getValue();
                                if (userMap == null) {
                                    return;
                                }

                                update_owner.setText((String) userMap.get("name"));
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Toast.makeText(EditorPanelActivity.this, getString(R.string.database_error),
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    if( u_update.get("updated")!=null){
                        time = ((Number) u_update.get("updated")).longValue() * 1000;
                        update_created.setText(DateUtils.getRelativeTimeSpanString(time));


                    }

                    ImageView img = mPopupWindow.getContentView().findViewById(R.id.img);
                    int res = IconManager.getInstance().getResourceFrType(u_update.get("type").toString());
                    if(res != -1)
                        img.setBackgroundResource(IconManager.getInstance().getResourceFrType(u_update.get("type").toString()));
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(EditorPanelActivity.this, getString(R.string.database_error),
                        Toast.LENGTH_SHORT).show();
            }
        });

        TextView discreption = mPopupWindow.getContentView().findViewById(R.id.update_minor_content);
        discreption.setText(update.getSnippet());



        Button not_hare =  mPopupWindow.getContentView().findViewById(R.id.not_here);
        not_hare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean enableGps = !map.isMyLocationEnabled();
                if (enableGps) {
                    // Check if user has granted location permission
                    permissionsManager = new PermissionsManager(EditorPanelActivity.this);
                    if (!PermissionsManager.areLocationPermissionsGranted(EditorPanelActivity.this)) {
                        permissionsManager.requestLocationPermissions(EditorPanelActivity.this);
                    } else {
                        Location lastLocation = getLastLocation();
                        if(lastLocation!= null) {
                            LatLng currentLoc = new LatLng(lastLocation);
                            LatLng updateLoc = new LatLng(update.getPosition());
                            if(currentLoc.distanceTo(updateLoc) <= 50.0){
                                user_updates.child(dirkey).child(key).removeValue();
                                Iterator<Marker> allMarkersIterator = map.getMarkers().iterator();

                                while(allMarkersIterator.hasNext()) {
                                    Marker markerItemAll = allMarkersIterator.next();

                                    if (PMethods.getInstance().getUserUpdateHashKey(markerItemAll).equals(key)) {
                                        allMarkersIterator.remove();
                                        // map.removeMarker(markerItemAll);
                                        myService.removeMarkerFromActivity(markerItemAll);
                                        break;
                                    }
//                                    if (markerItemAll.toString().equals(update.toString())) {
//                                        allMarkersIterator.remove();
//                                        map.removeMarker(markerItemAll);
//                                    }
                                }
                                mPopupWindow.dismiss();
                                Toast.makeText(EditorPanelActivity.this, getString(R.string.notification_removed),
                                        Toast.LENGTH_LONG).show();
                            }
                            else{
                                Toast.makeText(EditorPanelActivity.this, getString(R.string.remove_update_alert),
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                        else{
                            Toast.makeText(EditorPanelActivity.this, getString(R.string.location_not_found),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                } else {
                    Location lastLocation = getLastLocation();
                    if(lastLocation!= null) {
                        LatLng currentLoc = new LatLng(lastLocation);
                        LatLng updateLoc = new LatLng(update.getPosition());
                        if(currentLoc.distanceTo(updateLoc) <= 50.0){
                            user_updates.child(dirkey).child(key).removeValue();
                            Iterator<Marker> allMarkersIterator = map.getMarkers().iterator();

                            while(allMarkersIterator.hasNext()) {
                                Marker markerItemAll = allMarkersIterator.next();

                                if (PMethods.getInstance().getUserUpdateHashKey(markerItemAll).equals(key)) {
                                    allMarkersIterator.remove();
                                    myService.removeMarkerFromActivity(markerItemAll);
                                    break;
                                }

                            }
                            Toast.makeText(EditorPanelActivity.this, getString(R.string.notification_removed),
                                    Toast.LENGTH_LONG).show();
                            mPopupWindow.dismiss();
                        }
                        else{
                            Toast.makeText(EditorPanelActivity.this, getString(R.string.remove_update_alert),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                    else{
                        Toast.makeText(EditorPanelActivity.this, getString(R.string.location_not_found),
                                Toast.LENGTH_LONG).show();
                    }
                }

            }
        });


        mPopupWindow.showAtLocation(mRelativeLayout, Gravity.CENTER, 0, 0);

    }




    public Intent getOpenFacebookIntent(String uuid) {

        try {
            getPackageManager()
                    .getPackageInfo("com.facebook.katana", 0); //Checks if FB is even installed.
            return new Intent(Intent.ACTION_VIEW,
                    Uri.parse("fb://profile/"+uuid)); //Trys to make intent with FB's URI
        } catch (Exception e) {
            return new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://www.facebook.com/arkverse")); //catches and opens a url to the desired page
        }
    }


    private void uploadImage(final String imageName, final PointOfInterest pointOfInterest) {

        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            final String uuid = UUID.randomUUID().toString();
            StorageReference ref = storageReference.child("images/"+ imageName +"/"+ uuid);
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(EditorPanelActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                            String newPath = storageReference.child("images/"+ imageName +"/"+ uuid).getPath();
                            pointOfInterest.setImage(newPath);
                            Map<String, Object> pointMap = pointOfInterest.toMap();

                            Map<String, Object> childUpdates = new HashMap<>();
                            childUpdates.put(imageName, pointMap);
                            points_of_interest.updateChildren(childUpdates);

                            Image image = new Image(newPath);
                            Map<String, Object> imageMap = image.toMap();

                            String key = images.child(imageName).push().getKey();
                            childUpdates.clear();
                            childUpdates.put(key, imageMap);
                            images.child(imageName).updateChildren(childUpdates);

                            uploadImage.setVisibility(View.INVISIBLE);
                            cancle.setVisibility(View.INVISIBLE);
                            camera.setVisibility(View.VISIBLE);
                            showAllImages.setVisibility(View.VISIBLE);


                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(EditorPanelActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
        }
    }




























    //track popup
    private void initTrackPopup(final  Map<String, Object> track){

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.track_details_popup, null);
        mPopupWindow.setContentView(view);

        delete_coordinate_button = (ImageButton)mPopupWindow.getContentView().findViewById(R.id.deleteCoordinateButt);
        edit_coordinate_button = (ImageButton)mPopupWindow.getContentView().findViewById(R.id.editCoordinateButt);


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
    private void initMarkerPopup(final Marker marker, final String type){


        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.coordinate_details_popup, null);
        mPopupWindow.setContentView(view);

        delete_coordinate_button = mPopupWindow.getContentView().findViewById(R.id.deleteCoordinateButt);
        edit_coordinate_button = mPopupWindow.getContentView().findViewById(R.id.editCoordinateButt);

        TextView title = mPopupWindow.getContentView().findViewById(R.id.main_content);
        title.setText(marker.getTitle());

        TextView discreption = mPopupWindow.getContentView().findViewById(R.id.minor_content);
        discreption.setText(marker.getSnippet());

        Button read_more = mPopupWindow.getContentView().findViewById(R.id.read_more);
        read_more.setVisibility(View.GONE);

        //add click listeners for editing and removing point of intereset
        delete_coordinate_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogDeleteCoordinate(marker, type);
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
                    drawRoute(route, Color.RED);
                }
                loading_map_progress_bar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                loading_map_progress_bar.setVisibility(View.INVISIBLE);
            }
        });
    }

    private boolean checkIfTransportaionSation(String type){
        return type.equalsIgnoreCase("תחנת רכבת") || type.equalsIgnoreCase("תחנת אוטובוס")
                || type.equalsIgnoreCase("Train Station") || type.equalsIgnoreCase("Bus Station");
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
                            position.getLatitude(),
                            position.getLongitude());
                    String type = (String)point.get("type");
                    String title = (String) point.get("title");

                    addMarkerForPointOfInterest(latlng, title, (String)point.get("snippet"), type);
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
                            position.getLatitude(),
                            position.getLongitude());
                    addMarkerForCoordinate(latlng, (String)cor.get("title"), (String)cor.get("snippet"));
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}

        });


    }

    private void getRoute() throws ServicesException {

        LatLng ori =track_positions.get(0);
        LatLng desti = track_positions.get(track_positions.size()-1);
        Point origin = Point.fromLngLat(ori.getLongitude(),ori.getLatitude());
        Point destination = Point.fromLngLat(desti.getLongitude(),desti.getLatitude());
        MapboxDirections.Builder builder = MapboxDirections.builder()
                .origin(origin)
                .destination(destination)
                .profile(DirectionsCriteria.PROFILE_WALKING)
                .accessToken(getString(R.string.access_token))
                .steps(true)
                .bannerInstructions(true);

        for (int i=1; i<track_positions.size()-1;i++) {
            LatLng point = track_positions.get(i);
            Point waypoint = Point.fromLngLat(point.getLongitude(),point.getLatitude());
            builder.addWaypoint(waypoint);
        }

        builder.build().enqueueCall(new Callback<DirectionsResponse>() {
            @Override
            public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                // You can get the generic HTTP info about the response

                if (response.body() == null) {
                    return;
                } else if (response.body().routes().size() < 1) {
                    return;
                }

                // Print some info about the route
                currentRoute = response.body().routes().get(0);
                currentRouteWaypoints = response.body().waypoints();
                Toast.makeText(
                        EditorPanelActivity.this,
                        "Route is " + currentRoute.distance() + " meters long.",
                        Toast.LENGTH_LONG).show();

                // Draw the route on the map
                drawRoute(currentRoute,Color.DKGRAY);
            }

            @Override
            public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
                Toast.makeText(EditorPanelActivity.this, "Error: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }


    private void drawRoute(DirectionsRoute route,int color) {
        // Convert LineString coordinates into LatLng[]
        LineString lineString = LineString.fromPolyline(route.geometry(), com.mapbox.services.Constants.OSRM_PRECISION_V5);
        List<Position> coordinates = lineString.getCoordinates();
        LatLng[] points = new LatLng[coordinates.size()];
        for (int i = 0; i < coordinates.size(); i++) {
            points[i] = new LatLng(
                    coordinates.get(i).getLatitude()/10,
                    coordinates.get(i).getLongitude()/10);
        }

        // Draw Points on MapView
        routeLine = new PolylineOptions()
                .add(points)
                .color(color)
                .width(ROUTE_LINE_WIDTH);

        if(color == Color.BLUE) {
            currRouteLine = routeLine;
        }
        map.addPolyline(routeLine);

    }

    private Polyline getPolyLineFromRoute(DirectionsRoute route){
        LineString lineString = LineString.fromPolyline(route.geometry(), com.mapbox.services.Constants.OSRM_PRECISION_V5);
        List<Position> coordinates = lineString.getCoordinates();
        LatLng[] points = new LatLng[coordinates.size()];
        for (int i = 0; i < coordinates.size(); i++) {
            points[i] = new LatLng(
                    coordinates.get(i).getLatitude()/10,
                    coordinates.get(i).getLongitude()/10);
        }

        // Draw Points on MapView
        routeLine = new PolylineOptions()
                .add(points)
                .color(Color.RED)
                .width(ROUTE_LINE_WIDTH);
        return routeLine.getPolyline();
    }

    private void showLastLocation(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String lastLocationString  = prefs.getString(LAST_LOCATION_SAVED,"");
        if(lastLocationString.equalsIgnoreCase("")){
            showDefaultLocation();
        }
        else{
            LatLng lastSavedLocation = JsonParserManager.getInstance().retreiveLatLngFromJson(lastLocationString);
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    lastSavedLocation,ZOOM_LEVEL_CURRENT_LOCATION));

        }
    }


    private void showCurrentLocation(boolean enableGps) {
        Location location = getLastLocation();
        if(location == null){
            if(enableGps)
                showLastLocation();
            floatingActionButton.setImageResource(R.drawable.ic_location_disabled_24dp);
            map.setMyLocationEnabled(true);

        }else {
//        if (enableGps) {
            // Check if user has granted location permission
            permissionsManager = new PermissionsManager(this);
            if (!PermissionsManager.areLocationPermissionsGranted(this)) {
                permissionsManager.requestLocationPermissions(this);
            } else {
                enableLocation(true);
            }
        }

    }

    private Location getLastLocation(){
        if (ActivityCompat.checkSelfPermission(EditorPanelActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(EditorPanelActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return null;
        }

        return locationEngine.getLastLocation();
    }


    private void showDefaultLocation(){
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(DEFAULT_JERUSALEM_COORDINATE.getLatitude(),
                        DEFAULT_JERUSALEM_COORDINATE.getLongitude()), 12));
    }


    public void setCameraPositionFromPrefs() {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        LatLng target = JsonParserManager.getInstance().retreiveLatLngFromJson(preferences.getString("targetJSON",""));
        Float zoom = preferences.getFloat("zoom",12);
        Float tilt = preferences.getFloat("tilt", 0);
        Float bearing = preferences.getFloat("bearing",0);

        map.animateCamera(CameraUpdateFactory.newCameraPosition(
                new CameraPosition.Builder()
                        .target(target)
                        .bearing(bearing)
                        .tilt(tilt)
                        .zoom(zoom)
                        .build()));
    }

    public void saveCameraPositionToPrefs() {

        CameraPosition cameraP = map.getCameraPosition();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString("targetJSON",JsonParserManager.getInstance().castLatLngToJson(cameraP.target));
        editor.putFloat("zoom",(float)cameraP.zoom);
        editor.putFloat("tilt",(float)cameraP.tilt);
        editor.putFloat("bearing",(float)cameraP.bearing);

        editor.apply();

    }

    private class MyOnMapClickListener implements MapboxMap.OnMapClickListener{
        @Override
        public void onMapClick(@NonNull final LatLng point) {

            if(SHOW_DETAILS_POPUP || SHOW_TRACK_POPUP){
                mPopupWindow.dismiss();
                mTrackPopupWindow.dismiss();
                return;
            }

            if(ADD_COORDINATE_MODE) {
                dialogAddNewCoordinate(point, ADD_TRACK_MODE);
                return;
            /*When clicking on a location (which is not a marker!) in the map while
            * being in ADD_COORDINATE_MODE,
            * we want to ask the editor if he wants to add a new coordinate
            * in the database for this specific location*/
            }

            if(ADD_TRACK_MODE && count_coordinates_selected < MAX_NUM_OF_TRACK_COORDINATES) {
//                if(count_coordinates_selected == 0){
//                    Toast.makeText(EditorPanelActivity.this, R.string.toast_starting_point, Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                else{
                    writeGenericCoordinateToDB(point);
                    MarkerView marker = addMarkerForCoordinate(point, "", "");
                    marker.setIcon(IconManager.getInstance().getIconForEdit());

                    addTrackPoint(point, marker);

                    count_coordinates_selected++;
                    updateScreenCounter();
//                }

            }
            else if(ADD_TRACK_MODE && count_coordinates_selected >= MAX_NUM_OF_TRACK_COORDINATES){
                Toast.makeText(EditorPanelActivity.this, R.string.reached_limit_of_coordinates, Toast.LENGTH_SHORT).show();
            }
            else if(!ADD_TRACK_MODE && !ADD_COORDINATE_MODE){
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
                            String uuid = (String)track.get("uuid");
                            uuid = uuid == null ? "" : uuid;

                            DirectionsRoute route = JsonParserManager.getInstance().retrieveRouteFromJson(route_st);
                            Polyline polyline = getPolyLineFromRoute(route);


                            for (LatLng polyCoords : polyline.getPoints()) {
                                float[] results = new float[1];
                                Location.distanceBetween(point.getLatitude(), point.getLongitude(),
                                        polyCoords.getLatitude(), polyCoords.getLongitude(), results);

//                                if (results[0] < 100 && !SHOW_DETAILS_POPUP) {
//                                    // If distance is less than 100 meters, this is your polyline
////                                    Log.e(TAG, "Found @ "+clickCoords.latitude+" "+clickCoords.longitude);
//                                    SHOW_DETAILS_POPUP = true;
//                                    initTrackPopup(track);
//                                }
                                if (results[0] < 100) {
                                    // If distance is less than 100 meters, this is your polyline
//                                    Log.e(TAG, "Found @ "+clickCoords.latitude+" "+clickCoords.longitude);
                                    if(!SHOW_TRACK_POPUP) {
                                        List<Polyline> mapPolylines = map.getPolylines();
                                        for(int i=0; i<mapPolylines.size();i++){
                                            Polyline map_polyline = mapPolylines.get(i);
                                            if (polyline.getPoints().toString().equals(map_polyline.getPoints().toString()))
                                            {
                                                map.removePolyline(map_polyline);
                                                i=mapPolylines.size();
                                            }
                                        }
                                        currRoute = route;
                                        drawRoute(route,Color.BLUE);
                                        SHOW_TRACK_POPUP = true;
                                        points_for_track.clear();
                                        Map<String,String> pointsL =  (HashMap<String,String>)track.get("points");
                                        if (pointsL != null) {

                                            for (Marker markerItemAll : map.getMarkers()) {
                                                //                                                for (String point : pointsL) {
                                                String markerKey = PMethods.getInstance().getMarkerHashKey(markerItemAll);
                                                if (pointsL.containsKey(markerKey)) {
                                                    String type = pointsL.get(markerKey);
                                                    String large = type + " ג";
                                                    markerItemAll.setIcon(IconManager.getInstance().getIconForType(large));
                                                    Map<String,Marker> child = points_for_track.get(type);
                                                    if(child == null)
                                                        child = new HashMap<>();
                                                    child.put(markerKey, markerItemAll);
                                                    points_for_track.put(type,child);

                                                }
                                            }
                                        }

                                        initTrackPopup(track, uuid);

                                    }
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
        LatLng temp = new LatLng(marker.getPosition().getLatitude(), marker.getPosition().getLongitude());
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
        LatLng tempP = new LatLng(temp.getPosition().getLatitude(), temp.getPosition().getLongitude());
        LatLng newP = new LatLng(marker.getPosition().getLatitude(), marker.getPosition().getLongitude());
        for(int i=0; i<track_positions.size();i++){
            if(track_positions.get(i).toString().equals(tempP.toString()))
                track_positions.set(i,newP);

        }
    }

    private void addTrackPoint(LatLng point, Marker marker){
        track_coordinates.add(point.getLongitude());
        track_coordinates.add(point.getLatitude());
        track_markers.add(marker);
        track_positions.add(point);
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

        if(checkIfTransportaionSation(type))
            stations.put(title,type);
        MarkerViewOptions markerViewOptions = new MarkerViewOptions()
                .position(point)
                .title(title)
                .snippet(snippet);
        map.addMarker(markerViewOptions);

        markerViewOptions.getMarker().setIcon(IconManager.getInstance().getIconForType(type));

        return markerViewOptions.getMarker();
    }

    private MarkerView addMarkerForUserUpdate(LatLng point, String title, String snippet, String type){
        MarkerViewOptions markerViewOptions = new MarkerViewOptions()
                .position(point)
                .title(title)
                .snippet(snippet);
        map.addMarker(markerViewOptions);

        markerViewOptions.getMarker().setIcon(IconManager.getInstance().getIconForType(type));

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
    private class MyOnMarkerClickListener implements MapboxMap.OnMarkerViewClickListener{

        @Override
        public boolean onMarkerClick(@NonNull final Marker marker, @NonNull View view, @NonNull MapboxMap.MarkerViewAdapter adapter) {

            if(points_for_track.isEmpty())
                mTrackPopupWindow.dismiss();

            if(!ADD_TRACK_MODE) {
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(marker.getPosition().getLatitude(),
                        marker.getPosition().getLongitude()), ZOOM_LEVEL_MARKER_CLICK));

                final String key = getMarkerHashKey(marker);
                points_of_interest.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Map<String, Object> pointsMap = (Map<String, Object>)dataSnapshot.getValue();
                        Map<String, Object> point = ((Map<String, Object>)pointsMap.get(key));
//                        if(point != null) {
//                            String type = (String) point.get("type");
//                            //if (!type.equals("תחנת רכבת")) {
//                                SHOW_DETAILS_POPUP = true;
//                                initMarkerPopup(marker, type);
//                           // }
//                        }

                        if(point != null) {
                            String type = (String) point.get("type");
                            String imageUUId = (String) point.get("imagePath");
                            String uuid = (String) point.get("uuid");
                            uuid = uuid== null ? "" : uuid;
//                        if (!type.equals("תחנת רכבת")) {
                            SHOW_DETAILS_POPUP = true;
                            mTrackPopupWindow.dismiss();
                            initMarkerPopup(marker, type,imageUUId, uuid);

//                        }
                        }
                        else{
                            SHOW_DETAILS_POPUP = true;
                            mTrackPopupWindow.dismiss();
                            detectUpdtesAtpoint(marker);
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
//                        if(i==0){
//                            dialogChangeFirstStop();
//                            return true;
//                        }
                        touchedmarkertype = "";
                        markerIsPointOfInterest = false;
                        final String key = getMarkerHashKey(marker);
                        points_of_interest.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Map<String, Object> pointsMap = (Map<String, Object>)dataSnapshot.getValue();
                                Map<String, Object> point = ((Map<String, Object>)pointsMap.get(key));
                                if(point != null) {
                                    dialogEditMarker(marker, (String) point.get("type"), true);
                                }
                                else{
                                    dialogEditMarker(marker, null, false);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

//                        dialogEditMarker(marker, touchedmarkertype, markerIsPointOfInterest);
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

//                /*We want to make sure that the first chosen coordinate is train station*/
//                if(count_coordinates_selected == 0){
//                    final String key1 = getMarkerHashKey(marker);
//                    points_of_interest.addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(DataSnapshot dataSnapshot) {
//                            Map<String, Object> pointsMap = (Map<String, Object>)dataSnapshot.getValue();
//                            if(pointsMap == null) {
//                                Toast.makeText(EditorPanelActivity.this, R.string.toast_starting_point, Toast.LENGTH_SHORT).show();
//                                return;
//                            }
//                            Map<String, Object> point = ((Map<String, Object>)pointsMap.get(key1));
//
//
//                            if(point == null){
//                                Toast.makeText(EditorPanelActivity.this, R.string.toast_starting_point, Toast.LENGTH_SHORT).show();
//                                return;
//                            }
//
//                            String type = (String)point.get("type");
//                            if(!checkIfTransportaionSation(type)){
//                                Toast.makeText(EditorPanelActivity.this, R.string.toast_starting_point, Toast.LENGTH_SHORT).show();
//                                return;
//                            }
//
//                            addTrackPoint(marker.getPosition(), marker);
//                            count_coordinates_selected++;
//                            updateScreenCounter();
//
//                            marker.setIcon(IconManager.getInstance().getIconForEdit());
//
//
//                        }
//
//                        @Override
//                        public void onCancelled(DatabaseError databaseError) {
//
//                        }
//                    });
//                    return true;
//                }
//                else{

                    addTrackPoint(marker.getPosition(),marker);
                    count_coordinates_selected++;
                    updateScreenCounter();

                    marker.setIcon(IconManager.getInstance().getIconForEdit());
                    return true;
               // }

            }

            return true;
        }
    }


    private void updateScreenCounter(){
        StringBuilder s = new StringBuilder().append(getResources().getString(R.string.coordinates_selected_txt)).
                append(" "+count_coordinates_selected);
        counter_coordinates.setText(s.toString());
    }


    private void dialogEditMarker(final Marker marker,final  String touchedmarkertype,
            final boolean markerIsPointOfInterest){

        AlertDialog.Builder builder = new AlertDialog.Builder(EditorPanelActivity.this);
        builder.setMessage(getResources().getString(R.string.dialog_change_point));

        builder.setPositiveButton(R.string.dialog_remove_point, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                removePointFromTrack(marker, markerIsPointOfInterest, touchedmarkertype);
            }
        });
        builder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            return;
            }
        });
        if(!markerIsPointOfInterest) {
            builder.setNeutralButton(R.string.dialog_move_point, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    movePointOnMap(marker);
                }
            });
        }

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void dialogChangeFirstStop(){

        final AlertDialog.Builder builder = new AlertDialog.Builder(EditorPanelActivity.this);
        builder.setMessage(getResources().getString(R.string.dialog_edit_first_stop));

        builder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                AlertDialog.Builder builder = new AlertDialog.Builder(EditorPanelActivity.this);

                builder.setTitle(getString(R.string.choose_a_station));
                final String stops[] = stations.keySet().toArray(new String[stations.size()]);
                builder.setSingleChoiceItems(stops, -1,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                // dialog.dismiss();
                                switchbusstation(stops, which);
                                dialog.dismiss();
                            }
                        });
                builder.setNegativeButton(getString(R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();


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


    private void dialogEditTrack(final Map<String,Object> track){
        AlertDialog.Builder builder = new AlertDialog.Builder(EditorPanelActivity.this);
        builder.setMessage(getResources().getString(R.string.dialog_edit_track));

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

    private void dialogDeleteCoordinate(final Marker marker, final String type){
        AlertDialog.Builder builder = new AlertDialog.Builder(EditorPanelActivity.this);
        builder.setMessage(getResources().getString(R.string.dialog_delete_coordinate));

        builder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteCoordinateFromDb(marker, true, type);
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
    private void deleteCoordinateFromDb(final Marker marker, boolean point_of_interest, String type) {
        String key = getMarkerHashKey(marker);
        coordinates.child(key).removeValue();
        if(point_of_interest)
            if(checkIfTransportaionSation(type)){
                if(stations.containsKey(marker.getTitle()))
                    stations.remove(marker.getTitle());
            }

            points_of_interest.child(key).removeValue();
        map.removeMarker(marker);
    }

    /*Dialog function to ask the editor if he wants to add new coordinate with details.
    * if yes, we are directed to the creating new coordinate activity*/
    private void dialogAddNewCoordinate(final LatLng point, final boolean trackMode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(EditorPanelActivity.this);
        builder.setMessage(getResources().getString(R.string.dialog_add_new_coordinate));


        builder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent i = new Intent(EditorPanelActivity.this, CreateNewCoordinateActivity.class);
                i.putExtra(CHOSEN_COORDINATE, JsonParserManager.getInstance().castLatLngToJson(point));
                i.putExtra(CURRENT_USER_NAME,userhash);
                i.putExtra(FROM_ADD_TRACK,trackMode);
                startActivityForResult(i, NEW_COORDINATE);
            }
        });

        builder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if(trackMode)
                    ADD_COORDINATE_MODE = false;
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



    private void enableLocation(boolean enabled) {
        if (enabled) {
            // If we have the last location of the user, we can move the camera to that position.
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            Location lastLocation = locationEngine.getLastLocation();
            if (lastLocation != null) {
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lastLocation), ZOOM_LEVEL_CURRENT_LOCATION));
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(EditorPanelActivity.this);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(LAST_LOCATION_SAVED,JsonParserManager.getInstance().castLatLngToJson(new LatLng(lastLocation)));
                editor.apply();
                floatingActionButton.setImageResource(R.drawable.ic_my_location_24dp);
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

                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(EditorPanelActivity.this);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString(LAST_LOCATION_SAVED,JsonParserManager.getInstance().castLatLngToJson(new LatLng(location)));
                        editor.apply();
                    }
                }
            };
            locationEngine.addLocationEngineListener(locationEngineListener);


        } else {
            floatingActionButton.setImageResource(R.drawable.ic_location_disabled_24dp);
            // floatingActionButton.setImageResource(R.drawable.ic_my_location_24dp);
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

            ArrayList<String> temp = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.spinner_editor_list_item_array)));

            String uname = currentUser.getDisplayName();
            uid = currentUser.getUid();
            userhash = uid;
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
                        saveCameraPositionToPrefs();
                        Intent i = new Intent(EditorPanelActivity.this, HomeActivity.class);
                        i.putExtra(SET_FROM_PREFS,true);
                        startActivity(i);
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

            case R.id.home:
                saveCameraPositionToPrefs();
                i = new Intent(EditorPanelActivity.this, HomeActivity.class);
                i.putExtra(SET_FROM_PREFS,true);
                startActivity(i);
                return true;

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



            case R.id.tracksActivity:
                i = new Intent(this, TracksActivity.class);
                startActivity(i);
                return true;

            case R.id.languageActivity:

                toggleLanguage();
                return true;

            case R.id.signOut:
                signOut();
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
        saveCameraPositionToPrefs();
        FirebaseAuth.getInstance().signOut();

        AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(
                    AccessToken oldAccessToken,
                    AccessToken currentAccessToken) {

                if (currentAccessToken == null){
                    //User logged out

                    LoginManager.getInstance().logOut();
                    Intent i = new Intent(EditorPanelActivity.this, HomeActivity.class);
                    startActivity(i);
                }
            }
        };
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        Intent i = new Intent(EditorPanelActivity.this, HomeActivity.class);
                        i.putExtra(SET_FROM_PREFS,true);
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
                String key = hashFunction(createdCoordinateLatLng.getLongitude());
                updateScreenCoordinates(key, createdCoordinateLatLng, EDIT_COORDINATE);

                map.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(createdCoordinateLatLng.getLatitude(),
                                createdCoordinateLatLng.getLongitude()), ZOOM_LEVEL_MARKER_CLICK));
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
                            LatLng tempLatLng = new LatLng(createdCoordinate.getLatitude(), createdCoordinate.getLongitude());
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
                    return;
                }

                tracksPointsOfInterest.put(key, (String)point.get("type"));
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
                        LatLng tempLatLng = new LatLng(createdCoordinate.getLatitude(), createdCoordinate.getLongitude());
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


    private void removePointFromTrack(Marker marker, boolean isPointOfInterest, String type){
        removeTrackPoint(marker);
        count_coordinates_selected--;
        updateScreenCounter();
        if(isPointOfInterest){

            marker.setIcon(IconManager.getInstance().getIconForType(type));

        }

        else {
            addMarkerForCoordinate(marker.getPosition(), marker.getTitle(), marker.getSnippet());
            map.removeMarker(marker);
        }

    }

    private void switchbusstation(String stops[], int pos) {

        String name = stops[pos];
        String type = stations.get(name);

        List<Marker> markers = map.getMarkers();
        for(int i=0; i<markers.size(); i++){
            if(markers.get(i).getTitle().equals(name)){
                Marker tempMarker = markers.get(i);
                tempMarker.setIcon(IconManager.getInstance().getIconForEdit());
                Marker curMarker = track_markers.get(0);
                curMarker.setIcon(IconManager.getInstance().getIconForType(type));
                track_markers.set(0,tempMarker);
                break;
            }
        }
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


    private void dialogChooseanUpdate(final ArrayList<String> updates_names){


        AlertDialog.Builder builder = new AlertDialog.Builder(EditorPanelActivity.this);

        builder.setTitle(getString(R.string.choose_an_update));
        //  builder.setMessage(R.string.choose_update_message);
        final String[] names = updates_names.toArray(new String[updates_names.size()]);
        builder.setSingleChoiceItems(names, -1,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // dialog.dismiss();
                        findmarkerforupdate(dirUpdates.get(names[which]));
                        dialog.dismiss();
                    }
                });
        builder.setNegativeButton(R.string.cancel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();


    }





    private void detectUpdtesAtpoint(final Marker marker){
        String dirkey = PMethods.getInstance().getMarkerHashKey(marker);

        user_updates.child(dirkey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() > 1) {
                    ArrayList<String> names = new ArrayList<String>();
                    Map<String, Object> updatesMap = (Map<String, Object>) dataSnapshot.getValue();
                    for (Map.Entry<String, Object> entry : updatesMap.entrySet()) {
                    /*For each coordinate in the database, we want to create a new marker
                    * for it and to show the marker on the map*/
                        Map<String, Object> update = ((Map<String, Object>) entry.getValue());
                        String name = (String) update.get("title");
                        names.add(name);
                        dirUpdates.put(name, entry.getKey());
                    }

                    dialogChooseanUpdate(names);
                }
                else{
                    initUpdateInfoWindow(marker);
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(EditorPanelActivity.this, getString(R.string.database_error),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void findmarkerforupdate(String key) {


        Iterator<Marker> allMarkersIterator = map.getMarkers().iterator();

        while(allMarkersIterator.hasNext()) {
            Marker markerItemAll = allMarkersIterator.next();

            if (PMethods.getInstance().getUserUpdateHashKey(markerItemAll).equals(key)) {
                allMarkersIterator.remove();
                initUpdateInfoWindow(markerItemAll);
                break;
            }

        }

        dirUpdates.clear();
    }

    @Override
    public void onBackPressed() {
        if(SHOW_DETAILS_POPUP)
            mPopupWindow.dismiss();
        else if(SHOW_TRACK_POPUP)
            mTrackPopupWindow.dismiss();
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
