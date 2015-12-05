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

import com.swiftkaydevelopment.findme.fragment.FriendRequestsFrag;
import com.swiftkaydevelopment.findme.fragment.FriendsFrag;

public class FriendsPagerAdapter extends FragmentPagerAdapter {
    private static final String TAG = "FriendsPagerAdapter";
    private static final int MAX_PAGES = 2;
    FriendsFrag friendsFrag;
    FriendRequestsFrag friendRequestsFrag;
    private static String mUid;
    FragmentManager fm;

    public FriendsPagerAdapter(FragmentManager fm, String uid) {
        super(fm);
        mUid = uid;
        this.fm = fm;
    }

    @Override
    public Fragment getItem(int position) {

        if (position == 0) {
            friendRequestsFrag = (FriendRequestsFrag) fm.findFragmentByTag(FriendRequestsFrag.TAG);
            if (friendRequestsFrag == null) {
                friendRequestsFrag = FriendRequestsFrag.newInstance(mUid);
            }
            return friendRequestsFrag;
        } else {
            friendsFrag = (FriendsFrag) fm.findFragmentByTag(FriendsFrag.TAG);
            if (friendsFrag == null) {
                friendsFrag = FriendsFrag.newInstance(mUid);
            }
            return friendsFrag;
        }
    }

    @Override
    public int getCount() {
        return MAX_PAGES;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return "Requests";
        }
        return "Friends";
    }
}
