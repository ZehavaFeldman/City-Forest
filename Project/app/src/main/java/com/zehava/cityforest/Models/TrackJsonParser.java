package com.zehava.cityforest.Models;

import org.json.JSONObject;

/**
 * Created by avigail on 17/03/18.
 */

public class TrackJsonParser {

    public Track parse(JSONObject jsonObject)
    {
        if (jsonObject == null)
            return null;
        String route = jsonObject.optString("route");
        String db_key = jsonObject.optString("db_key");
        String track_name = jsonObject.optString("track_name");
        String starting_point = jsonObject.optString("starting_point");
        String ending_point = jsonObject.optString("ending_point");
        double duration = Double.parseDouble(jsonObject.optString("duration"));
        double length = Double.parseDouble(jsonObject.optString("length"));
        String additional_info = jsonObject.optString("additional_info");
        String starting_point_JsonLatLng = jsonObject.optString("starting_point_json_latlng");
        String ending_point_JsonLatLng = jsonObject.optString("ending_point_json latlng");

        if (route != null && db_key != null && track_name != null
            && starting_point != null && ending_point != null &&  duration != 0 && length != 0
            && additional_info != null && starting_point_JsonLatLng != null  && ending_point_JsonLatLng != null)
            return new Track(route, db_key, track_name, starting_point,
                    ending_point, duration, length,
                    additional_info, starting_point_JsonLatLng,
                    ending_point_JsonLatLng);
        return null;
    }
}
