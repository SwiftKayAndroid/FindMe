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

package com.swiftkaytech.findme.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.swiftkaytech.findme.R;

public class FriendsActivity extends BaseActivity{
    private static final String TAG = "FriendsActivity";
    private static final String ARG_UID = "ARG_UID";

    public static Intent createIntent(Context context, String uid) {
        Intent i = new Intent(context, FriendsActivity.class);
        i.putExtra(ARG_UID, uid);
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
    protected void createActivity(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            uid = savedInstanceState.getString(ARG_UID);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(ARG_UID, uid);
        super.onSaveInstanceState(outState);
    }
}
