package com.swiftkaydevelopment.findme.fragment;

import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.swiftkaydevelopment.findme.R;
import com.swiftkaydevelopment.findme.adapters.FriendsAdapter;
import com.swiftkaydevelopment.findme.data.User;
import com.swiftkaydevelopment.findme.managers.UserManager;

import java.util.ArrayList;

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
    private ProgressBar mProgressBar;

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
        mProgressBar = (ProgressBar) layout.findViewById(R.id.progressBar);
        return layout;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (savedInstanceState != null) {
            mProgressBar.setVisibility(View.GONE);
            users = (ArrayList) savedInstanceState.getSerializable(ARG_FRIENDS);
            if (users == null || users.isEmpty()) {
                mEmptyView.setVisibility(View.VISIBLE);
            } else {
                mEmptyView.setVisibility(View.GONE);
            }
        } else {
            mProgressBar.setVisibility(View.VISIBLE);
            mEmptyView.setVisibility(View.GONE);
            UserManager.getInstance(uid, getActivity()).getFriends(uid);
        }

        if (mAdapter == null) {
            mAdapter = new FriendsAdapter(getActivity(), users, uid);
        }

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(ARG_UID, uid);
        outState.putSerializable(ARG_FRIENDS, users);
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
        mProgressBar.setVisibility(View.GONE);
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

    @Override
    public void onPeopleFound(ArrayList<User> users) {

    }

    @Override
    public void onProfileViewsFetched(ArrayList<User> users) {

    }
}
