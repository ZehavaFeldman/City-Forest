package com.zehava.cityforest.Managers;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;

import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by avigail on 28/01/18.
 */

/**
 * adding multiple languages to app requires using locale to set the resource to user choice
 * on app creation we call setLocale to set the apps language
 * setting it to user last choice
 * when user changes the app language we update SharedPreferences - holding user choice
 * default languge is english
 */


public class LocaleManager {

    //toggle language from activity
    public static void toggaleLang(Context c){
        if(getLanguage(c).equalsIgnoreCase("en"))
            setNewLocale(c,"iw");
        else setNewLocale(c,"en");
    }

    //set apps new locale
    public static void setLocale(Context c) {
        setNewLocale(c, getLanguage(c));
    }

    //set locale and update prefs
    public static void setNewLocale(Context c, String language) {
        persistLanguage(c, language);
        updateResources(c, language);
    }

    //get language
    public static String getLanguage(Context c) {
        SharedPreferences languagepref = c.getSharedPreferences("language",MODE_PRIVATE);
        return languagepref.getString("languageToLoad","en");
    }

    //save currnet language
    private static void persistLanguage(Context c, String language) {
        SharedPreferences languagepref = c.getSharedPreferences("language",MODE_PRIVATE);
        SharedPreferences.Editor editor = languagepref.edit();
        editor.putString("languageToLoad",language );
        editor.commit();
    }

    //update apps resource to be used
    private static void updateResources(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Resources res = context.getResources();
        Configuration config = new Configuration(res.getConfiguration());
        config.locale = locale;
        res.updateConfiguration(config, res.getDisplayMetrics());
    }


}

