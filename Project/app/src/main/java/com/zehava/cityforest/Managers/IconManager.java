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

    public void generateIcons(IconFactory iconFactory){
        if(icons != null)
            return;
        icons = new HashMap<>();
        icons.put("תחנת רכבת",iconFactory.fromResource(R.mipmap.ic_tram_black_24dp));
        icons.put("תחנת אוטובוס",iconFactory.fromResource(R.mipmap.ic_directions_bus_black_24dp));
        icons.put("אתר היסטורי",iconFactory.fromResource(R.mipmap.ic_change_history_black_24dp));
        icons.put("בית קפה",iconFactory.fromResource(R.mipmap.ic_local_cafe_black_24dp));
        icons.put("תחנת רכבת ג",iconFactory.fromResource(R.drawable.train_large));
        icons.put("תחנת אוטובוס ג",iconFactory.fromResource(R.drawable.ic_bus_large));
        icons.put("אתר היסטורי ג",iconFactory.fromResource(R.drawable.history_large));
        icons.put("בית קפה ג",iconFactory.fromResource(R.drawable.coffe_large));
        icons.put("מזג אויר",iconFactory.fromResource(R.drawable.ic_cloud_1));
        icons.put("אין כניסה",iconFactory.fromResource(R.drawable.ic_key));
        icons.put("אין תצפית",iconFactory.fromResource(R.drawable.ic_no_vision));
        icons.put("יצירת מסלול", iconFactory.fromResource(R.drawable.blue_marker));
        icons.put("default",iconFactory.defaultMarker());

    }

    public Icon getIconForEdit(){
        return  icons.get("יצירת מסלול");
    }

    public int getResourceFrType(String type){
        if(type.equals("מזג אויר")){
            return R.drawable.ic_cloud_1;
        }
        else if(type.equals("אין כניסה")){
            return R.drawable.ic_key;
        }
        else if(type.equals("אין תצפית")){
            return R.drawable.ic_no_vision;
        }

        else  return  -1;
    }

    public Icon getIconForType(String type){
        if(icons.containsKey(type))
            return icons.get(type);
        return icons.get("default");
    }

    public String getTypeForIcon(Icon icon){

        for (Map.Entry<String, Icon> entry : icons.entrySet()) {
            if (entry.getValue().equals(icon))
                return entry.getKey();
        }

        return "default";
    }

    public int getIdForIcon(Icon icon){

        for (Map.Entry<String, Icon> entry : icons.entrySet()) {
            if (entry.getValue().equals(icon))
                return getIdForType(entry.getKey());
        }

        return -1;
    }

    public int getIdForType(String type){
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
}
