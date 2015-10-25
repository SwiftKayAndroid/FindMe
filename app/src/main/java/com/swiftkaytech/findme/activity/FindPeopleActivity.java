package com.swiftkaytech.findme.activity;

import android.content.Context;
import com.swiftkaytech.findme.R;
import com.swiftkaytech.findme.fragment.FindPeopleFrag;

/**
 * Created by Kevin Haines on 10/20/15.
 */
public class FindPeopleActivity extends BaseActivity {


    private FindPeopleFrag mFindPeopleFragment;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_base;
    }

    @Override
    protected Context getContext() {
        return this;
    }

    @Override
    protected void createActivity() {
        mFindPeopleFragment = FindPeopleFrag.newInstance(uid);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.activityContainer, mFindPeopleFragment, "tag")
                .commit();
    }


}
