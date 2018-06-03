package com.zehava.cityforest.Activities;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

// classes needed to initialize map
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.models.DirectionsWaypoint;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.constants.Style;
import com.mapbox.mapboxsdk.maps.MapView;

// classes needed to add location layer
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import android.location.Location;

import com.mapbox.mapboxsdk.geometry.LatLng;

import android.support.annotation.NonNull;

import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerMode;
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin;
import com.mapbox.services.android.location.LostLocationEngine;
//import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions;
import com.mapbox.services.android.navigation.ui.v5.NavigationViewOptions;
import com.mapbox.services.android.telemetry.location.LocationEngine;
import com.mapbox.services.android.telemetry.location.LocationEngineListener;
import com.mapbox.services.android.telemetry.location.LocationEnginePriority;
import com.mapbox.services.android.telemetry.permissions.PermissionsListener;
import com.mapbox.services.android.telemetry.permissions.PermissionsManager;

// classes needed to add a marker
import com.mapbox.mapboxsdk.annotations.Marker;

// classes to calculate a route
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.util.Log;

// classes needed to launch navigation UI
import android.view.View;
import android.widget.Button;

import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher;
import com.zehava.cityforest.Managers.JsonParserManager;
import com.zehava.cityforest.R;

import static com.zehava.cityforest.Constants.SELECTED_TRACK;
import static com.zehava.cityforest.Constants.TRACK_WAYPOINTS;
import static com.zehava.cityforest.Constants.ZOOM_LEVEL_CURRENT_LOCATION;
import static com.zehava.cityforest.Constants.ZOOM_LEVEL_MARKER_CLICK;

import android.widget.ProgressBar;

public class MyNavigationActivity extends AppCompatActivity implements LocationEngineListener, PermissionsListener {


    private MapView mapView;


    // variables for adding location layer
    private MapboxMap map;
    private PermissionsManager permissionsManager;
    private LocationLayerPlugin locationPlugin;
    private LocationEngine locationEngine;
    private Location originLocation;


    // variables for adding a marker
    private Marker destinationMarker, origionMarker;
    private LatLng originCoord;
    private LatLng destinationCoord;

    private ProgressBar progressBar;
    // variables for calculating and drawing a route
    private Point originPosition;
    private Point destinationPosition;
    private DirectionsRoute currentRoute;
    private static final String TAG = "DirectionsActivity";
    private NavigationMapRoute navigationMapRoute;


