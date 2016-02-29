package com.swiftkaydevelopment.findme.fragment;

import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.swiftkaydevelopment.findme.R;
import com.swiftkaydevelopment.findme.activity.ProfileActivity;
import com.swiftkaydevelopment.findme.adapters.MessagesAdapter;
import com.swiftkaydevelopment.findme.data.Message;
import com.swiftkaydevelopment.findme.data.ThreadInfo;
import com.swiftkaydevelopment.findme.data.User;
import com.swiftkaydevelopment.findme.events.MessageReceivedEvent;
import com.swiftkaydevelopment.findme.managers.MessagesManager;
import com.swiftkaydevelopment.findme.managers.UserManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

/**
 * Created by Kevin Haines on 3/11/2015.
 */
public class MessagesFrag extends BaseFragment implements View.OnClickListener, MessagesAdapter.MessagesAdapterListener, TextWatcher {
    public static final String  TAG = "MessagesFrag";
    private static final String ARG_USER = "ARG_USER";
    private static final String ARG_MESSAGES = "ARG_MESSAGES";

    private ArrayList<Message>  mMessagesList = new ArrayList<>();
    private User user;
    private ThreadInfo          mThreadInfo;
    private MessagesAdapter     mMessageAdapter;

    private EditText            etmessage;
    private ImageView           ivsend;
    private RecyclerView        mRecyclerView;
    private View                mEmptyView;
    private ProgressBar         mProgressBar;

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
        mEmptyView = layout.findViewById(R.id.messageInlineEmptyView);
        mProgressBar = (ProgressBar) layout.findViewById(R.id.progressBar);

        ivsend.setOnClickListener(this);
        return layout;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (savedInstanceState != null) {
            mMessagesList = (ArrayList) savedInstanceState.getSerializable(ARG_MESSAGES);
            mProgressBar.setVisibility(View.GONE);
            if (mMessagesList == null || mMessagesList.isEmpty()) {
                mEmptyView.setVisibility(View.VISIBLE);
            } else {
                mEmptyView.setVisibility(View.GONE);
            }
        } else {
            mEmptyView.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.VISIBLE);
            if (mThreadInfo != null) {
                MessagesManager.getInstance(uid).getMoreMessages("0", mThreadInfo, getActivity());
            } else {
                MessagesManager.getInstance(uid).getMoreMessages("0", user, getActivity());
            }
        }

        if (mMessageAdapter == null) {
            mMessageAdapter = new MessagesAdapter(getActivity(), mMessagesList, uid);
        }

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mMessageAdapter);
        etmessage.addTextChangedListener(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(ARG_USER, user);
        outState.putString(ARG_UID, uid);
        outState.putSerializable(ARG_MESSAGES, mMessagesList);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mMessageAdapter != null) {
            mMessageAdapter.setMessagesAdapterListener(this);
        }
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mMessageAdapter != null) {
            mMessageAdapter.setMessagesAdapterListener(null);
        }
        EventBus.getDefault().unregister(this);
    }

    /**
     * Called when messages are fetched/refreshed
     * @param messages Arraylist of Messages
     */
    public void updateMessages(ArrayList<Message> messages) {
        mProgressBar.setVisibility(View.GONE);
        mMessageAdapter.addAllMessages(messages);
        mMessageAdapter.setMessagesAdapterListener(this);
        int size = mRecyclerView.getLayoutManager().getItemCount() - 1;
        if (size > 0) {
            mRecyclerView.smoothScrollToPosition(size);
        }

        if (messages.size() < 1) {
            mEmptyView.setVisibility(View.VISIBLE);
        } else {
            mEmptyView.setVisibility(View.GONE);
        }
    }

    /**
     * Called when message is sent
     * @param message message that was sent
     */
    public void notifyNewMessage(Message message) {
        Log.i(TAG, "new message");
        Log.e(TAG, "message user id: " + message.getUser().getOuid());
        if (message.getUser().getOuid().equals(user.getOuid()) || message.getUser().getOuid().equals(uid)) {
            if (mMessageAdapter != null) {
                Log.i(TAG, "Message Adapter isn't null");
                mMessageAdapter.addMessage(message);
                if (!message.getUser().getOuid().equals(uid)) {
                    MessagesManager.getInstance(uid).markThreadAsSeen(uid, message.getThreadId());
                    try {
                        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                        Ringtone r = RingtoneManager.getRingtone(getActivity(), notification);
                        r.play();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                int size = mRecyclerView.getLayoutManager().getItemCount() - 1;
                mRecyclerView.smoothScrollToPosition(size);
            }
        } else {
            //todo: should we show push notification or just make notification sound
        }

        if (mMessagesList.size() < 1) {
            mEmptyView.setVisibility(View.VISIBLE);
        } else {
            mEmptyView.setVisibility(View.GONE);
        }
    }

    public void notifyMessageDeleted(Message message) {
        mMessageAdapter.removeMessage(message);
    }

    public String getThreadId() {
        if (mMessageAdapter != null && mMessageAdapter.getMessages().size() > 0) {
            return mMessageAdapter.getMessages().get(0).getThreadId();
        }
        return "";
    }

    @Override
    public void onMessageLongClick(View itemView, final Message message) {
        PopupMenu popup = new PopupMenu(getActivity(), itemView);
        //Inflating the Popup using xml file
        popup.getMenuInflater().inflate(R.menu.message_pop_up_menu, popup.getMenu());
        if (!message.getUser().getOuid().equals(uid)) {
            popup.getMenu().findItem(R.id.messagesPopUpMenuUnsend).setVisible(false);
        }

        //registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.messagePopUpMenuDelete) {
                    MessagesManager.getInstance(uid).deleteMessage(message);
                } else if (item.getItemId() == R.id.messagesPopUpMenuUnsend) {
                    MessagesManager.getInstance(uid).unSendMessage(message);
                }
                return true;
            }
        });
        popup.show();//showing popup menu
    }

    @Override
    public void onProfileImageClicked(Message message) {
        getActivity().startActivity(ProfileActivity.createIntent(getActivity(), message.getUser()));
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEvent(MessageReceivedEvent event) {
        EventBus.getDefault().removeStickyEvent(event);
        if (event.message.getUser().getOuid().equals(user.getOuid())) {
            mMessageAdapter.addMessage(event.message);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (count > 0) {
            ivsend.setEnabled(true);
            ivsend.setColorFilter(getActivity().getResources().getColor(R.color.base_green));
        } else {
            ivsend.setEnabled(false);
            ivsend.setColorFilter(getActivity().getResources().getColor(R.color.disabled_gray));
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tvsendmessage && !(etmessage.getText().toString().equals(""))) {
            Message message = Message.instance();
            message.setDeletedStatus(0);
            message.setMessage(etmessage.getText().toString());
            message.setOuid(uid);
            message.setReadStatus(1);
            message.setSeenStatus(0);
            message.setUser(UserManager.getInstance(uid).me());
            message.setTime("Just Now");
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromInputMethod(etmessage.getWindowToken(), 0);
            etmessage.setText("");

            MessagesManager.getInstance(uid).sendMessage(message, user);
        }
    }
}
