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
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.swiftkaydevelopment.findme.data.User;
import com.swiftkaydevelopment.findme.managers.UserManager;
import com.swiftkaydevelopment.findme.R;

public class EditProfile extends BaseFragment{
    public static final String TAG = "EditProfile";
    private static final String ARG_USER = "ARG_USER";

    private User user;

    private EditText etAboutme;
    private Toolbar mToolbar;

    private RadioGroup rgOrientation;
    private RadioButton rbStraight;
    private RadioButton rbGay;
    private RadioButton rbBi;

    private RadioGroup rgStatus;
    private RadioButton rbSingle;
    private RadioButton rbTaken;

    public static EditProfile newInstance(String uid, User user) {
        EditProfile frag = new EditProfile();
        Bundle b = new Bundle();
        b.putString(ARG_UID, uid);
        b.putSerializable(ARG_USER, user);
        frag.setArguments(b);
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            uid = savedInstanceState.getString(ARG_UID);
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
        View layout = inflater.inflate(R.layout.edit_profile, container, false);
        etAboutme = (EditText) layout.findViewById(R.id.editProfileAboutMe);
        rgOrientation = (RadioGroup) layout.findViewById(R.id.editProfileRadioGroupOrientation);
        rbStraight = (RadioButton) layout.findViewById(R.id.rbEditProfileStraight);
        rbGay = (RadioButton) layout.findViewById(R.id.rbEditProfileGay);
        rbBi = (RadioButton) layout.findViewById(R.id.rbEditProfileBi);

        rgStatus = (RadioGroup) layout.findViewById(R.id.editProfileRadioGroupRelationshipStatus);
        rbSingle = (RadioButton) layout.findViewById(R.id.rbEditProfileSingle);
        rbTaken = (RadioButton) layout.findViewById(R.id.rbEditProfileTaken);

        mToolbar = (Toolbar) layout.findViewById(R.id.editProfileToolbar);
        mToolbar.setNavigationIcon(R.mipmap.ic_arrow_back_white_24dp);
        mToolbar.inflateMenu(R.menu.newsfeed_settings_menu);
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.newsfeedSettingsDiscard) {
                    getActivity().getSupportFragmentManager().popBackStack();
                    return true;
                }
                return false;
            }
        });
        mToolbar.setTitle("Edit Profile");
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProfile();
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        return layout;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etAboutme.setText(user.getAboutMe());

        switch (user.getOrientation()) {
            case STRAIGHT:
                rbStraight.setChecked(true);
                break;
            case GAY: rbGay.setChecked(true);
                break;
            case BISEXUAL: rbBi.setChecked(true);
                break;
            default:
                rbStraight.setChecked(true);
                break;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(ARG_UID, uid);
        super.onSaveInstanceState(outState);
    }

    private void saveProfile() {
        String about = etAboutme.getText().toString();
        int id = rgOrientation.getCheckedRadioButtonId();
        String orientation;
        if (id == rbStraight.getId()) {
            orientation = rbStraight.getText().toString();
        } else if (id == rbGay.getId()) {
            orientation = rbGay.getText().toString();
        } else if (id == rbBi.getId()) {
            orientation = "Bisexual";
        } else {
            orientation = "Straight";
        }

        int statusId = rgStatus.getCheckedRadioButtonId();
        String status = "";
        if (statusId == rbSingle.getId()) {
            status = "Single";
        } else if (statusId == rbTaken.getId()) {
            status = "Taken";
        }

        user.setAboutMe(about);
        user.setOrientation(User.setOrientationFromString(orientation));
        user.mRelationshipStatus = status;
        UserManager.getInstance(uid, getActivity()).updateProfile(about, orientation, status);
    }
}
