package com.zehava.cityforest;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.plugins.cluster.clustering.ClusterItem;
import com.mapbox.mapboxsdk.plugins.cluster.clustering.ClusterManagerPlugin;
import com.zehava.cityforest.Models.Coordinate;
import com.zehava.cityforest.Models.Track;
import com.zehava.cityforest.Models.UserUpdate;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerView;
import com.mapbox.mapboxsdk.annotations.MarkerViewOptions;
import com.mapbox.mapboxsdk.annotations.Polyline;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
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

import com.mapbox.services.directions.v5.models.DirectionsRoute;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Scanner;


import static com.zehava.cityforest.Constants.CREATED_UPDATE_FOR_ZOOM;
import static com.zehava.cityforest.Constants.CURRENT_USER_NAME;
import static com.zehava.cityforest.Constants.CURRENT_USER_UID;
import static com.zehava.cityforest.Constants.DEFAULT_JERUSALEM_COORDINATE;
import static com.zehava.cityforest.Constants.ENGLISH;
import static com.zehava.cityforest.Constants.HEBREW;
import static com.zehava.cityforest.Constants.LANGUAGE;
import static com.zehava.cityforest.Constants.LANGUAGE_TO_LOAD;
import static com.zehava.cityforest.Constants.MOVE_MARKER;
import static com.zehava.cityforest.Constants.NEW_USER_UPDATE;
import static com.zehava.cityforest.Constants.ROUTE_LINE_WIDTH;
import static com.zehava.cityforest.Constants.SHOW_DETAILS_POPUP;
import static com.zehava.cityforest.Constants.UPDATE_POSITION;
import static com.zehava.cityforest.Constants.USER_UPDATE_CREATED;
import static com.zehava.cityforest.Constants.ZOOM_LEVEL_CURRENT_LOCATION;
import static com.zehava.cityforest.Constants.ZOOM_LEVEL_MARKER_CLICK;
import static com.mapbox.services.android.telemetry.location.AndroidLocationEngine.getLocationEngine;

public class Home extends AppCompatActivity implements PermissionsListener, ICallback {

    private FloatingActionButton floatingActionButton;
    private LocationEngine locationEngine;
    private LocationEngineListener locationEngineListener;
    private PermissionsManager permissionsManager;

    private GoogleApiClient mGoogleApiClient;
    private ProgressBar loading_map_progress_bar;

    private MapView mapView;
    private MapboxMap map;

    private PolylineOptions routeLine;
    private FirebaseDatabase database;
    private DatabaseReference coordinates;
    private DatabaseReference tracks;
    private DatabaseReference points_of_interest;
    private DatabaseReference user_updates;
    private ImageButton delete_coordinate_button;
    private ImageButton edit_coordinate_button;
    private TextView likes_count;

    private FloatingActionButton createUserUpdate;

    private PopupWindow mPopupWindow;
    private RelativeLayout mRelativeLayout;

    String uid,uname;

    private ClusterManagerPlugin<MyItem> clusterManagerPlugin;

    Intent serviceIntent;
    UpdatesManagerService myService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        String languageToLoad  = "he"; // your language
//        Locale locale = new Locale(languageToLoad);
//        Locale.setDefault(locale);
//        Configuration config = new Configuration();
//        config.locale = locale;
//        getBaseContext().getResources().updateConfiguration(config,
//                getBaseContext().getResources().getDisplayMetrics());

        /*Mapbox and firebase initializations*/
        Mapbox.getInstance(this, getString(R.string.access_token));
        setContentView(R.layout.activity_home);

        // Get the location engine object for later use.
        locationEngine = getLocationEngine(this);
        locationEngine.activate();

