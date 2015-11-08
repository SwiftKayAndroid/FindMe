package com.swiftkaytech.findme.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.swiftkaytech.findme.R;
import com.swiftkaytech.findme.activity.MessagesActivity;
import com.swiftkaytech.findme.adapters.MessageThreadsAdapter;
import com.swiftkaytech.findme.data.Message;
import com.swiftkaytech.findme.data.ThreadInfo;
import com.swiftkaytech.findme.managers.MessagesManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by BN611 on 3/9/2015.
 */
public class MessagesListFrag extends BaseFragment implements MessagesManager.MessageThreadListener,
        MessageThreadsAdapter.ThreadSelectedListener{
    private static final String     TAG = "MessagesListFrag";
    private static final String     ARG_THREAD_LIST = "ARG_THREAD_LIST";

    private static MessagesListFrag frag = null;

    private List<ThreadInfo>        mThreadList;
    private MessageThreadsAdapter   mMessagesAdapter;

    private boolean                 mRefreshing;
    private RecyclerView            mRecyclerView;

    public static MessagesListFrag getInstance(String uid) {
        if (frag == null) {
            frag = new MessagesListFrag();
        }
        Bundle b = new Bundle();
        b.putString(ARG_UID, uid);
        frag.setArguments(b);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.messageslistfrag, container, false);
        mRecyclerView = (RecyclerView) layout.findViewById(R.id.recyclerViewMessagesList);

        return layout;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
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
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        MessagesManager.getInstance(uid, getActivity()).addThreadsListener(this);
        if (mMessagesAdapter != null) {
            mMessagesAdapter.setThreadSelectedListener(this);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        MessagesManager.getInstance(uid, getActivity()).removeThreadsListener(this);
        if (mMessagesAdapter != null) {
            mMessagesAdapter.setThreadSelectedListener(null);
        }
    }

    @Override
    public void onRetrieveMoreThreads(ArrayList<ThreadInfo> threadInfos) {

        if (mMessagesAdapter == null) {
            mMessagesAdapter = new MessageThreadsAdapter(getActivity(), threadInfos, uid);
            mMessagesAdapter.setThreadSelectedListener(this);
            mRecyclerView.setAdapter(mMessagesAdapter);
        }
        if (mRefreshing) {
            mMessagesAdapter.removeAllThreads();
            mMessagesAdapter.addThreads(threadInfos);
        }
        mRefreshing = false;
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
    public void onMessageRecevied(Message message) {
        mMessagesAdapter.addMessage(message);
    }
}
