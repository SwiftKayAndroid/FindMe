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

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.swiftkaydevelopment.findme.R;
import com.swiftkaydevelopment.findme.data.Post;
import com.swiftkaydevelopment.findme.managers.AccountManager;
import com.swiftkaydevelopment.findme.managers.PersistenceManager;
import com.swiftkaydevelopment.findme.managers.UserManager;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class FullImageFragment extends BaseFragment implements OkCancelDialog.OkCancelDialogListener, PhotoViewAttacher.OnViewTapListener{

    public interface FullImageFragListener {
        void onImageDeleted(Post post);
    }
    public static final String TAG = "FullImageFragment";
    private static final String ARG_LOC = "ARG_LOC";

    private static final String ARG_PIC = "ARG_PIC";
    private static final String ARG_ISUSER = "ARG_ISUSER";
    private static final String DELETE_LABEL = "DELETE";

    private Post post;
    private String mLocation;
    private PhotoView ivPicture;
    private Toolbar mToolbar;
    private boolean isUser;
    private boolean mVisible = true;

    private FullImageFragListener mListener;

    /**
     * Factory method for a new instance of Fullimage Fragment using a Post
     *
     * @param uid User's id
     * @param post Post
     * @param isUser is this the users picture
     * @return new instance of Fullimage fragment
     */
    public static FullImageFragment newInstance(String uid, Post post, boolean isUser) {
        FullImageFragment frag = new FullImageFragment();
        Bundle b = new Bundle();
        b.putSerializable(ARG_PIC, post);
        b.putString(ARG_UID, uid);
        b.putBoolean(ARG_ISUSER, isUser);
        frag.setArguments(b);
        return frag;
    }

    public static FullImageFragment newInstance(String uid, String location, boolean isUser) {
        FullImageFragment frag = new FullImageFragment();
        Bundle b = new Bundle();
        b.putSerializable(ARG_LOC, location);
        b.putString(ARG_UID, uid);
        b.putBoolean(ARG_ISUSER, isUser);
        frag.setArguments(b);
        return frag;
    }

    public void setFullImageFragListener(FullImageFragListener listener) {
        mListener = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            uid = savedInstanceState.getString(ARG_UID);
            post = (Post) savedInstanceState.getSerializable(ARG_PIC);
            isUser = savedInstanceState.getBoolean(ARG_ISUSER);
            mLocation = savedInstanceState.getString(ARG_LOC);
        } else {
            if (getArguments() != null) {
                uid = getArguments().getString(ARG_UID);
                post = (Post) getArguments().getSerializable(ARG_PIC);
                isUser = getArguments().getBoolean(ARG_ISUSER);
                mLocation = getArguments().getString(ARG_LOC);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.viewfullimagepop, container, false);
        ivPicture = (PhotoView) layout.findViewById(R.id.ivviewfullimageimage);
        mToolbar = (Toolbar) layout.findViewById(R.id.fullImageToolbar);
        return layout;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (savedInstanceState != null) {
            post = (Post) savedInstanceState.getSerializable(ARG_PIC);
            mLocation = savedInstanceState.getString(ARG_LOC);
        }

        if (TextUtils.isEmpty(mLocation) && !TextUtils.isEmpty(post.getPostImage())) {
            Picasso.with(getActivity())
                    .load(post.getPostImage())
                    .into(ivPicture);
        } else if (!TextUtils.isEmpty(mLocation)) {
            Picasso.with(getActivity())
                    .load(mLocation)
                    .into(ivPicture);
        } else {
            getActivity().getSupportFragmentManager().popBackStack();
        }

        if (isUser && TextUtils.isEmpty(mLocation)) {
            mToolbar.setVisibility(View.VISIBLE);
            mToolbar.inflateMenu(R.menu.fullimage_menu);
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            });
            mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if (item.getItemId() == R.id.fullImageMenuDelete) {
                        showDialog("Delete", "Are you sure you want to delete this picture", "Delete", DELETE_LABEL);
                    } else if (item.getItemId() == R.id.fullImageMenuMakeProfilePicture) {
                        Log.e(TAG, "clicked");
                        AccountManager.getInstance(getActivity()).changeProfilePicture(uid, post.getPostImage());
                        PersistenceManager.getInstance(getActivity()).updatePropicloc(post.getPostImage());
                        Toast.makeText(getActivity(), "Profile picture updated", Toast.LENGTH_LONG).show();
                        UserManager.getInstance(uid).invalidateMe();
                    }
                    return true;
                }
            });
        } else {
            mToolbar.setVisibility(View.GONE);
        }
        ivPicture.setOnViewTapListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        OkCancelDialog dialog = (OkCancelDialog) getActivity().getSupportFragmentManager().findFragmentByTag(OkCancelDialog.TAG);
        if (dialog != null) {
            dialog.setOkCancelDialogListener(this);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        OkCancelDialog dialog = (OkCancelDialog) getActivity().getSupportFragmentManager().findFragmentByTag(OkCancelDialog.TAG);
        if (dialog != null) {
            dialog.setOkCancelDialogListener(null);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(ARG_UID, uid);
        outState.putSerializable(ARG_PIC, post);
        outState.putBoolean(ARG_ISUSER, isUser);
        outState.putString(ARG_LOC, mLocation);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onOkPressed(Object tag) {
        AccountManager.getInstance(getActivity()).deletePicture(uid, post.getPostImage());
        if (mListener != null) {
            mListener.onImageDeleted(post);
        }
        getActivity().getSupportFragmentManager().popBackStack();
    }

    @Override
    public void onCancelPressed(Object tag) {

    }

    private void showDialog(String title, String message, String pos, String label) {
        OkCancelDialog dialog = (OkCancelDialog) getActivity().getSupportFragmentManager().findFragmentByTag(OkCancelDialog.TAG);
        if (dialog == null) {
            dialog = OkCancelDialog.newInstance(title, message, pos, null);
            dialog.show(getActivity().getSupportFragmentManager(), OkCancelDialog.TAG);
            dialog.setOkCancelDialogListener(this);
        }

    }

    @Override
    public void onViewTap(View view, float x, float y) {
        toggle();
    }

    /**
     * Toggles the system ui visibility
     *
     */
    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    /**
     * Hides the system ui
     *
     */
    private void hide() {
        // Hide UI first

        if (getView() != null) {
            mVisible = false;
            if (mToolbar.getVisibility() == View.VISIBLE) {
                mToolbar.setVisibility(View.INVISIBLE);
            }
            getView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                    | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                    | View.SYSTEM_UI_FLAG_IMMERSIVE);

            getView().setFitsSystemWindows(mVisible);
        }
    }

    /**
     * Shows the system ui
     *
     */
    @SuppressLint("InlinedApi")
    private void show() {
        if (getView() != null) {
            if (mToolbar.getVisibility() == View.INVISIBLE) {
                mToolbar.setVisibility(View.VISIBLE);
            }
            mVisible = true;

            getView().setFitsSystemWindows(mVisible);

            getView().setSystemUiVisibility(0);
        }
    }
}
