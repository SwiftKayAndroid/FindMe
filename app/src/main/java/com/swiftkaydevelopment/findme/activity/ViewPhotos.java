package com.swiftkaydevelopment.findme.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.swiftkaydevelopment.findme.data.User;
import com.swiftkaydevelopment.findme.fragment.ViewPhotosFrag;
import com.swiftkaydevelopment.findme.R;

/**
 * Created by Kevin Haines on 9/14/15.
 */
public class ViewPhotos extends BaseActivity {
    public static final String TAG = "ViewPhotosActivity";
    private static final String ARG_USER = "ARG_USER";

    private User user;

    public static Intent createIntent(Context context, User user) {
        Intent i = new Intent(context, ViewPhotos.class);
        i.putExtra(ARG_USER, user);
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
        mToolbar.setNavigationIcon(R.mipmap.ic_arrow_back_white_24dp);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if (getIntent().getExtras() != null) {
            user = (User) getIntent().getExtras().getSerializable(ARG_USER);
        }

        if (getSupportFragmentManager().findFragmentByTag(ViewPhotosFrag.TAG) == null) {
            ViewPhotosFrag viewPhotosFrag = ViewPhotosFrag.newInstance(uid, user);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.activityContainer, viewPhotosFrag, ViewPhotosFrag.TAG)
                    .addToBackStack(null)
                    .commit();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
