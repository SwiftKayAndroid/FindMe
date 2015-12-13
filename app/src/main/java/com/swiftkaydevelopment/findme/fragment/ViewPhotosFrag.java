package com.swiftkaydevelopment.findme.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.swiftkaydevelopment.findme.data.User;
import com.swiftkaydevelopment.findme.utils.ImageLoader;
import com.swiftkaydevelopment.findme.R;

import org.apmem.tools.layouts.FlowLayout;

import java.util.ArrayList;

/**
 * Created by Kevin Haines on 8/24/15.
 */
public class ViewPhotosFrag extends BaseFragment implements  View.OnClickListener, User.UserListener {
    public static final String TAG = "ViewPhotosFrag";
    private static final String ARG_USER = "ARG_USER";
    private static final String ARG_PICS = "ARG_PICS";

    private User user;
    private ArrayList<String> urls;

    private FlowLayout mFlowLayout;
    private ImageLoader imageLoader;

    public static ViewPhotosFrag newInstance(String uid, User user) {
        ViewPhotosFrag frag = new ViewPhotosFrag();
        Bundle b = new Bundle();
        b.putString(ARG_UID, uid);
        b.putSerializable(ARG_USER, user);
        frag.setArguments(b);
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            user = (User) savedInstanceState.getSerializable(ARG_USER);
            uid = savedInstanceState.getString(ARG_UID);
            urls = savedInstanceState.getStringArrayList(ARG_PICS);
        } else {
            if (getArguments() != null) {
                uid = getArguments().getString(ARG_UID);
                user = (User) getArguments().getSerializable(ARG_USER);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.viewpicturesfrag,container,false);
        mFlowLayout = (FlowLayout) layout.findViewById(R.id.flowLayoutViewPhotos);

        return layout;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState != null) {
            urls = savedInstanceState.getStringArrayList(ARG_PICS);
        }

        if (imageLoader == null) {
            imageLoader = new ImageLoader(getActivity());
        }
        if (urls == null) {
            user.getPictures();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(ARG_UID, uid);
        outState.putSerializable(ARG_USER, user);
        outState.putStringArrayList(ARG_PICS, urls);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        user.addListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        user.removeListener(this);
    }

    @Override
    public void onPicturesFetched(ArrayList<String> urls) {
        Log.w(TAG, "onPicturesFetched");
        if (this.urls == null) {
           this.urls = new ArrayList<>();
        }
        this.urls.addAll(urls);
        reinitializeViews();
    }

    private void reinitializeViews() {
        mFlowLayout.removeAllViews();

        for (String url : urls) {
            addPictureView(url);
        }
    }

    public void addPictureView(String url) {
        ImageView iv = (ImageView) ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.sgvitem, null);
        imageLoader.DisplayImage(url, iv, true);
        iv.setTag(url);
        iv.setOnClickListener(this);
        mFlowLayout.addView(iv);
    }

    @Override
    public void onClick(View v) {
        String url = (String) v.getTag();
        FullImageFragment fullImageFragment = FullImageFragment.newInstance(uid, url, user.getOuid().equals(uid));
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, fullImageFragment, FullImageFragment.TAG)
                .addToBackStack(null)
                .commit();
    }
}
