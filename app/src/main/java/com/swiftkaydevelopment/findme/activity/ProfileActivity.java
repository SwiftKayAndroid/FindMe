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

package com.swiftkaydevelopment.findme.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.swiftkaydevelopment.findme.data.User;
import com.swiftkaydevelopment.findme.fragment.ProfileFragment;
import com.swiftkaydevelopment.findme.R;

public class ProfileActivity extends BaseActivity {
    private static final String TAG = "ProfileActivity";
    private static final String ARG_USER = "ARG_USER";

    private User mUser;

    //instance grabber
    public static Intent createIntent(Context context, User user) {
        Intent i = new Intent(context, ProfileActivity.class);
        i.putExtra(ARG_USER, user);
        return i;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_profile;
    }

    @Override
    protected Context getContext() {
        return this;
    }

    @Override
    protected void createActivity(Bundle inState) {

        if (inState != null) {
            mUser = (User) inState.getSerializable(ARG_USER);
        } else {
            mUser = (User) getIntent().getExtras().getSerializable(ARG_USER);
        }

        ProfileFragment profileFragment = (ProfileFragment) getSupportFragmentManager().findFragmentByTag(ProfileFragment.TAG);
        if (profileFragment == null) {
            profileFragment = ProfileFragment.newInstance(mUser, uid);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.activityContainer, profileFragment, ProfileFragment.TAG);
            ft.commit();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(ARG_USER, mUser);
        super.onSaveInstanceState(outState);
    }
}
