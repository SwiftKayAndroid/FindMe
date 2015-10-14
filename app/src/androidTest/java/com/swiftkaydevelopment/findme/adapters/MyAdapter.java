package com.swiftkaydevelopment.findme.adapters;



import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

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