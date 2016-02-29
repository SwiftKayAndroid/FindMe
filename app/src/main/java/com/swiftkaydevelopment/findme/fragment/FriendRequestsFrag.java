package com.swiftkaydevelopment.findme.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.swiftkaydevelopment.findme.adapters.FriendRequestsAdapter;
import com.swiftkaydevelopment.findme.data.User;
import com.swiftkaydevelopment.findme.managers.UserManager;
import com.swiftkaydevelopment.findme.R;

import java.util.ArrayList;

/**
 * Created by Kevin Haines on 9/9/15.
 */
public class FriendRequestsFrag extends BaseFragment implements UserManager.UserManagerListener,
        FriendRequestsAdapter.FriendRequestsAdapterListener {
    public static final String TAG = "FriendRequestsFrag";
    private  static  final String ARG_USERS = "ARG_USERS";

    private ArrayList<User> users = new ArrayList<>();

    private RecyclerView mRecyclerView;
    private FriendRequestsAdapter mAdapter;
    private View                mEmptyView;
    private ProgressBar mProgressBar;

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

        View layout = inflater.inflate(R.layout.friendrequestsfrag, container, false);
        mRecyclerView = (RecyclerView) layout.findViewById(R.id.recyclerViewFriendRequests);
        mEmptyView = layout.findViewById(R.id.friendsEmptyView);
        mProgressBar = (ProgressBar) layout.findViewById(R.id.progressBar);

        return layout;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (savedInstanceState != null) {
            mProgressBar.setVisibility(View.GONE);
            users = (ArrayList) savedInstanceState.getSerializable(ARG_USERS);
            if (users == null || users.isEmpty()) {
                mEmptyView.setVisibility(View.VISIBLE);
            } else {
                mEmptyView.setVisibility(View.GONE);
            }
        } else {
            mProgressBar.setVisibility(View.VISIBLE);
            UserManager.getInstance(uid).getFriendRequests(uid);
            mEmptyView.setVisibility(View.GONE);
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
        UserManager.getInstance(uid).addListener(this);
        if (mAdapter != null) {
            mAdapter.setFriendRequestsAdapterListener(this);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        UserManager.getInstance(uid).removeListener(this);
        if (mAdapter != null) {
            mAdapter.setFriendRequestsAdapterListener(null);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(ARG_UID, uid);
        outState.putSerializable(ARG_USERS, users);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onFriendRequestAccepted(User user) {
        UserManager.getInstance(uid).sendFriendRequest(uid, user);
    }

    @Override
    public void onFriendRequestDenied(User user) {
        UserManager.getInstance(uid).denyFriendRequest(uid, user);
    }

    @Override
    public void onFriendRequestsRetrieved(ArrayList<User> users) {
        mProgressBar.setVisibility(View.GONE);
        mAdapter.addUsers(users);
        if (mAdapter.getItemCount() < 1) {
            mEmptyView.setVisibility(View.VISIBLE);
        } else {
            mEmptyView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onFriendsRetrieved(ArrayList<User> users) {

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