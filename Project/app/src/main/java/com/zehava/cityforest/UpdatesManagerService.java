package com.zehava.cityforest;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerView;
import com.mapbox.mapboxsdk.annotations.MarkerViewOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.services.commons.models.Position;

import java.sql.Date;
import java.util.Map;
import java.util.ArrayList;

/**
 * Created by avigail on 09/12/17.
 */

public class UpdatesManagerService extends Service {

    private FirebaseDatabase database;
    private DatabaseReference updates;
    private ICallback iCallback;
    private long lastupdated;

    private ArrayList<Marker> newmarkers,oldmarkers;

    Handler handler = new Handler();
    private final IBinder mBinder = new LocalBinder();

    Runnable serviceRunnable = new Runnable() {
        @Override
        public void run() {
            LoopThroughUpdates();

            handler.postDelayed(this, 1000);
        }
    };


    @Override
    public void onCreate() {
        super.onCreate();

        database = FirebaseDatabase.getInstance();
        updates = database.getReference("user_updates");

        newmarkers=new ArrayList<>();
        oldmarkers=new ArrayList<>();

        lastupdated=0;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        stopCounter();


    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        startCounter();

        return START_NOT_STICKY;
    }

    private void LoopThroughUpdates() {

        long cutoff = (System.currentTimeMillis() - (1000 * 60 * 60*24)) / 1000;
        Query oldItems = updates.orderByChild("updated").endAt(cutoff);
        oldItems.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    Map<String, Object> point = ((Map<String, Object>) itemSnapshot.getValue());

                    if (point != null) {
                        createAndAddMarker(point, true);

                        String key = itemSnapshot.getKey();
                        updates.child(key).removeValue();

                    }

                }

                iCallback.onUserUpdateNotify(ICallback.USER_UPDATES_CLASS.UPDATE_REMOVED, oldmarkers);
                oldmarkers.clear();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });


        Query newItems = updates.orderByChild("updated").startAt(lastupdated);
        newItems.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    Map<String, Object> point = ((Map<String, Object>) itemSnapshot.getValue());

                    if (point != null) {
                        createAndAddMarker(point, false);
                    }

                }

                iCallback.onUserUpdateNotify(ICallback.USER_UPDATES_CLASS.UPDATE_CREATED, newmarkers);
                newmarkers.clear();

                lastupdated = System.currentTimeMillis() / 1000;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });

    }

    public class LocalBinder extends Binder {
        public UpdatesManagerService getServiceInstance(){
            return UpdatesManagerService.this;
        }
    }

    //Here Activity register to the service as Callbacks client
    public void registerClient(Activity activity){
        this.iCallback = (ICallback) activity;
    }

    public void startCounter(){

        handler.postDelayed(serviceRunnable, 0);

    }

    public void stopCounter(){
        handler.removeCallbacks(serviceRunnable);
    }

    private void createAndAddMarker(Map<String, Object> point, boolean old){

        String positionJSON = (String) point.get("position");
        Position position = retrievePositionFromJson(positionJSON);

        /*Creating the marker*/
        LatLng latlng = new LatLng(
                position.getLongitude(),
                position.getLatitude());


        MarkerViewOptions markerViewOptions = new MarkerViewOptions()
                .position(latlng)
                .title((String)point.get("title"))
                .snippet((String)point.get("snippet"));

        IconFactory iconFactory = IconFactory.getInstance(UpdatesManagerService.this);
        Icon icon = iconFactory.fromResource((int) (long)point.get("logo"));
        markerViewOptions.getMarker().setIcon(icon);

        if(old)
            oldmarkers.add(markerViewOptions.getMarker());
        else
            newmarkers.add(markerViewOptions.getMarker());


    }

    public Position retrievePositionFromJson(String posJs) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.serializeSpecialFloatingPointValues();

        Gson gson = gsonBuilder.create();
        Position obj = gson.fromJson(posJs, Position.class);
        return obj;
    }


}
