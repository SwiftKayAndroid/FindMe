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

import java.util.ArrayList;
import java.util.List;

public class LikedUsersFragment extends BaseMatchFragment {

    public static LikedUsersFragment newInstance(String uid) {
        LikedUsersFragment frag = new LikedUsersFragment();
        Bundle b = new Bundle();
        b.putString(ARG_UID, uid);
        frag.setArguments(b);
        return frag;
    }

    @Override
    public void getData() {
        MatchManager.getInstance(getActivity()).getLikedUsers(uid, "0");
    }

    @Override
    public MatchAdapter getAdapter() {
        return new MatchAdapter(getActivity(), users, MatchAdapter.TYPE_LIKED_USERS);
    }

    private int check(int[] A) {
        int lower = 0;
        int upper = 0;
        List<Integer> checks = new ArrayList<>();
        int length = A.length;
        for (int i = 0; i < length; i++) {
            int size = A.length - i;

            for (int e = i + 1; e < size; e++) {
                upper += e;
            }

            if (lower == upper) {
                checks.add(i);
            }
        }

        if (!checks.isEmpty()) {
            return checks.get(0);
        } else {
            return -1;
        }
    }
}
