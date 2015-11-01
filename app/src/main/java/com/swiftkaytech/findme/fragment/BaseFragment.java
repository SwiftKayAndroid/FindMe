package com.swiftkaytech.findme.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.swiftkaytech.findme.activity.BaseActivity;

/**
 * Created by Kevin Haines on 10/20/15.
 */
public class BaseFragment extends Fragment {

    protected static String TAG = "findme-basefragment";
    public static final String ARG_UID = "ARG_UID";
    protected String uid;

    protected SharedPreferences getPrefs(){
        log("getting shared preferences from fragment");
        return ((BaseActivity) getActivity()).getPrefs();
    }
    protected void writePref(String key,String value){
        SharedPreferences.Editor editor = getPrefs().edit();
        editor.putString(key, value);
    }

    protected void writePref(String key, boolean value){
        SharedPreferences.Editor editor = getPrefs().edit();
        editor.putBoolean(key, value);
    }

    protected void writePref(String key, int value){
        SharedPreferences.Editor editor = getPrefs().edit();
        editor.putInt(key, value);
    }
    protected void writePref(String key, float value){
        SharedPreferences.Editor editor = getPrefs().edit();
        editor.putFloat(key, value);
    }

    protected void writePref(String key, Long value){
        SharedPreferences.Editor editor = getPrefs().edit();
        editor.putLong(key, value);
    }

    private void checkForNullUID(){
        if (uid == null || uid.isEmpty()) {
            warn("WARNING UID IS NULL...WARNING UID IS NULL....WARNING...WARNING..");
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        checkForNullUID();
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
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
    }
}
