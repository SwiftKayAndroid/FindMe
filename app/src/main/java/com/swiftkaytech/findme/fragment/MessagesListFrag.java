package com.swiftkaytech.findme.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.swiftkaytech.findme.R;
import com.swiftkaytech.findme.adapters.MessageThreadsAdapter;
import com.swiftkaytech.findme.data.Message;
import com.swiftkaytech.findme.data.ThreadInfo;
import com.swiftkaytech.findme.managers.MessagesManager;
import com.swiftkaytech.findme.utils.VarHolder;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by BN611 on 3/9/2015.
 */
public class MessagesListFrag extends BaseFragment implements MessagesManager.MessageThreadListener{
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

    public MessagesListFrag(){}

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
            MessagesManager.getInstance(uid).refreshThreads();
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
        MessagesManager.getInstance(uid).addThreadsListener(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        MessagesManager.getInstance(uid).removeThreadsListener(this);
    }

    @Override
    public void onRetrieveMoreThreads(ArrayList<ThreadInfo> threadInfos) {

        if (mMessagesAdapter == null) {
            mMessagesAdapter = new MessageThreadsAdapter(getActivity(), threadInfos, uid);
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
}
