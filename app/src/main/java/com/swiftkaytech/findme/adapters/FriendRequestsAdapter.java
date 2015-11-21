package com.swiftkaytech.findme.adapters;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.swiftkaytech.findme.R;
import com.swiftkaytech.findme.data.User;
import com.swiftkaytech.findme.fragment.FriendRequestsFrag;
import com.swiftkaytech.findme.utils.ImageLoader;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kevin Haines on 3/2/2015.
 */
public class FriendRequestsAdapter extends RecyclerView.Adapter<FriendRequestsAdapter.FriendRequestViewHolder> {

    private ImageLoader imageLoader;
    private String uid;
    private Context mContext;
    private ArrayList<User> users;


    public FriendRequestsAdapter(Context context, ArrayList<User> users, String uid){
        this.uid = uid;
        this.mContext = context;
        this.users = users;
        imageLoader = new ImageLoader(context);
    }

    @Override
    public FriendRequestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friendslistitem, parent, false);
        return new FriendRequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FriendRequestViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return users.size();
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
                //todo: show users profile
            }

        }
    }
}
