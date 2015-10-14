package com.swiftkaydevelopment.findme;

import android.app.Activity;
import android.content.SharedPreferences;

public abstract class PrefAppend {

    public static boolean addFavoriteItem(Activity activity,String favoriteItem,String prefname){
        //Get previous favorite items
        String favoriteList = getStringFromPreferences(activity,null,prefname);
        // Append new Favorite item
        if(favoriteList!=null){
            favoriteList = favoriteList+","+favoriteItem;
        }else{
            favoriteList = favoriteItem;
        }
        // Save in Shared Preferences
        return putStringInPreferences(activity,favoriteList,prefname);
    }
    public static String[] getFavoriteList(Activity activity,String prefname){
        String favoriteList = getStringFromPreferences(activity,null,prefname);
        return convertStringToArray(favoriteList);
    }
    private static boolean putStringInPreferences(Activity activity,String nick,String key){
        SharedPreferences sharedPreferences = activity.getPreferences(Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, nick);
        editor.commit();                        
        return true;        
    }
    private static String getStringFromPreferences(Activity activity,String defaultValue,String key){
        SharedPreferences sharedPreferences = activity.getPreferences(Activity.MODE_PRIVATE);
        String temp = sharedPreferences.getString(key, defaultValue);
        return temp;        
    }

    private static String[] convertStringToArray(String str){
        if(str != null) {
            String[] arr = str.split(",");
            return arr;
        }
        return null;

    }
}