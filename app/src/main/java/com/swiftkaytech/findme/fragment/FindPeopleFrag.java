package com.swiftkaytech.findme.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import com.swiftkaytech.findme.R;
import com.swiftkaytech.findme.adapters.FindPeopleAdapter;
import com.swiftkaytech.findme.data.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kevin Haines on 2/25/2015.
 */
public class FindPeopleFrag extends BaseFragment {

    public static final String UID_ARGS = "UID_ARGS";

    private ArrayList<User> mUserList;

    GridView mGridView;
    boolean refreshing = false;

    public static FindPeopleFrag newInstance(String id){
        FindPeopleFrag frag = new FindPeopleFrag();
        Bundle b = new Bundle();
        b.putString(UID_ARGS,id);
        frag.setArguments(b);
        return frag;
    }

    public FindPeopleFrag(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            uid = savedInstanceState.getString(UID_ARGS);
        } else {
            if(getArguments() != null){
                uid = getArguments().getString(UID_ARGS);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(UID_ARGS, uid);
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.findpeople, container, false);
        mGridView = (GridView) layout.findViewById(R.id.gvviewfindpeoplefrag);

        return layout;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {


        super.onViewCreated(view, savedInstanceState);
    }
}