        mapView = (MapView) findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new myOnMapReadyCallback());

        database = FirebaseDatabase.getInstance();
        coordinates = database.getReference("coordinates");
        points_of_interest = database.getReference("points_of_interest");
        tracks = database.getReference("tracks");
        user_updates = database.getReference("user_updates");


        mRelativeLayout = (RelativeLayout) findViewById(R.id.mapLayout);

        SHOW_DETAILS_POPUP = false;
        MOVE_MARKER = false;
        loading_map_progress_bar = (ProgressBar)findViewById(R.id.loadingMapProgress);
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
                Intent i = new Intent(Home.this, CreateUserUpdateActivity.class);
                i.putExtra(CURRENT_USER_UID, uid);
                i.putExtra(CURRENT_USER_NAME, uname);

                if (ActivityCompat.checkSelfPermission(Home.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Home.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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

                i.putExtra(UPDATE_POSITION, castLatLngToJson(new LatLng(lastLocation)));
                startActivityForResult(i, NEW_USER_UPDATE);

            }
        });
        loading_map_progress_bar.setVisibility(View.VISIBLE);

        serviceIntent = new Intent(Home.this, UpdatesManagerService.class);

        startService(serviceIntent); //Starting the service
        bindService(serviceIntent, mConnection, Context.BIND_AUTO_CREATE); //Binding to the service!

