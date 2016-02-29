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
 * Created by khaines178 on 9/10/15.
 */
public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.FriendsViewHolder> {
    public static final String TAG = "FriendsAdapter";
    Context mContext;
    String uid;
    ArrayList<User> users = new ArrayList<>();

    public FriendsAdapter(Context context, ArrayList<User> users, String uid) {
        this.mContext = context;
        this.uid = uid;
        this.users = users;

    }

    @Override
    public FriendsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.friendslistitem, parent, false);
        return new FriendsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FriendsViewHolder holder, int position) {
        User user = users.get(position);
        if (user.getPropicloc().equals("")) {
            holder.iv.setImageResource(R.drawable.ic_placeholder);
        } else {
            Picasso.with(mContext)
                    .load(user.getPropicloc())
                    .transform(new CircleTransform())
                    .into(holder.iv);
        }
        holder.tvname.setText(user.getFirstname() + " " + user.getLastname());
        holder.tvdesc.setText(user.getAge() + "/" + user.getLocation().getCity() + "/" + user.getGender().toString());

    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    /**
     * Adds an ArrayList of User to the users ArrayList
     * then notifies data change
     * @param users ArrayList of User to add to data
     */
    public void addFriends(ArrayList<User> users) {
        this.users.addAll(users);
        notifyDataSetChanged();
    }

    public void removeUser(User user) {
        if (users.contains(user)) {
            users.remove(user);
            notifyDataSetChanged();
        }
    }

    protected class FriendsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView iv;
        TextView tvname;
        TextView tvdesc;
        ImageView ivUnfriend;
        public FriendsViewHolder(View itemView) {
            super(itemView);

            iv = (ImageView) itemView.findViewById(R.id.ivfriendslist);
            tvname = (TextView) itemView.findViewById(R.id.tvfriendslistitemname);
            tvdesc = (TextView) itemView.findViewById(R.id.tvfriendslistitemdesc);
            ivUnfriend = (ImageView) itemView.findViewById(R.id.ivDenyFriendRequest);
            ivUnfriend.setOnClickListener(this);
            iv.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.ivDenyFriendRequest) {
                UserManager.getInstance(uid).unfriend(uid, users.get(getLayoutPosition()));
                removeUser(users.get(getLayoutPosition()));
            } else if (v.getId() == R.id.ivfriendslist) {
                mContext.startActivity(ProfileActivity.createIntent(mContext, users.get(getLayoutPosition())));
            }
        }
    }
}
