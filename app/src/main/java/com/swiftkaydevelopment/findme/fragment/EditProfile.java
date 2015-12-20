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
import android.widget.Toast;

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

    private RadioGroup rgHasKids;
    private RadioButton rbHavekids;
    private RadioButton rbNoKids;
    private RadioButton rbKidsPreferNoSay;

    private RadioGroup rgWantsKids;
    private RadioButton rbWantsKids;
    private RadioButton rbDoesntWantKids;
    private RadioButton rbWantsKidsPreferNoSay;

    private RadioGroup rgWeed;
    private RadioButton rbWeedOccasionally;
    private RadioButton rbWeedOften;
    private RadioButton rbWeedNever;
    private RadioButton rbWeedPreferNoSay;

    EditText etProfession;
    private EditText etSchool;

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

        rgHasKids = (RadioGroup) layout.findViewById(R.id.editProfileRadioGroupHasKids);
        rbHavekids = (RadioButton) layout.findViewById(R.id.rbEditProfileHaveKids);
        rbNoKids = (RadioButton) layout.findViewById(R.id.rbEditProfileNoKids);
        rbKidsPreferNoSay = (RadioButton) layout.findViewById(R.id.rbEditProfileKidsPreferNoSay);

        rgWantsKids = (RadioGroup) layout.findViewById(R.id.editProfileRadioGroupWantsKids);
        rbWantsKids = (RadioButton) layout.findViewById(R.id.rbEditProfileSomeday);
        rbDoesntWantKids = (RadioButton) layout.findViewById(R.id.rbEditProfileDoesntWant);
        rbWantsKidsPreferNoSay = (RadioButton) layout.findViewById(R.id.rbEditProfileWantsKidsPreferNoSay);

        rgWeed = (RadioGroup) layout.findViewById(R.id.editProfileRadioWeed);
        rbWeedOccasionally = (RadioButton) layout.findViewById(R.id.rbEditProfileWeedSocially);
        rbWeedNever = (RadioButton) layout.findViewById(R.id.rbEditProfileWeedNever);
        rbWeedOften = (RadioButton) layout.findViewById(R.id.rbEditProfileWeedOften);
        rbWeedPreferNoSay = (RadioButton) layout.findViewById(R.id.rbEditProfileWeedPreferNoSay);

        etProfession = (EditText) layout.findViewById(R.id.editProfileProfession);
        etSchool = (EditText) layout.findViewById(R.id.editProfileSchool);

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
                Toast.makeText(getActivity(), "Profile Updated", Toast.LENGTH_SHORT).show();
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        return layout;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etAboutme.setText(user.getAboutMe());

        //Set orientation
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

        etProfession.setText(user.profession);
        etSchool.setText(user.school);

        if (user.mRelationshipStatus.equals(rbSingle.getText().toString())) {
            rbSingle.setChecked(true);
        } else {
            rbTaken.setChecked(true);
        }

        if (user.hasKids.equals(rbHavekids.getText().toString())) {
            rbHavekids.setChecked(true);
        } else if (user.hasKids.equals(rbNoKids.getText().toString())) {
            rbNoKids.setChecked(true);
        } else {
            rbKidsPreferNoSay.setChecked(true);
        }

        if (user.wantsKids.equals(rbWantsKids.getText().toString())) {
            rbWantsKids.setChecked(true);
        } else if (user.wantsKids.equals(rbDoesntWantKids.getText().toString())) {
            rbDoesntWantKids.setChecked(true);
        } else {
            rbWantsKidsPreferNoSay.setChecked(true);
        }

        if (user.weed.equals(rbWeedOccasionally.getText().toString())) {
            rbWeedOccasionally.setChecked(true);
        } else if (user.weed.equals(rbWeedOften.getText().toString())) {
            rbWeedOften.setChecked(true);
        } else if (user.weed.equals(rbWeedNever.getText().toString())) {
            rbWeedNever.setChecked(true);
        } else {
            rbWeedPreferNoSay.setChecked(true);
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

        String haskids = ((RadioButton) getView().findViewById(rgHasKids.getCheckedRadioButtonId())).getText().toString();
        String wantskids = ((RadioButton) getView().findViewById(rgWantsKids.getCheckedRadioButtonId())).getText().toString();
        String weed = ((RadioButton) getView().findViewById(rgWeed.getCheckedRadioButtonId())).getText().toString();
        String profession = etProfession.getText().toString();
        String school = etSchool.getText().toString();

        user.setAboutMe(about);
        user.setOrientation(User.setOrientationFromString(orientation));
        user.mRelationshipStatus = status;
        user.profession = profession;
        user.school = school;
        user.weed = weed;
        user.hasKids = haskids;
        user.wantsKids = wantskids;

        UserManager.getInstance(uid, getActivity()).updateProfile(about, orientation, status, haskids, wantskids, weed, profession, school);
    }
}
