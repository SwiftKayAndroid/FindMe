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
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.swiftkaydevelopment.findme.R;
import com.swiftkaydevelopment.findme.data.Post;
import com.swiftkaydevelopment.findme.managers.AccountManager;
import com.swiftkaydevelopment.findme.managers.PersistenceManager;
import com.swiftkaydevelopment.findme.managers.UserManager;

public class FullImageFragment extends BaseFragment implements OkCancelDialog.OkCancelDialogListener{

    public interface FullImageFragListener {
        void onImageDeleted(Post post);
    }
    public static final String TAG = "FullImageFragment";

    private static final String ARG_PIC = "ARG_PIC";
    private static final String ARG_ISUSER = "ARG_ISUSER";
    private static final String DELETE_LABEL = "DELETE";

    private Post post;
    private ImageView ivPicture;
    private Toolbar mToolbar;
    private boolean isUser;

    private FullImageFragListener mListener;

    public static FullImageFragment newInstance(String uid, Post post, boolean isUser) {
        FullImageFragment frag = new FullImageFragment();
        Bundle b = new Bundle();
        b.putSerializable(ARG_PIC, post);
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
        } else {
            if (getArguments() != null) {
                uid = getArguments().getString(ARG_UID);
                post = (Post) getArguments().getSerializable(ARG_PIC);
                isUser = getArguments().getBoolean(ARG_ISUSER);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.viewfullimagepop, container, false);
        ivPicture = (ImageView) layout.findViewById(R.id.ivviewfullimageimage);
        mToolbar = (Toolbar) layout.findViewById(R.id.fullImageToolbar);
        return layout;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Picasso.with(getActivity())
                .load(post.getPostImage())
                .into(ivPicture);

        if (isUser) {
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
}
