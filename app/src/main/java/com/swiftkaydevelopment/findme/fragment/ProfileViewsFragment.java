/*
 *      Copyright (C) 2015 Kevin Haines
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 */

package com.swiftkaydevelopment.findme.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.swiftkaydevelopment.findme.R;
import com.swiftkaydevelopment.findme.activity.ProfileActivity;
import com.swiftkaydevelopment.findme.adapters.ProfileViewsAdapter;
import com.swiftkaydevelopment.findme.data.User;
import com.swiftkaydevelopment.findme.managers.UserManager;

import java.util.ArrayList;

public class ProfileViewsFragment extends BaseFragment implements ProfileViewsAdapter.ProfileViewsAdapterListener,
        UserManager.UserManagerListener{
    public static final String TAG = "ProfileViewsFragment";
    private static final String ARG_USERS = "ARG_USERS";

    private ArrayList<User> users =  new ArrayList<>();
    private ProfileViewsAdapter mAdapter;

    private RecyclerView mRecyclerView;

    public static ProfileViewsFragment newInstance(String uid) {
        ProfileViewsFragment frag = new ProfileViewsFragment();
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
            UserManager.getInstance(uid, getActivity()).getProfileViews(uid, "0");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.profile_views_fragment, container, false);
        mRecyclerView = (RecyclerView) layout.findViewById(R.id.profileViewsRecyclerView);
        return layout;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (mAdapter == null) {
            mAdapter = new ProfileViewsAdapter(getActivity(), users);
        }

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(ARG_UID, uid);
        outState.putSerializable(ARG_USERS, users);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        UserManager.getInstance(uid, getActivity()).addListener(this);
        if (mAdapter != null) {
            mAdapter.setProfileViewsAdapterListener(this);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        UserManager.getInstance(uid, getActivity()).removeListener(this);
        if (mAdapter != null) {
            mAdapter.setProfileViewsAdapterListener(null);
        }
    }

    @Override
    public void onProfileViewClicked(User user) {
        getActivity().startActivity(ProfileActivity.createIntent(getActivity(), user));
    }

    @Override
    public void onFriendRequestsRetrieved(ArrayList<User> users) {

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
        mAdapter.addFirstUsers(users);
    }
}