    private Button button;
    private List<DirectionsWaypoint> waypoints;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.access_token));
        setTheme(R.style.CustomNavigationViewLight);
        setContentView(R.layout.activity_my_navigation);

        button = findViewById(R.id.startButton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Point origin = originPosition;
                Point destination = destinationPosition;
                // Pass in your Amazon Polly pool id for speech synthesis using Amazon Polly
                // Set to null to use the default Android speech synthesizer
                String awsPoolId = null;
                boolean simulateRoute = false;
                NavigationViewOptions options = NavigationViewOptions.builder()
//                        .origin(origin)
//                        .destination(destination)
                        .directionsRoute(currentRoute)
                        .directionsProfile(DirectionsCriteria.PROFILE_WALKING)
//                        .awsPoolId(awsPoolId)
                        .shouldSimulateRoute(simulateRoute)
                        .build();

                // Call this method with Context from within an Activity
                NavigationLauncher.startNavigation(MyNavigationActivity.this,options);
            }
        });

        progressBar = findViewById(R.id.loadingMapProgress);


        mapView = (MapView) findViewById(R.id.mapViewnav);
        mapView.onCreate(savedInstanceState);
        mapView.setStyleUrl(Style.OUTDOORS);

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final MapboxMap mapboxMap) {


                map = mapboxMap;
                enableLocationPlugin();
                Intent i = getIntent();
                ArrayList<String> list = i.getStringArrayListExtra(TRACK_WAYPOINTS);
                String track_db = i.getStringExtra(SELECTED_TRACK);
                if(list != null){
                    getRouteFromWayPoints(waypoints = JsonParserManager.getInstance().retrieveDirectionsWaypointsFromJson(list));
                }
                else{
                    getRouteFromFirebase(track_db);
                }

//                originCoord = new LatLng(originLocation.getLatitude(), originLocation.getLongitude());
//                mapboxMap.setOnMapClickListener(new MapboxMap.OnMapClickListener() {
//                    @Override
//                    public void onMapClick(@NonNull LatLng point) {
//
//
//                        if(destinationMarker != null) {
//                            mapboxMap.removeMarker(destinationMarker);
//                        }
//                        destinationCoord = point;
//                        destinationMarker = mapboxMap.addMarker(new MarkerOptions()
//                                .position(destinationCoord)
//                        );
//
//
//                        destinationPosition = Point.fromLngLat(destinationCoord.getLongitude(), destinationCoord.getLatitude());
//                        originPosition = Point.fromLngLat(originCoord.getLongitude(), originCoord.getLatitude());
//                        getRoute(originPosition, destinationPosition);
//
//                        button.setEnabled(true);
//                        button.setBackgroundResource(R.color.mapboxBlue);
//
//
//                    }
//
//
//                });






            }

        });


    }

    private void getRouteFromFirebase(final String key){
//        ArrayList<String> trackwaypoints = new ArrayList<>();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference tracks = "https://fir-cityforest.firebaseio.com/tracks";
        DatabaseReference tracks = database.getReference("tracks");

        tracks.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> tracksMap = (Map<String, Object>) dataSnapshot.getValue();
                Map<String, Object> track = (Map<String, Object>) tracksMap.get(key);

                ArrayList<String> trackwaypoints  = new ArrayList<>((List<String>) track.get("waypoints"));
                getRouteFromWayPoints(JsonParserManager.getInstance().retrieveDirectionsWaypointsFromJson(trackwaypoints));

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressBar.setVisibility(View.INVISIBLE);
            }
        });

    }

    private void getRouteFromWayPoints(List<DirectionsWaypoint> waypoints){



        originPosition = waypoints.get(0).location();
        destinationPosition = waypoints.get(waypoints.size()-1).location();

        originCoord = new LatLng(originPosition.latitude(),originPosition.longitude());
        destinationCoord = new LatLng(destinationPosition.latitude(),destinationPosition.longitude());

//        map.addMarker(new MarkerOptions().position(destinationCoord));
//        map.addMarker(new MarkerOptions().position(originCoord));

        getRoute(originPosition, destinationPosition, waypoints);

        button.setEnabled(true);
        button.setBackgroundResource(R.color.mapboxBlue);

    }


    private void getRoute(Point origin, Point destination, List<DirectionsWaypoint> waypoints) {
        NavigationRoute.Builder builder = NavigationRoute.builder()
                .accessToken(Mapbox.getAccessToken())
                .origin(origin)
                .destination(destination)
                .profile(DirectionsCriteria.PROFILE_WALKING);

                for (DirectionsWaypoint waypoint : waypoints) {
                    Point temp = waypoint.location();
                    Point p = Point.fromLngLat(temp.longitude(),temp.latitude());
                    builder.addWaypoint(p);
                }
                builder.build()
                .getRoute(new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                        // You can get the generic HTTP info about the response
                        Log.d(TAG, "Response code: " + response.code());
                        if (response.body() == null) {
                            Log.e(TAG, "No routes found, make sure you set the right user and access token.");
                            return;
                        } else if (response.body().routes().size() < 1) {
                            Log.e(TAG, "No routes found");
                            return;
                        }

                        currentRoute = response.body().routes().get(0);
                        // Draw the route on the map
                        if (navigationMapRoute != null) {
                            navigationMapRoute.removeRoute();
                        } else {
                            navigationMapRoute = new NavigationMapRoute(null, mapView, map, R.style.NavigationMapRoute);
                        }
                        navigationMapRoute.addRoute(currentRoute);
                        setCameraPosition(new LatLng(currentRoute.routeOptions().coordinates().get(0).latitude(),
                                currentRoute.routeOptions().coordinates().get(0).longitude()
                        ), ZOOM_LEVEL_MARKER_CLICK);
                        progressBar.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
                        Log.e(TAG, "Error: " + throwable.getMessage());
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });
    }


    @SuppressWarnings( {"MissingPermission"})
    private void enableLocationPlugin() {
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            // Create an instance of LOST location engine
            initializeLocationEngine();

            locationPlugin = new LocationLayerPlugin(mapView, map, locationEngine);
            locationPlugin.setLocationLayerEnabled(LocationLayerMode.TRACKING);
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    @SuppressWarnings( {"MissingPermission"})
    private void initializeLocationEngine() {
        locationEngine = new LostLocationEngine(MyNavigationActivity.this);
        locationEngine.setPriority(LocationEnginePriority.HIGH_ACCURACY);
        locationEngine.activate();

        Location lastLocation = locationEngine.getLastLocation();
        if (lastLocation != null) {
            originLocation = lastLocation;
            setCameraPosition(lastLocation, ZOOM_LEVEL_CURRENT_LOCATION);
        } else {
            locationEngine.addLocationEngineListener(this);
        }
    }

    private void setCameraPosition(Location location, int zoomLevel) {
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(location.getLatitude(), location.getLongitude()), zoomLevel));
    }

    private void setCameraPosition(LatLng location, int zoomLevel) {
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(
               location, zoomLevel));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {

    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            enableLocationPlugin();
        } else {
            finish();
        }
    }

    @Override
    @SuppressWarnings( {"MissingPermission"})
    public void onConnected() {
        locationEngine.requestLocationUpdates();
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            originLocation = location;
            setCameraPosition(location, ZOOM_LEVEL_CURRENT_LOCATION);
            locationEngine.removeLocationEngineListener(this);
        }
    }

    @Override
    @SuppressWarnings( {"MissingPermission"})
    protected void onStart() {
        super.onStart();
        if (locationEngine != null) {
            locationEngine.requestLocationUpdates();
        }
        if (locationPlugin != null) {
            locationPlugin.onStart();
        }
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (locationEngine != null) {
            locationEngine.removeLocationUpdates();
        }
        if (locationPlugin != null) {
            locationPlugin.onStop();
        }
        mapView.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        if (locationEngine != null) {
            locationEngine.deactivate();
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }


}

