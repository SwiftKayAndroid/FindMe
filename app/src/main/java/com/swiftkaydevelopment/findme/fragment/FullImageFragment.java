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

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.swiftkaydevelopment.findme.R;
import com.swiftkaydevelopment.findme.data.User;
import com.swiftkaydevelopment.findme.managers.AccountManager;
import com.swiftkaydevelopment.findme.managers.PersistenceManager;
import com.swiftkaydevelopment.findme.utils.ImageLoader;

public class FullImageFragment extends BaseFragment{
    public static final String TAG = "FullImageFragment";

    private static final String ARG_PIC = "ARG_PIC";
    private static final String ARG_ISUSER = "ARG_ISUSER";

    private String picloc;
    private ImageView ivPicture;
    private ImageLoader imageLoader;
    private Toolbar mToolbar;
    private boolean isUser;

    public static FullImageFragment newInstance(String uid, String picloc, boolean isUser) {
        FullImageFragment frag = new FullImageFragment();
        Bundle b = new Bundle();
        b.putSerializable(ARG_PIC, picloc);
        b.putString(ARG_UID, uid);
        b.putBoolean(ARG_ISUSER, isUser);
        frag.setArguments(b);
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            uid = savedInstanceState.getString(ARG_UID);
            picloc = savedInstanceState.getString(ARG_PIC);
            isUser = savedInstanceState.getBoolean(ARG_ISUSER);
        } else {
            if (getArguments() != null) {
                uid = getArguments().getString(ARG_UID);
                picloc = getArguments().getString(ARG_PIC);
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
        imageLoader.DisplayImage(picloc, ivPicture, true);
        if (isUser) {
            mToolbar.setVisibility(View.VISIBLE);
            mToolbar.inflateMenu(R.menu.fullimage_menu);
            mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if (item.getItemId() == R.id.fullImageMenuDelete) {
                        AccountManager.getInstance(getActivity()).deletePicture(uid, picloc);
                    } else if (item.getItemId() == R.id.fullImageMenuMakeProfilePicture) {
                        Log.e(TAG, "clicked");
                        AccountManager.getInstance(getActivity()).changeProfilePicture(uid, picloc);
                        PersistenceManager.getInstance(getActivity()).updatePropicloc(picloc);
                    }
                    return true;
                }
            });
        } else {
            mToolbar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        imageLoader = new ImageLoader(context);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(ARG_UID, uid);
        outState.putString(ARG_PIC, picloc);
        outState.putBoolean(ARG_ISUSER, isUser);
        super.onSaveInstanceState(outState);
    }
}
