package com.zehava.cityforest.Models;

import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.zehava.cityforest.Managers.IconManager;
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


    public UserUpdate(){

    }

    public UserUpdate(String uuid, String type, String snippet, double coX, double coY){

        super(coX, coY, type, snippet, type, uuid);

        this.updated_time = System.currentTimeMillis()/1000;

        setTimeSpace(type);
    }

    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("position", castPositionToJson());
        result.put("title", this.title);
        result.put("snippet", this.snippet);
        result.put("type", this.type);
        result.put("uuid", this.uuid);
        result.put("updated", this.updated_time);
        result.put("update_time_space", this.update_time_space);

        return result;
    }

    public String getType(){
        return type;
    }

    public void setType(String type){
        this.type = type;

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


}
