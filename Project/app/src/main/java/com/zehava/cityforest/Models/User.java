package com.zehava.cityforest.Models;

import com.google.firebase.auth.FirebaseAuthProvider;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by avigail on 04/05/18.
 */

public class User {

    private String name;
    private String email;
    private String uid;
    private String userAccessToken;
    private String image;


    public User(FirebaseUser firebaseUser, String accessToken){

        String facebookUserId = "";

        name = firebaseUser.getDisplayName();
        uid = firebaseUser.getUid();
        email = firebaseUser.getEmail();
        for(UserInfo profile : firebaseUser.getProviderData()) {
            facebookUserId = profile.getUid();
        }
        image = "https://graph.facebook.com/" + facebookUserId + "/picture?type=small";

        userAccessToken = accessToken;


    }

    public User(String name, String uid, String email, String image, String accessToken){

        this.name = name;
        this.uid = uid;
        this.email = email;
        this.image = image;
        this.userAccessToken = accessToken;


    }

    /*building the JSON branch in the database that will include the track*/
    public Map<String, Object> toMap(){

        HashMap<String, Object> result = new HashMap<>();
        result.put("user_accessyoken", this.userAccessToken);
        result.put("uid", this.uid);
        result.put("image", this.image);
        result.put("email", this.email);
        result.put("name", this.name);

        return result;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUserAccessToken() {
        return userAccessToken;
    }

    public void setUserAccessToken(String userAccessToken) {
        this.userAccessToken = userAccessToken;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
