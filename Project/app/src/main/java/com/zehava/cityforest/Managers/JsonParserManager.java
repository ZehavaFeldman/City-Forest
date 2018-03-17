package com.zehava.cityforest.Managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.services.commons.models.Position;
import com.mapbox.services.directions.v5.models.DirectionsRoute;

/**
 * Created by avigail on 31/01/18.
 */



/*class used objects to prase to and from json
  inorder to send data in between activitys via intent
 these method are needed may times through out the project
 using manager we avoid extra code*/
public class JsonParserManager {


    private static JsonParserManager instance;



    private JsonParserManager()
    {

    }
    public static JsonParserManager getInstance()
    {
        if (instance == null)
        {
            instance = new JsonParserManager();
        }
        return  instance;
    }



    /*Method casts LatLng object to Json, to be able to send it via intent*/
    public String castLatLngToJson(LatLng point){
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.serializeSpecialFloatingPointValues();

        Gson gson = gsonBuilder.create();
        String json = gson.toJson(point, LatLng.class);
        return json;
    }

    /*Method casts route object to Json, to be able to send it via intent*/
    public String castRouteToJson(DirectionsRoute route){
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.serializeSpecialFloatingPointValues();

        Gson gson = gsonBuilder.create();
        String json = gson.toJson(route, DirectionsRoute.class);
        return json;
    }


    /*Method get String that represents a LatLang Json object.
    * Method retrieve the latLang object and returns it*/
    public LatLng retreiveLatLngFromJson(String stringExtra) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.serializeSpecialFloatingPointValues();

        Gson gson = gsonBuilder.create();
        LatLng obj = gson.fromJson(stringExtra, LatLng.class);
        return obj;
    }



    /*Method get String that represents a DirectionRoute Json object.
    * Method retrieve the route object and returns it*/
    public DirectionsRoute retrieveRouteFromJson(String route) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.serializeSpecialFloatingPointValues();

        Gson gson = gsonBuilder.create();
        DirectionsRoute obj = gson.fromJson(route, DirectionsRoute.class);
        return obj;
    }


    /*Method get String that represents a Position Json object.
    * Method retrieve the position object and returns it*/
    public Position retrievePositionFromJson(String posJs) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.serializeSpecialFloatingPointValues();

        Gson gson = gsonBuilder.create();
        Position obj = gson.fromJson(posJs, Position.class);
        return obj;
    }

     /*Method get String that represents a DirectionRoute Json object.
    * Method retrieve the route object and returns it*/
    public DirectionsRoute retreiveRouteFromJson(String stringExtra) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.serializeSpecialFloatingPointValues();

        Gson gson = gsonBuilder.create();
        DirectionsRoute obj = gson.fromJson(stringExtra, DirectionsRoute.class);
        return obj;
    }

        public LatLng retrieveLatLngFromJson(String posJs) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.serializeSpecialFloatingPointValues();

        Gson gson = gsonBuilder.create();
        LatLng obj = gson.fromJson(posJs, LatLng.class);
        return obj;
    }
}
