package com.zehava.cityforest.Models;

import com.google.android.gms.ads.InterstitialAd;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.Polyline;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Track {

    private String route;
    private String db_key;
    private String track_name;
    private String starting_point;
    private String ending_point;
    private double duration;
    private double length;

    private Map<String,String> points_of_interest;
    private String additional_info;
    private String starting_point_JsonLatLng;
    private String ending_point_JsonLatLng;
    private float  star_count = 0;
    private Map<String, Float> stars = new HashMap<>();
    private float  like_count = 0;
    private Map<String, Boolean> likes = new HashMap<>();
    private List<String> waypoints;
    private String userHashkey;

    public Track(){

    }

    public Track(String route, List<String> waypointList, String db_key, String track_name, String starting_point,
                 String ending_point, double duration, double length,
                 String additional_info, String starting_point_JsonLatLng,
                 String ending_point_JsonLatLng,
                 Map<String,String> points,
                 String userHashkey
                ){

        this.route = route;
        this.db_key = db_key;
        this.track_name = track_name;
        this.starting_point = starting_point;
        this.ending_point = ending_point;
        this.duration = duration;
        this.length = length;
        this.additional_info = additional_info;
        this.starting_point_JsonLatLng = starting_point_JsonLatLng;
        this.ending_point_JsonLatLng = ending_point_JsonLatLng;
        this.points_of_interest = points;
        this.waypoints = waypointList;
        this.userHashkey = userHashkey;
    }

    public String getUserHashkey() {
        return userHashkey;
    }

    public void setUserHashkey(String userHashkey) {
        this.userHashkey = userHashkey;
    }

    public Track(String route, List<String> waypointList, String db_key, String track_name, String starting_point,
                 String ending_point, double duration, double length,
                 String additional_info, String starting_point_JsonLatLng,
                 String ending_point_JsonLatLng,
                 String userHashkey
                ){

        this.route = route;
        this.db_key = db_key;
        this.track_name = track_name;
        this.starting_point = starting_point;
        this.ending_point = ending_point;
        this.duration = duration;
        this.length = length;
        this.additional_info = additional_info;
        this.starting_point_JsonLatLng = starting_point_JsonLatLng;
        this.ending_point_JsonLatLng = ending_point_JsonLatLng;
        this.points_of_interest = new HashMap<>();
        this.waypoints = waypointList;
        this.userHashkey = userHashkey;
    }

    public Track(String db_key, String track_name, String starting_point,
                 String ending_point, double duration, double length,
                 String additional_info, String starting_point_JsonLatLng,
                 String ending_point_JsonLatLng,
                 String userHashkey
    ){
        this.db_key = db_key;
        this.track_name = track_name;
        this.starting_point = starting_point;
        this.ending_point = ending_point;
        this.duration = duration;
        this.length = length;
        this.additional_info = additional_info;
        this.starting_point_JsonLatLng = starting_point_JsonLatLng;
        this.ending_point_JsonLatLng = ending_point_JsonLatLng;
        this.points_of_interest = new HashMap<>();
        this.route = "";
        this.waypoints = new ArrayList<>();
        this.userHashkey = userHashkey;
    }




    /*building the JSON branch in the database that will include the track*/
    public Map<String, Object> toMap(){

        HashMap<String, Object> result = new HashMap<>();
        result.put("route", this.route);
        result.put("db_key", this.db_key);
        result.put("track_name", this.track_name);
        result.put("starting_point", this.starting_point);
        result.put("ending_point", this.ending_point);
        result.put("duration", this.duration);
        result.put("length", this.length);
        result.put("additional_info", this.additional_info);
        result.put("starting_point_json_latlng", this.starting_point_JsonLatLng);
        result.put("ending_point_json_latlng", this.ending_point_JsonLatLng);
        result.put("star_count", this.star_count);
        result.put("starts", this.stars);
        result.put("like_count", this.like_count);
        result.put("likes", this.likes);
        result.put("points", this.points_of_interest);
        result.put("waypoints", this.waypoints);
        result.put("uuid", this.userHashkey);

        return result;
    }

    //=========================Getters & Setters=========================//

    public String getRoute(){
        return route;
    }
    public String getDb_key(){
        return db_key;
    }
    public String getTrack_name(){
        return track_name;
    }
    public String getStarting_point(){
        return starting_point;
    }
    public String getEnding_point(){
        return ending_point;
    }
    public double getDuration(){
        return duration;
    }
    public double getLength(){
        return length;
    }
//    public String getLevel(){
//        return level;
//    }
//    public String getSeason(){
//        return season;
//    }
//    public boolean getHas_water(){
//        return has_water;
//    }
//    public boolean getSuitable_for_bikes(){
//        return suitable_for_bikes;
//    }
//    public boolean getSuitable_for_dogs(){
//        return suitable_for_dogs;
//    }
//    public boolean getSuitable_for_families(){
//        return suitable_for_families;
//    }
//    public boolean getIs_romantic(){
//        return is_romantic;
//    }
    public String getAdditional_info(){
        return additional_info;
    }
    public String getStarting_point_JsonLatLng(){
        return this.starting_point_JsonLatLng;
    }
    public String getEnding_point_JsonLatLng(){
        return this.ending_point_JsonLatLng;
    }
    public float getStar_count() {
        return star_count;
    }
    public Map<String, Float> getStars() {
        return stars;
    }
    public float getLike_count() {
        return like_count;
    }
    public Map<String, Boolean> getLikes() {
        return likes;
    }
    public Map<String,String> getPoints_of_interest() {
        return points_of_interest;
    }
    public List<String> getWaypoints() {
        return waypoints;
    }

    public void setWaypoints(List<String> waypoints) {
        this.waypoints = waypoints;
    }
    public void setRoute(String route){
        this.route = route;
    }
    public void setDb_key(String db_key){
        this.db_key = db_key;
    }
    public void setTrack_name(String track_name){
        this.track_name = track_name;
    }
    public void setStarting_point(String starting_point){
        this.starting_point = starting_point;
    }
    public void setEnding_point(String ending_point){
        this.ending_point = ending_point;
    }
    public void setDuration(double duration){
        this.duration = duration;
    }
    public void setLength(double length){
        this.length = length;
    }
//    public void setLevel(String level){
//        this.level = level;
//    }
//    public void setSeason(String season){
//        this.season = season;
//    }
//    public void setHas_water(boolean has_water){
//        this.has_water = has_water;
//    }
//    public void setSuitable_for_bikes(boolean suitable_for_bikes){
//        this.suitable_for_bikes = suitable_for_bikes;
//    }
//    public void setSuitable_for_dogs(boolean suitable_for_dogs){
//        this.suitable_for_dogs = suitable_for_dogs;
//    }
//    public void setSuitable_for_families(boolean suitable_for_families){
//        this.suitable_for_families = suitable_for_families;
//    }
//    public void setIs_romantic(boolean is_romantic){
//        this.is_romantic = is_romantic;
//    }
    public void setAdditional_info(String additional_info){
        this.additional_info = additional_info;
    }
    public void setStarting_point_JsonLatLng(String starting_point_JsonLatLng){
        this.starting_point_JsonLatLng = starting_point_JsonLatLng;
    }
    public void setEnding_point_JsonLatLng(String ending_point_JsonLatLng){
        this.ending_point_JsonLatLng = ending_point_JsonLatLng;
    }
    public void setStar_count(float star_count) {
        this.star_count = star_count;
    }
    public void setStars(Map<String, Float> stars) {
        this.stars = stars;
    }
    public void setLike_count(float like_count) {
        this.like_count = like_count;
    }
    public void setLikes(Map<String, Boolean> likes) {
        this.likes = likes;
    }



    public void setPoints_of_interest(Map<String,String> points_of_interest) {
        this.points_of_interest = points_of_interest;
    }

    
    //========================= END =========================//
}
