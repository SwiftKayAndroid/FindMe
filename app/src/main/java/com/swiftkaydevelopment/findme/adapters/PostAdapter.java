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

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.swiftkaydevelopment.findme.data.User;
import com.swiftkaydevelopment.findme.utils.ImageLoader;
import com.swiftkaydevelopment.findme.views.CircleTransform;
import com.swiftkaydevelopment.findme.views.ExpandableLinearLayout;
import com.swiftkaydevelopment.findme.views.tagview.TagView;
import com.swiftkaydevelopment.findme.R;
import com.swiftkaydevelopment.findme.activity.ProfileActivity;
import com.swiftkaydevelopment.findme.data.Post;
import com.swiftkaydevelopment.findme.managers.PostManager;
import com.swiftkaydevelopment.findme.views.tagview.Tag;

import java.util.ArrayList;
import java.util.Map;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> implements View.OnClickListener{
    public interface PostAdapterListener{
        void onCommentsClicked(Post post);
        void onImageClicked(Post post);
        void onEditClicked();
    }

    public static final String TAG = "PostAdapter";
    public static final int VIEW_TYPE_HEADER = 0;
    public static final int VIEW_TYPE_CONTENT = 1;

    private Context mContext;
    private ArrayList<Post> mPostList;

    private boolean hasHeader = false;
    private PostAdapterListener mListener;
    public User user;
    private boolean isUser;

    public PostAdapter(Context context, ArrayList<Post> plist, User user, boolean isProfile, boolean isUser) {
        mContext = context;
        mPostList = plist;
        this.user = user;
        this.hasHeader = isProfile;
        this.isUser = isUser;
    }

    public void setPostAdapterListener(PostAdapterListener listener) {
        mListener = listener;
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
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_header, parent, false);
        } else if (viewType == VIEW_TYPE_CONTENT) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.postitem, parent, false);
        } else {
            view = null;
        }
        return new PostViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(final PostViewHolder holder, final int pos) {

        View itemView = holder.itemView;
        final int position;
        if (hasHeader) {
            position = pos - 1;
        } else {
            position = pos;
        }

        if (holder.viewType == VIEW_TYPE_CONTENT) {
            Log.e(TAG, "position: " + position);
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
            holder.tvName.setText(post.getUser().getFirstname() + " " + post.getUser().getLastname());
            holder.tvTime.setText(post.getTime());
                Picasso.with(mContext)
                        .load(!TextUtils.isEmpty(post.getUser().getPropicloc()) ? post.getUser().getPropicloc() : "empty")
                        .transform(new CircleTransform())
                        .resize(100, 100)
                        .error(R.drawable.ic_placeholder)
                        .into(holder.ivProfilePicture);

            holder.ivProfilePicture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!mPostList.get(position).getUser().getOuid().equals(user.getOuid())) {
                        mContext.startActivity(ProfileActivity.createIntent(mContext, mPostList.get(position).getUser()));
                    }
                }
            });
            holder.ivPostLike.setTag(position);
            if (mPostList.get(position).getLiked()) {
                holder.ivPostLike.setImageResource(R.drawable.checkmark_liked);
            } else {
                holder.ivPostLike.setImageResource(R.drawable.checkmark);
            }

            if (!mPostList.get(position).getPostImage().equals("")) {
                Log.e(TAG, "showing post image");
                holder.ivPostImage.setVisibility(View.VISIBLE);
                holder.ivPostImage.setImageResource(R.drawable.ic_placeholder);
//                imageLoader.DisplayImage(mPostList.get(position).getPostImage(), holder.ivPostImage, true);
                Picasso.with(mContext).load(mPostList.get(position).getPostImage()).into(holder.ivPostImage);
                holder.ivPostImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mListener != null) {
                            mListener.onImageClicked(mPostList.get(position));
                        }
                    }
                });
            } else {
                holder.ivPostImage.setVisibility(View.GONE);
            }

            holder.tvNumComments.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.onCommentsClicked(mPostList.get(position));
                    }
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
        } else {
            holder.orientation.setText(user.getOrientation().toString());
            holder.aboutMe.setText(user.getAboutMe());
            holder.location.setText(user.getLocation().getCity());
            holder.gender.setText(user.getGender().toString());
            holder.age.setText(Integer.toString(user.getAge()));
            if (isUser) {
                holder.mEditProfile.setVisibility(View.VISIBLE);
                holder.mEditProfile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mListener != null) {
                            mListener.onEditClicked();
                        }
                    }
                });
            } else {
                holder.mEditProfile.setVisibility(View.GONE);
            }

            StringBuilder sb = new StringBuilder();
            for (Map.Entry entry : user.getSearchingFor().entrySet()) {
                if (entry.getValue().equals("yes")) {
                    sb.append(entry.getKey() + " ");
                }
            }
            holder.lookingfor.setText(sb.toString());
            holder.status.setText(user.mRelationshipStatus);

            holder.hasKids.setText(user.hasKids);
            holder.wantsKids.setText(user.wantsKids);
            holder.profession.setText(user.profession);
            holder.school.setText(user.school);
            holder.weed.setText(user.weed);
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

        //header
        TextView aboutMe, age, gender, orientation, location, status, lookingfor;
        TextView hasKids, wantsKids, profession, school, weed;
        ImageView mEditProfile;

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
        }

        private void setUpHeader(View v) {
            age = (TextView) v.findViewById(R.id.profileHeaderAge);
            location = (TextView) v.findViewById(R.id.profileHeaderLocation);
            aboutMe = (TextView) v.findViewById(R.id.profileHeaderAboutMe);
            gender = (TextView) v.findViewById(R.id.profileHeaderGender);
            orientation = (TextView) v.findViewById(R.id.profileHeaderOrientation);
            status = (TextView) v.findViewById(R.id.profileHeaderStatus);
            lookingfor = (TextView) v.findViewById(R.id.profileHeaderLookingFor);
            mEditProfile = (ImageView) v.findViewById(R.id.ivProfileEditProfile);

            hasKids = (TextView) v.findViewById(R.id.profileHeaderHasKids);
            wantsKids = (TextView) v.findViewById(R.id.profileHeaderWantsKids);
            profession = (TextView) v.findViewById(R.id.profileHeaderProfession);
            school = (TextView) v.findViewById(R.id.profileHeaderSchool);
            weed = (TextView) v.findViewById(R.id.profileHeaderWeed);
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

    public ArrayList<Post> getPosts() {
        return new ArrayList<>(mPostList);
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
