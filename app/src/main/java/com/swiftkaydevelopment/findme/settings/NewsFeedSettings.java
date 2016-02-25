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

package com.swiftkaydevelopment.findme.settings;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.swiftkaydevelopment.findme.R;
import com.swiftkaydevelopment.findme.data.NewsfeedPrefData;
import com.swiftkaydevelopment.findme.fragment.BaseFragment;
import com.swiftkaydevelopment.findme.managers.AccountManager;

public class NewsFeedSettings extends BaseFragment implements GenderSelectDialog.GenderSelectListener{
    public static final String TAG = "NewsFeedSettings";

    private TextView mTvGender, mTvOrientation, mTvRelationshipStatus;
    private TextView mTvAge;
    private SeekBar mDistanceSeek;
    private TextView mDistanceWriter;

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
        View layout = inflater.inflate(R.layout.newsfeed_settings, container, false);

        mToolbar = (Toolbar) layout.findViewById(R.id.newsfeedSettingsToolbar);
        setUpToolbar();

        mTvGender = (TextView) layout.findViewById(R.id.tvGender);
        mTvOrientation = (TextView) layout.findViewById(R.id.tvOrientation);
        mTvRelationshipStatus = (TextView) layout.findViewById(R.id.tvRelationship);
        mTvAge = (TextView) layout.findViewById(R.id.tvAge);

        mDistanceSeek = (SeekBar) layout.findViewById(R.id.seekNewsfeedSettingsDistance);
        mDistanceWriter = (TextView) layout.findViewById(R.id.newsfeedSettingsDistanceWriter);


        return layout;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        prefData = AccountManager.getInstance(getActivity()).getNewsfeedPreferences();

        mTvGender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GenderSelectDialog dialog = (GenderSelectDialog) getActivity().getSupportFragmentManager().findFragmentByTag(GenderSelectDialog.TAG);
                if (dialog == null) {
                    dialog = GenderSelectDialog.newInstance();
                    dialog.show(getActivity().getSupportFragmentManager(), GenderSelectDialog.TAG);
                    dialog.setListener(NewsFeedSettings.this);
                }
            }
        });


        mDistanceSeek.setProgress(Integer.parseInt(prefData.distance));
        mDistanceWriter.setText(Integer.toString(mDistanceSeek.getProgress()) + " Miles");

        mDistanceSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mDistanceWriter.setText(Integer.toString(progress) + " Miles");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(ARG_UID, uid);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        GenderSelectDialog dialog = (GenderSelectDialog) getActivity().getSupportFragmentManager().findFragmentByTag(GenderSelectDialog.TAG);
        if (dialog != null) {
            dialog.setListener(this);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        GenderSelectDialog dialog = (GenderSelectDialog) getActivity().getSupportFragmentManager().findFragmentByTag(GenderSelectDialog.TAG);
        if (dialog != null) {
            dialog.setListener(null);
        }
    }

    /**
     * Sets up the Toolbar for Connect Settings
     *
     */
    private void setUpToolbar() {
        mToolbar.setTitle("Filters");
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
        
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateSettings();
            }
        });
    }

    private void updateSettings() {
        String gender;
        String status = "both";
        String gay = "yes";
        String straight = "yes";
        String bi = "yes";
        String distance;

        gender = mTvGender.getText().toString().trim().toLowerCase();
//        status = mTvRelationshipStatus.getText().toString().trim();

        distance = Integer.toString(mDistanceSeek.getProgress());

        AccountManager.getInstance(getActivity()).updateNewsfeedSettings(status, uid, distance, gender, straight, gay, bi);
        Toast.makeText(getActivity(), "Settings Saved", Toast.LENGTH_SHORT).show();
        getActivity().getSupportFragmentManager().popBackStack();
    }

    @Override
    public void onGenderSelected(String gender) {
        mTvGender.setText(gender);
    }
}
