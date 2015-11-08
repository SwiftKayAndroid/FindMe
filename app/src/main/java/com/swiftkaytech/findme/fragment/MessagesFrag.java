package com.swiftkaytech.findme.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.swiftkaytech.findme.R;
import com.swiftkaytech.findme.adapters.MessagesAdapter;
import com.swiftkaytech.findme.data.Message;
import com.swiftkaytech.findme.data.ThreadInfo;
import com.swiftkaytech.findme.data.User;
import com.swiftkaytech.findme.managers.MessagesManager;
import com.swiftkaytech.findme.managers.UserManager;

import java.util.ArrayList;

/**
 * Created by Kevin Haines on 3/11/2015.
 */
public class MessagesFrag extends BaseFragment implements View.OnClickListener{
    public static final String TAG = "MessagesFrag";
    private static final String ARG_USER = "ARG_USER";
    private static final String ARG_MESSAGES = "ARG_MESSAGES";

    private ArrayList<Message> mMessagesList = new ArrayList<>();
    private User user;
    private ThreadInfo mThreadInfo;
    private MessagesAdapter mMessageAdapter;

    private EditText etmessage;
    private ImageView ivsend;
    private RecyclerView mRecyclerView;


    public static MessagesFrag instance(String uid, User user) {
        MessagesFrag frag = new MessagesFrag();
        Bundle b = new Bundle();
        b.putSerializable(ARG_USER, user);
        b.putString(ARG_UID, uid);
        frag.setArguments(b);
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            uid = savedInstanceState.getString(ARG_UID);
            user = (User) savedInstanceState.getSerializable(ARG_USER);
            mMessagesList = (ArrayList) savedInstanceState.getSerializable(ARG_MESSAGES);
        } else {
            if (getArguments() != null) {
                uid = getArguments().getString(ARG_UID);
                user = (User) getArguments().getSerializable(ARG_USER);
                if (mThreadInfo != null) {
                    MessagesManager.getInstance(uid, getActivity()).getMoreMessages("0", mThreadInfo, getActivity());
                } else {
                    MessagesManager.getInstance(uid, getActivity()).getMoreMessages("0", user, getActivity());
                }
            }
        };
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.messageinline, container, false);

        etmessage = (EditText) layout.findViewById(R.id.etmessaget);
        ivsend = (ImageView) layout.findViewById(R.id.tvsendmessage);
        mRecyclerView = (RecyclerView) layout.findViewById(R.id.messagesInlineRecyclerView);

        ivsend.setOnClickListener(this);
        return layout;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (mMessageAdapter == null) {
            mMessageAdapter = new MessagesAdapter(getActivity(), mMessagesList, uid);
        }
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mMessageAdapter);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(ARG_USER, user);
        outState.putString(ARG_UID, uid);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    /**
     * Called when messages are fetched/refreshed
     * @param messages Arraylist of Messages
     */
    public void updateMessages(ArrayList<Message> messages) {
        mMessagesList = messages;
        mMessageAdapter.addAllMessages(mMessagesList);
        int size = mRecyclerView.getLayoutManager().getItemCount() - 1;
        mRecyclerView.smoothScrollToPosition(size);
    }

    /**
     * Called when message is sent
     * @param message message that was sent
     */
    public void notifyNewMessage(Message message) {
        mMessageAdapter.addMessage(message);
        int size = mRecyclerView.getLayoutManager().getItemCount() - 1;
        mRecyclerView.smoothScrollToPosition(size);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tvsendmessage) {
            Message message = Message.instance(uid);
            message.setDeletedStatus(0);
            message.setMessage(etmessage.getText().toString());
            message.setOuid(uid);
            message.setReadStatus(0);
            message.setSeenStatus(0);
            message.setUser(UserManager.getInstance(uid, getActivity()).me());
            message.setTime("Just Now");
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromInputMethod(etmessage.getWindowToken(), 0);
            etmessage.setText("");

            MessagesManager.getInstance(uid,getActivity()).sendMessage(message, user);
        }
    }
}
