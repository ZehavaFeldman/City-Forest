package com.zehava.cityforest.Managers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.mapbox.api.directions.v5.DirectionsAdapterFactory;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.api.directions.v5.models.DirectionsWaypoint;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapboxMap;
//import com.mapbox.services.api.directions.v5.models.DirectionsRoute;
import com.mapbox.services.commons.models.Position;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;


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
        gsonBuilder.registerTypeAdapterFactory(DirectionsAdapterFactory.create());

        Gson gson = gsonBuilder.create();
        String json = gson.toJson(route, DirectionsRoute.class);
        return json;

    }


    public ArrayList<String> castListToJson(List<DirectionsWaypoint> list){
        ArrayList<String> waypointsST = new ArrayList<>();
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapterFactory(DirectionsAdapterFactory.create());

        Gson gson = gsonBuilder.create();

        for(DirectionsWaypoint waypoint : list){
            String json = gson.toJson(waypoint, DirectionsWaypoint.class);
            waypointsST.add(json);
        }

        return waypointsST;
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

        return DirectionsRoute.fromJson(route);

    }

    public List<DirectionsWaypoint> retrieveDirectionsWaypointsFromJson(ArrayList<String> waypoints){
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.serializeSpecialFloatingPointValues();

        Gson gson = gsonBuilder.create();
        TypeAdapter<DirectionsWaypoint> typeAdapter = DirectionsWaypoint.typeAdapter(gson);
        List<DirectionsWaypoint> point = new ArrayList<>();
        try {
            for(String waypointST : waypoints){
                DirectionsWaypoint waypoint = typeAdapter.fromJson(waypointST);
                point.add(waypoint);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return point;

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

    public List<Point> retrieveListOfPointsFromJson(String pointListJs) {

        List<Point> obj = new ArrayList<>();
        Point p = Point.fromJson(pointListJs);

        obj.add(p);
        return obj;
    }





}