//        myService.startCounter();

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



        super.onStart();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        final MenuInflater inflater = getMenuInflater();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null)
            inflater.inflate(R.menu.not_signedin_menu, menu);
        else {

            inflater.inflate(R.menu.app_menu, menu);

            MenuItem item = menu.findItem(R.id.spinner);
            Spinner spinner = (Spinner) MenuItemCompat.getActionView(item);

            ArrayList<String> temp = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.spinner_list_item_array)));

            String userUid = currentUser.getUid();
            if(!userUid.equals(getResources().getString(R.string.permitted_editor)) &&
                    !userUid.equals(getResources().getString(R.string.permitted_editor2))&&!(userUid.equals("TNHu1lOD4vfz9SY8EEf4bsiQQuG2"))) {
                temp.remove(1);
            }

            uname = currentUser.getDisplayName();
            uid = currentUser.getUid();
            temp.add(0, uname);

            String carArr[] = temp.toArray(new String[temp.size()]);


            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item, carArr);
            spinner.setAdapter(adapter);
            adapter.setDropDownViewResource(R.layout.spinner_item);
            spinner.setSelection(0, false);

            spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
                public void onItemSelected(
                        AdapterView<?> parent, View view, int position, long id) {

                    if (position == 0) {

                    }

                    if (position == 1) {
                        signOut();
                    }

                    if (position == 2) {
                        Intent intent = new Intent(Home.this, EditorPanelActivity.class);
                        startActivity(intent);

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

            case R.id.signOut:
                Intent intent = new Intent(Home.this, SignInActivity.class);
                startActivityForResult(intent, 50);
                return true;
            case R.id.languageActivity:
                toggleLanguage();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }



    /*Exit app dialog if the user clicked back button*/
    @Override
    public void onBackPressed() {
        if(SHOW_DETAILS_POPUP)
        {
            SHOW_DETAILS_POPUP = false;
            mPopupWindow.dismiss();
            mPopupWindow = null;
            showDefaultLocation();
        }
        else {
            String current_user_uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

            if (current_user_uid.equals(getResources().getString(R.string.permitted_editor))) {
                Intent i = new Intent(Home.this, EditorHomeActivity.class);
                startActivity(i);
            } else {
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
    }

    @Override
    public void onDraggableNotify(DRAGGABLE_CALSS draggableIcall) {

    }

    @Override
    public void onUserUpdateNotify(USER_UPDATES_CLASS userUpdatesClass, int id) {
        //Toast.makeText(this , "id: "+id, Toast.LENGTH_SHORT).show();
       // Log.d("testingggg","testing");
    }


    private class myOnMapReadyCallback implements OnMapReadyCallback {
        @Override
        public void onMapReady(MapboxMap mapboxMap) {
            map = mapboxMap;
            map.setOnMapClickListener(new MyOnMapClickListener());
            map.setOnMarkerClickListener(new MyOnMarkerClickListener());
            map.setStyleUrl(Style.OUTDOORS);
            showDefaultLocation();
            showAllUserUpdates();
            showAllPointsOfInterest();
            showAllTracks();

            clusterManagerPlugin = new ClusterManagerPlugin<>(Home.this, map);

            initCameraListener();

            myService.startCounter();
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            //Toast.makeText(Home.this, "onServiceConnected called", Toast.LENGTH_SHORT).show();
            // We've binded to LocalService, cast the IBinder and get LocalService instance
            UpdatesManagerService.LocalBinder binder = (UpdatesManagerService.LocalBinder) service;
            myService = binder.getServiceInstance(); //Get instance of your service!
            myService.registerClient(Home.this); //Activity register in the service as client for callabcks!



        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
           // Toast.makeText(Home.this, "onServiceDisconnected called", Toast.LENGTH_SHORT).show();
        }
    };


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
                    DirectionsRoute route = retrieveRouteFromJson(route_st);
                    drawRoute(route);
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
                    Position position = retrievePositionFromJson(positionJSON);

                    /*Creating the marker on the map*/
                    LatLng latlng = new LatLng(
                            position.getLongitude(),
                            position.getLatitude());

                    long logo = (long)point.get("logo");
                    addMarkerForPointOfInterest(latlng, (String)point.get("title"), (String)point.get("snippet"), (int) logo);
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
                    Position position = retrievePositionFromJson(positionJSON);

                    /*Creating the marker on the map*/
                    LatLng latlng = new LatLng(
                            position.getLongitude(),
                            position.getLatitude());

                    String type = point.get("type").toString();
                    addMarkerForUserUpdate(latlng, (String)point.get("title"), (String)point.get("snippet"), (int)(long)UserUpdate.whatIsTheLogoForType(type));
                }
                loading_map_progress_bar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                loading_map_progress_bar.setVisibility(View.INVISIBLE);
            }

        });
    }



    private void drawRoute(DirectionsRoute route) {
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
        routeLine = new PolylineOptions()
                .add(points)
                .color(Color.RED)
                .width(ROUTE_LINE_WIDTH);
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
                        DEFAULT_JERUSALEM_COORDINATE.getLongitude()), 10));
    }


    private void toggleLanguage() {

        SharedPreferences languagepref = getSharedPreferences(LANGUAGE,MODE_PRIVATE);
        SharedPreferences.Editor editor = languagepref.edit();

        String currLanguage = languagepref.getString(LANGUAGE_TO_LOAD,"");
        String languageToLoad = currLanguage.equalsIgnoreCase(HEBREW)?ENGLISH:HEBREW;

        editor.putString(LANGUAGE_TO_LOAD,languageToLoad );
        editor.apply();

    }


    private void initPopupWindow(Object object){

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View customView = inflater.inflate(R.layout.coordinate_details_popup, null);

        mPopupWindow = new PopupWindow(
                customView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        if(Build.VERSION.SDK_INT>=21){
            mPopupWindow.setElevation(5.0f);
        }

        delete_coordinate_button = (ImageButton)customView.findViewById(R.id.deleteCoordinateButt);
        edit_coordinate_button = (ImageButton)customView.findViewById(R.id.editCoordinateButt);

        Log.d("class", "--"+object.getClass());
        if(object.getClass().equals(MarkerView.class))
            initMarkerPopup((Marker) object, customView);
        else if(object.getClass().equals(HashMap.class))
            initTrackPopup((Map<String, Object>)object, customView);

        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                SHOW_DETAILS_POPUP=false;
            }
        });
        mPopupWindow.showAtLocation(mRelativeLayout, Gravity.BOTTOM, 0, 0);

    }

    private void initTrackPopup(final  Map<String, Object> track, View customView){
        TextView title = (TextView)customView.findViewById(R.id.main_content);
        title.setText(track.get("track_name").toString());

        TextView discreption = (TextView)customView.findViewById(R.id.minor_content);
        discreption.setText(track.get("additional_info").toString());

        Button read_more = (Button) customView.findViewById(R.id.read_more);
        read_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        delete_coordinate_button.setVisibility(View.GONE);
        edit_coordinate_button.setVisibility(View.GONE);

        RatingBar ratingBar = (RatingBar)customView.findViewById(R.id.ratingBar);
        ratingBar.setVisibility(View.VISIBLE);
        ratingBar.setRating((long) track.get("star_count"));
        if(uid == null)
            ratingBar.setIsIndicator(true);

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                onStarClicked(track, rating);
            }
        });

        LinearLayout likesWrp = (LinearLayout) customView.findViewById(R.id.like_wrp);
        likesWrp.setVisibility(View.VISIBLE);

        likes_count = (TextView) mPopupWindow.getContentView().findViewById(R.id.like_count);
        likes_count.setText(String.valueOf( track.get("like_count")));

        likesWrp.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                onLikeClicked(track);
                return true;

            }
        });
    }

    private void initMarkerPopup(final Marker marker, View customView){
        TextView title = (TextView)customView.findViewById(R.id.main_content);
        title.setText(marker.getTitle());

        TextView discreption = (TextView)customView.findViewById(R.id.minor_content);
        discreption.setText(marker.getSnippet());

        Button read_more = (Button) customView.findViewById(R.id.read_more);
        read_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        delete_coordinate_button.setVisibility(View.GONE);
        edit_coordinate_button.setVisibility(View.GONE);

    }


    private void initUpdateInfoWindow(final Marker update){

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        final View customView = inflater.inflate(R.layout.user_update_infowindow, null);

        mPopupWindow = new PopupWindow(
                customView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        if(Build.VERSION.SDK_INT>=21){
            mPopupWindow.setElevation(5.0f);
        }


        TextView title = (TextView)customView.findViewById(R.id.update_main_content);
        title.setText(update.getTitle());

        int type=type(update.getIcon());
        final String key =getUserUpdateHashKey(update,type);
        user_updates.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> updates = (Map<String, Object>)dataSnapshot.getValue();
                Log.d("key",key);

                Map<String,Object> u_update = (Map<String,Object>)updates.get(key);
                if(u_update!=null){
                    Log.d("key foud","");
                    String created;
                    Date time;
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yy | HH:mm");

                    TextView update_created = (TextView)customView.findViewById(R.id.created);
                    if(u_update.get("uname")!=null && u_update.get("updated")!=null) {
                        created = getResources().getString(R.string.created_by) + ": " + u_update.get("uname").toString();
                        time = new Date(((Number) u_update.get("updated")).longValue()*1000);
                        created = created + " " + getResources().getString(R.string.at)+": "+dateFormat.format(time);
                        update_created.setText(created);
                    }

                    ImageView img = (ImageView)customView.findViewById(R.id.img);
                    img.setBackgroundResource((int) (long)UserUpdate.whatIsTheLogoForType(u_update.get("type").toString()));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        TextView discreption = (TextView)customView.findViewById(R.id.update_minor_content);
        discreption.setText(update.getSnippet());





        Button read_more = (Button) customView.findViewById(R.id.not_here);
        read_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

//
//        RatingBar ratingBar = (RatingBar)customView.findViewById(R.id.ratingBar);
//        ratingBar.setVisibility(View.VISIBLE);
//        ratingBar.setRating((long) update.ge);
//        if(uid == null)
//            ratingBar.setIsIndicator(true);
//
//        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
//            @Override
//            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
//                onStarClicked(update, rating);
//            }
//        });
//
//        LinearLayout likesWrp = (LinearLayout) customView.findViewById(R.id.like_wrp);
//        likesWrp.setVisibility(View.VISIBLE);
//
//        likes_count = (TextView) mPopupWindow.getContentView().findViewById(R.id.like_count);
//        likes_count.setText(String.valueOf( track.get("like_count")));
//
//        likesWrp.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                onLikeClicked(track);
//                return true;
//
//            }
//        });


        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                SHOW_DETAILS_POPUP=false;
            }
        });
        mPopupWindow.showAtLocation(mRelativeLayout, Gravity.CENTER, 0, 0);


    }



    private void onLikeClicked(final Map<String,Object> track) {
        String key= (String) track.get("db_key");
        DatabaseReference trackRef = tracks.child(key);

        trackRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {

                Track t = mutableData.getValue(Track.class);

                if(t == null) {
                    return Transaction.success(mutableData);
                }

                if (t.getLikes().containsKey(uid)) {
                    // Unstar the track and remove self from stars
                    t.setLike_count(t.getLike_count() - 1);
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            textview.setText(String.valueOf(t.getLike_count()));
//
//                            popupWindow.dismiss();
//                        }
//                    });
                    likes_count.setText(String.valueOf(Integer.valueOf(likes_count.getText().toString())-1));


                    Map<String, Boolean> temp = t.getLikes();
                    temp.remove(uid);
                    t.setLikes(temp);
                } else {
                    // Star the track and add self to stars
                    t.setLike_count(t.getLike_count() + 1);
                    likes_count.setText(String.valueOf(Integer.valueOf(likes_count.getText().toString())+1));


                    Map<String, Boolean> temp = t.getLikes();
                    temp.put(uid,true);
                    t.setLikes(temp);

                }

                // Set value and report transaction success
                mutableData.setValue(t);

                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                // Transaction completed
                Log.d(EditorPanelActivity.class.getSimpleName(), "postTransaction:onComplete:" + databaseError);
            }
        });
    }


    private void onStarClicked(final Map<String,Object> track, final Float stars) {
        String key= (String) track.get("db_key");
        DatabaseReference trackRef = tracks.child(key);

        trackRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {

                Track t = mutableData.getValue(Track.class);

                if(t == null) {
                    return Transaction.success(mutableData);
                }

                if (t.getStars().containsKey(uid)) {

                    Map<String, Float> temp = t.getStars();
                    temp.remove(uid);
                    temp.put(uid,stars);
                    t.setStars(temp);
                } else {

                    Map<String, Float> temp = t.getStars();
                    temp.put(uid,stars);
                    t.setStars(temp);

                }
                t.setStar_count(stars);

                // Set value and report transaction success
                mutableData.setValue(t);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                // Transaction completed
                Log.d(EditorPanelActivity.class.getSimpleName(), "postTransaction:onComplete:" + databaseError);
            }
        });
    }


    private class MyOnMapClickListener implements MapboxMap.OnMapClickListener{
        @Override
        public void onMapClick(@NonNull final LatLng point) {
            if(SHOW_DETAILS_POPUP){
                mPopupWindow.dismiss();
            }


            else{
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
                            DirectionsRoute route = retrieveRouteFromJson(route_st);
                            Polyline polyline = getPolyLineFromRoute(route);


                            for (LatLng polyCoords : polyline.getPoints()) {
                                float[] results = new float[1];
                                Location.distanceBetween(point.getLatitude(), point.getLongitude(),
                                        polyCoords.getLatitude(), polyCoords.getLongitude(), results);

                                if (results[0] < 100) {
                                    // If distance is less than 100 meters, this is your polyline
//                                    Log.e(TAG, "Found @ "+clickCoords.latitude+" "+clickCoords.longitude);
                                    if(!SHOW_DETAILS_POPUP) {
                                        SHOW_DETAILS_POPUP = true;
                                        initPopupWindow(track);
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


    /*Marker clicked listener. We can delete/edit a coordinate*/
    private class MyOnMarkerClickListener implements MapboxMap.OnMarkerClickListener{
        @Override
        public boolean onMarkerClick(@NonNull final Marker marker) {
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
                            initPopupWindow(marker);

                        }
                    }
                    else{
                        SHOW_DETAILS_POPUP = true;
                        initUpdateInfoWindow(marker);
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            return true;
        }
    }


    private MarkerView addMarkerForPointOfInterest(LatLng point, String title, String snippet, int logo){
        MarkerViewOptions markerViewOptions = new MarkerViewOptions()
                .position(point)
                .title(title)
                .snippet(snippet);
        map.addMarker(markerViewOptions);



        if(logo != -1) {
            IconFactory iconFactory = IconFactory.getInstance(Home.this);
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
            IconFactory iconFactory = IconFactory.getInstance(Home.this);
            Icon icon = iconFactory.fromResource(logo);
            markerViewOptions.getMarker().setIcon(icon);
        }
        return markerViewOptions.getMarker();
    }

    private int type(Icon icon){
        for(int i=0;i<10;i++) {
            IconFactory iconFactory = IconFactory.getInstance(Home.this);
            Icon icon1 = iconFactory.fromResource(UserUpdate.whatIsTheLogoForId(1));
            Log.d("icon1 2nd ", icon+" "+icon1);
            if(icon==icon1)
                return i;
        }

        return 1;
    }


    private String getMarkerHashKey(final Marker marker) {
        double longitude = marker.getPosition().getLongitude();
        //double latitude = chosenCoordinateLatLng.getLatitude();

        int hash = (int) (10000000*longitude);
        return "" + hash;
    }

    private String getUserUpdateHashKey(final Marker marker, int id) {
        double longitude = marker.getPosition().getLongitude();
        //double latitude = chosenCoordinateLatLng.getLatitude();

        int hash = ((int) (10000000*longitude))+id;
        return "" + hash;
    }


    /*Method casts LatLng object to Json, to be able to send it via intent*/
    public String castLatLngToJson(LatLng point){
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.serializeSpecialFloatingPointValues();

        Gson gson = gsonBuilder.create();
        String json = gson.toJson(point, LatLng.class);
        return json;
    }

    /*Method casts LatLng object to Json, to be able to send it via intent*/
    public String castRouteToJson(DirectionsRoute route){
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.serializeSpecialFloatingPointValues();

        Gson gson = gsonBuilder.create();
        String json = gson.toJson(route, DirectionsRoute.class);
        return json;
    }

    private String userUpdateHashFunction(double value, int id) {
        //double latitude = chosenCoordinateLatLng.getLatitude();

        int hash = ((int) (10000000*value))+id;
        return "" + hash;
    }

    private LatLng retreiveLatLngFromJson(String stringExtra) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.serializeSpecialFloatingPointValues();

        Gson gson = gsonBuilder.create();
        LatLng obj = gson.fromJson(stringExtra, LatLng.class);
        return obj;
    }


    /*Method get String that represents a Position Json object.
    * Method retrieve the position object and returns it*/
    public Position retrievePositionFromJson(String posJs) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.serializeSpecialFloatingPointValues();

        Gson gson = gsonBuilder.create();
        Position obj = gson.fromJson(posJs, Position.class);
        return obj;
    }

    public DirectionsRoute retrieveRouteFromJson(String route) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.serializeSpecialFloatingPointValues();

        Gson gson = gsonBuilder.create();
        DirectionsRoute obj = gson.fromJson(route, DirectionsRoute.class);
        return obj;
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



    /*Method signs out user's google account*/
    private void signOut() {
        FirebaseAuth.getInstance().signOut();
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
//                        Intent i = new Intent(Home.this, SignInActivity.class);
//                        startActivity(i);
                        invalidateOptionsMenu();
                    }});
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);


            if(requestCode == NEW_USER_UPDATE && resultCode == USER_UPDATE_CREATED){
                LatLng createdUpdateCoordinateLatLang = retreiveLatLngFromJson(data.getStringExtra(CREATED_UPDATE_FOR_ZOOM));
                String key = userUpdateHashFunction(createdUpdateCoordinateLatLang.getLatitude(),data.getIntExtra("id",0));
                updateScreenUserUpdatesValue(key, createdUpdateCoordinateLatLang, NEW_USER_UPDATE);

                map.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(createdUpdateCoordinateLatLang.getLongitude(),
                                createdUpdateCoordinateLatLang.getLatitude()), ZOOM_LEVEL_CURRENT_LOCATION));
            }

            if (requestCode == 50 && resultCode == 53) {

                Toast.makeText(Home.this, "SIGNED IN SUCCSFULLY",
                        Toast.LENGTH_SHORT).show();
                invalidateOptionsMenu();
            }

            if (requestCode == 51 && resultCode == 53) {

                Intent intent = new Intent(Home.this, EditorPanelActivity.class);
                startActivity(intent);
            }

        } catch (Exception ex) {
            Toast.makeText(Home.this, ex.toString(),
                    Toast.LENGTH_SHORT).show();
        }



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
                    Toast.makeText(Home.this, "point is null", Toast.LENGTH_SHORT).show();
                    return;
                }

                String type = point.get("type").toString();
                if(mode == NEW_USER_UPDATE){
                    addMarkerForUserUpdate(createdCoordinate,
                            (String)point.get("title"), (String)point.get("snippet"),
                            (int)(long) UserUpdate.whatIsTheLogoForType(type));
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


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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

    protected void initCameraListener() {
        map.addOnCameraIdleListener(clusterManagerPlugin);
        try {
            addItemsToClusterPlugin(R.raw.points);
        } catch (JSONException exception) {
            Toast.makeText(this, "Problem reading list of markers.", Toast.LENGTH_LONG).show();
        }
    }

    private void addItemsToClusterPlugin(int rawResourceFile) throws JSONException {
        InputStream inputStream = getResources().openRawResource(rawResourceFile);
        List<MyItem> items = new MyItemReader().read(inputStream);
        clusterManagerPlugin.addItems(items);
        clusterManagerPlugin.cluster();
    }

    /**
     * Custom class for use by the marker cluster plugin
     */
    public static class MyItem implements ClusterItem {
        private final LatLng position;
        private String title;
        private String snippet;

        public MyItem(double lat, double lng) {
            position = new LatLng(lat, lng);
            title = null;
            snippet = null;
        }

        public MyItem(double lat, double lng, String title, String snippet) {
            position = new LatLng(lat, lng);
            this.title = title;
            this.snippet = snippet;
        }

        @Override
        public LatLng getPosition() {
            return position;
        }

        @Override
        public String getTitle() {
            return title;
        }

        @Override
        public String getSnippet() {
            return snippet;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public void setSnippet(String snippet) {
            this.snippet = snippet;
        }
    }

    /**
     * Custom class which reads JSON data and creates a list of MyItem objects
     */
    public static class MyItemReader {

        private static final String REGEX_INPUT_BOUNDARY_BEGINNING = "\\A";

        public List<MyItem> read(InputStream inputStream) throws JSONException {
            List<MyItem> items = new ArrayList<MyItem>();
            String json = new Scanner(inputStream).useDelimiter(REGEX_INPUT_BOUNDARY_BEGINNING).next();
            JSONArray array = new JSONArray(json);
            for (int i = 0; i < array.length(); i++) {
                String title = null;
                String snippet = null;
                JSONObject object = array.getJSONObject(i);

                 String pos = object.getString("position");
                 Position position = retrievePositionFromJson(pos);

                double lat = position.getLatitude();
                double lng = position.getLongitude();
                if (!object.isNull("title")) {
                    title = object.getString("title");
                }
                if (!object.isNull("snippet")) {
                    snippet = object.getString("snippet");
                }
                items.add(new MyItem(lat, lng, title, snippet));

            }
            return items;
        }
        public Position retrievePositionFromJson(String posJs) {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.serializeSpecialFloatingPointValues();

            Gson gson = gsonBuilder.create();
            Position obj = gson.fromJson(posJs, Position.class);
            return obj;
        }
    }








}

