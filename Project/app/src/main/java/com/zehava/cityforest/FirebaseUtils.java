package com.zehava.cityforest;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

/**
 * Created by brad on 2017/02/05.
 */

public class FirebaseUtils {
    //I'm creating this class for similar reasons as the Constants class, and to make my code a bit
    //cleaner and more well managed.

    public static DatabaseReference getUserRef(String uid){
        return FirebaseDatabase.getInstance()
                .getReference(Constants.USERS_KEY)
                .child(uid);
    }

    public static DatabaseReference getUserRef(){
        return FirebaseDatabase.getInstance()
                .getReference(Constants.USERS_KEY);
    }

    public static DatabaseReference getPointsRef(){
        return FirebaseDatabase.getInstance()
                .getReference(Constants.POINTS_KEY);
    }
    public static DatabaseReference getUserUpdatesRef(){
        return FirebaseDatabase.getInstance()
                .getReference(Constants.UPDATES_KEY);
    }
    public static DatabaseReference getTracksRef(){
        return FirebaseDatabase.getInstance()
                .getReference(Constants.TRACK_KEY);
    }
    public static DatabaseReference getCoordinatesRef(){
        return FirebaseDatabase.getInstance()
                .getReference(Constants.COORDINATES_KEY);
    }
    public static DatabaseReference getImagesRef(){
        return FirebaseDatabase.getInstance()
                .getReference(Constants.IMAGE_KEY);
    }

    public static DatabaseReference getPostRef(String modelId){
        return FirebaseDatabase.getInstance().getReference(Constants.POST_KEY)
                .child(modelId);
    }

    public static DatabaseReference getPostLikedRef(){
        return FirebaseDatabase.getInstance()
                .getReference(Constants.POST_LIKED_KEY);
    }

    public static DatabaseReference getPostLikedRef(String postId){
        return getPostLikedRef().child(getCurrentUser().getUid())
                .child(postId);
    }

    public static FirebaseUser getCurrentUser(){
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    public static String getUid(){
        String path = FirebaseDatabase.getInstance().getReference().push().toString();
        return path.substring(path.lastIndexOf("/") + 1);
    }

    public static StorageReference getImageSRef(){
        return FirebaseStorage.getInstance().getReference(Constants.POST_IMAGES);
    }

//    public static DatabaseReference getMyPostRef(){
//        return FirebaseDatabase.getInstance().getReference(Constants.MY_POSTS)
//                .child(getCurrentUser().getEmail().replace(".",","));
//    }

    public static DatabaseReference getCommentRef(String postId){
        return FirebaseDatabase.getInstance().getReference(Constants.COMMENTS_KEY)
                .child(postId);
    }

//    public static DatabaseReference getMyRecordRef(){
//        return FirebaseDatabase.getInstance().getReference(Constants.USER_RECORD)
//                .child(getCurrentUser().getEmail().replace(".",","));
//    }

//    public static void addToMyRecord(String node, final String id){
//        getMyRecordRef().child(node).runTransaction(new Transaction.Handler() {
//            @Override
//            public Transaction.Result doTransaction(MutableData mutableData) {
//                ArrayList<String> myRecordCollection;
//                if(mutableData.getValue() == null){
//                    myRecordCollection = new ArrayList<String>(1);
//                    myRecordCollection.add(id);
//                }else{
//                    myRecordCollection = (ArrayList<String>) mutableData.getValue();
//                    myRecordCollection.add(id);
//                }
//
//                mutableData.setValue(myRecordCollection);
//                return Transaction.success(mutableData);
//            }
//
//            @Override
//            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
//
//            }
//        });
//    }

}
