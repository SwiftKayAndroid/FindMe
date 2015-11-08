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
import com.swiftkaytech.findme.data.ThreadInfo;
import com.swiftkaytech.findme.utils.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kevin Haines on 3/9/2015.
 */
public class MessageThreadsAdapter extends RecyclerView.Adapter<MessageThreadsAdapter.MessageThreadsViewHolder> {

    public interface ThreadSelectedListener{
        void onThreadSelected(ThreadInfo threadInfo);
    }
    private static final String TAG = "MessageThreadsAdapter";

    private ThreadSelectedListener mListener;

    private Context mContext;
    List<ThreadInfo> mThreadList;
    ImageLoader imageLoader;
    String mUid;

    public MessageThreadsAdapter(Context context, List<ThreadInfo> mlist, String uid){
        this.mContext = context;
        this.mThreadList = mlist;
        this.mUid = uid;
        imageLoader = new ImageLoader(context);
    }

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
        if(thread != null){
            if(!thread.threadUser.getPropicloc().equals("")){
                imageLoader.DisplayImage(thread.threadUser.getPropicloc(), holder.ivpropic, false);
            }
            holder.tvname.setText(thread.threadUser.getName());
            holder.tvmessage.setText(thread.lastMessage);
            holder.tvtime.setText(thread.time);
            if(thread.readStatus == ThreadInfo.READ){

            }
            if(thread.seenStatus == ThreadInfo.SEEN){
                holder.checkmark.setVisibility(View.VISIBLE);
            }

            holder.ivpropic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //todo: start profile activity
                }
            });

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        Log.i(TAG, "message clicked");
                        mListener.onThreadSelected(mThreadList.get(position));
                    }
                }
            });
        }
    }

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
                if (threadInfo.threadId.equals(message.getThreadId())) {
                    threadInfo.lastMessage = message.getMessage();
                    messageFound = true;
                    break;
                }
            }
            if (!messageFound) {
                ThreadInfo threadInfo = ThreadInfo.instance(mUid);
                threadInfo.lastMessage = message.getMessage();
                threadInfo.threadId = message.getThreadId();
                threadInfo.threadUser = message.getUser();
                threadInfo.seenStatus = message.getSeenStatus();
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
