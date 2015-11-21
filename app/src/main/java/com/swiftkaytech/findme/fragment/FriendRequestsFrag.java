package com.swiftkaytech.findme.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.swiftkaytech.findme.R;
import com.swiftkaytech.findme.adapters.FriendRequestsAdapter;
import com.swiftkaytech.findme.data.User;
import com.swiftkaytech.findme.managers.UserManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kevin Haines on 9/9/15.
 */
public class FriendRequestsFrag extends BaseFragment implements UserManager.UserManagerListener {
    public static final String TAG = "FriendRequestsFrag";
    private  static  final String ARG_USERS = "ARG_USERS";

    private ArrayList<User> users = new ArrayList<>();

    private RecyclerView mRecyclerView;
    private FriendRequestsAdapter mAdapter;

    public static FriendRequestsFrag newInstance(String uid) {
        FriendRequestsFrag frag = new FriendRequestsFrag();
        Bundle b = new Bundle();
        b.putString(ARG_UID, uid);
        frag.setArguments(b);
        return frag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            uid = savedInstanceState.getString(ARG_UID);
        } else {
            if (getArguments() != null) {
                uid = getArguments().getString(ARG_UID);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.friendrequestsfrag,container,false);

        return layout;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (savedInstanceState != null) {
            users = (ArrayList) savedInstanceState.getSerializable(ARG_USERS);
        } else {
            UserManager.getInstance(uid, getActivity()).getFriendRequests(uid);
        }

        if (mAdapter == null) {
            mAdapter = new FriendRequestsAdapter(getActivity(), users, uid);
        }
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mAdapter);

    }

    @Override
    public void onResume() {
        super.onResume();
        UserManager.getInstance(uid, getActivity()).addListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        UserManager.getInstance(uid, getActivity()).removeListener(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(ARG_UID, uid);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onFriendRequestsRetrieved(ArrayList<User> users) {
        mAdapter.addUsers(users);
    }

    @Override
    public void onFriendsRetrieved(ArrayList<User> users) {

    }

    @Override
    public void onMatchesRetrieved(ArrayList<User> users) {

    }
}
