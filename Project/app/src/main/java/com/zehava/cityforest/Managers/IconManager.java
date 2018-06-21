package com.zehava.cityforest.Managers;

import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.zehava.cityforest.R;

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by avigail on 13/04/18.
 */

public class IconManager {

    private static IconManager instance;
    private Map<String,Icon> icons;

    private IconManager()
    {

    }
    public static IconManager getInstance()
    {
        if (instance == null)
        {
            instance = new IconManager();
        }
        return  instance;
    }

    public void generateIcons(IconFactory iconFactory) {
        if (icons != null)
            return;
        icons = new HashMap<>();
        icons.put("תחנת רכבת", iconFactory.fromResource(R.mipmap.ic_tram_black_24dp));
        icons.put("תחנת רכבת ג", iconFactory.fromResource(R.drawable.train_large));
        icons.put("תחנת אוטובוס", iconFactory.fromResource(R.mipmap.ic_directions_bus_black_24dp));
        icons.put("תחנת אוטובוס ג", iconFactory.fromResource(R.drawable.ic_bus_large));
        icons.put("אתר היסטורי", iconFactory.fromResource(R.mipmap.ic_change_history_black_24dp));
        icons.put("אתר היסטורי ג", iconFactory.fromResource(R.drawable.history_large));
        icons.put("בית קפה", iconFactory.fromResource(R.mipmap.ic_local_cafe_black_24dp));
        icons.put("בית קפה ג", iconFactory.fromResource(R.drawable.coffe_large));
        icons.put("מזג אויר", iconFactory.fromResource(R.drawable.ic_cloud_1));
        icons.put("אין כניסה", iconFactory.fromResource(R.drawable.ic_key));
        icons.put("אין תצפית", iconFactory.fromResource(R.drawable.ic_no_vision));
        icons.put("יצירת מסלול", iconFactory.fromResource(R.drawable.blue_marker));
        icons.put("נופים", iconFactory.fromResource(R.drawable.ic_mountain));
        icons.put("לא קיים", iconFactory.fromResource(R.drawable.ic_nothing_here));
        icons.put("אזהרה", iconFactory.fromResource(R.drawable.ic_problem));
        icons.put("שונה", iconFactory.fromResource(R.drawable.ic_flag));
        icons.put("עומס", iconFactory.fromResource(R.drawable.ic_group));
        icons.put("אין חניה", iconFactory.fromResource(R.drawable.ic_parking));
        icons.put("default", iconFactory.defaultMarker());
        icons.put("Train Station", icons.get("תחנת רכבת"));
        icons.put("Train Station ג", icons.get("תחנת רכבת ג"));
        icons.put("Bus Station", icons.get("תחנת אוטובוס"));
        icons.put("Bus Station ג", icons.get("תחנת אוטובוס ג"));
        icons.put("Historic Site", icons.get("אתר היסטורי"));
        icons.put("Historic Site ג", icons.get("אתר היסטורי ג"));
        icons.put("Coffee Shop", icons.get("בית קפה"));
        icons.put("Coffee Shop ג", icons.get("בית קפה ג"));
        icons.put("Weaterh", icons.get("מזג אויר"));
        icons.put("No Entrance", icons.get("אין כניסה"));
        icons.put("No View", icons.get("אין תצפית"));
        icons.put("Create Track", icons.get("יצירת מסלול"));
        icons.put("Scenery", icons.get("נופים"));
        icons.put("Does not Exist", icons.get("לא קיים"));
        icons.put("Warning", icons.get("אזהרה"));
        icons.put("Different", icons.get("שונה"));
        icons.put("Crowded", icons.get("עומס"));
        icons.put("No Parking", icons.get("אין חניה"));

    }
    public Icon getIconForEdit(){
        return  icons.get("יצירת מסלול");
    }

    public int getResourceFrType(String type){
        if(type.equals("מזג אויר")||type.equalsIgnoreCase("Weather") ){
            return R.drawable.ic_cloud_1;
        }
        else if(type.equals("אין כניסה")||type.equalsIgnoreCase("No Entrance")){
            return R.drawable.ic_key;
        }
        else if(type.equals("אין תצפית")||type.equalsIgnoreCase("No View")){
            return R.drawable.ic_no_vision;
        }
        else if(type.equals("אין חניה")||type.equalsIgnoreCase("No Parking") ){
            return R.drawable.ic_parking;
        }
        else if(type.equals("עומס")||type.equalsIgnoreCase("Crowded")){
            return R.drawable.ic_group;
        }
        else if(type.equals("שונה")||type.equalsIgnoreCase("Different")){
            return R.drawable.ic_flag;
        }
        else if(type.equals("אזהרה")||type.equalsIgnoreCase("Warning") ){
            return R.drawable.ic_problem;
        }
        else if(type.equals("לא קיים")||type.equalsIgnoreCase("Does not Exist")){
            return R.drawable.ic_nothing_here;
        }
        else if(type.equals("נופים")||type.equalsIgnoreCase("Scenery")){
            return R.drawable.ic_mountain;
        }

        else if(type.equals("Train Station") ){
            return R.mipmap.ic_tram_black_24dp;
        }
        else if(type.equals("Bus Station")){
            return R.mipmap.ic_directions_bus_black_24dp;
        }
        else if(type.equals("Historic Site")){
            return R.mipmap.ic_change_history_black_24dp;
        }
        else if(type.equals("Coffee Shop") ){
            return R.mipmap.ic_local_cafe_black_24dp;
        }

        else if(type.equals("תחנת אוטובוס") ){
            return R.mipmap.ic_directions_bus_black_24dp;
        }
        else if(type.equals("תחנת רכבת")){
            return R.mipmap.ic_tram_black_24dp;
        }
        else if(type.equals("בית קפה")){
            return R.mipmap.ic_local_cafe_black_24dp;
        }
        else if(type.equals("אתר היסטורי") ){
            return R.mipmap.ic_change_history_black_24dp;
        }

        else  return  -1;
    }

    public Icon getIconForType(String type, boolean isPointOfInterest){
        if( isPointOfInterest && (type.equalsIgnoreCase("אחר")||type.equalsIgnoreCase("Other")))
            return icons.get("default");
        if(icons.containsKey(type))
            return icons.get(type);
        return icons.get("default");
    }





    public int getIdForType(String type){

        if(type.equals("מזג אויר")||type.equalsIgnoreCase("Weather") ){
            return 0;
        }
        else if(type.equals("אין כניסה")||type.equalsIgnoreCase("No Entrance")){
            return 1;
        }
        else if(type.equals("אין תצפית")||type.equalsIgnoreCase("No View")){
            return 2;
        }
        else if(type.equals("אין חניה")||type.equalsIgnoreCase("No Parking") ){
            return 3;
        }
        else if(type.equals("עומס")||type.equalsIgnoreCase("Crowded")){
            return 4;
        }
        else if(type.equals("שונה")||type.equalsIgnoreCase("Different")){
            return 5;
        }
        else if(type.equals("אזהרה")||type.equalsIgnoreCase("Warning") ){
            return 6;
        }
        else if(type.equals("לא קיים")||type.equalsIgnoreCase("Does not Exist")){
            return 7;
        }
        else if(type.equals("נופים")||type.equalsIgnoreCase("Scenery")){
            return 8;
        }
        else { return  -1;}
    }


}
