package com.swiftkaytech.findme.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.swiftkaytech.findme.R;
import com.swiftkaytech.findme.activity.ProfileActivity;
import com.swiftkaytech.findme.data.User;
import com.swiftkaytech.findme.utils.ImageLoader;

import java.util.ArrayList;

/**
 * Created by Kevin Haines on 3/2/2015.
 */
public class FriendRequestsAdapter extends RecyclerView.Adapter<FriendRequestsAdapter.FriendRequestViewHolder> {

    public interface FriendRequestsAdapterListener{
        void onFriendRequestAccepted(User user);
        void onFriendRequestDenied(User user);
    }

    private ImageLoader imageLoader;
    private String uid;
    private Context mContext;
    private ArrayList<User> users;

    private FriendRequestsAdapterListener mListener;


    public FriendRequestsAdapter(Context context, ArrayList<User> users, String uid){
        this.uid = uid;
        this.mContext = context;
        this.users = users;
        imageLoader = new ImageLoader(context);
    }

    /**
     * Sets the FriendRequestAdapterListener
     * @param listener FriendRequestsAdapterListener
     */
    public void setFriendRequestsAdapterListener(FriendRequestsAdapterListener listener) {
        mListener = listener;
    }

    @Override
    public FriendRequestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friendslistitem, parent, false);
        return new FriendRequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FriendRequestViewHolder holder, int position) {
        if (users.get(position).getPropicloc().equals("")) {
            holder.iv.setImageResource(R.drawable.ic_placeholder);
        } else {
            imageLoader.DisplayImage(users.get(position).getPropicloc(), holder.iv, false);
        }
        holder.tvName.setText(users.get(position).getFirstname() + " " + users.get(position).getLastname());
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public void addUsers(ArrayList<User> users) {
        this.users.addAll(users);
    }


    protected class FriendRequestViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView iv;
        TextView tvName;
        TextView tvDesc;
        TextView tvAccept;
        TextView tvDeny;

        public FriendRequestViewHolder(View itemView) {
            super(itemView);

            iv = (ImageView) itemView.findViewById(R.id.ivfriendslist);
            tvName = (TextView) itemView.findViewById(R.id.tvfriendslistitemname);
            tvDesc = (TextView) itemView.findViewById(R.id.tvfriendslistitemdesc);
            tvAccept = (TextView) itemView.findViewById(R.id.tvacceptfriendrequest);
            tvDeny = (TextView) itemView.findViewById(R.id.tvdenyfriendrequest);

            iv.setOnClickListener(this);
            tvAccept.setOnClickListener(this);
            tvDeny.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.ivfriendslist) {
                mContext.startActivity(ProfileActivity.createIntent(mContext, users.get(getLayoutPosition())));
            }

        }
    }
}
