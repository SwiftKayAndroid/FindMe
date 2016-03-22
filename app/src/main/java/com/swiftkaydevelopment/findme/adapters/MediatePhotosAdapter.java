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
import android.widget.ImageButton;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.swiftkaydevelopment.findme.R;
import com.swiftkaydevelopment.findme.data.MediationPhoto;

import java.util.ArrayList;
import java.util.List;

public class MediatePhotosAdapter extends RecyclerView.Adapter<MediatePhotosAdapter.MediatePhotosViewHolder> {
    public static final String TAG = "MediatePhotosAdapter";

    public interface MediatePhotosAdapterListener {
        void onPhotoApproved(MediationPhoto photo);
        void onPhotoDenied(MediationPhoto photo);
    }

    private List<MediationPhoto> mItems = new ArrayList<>();
    private MediatePhotosAdapterListener mListener;
    private Context mContext;

    public MediatePhotosAdapter(List<MediationPhoto> mItems, MediatePhotosAdapterListener mListener, Context mContext) {
        this.mItems = mItems;
        this.mListener = mListener;
        this.mContext = mContext;
    }

    @Override
    public MediatePhotosViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_mediation_item, parent, false);
        return new MediatePhotosViewHolder(layout);
    }

    @Override
    public void onBindViewHolder(MediatePhotosViewHolder holder, final int position) {

        final MediationPhoto item = mItems.get(position);

        holder.ibDeny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onPhotoDenied(item);
                    mItems.remove(item);
                    notifyItemRemoved(position);
                }
            }
        });

        holder.ibAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onPhotoApproved(item);
                    mItems.remove(item);
                    notifyItemRemoved(position);
                }
            }
        });

        Picasso.with(mContext)
                .load(item.imgLocation)
                .into(holder.ivPhoto);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public void addItems(ArrayList<MediationPhoto> photos) {
        mItems.addAll(photos);
        notifyDataSetChanged();
    }

    protected class MediatePhotosViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPhoto;
        ImageButton ibAccept, ibDeny;

        public MediatePhotosViewHolder(View itemView) {
            super(itemView);

            ivPhoto = (ImageView) itemView.findViewById(R.id.ivPhototoApprove);
            ibAccept = (ImageButton) itemView.findViewById(R.id.ibApprove);
            ibDeny = (ImageButton) itemView.findViewById(R.id.ibDeny);
        }
    }
}
