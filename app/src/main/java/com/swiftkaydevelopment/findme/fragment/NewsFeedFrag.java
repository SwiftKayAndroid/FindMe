package com.swiftkaydevelopment.findme.fragment;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.swiftkaydevelopment.findme.adapters.PostAdapter;
import com.swiftkaydevelopment.findme.data.Post;
import com.swiftkaydevelopment.findme.managers.PostManager;
import com.swiftkaydevelopment.findme.managers.UserManager;
import com.swiftkaydevelopment.findme.R;

import java.util.ArrayList;

/**
 * Created by kevin haines on 2/8/2015.
 */
public class NewsFeedFrag extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener,View.OnClickListener,
        PostManager.PostsListener, PostAdapter.PostAdapterListener {

    public static final String TAG = "NewsFeedFrag";
    public static final String ARG_POSTS_LIST = "ARG_POSTS_LIST";

    private String lp = "0";
    private ProgressBar         pb;
    private View                fab;
    private View                fabstatus;
    private View                fabphoto;
    private RecyclerView        mRecyclerView;
    private SwipeRefreshLayout  swipeLayout;
    private boolean             loadingMore;
    private ArrayList<Post>     mPostsList = new ArrayList<>();

    private PostAdapter mPostAdapter;

    public static NewsFeedFrag getInstance(String uid){
        NewsFeedFrag newsFeedFrag = new NewsFeedFrag();
        Bundle b = new Bundle();
        b.putString(ARG_UID, uid);
        newsFeedFrag.setArguments(b);
        return newsFeedFrag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            uid = savedInstanceState.getString(ARG_UID);
            mPostsList = (ArrayList<Post>) savedInstanceState.getSerializable(ARG_POSTS_LIST);
        } else {
            if (getArguments() != null) {
                uid = getArguments().getString(ARG_UID);
            }
            PostManager.getInstance(uid, getActivity()).fetchPosts(getActivity(), "0");
            loadingMore = true;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");
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
        Log.i(TAG, "onViewCreated");
        super.onViewCreated(view, savedInstanceState);

        if (mPostAdapter == null) {
            mPostAdapter = new PostAdapter(getActivity(), mPostsList, UserManager.getInstance(uid, getActivity()).me(), false);
            mRecyclerView.setAdapter(mPostAdapter);
            mPostAdapter.setPostAdapterListener(this);
        }

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        pb.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int totalItemCount = mRecyclerView.getLayoutManager().getItemCount();
                int lastVisibleItem = ((LinearLayoutManager) mRecyclerView.getLayoutManager()).findLastVisibleItemPosition();
                Log.i(TAG, "total: " + totalItemCount + " last: " + lastVisibleItem);
                if (!loadingMore && totalItemCount <= lastVisibleItem + 1) {
                    loadingMore = true;
                    loadMorePosts();
                }
            }
        });
    }

    @Override
    public void onPause() {
        Log.i(TAG, "onPause");
        super.onPause();
        PostManager.getInstance(uid, getActivity()).removeListener(this);
        if (mPostAdapter != null) {
            mPostAdapter.setPostAdapterListener(null);
        }
    }

    @Override
    public void onResume() {
        Log.i(TAG, "onResume");
        super.onResume();
        PostManager.getInstance(uid, getActivity()).addPostListener(this);
        if (mPostAdapter != null) {
            mPostAdapter.setPostAdapterListener(this);
        }
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
        Log.i(TAG, "onRefresh");
        swipeLayout.setRefreshing(true);
        PostManager.getInstance(uid, getActivity()).refreshPosts(getActivity());
    }

    private void loadMorePosts() {
        Log.i(TAG, "loading more posts");
        if (mPostAdapter.getPosts().size() > 25) {
            String lastpost = mPostAdapter.getPosts().get(mPostAdapter.getPosts().size() - 1).getPostId();
            PostManager.getInstance(uid, getActivity()).fetchPosts(getActivity(), lastpost);
        }
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
        set.setDuration(300);
        set.start();
    }

    @Override
    public void onPostsRetrieved(ArrayList<Post> posts) {
        Log.i(TAG, "onPostsRetrieved size: " + posts.size() + " original size: " + mPostsList.size());

        if (swipeLayout.isRefreshing()) {
            mPostAdapter.clearAdapter();
            mPostAdapter.addPosts(posts);
            swipeLayout.setRefreshing(false);
        } else {
            if (mPostAdapter == null) {
                mPostAdapter = new PostAdapter(getActivity(), mPostsList, UserManager.getInstance(uid, getActivity()).me(), false);
                mRecyclerView.setAdapter(mPostAdapter);
                mPostAdapter.setPostAdapterListener(this);
            } else {
                mPostAdapter.addPosts(posts);
                loadingMore = false;
            }
        }
    }

    @Override
    public void onCommentsClicked(Post post) {
        if (getActivity().getSupportFragmentManager().findFragmentByTag(CommentsDialog.TAG) == null) {
            CommentsDialog dialog = CommentsDialog.newInstance(post.getPostId(), uid);
            dialog.show(getActivity().getSupportFragmentManager(), CommentsDialog.TAG);
        }
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