package com.swiftkaytech.findme.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

import com.swiftkaytech.findme.R;
import com.swiftkaytech.findme.fragment.FindPeopleFrag;

/**
 * Created by Kevin Haines on 10/20/15.
 */
public class FindPeopleActivity extends BaseActivity {

    private FindPeopleFrag mFindPeopleFragment;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public static Intent createIntent(Context context) {
        Intent i = new Intent(context, FindPeopleActivity.class);
        return i;
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
    protected void createActivity(Bundle inState) {

        mToolbar = (Toolbar) findViewById(R.id.baseActivityToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        mFindPeopleFragment = FindPeopleFrag.newInstance(uid);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.activityContainer, mFindPeopleFragment, "tag")
                .commit();
    }
}
