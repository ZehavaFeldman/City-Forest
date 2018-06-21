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
//import android.graphics.Bitmap;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.UploadTask;
import com.mapbox.api.directions.v5.models.DirectionsRoute;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.zehava.cityforest.FirebaseUtils;
import com.zehava.cityforest.ICallback;
import com.zehava.cityforest.Managers.IconManager;
import com.zehava.cityforest.Managers.ImagePicker;
import com.zehava.cityforest.Managers.JsonParserManager;
import com.zehava.cityforest.Managers.LocaleManager;
import com.zehava.cityforest.Managers.PMethods;
import com.zehava.cityforest.Models.Image;
import com.zehava.cityforest.Models.PointOfInterest;
import com.zehava.cityforest.Models.Track;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
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
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.services.android.telemetry.location.LocationEngine;
import com.mapbox.services.android.telemetry.location.LocationEngineListener;
import com.mapbox.services.android.telemetry.permissions.PermissionsListener;
import com.mapbox.services.android.telemetry.permissions.PermissionsManager;

import com.mapbox.services.commons.geojson.LineString;
import com.mapbox.services.commons.models.Position;

import com.zehava.cityforest.R;
import com.zehava.cityforest.UpdatesManagerService;


import java.io.ByteArrayOutputStream;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import 	java.util.UUID;

import static com.zehava.cityforest.Constants.CURRENT_USER_NAME;
import static com.zehava.cityforest.Constants.DEFAULT_JERUSALEM_COORDINATE;
import static com.zehava.cityforest.Constants.EXTRA_MODEL_KEY;
import static com.zehava.cityforest.Constants.IMAGE_NAME;
import static com.zehava.cityforest.Constants.LAST_LOCATION_SAVED;
import static com.zehava.cityforest.Constants.MOVE_MARKER;
import static com.zehava.cityforest.Constants.NEW_USER_UPDATE;
import static com.zehava.cityforest.Constants.PICK_IMAGE_REQUEST;
import static com.zehava.cityforest.Constants.RC_SIGN_IN;
import static com.zehava.cityforest.Constants.ROUTE_LINE_WIDTH;
import static com.zehava.cityforest.Constants.SELECTED_TRACK;
import static com.zehava.cityforest.Constants.SET_FROM_PREFS;
import static com.zehava.cityforest.Constants.SHOW_DETAILS_POPUP;
import static com.zehava.cityforest.Constants.SHOW_TRACK_POPUP;
import static com.zehava.cityforest.Constants.UPDATE_POSITION;
import static com.zehava.cityforest.Constants.ZOOM_LEVEL_CURRENT_LOCATION;
import static com.zehava.cityforest.Constants.ZOOM_LEVEL_MARKER_CLICK;
import static com.mapbox.services.android.telemetry.location.AndroidLocationEngine.getLocationEngine;

public class HomeActivity extends AppCompatActivity implements PermissionsListener, ICallback {

    private FloatingActionButton floatingActionButton;
    private LocationEngine locationEngine;
    private LocationEngineListener locationEngineListener;
    private PermissionsManager permissionsManager;

    private ProgressBar loading_map_progress_bar;

    private MapView mapView;
    private MapboxMap map;

    private PolylineOptions routeLine,currRouteLine;
    private DatabaseReference tracks;
    private DatabaseReference points_of_interest;
    private DatabaseReference user_updates;
    private DatabaseReference images;

    private FirebaseStorage storage;
    private StorageReference storageReference;


    private TextView likes_count;

    private FloatingActionButton createUserUpdate;
    private FloatingActionButton camera, cancle;
    private FloatingActionButton uploadImage, showAllImages;
    Button post;

    private PopupWindow mPopupWindow, mTrackPopupWindow;
    private RelativeLayout mRelativeLayout;

    String uid,uname,userhash, owner;
    private FirebaseAuth mAuth;
    private StorageReference reallImgref;

    Snackbar mySnackbar;
    Boolean expand=false;
    Boolean expandable=false;

//    private ClusterManagerPlugin<MyItem> clusterManagerPlugin;

