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

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.swiftkaydevelopment.findme.R;
import com.swiftkaydevelopment.findme.activity.MessagesActivity;
import com.swiftkaydevelopment.findme.activity.UpdateStatus;
import com.swiftkaydevelopment.findme.activity.ViewPhotos;
import com.swiftkaydevelopment.findme.adapters.PostAdapter;
import com.swiftkaydevelopment.findme.data.Post;
import com.swiftkaydevelopment.findme.data.User;
import com.swiftkaydevelopment.findme.managers.PostManager;
import com.swiftkaydevelopment.findme.managers.UserManager;
import com.swiftkaydevelopment.findme.services.UploadService;

import java.util.ArrayList;
import java.util.Map;

public class ProfileFragment extends BaseFragment implements
        View.OnClickListener, PostManager.PostsListener,
        PostAdapter.PostAdapterListener{
    public static final String TAG = "ProfileFragment";
    private static final String ARG_USER = "ARG_USER";
    private static final String ARG_POSTS = "ARG_POSTS";

    private User                user;
    private RecyclerView        mRecyclerView;
    private ImageView mProfilePicture;

    private FloatingActionButton mFabBase;
    private FloatingActionButton mFabLeft;
    private FloatingActionButton mFabCenter;
    private FloatingActionButton mFabUpper;

    private PostAdapter mPostAdapter;
    private ArrayList<Post> mPostList = new ArrayList<>();

    public static ProfileFragment newInstance(User user, String uid) {
        ProfileFragment frag = new ProfileFragment();
        Bundle b = new Bundle();
        b.putSerializable(ARG_USER, user);
        b.putString(ARG_UID, uid);
        frag.setArguments(b);
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (savedInstanceState != null) {
            user = (User) savedInstanceState.getSerializable(ARG_USER);
            mPostList = (ArrayList) savedInstanceState.getSerializable(ARG_POSTS);
            uid = savedInstanceState.getString(ARG_UID);
        } else {
            if (getArguments() != null) {
                user = (User) getArguments().getSerializable(ARG_USER);
                uid = getArguments().getString(ARG_UID);
            }
            PostManager.getInstance().fetchUserPosts(user, "0", uid);
            if (!user.getOuid().equals(uid)) {
                UserManager.getInstance(uid, getActivity()).addProfileView(uid, user);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.profile_fragment, container, false);
        Toolbar toolbar = (Toolbar) layout.findViewById(R.id.profileToolbar);
        toolbar.setNavigationIcon(R.mipmap.ic_arrow_back_white_24dp);
        toolbar.setTitle(user.getName());
        toolbar.inflateMenu(R.menu.profile_menu);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.profileReportProfile) {
                    Toast.makeText(getActivity(), "Profile Reported", Toast.LENGTH_SHORT).show();
                } else if (item.getItemId() == R.id.profileMenuPictures) {
                    getActivity().startActivity(ViewPhotos.createIntent(getActivity(), user));
                }
                return true;
            }
        });

        mFabBase = (FloatingActionButton) layout.findViewById(R.id.profileFabBase);
        mFabLeft = (FloatingActionButton) layout.findViewById(R.id.profileFabLeft);
        mFabCenter = (FloatingActionButton) layout.findViewById(R.id.profileFabCenter);
        mFabUpper = (FloatingActionButton) layout.findViewById(R.id.profileFabUpper);

        mFabBase.setOnClickListener(this);
        mFabLeft.setOnClickListener(this);
        mFabCenter.setOnClickListener(this);
        mFabUpper.setOnClickListener(this);
        mProfilePicture = (ImageView) layout.findViewById(R.id.profileProfilePicture);

        mRecyclerView = (RecyclerView) layout.findViewById(R.id.recyclerViewProfile);

        return layout;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        if (user == null) {
            throw new IllegalStateException("user is null");
        }

        if (user.getPropicloc().equals("")) {
            mProfilePicture.setImageResource(R.drawable.ic_placeholder);
        } else {
            Picasso.with(getActivity())
                    .load(user.getPropicloc())
                    .into(mProfilePicture);
        }

        mProfilePicture.setOnClickListener(this);
        if (mPostAdapter != null) {
            log("adapter is not null");
            mRecyclerView.setAdapter(mPostAdapter);
        } else {
            log("adapter is null");
            log("size " + Integer.toString(mPostList.size()));
            mPostAdapter = new PostAdapter(getActivity(), mPostList, user, true, user.getOuid().equals(uid));
            mRecyclerView.setAdapter(mPostAdapter);
        }

        if (user.isFriend()) {
            mFabBase.setImageResource(R.mipmap.ic_message_black_24dp);
            mFabBase.setColorFilter(Color.WHITE);
        }

        initializeProfileInformation();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(ARG_POSTS, mPostList);
        outState.putSerializable(ARG_USER, user);
        outState.putString(ARG_UID, uid);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        super.onPause();
        PostManager.getInstance().removeListener(this);
        mPostAdapter.setPostAdapterListener(null);
    }

    @Override
    public void onResume() {
        super.onResume();
        PostManager.getInstance().addPostListener(this);
        if (user.getOuid().equals(uid)) {
            user = UserManager.getInstance(uid, getActivity()).me();
        }
        mPostAdapter.setPostAdapterListener(this);
    }

    private void initializeProfileInformation() {

        StringBuilder sb = new StringBuilder();
        for (Map.Entry entry : user.getSearchingFor().entrySet()) {
            if (entry.getValue().equals("yes")) {
                sb.append(entry.getKey() + " ");
            }
        }
    }

    /**
     * Expands the fabs for the users own profile
     */
    private void expandOwnProfileFabs() {
        mFabLeft.setImageResource(R.drawable.ic_action_edit_dark);
        mFabUpper.setImageResource(R.mipmap.ic_photo_camera_white_24dp);
        mFabUpper.setVisibility(mFabUpper.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
        setVisibility();

    }

    /**
     * Expands the fabs for a different users profile
     */
    private void expandOtherProfileFabs() {
        if (user.isFriend()) {
            startSendMessage();
        } else {
            mFabUpper.setImageResource(R.mipmap.ic_person_add_white_24dp);
            mFabUpper.setVisibility(View.VISIBLE);
            mFabLeft.setImageResource(R.mipmap.ic_message_black_24dp);
            mFabLeft.setColorFilter(Color.WHITE);
            setVisibility();
        }
    }

    private void setVisibility () {
        if (mFabLeft.getVisibility() == View.GONE) {
            mFabLeft.setVisibility(View.VISIBLE);
        } else {
            mFabUpper.setVisibility(View.GONE);
            mFabLeft.setVisibility(View.GONE);
        }
    }

    private void rotate(View v) {
        AnimatorSet set = new AnimatorSet();
        float startRotation = v.getRotation();
        float endRotation = 0;
        if (startRotation == 0) {
            endRotation = 45;
        }
        set.play(ObjectAnimator.ofFloat(v, View.ROTATION, startRotation, endRotation));
        set.setDuration(200);
        set.start();
    }

    /**
     * Starts activity for sending a message
     */
    private void startSendMessage() {
        startActivity(MessagesActivity.createIntent(getActivity(), user));
    }

    /**
     * Starts User to match user
     */
    private void matchUser() {
        UserManager.getInstance(uid, getActivity()).sendMatchRequest(uid, user);
    }

    private void editProfile() {
        EditProfile editProfile = EditProfile.newInstance(uid, user);
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, editProfile, EditProfile.TAG)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onPostsRetrieved(ArrayList<Post> posts) {

    }

    @Override
    public void onProfilePostsRetrieved(ArrayList<Post> posts) {
        for (Post post : posts) {
            post.setUser(user);
        }

        mPostAdapter.clearAdapter();
        mPostAdapter.addPosts(posts);
    }

    @Override
    public void onCommentsClicked(Post post) {
    }

    @Override
    public void onImageClicked(Post post) {
        FullImageFragment fullImageFragment = FullImageFragment.newInstance(uid, post, (post.getPostingUsersId().equals(uid)));
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, fullImageFragment, FullImageFragment.TAG)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onLastPost(Post post) {

    }

    @Override
    public void onClick(View v) {
        boolean isSameProfile = false;
        if (user.getOuid().equals(uid)) {
            isSameProfile = true;
        }
        if (v.getId() == R.id.profileFabBase) {
            rotate(v);
            if (isSameProfile) {
                expandOwnProfileFabs();
            } else {
                expandOtherProfileFabs();
            }
        } else if (v.getId() == R.id.profileFabLeft) {
            if (isSameProfile) {
                getActivity().startActivity(UpdateStatus.createIntent(getActivity()));
            } else {
                startSendMessage();
            }
        } else if (v.getId() == R.id.profileFabUpper) {
            if (!isSameProfile) {
                UserManager.getInstance(uid, getActivity()).sendFriendRequest(uid, user);
                Toast.makeText(getActivity(), "Friend Request sent", Toast.LENGTH_SHORT).show();
            } else {
                getActivity().startActivity(UploadService.createIntent(getActivity()));
            }
        } else if (v.getId() == R.id.profileProfilePicture) {
            FullImageFragment fullImageFragment = FullImageFragment.newInstance(uid, null, (user.getOuid().equals(uid)));
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(android.R.id.content, fullImageFragment, FullImageFragment.TAG)
                    .addToBackStack(null)
                    .commit();
        }
    }
}
