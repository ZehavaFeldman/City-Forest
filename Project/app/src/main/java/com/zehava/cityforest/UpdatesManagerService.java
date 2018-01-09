package com.zehava.cityforest;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.services.commons.models.Position;

import java.util.Map;

/**
 * Created by avigail on 09/12/17.
 */

public class UpdatesManagerService extends Service {

    private FirebaseDatabase database;
    private DatabaseReference updates;
    private ICallback iCallback;
    private boolean finshed_looping;

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
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(finshed_looping)
                    LoopThroughUpdates();
            }
        },0);



        return super.onStartCommand(intent, flags, startId);
    }

    private void LoopThroughUpdates() {

        finshed_looping = false;

        updates.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> pointsMap = (Map<String, Object>) dataSnapshot.getValue();
                if (pointsMap == null) {
                    finshed_looping = true;
                    return;
                }

                for (Map.Entry<String, Object> entry : pointsMap.entrySet()) {
                    /*For each coordinate in the database, we want to create a new marker
                    * for it and to show the marker on the map*/
                    Map<String, Object> point = ((Map<String, Object>) entry.getValue());
                    /*Now the object 'cor' holds a *map* for a specific coordinate*/
                    if (point != null) {
                        long last_updated = (long) point.get("updated");
                        long time_space = (long) point.get("update_time_space");
                        if (last_updated * 1000 + time_space > System.currentTimeMillis()) {
                            updates.child(entry.getKey()).removeValue();

                            if(iCallback!=null)
                                iCallback.onUserUpdateNotify(ICallback.USER_UPDATES_CLASS.UPDATE_REMOVED, (int) point.get("id"));

                        }

                    }

                }

                finshed_looping = true;

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                finshed_looping = true;

            }
        });
    }

    public void setCallback(ICallback callback){
        iCallback = callback;
    }


}
