package com.zehava.cityforest.Models;

import com.zehava.cityforest.Models.Coordinate;
import com.zehava.cityforest.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;



public class PointOfInterest extends Coordinate {

    protected String type;
    protected long logo;
    private String image;

    public PointOfInterest(){

    }

    public PointOfInterest(double coX, double coY,
                           String title, String snippet, String type){
        super(coX, coY, title, snippet);

        this.type = type;

    }

    public PointOfInterest(String position,
                           String title, String snippet, String type){
        super(position, title, snippet);

        this.type = type;

    }



    /*building the JSON branch in the database that will include the coordinate*/
    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("position", castPositionToJson());
        result.put("title", this.title);
        result.put("snippet", this.snippet);
        result.put("type", this.type);
        if(this.image != null)
            result.put("imagePath", this.image);
        return result;
    }

    public String getType(){
        return type;
    }

    public void setType(String type){
        this.type = type;
    }

    public String getImage(){
        return image;
    }

    public void setImage(String imagePath){
        this.image = imagePath;
    }

    public long getLogo(){
        return this.logo;
    }

    public void setPointLogo(int logo){
        this.logo = logo;
    }





}



