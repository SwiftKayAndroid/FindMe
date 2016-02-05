package com.swiftkaydevelopment.findme.fragment;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.swiftkaydevelopment.findme.R;
import com.swiftkaydevelopment.findme.adapters.PicturesAdapter;
import com.swiftkaydevelopment.findme.data.Post;
import com.swiftkaydevelopment.findme.data.User;

import java.util.ArrayList;

/**
 * Created by Kevin Haines on 8/24/15.
 */
public class ViewPhotosFrag extends BaseFragment implements PicturesAdapter.PicturesAdapterListener, User.UserListener, FullImageFragment.FullImageFragListener {
    public static final String TAG = "ViewPhotosFrag";
    private static final String ARG_USER = "ARG_USER";
    private static final String ARG_PICS = "ARG_PICS";

    private User user;
    private ArrayList<Post> posts =  new ArrayList<>();

    private RecyclerView mRecyclerView;
    private PicturesAdapter mAdapter;

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
            posts = (ArrayList) savedInstanceState.getSerializable(ARG_PICS);
        } else {
            if (getArguments() != null) {
                uid = getArguments().getString(ARG_UID);
                user = (User) getArguments().getSerializable(ARG_USER);
            }
            if (user != null) {
                user.getPictures();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.viewpicturesfrag,container,false);
        mRecyclerView = (RecyclerView) layout.findViewById(R.id.recyclerViewViewPhotos);

        return layout;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState != null) {
            posts = (ArrayList) savedInstanceState.getSerializable(ARG_PICS);
        }

        if (mAdapter == null) {
            mAdapter = new PicturesAdapter(posts, getActivity());
        }

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(ARG_UID, uid);
        outState.putSerializable(ARG_USER, user);
        outState.putSerializable(ARG_PICS, posts);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        user.addListener(this);
        FullImageFragment fullImageFragment = (FullImageFragment) getActivity().getSupportFragmentManager().findFragmentByTag(FullImageFragment.TAG);
        if (fullImageFragment != null) {
            fullImageFragment.setFullImageFragListener(this);
        }
        if (mAdapter != null) {
            mAdapter.setPicturesAdapterListener(this);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        user.removeListener(this);
        FullImageFragment fullImageFragment = (FullImageFragment) getActivity().getSupportFragmentManager().findFragmentByTag(FullImageFragment.TAG);
        if (fullImageFragment != null) {
            fullImageFragment.setFullImageFragListener(null);
        }
        if (mAdapter != null) {
            mAdapter.setPicturesAdapterListener(null);
        }
    }

    @Override
    public void onPicturesFetched(ArrayList<Post> posts) {
        Log.w(TAG, "onPicturesFetched");
        mAdapter.addPictures(posts);

    }

    @Override
    public void onImageDeleted(Post post) {
        mAdapter.removePicture(post);
    }

    @Override
    public void onImageClicked(Post post) {
        FullImageFragment fullImageFragment = FullImageFragment.newInstance(uid, post, user.getOuid().equals(uid));
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, fullImageFragment, FullImageFragment.TAG)
                .addToBackStack(null)
                .commit();
        fullImageFragment.setFullImageFragListener(this);
    }
}
