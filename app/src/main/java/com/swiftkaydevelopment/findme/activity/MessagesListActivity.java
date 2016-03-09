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
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.swiftkaydevelopment.findme.fragment.MessagesListFrag;
import com.swiftkaydevelopment.findme.R;
import com.swiftkaydevelopment.findme.managers.MessagesManager;

public class MessagesListActivity extends BaseActivity {
    private static final String TAG = "MessagesListActivity";

    private MessagesListFrag mMessagesListFrag;

    public static Intent createIntent(Context context) {
        Intent i = new Intent(context, MessagesListActivity.class);
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

        if (getSupportFragmentManager().findFragmentByTag(MessagesListFrag.TAG) == null) {
            mMessagesListFrag = MessagesListFrag.getInstance(uid);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.activityContainer, mMessagesListFrag, MessagesListFrag.TAG)
                    .addToBackStack(null)
                    .commit();
        }
        setUpToolbar();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    /**
     * Sets up the toolbar for this Activity
     *
     */
    private void setUpToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.baseActivityToolbar);
        mToolbar.setNavigationIcon(R.mipmap.ic_arrow_back_white_24dp);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mToolbar.inflateMenu(R.menu.messages_menu);
        mToolbar.setTitle("Messages");
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.messagesThreadDeleteAllMessages) {
                    MessagesManager.getInstance(uid).deleteAllThreads(uid);
                }
                return true;
            }
        });
    }
}
