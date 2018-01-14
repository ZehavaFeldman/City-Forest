package com.zehava.cityforest.Models;

import com.zehava.cityforest.R;
import com.mapbox.services.commons.models.Position;

import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by avigail on 10/12/17.
 */

public class UserUpdate extends PointOfInterest{


    private long updated_time;
    private long update_time_space;
    private String uid;
    private String uname;
    private int id;


    public UserUpdate(){

    }

    public UserUpdate(String uid, String uname,int id, String type, String snippet, double coX, double coY){

        super(coX, coY, type, snippet, type);

        this.uid = uid;
        this.uname = uname;
        this.id=id;
        this.updated_time = System.currentTimeMillis()/1000;

        setTimeSpace(type);
    }

    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("position", castPositionToJson());
        result.put("title", this.title);
        result.put("snippet", this.snippet);
        result.put("type", this.type);
        result.put("logo", this.logo);
        result.put("uid", this.uid);
        result.put("uname", this.uname);
        result.put("updated", this.updated_time);
        result.put("update_time_space", this.update_time_space);

        return result;
    }

    public String getType(){
        return type;
    }

    public void setType(String type){
        this.type = type;
        setLogo(type);
    }


    public static int whatIsTheLogoForId(int id){

        if(id==0){
            return R.drawable.ic_cloud_1;
        }
        else if(id==1){
            return  R.drawable.ic_key;
        }
        else if(id==2){
            return R.drawable.ic_no_vision;
        }


        else  return  -1;

    }

    public static int generateIdByType(String type){

        if(type.equals("מזג אויר")){
            return 0;
        }
        else if(type.equals("אין כניסה")){
            return 1;
        }
        else if(type.equals("אין תצפית")){
            return 2;
        }

        else  return  -1;

    }

    public void setTimeSpace(String type){
        if(type.equalsIgnoreCase("מזג אויר")){
            update_time_space = 1000*60*15;
        }
        else if(type.equalsIgnoreCase("אין כניסה")){
            update_time_space = 1000*60*25;
        }
        else if(type.equalsIgnoreCase("אין תצפית")){
            update_time_space = 1000*60*60*2;
        }

        //default is 10 minutes
        else update_time_space = 1000*60*10;
    }

    private void setUpdated_time(long new_update){
        updated_time = new_update;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
