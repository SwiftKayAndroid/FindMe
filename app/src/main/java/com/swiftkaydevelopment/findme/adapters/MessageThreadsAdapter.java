package com.swiftkaydevelopment.findme.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.swiftkaydevelopment.findme.R;
import com.swiftkaydevelopment.findme.data.Message;
import com.swiftkaydevelopment.findme.data.ThreadInfo;
import com.swiftkaydevelopment.findme.data.User;
import com.swiftkaydevelopment.findme.utils.ImageLoader;
import com.swiftkaydevelopment.findme.views.CircleTransform;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kevin Haines on 3/9/2015.
 */
public class MessageThreadsAdapter extends RecyclerView.Adapter<MessageThreadsAdapter.MessageThreadsViewHolder> {

    public interface ThreadSelectedListener{
        void onThreadSelected(ThreadInfo threadInfo);
        void onThreadLongClicked(ThreadInfo threadInfo);
        void onThreadUserSelected(User user);
    }
    private static final String     TAG = "MessageThreadsAdapter";

    private ThreadSelectedListener  mListener;

    private Context                 mContext;
    List<ThreadInfo>                mThreadList;
    ImageLoader imageLoader;
    String                          mUid;

    public MessageThreadsAdapter(Context context, List<ThreadInfo> mlist, String uid){
        this.mContext = context.getApplicationContext();
        this.mThreadList = mlist;
        this.mUid = uid;
        imageLoader = new ImageLoader(context);
    }

    /**
     * Sets the listener for this adapter
     *
     * @param listener Listener to assign to this adapter
     */
    public void setThreadSelectedListener(ThreadSelectedListener listener) {
        mListener = listener;
    }

    @Override
    public MessageThreadsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.messagethreadlistitem, parent, false);
        return new MessageThreadsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MessageThreadsViewHolder holder, final int position) {

        final ThreadInfo thread = mThreadList.get(position);
        if(thread != null && thread.threadUser != null && thread.threadUser.getOuid() != null){
            if(!thread.threadUser.getPropicloc().equals("")){
                Picasso.with(mContext)
                        .load(thread.threadUser.getPropicloc())
                        .transform(new CircleTransform(mContext))
                        .into(holder.ivpropic);
            } else {
                Picasso.with(mContext)
                        .load(R.drawable.ic_placeholder)
                        .transform(new CircleTransform(mContext))
                        .into(holder.ivpropic);
            }

            holder.tvname.setText(thread.threadUser.getFirstname() + " " + thread.threadUser.getLastname());
            holder.tvmessage.setText(thread.lastMessage);
            holder.tvtime.setText(thread.time);

            if(thread.senderId.equals(mUid) && thread.seenStatus == ThreadInfo.SEEN){
                holder.checkmark.setVisibility(View.VISIBLE);
            } else{
                holder.checkmark.setVisibility(View.GONE);
            }

            if (thread.readStatus == ThreadInfo.READ) {
                holder.tvname.setTypeface(null, Typeface.NORMAL);
                holder.tvmessage.setTypeface(null, Typeface.NORMAL);
                holder.tvtime.setTypeface(null, Typeface.NORMAL);
            } else {
                holder.tvmessage.setTypeface(null, Typeface.BOLD);
                holder.tvname.setTypeface(null, Typeface.BOLD);
                holder.tvtime.setTypeface(null, Typeface.BOLD);
            }

            holder.ivpropic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.onThreadUserSelected(mThreadList.get(position).threadUser);
                    }
                }
            });

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        Log.i(TAG, "message clicked");
                        mThreadList.get(position).readStatus = ThreadInfo.READ;
                        notifyItemChanged(position);
                        mListener.onThreadSelected(mThreadList.get(position));
                    }
                }
            });
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (mListener != null) {
                        mListener.onThreadLongClicked(mThreadList.get(position));
                    }
                    return true;
                }
            });
        }
    }

    /**
     * Removes a thread from the adapter
     *
     * @param threadInfo ThreadInfo to remove
     */
    public void removeThread(ThreadInfo threadInfo) {
        if (mThreadList.contains(threadInfo)) {
            mThreadList.remove(threadInfo);
            notifyDataSetChanged();
        }
    }

    public void removeAllThreads() {
        if (mThreadList != null) {
            mThreadList.clear();
            notifyDataSetChanged();
        }
    }

    public void clearMessages() {
        mThreadList.clear();
        notifyDataSetChanged();
    }

    public void addThreads(ArrayList<ThreadInfo> threadInfos) {
        if (mThreadList == null) {
            mThreadList = new ArrayList<>();
        }
        mThreadList.addAll(threadInfos);
        notifyDataSetChanged();
    }

    public void addMessage(Message message) {
        boolean messageFound = false;
        if (mThreadList != null) {
            for (ThreadInfo threadInfo : mThreadList) {
                if (threadInfo.ouid.equals(message.getOuid())) {
                    threadInfo.lastMessage = message.getMessage();
                    messageFound = true;
                    break;
                }
            }
            if (!messageFound) {
                ThreadInfo threadInfo = ThreadInfo.instance(mUid);
                threadInfo.lastMessage = message.getMessage();
                threadInfo.threadUser = message.getUser();
                threadInfo.seenStatus = message.getSeenStatus();
                threadInfo.senderId = message.getSenderId();
                threadInfo.ouid = message.getOuid();
                threadInfo.readStatus = message.getReadStatus();
                threadInfo.time = message.getTime();
                mThreadList.add(threadInfo);
                //todo: check to make sure that added thread shows up
                //todo: at top of list
            }
            notifyDataSetChanged();
        }
    }

    public void markSeen(String ouid) {
        for (ThreadInfo info : mThreadList) {
            if (info.ouid.equals(ouid)) {
                info.seenStatus = 1;
                notifyItemChanged(mThreadList.indexOf(info));
                break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return mThreadList.size();
    }

    public class MessageThreadsViewHolder extends RecyclerView.ViewHolder{

        ImageView ivpropic;
        TextView tvname;
        TextView tvmessage;
        TextView tvtime;
        ImageView checkmark;

        public MessageThreadsViewHolder(View itemView) {
            super(itemView);

            ivpropic = (ImageView) itemView.findViewById(R.id.ivmessagethread);
            tvname = (TextView) itemView.findViewById(R.id.tvmessagethreadname);
            tvmessage = (TextView) itemView.findViewById(R.id.tvmessagethreadmessage);
            tvtime = (TextView) itemView.findViewById(R.id.tvmessagethreadtime);
            checkmark = (ImageView) itemView.findViewById(R.id.ivcheckmarkmessagethread);
            itemView.setTag(this);
        }
    }
}
