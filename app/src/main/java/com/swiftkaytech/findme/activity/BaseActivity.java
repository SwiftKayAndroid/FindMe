package com.swiftkaytech.findme.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

/**
 * Created by Kevin Haines on 10/19/15.
 */
public abstract class BaseActivity extends AppCompatActivity {

    public static final String TAG = "findme-baseactivity";
    protected Toolbar mToolbar;
    protected SharedPreferences prefs;
    protected String uid;

    protected abstract int getLayoutResource();
    protected abstract Context getContext();

    private String initializeUser() {
        String KEY = "uid";
        if (prefs != null) {
            return prefs.getString(KEY, null);
        }
        err("prefs is null");
        return null;
    }
    public SharedPreferences getPrefs(){return prefs;}

    public String getUid(){return uid;}
    public void setUid(String s){uid = s;}

    protected abstract void createActivity(Bundle inState);
    protected abstract Bundle saveState(Bundle b);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResource());
        prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        uid = initializeUser();
         if (uid == null || uid.isEmpty()) {
             err("uid is null");
         }
        createActivity(savedInstanceState);
        checkForNullUID();
    }

    protected void writePref(String key,boolean value){
        SharedPreferences.Editor editor = getPrefs().edit();
        editor.putBoolean(key, value);
    }

    protected void writePref(String key,int value){
        SharedPreferences.Editor editor = getPrefs().edit();
        editor.putInt(key, value);
    }
    protected void writePref(String key,float value){
        SharedPreferences.Editor editor = getPrefs().edit();
        editor.putFloat(key, value);
    }

    protected void writePref(String key,Long value){
        SharedPreferences.Editor editor = getPrefs().edit();
        editor.putLong(key, value);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(saveState(outState));
    }

    private void checkForNullUID(){
        if (uid == null || uid.isEmpty()) {
            warn("WARNING UID IS NULL...WARNING UID IS NULL....WARNING...WARNING..");
        }
    }

    protected void err(String message){
        Log.e(TAG, message);
    }
    protected void warn(String message){
        Log.w(TAG, message);
    }
    protected  void log(String message){
        Log.i(TAG, message);
    }
    protected void toast(String message){
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }
}
