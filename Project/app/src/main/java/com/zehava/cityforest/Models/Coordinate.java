package com.zehava.cityforest.Models;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mapbox.services.commons.models.Position;
import com.zehava.cityforest.Managers.JsonParserManager;


import java.util.HashMap;
import java.util.Map;

public class Coordinate {

    protected Position position = null;
    protected String title;
    protected String snippet;


    public Coordinate(){
    }

    public Coordinate(double coX, double coY,
                      String title, String snippet){

        this.position = Position.fromCoordinates(coY, coX);


        this.title = title;
        this.snippet = snippet;
    }

    public Coordinate(String pos,
                      String title, String snippet){
        Position position = JsonParserManager.getInstance().retrievePositionFromJson(pos);

        this.position = position;
        this.title = title;
        this.snippet = snippet;
    }

    /*building the JSON branch in the database that will include the coordinate*/
    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("position", JsonParserManager.getInstance().castPositionToJson(this.position));
        result.put("title", this.title);
        result.put("snippet", this.snippet);

        return result;
    }

    //=========================Getters & Setters=========================//
    public Position getPosition(){
        return this.position;
    }
    public String getTitle(){
        return this.title;
    }
    public String getSnippet(){
        return this.snippet;
    }

    public void setPosition(double coX, double coY){
        this.position = Position.fromCoordinates(coY, coX);
    }

    public void setPosition(String position){
        this.position  = JsonParserManager.getInstance().retrievePositionFromJson(position);
    }
    public void setTitle(String title){
        this.title = title;
    }
    public void setSnippet(String snippet){
        this.snippet = snippet;
    }
    //========================= END =========================//



}
