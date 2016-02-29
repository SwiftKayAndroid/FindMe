package com.swiftkaydevelopment.findme.fragment;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.swiftkaydevelopment.findme.R;
import com.swiftkaydevelopment.findme.activity.ProfileActivity;
import com.swiftkaydevelopment.findme.adapters.FindPeopleAdapter;
import com.swiftkaydevelopment.findme.data.User;
import com.swiftkaydevelopment.findme.managers.UserManager;

import java.util.ArrayList;

/**
 * Created by Kevin Haines on 2/25/2015.
 */
public class FindPeopleFrag extends BaseFragment implements UserManager.UserManagerListener, FindPeopleAdapter.ConnectAdapterListener{
    public static final String TAG = "FindPeopleFrag";
    private static final String ARG_USERS = "ARG_USERS";

    public static final String UID_ARGS = "UID_ARGS";

    private ArrayList<User> users = new ArrayList<>();
    private FindPeopleAdapter mAdapter;
    private boolean mLoadingMore = false;

    private RecyclerView mRecyclerView;
    private ProgressBar mLoadingMorePb;
    private ProgressBar mProgressBar;

    public static FindPeopleFrag newInstance(String id){
        FindPeopleFrag frag = new FindPeopleFrag();
        Bundle b = new Bundle();
        b.putString(UID_ARGS,id);
        frag.setArguments(b);
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            uid = savedInstanceState.getString(UID_ARGS);
            users = (ArrayList) savedInstanceState.getSerializable(ARG_USERS);
        } else {
            if(getArguments() != null){
                uid = getArguments().getString(UID_ARGS);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.findpeople, container, false);
        mRecyclerView = (RecyclerView) layout.findViewById(R.id.recyclerView);
        mLoadingMorePb = (ProgressBar) layout.findViewById(R.id.loadingMorePb);
        mProgressBar = (ProgressBar) layout.findViewById(R.id.progressBar);

        return layout;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mLoadingMorePb.setVisibility(View.GONE);

        if (savedInstanceState != null) {
            mProgressBar.setVisibility(View.GONE);
            users = (ArrayList) savedInstanceState.getSerializable(ARG_USERS);
        } else {
            mProgressBar.setVisibility(View.VISIBLE);
            UserManager.getInstance(uid).findPeople(uid, "0");
        }

        if (mAdapter == null) {
            mAdapter = new FindPeopleAdapter(getActivity(), users, uid);
        }

        mAdapter.setListener(this);

        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(
                getActivity().getResources().getInteger(R.integer.grid_layout_columns_connect),
                StaggeredGridLayoutManager.VERTICAL));
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(UID_ARGS, uid);
        outState.putSerializable(ARG_USERS, users);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        UserManager.getInstance(uid).addListener(this);
        if (mAdapter != null) {
            mAdapter.setListener(this);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        UserManager.getInstance(uid).removeListener(this);
        mAdapter.setListener(null);
    }

    @Override
    public void onFriendRequestsRetrieved(ArrayList<User> users) {}

    @Override
    public void onFriendsRetrieved(ArrayList<User> users) {}

    @Override
    public void onMatchesRetrieved(ArrayList<User> users) {}

    @Override
    public void onProfileViewsFetched(ArrayList<User> users) {}

    @Override
    public void onPeopleFound(ArrayList<User> users) {
        mLoadingMore = false;
        mLoadingMorePb.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.GONE);
        if (mAdapter != null && users != null) {
            mAdapter.addUsers(users);
        }
    }

    @Override
    public void onLastItem(User lastUser) {
        if (!mLoadingMore) {
            mLoadingMore = true;
            mLoadingMorePb.setVisibility(View.VISIBLE);
            UserManager.getInstance(uid).findPeople(uid, lastUser.getOuid());
        }
    }

    @Override
    public void onUserSelected(User user) {
        getActivity().startActivity(ProfileActivity.createIntent(getActivity(), user));
    }
}
