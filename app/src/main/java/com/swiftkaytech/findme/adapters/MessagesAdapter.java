package com.swiftkaytech.findme.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.swiftkaytech.findme.R;
import com.swiftkaytech.findme.data.Message;
import com.swiftkaytech.findme.utils.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kevin Haines on 3/29/2015.
 */
public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessageViewHolder> {
    public static final String TAG = "MessagesAdapter";
    private static final int VIEW_TYPE_USER_MESSAGE = 0;
    private static final int VIEW_TYPE_OTHER_MESSAGE = 1;

    Context mContext;
    ArrayList<Message> mMessageList;
    String uid;
    ImageLoader imageLoader;

    public MessagesAdapter(Context context, ArrayList<Message> mlist, String uid){
        this.mContext = context;
        this.mMessageList = mlist;
        this.uid = uid;
        imageLoader = new ImageLoader(context);
    }

    @Override
    public MessagesAdapter.MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        if (viewType == VIEW_TYPE_USER_MESSAGE) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.messageitemuser, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.messageitemother, parent, false);
        }
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MessagesAdapter.MessageViewHolder holder, int position) {

        holder.tvMessage.setText(mMessageList.get(position).getMessage());
        holder.tvTime.setText(mMessageList.get(position).getTime());
        if (mMessageList.get(position).getUser().getPropicloc().isEmpty()) {
            holder.profilePicture.setImageResource(R.drawable.ic_placeholder);
        } else {
            imageLoader.DisplayImage(mMessageList.get(position).getUser().getPropicloc(), holder.profilePicture, false);
        }

    }

    @Override
    public int getItemViewType(int position) {
        if (mMessageList.get(position).getUser().getOuid().equals(uid)) {
            return VIEW_TYPE_USER_MESSAGE;
        }
        return VIEW_TYPE_OTHER_MESSAGE;
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    public void updateMessages(ArrayList<Message> messages) {
        removeAllMessages();
        addAllMessages(messages);
    }

    private void reorderMessages() {
        List<Message> templist = new ArrayList<Message>();
        templist.addAll(mMessageList);
        mMessageList.clear();
        int size = templist.size() -1;
        for(int i = size; i >= 0; i--){
            mMessageList.add(templist.get(i));
        }
    }

    public void removeAllMessages(){
        mMessageList.clear();
        notifyDataSetChanged();
    }

    public void addAllMessages(ArrayList<Message> messages) {
        Log.w(TAG, "size" + Integer.toString(messages.size()));
        reorderMessages();
        mMessageList.addAll(messages);
        reorderMessages();
        notifyDataSetChanged();
    }

    public void addMessage(Message message) {
        mMessageList.add(message);
        notifyDataSetChanged();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {

    ImageView profilePicture;
    TextView tvTime;
    TextView tvMessage;

    public MessageViewHolder(View itemView) {
        super(itemView);

        profilePicture = (ImageView) itemView.findViewById(R.id.messageItemProfilePicture);
        tvTime = (TextView) itemView.findViewById(R.id.messageItemTime);
        tvMessage = (TextView) itemView.findViewById(R.id.messageItemMessageText);
        itemView.setTag(this);
    }
}
}
