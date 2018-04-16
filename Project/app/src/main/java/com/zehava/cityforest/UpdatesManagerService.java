package com.zehava.cityforest;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
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
import com.zehava.cityforest.Managers.IconManager;
import com.zehava.cityforest.Managers.JsonParserManager;
import com.zehava.cityforest.Models.UserUpdate;

import java.sql.Date;
import java.util.Map;
import java.util.ArrayList;

/**
 * Created by avigail on 09/12/17.
 */


/*adding and removing user_updates from database and map requires lots of call to database
 we use a service binded to activity to do the work in the background,
 avoiding the work from being done on the main thread of the activity
 notifay activity when needed using callbacks*/

public class UpdatesManagerService extends Service {

    private FirebaseDatabase database;
    private DatabaseReference updates;
    private ICallback iCallback;
    private long lastupdated;

    private ArrayList<Marker> newmarkers,oldmarkers;

    Handler handler = new Handler();
    private final IBinder mBinder = new LocalBinder();

    //using a runnable to do needed work non stop
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

        //firebase refs
        database = FirebaseDatabase.getInstance();
        updates = database.getReference("user_updates");

        //mrakers arrays to send to activity
        newmarkers=new ArrayList<>();
        oldmarkers=new ArrayList<>();

        //when service started set lastupdated to 0 inorder to get all updates
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

        //start doing work
        startCounter();

        return START_NOT_STICKY;
    }

    private void LoopThroughUpdates() {

        /* using query to get only old updates from database*/
        long cutoff = (System.currentTimeMillis() - (1000 * 60 * 60*24)) / 1000;
        Query oldItems = updates.orderByChild("updated").endAt(cutoff);
        oldItems.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    Map<String, Object> point = ((Map<String, Object>) itemSnapshot.getValue());

                    if (point != null) {

                        createAndAddMarker(point, true);

                        //remove data from database
                        String key = itemSnapshot.getKey();
                        updates.child(key).removeValue();

                    }

                }

                /*notifay activity on updates removed and then clear array
                activity will remove marker from map
                */
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

                /*notifay activity on new updates and then clear array
                and update lastupdates to insure that next time we will recive only updates we havent recived previsly
                activity will remove marker from map
                */
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
        IconManager.getInstance().generateIcons(IconFactory.getInstance(this));
    }

    public void startCounter(){

        handler.postDelayed(serviceRunnable, 0);

    }

    public void stopCounter(){
        lastupdated=0;
        handler.removeCallbacks(serviceRunnable);
    }


    //creating a marker for user_updates and sending ready markers to activity
    private void createAndAddMarker(Map<String, Object> point, boolean old){

        String positionJSON = (String) point.get("position");
        Position position = JsonParserManager.getInstance().retrievePositionFromJson(positionJSON);

        /*Creating the marker*/
        LatLng latlng = new LatLng(
                position.getLongitude(),
                position.getLatitude());


        MarkerViewOptions markerViewOptions = new MarkerViewOptions()
                .position(latlng)
                .title((String)point.get("title"))
                .snippet((String)point.get("snippet"));


        markerViewOptions.getMarker().setIcon(IconManager.getInstance().getIconForType((String)point.get("type")));


        if(old)
            oldmarkers.add(markerViewOptions.getMarker());
        else
            newmarkers.add(markerViewOptions.getMarker());


    }



}
