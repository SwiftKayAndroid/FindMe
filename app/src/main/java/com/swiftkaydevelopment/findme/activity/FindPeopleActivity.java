package com.swiftkaydevelopment.findme.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.swiftkaydevelopment.findme.fragment.FindPeopleFrag;
import com.swiftkaydevelopment.findme.fragment.NewsFeedSettings;
import com.swiftkaydevelopment.findme.R;

/**
 * Created by Kevin Haines on 10/20/15.
 */
public class FindPeopleActivity extends BaseActivity {

    private FindPeopleFrag mFindPeopleFragment;

    public static Intent createIntent(Context context) {
        Intent i = new Intent(context, FindPeopleActivity.class);
        return i;
    }

    @Override
    public void onBackPressed() {
        finish();
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
        mToolbar.inflateMenu(R.menu.main);
        mToolbar.setTitle("Connect");
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.menusettingsicon) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(android.R.id.content, NewsFeedSettings.newInstance(uid), NewsFeedSettings.TAG)
                            .addToBackStack(null)
                            .commit();
                }
                return false;
            }
        });
        mToolbar.setNavigationIcon(R.mipmap.ic_arrow_back_white_24dp);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        FindPeopleFrag findPeopleFrag = (FindPeopleFrag) getSupportFragmentManager().findFragmentByTag(FindPeopleFrag.TAG);
        if (findPeopleFrag == null) {
            mFindPeopleFragment = FindPeopleFrag.newInstance(uid);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.activityContainer, mFindPeopleFragment, "tag")
                    .addToBackStack(null)
                    .commit();
        }
    }
}
