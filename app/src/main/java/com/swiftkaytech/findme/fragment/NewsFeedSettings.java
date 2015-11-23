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

package com.swiftkaytech.findme.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.Toast;

import com.swiftkaytech.findme.R;
import com.swiftkaytech.findme.data.NewsfeedPrefData;
import com.swiftkaytech.findme.managers.AccountManager;

public class NewsFeedSettings extends BaseFragment {
    public static final String TAG = "NewsFeedSettings";

    private static String mUid;

    private CheckBox mGenderBoth;
    private CheckBox mStatusBoth;

    private SwitchCompat mGenderSwitch;
    private SwitchCompat mStatusSwitch;

    private SwitchCompat mStraightSwitch;
    private SwitchCompat mGaySwitch;
    private SwitchCompat mBiSwitch;

    private SeekBar mDistanceSeek;

    private Toolbar mToolbar;

    private NewsfeedPrefData prefData;

    public static NewsFeedSettings newInstance (String uid) {
        NewsFeedSettings frag = new NewsFeedSettings();
        Bundle b = new Bundle();
        b.putString(ARG_UID, uid);
        frag.setArguments(b);
        return frag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            uid = savedInstanceState.getString(ARG_UID);
        } else {
            if (getArguments() != null) {
                uid = getArguments().getString(ARG_UID);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.newsfeed_settings, container, false);
        mToolbar = (Toolbar) layout.findViewById(R.id.newsfeedSettingsToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateSettings();
            }
        });

        mGenderBoth = (CheckBox) layout.findViewById(R.id.checkboxNewsfeedSettingsGenderBoth);
        mStatusBoth = (CheckBox) layout.findViewById(R.id.checkboxNewsfeedSettingsStatusBoth);

        mGenderSwitch = (SwitchCompat) layout.findViewById(R.id.switchNewsfeedSettingsGender);
        mStatusSwitch = (SwitchCompat) layout.findViewById(R.id.switchNewsfeedSettingsStatus);

        mStraightSwitch = (SwitchCompat) layout.findViewById(R.id.switchNewsfeedSettingsStraight);
        mGaySwitch = (SwitchCompat) layout.findViewById(R.id.switchNewsfeedSettingsGay);
        mBiSwitch = (SwitchCompat) layout.findViewById(R.id.switchNewsfeedSettingsBi);

        mDistanceSeek = (SeekBar) layout.findViewById(R.id.seekNewsfeedSettingsDistance);

        return layout;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        prefData = AccountManager.getInstance(getActivity()).getNewsfeedPreferences();
        if (prefData.gender.equals("both")) {
            mGenderBoth.setChecked(true);
        } else if (prefData.gender.equals("Male")) {
            mGenderBoth.setChecked(false);
            mGenderSwitch.setChecked(false);
        } else {
            mGenderBoth.setChecked(false);
            mGenderSwitch.setChecked(true);
        }

        if (prefData.straight.equals("yes")) {
            mStraightSwitch.setChecked(true);
        } else {
            mStraightSwitch.setChecked(false);
        }

        if (prefData.gay.equals("yes")) {
            mGaySwitch.setChecked(true);
        } else {
            mGaySwitch.setChecked(false);
        }

        if (prefData.bi.equals("yes")) {
            mBiSwitch.setChecked(true);
        } else {
            mBiSwitch.setChecked(false);
        }

        if (prefData.status.equals("both")) {
            mStatusBoth.setChecked(true);
        } else if (prefData.status.equals("single")) {
            mStatusBoth.setChecked(false);
            mStatusSwitch.setChecked(true);
        } else {
            mStatusBoth.setChecked(false);
            mStatusSwitch.setChecked(false);
        }

        mDistanceSeek.setProgress(Integer.parseInt(prefData.distance));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(ARG_UID, uid);
        super.onSaveInstanceState(outState);
    }

    private void updateSettings() {
        String gender;
        String status;
        String gay;
        String straight;
        String bi;
        String distance;

        if (mGenderBoth.isChecked()) {
            gender = "both";
        } else if (mGenderSwitch.isChecked()) {
            gender = "Female";
        } else {
            gender = "Male";
        }

        if (mStatusBoth.isChecked()) {
            status = "both";
        } else if (mStatusSwitch.isChecked()) {
            status= "single";
        } else {
            status = "taken";
        }

        if (mStraightSwitch.isChecked()) {
            straight = "yes";
        } else {
            straight = "no";
        }

        if (mGaySwitch.isChecked()) {
            gay = "yes";
        } else {
            gay = "no";
        }

        if (mBiSwitch.isChecked()) {
            bi = "yes";
        } else {
            bi = "no";
        }

        distance = Integer.toString(mDistanceSeek.getProgress());

        AccountManager.getInstance(getActivity()).updateNewsfeedSettings(status, uid, distance, gender, straight, gay, bi);
        Toast.makeText(getActivity(), "Settings Saved", Toast.LENGTH_SHORT).show();
        getActivity().getSupportFragmentManager().popBackStack();
    }
}
