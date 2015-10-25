package com.swiftkaytech.findme.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import com.swiftkaytech.findme.R;
import com.swiftkaytech.findme.adapters.PostAdapter;
import com.swiftkaytech.findme.utils.VarHolder;

/**
 * Created by kevin haines on 2/8/2015.
 */
public class NewsFeedFrag extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener,View.OnClickListener{

    private String lp = "0";

    private boolean             loadingMore = false;
    private boolean             refreshing = false;
    private int                 lastpos;
    private ProgressBar         pb;
    private View                fab;
    private View                fabstatus;
    private View                fabphoto;
    private ListView lv;
    private SwipeRefreshLayout  swipeLayout;

    private PostAdapter mPostAdapter;

    public NewsFeedFrag(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {

        } else {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.newsfeedfrag, container, false);

        fab = layout.findViewById(R.id.fab);
        fabstatus = layout.findViewById(R.id.fabpencil);
        fabphoto = layout.findViewById(R.id.fabcamera);
        lv = (ListView) layout.findViewById(R.id.Lvnewsfeed);
        pb = (ProgressBar) layout.findViewById(R.id.pbnewsfeed);

        swipeLayout = (SwipeRefreshLayout) layout.findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setColorSchemeColors(android.R.color.holo_blue_bright,
                android.R.color.black,
                android.R.color.holo_blue_bright,
                android.R.color.black);

        return layout;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(VarHolder.TAG, "view is created newsfeedfrag");

        pb.setVisibility(View.GONE);
        lv.setVisibility(View.VISIBLE);
        Log.d(VarHolder.TAG, "fragment restored newsfeedfrag");
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
        fabstatus.setVisibility(View.GONE);
        fabphoto.setVisibility(View.GONE);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRefresh() {

    }

//                lv.setOnScrollListener(new AbsListView.OnScrollListener() {
//
//                    //useless here, skip!
//
//
//                    @Override
//                    public void onScrollStateChanged(AbsListView view, int scrollState) {
//                    }
//
//                    //dumdumdum
//
//                    @Override
//                    public void onScroll(AbsListView view, int firstVisibleItem,
//
//                                         int visibleItemCount, int totalItemCount) {
//
//
//                        int lastInScreen = firstVisibleItem + visibleItemCount;
//
//
//                        if (lastInScreen >= lastpos) {
//                            shrinkview = true;
//
//                            lastpos = lastInScreen;
//                        } else {
//                            shrinkview = false;
//
//                            lastpos = lastInScreen;
//                        }
//
//                        //is the bottom item visible & not loading more already ? Load more !
//
//                        if ((lastInScreen == totalItemCount) && !(loadingMore) && (plist.size() > 24)) {
//                            loadingMore = true;
//
//                            runGetAdditional();
//
//
//                        }
//
//                    }
//
//                });

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.fabcamera) {
            Intent i = new Intent("com.swiftkaytech.findme.UPLOADSERVICE");
            startActivity(i);

        } else if (v.getId() == R.id.fabpencil) {
            Intent i = new Intent("com.swiftkaytech.findme.UPDATESTATUS");
            startActivity(i);

        } else if (v.getId() == R.id.fab) {
            if (fabstatus.getVisibility() == View.GONE) {
                fabstatus.setVisibility(View.VISIBLE);
                fabphoto.setVisibility(View.VISIBLE);
            } else {
                fabstatus.setVisibility(View.GONE);
                fabphoto.setVisibility(View.GONE);
            }
        }
    }
}