package com.swiftkaytech.findme.fragment;

import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.swiftkaytech.findme.R;
import com.swiftkaytech.findme.adapters.FriendsAdapter;
import com.swiftkaytech.findme.data.User;
import com.swiftkaytech.findme.managers.UserManager;
import com.swiftkaytech.findme.views.TabHolder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kevin Haines on 3/2/2015.
 */
public class FriendsFrag extends BaseFragment implements UserManager.UserManagerListener{
    public static final String TAG = "FriendsFrag";
    private static final String ARG_FRIENDS = "ARG_FRIENDS";

    private RecyclerView mRecyclerView;
    private FriendsAdapter mAdapter;

    private ArrayList<User> users = new ArrayList<>();
    private View mEmptyView;

    public static FriendsFrag newInstance(String uid) {
        FriendsFrag frag = new FriendsFrag();
        Bundle b = new Bundle();
        b.putString(ARG_UID, uid);
        frag.setArguments(b);
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            uid = savedInstanceState.getString(ARG_UID);
        } else {
            if (getArguments() != null) {
                uid = getArguments().getString(ARG_UID);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.friendslistfrag, container, false);
        mRecyclerView = (RecyclerView) layout.findViewById(R.id.recyclerViewFriendsList);
        mEmptyView = layout.findViewById(R.id.friendsEmptyView);
        return layout;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (savedInstanceState != null) {
            users = (ArrayList) savedInstanceState.getSerializable(ARG_FRIENDS);
        } else {
            UserManager.getInstance(uid, getActivity()).getFriends(uid);
        }

        if (mAdapter == null) {
            mAdapter = new FriendsAdapter(getActivity(), users, uid);
        }

        if (mAdapter.getItemCount() < 1) {
            mEmptyView.setVisibility(View.VISIBLE);
        } else {
            mEmptyView.setVisibility(View.GONE);
        }
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(ARG_UID, uid);
        super.onSaveInstanceState(outState);
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
    public void onFriendRequestsRetrieved(ArrayList<User> users) {

    }

    @Override
    public void onFriendsRetrieved(ArrayList<User> users) {
        mAdapter.addFriends(users);
        if (mAdapter.getItemCount() < 1) {
            mEmptyView.setVisibility(View.VISIBLE);
        } else {
            mEmptyView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onMatchesRetrieved(ArrayList<User> users) {

    }
}