    Intent serviceIntent;
    UpdatesManagerService myService;

    TextView title;
    TextView update_owner;
    TextView discreption;
    TextView username;
    Button track_details;
    Button read_more;
    ImageView pointOfInterestImageView;
    Uri filePath;
    Bitmap bmp;
    Map<String,Map<String,Marker>> points_for_track;
    Map<String,String> dirUpdates;
    Map<String,Object> u_update;
    DirectionsRoute currRoute;

    boolean mBound = false;

    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);


        LocaleManager.setLocale(this);
        IconManager.getInstance().generateIcons(IconFactory.getInstance(this));

        /*Mapbox and firebase initializations*/
        Mapbox.getInstance(this, getString(R.string.access_token));
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        //MapboxAccountManager.start(this,getString(R.string.access_token));
        setContentView(R.layout.activity_home);

        // Get the location engine object for later use.
        locationEngine = getLocationEngine(this);
        locationEngine.activate();

        mapView = findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new myOnMapReadyCallback());

        points_of_interest = FirebaseUtils.getPointsRef();
        tracks = FirebaseUtils.getTracksRef();
        user_updates = FirebaseUtils.getUserUpdatesRef();
        images = FirebaseUtils.getImagesRef();


        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();


        mRelativeLayout = findViewById(R.id.mapLayout);

        SHOW_DETAILS_POPUP = false;
        SHOW_TRACK_POPUP = false;
        MOVE_MARKER = false;
        loading_map_progress_bar = (ProgressBar)findViewById(R.id.loadingMapProgress);

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
                if (map != null) {
                    sendUpdate(!map.isMyLocationEnabled());
                }

            }
        });

        // Configure sign-in with facebook
        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
            new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {

                    handleFacebookAccessToken(loginResult.getAccessToken());
                    // App code
                }

                @Override
                public void onCancel() {
                    // App code
                }

                @Override
                public void onError(FacebookException exception) {
                    // App code
                }
            });


        mAuth = FirebaseAuth.getInstance();

        loading_map_progress_bar.setVisibility(View.VISIBLE);
        points_for_track = new HashMap<>();
        points_for_track.clear();
        dirUpdates = new HashMap<>();

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.editor_layout);
        mySnackbar = Snackbar.make(linearLayout,
                R.string.sign_in_message, Snackbar.LENGTH_LONG)
                .setAction(R.string.sign_in_action, new MySignInListener());

        initPopupWindow();

    }


    private void signIn() {

        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email"));

    }



    private void handleFacebookAccessToken(AccessToken token) {

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(HomeActivity.this, getString(R.string.signin_success),
                                    Toast.LENGTH_SHORT).show();

                            userhash = PMethods.getInstance().createNewUser(
                                    FirebaseAuth.getInstance().getCurrentUser(),
                                    AccessToken.getCurrentAccessToken().getToken());

                            invalidateOptionsMenu();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(HomeActivity.this, getString(R.string.authentication_failed),
                                    Toast.LENGTH_SHORT).show();

                        }


                    }
                });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        final MenuInflater inflater = getMenuInflater();

        //user not loged in show login button
        final FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            inflater.inflate(R.menu.not_signedin_menu, menu);
            uname = getString(R.string.not_connected);
        }
        else {

            inflater.inflate(R.menu.app_menu, menu);

            uname = currentUser.getDisplayName();
            uid = currentUser.getUid();
            userhash = uid;


        }

        MenuItem item = menu.findItem(R.id.spinner);
        final Spinner spinner = (Spinner) MenuItemCompat.getActionView(item);

        ArrayList<String> temp = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.spinner_list_item_array)));

        temp.add(0, uname);
        String carArr[] = temp.toArray(new String[temp.size()]);


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.propfile_spinner_item, carArr);
        adapter.setDropDownViewResource(R.layout.propfile_spinner_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(0, false);

        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            public void onItemSelected(
                    AdapterView<?> parent, View view, int position, long id) {

                //user name  nothing to do on click
                if (position == 0) {

                }

                //switch to editor mode
                if (position == 1) {
                    if(uid == null){
                        Toast.makeText(HomeActivity.this,getString(R.string.sign_in_message),Toast.LENGTH_LONG).show();
                        spinner.setSelection(0);
                    }
                    else {
                        Intent intent = new Intent(HomeActivity.this, EditorPanelActivity.class);
                        saveCameraPositionToPrefs();
                        startActivity(intent);
                        spinner.setSelection(0);
                    }

                }
            }

            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return true;


    }

    // side menu options
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent i;
        switch(item.getItemId()){
            case R.id.home:
                return true;
            case R.id.aboutActivity:
                i = new Intent(this, MyNavigationActivity.class);
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

            case R.id.signOut:
                signOut();
                return true;
            case R.id.signIn:
                signIn();
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
        if(SHOW_DETAILS_POPUP) {
            mPopupWindow.dismiss();
            return;
        }
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

    /*override Icallbacks methods
    here we use icallback to detect user_updates updates
    add and remove update marker from map accordingly
    */
    @Override
    public void onDraggableNotify(DRAGGABLE_CALSS draggableIcall) {

    }

    @Override
    public void onUserUpdateNotify(USER_UPDATES_CLASS userUpdatesClass, ArrayList<Marker> updateMarkers) {

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
        else if(userUpdatesClass == USER_UPDATES_CLASS.UPDATE_REMOVED){

            if(map != null ) {
                Iterator<Marker> allMarkersIterator = map.getMarkers().iterator();

                while (allMarkersIterator.hasNext()) {
                    Marker markerItemAll = allMarkersIterator.next();

                    for (Marker markerItemRemove : updateMarkers) {
                        String key = PMethods.getInstance().getUserUpdateHashKey(markerItemRemove);
                        if (PMethods.getInstance().getUserUpdateHashKey(markerItemAll).equals(key)) {
//                    if (markerItemAll.toString().equals(markerItemRemove.toString())) {
                            allMarkersIterator.remove();
                            map.removeMarker(markerItemAll);
                        }
                    }
                }
            }

        }
    }

    //when map ready call function to add tracks and point of interest to map
    private class myOnMapReadyCallback implements OnMapReadyCallback {
        @Override
        public void onMapReady(MapboxMap mapboxMap) {
            map = mapboxMap;

            map.setOnMapClickListener(new MyOnMapClickListener());
            map.getMarkerViewManager().setOnMarkerViewClickListener (new MyOnMarkerClickListener());
            map.setStyleUrl(Style.OUTDOORS);
            Intent intent = getIntent();
            if(intent!= null && intent.getBooleanExtra(SET_FROM_PREFS,false)) {
                setCameraPositionFromPrefs();
            }
            else {
                showCurrentLocation(true);
            }

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
        //    Toast.makeText(HomeActivity.this, "onServiceConnected called", Toast.LENGTH_SHORT).show();
            // We've binded to LocalService, cast the IBinder and get LocalService instance
            UpdatesManagerService.LocalBinder binder = (UpdatesManagerService.LocalBinder) service;
            myService = binder.getServiceInstance(); //Get instance of your service!
            myService.registerClient(HomeActivity.this); //Activity register in the service as client for callabcks!
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
         //   Toast.makeText(HomeActivity.this, "onServiceDisconnected called dis", Toast.LENGTH_SHORT).show();
            mBound = false;
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
                    DirectionsRoute route = JsonParserManager.getInstance().retrieveRouteFromJson(route_st);
                    drawRoute(route, Color.RED);
                }
                loading_map_progress_bar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                loading_map_progress_bar.setVisibility(View.INVISIBLE);
                Toast.makeText(HomeActivity.this, getString(R.string.database_error),
                        Toast.LENGTH_SHORT).show();

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
                            position.getLatitude(),
                            position.getLongitude());

                    addMarkerForPointOfInterest(latlng, (String)point.get("title"), (String)point.get("snippet"), (String) point.get("type"));
                }
                loading_map_progress_bar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                loading_map_progress_bar.setVisibility(View.INVISIBLE);
                Toast.makeText(HomeActivity.this, getString(R.string.database_error),
                        Toast.LENGTH_SHORT).show();
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


    private void initPopupWindow(){

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);


        mTrackPopupWindow = new PopupWindow(
                inflater.inflate(R.layout.track_details_popup_view, null),
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
           // if(!SHOW_DETAILS_POPUP) {
                for (Object o : points_for_track.entrySet()) {
                    Map.Entry pair = (Map.Entry) o;
                    HashMap<String,Marker> child = (HashMap<String,Marker>)pair.getValue();
                    for(Object c: child.entrySet()) {
                        Map.Entry cpair = (Map.Entry) c;
                        Marker marker = (Marker) cpair.getValue();
                        marker.setIcon(IconManager.getInstance().getIconForType((String)pair.getKey(),true));
                    }

                }
                map.removePolyline(currRouteLine.getPolyline());
                drawRoute(currRoute,Color.RED);
                points_for_track.clear();
                SHOW_TRACK_POPUP = false;
                //showDefaultLocation();
                showCurrentLocation(true);
           // }

            }
        });


        mPopupWindow = new PopupWindow(
                inflater.inflate(R.layout.coordinate_details_popup_view, null),
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
//                if(!points_for_track.isEmpty()){
//                    //SHOW_TRACK_POPUP = true;
//                    mTrackPopupWindow.showAtLocation(mRelativeLayout, Gravity.BOTTOM, 0, 0);
//                }
                //showDefaultLocation();
                showCurrentLocation(true);

            }
        });

    }

    private void initTrackPopup(final  Map<String, Object> track, final String userHashkey){

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.track_details_popup_view, null);
        mTrackPopupWindow.setContentView(view);

        title = mTrackPopupWindow.getContentView().findViewById(R.id.main_content);
        discreption = mTrackPopupWindow.getContentView().findViewById(R.id.minor_content);
        track_details = mTrackPopupWindow.getContentView().findViewById(R.id.goto_full_des);
        read_more = mTrackPopupWindow.getContentView().findViewById(R.id.read_more);
        post = mTrackPopupWindow.getContentView().findViewById(R.id.post);

        title.setText(track.get("track_name").toString());
        discreption.setText(track.get("additional_info").toString());


        track_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, SelectedTrackDetailsActivity.class);
                intent.putExtra(SELECTED_TRACK, String.valueOf(track.get("db_key")));
                startActivity(intent);
            }
        });

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(HomeActivity.this, PostListActivity.class);
                i.putExtra(EXTRA_MODEL_KEY, track.get("db_key").toString());
                startActivity(i);
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
        DatabaseReference childref = FirebaseUtils.getUserRef(userHashkey);

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
                Toast.makeText(HomeActivity.this, getString(R.string.database_error),
                        Toast.LENGTH_SHORT).show();
            }
        });

        RatingBar ratingBar = mTrackPopupWindow.getContentView().findViewById(R.id.ratingBar);
       

        Float d = Float.valueOf(track.get("star_count").toString());
        Object o = (Object) d;
        ratingBar.setRating((float) o);

        if(uid == null)
            ratingBar.setIsIndicator(true);

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if(fromUser)
                    onStarClicked(track, rating, ratingBar);
            }
        });

        LinearLayout likesWrp = mTrackPopupWindow.getContentView().findViewById(R.id.like_wrp);

        likes_count = mTrackPopupWindow.getContentView().findViewById(R.id.like_count);
        likes_count.setText(String.valueOf( track.get("like_count")));

        likesWrp.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(uid== null) {
                    Toast.makeText(HomeActivity.this,  R.string.sign_in_message,
                            Toast.LENGTH_LONG).show();
                    return true;
                }
                   // mySnackbar.show();
                else
                    onLikeClicked(track, likes_count);
                return true;

            }
        });

        mTrackPopupWindow.showAtLocation(mRelativeLayout, Gravity.BOTTOM, 0, 0);
    }

    private void initMarkerPopup(final Marker marker, final String type, final String imageUUID, final String userHashkey){
        expand=false;
        expandable=true;

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.coordinate_details_popup_view, null);
        mPopupWindow.setContentView(view);

        title = mPopupWindow.getContentView().findViewById(R.id.main_content);
        discreption = mPopupWindow.getContentView().findViewById(R.id.minor_content);
        username = mPopupWindow.getContentView().findViewById(R.id.user_id);
        read_more = mPopupWindow.getContentView().findViewById(R.id.read_more);
        camera = mPopupWindow.getContentView().findViewById(R.id.camera);
        post = mPopupWindow.getContentView().findViewById(R.id.post);
        cancle = mPopupWindow.getContentView().findViewById(R.id.cancle);
        showAllImages = mPopupWindow.getContentView().findViewById(R.id.show_all_imges);
        uploadImage = mPopupWindow.getContentView().findViewById(R.id.upload_imge);
        pointOfInterestImageView = mPopupWindow.getContentView().findViewById(R.id.firebase_storage_image);

        final String imageName = PMethods.getInstance().getMarkerHashKey(marker);
        if(imageUUID != null) {
            reallImgref = storageReference.child(imageUUID);
        }

        if(cancle != null) {
            cancle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(imageUUID != null && !imageUUID.isEmpty()) {
                        Glide.with(HomeActivity.this /* context */)
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
                        pointOfInterestImageView.setImageResource(R.drawable.placeholder_image_upload);
                    }
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

        if(post != null) {
            post.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(HomeActivity.this, PostListActivity.class);
                    i.putExtra(EXTRA_MODEL_KEY, PMethods.getInstance().getMarkerHashKey(marker));
                    startActivity(i);
                }
            });
        }

        if(showAllImages != null){
            showAllImages.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(HomeActivity.this, PointOfInterestGallary.class);
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
                            marker.getPosition().getLatitude(),
                            marker.getPosition().getLongitude(),
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


        FirebaseUtils.getUserRef(userHashkey).addListenerForSingleValueEvent(new ValueEventListener() {
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
                Toast.makeText(HomeActivity.this, getString(R.string.database_error),
                        Toast.LENGTH_SHORT).show();
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


        if(imageUUID != null) {
            Glide.with(HomeActivity.this /* context */)
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
                    Intent imagePreview = new Intent(HomeActivity.this, ImageFullScreenPreview.class);
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
                if(updates == null)
                    return;

                    u_update = (Map<String,Object>)updates.get(key);
                    if(u_update!=null){

                        long time;
                        TextView title = mPopupWindow.getContentView().findViewById(R.id.update_main_content);
                        title.setText(update.getTitle());

                        TextView update_created = mPopupWindow.getContentView().findViewById(R.id.created);
                        update_owner= mPopupWindow.getContentView().findViewById(R.id.owner);
                        if(u_update.get("uuid")!=null ) {

                            DatabaseReference childref = FirebaseUtils.getUserRef((String) u_update.get("uuid"));

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
                                    Toast.makeText(HomeActivity.this, getString(R.string.database_error),
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
                Toast.makeText(HomeActivity.this, getString(R.string.database_error),
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
                    permissionsManager = new PermissionsManager(HomeActivity.this);
                    if (!PermissionsManager.areLocationPermissionsGranted(HomeActivity.this)) {
                        permissionsManager.requestLocationPermissions(HomeActivity.this);
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
                                Toast.makeText(HomeActivity.this, getString(R.string.notification_removed),
                                        Toast.LENGTH_LONG).show();
                            }
                            else{
                                Toast.makeText(HomeActivity.this, getString(R.string.remove_update_alert),
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                        else{
                            Toast.makeText(HomeActivity.this, getString(R.string.location_not_found),
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
                            Toast.makeText(HomeActivity.this, getString(R.string.notification_removed),
                                    Toast.LENGTH_LONG).show();
                            mPopupWindow.dismiss();
                        }
                        else{
                            Toast.makeText(HomeActivity.this, getString(R.string.remove_update_alert),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                    else{
                        Toast.makeText(HomeActivity.this, getString(R.string.location_not_found),
                                Toast.LENGTH_LONG).show();
                    }
                }

            }
        });


        mPopupWindow.showAtLocation(mRelativeLayout, Gravity.CENTER, 0, 0);

    }



    private void onLikeClicked(final Map<String,Object> track, final TextView textview) {
        String key= (String) track.get("db_key");
        DatabaseReference trackRef = tracks.child(key);

        trackRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {

                final Track t = mutableData.getValue(Track.class);

                if(t == null) {
                    return Transaction.success(mutableData);
                }

                    /* when user clicked on like check if his uid is in tacks likes array-
                    if found decrese likes count and remove him from array, user is deleting his vote. otherwise increse like count
                     */

                if (t.getLikes().containsKey(uid)) {
                    // Unstar the track and remove self from stars
                    t.setLike_count(t.getLike_count() - 1);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            textview.setText(String.valueOf((int)t.getLike_count()));
                        }
                    });


                    Map<String, Boolean> temp = t.getLikes();
                    temp.remove(uid);
                    t.setLikes(temp);
                } else {
                   // Star the track and add self to stars
                    t.setLike_count(t.getLike_count() + 1);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            textview.setText(String.valueOf((int)t.getLike_count()));

                        }
                    });

                    Map<String, Boolean> temp = t.getLikes();
                    temp.put(uid,true);
                    t.setLikes(temp);

                }
                Toast.makeText(HomeActivity.this, R.string.success_message,
                        Toast.LENGTH_LONG).show();
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


    private void onStarClicked(final Map<String,Object> track, final Float stars, final RatingBar ratingBar) {
        String key= (String) track.get("db_key");
        DatabaseReference trackRef = tracks.child(key);

        trackRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {

                Track t = mutableData.getValue(Track.class);

                if(t == null) {
                    return Transaction.success(mutableData);
                }

                float usersCount = t.getStars().size();
                float newStarCount, oldStars;

                if (t.getStars().containsKey(uid)) {
                    oldStars= t.getStar_count()*(t.getStars().size()-1);
                    Map<String, Float> temp = t.getStars();
                    temp.remove(uid);
                    temp.put(uid,stars);
                    t.setStars(temp);

                    newStarCount = (oldStars+stars)/usersCount;
                } else {

                    oldStars= t.getStar_count()*t.getStars().size();

                    Map<String, Float> temp = t.getStars();
                    temp.put(uid,stars);
                    t.setStars(temp);

                    newStarCount = (oldStars+stars)/(usersCount+1);

                }



                t.setStar_count(newStarCount);
//                ratingBar.setRating(newStarCount);
                Toast.makeText(HomeActivity.this, R.string.success_message,
                        Toast.LENGTH_LONG).show();
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



    public class MySignInListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {

            // Code to sinin the user
            signIn();
        }
    }

    /* mp click listener- when click detected check if user touced a route*/
    private class MyOnMapClickListener implements MapboxMap.OnMapClickListener{
        @Override
        public void onMapClick(@NonNull final LatLng point) {

            //if popup open user touched map to dissmis
            if(SHOW_DETAILS_POPUP ) {
                mPopupWindow.dismiss();
                return;
            }
            if(SHOW_TRACK_POPUP)
                mTrackPopupWindow.dismiss();


            //loop through tacks and check if touch point is in route points
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
                            String uuid = (String)track.get("uuid");
                            uuid = uuid == null ? "" : uuid;

                            DirectionsRoute route = JsonParserManager.getInstance().retrieveRouteFromJson(route_st);
                            Polyline polyline = getPolyLineFromRoute(route);


                            for (LatLng polyCoords : polyline.getPoints()) {
                                float[] results = new float[1];
                                Location.distanceBetween(point.getLatitude(), point.getLongitude(),
                                        polyCoords.getLatitude(), polyCoords.getLongitude(), results);

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
                                                    markerItemAll.setIcon(IconManager.getInstance().getIconForType(large, true));
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
                        Toast.makeText(HomeActivity.this, getString(R.string.database_error),
                                Toast.LENGTH_SHORT).show();
                    }
                });

            }

        }
    }


    /*Marker clicked listener.*/
    private class MyOnMarkerClickListener implements MapboxMap.OnMarkerViewClickListener{

        @Override
        public boolean onMarkerClick(@NonNull final Marker marker, @NonNull View view, @NonNull MapboxMap.MarkerViewAdapter adapter) {

            if(SHOW_TRACK_POPUP)
                mTrackPopupWindow.dismiss();
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(marker.getPosition().getLatitude(),
                    marker.getPosition().getLongitude()), ZOOM_LEVEL_MARKER_CLICK));

            final String key = PMethods.getInstance().getMarkerHashKey(marker);
            points_of_interest.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Map<String, Object> pointsMap = (Map<String, Object>)dataSnapshot.getValue();
                    Map<String, Object> point = ((Map<String, Object>)pointsMap.get(key));
                    if(point != null) {
                        String type = (String) point.get("type");
                        String imageUUId = (String) point.get("imagePath");
                        String uuid = (String) point.get("uuid");
                        uuid = uuid== null ? "" : uuid;
//                        if (!type.equals("תחנת רכבת")) {
                        SHOW_DETAILS_POPUP = true;
                       // mTrackPopupWindow.dismiss();
                        initMarkerPopup(marker, type,imageUUId, uuid);

//                        }
                    }
                    else{
                        SHOW_DETAILS_POPUP = true;
                       // mTrackPopupWindow.dismiss();
                        detectUpdtesAtpoint(marker);
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(HomeActivity.this, getString(R.string.database_error),
                            Toast.LENGTH_SHORT).show();
                }
            });

            return true;
        }
    }


    private MarkerView addMarkerForPointOfInterest(LatLng point, String title, String snippet, String type){
        MarkerViewOptions markerViewOptions = new MarkerViewOptions()
                .position(point)
                .title(title)
                .snippet(snippet);
        map.addMarker(markerViewOptions);
            

        markerViewOptions.getMarker().setIcon(IconManager.getInstance().getIconForType(type, true));
        MarkerView m = markerViewOptions.getMarker();
        return m;


    }

    private boolean checkIfTransportaionSation(String type){
        return type.equals("תחנת רכבת") || type.equals("תחנת אוטובוס")
                || type.equalsIgnoreCase("Train Station") || type.equalsIgnoreCase("Bus Station");
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
            enableLocation(false);
            finish();
        }
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


    // toggle language via locale manager and restart activty to update UI on language changed
    private void toggleLanguage() {

        LocaleManager.toggaleLang(this);
        saveCameraPositionToPrefs();

        Intent intent = getIntent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.putExtra(SET_FROM_PREFS,true);
        finish();
        startActivity(intent);

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


    private void showDefaultLocation(){
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(DEFAULT_JERUSALEM_COORDINATE.getLatitude(),
                        DEFAULT_JERUSALEM_COORDINATE.getLongitude()), ZOOM_LEVEL_CURRENT_LOCATION));
    }

    // when returning from different activity load map with settings from hared prefs
    public void setCameraPositionFromPrefs() {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        LatLng target = JsonParserManager.getInstance().retreiveLatLngFromJson(preferences.getString("targetJSON",""));
        Float zoom = preferences.getFloat("zoom",ZOOM_LEVEL_CURRENT_LOCATION);
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

    // save map setting  to hared prefs before switching activities to get a smooth trans
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


    private void enableLocation(boolean enabled) {
        if (enabled) {
            // If we have the last location of the user, we can move the camera to that position.
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            Location lastLocation = locationEngine.getLastLocation();
            if (lastLocation != null) {
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lastLocation), ZOOM_LEVEL_CURRENT_LOCATION));
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(HomeActivity.this);
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

                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(HomeActivity.this);
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

    //retrive usr location to set his update at that point
    // user can only send an update at his current location- if not found update will not be sent
    private void sendUpdate(boolean enableGps){
        if (enableGps) {
            // Check if user has granted location permission
            permissionsManager = new PermissionsManager(this);
            if (!PermissionsManager.areLocationPermissionsGranted(this)) {
                permissionsManager.requestLocationPermissions(this);
            } else {
                Location lastLocation = getLastLocation();
                if(lastLocation!= null) {
                    Intent i = new Intent(HomeActivity.this, CreateUserUpdateActivity.class);
                    i.putExtra(CURRENT_USER_NAME, userhash);
                    i.putExtra(UPDATE_POSITION, JsonParserManager.getInstance().castLatLngToJson(new LatLng(lastLocation)));
                    startActivityForResult(i, NEW_USER_UPDATE);
                }
                else{
                    Toast.makeText(this, getString(R.string.location_not_found),
                            Toast.LENGTH_LONG).show();
                }
            }
        } else {
            Location lastLocation = getLastLocation();
            if(lastLocation!= null) {
                Intent i = new Intent(HomeActivity.this, CreateUserUpdateActivity.class);
                i.putExtra(CURRENT_USER_NAME, userhash);
                i.putExtra(UPDATE_POSITION, JsonParserManager.getInstance().castLatLngToJson(new LatLng(lastLocation)));
                startActivityForResult(i, NEW_USER_UPDATE);
            }
            else{
                Toast.makeText(this, getString(R.string.location_not_found),
                        Toast.LENGTH_LONG).show();
            }
        }

    }

    private Location getLastLocation(){
        if (ActivityCompat.checkSelfPermission(HomeActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(HomeActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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



    /*Method signs out user's facebook account*/
    private void signOut() {
        FirebaseAuth.getInstance().signOut();

        AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(
                    AccessToken oldAccessToken,
                    AccessToken currentAccessToken) {

                if (currentAccessToken == null){
                    //User logged out

                    LoginManager.getInstance().logOut();
                    userhash = null;
                    uid = null;
                    invalidateOptionsMenu();
                }
            }
        };

    }

    /*
     upload user image to fire store
     the image is saved in bytes to decrease size
    */
    private void uploadImage(final String imageName, final PointOfInterest pointOfInterest) {

        if(filePath != null || bmp != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle(getString(R.string.uploading));
            progressDialog.show();

            final String uuid = UUID.randomUUID().toString();
            StorageReference ref = storageReference.child("images/"+ imageName +"/"+ uuid);
            UploadTask uploadTask;

            if(bmp != null) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                uploadTask = ref.putBytes(byteArray);
            }
            else
                uploadTask = ref.putFile(filePath);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(HomeActivity.this, getString(R.string.uploaded), Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(HomeActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage(getString(R.string.uploaded)+" "+(int)progress+"%");
                        }
                    });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {

            if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                    && data != null && data.getData() != null )
            {
                bmp = ImagePicker.getImageFromResult(this, resultCode, data);//your compressed bitmap here
                filePath = data.getData();
                pointOfInterestImageView.setImageBitmap(bmp);
                uploadImage.setVisibility(View.VISIBLE);
                camera.setVisibility(View.INVISIBLE);
                cancle.setVisibility(View.VISIBLE);
                showAllImages.setVisibility(View.INVISIBLE);

            }
            else{
                callbackManager.onActivityResult(requestCode, resultCode, data);
            }
            super.onActivityResult(requestCode, resultCode, data);


        } catch (Exception ex) {Toast.makeText(HomeActivity.this, ex.toString(),
                    Toast.LENGTH_SHORT).show();
        }


    }


    private void dialogChooseanUpdate(final ArrayList<String> updates_names){

        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);

        builder.setTitle(getString(R.string.choose_an_update));
        final String[] names = updates_names.toArray(new String[updates_names.size()]);
        builder.setSingleChoiceItems(names, -1,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        findmarkerforupdate(dirUpdates.get(names[which]));
                        dialog.dismiss();
                    }
                });
        builder.setNegativeButton(R.string.dialog_cancel,
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
                Toast.makeText(HomeActivity.this, getString(R.string.database_error),
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

    //unbind service to activity when activity starts
    @Override
    protected void onStop() {
        super.onStop();
        unbindService(mConnection);
        mBound = false;
    }

    //bind service to activity when activity starts
    @Override
    protected void onStart() {
        serviceIntent = new Intent(HomeActivity.this, UpdatesManagerService.class);
        bindService(serviceIntent, mConnection, Context.BIND_AUTO_CREATE); //Binding to the service!
        super.onStart();
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

