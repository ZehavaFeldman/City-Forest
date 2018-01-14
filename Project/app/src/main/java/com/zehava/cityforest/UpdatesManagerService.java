package com.zehava.cityforest;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
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
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.services.commons.models.Position;

import java.sql.Date;
import java.util.Map;

/**
 * Created by avigail on 09/12/17.
 */

public class UpdatesManagerService extends Service {

    private FirebaseDatabase database;
    private DatabaseReference updates;
    private ICallback iCallback;
    private boolean finshed_looping;

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
        finshed_looping = true;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();


    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return START_NOT_STICKY;
    }

    private void LoopThroughUpdates() {

        long cutoff = System.currentTimeMillis() - 1000*60*10;
        Query oldItems = updates.orderByChild("timestamp").endAt(cutoff);
        oldItems.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot itemSnapshot: snapshot.getChildren()) {
                    Map<String, Object> point = ((Map<String, Object>) itemSnapshot.getValue());
                  //  itemSnapshot.getRef().removeValue();
                    if(point != null)
                        iCallback.onUserUpdateNotify(ICallback.USER_UPDATES_CLASS.UPDATE_REMOVED, (int) (long)point.get("logo"));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });



//        finshed_looping = false;
//
//        updates.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                Map<String, Object> pointsMap = (Map<String, Object>) dataSnapshot.getValue();
//                if (pointsMap == null) {
//                    finshed_looping = true;
//                    return;
//                }
//
//                for (Map.Entry<String, Object> entry : pointsMap.entrySet()) {
//                    /*For each coordinate in the database, we want to create a new marker
//                    * for it and to show the marker on the map*/
//                    Map<String, Object> point = ((Map<String, Object>) entry.getValue());
//                    /*Now the object 'cor' holds a *map* for a specific coordinate*/
//                    if (point != null) {
//                        long last_updated = (long) point.get("updated");
//                        long time_space = (long) point.get("update_time_space");
//                        if (last_updated * 1000 + time_space > System.currentTimeMillis()) {
//                            updates.child(entry.getKey()).removeValue();
//
//                            if(iCallback!=null)
//                                iCallback.onUserUpdateNotify(ICallback.USER_UPDATES_CLASS.UPDATE_REMOVED, (int) point.get("id"));
//
//                        }
//
//                    }
//
//                }
//
//                finshed_looping = true;
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//                finshed_looping = true;
//
//            }
//        });
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
        //Toast.makeText(getApplicationContext(), "Counter started", Toast.LENGTH_SHORT).show();
    }

    public void stopCounter(){
        handler.removeCallbacks(serviceRunnable);
    }


}
