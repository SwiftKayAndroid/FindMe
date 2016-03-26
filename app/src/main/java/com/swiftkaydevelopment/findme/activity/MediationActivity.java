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
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.swiftkaydevelopment.findme.R;
import com.swiftkaydevelopment.findme.data.AppConstants;
import com.swiftkaydevelopment.findme.fragment.MediateTermsFragment;
import com.swiftkaydevelopment.findme.fragment.PhotoMediationFragment;

public class MediationActivity extends BaseActivity {
    public static final String TAG = "MediationActivity";

    public static Intent createIntent(Context context) {
        return new Intent(context, MediationActivity.class);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_base;
    }

    @Override
    protected Context getContext() {
        return this;
    }

    @Override
    protected void createActivity(Bundle savedInstanceState) {

        mToolbar = (Toolbar) findViewById(R.id.baseActivityToolbar);
        mToolbar.setNavigationIcon(R.mipmap.ic_arrow_back_white_24dp);
        mToolbar.setTitle("Mediate Photos");
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if (!PreferenceManager.getDefaultSharedPreferences(this).getBoolean(AppConstants.PreferenceConstants.PREF_MEDIATE_TERMS, false)) {
            MediateTermsFragment fragment = new MediateTermsFragment();
            fragment.show(getSupportFragmentManager(), MediateTermsFragment.TAG);
        }

        if (getSupportFragmentManager().findFragmentByTag(PhotoMediationFragment.TAG) == null) {
            PhotoMediationFragment fragment = PhotoMediationFragment.newInstance(uid);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.activityContainer, fragment, PhotoMediationFragment.TAG)
                    .addToBackStack(null)
                    .commit();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
