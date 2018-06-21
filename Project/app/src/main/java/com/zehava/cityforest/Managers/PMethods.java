package com.zehava.cityforest.Managers;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.zehava.cityforest.FirebaseUtils;
import com.zehava.cityforest.Models.User;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by avigail on 04/05/18.
 */

public class PMethods {

    private static PMethods instance;
    private User user;

    private PMethods()
    {

    }
    public static PMethods getInstance()
    {
        if (instance == null)
        {
            instance = new PMethods();
        }
        return  instance;
    }

    public User getUserForHashkey(DatabaseReference ref, String key){


        DatabaseReference childref = ref.child(key);

        childref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> userMap = (Map<String, Object>)dataSnapshot.getValue();
                if(userMap == null) {
                    return;
                }

                user = new User((String) userMap.get("name"),
                                    (String) userMap.get("uid"),
                                    (String) userMap.get("email"),
                                    (String) userMap.get("image"),
                                    (String) userMap.get("accessToken"));


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return user;
    }




    public String createNewUser(FirebaseUser fuser, String accessToken){

        String key = fuser.getUid();
        User user = new User(fuser, accessToken);

        Map<String, Object> userMap = user.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(key, userMap);
        FirebaseUtils.getUserRef().updateChildren(childUpdates);

        return key;

    }

    public String getMarkerHashKey(final Marker marker) {
        double longitude = marker.getPosition().getLongitude();
        //double latitude = chosenCoordinateLatLng.getLatitude();

        int hash = (int) (10000000*longitude);
        return "" + hash;
    }

    public String getUserUpdateHashKey(final Marker marker) {
        int id =IconManager.getInstance().getIdForType(marker.getTitle());
        double longitude = marker.getPosition().getLongitude();

        long longlng = (long) (100000000 * longitude);
        long removelastint = longlng / 10;
        long roundedlng = removelastint * 10;
        long hash = roundedlng + id;

        return "" + hash;

    }

    public String getMarkerHashKey(final double longitude) {


        int hash = (int) (10000000*longitude);
        return "" + hash;
    }

    public String getUserUpdateHashKey(final double longitude, String type) {
        int id = IconManager.getInstance().getIdForType(type);
        long longlng = (long) (100000000 * longitude);
        long removelastint = longlng / 10;
        long roundedlng = removelastint * 10;
        long hash = roundedlng + id;
        return "" + hash;
    }


}
