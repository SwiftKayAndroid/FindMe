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

import com.squareup.picasso.Picasso;
import com.swiftkaydevelopment.findme.R;
import com.swiftkaydevelopment.findme.data.User;
import com.swiftkaydevelopment.findme.views.CircleTransform;

import java.util.ArrayList;

public class ProfileViewsAdapter extends RecyclerView.Adapter<ProfileViewsAdapter.ProfileViewViewHolder>{

    public interface ProfileViewsAdapterListener {
        void onProfileViewClicked(User user);
    }
    private static final String TAG = "ProfileViewsAdapter";

    private ProfileViewsAdapterListener mListener;

    private ArrayList<User> mUsers;
    private Context context;

    public ProfileViewsAdapter(Context context, ArrayList<User> users) {
        this.mUsers = users;
        this.context = context;
    }

    public void setProfileViewsAdapterListener(ProfileViewsAdapterListener listener) {
        mListener = listener;
    }

    @Override
    public ProfileViewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_views_item, parent, false);
        return new ProfileViewViewHolder(layout);
    }

    @Override
    public void onBindViewHolder(ProfileViewViewHolder holder, int position) {
        final User user = mUsers.get(position);
        if (!user.getPropicloc().equals("")) {
            Picasso.with(context)
                    .load(user.getPropicloc())
                    .transform(new CircleTransform(context))
                    .into(holder.profilePicture);
        } else {
            Picasso.with(context)
                    .load(R.drawable.ic_placeholder)
                    .transform(new CircleTransform(context))
                    .into(holder.profilePicture);
        }

        holder.name.setText(user.getFirstname() + " " + user.getLastname());
        holder.desc.setText(user.getAge() + "/" + user.getLocation().getCity() + "/" + user.getGender().toString());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onProfileViewClicked(user);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public void addFirstUsers(ArrayList<User> users) {
        this.mUsers.addAll(users);
        notifyDataSetChanged();
    }

    protected class ProfileViewViewHolder extends RecyclerView.ViewHolder {
        ImageView profilePicture;
        TextView  name;
        TextView desc;
        public ProfileViewViewHolder(View itemView) {
            super(itemView);
            profilePicture = (ImageView) itemView.findViewById(R.id.ivProfileViewimage);
            name = (TextView) itemView.findViewById(R.id.tvProfileViewName);
            desc = (TextView) itemView.findViewById(R.id.tvProfileViewDesc);
        }
    }

}
