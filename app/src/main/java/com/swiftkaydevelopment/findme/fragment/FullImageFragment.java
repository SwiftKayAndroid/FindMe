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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.swiftkaydevelopment.findme.data.User;
import com.swiftkaydevelopment.findme.utils.ImageLoader;

public class FullImageFragment extends BaseFragment{
    public static final String TAG = "FullImageFragment";

    private static final String ARG_USER = "ARG_USER";
    private static final String ARG_PIC = "ARG_PIC";

    private User user;
    private String picloc;
    private ImageView ivPicture;
    private ImageLoader imageLoader;

    public static FullImageFragment newInstance(String uid, User user) {
        FullImageFragment frag = new FullImageFragment();
        Bundle b = new Bundle();
        b.putSerializable(ARG_USER, user);
        b.putString(ARG_UID, uid);
        frag.setArguments(b);
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            uid = savedInstanceState.getString(ARG_UID);
            picloc = savedInstanceState.getString(ARG_PIC);
            user = (User) savedInstanceState.getSerializable(ARG_USER);
        } else {
            if (getArguments() != null) {
                uid = getArguments().getString(ARG_UID);
                user = (User) getArguments().getSerializable(ARG_USER);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = null;
        return layout;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        imageLoader.DisplayImage(picloc, ivPicture, true);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        imageLoader = new ImageLoader(context);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(ARG_USER, user);
        outState.putString(ARG_UID, uid);
        outState.putString(ARG_PIC, picloc);
        super.onSaveInstanceState(outState);
    }
}
