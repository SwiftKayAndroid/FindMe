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

package com.swiftkaytech.findme.adapters;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.swiftkaytech.findme.R;
import com.swiftkaytech.findme.activity.ProfileActivity;
import com.swiftkaytech.findme.data.Comment;
import com.swiftkaytech.findme.data.Post;
import com.swiftkaytech.findme.fragment.CommentsDialog;
import com.swiftkaytech.findme.managers.PostManager;
import com.swiftkaytech.findme.utils.ImageLoader;
import com.swiftkaytech.findme.views.ExpandableLinearLayout;
import com.swiftkaytech.findme.views.tagview.Tag;
import com.swiftkaytech.findme.views.tagview.TagView;

import java.util.ArrayList;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> implements View.OnClickListener{

    public static final String TAG = "PostAdapter";
    public static final int VIEW_TYPE_HEADER = 0;
    public static final int VIEW_TYPE_CONTENT = 1;

    private Context mContext;
    private ArrayList<Post> mPostList;

    private boolean hasHeader = false;

    public PostAdapter(Context context, ArrayList<Post> plist) {
        mContext = context;
        mPostList = plist;
    }

    @Override
    public int getItemViewType(int position) {

        if (position == 0 && hasHeader) {
            return VIEW_TYPE_HEADER;
        }
        return VIEW_TYPE_CONTENT;
    }

    @Override
    public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_HEADER) {
            view = null;
        } else if (viewType == VIEW_TYPE_CONTENT) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.postitem, parent, false);
        } else {
            view = null;
        }
        return new PostViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(final PostViewHolder holder, final int position) {

        View itemView = holder.itemView;

        if (holder.viewType == VIEW_TYPE_CONTENT) {
            Post post = mPostList.get(position);

            if (post.getNumLikes() == 0) {
                holder.tvNumLikes.setText("No likes");
            } else {
                if (post.getNumLikes() > 1) {
                    holder.tvNumLikes.setText(Integer.toString(post.getNumLikes()) + " people liked this!");
                } else {
                    holder.tvNumLikes.setText(Integer.toString(post.getNumLikes()) + " person liked this!");
                }
            }

            if (post.getNumComments() == 0) {
                holder.tvNumComments.setText("No comments");
            } else if (post.getNumComments() == 1) {
                holder.tvNumComments.setText("1 comment");
            } else {
                holder.tvNumComments.setText(Integer.toString(post.getNumComments()) + " comments");
            }

            holder.tvPost.setText(post.getPostText());
            int distance = (int) post.getUser().getLocation().getDistance();
            String append = "";
            if (distance == 1) {
                append = " mile away";
            } else {
                append = " miles away";
            }
            holder.tvLocation.setText(Integer.toString(distance) + append);
            holder.tvName.setText(post.getUser().getName());
            holder.tvTime.setText(post.getTime());
            ImageLoader imageLoader = new ImageLoader(mContext);
            if (post.getUser().getPropicloc().equals("")) {
                holder.ivProfilePicture.setImageResource(R.drawable.ic_placeholder);
            } else {
                imageLoader.DisplayImage(post.getUser().getPropicloc(), holder.ivProfilePicture, false);
            }
            imageLoader.DisplayImage(post.getPostImage(), holder.ivPostImage, true);
            holder.ivProfilePicture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mContext.startActivity(ProfileActivity.createIntent(mContext, mPostList.get(position).getUser()));
                }
            });
            holder.ivPostLike.setTag(position);
            if (mPostList.get(position).getLiked()) {
                holder.ivPostLike.setImageResource(R.drawable.checkmark_liked);
            } else {
                holder.ivPostLike.setImageResource(R.drawable.checkmark);
            }

            holder.tvNumComments.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //todo: move this to an interface call
                    CommentsDialog dialog = CommentsDialog.newInstance(mPostList.get(position).getPostId());
                    dialog.show(((AppCompatActivity) mContext).getSupportFragmentManager(), CommentsDialog.TAG);
                }
            });

            holder.ivPostToggle.setRotation(0);
            holder.ivPostLike.setOnClickListener(this);
            holder.ivPostToggle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rotate(v);
                    Log.i(TAG, "onclick of item");

                    if (holder.extrasContainer.isExpanded()) {
                        holder.extrasContainer.retract();
                    } else {
                        setExtrasContainer(position, holder);
                    }
                }
            });
        }
        itemView.setTag(position);
    }

    private void setExtrasContainer(int pos, PostAdapter.PostViewHolder holder) {
        Log.i(TAG, "setting extras container");

        if (mPostList.get(pos).getTags() != null) {
            holder.tagView.removeAllTags();
            for (Tag tag : mPostList.get(pos).getTags()) {
                holder.tagView.addTag(tag);
            }
        }
        holder.extrasContainer.expand();
    }

    /**
     * used to rotate the toggle button for the extras layout
     * at bottom of cardview on each post
     * @param v toggle imageview to be rotated
     */
    private void rotate(View v) {
        AnimatorSet set = new AnimatorSet();
        float startRotation = v.getRotation();
        float endRotation = 0;
        if (startRotation == 0) {
            endRotation = 180;
        }
        set.play(ObjectAnimator.ofFloat(v, View.ROTATION, startRotation, endRotation));
        set.setDuration(200);
        set.start();
    }

    @Override
    public int getItemCount() {
        if (hasHeader) {
            return mPostList.size() + 1;
        }
        return mPostList.size();
    }

    /**
     * sets adapter to know that it needs to display a header
     * @param hasHeader boolean true if needs to show header false otherwise
     */
    public void setHasHeader(boolean hasHeader){
        this.hasHeader = hasHeader;
    }

    /**
     * gets whether or not the adapter is set to show a header
     * @return true if set to show header
     */
    public boolean getHasHeader(){
        return hasHeader;
    }

    public class PostViewHolder extends RecyclerView.ViewHolder {

        //contentview
        TextView tvName, tvTime, tvLocation, tvPost, tvNumLikes, tvNumComments;
        ImageView ivProfilePicture, ivPostImage, ivPostLike, ivPostToggle;
        ExpandableLinearLayout extrasContainer;
        TagView tagView;

        public int viewType;

        public PostViewHolder(View itemView, int viewType){
            super(itemView);
            this.viewType = viewType;
            if (itemView != null) {

                if (viewType == VIEW_TYPE_CONTENT) {
                    setUpContent(itemView);
                } else if (viewType == VIEW_TYPE_HEADER) {
                    setUpHeader(itemView);
                }
            } else {
                Log.e(TAG,"attempting to pass a null view to PostViewHolder");
            }
        }

        private void setUpContent(View v) {
            tvName = (TextView) v.findViewById(R.id.postUsername);
            tvTime = (TextView) v.findViewById(R.id.postTime);
            tvLocation = (TextView) v.findViewById(R.id.tvPostLocation);
            tvPost = (TextView) v.findViewById(R.id.tvPostText);
            tvNumLikes = (TextView) v.findViewById(R.id.tvPostNumLikes);
            tvNumComments = (TextView) v.findViewById(R.id.tvPostNumComments);
            ivPostLike = (ImageView) v.findViewById(R.id.ivPostLike);
            ivPostImage = (ImageView) v.findViewById(R.id.postImage);
            ivProfilePicture = (ImageView) v.findViewById(R.id.ivPostProfilePicture);
            extrasContainer = (ExpandableLinearLayout) v.findViewById(R.id.postExtrasContainer);
            ivPostToggle = (ImageView) v.findViewById(R.id.ivPostToggleButton);
            tagView = (TagView) v.findViewById(R.id.postTagView);
            ivPostImage.setVisibility(View.GONE);
        }

        private void setUpHeader(View v) {

        }
    }

    /**
     * empties out list of posts
     */
    public void clearAdapter() {
        mPostList.clear();
    }

    public void addPosts(ArrayList<Post> posts) {
        mPostList.addAll(posts);
        notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.ivPostLike) {
            int pos = (Integer) v.getTag();
            Post p = mPostList.get(pos);
            if (!p.getLiked()) {
                PostManager.getInstance(p.getUid(), mContext).likePost(p.getPostId());
                ((ImageView) v).setImageResource(R.drawable.checkmark_liked);
            } else {
                PostManager.getInstance(p.getUid(), mContext).unLikePost(p.getPostId());
                ((ImageView) v).setImageResource(R.drawable.checkmark);
            }
            p.setLiked(!p.getLiked());
        }
    }
}
