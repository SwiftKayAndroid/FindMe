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
import com.swiftkaydevelopment.findme.data.User;
import com.swiftkaydevelopment.findme.views.CircleTransform;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kevin Haines on 3/1/2015.
 */
public class FindPeopleAdapter extends RecyclerView.Adapter<FindPeopleAdapter.ConnectViewHolder> {

    public interface ConnectAdapterListener {
        void onLastItem(User lastUser);
        void onUserSelected(User user);
    }

    private Context mContext;
    private List<User> users;
    private String uid;
    private ConnectAdapterListener mListener;

    public FindPeopleAdapter(Context context, List<User> users, String uid){
        this.mContext = context;
        this.users = users;
        this.uid = uid;
    }

    /**
     * Adds users to this arraylist/adapter
     *
     * @param users List of users to add
     */
    public void addUsers(ArrayList<User> users) {
        this.users.addAll(users);
        notifyDataSetChanged();
    }

    /**
     * Empties the adapter
     *
     */
    public void clear() {
        users.clear();
    }

    /**
     * Sets the ConnectAdapterListener
     *
     * @param listener Listener to set
     */
    public void setListener(ConnectAdapterListener listener) {
        mListener = listener;
    }

    @Override
    public ConnectViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ConnectViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.findpeoplegriditem, parent, false));
    }

    @Override
    public void onBindViewHolder(ConnectViewHolder holder, final int position) {
        holder.tvDesc.setText(users.get(position).getGender().toString().substring(0, 1) + "/" +
                users.get(position).getAge() + "/" + users.get(position).getOrientation().toString());

        if (users.get(position).getPropicloc().equals("")) {
            Picasso.with(mContext)
                    .load(R.drawable.ic_placeholder)
                    .transform(new CircleTransform())
                    .into(holder.propic);
        } else {
            Picasso.with(mContext)
                    .load(users.get(position).getPropicloc())
                    .transform(new CircleTransform())
                    .into(holder.propic);
        }

        if (position == getItemCount() - 1) {
            if (mListener != null) {
                mListener.onLastItem(users.get(users.size() - 1));
            }
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onUserSelected(users.get(position));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    protected class ConnectViewHolder extends RecyclerView.ViewHolder {
        ImageView propic;
        TextView tvDesc;

        public ConnectViewHolder(View itemView) {
            super(itemView);
            propic = (ImageView) itemView.findViewById(R.id.ivFindPeopleGridItemProfilePicture);
            tvDesc = (TextView) itemView.findViewById(R.id.tvfindpeople);
        }
    }
}
