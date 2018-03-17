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

        setLogo(type);
    }



    /*building the JSON branch in the database that will include the coordinate*/
    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("position", castPositionToJson());
        result.put("title", this.title);
        result.put("snippet", this.snippet);
        result.put("type", this.type);
        result.put("logo", this.logo);
        if(this.image != null)
            result.put("imagePath", this.image);
        return result;
    }

    public String getType(){
        return type;
    }

    public void setType(String type){
        this.type = type;
        setLogo(type);
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

    public void setLogo(int logo){
        this.logo = logo;
    }

    public void setLogo(String type){
        if(type.equals("תחנת רכבת")){
            logo = R.mipmap.ic_tram_black_24dp;
        }
        else if(type.equals("תחנת אוטובוס")){
            logo = R.mipmap.ic_directions_bus_black_24dp;
        }
        else if(type.equals("אתר היסטורי")){
            logo = R.mipmap.ic_change_history_black_24dp;
        }
        else if(type.equals("בית קפה")){
            logo = R.mipmap.ic_local_cafe_black_24dp;
        }
        else if(type.equals("מזג אויר")){
            logo =  R.drawable.ic_cloud_1;
        }
        else if(type.equals("אין כניסה")){
            logo =  R.drawable.ic_key;
        }
        else if(type.equals("אין תצפית")){
            logo =  R.drawable.ic_no_vision;
        }
        else logo = -1;
    }

    public static Long whatIsTheLogoForType(String type){
        if(type.equals("תחנת רכבת")){
            return (long)R.mipmap.ic_tram_black_24dp;
        }
        else if(type.equals("תחנת אוטובוס")){
            return (long)R.mipmap.ic_directions_bus_black_24dp;
        }
        else if(type.equals("אתר היסטורי")){
            return (long)R.mipmap.ic_change_history_black_24dp;
        }
        else if(type.equals("בית קפה")){
            return (long)R.mipmap.ic_local_cafe_black_24dp;
        }
        else if(type.equals("מזג אויר")){
            return (long) R.drawable.ic_cloud_1;
        }
        else if(type.equals("אין כניסה")){
            return  (long)R.drawable.ic_key;
        }
        else if(type.equals("אין תצפית")){
            return (long)R.drawable.ic_no_vision;
        }
        else return (long) -1;
    }


}



