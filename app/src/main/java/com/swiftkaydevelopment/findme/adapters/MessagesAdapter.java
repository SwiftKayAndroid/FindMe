package com.swiftkaydevelopment.findme.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.swiftkaydevelopment.findme.R;
import com.swiftkaydevelopment.findme.data.Message;
import com.swiftkaydevelopment.findme.utils.ImageLoader;
import com.swiftkaydevelopment.findme.views.CircleTransform;
import com.swiftkaydevelopment.findme.views.ReducedTransform;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kevin Haines on 3/29/2015.
 */
public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessageViewHolder> {

    public interface MessagesAdapterListener{
        void onMessageLongClick(View itemView, Message message);
        void onProfileImageClicked(Message message);
    }
    public static final String TAG = "MessagesAdapter";
    private static final int VIEW_TYPE_USER_MESSAGE = 0;
    private static final int VIEW_TYPE_OTHER_MESSAGE = 1;

    Context mContext;
    ArrayList<Message> mMessageList;
    String uid;
    ImageLoader imageLoader;
    private MessagesAdapterListener mListener;

    public MessagesAdapter(Context context, ArrayList<Message> mlist, String uid){
        this.mContext = context;
        this.mMessageList = mlist;
        this.uid = uid;
        imageLoader = new ImageLoader(context);
    }

    public void setMessagesAdapterListener (MessagesAdapterListener listener) {
        mListener = listener;
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
    public void onBindViewHolder(final MessagesAdapter.MessageViewHolder holder, final int position) {

        holder.tvMessage.setText(mMessageList.get(position).getMessage());
        holder.tvTime.setText(mMessageList.get(position).getTime());
        if (TextUtils.isEmpty(mMessageList.get(position).getUser().getPropicloc())) {
            Picasso.with(mContext)
                    .load(R.drawable.ic_placeholder)
                    .transform(new CircleTransform(mContext))
                    .into(holder.profilePicture);
        } else {
            Picasso.with(mContext)
                    .load(mMessageList.get(position).getUser().getPropicloc())
                    .transform(new CircleTransform(mContext))
                    .into(holder.profilePicture);
        }

        if (getItemViewType(position) == VIEW_TYPE_USER_MESSAGE) {
            if (mMessageList.get(position).getSeenStatus() == 1) {
                holder.tvSeen.setVisibility(View.VISIBLE);
            } else {
                holder.tvSeen.setVisibility(View.GONE);
            }
        }

        if (!TextUtils.isEmpty(mMessageList.get(position).mMessageImageLocation)) {
            holder.ivImage.setVisibility(View.VISIBLE);
            Picasso.with(mContext)
                    .load(mMessageList.get(position).mMessageImageLocation)
                    .transform(new ReducedTransform())
                    .into(holder.ivImage);
        } else {
            holder.ivImage.setVisibility(View.GONE);
        }

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                if (mListener != null) {
                    mListener.onMessageLongClick(holder.tvMessage, mMessageList.get(position));
                }
                return true;
            }

        });

        holder.profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onProfileImageClicked(mMessageList.get(position));
                }
            }
        });
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

    public void removeMessage(Message message) {
        if (mMessageList.contains(message)) {
            mMessageList.remove(message);
            notifyDataSetChanged();
        }
    }

    public ArrayList<Message> getMessages() {
        return new ArrayList<>(mMessageList);
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {

        ImageView profilePicture;
        ImageView ivImage;
        TextView tvTime;
        TextView tvMessage;
        TextView tvSeen;

        public MessageViewHolder(View itemView) {
            super(itemView);

            profilePicture = (ImageView) itemView.findViewById(R.id.messageItemProfilePicture);
            tvTime = (TextView) itemView.findViewById(R.id.messageItemTime);
            tvMessage = (TextView) itemView.findViewById(R.id.messageItemMessageText);
            tvSeen = (TextView) itemView.findViewById(R.id.messageItemUserSeenStatus);
            ivImage = (ImageView) itemView.findViewById(R.id.ivMessageImage);
        }
    }
}
