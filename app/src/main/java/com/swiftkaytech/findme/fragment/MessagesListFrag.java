package com.swiftkaytech.findme.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.swiftkaytech.findme.R;
import com.swiftkaytech.findme.activity.MessagesActivity;
import com.swiftkaytech.findme.adapters.MessageThreadsAdapter;
import com.swiftkaytech.findme.data.Message;
import com.swiftkaytech.findme.data.ThreadInfo;
import com.swiftkaytech.findme.managers.MessagesManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kevin Haines on 3/9/2015.
 */
public class MessagesListFrag extends BaseFragment implements MessagesManager.MessageThreadListener,
        MessageThreadsAdapter.ThreadSelectedListener, SwipeRefreshLayout.OnRefreshListener{
    public static final String      TAG = "MessagesListFrag";
    private static final String     ARG_THREAD_LIST = "ARG_THREAD_LIST";

    private List<ThreadInfo>        mThreadList = new ArrayList<>();
    private MessageThreadsAdapter   mMessagesAdapter;

    private boolean                 mRefreshing;
    private RecyclerView            mRecyclerView;
    private SwipeRefreshLayout      mRefreshLayout;
    private View                    mEmptyView;

    public static MessagesListFrag getInstance(String uid) {
        MessagesListFrag frag = new MessagesListFrag();
        Bundle b = new Bundle();
        b.putString(ARG_UID, uid);
        frag.setArguments(b);
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mThreadList = (ArrayList) savedInstanceState.getSerializable(ARG_THREAD_LIST);
            uid = savedInstanceState.getString(ARG_UID);
        } else {
            if (getArguments() != null) {
                uid = getArguments().getString(ARG_UID);
            }
            if (uid == null || uid.isEmpty()) {
                err("uid is null or empty on view created");
            }
            MessagesManager.getInstance(uid, getActivity()).refreshThreads(getActivity());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.messageslistfrag, container, false);
        mRecyclerView = (RecyclerView) layout.findViewById(R.id.recyclerViewMessagesList);
        mRefreshLayout = (SwipeRefreshLayout) layout.findViewById(R.id.messageThreadsSwipeRefreshContainer);
        mEmptyView = layout.findViewById(R.id.messageThreadsEmptyView);

        return layout;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        if (mMessagesAdapter == null) {
            mMessagesAdapter = new MessageThreadsAdapter(getActivity(), mThreadList, uid);
            mMessagesAdapter.setThreadSelectedListener(this);
            mRecyclerView.setAdapter(mMessagesAdapter);
        }

        if (mThreadList.size() < 1) {
            mEmptyView.setVisibility(View.VISIBLE);
        } else {
            mEmptyView.setVisibility(View.GONE);
        }

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(ARG_THREAD_LIST, (ArrayList) mThreadList);
        outState.putString(ARG_UID, uid);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onThreadDeleted(ThreadInfo threadInfo) {
        mMessagesAdapter.removeThread(threadInfo);
        if (mThreadList.size() < 1) {
            mEmptyView.setVisibility(View.VISIBLE);
        } else {
            mEmptyView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onThreadsPurged() {
        mMessagesAdapter.clearMessages();
        if (mThreadList.size() < 1) {
            mEmptyView.setVisibility(View.VISIBLE);
        } else {
            mEmptyView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        MessagesManager.getInstance(uid, getActivity()).addThreadsListener(this);
        if (mMessagesAdapter != null) {
            mMessagesAdapter.setThreadSelectedListener(this);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        MessagesManager.getInstance(uid, getActivity()).removeThreadsListener(this);
        if (mMessagesAdapter != null) {
            mMessagesAdapter.setThreadSelectedListener(null);
        }
    }

    @Override
    public void onRefresh() {
        mRefreshLayout.setRefreshing(true);
        mRefreshing = true;
        MessagesManager.getInstance(uid, getActivity()).refreshThreads(getActivity());
    }

    @Override
    public void onRetrieveMoreThreads(ArrayList<ThreadInfo> threadInfos) {
        Log.i(TAG, "onRetriveMoreThreads");
        if (mRefreshing) {
            mMessagesAdapter.removeAllThreads();
            mMessagesAdapter.addThreads(threadInfos);
        } else {
            mMessagesAdapter.addThreads(threadInfos);
        }
        mRefreshLayout.setRefreshing(false);
        mRefreshing = false;

        if (mThreadList.size() < 1) {
            mEmptyView.setVisibility(View.VISIBLE);
        } else {
            mEmptyView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onMessageSentComplete(Message message) {
        mMessagesAdapter.addMessage(message);
    }

    @Override
    public void onMessageUnsent(Message message) {
        //todo: will complete this after we find a way
        //todo: to retrieve the last last message
    }

    @Override
    public void onThreadSelected(ThreadInfo threadInfo) {
        startActivity(MessagesActivity.createIntent(getActivity(), threadInfo.threadUser));
    }

    @Override
    public void onThreadLongClicked(final ThreadInfo threadInfo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Delete Thread");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MessagesManager.getInstance(uid, getActivity()).deleteThread(threadInfo);
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    @Override
    public void onMessageRecevied(Message message) {
        mMessagesAdapter.addMessage(message);
    }
}
