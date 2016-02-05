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

package com.swiftkaydevelopment.findme.fragment.Match;

import android.os.Bundle;

import com.swiftkaydevelopment.findme.adapters.MatchAdapter;
import com.swiftkaydevelopment.findme.managers.MatchManager;

public class MyMatches extends BaseMatchFragment {
    public static final String TAG = "MyMatches";

    public static MyMatches newInstance(String uid) {
        MyMatches frag = new MyMatches();
        Bundle b = new Bundle();
        b.putString(ARG_UID, uid);
        frag.setArguments(b);
        return frag;
    }

    @Override
    public void getData() {
        mMatchType = MatchAdapter.TYPE_MATCHES;
        MatchManager.getInstance(getActivity()).getMatches(uid, "0");
    }

    @Override
    public MatchAdapter getAdapter() {
        return new MatchAdapter(getActivity(), users, MatchAdapter.TYPE_MATCHES);
    }
}
