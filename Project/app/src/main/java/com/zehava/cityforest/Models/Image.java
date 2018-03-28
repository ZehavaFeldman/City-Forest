package com.zehava.cityforest.Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by avigail on 10/03/18.
 */

public class Image implements Parcelable {

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    private String imagePath;

    public Image(){

    }

    public Image(String imagePath){

        this.imagePath = imagePath;

    }

    protected Image(Parcel in) {
        this.imagePath = in.readString();

    }

    public static final Creator<Image> CREATOR = new Creator<Image>() {
        @Override
        public Image createFromParcel(Parcel in) {
            return new Image(in);
        }

        @Override
        public Image[] newArray(int size) {
            return new Image[size];
        }
    };

    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("imagePath", this.imagePath);
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(imagePath);
    }
}
