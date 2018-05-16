package com.zehava.cityforest.Models;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mapbox.services.commons.models.Position;
import com.zehava.cityforest.Managers.JsonParserManager;

import org.json.JSONObject;

/**
 * Created by avigail on 10/01/18.
 */


//parse single pointOfInterest
public class PointOfInterestJsonParser
{
    public PointOfInterest parse(JSONObject jsonObject)
    {
        if (jsonObject == null)
            return null;
        String title = jsonObject.optString("title");
        String snippet = jsonObject.optString("snippet");
        String type = jsonObject.optString("type");
        String position = jsonObject.optString("position");
        String userHashkey = jsonObject.optString("userHashkey");
        Position pos = JsonParserManager.getInstance().retrievePositionFromJson(position);
        double lat = pos.getLatitude();
        double longitude = pos.getLongitude();
        if (title != null && snippet != null && lat != 0 && longitude != 0)
            return new PointOfInterest(longitude,lat,title, snippet,type, userHashkey);
        return null;
    }


}