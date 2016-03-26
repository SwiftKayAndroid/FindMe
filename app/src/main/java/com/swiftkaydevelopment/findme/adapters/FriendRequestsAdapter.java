package com.swiftkaydevelopment.findme.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.swiftkaydevelopment.findme.R;
import com.swiftkaydevelopment.findme.activity.ProfileActivity;
import com.swiftkaydevelopment.findme.data.User;
import com.swiftkaydevelopment.findme.managers.UserManager;
import com.swiftkaydevelopment.findme.views.CircleTransform;

import java.util.ArrayList;

/**
 * Created by Kevin Haines on 3/2/2015.
 */
public class FriendRequestsAdapter extends RecyclerView.Adapter<FriendRequestsAdapter.FriendRequestViewHolder> {

    public interface FriendRequestsAdapterListener{
        void onFriendRequestAccepted(User user);
        void onFriendRequestDenied(User user);
    }

    private String uid;
    private Context mContext;
    private ArrayList<User> users = new ArrayList<>();

    private FriendRequestsAdapterListener mListener;


    public FriendRequestsAdapter(Context context, ArrayList<User> users, String uid){
        this.uid = uid;
        this.mContext = context;
        this.users = users;
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friendrequest_list_item, parent, false);
        return new FriendRequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FriendRequestViewHolder holder, int position) {
        if (users.get(position).getPropicloc().equals("")) {
            holder.iv.setImageResource(R.drawable.ic_placeholder);
        } else {
            Picasso.with(mContext)
                    .load(users.get(position).getPropicloc())
                    .transform(new CircleTransform(mContext))
                    .into(holder.iv);
        }
        holder.tvName.setText(users.get(position).getFirstname() + " " + users.get(position).getLastname());
        holder.tvDesc.setText(users.get(position).getAge() + "/" + users.get(position).getGender().toString());
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public void addUsers(ArrayList<User> users) {
        this.users.addAll(users);
        notifyDataSetChanged();
    }

    public void removeUser(User user) {
        if (users.contains(user)) {
            users.remove(user);
            notifyDataSetChanged();
        }
    }


    protected class FriendRequestViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView iv;
        TextView tvName;
        TextView tvDesc;
        ImageView ivAccept;
        ImageView ivDeny;

        public FriendRequestViewHolder(View itemView) {
            super(itemView);

            iv = (ImageView) itemView.findViewById(R.id.ivfriendslist);
            tvName = (TextView) itemView.findViewById(R.id.tvfriendslistitemname);
            tvDesc = (TextView) itemView.findViewById(R.id.tvfriendslistitemdesc);
            ivAccept = (ImageView) itemView.findViewById(R.id.ivAddFriendFriendRequests);
            ivDeny = (ImageView) itemView.findViewById(R.id.ivDenyFriendRequest);

            iv.setOnClickListener(this);
            ivAccept.setOnClickListener(this);
            ivDeny.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.ivfriendslist) {
                mContext.startActivity(ProfileActivity.createIntent(mContext, users.get(getLayoutPosition())));
            } else if (v.getId() == R.id.ivAddFriendFriendRequests) {
                UserManager.getInstance(uid).sendFriendRequest(uid, users.get(getLayoutPosition()));
                removeUser(users.get(getLayoutPosition()));
            } else if (v.getId() == R.id.ivDenyFriendRequest) {
                UserManager.getInstance(uid).denyFriendRequest(uid, users.get(getLayoutPosition()));
                removeUser(users.get(getLayoutPosition()));
            }
        }
    }
}
