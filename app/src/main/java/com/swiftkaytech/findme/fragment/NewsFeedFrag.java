package com.swiftkaytech.findme.fragment;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import com.swiftkaytech.findme.R;
import com.swiftkaytech.findme.adapters.PostAdapter;
import com.swiftkaytech.findme.data.Post;
import com.swiftkaytech.findme.managers.PostManager;
import com.swiftkaytech.findme.utils.VarHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kevin haines on 2/8/2015.
 */
public class NewsFeedFrag extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener,View.OnClickListener{

    public static final String TAG = "NewsFeedFrag";
    public static final String ARG_POSTS_LIST = "ARG_POSTS_LIST";
    private String lp = "0";
    private ProgressBar         pb;
    private View                fab;
    private View                fabstatus;
    private View                fabphoto;
    private RecyclerView        mRecyclerView;
    private SwipeRefreshLayout  swipeLayout;
    private ArrayList<Post> mPostsList;

    private PostAdapter mPostAdapter;
    private static NewsFeedFrag newsFeedFrag = null;

    public NewsFeedFrag(){

    }

    public static NewsFeedFrag getInstance(String uid){
        if (newsFeedFrag == null) {
            newsFeedFrag = new NewsFeedFrag();
            Bundle b = new Bundle();
            b.putString(ARG_UID, uid);
            newsFeedFrag.setArguments(b);
        }
        return newsFeedFrag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            uid = savedInstanceState.getString(ARG_UID);
            mPostsList = (ArrayList<Post>) savedInstanceState.getSerializable(ARG_POSTS_LIST);
        } else {
            if (getArguments() != null) {
                uid = getArguments().getString(ARG_UID);
            }
            mPostsList = PostManager.getInstance(uid, getActivity()).getPosts(getActivity());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.newsfeedfrag, container, false);

        fab = layout.findViewById(R.id.fab);
        fabstatus = layout.findViewById(R.id.fabpencil);
        fabphoto = layout.findViewById(R.id.fabcamera);
        mRecyclerView = (RecyclerView) layout.findViewById(R.id.recyclerViewNewsFeed);
        pb = (ProgressBar) layout.findViewById(R.id.pbnewsfeed);

        swipeLayout = (SwipeRefreshLayout) layout.findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.black,
                android.R.color.holo_blue_bright,
                android.R.color.black);

        fab.setOnClickListener(this);
        fabstatus.setOnClickListener(this);
        fabphoto.setOnClickListener(this);

        return layout;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState != null) {
            uid = savedInstanceState.getString(ARG_UID);
            mPostsList = (ArrayList<Post>) savedInstanceState.getSerializable(ARG_POSTS_LIST);
        } else {
            if (getArguments() != null) {
                uid = getArguments().getString(ARG_UID);
            }
            mPostsList = PostManager.getInstance(uid, getActivity()).getPosts(getActivity());
        }
        Log.d(VarHolder.TAG, "view is created newsfeedfrag");

        if (uid != null) {

            if (mPostAdapter == null) {
                mPostAdapter = new PostAdapter(getActivity(), mPostsList);
            }

            pb.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            mRecyclerView.setAdapter(mPostAdapter);
            Snackbar.make(view, Integer.toString(mPostsList.size()), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(VarHolder.TAG, "pausing newsfeedfrag");
    }

    @Override
    public void onResume() {
        Log.d(VarHolder.TAG, "onResume of newsfeedfrag");
        super.onResume();

        if (fab.getRotation() > 0) {
            rotate(fab);
        }
        fabstatus.setVisibility(View.INVISIBLE);
        fabphoto.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(ARG_UID, uid);
        outState.putSerializable(ARG_POSTS_LIST, mPostsList);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRefresh() {
        swipeLayout.setRefreshing(true);
        PostManager.getInstance(uid, getActivity()).clearPosts();
        mPostsList.clear();
        mPostsList = PostManager.getInstance(uid, getActivity()).getPosts(getActivity());
        mPostAdapter.clearAdapter();
        mPostAdapter.addPosts(mPostsList);
        swipeLayout.setRefreshing(false);
    }

    private void rotate(View v) {
        AnimatorSet set = new AnimatorSet();
        float startRotation = v.getRotation();
        float endRotation = 0;
        if (startRotation == 0) {
            endRotation = 45;
        }
        set.play(ObjectAnimator.ofFloat(v, View.ROTATION, startRotation, endRotation));
        set.setDuration(200);
        set.start();
    }

    /**
     * Animates the fabstatus and fabphoto when the base fab
     * is clicked
     */
    private void fabClickAnimation() {
        float startRotation = fab.getRotation();
        float endRotation = 0;
        if (startRotation == 0) {
            endRotation = 45;
        }

        AnimatorSet set = new AnimatorSet();
        ObjectAnimator outAnimFabStatus = ObjectAnimator.ofFloat(fabstatus, View.Y, fab.getTop(), fabstatus.getTop());
        ObjectAnimator outAnimFabPhoto = ObjectAnimator.ofFloat(fabphoto, View.X, fab.getLeft(), fabphoto.getLeft());
        ObjectAnimator inAnimFabStatus = ObjectAnimator.ofFloat(fabstatus, View.Y, fabphoto.getTop(), fab.getTop());
        ObjectAnimator inAnimFabPhoto = ObjectAnimator.ofFloat(fabphoto, View.X, fabphoto.getLeft(), fab.getLeft());
        set.play(outAnimFabStatus)
                .with(outAnimFabPhoto)
                .with(ObjectAnimator.ofFloat(fab, View.ROTATION, startRotation, endRotation));
        set.setDuration(500);
        set.start();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.fabcamera) {
            Intent i = new Intent("com.swiftkaytech.findme.UPLOADSERVICE");
            startActivity(i);
        } else if (v.getId() == R.id.fabpencil) {
            Intent i = new Intent("com.swiftkaytech.findme.UPDATESTATUS");
            startActivity(i);
        } else if (v.getId() == R.id.fab) {
            if (fabstatus.getVisibility() == View.INVISIBLE) {
                fabstatus.setVisibility(View.VISIBLE);
                fabphoto.setVisibility(View.VISIBLE);
                fabClickAnimation();
            } else {
                fabstatus.setVisibility(View.INVISIBLE);
                fabphoto.setVisibility(View.INVISIBLE);
                rotate(v);
            }
        }
    }
}