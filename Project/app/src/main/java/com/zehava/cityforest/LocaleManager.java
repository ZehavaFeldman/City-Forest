package com.zehava.cityforest;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;

import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by avigail on 28/01/18.
 */

public class LocaleManager {

    public static void setLocale(Context c) {
        setNewLocale(c, getLanguage(c));
    }

    public static void setNewLocale(Context c, String language) {
        persistLanguage(c, language);
        updateResources(c, language);
    }

    public static String getLanguage(Context c) {
        SharedPreferences languagepref = c.getSharedPreferences("language",MODE_PRIVATE);
        return languagepref.getString("languageToLoad","en");
    }

    private static void persistLanguage(Context c, String language) {
        SharedPreferences languagepref = c.getSharedPreferences("language",MODE_PRIVATE);
        SharedPreferences.Editor editor = languagepref.edit();
        editor.putString("languageToLoad",language );
        editor.commit();
    }

    private static void updateResources(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Resources res = context.getResources();
        Configuration config = new Configuration(res.getConfiguration());
        config.locale = locale;
        res.updateConfiguration(config, res.getDisplayMetrics());
    }

    public static void toggaleLang(Context c){
        if(getLanguage(c).equalsIgnoreCase("en"))
            setNewLocale(c,"iw");
        else setNewLocale(c,"en");
    }
}

