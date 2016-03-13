/*
 *      Copyright (C) 2015 Kevin Haines
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 */

package com.swiftkaydevelopment.findme.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.swiftkaydevelopment.findme.R;
import com.swiftkaydevelopment.findme.data.User;

import java.util.ArrayList;

public class MatchAdapter extends RecyclerView.Adapter<MatchAdapter.MatchViewHolder> {

    public interface MatchAdapterListener {
        void onMatchClicked(User user);
    }

    public static final int TYPE_MATCHES = 0;
    public static final int TYPE_LIKED_USERS = 1;
    public static final int TYPE_LIKED_ME = 2;

    private static final String TAG = "MatchAdapter";

    private String mUid;
    private Context mContext;
    private ArrayList<User> matches;
    private int type;

    private MatchAdapterListener mListener;

    public MatchAdapter(Context mContext, ArrayList<User> matches, int type) {
        this.mContext = mContext;
        this.matches = matches;
        this.type = type;
    }

    public void setMatchAdapterListener(MatchAdapterListener listener) {
        mListener = listener;
    }

    @Override
    public MatchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.match_item, parent, false);
        return new MatchViewHolder(layout);
    }

    @Override
    public void onBindViewHolder(MatchViewHolder holder, int position) {
        final User user = matches.get(position);
        holder.name.setText(user.getName());
        holder.desc.setText(user.getAge() + " " + user.getGender().toString() + " " + user.city);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onMatchClicked(user);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return matches.size();
    }

    public void addMatches(ArrayList<User> users) {
        matches.addAll(users);
        notifyDataSetChanged();
    }

    public void removeMatch(User user) {
        if (matches.contains(user)) {
            matches.remove(user);
            notifyDataSetChanged();
        }
    }

    protected class MatchViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProfilePicture;
        TextView name;
        TextView desc;

        public MatchViewHolder(View itemView) {
            super(itemView);
            ivProfilePicture = (ImageView) itemView.findViewById(R.id.ivMatchImage);
            name = (TextView) itemView.findViewById(R.id.tvMatchName);
            desc = (TextView) itemView.findViewById(R.id.tvMatchDesc);
        }
    }
}
