package com.swiftkaydevelopment.findme.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

/**
 * Created by Kevin Haines on 10/20/15.
 */
public class BaseFragment extends Fragment {

    protected static String TAG = "findme-basefragment";
    public static final String ARG_UID = "ARG_UID";
    protected String uid;

    private void checkForNullUID(){
        if (uid == null || uid.isEmpty()) {
            warn("WARNING UID IS NULL...WARNING UID IS NULL....WARNING...WARNING..");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null && getArguments() != null) {
            uid = getArguments().getString(ARG_UID);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        checkForNullUID();
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
