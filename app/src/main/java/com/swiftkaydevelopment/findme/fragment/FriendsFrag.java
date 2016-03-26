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
import com.swiftkaydevelopment.findme.events.OnFriendsRetrievedEvent;
import com.swiftkaydevelopment.findme.managers.UserManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

/**
 * Created by Kevin Haines on 3/2/2015.
 */
public class FriendsFrag extends BaseFragment implements FriendsAdapter.FriendsAdapterListener {
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
            UserManager.getInstance(uid).getFriends(uid);
        }

        if (mAdapter == null) {
            mAdapter = new FriendsAdapter(getActivity(), users, uid, this);
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
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);

    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEvent(OnFriendsRetrievedEvent event) {
        EventBus.getDefault().removeStickyEvent(event);
        mProgressBar.setVisibility(View.GONE);
        mAdapter.addFriends(event.friends);
        if (mAdapter.getItemCount() < 1) {
            mEmptyView.setVisibility(View.VISIBLE);
        } else {
            mEmptyView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onLastItem(User item) {
        //todo: pagination of friends
    }
}
