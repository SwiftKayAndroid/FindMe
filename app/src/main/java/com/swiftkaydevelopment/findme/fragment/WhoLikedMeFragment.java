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

package com.swiftkaydevelopment.findme.fragment;

import com.swiftkaydevelopment.findme.adapters.MatchAdapter;
import com.swiftkaydevelopment.findme.fragment.Match.BaseMatchFragment;
import com.swiftkaydevelopment.findme.managers.MatchManager;

public class WhoLikedMeFragment extends BaseMatchFragment{
    public static final String TAG = "WhoLikedMeFragment";

    @Override
    public void getData() {
        MatchManager.getInstance(getActivity()).getWhoLikedMe(uid, "0");
    }

    @Override
    public MatchAdapter getAdapter() {
        return new MatchAdapter(getActivity(), users, MatchAdapter.TYPE_LIKED_ME);
    }
}
