package com.swiftkaytech.findme.adapters;



import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.swiftkaytech.findme.fragment.FriendRequestsFrag;
import com.swiftkaytech.findme.fragment.FriendsListFrag;

public class MyAdapter extends FragmentPagerAdapter {

        public MyAdapter(FragmentManager fm) {
            super(fm);

        }

        @Override
        public int getCount() {

            return 2;
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return new FriendRequestsFrag();

            }else{
                return new FriendsListFrag();
            }


        }
    }