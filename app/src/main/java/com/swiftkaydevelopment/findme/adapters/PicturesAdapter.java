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

import com.squareup.picasso.Picasso;
import com.swiftkaydevelopment.findme.R;
import com.swiftkaydevelopment.findme.data.AppConstants;
import com.swiftkaydevelopment.findme.data.Post;

import java.util.ArrayList;

public class PicturesAdapter extends RecyclerView.Adapter<PicturesAdapter.PicturesViewHolder>{

    public interface PicturesAdapterListener {
        void onImageClicked(Post post);
        void onLastImage(Post post);
    }

    private static final String TAG = "PicturesAdapter";

    ArrayList<Post> mPosts;
    Context mContext;

    private PicturesAdapterListener mListener;

    public PicturesAdapter(ArrayList<Post> mPosts, Context mContext) {
        this.mPosts = mPosts;
        this.mContext = mContext;
    }

    public void setPicturesAdapterListener(PicturesAdapterListener listener) {
        mListener = listener;
    }

    @Override
    public PicturesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.sgvitem, parent, false);
        return new PicturesViewHolder(layout);
    }

    @Override
    public void onBindViewHolder(PicturesViewHolder holder, final int position) {
        ImageView iv = (ImageView) holder.itemView;
        Picasso.with(mContext)
                .load(mPosts.get(position).getPostImage())
                .into(iv);

        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onImageClicked(mPosts.get(position));
                }
            }
        });

        if (position == mPosts.size() - 1 && (mPosts.size() % AppConstants.BASE_PAGE_SIZE == 0)) {
            if (mListener != null) {
                mListener.onLastImage(mPosts.get(mPosts.size() - 1));
            }
        }
    }

    public void addPictures(ArrayList<Post> posts) {
        mPosts.addAll(posts);
        notifyDataSetChanged();
    }

    public void removePicture(Post post) {
        if (mPosts.contains(post)) {
            mPosts.remove(post);
            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    protected class PicturesViewHolder extends RecyclerView.ViewHolder {
        public PicturesViewHolder(View itemView) {
            super(itemView);
        }
    }
}
