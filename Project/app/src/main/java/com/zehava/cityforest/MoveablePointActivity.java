package com.zehava.cityforest;

import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.LocationSource;
//import com.mapbox.mapboxsdk.Mapbox;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mapbox.mapboxsdk.MapboxAccountManager;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerView;
import com.mapbox.mapboxsdk.annotations.MarkerViewOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.constants.Style;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Projection;
import com.mapbox.services.android.telemetry.location.LocationEngine;
import com.mapbox.services.android.telemetry.location.LocationEngineListener;
import com.mapbox.services.android.telemetry.permissions.PermissionsListener;
import com.mapbox.services.android.telemetry.permissions.PermissionsManager;
import com.mapbox.services.commons.ServicesException;
import com.mapbox.services.commons.models.Position;
import com.mapbox.services.directions.v5.models.DirectionsRoute;
import com.mapbox.services.geocoding.v5.GeocodingCriteria;
import com.mapbox.services.geocoding.v5.MapboxGeocoding;
import com.mapbox.services.geocoding.v5.models.CarmenFeature;
import com.mapbox.services.geocoding.v5.models.GeocodingResponse;

import android.support.v7.util.SortedList;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.FrameLayout;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.zehava.cityforest.Constants.DEFAULT_JERUSALEM_COORDINATE;
import static com.zehava.cityforest.Constants.ZOOM_LEVEL_CURRENT_LOCATION;
import static com.mapbox.services.android.telemetry.location.AndroidLocationEngine.getLocationEngine;

/**
 * Created by avigail on 10/12/17.
 */


public class MoveablePointActivity extends AppCompatActivity implements PermissionsListener, ICallback {

    private LocationEngine locationEngine;
    private MapView mapView;
    private MapboxMap mapboxMap;
    private LocationEngineListener locationEngineListener;
    private PermissionsManager permissionsManager;
    private FloatingActionButton floatingActionButton;
    private Marker droppedMarker;
    private DragAndDrop hoveringMarker;
    private Projection projection;
    private FirebaseDatabase database;
    private DatabaseReference points_of_interest;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MapboxAccountManager.start(this, getString(R.string.access_token));
//        Mapbox.getInstance(this, getString(R.string.access_token));
        setContentView(R.layout.moveable_point_activity);

        floatingActionButton = (FloatingActionButton) findViewById(R.id.location_toggle_fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mapboxMap != null) {
                    toggleGps(!mapboxMap.isMyLocationEnabled());
                }
            }
        });



        database = FirebaseDatabase.getInstance();
        points_of_interest = database.getReference("points_of_interest");
        // Get the location engine object for later use.
        locationEngine = getLocationEngine(this);
        locationEngine.activate();

        // Initialize the mapboxMap view
        mapView = (MapView) findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new myOnMapReadyCallback());


        hoveringMarker = new DragAndDrop(this,this);
        hoveringMarker.setImageResource(R.drawable.blue_marker);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
        hoveringMarker.setLayoutParams(params);
      //  hoveringMarker.setVisibility(View.GONE);
        mapView.addView(hoveringMarker);


    }

    @Override
    public void onDraggableNotify(DRAGGABLE_CALSS draggableIcall) {


        if(draggableIcall == DRAGGABLE_CALSS.VIEW_MOVED){
            if (mapboxMap != null) {

                float coordinateX = hoveringMarker.getX() + (hoveringMarker.getWidth() / 2);
                float coordinateY = hoveringMarker.getY()+(hoveringMarker.getHeight());
                float[] coords = new float[]{coordinateX, coordinateY};
                final LatLng latLng = mapboxMap.getProjection().fromScreenLocation(new PointF(coords[0], coords[1]));
                hoveringMarker.setVisibility(View.INVISIBLE);


                // Create the marker icon the dropped marker will be using.
              //  Icon icon = IconFactory.getInstance(MoveablePointActivity.this).fromResource(R.drawable.blue_marker);

                // Placing the marker on the mapboxMap as soon as possible causes the illusion
                // that the hovering marker and dropped marker are the same.
                droppedMarker = mapboxMap.addMarker(new MarkerViewOptions().position(latLng));
                IconFactory iconFactory = IconFactory.getInstance(MoveablePointActivity.this);
                Icon icon = iconFactory.fromResource(R.drawable.blue_marker);
                droppedMarker.setIcon(icon);

                // Finally we get the geocoding information
                reverseGeocode(latLng);
            }

        }
        if(draggableIcall == DRAGGABLE_CALSS.VIEW_TOUCHED){
            if (mapboxMap != null && droppedMarker!=null) {



                // Lastly, set the hovering marker back to visible.
                PointF point = mapboxMap.getProjection().toScreenLocation(droppedMarker.getPosition());
                mapboxMap.removeMarker(droppedMarker);
                hoveringMarker.setX(point.x-(hoveringMarker.getWidth()/2));
                hoveringMarker.setY(point.y-hoveringMarker.getHeight());
                hoveringMarker.setVisibility(View.VISIBLE);
                droppedMarker = null;
            }

        }

    }

    @Override
    public void onUserUpdateNotify(USER_UPDATES_CLASS userUpdatesClass, int id) {

    }


    private class myOnMapReadyCallback implements OnMapReadyCallback {
        @Override
        public void onMapReady(MapboxMap mapbox) {
            mapboxMap = mapbox;
            mapboxMap.setStyleUrl(Style.OUTDOORS);
            mapboxMap.setOnMarkerClickListener(new MapboxMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(@NonNull Marker marker) {
                    PointF point = mapboxMap.getProjection().toScreenLocation(marker.getPosition());

                    hoveringMarker.setX(point.x-(hoveringMarker.getWidth()/2));
                    hoveringMarker.setY(point.y-hoveringMarker.getHeight());

                    hoveringMarker.setVisibility(View.VISIBLE);
                    droppedMarker = marker;
                    return true;
                }
            });

            showAllPointsOfInterest();

            showDefaultLocation();
        }
    }


    private MarkerView addMarkerForPointOfInterest(LatLng point, String title, String snippet){
        MarkerViewOptions markerViewOptions = new MarkerViewOptions()
                .position(point)
                .title(title)
                .snippet(snippet);
        mapboxMap.addMarker(markerViewOptions);

        IconFactory iconFactory = IconFactory.getInstance(MoveablePointActivity.this);
        Icon icon = iconFactory.fromResource(R.drawable.blue_marker);
        markerViewOptions.getMarker().setIcon(icon);
        return markerViewOptions.getMarker();
    }


    public Position retrievePositionFromJson(String posJs) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.serializeSpecialFloatingPointValues();

        Gson gson = gsonBuilder.create();
        Position obj = gson.fromJson(posJs, Position.class);
        return obj;
    }


    private void showAllPointsOfInterest() {
        /*Reading one time from the database, we get the points of interest map list*/

        points_of_interest.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> pointsMap = (Map<String, Object>)dataSnapshot.getValue();
                if(pointsMap == null) {

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

                    addMarkerForPointOfInterest(latlng, (String)point.get("title"), (String)point.get("snippet"));
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
                           // mapboxMap.selectMarker(droppedMarker);
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

    private void showDefaultLocation(){
        mapboxMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(DEFAULT_JERUSALEM_COORDINATE.getLatitude(),
                        DEFAULT_JERUSALEM_COORDINATE.getLongitude()), 10));
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
                mapboxMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lastLocation), ZOOM_LEVEL_CURRENT_LOCATION));
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
                        mapboxMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location), ZOOM_LEVEL_CURRENT_LOCATION));
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
        mapboxMap.setMyLocationEnabled(enabled);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
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
