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

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.swiftkaydevelopment.findme.R;
import com.swiftkaydevelopment.findme.adapters.MediatePhotosAdapter;
import com.swiftkaydevelopment.findme.data.MediationPhoto;
import com.swiftkaydevelopment.findme.events.MediationPhotosRetrieved;
import com.swiftkaydevelopment.findme.managers.MediationManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class PhotoMediationFragment extends BaseFragment implements MediatePhotosAdapter.MediatePhotosAdapterListener {
    public static final String TAG = "PhotoMediationFragment";

    private RecyclerView mRecyclerView;
    private MediatePhotosAdapter mAdapter;

    private List<MediationPhoto> mItems = new ArrayList<>();

    public static PhotoMediationFragment newInstance(String uid) {
        PhotoMediationFragment frag = new PhotoMediationFragment();
        Bundle b = new Bundle();
        b.putString(ARG_UID, uid);
        frag.setArguments(b);
        return frag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.mediate_photos, container, false);
        mRecyclerView = (RecyclerView) layout.findViewById(R.id.recyclerView);

        return layout;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MediationManager.instance().getMediatedPhotos();

        if (mAdapter == null) {
            mAdapter = new MediatePhotosAdapter(mItems, this, getActivity());
        }

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onPhotoApproved(MediationPhoto photo) {
        photo.decision = "no";
        MediationManager.instance().setPhotoMediationDecision(photo);
    }

    @Override
    public void onPhotoDenied(MediationPhoto photo) {
        photo.decision = "rejected";
        MediationManager.instance().setPhotoMediationDecision(photo);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEvent(MediationPhotosRetrieved event) {
        EventBus.getDefault().removeStickyEvent(event);
        mAdapter.addItems(event.photos);
    }
}
