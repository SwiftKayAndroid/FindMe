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
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import com.swiftkaydevelopment.findme.R;
import com.swiftkaydevelopment.findme.adapters.MatchViewPagerAdapter;

public class MatchActivity extends BaseActivity{
    private static final String TAG = "MatchActivity";
    TabLayout tabs;
    private MatchViewPagerAdapter mAdapter;
    ViewPager mPager;

    public static Intent createIntent(Context context) {
        return new Intent(context, MatchActivity.class);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.match_activity;
    }

    @Override
    protected Context getContext() {
        return this;
    }

    @Override
    protected void createActivity(Bundle inState) {
        tabs = (TabLayout) findViewById(R.id.tabs);
        mPager = (ViewPager) findViewById(R.id.viewPagerMatches);

        if (mAdapter == null) {
            mAdapter = new MatchViewPagerAdapter(getSupportFragmentManager(), uid);
        }

        mPager.setAdapter(mAdapter);
        tabs.setupWithViewPager(mPager);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
