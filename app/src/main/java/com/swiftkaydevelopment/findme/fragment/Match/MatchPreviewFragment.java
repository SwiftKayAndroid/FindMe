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

package com.swiftkaydevelopment.findme.fragment.Match;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.swiftkaydevelopment.findme.R;
import com.swiftkaydevelopment.findme.data.User;
import com.swiftkaydevelopment.findme.fragment.BaseFragment;
import com.swiftkaydevelopment.findme.managers.MatchManager;

import java.util.ArrayList;

public class MatchPreviewFragment extends BaseFragment implements View.OnClickListener, MatchManager.MatchManagerListener{
    public static final String TAG = "MatchPreviewFragment";

    private static final String ARG_USES = "ARG_USERS";

    private ImageView ivPreview;
    private View headerView;
    private ImageButton ibLike;
    private ImageButton ibDislike;
    private Toolbar mToolbar;

    private User currentUser;

    ArrayList<User> users = new ArrayList<>();

    //header
    TextView aboutMe, age, gender, orientation, location, status, lookingfor;
    TextView hasKids, wantsKids, profession, school, weed;
    ImageView mEditProfile;

    public static MatchPreviewFragment newInstance(String uid) {
        MatchPreviewFragment frag = new MatchPreviewFragment();
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
            users = (ArrayList) savedInstanceState.getSerializable(ARG_USES);
        } else {
            if (getArguments() != null) {
                uid = getArguments().getString(ARG_UID);
            }
            MatchManager.getInstance(getActivity()).getPotentialMatches(uid);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.match_search, container, false);

        ivPreview = (ImageView) layout.findViewById(R.id.ivMatchPreviewPhoto);
        ibDislike = (ImageButton) layout.findViewById(R.id.ibMatchDislike);
        ibLike = (ImageButton) layout.findViewById(R.id.ibMatchLike);
        headerView = layout.findViewById(R.id.matchesProfileHeader);
        mToolbar = (Toolbar) layout.findViewById(R.id.matchPreviewToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        mToolbar.setNavigationIcon(R.mipmap.ic_arrow_back_white_24dp);

        age = (TextView) headerView.findViewById(R.id.profileHeaderAge);
        location = (TextView) headerView.findViewById(R.id.profileHeaderLocation);
        aboutMe = (TextView) headerView.findViewById(R.id.profileHeaderAboutMe);
        gender = (TextView) headerView.findViewById(R.id.profileHeaderGender);
        orientation = (TextView) headerView.findViewById(R.id.profileHeaderOrientation);
        status = (TextView) headerView.findViewById(R.id.profileHeaderStatus);
        lookingfor = (TextView) headerView.findViewById(R.id.profileHeaderLookingFor);
        mEditProfile = (ImageView) headerView.findViewById(R.id.ivProfileEditProfile);
        mEditProfile.setVisibility(View.GONE);

        hasKids = (TextView) headerView.findViewById(R.id.profileHeaderHasKids);
        wantsKids = (TextView) headerView.findViewById(R.id.profileHeaderWantsKids);
        profession = (TextView) headerView.findViewById(R.id.profileHeaderProfession);
        school = (TextView) headerView.findViewById(R.id.profileHeaderSchool);
        weed = (TextView) headerView.findViewById(R.id.profileHeaderWeed);

        return layout;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (savedInstanceState != null) {
            uid = savedInstanceState.getString(ARG_UID);
            users = (ArrayList) savedInstanceState.getSerializable(ARG_USES);
        }
        loadNextUser();

        ibDislike.setOnClickListener(this);
        ibLike.setOnClickListener(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(ARG_UID, uid);
        outState.putSerializable(ARG_USES, users);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        MatchManager.getInstance(getActivity()).addListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        MatchManager.getInstance(getActivity()).removeListener(this);
    }

    private void likeUser(User user) {
        MatchManager.getInstance(getActivity()).matchUser(user, uid);
        users.remove(user);
        loadNextUser();
    }

    private void dislikeUser(User user) {
        MatchManager.getInstance(getActivity()).dislikeUser(user, uid);
        users.remove(user);
        loadNextUser();
    }

    private void loadNextUser() {
        if (users.size() > 0) {
            currentUser = users.get(0);
            mToolbar.setTitle(currentUser.getName());
            aboutMe.setText(currentUser.getAboutMe());
            orientation.setText(currentUser.getOrientation().toString());
            age.setText(Integer.toString(currentUser.getAge()));
            gender.setText(currentUser.getGender().toString());
            location.setText(currentUser.getLocation().getCity());
            status.setText(currentUser.mRelationshipStatus);
            lookingfor.setText(currentUser.getLookingForString());
            hasKids.setText(currentUser.hasKids);
            wantsKids.setText(currentUser.wantsKids);
            profession.setText(currentUser.profession);
            school.setText(currentUser.school);
            weed.setText(currentUser.weed);

            if (currentUser.getPropicloc().equals("")) {
                Picasso.with(getActivity())
                        .load(R.drawable.ic_placeholder)
                        .into(ivPreview);
            } else {
                Picasso.with(getActivity())
                        .load(currentUser.getPropicloc())
                        .into(ivPreview);
            }

        } else {
            currentUser = null;
        }

        if (users.size() > 0 && users.size() < 4) {
            MatchManager.getInstance(getActivity()).getPotentialMatches(uid);
        }
    }

    @Override
    public void onPotentialsFound(ArrayList<User> users) {
        this.users.addAll(users);
        if (currentUser == null) {
            loadNextUser();
        }
    }

    @Override
    public void onMatchesRetrieved(ArrayList<User> users) {

    }

    @Override
    public void onLikedUsersRetrieved(ArrayList<User> users) {

    }

    @Override
    public void onWhoLikedMeRetrieved(ArrayList<User> users) {

    }

    @Override
    public void onClick(View v) {
        if (currentUser != null) {
            if (v.getId() == ibDislike.getId()) {
                dislikeUser(currentUser);
            } else if (v.getId() == ibLike.getId()) {
                likeUser(currentUser);
            }
        }
    }
}
