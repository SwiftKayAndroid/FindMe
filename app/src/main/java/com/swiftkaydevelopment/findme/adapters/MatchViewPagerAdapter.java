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

package com.swiftkaydevelopment.findme.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.swiftkaydevelopment.findme.fragment.Match.MatchPreviewFragment;
import com.swiftkaydevelopment.findme.fragment.Match.MyMatches;

public class MatchViewPagerAdapter extends FragmentPagerAdapter{
    private static final String TAG = "MatchViewPagerAdapter";
    private static final int MAX_PAGES = 2;
    MyMatches mMyMatches;
    MatchPreviewFragment matchPreviewFragment;
    private static String mUid;
    FragmentManager fm;

    public MatchViewPagerAdapter(FragmentManager fm, String uid) {
        super(fm);
        mUid = uid;
        this.fm = fm;
    }

    @Override
    public Fragment getItem(int position) {

        if (position == 0) {
            matchPreviewFragment = (MatchPreviewFragment) fm.findFragmentByTag(MatchPreviewFragment.TAG);
            if (matchPreviewFragment == null) {
                matchPreviewFragment = MatchPreviewFragment.newInstance(mUid);
            }
            return matchPreviewFragment;
        } else if (position == 1){
            mMyMatches = (MyMatches) fm.findFragmentByTag(MyMatches.TAG);
            if (mMyMatches == null) {
                mMyMatches = MyMatches.newInstance(mUid);
            }
            return mMyMatches;
        }
        return null;
    }

    @Override
    public int getCount() {
        return MAX_PAGES;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return "Connect";
        }
        return "Matches";
    }

}
