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

package com.swiftkaydevelopment.findme.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.swiftkaydevelopment.findme.R;
import com.swiftkaydevelopment.findme.activity.ProfileActivity;
import com.swiftkaydevelopment.findme.adapters.CommentAdapter;
import com.swiftkaydevelopment.findme.data.Comment;
import com.swiftkaydevelopment.findme.data.Post;
import com.swiftkaydevelopment.findme.managers.CommentsManager;
import com.swiftkaydevelopment.findme.managers.PostManager;
import com.swiftkaydevelopment.findme.managers.UserManager;
import com.swiftkaydevelopment.findme.views.CircleTransform;

import java.util.ArrayList;
import java.util.List;

public class SinglePostFragment extends BaseFragment implements PostManager.PostsListener,
        CommentsManager.CommentsManagerListener, View.OnClickListener {
    public static final String TAG = "SinglePostFragment";

    private static final String ARG_COMMENTS = "ARG_COMMENTS";
    private static final String ARG_POST = "ARG_POST";
    private static final String ARG_POST_ID = "ARG_POST_ID";
    private static final String ARG_COMMENTS_FETCHED = "ARG_CMFETCHED";

    private Post mPost;
    private String mPostid;

    private ArrayList<Comment> mComments = new ArrayList<>();

    TextView tvName, tvTime, tvPost, tvNumLikes, tvNumComments;
    ImageView ivProfilePicture, ivPostImage;
    ImageButton ibLikePost, ibDislikePost, ibComment;
    private ImageView ivPostComment;
    private ImageView emptyView;
    private EditText etComment;
    private ListView lv;

    private CommentAdapter mAdapter;
    private boolean commentsFetched = false;

    public static SinglePostFragment newInstance (String uid, String postid) {
        SinglePostFragment frag = new SinglePostFragment();
        Bundle b = new Bundle();
        b.putString(ARG_UID, uid);
        b.putString(ARG_POST_ID, postid);
        frag.setArguments(b);
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            uid = savedInstanceState.getString(ARG_UID);
            mPost = (Post) savedInstanceState.getSerializable(ARG_POST);
            mComments = (ArrayList) savedInstanceState.getSerializable(ARG_COMMENTS);
            mPostid = savedInstanceState.getString(ARG_POST_ID);
        } else {
            if (getArguments() != null) {
                uid = getArguments().getString(ARG_UID);
                mPostid = getArguments().getString(ARG_POST_ID);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.single_post_item,container, false);

        tvName = (TextView) layout.findViewById(R.id.postUsername);
        tvTime = (TextView) layout.findViewById(R.id.postTime);
        tvPost = (TextView) layout.findViewById(R.id.tvPostText);
        tvNumLikes = (TextView) layout.findViewById(R.id.tvPostNumLikes);
        tvNumComments = (TextView) layout.findViewById(R.id.tvPostNumComments);
        ivPostImage = (ImageView) layout.findViewById(R.id.postImage);
        ivProfilePicture = (ImageView) layout.findViewById(R.id.ivPostProfilePicture);
        ibLikePost = (ImageButton) layout.findViewById(R.id.ibLikePost);
        ibDislikePost = (ImageButton) layout.findViewById(R.id.ibDislikePost);
        ibComment = (ImageButton) layout.findViewById(R.id.ibComment);
        lv = (ListView) layout.findViewById(R.id.lvcommentspop);
        emptyView = (ImageView) layout.findViewById(R.id.commentsEmptyView);
        etComment = (EditText) layout.findViewById(R.id.etcommentonpost);
        ivPostComment = (ImageView) layout.findViewById(R.id.ivCommentSend);

        return layout;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (savedInstanceState != null) {
            uid = savedInstanceState.getString(ARG_UID);
            commentsFetched = savedInstanceState.getBoolean(ARG_COMMENTS_FETCHED);
            mPost = (Post) savedInstanceState.getSerializable(ARG_POST);
            mComments = (ArrayList) savedInstanceState.getSerializable(ARG_COMMENTS);
            mPostid = savedInstanceState.getString(ARG_POST_ID);
            if (mPost != null) {
                initializePost();
            }
        } else {
            PostManager.getInstance().getSinglePost(mPostid, uid);
        }
        emptyView.setVisibility(View.GONE);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(ARG_UID, uid);
        outState.putSerializable(ARG_POST, mPost);
        outState.putSerializable(ARG_COMMENTS, mComments);
        outState.putString(ARG_POST_ID, mPostid);
        outState.putBoolean(ARG_COMMENTS_FETCHED, commentsFetched);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        PostManager.getInstance().addPostListener(this);
        CommentsManager.getInstance(uid, getActivity()).addListener(this);

    }

    @Override
    public void onPause() {
        super.onPause();
        PostManager.getInstance().removeListener(this);
        CommentsManager.getInstance(uid, getActivity()).removeListener(this);
    }

    /**
     * Initializes the views based on the post data
     *
     */
    private void initializePost() {

        if (mPost.numReactions == 0) {
            tvNumLikes.setText("");
        } else {
            if (mPost.numReactions > 1) {
                tvNumLikes.setText(Integer.toString(mPost.numReactions) + " reactions");
            } else {
                tvNumLikes.setText(Integer.toString(mPost.numReactions) + " reaction");
            }
        }

        if (mPost.getNumComments() == 0) {
            tvNumComments.setText("No comments");
        } else if (mPost.getNumComments() == 1) {
            tvNumComments.setText("1 comment");
        } else {
            tvNumComments.setText(Integer.toString(mPost.getNumComments()) + " comments");
        }

        tvPost.setText(mPost.getPostText());

        tvName.setText(mPost.getUser().getName());
        tvTime.setText(mPost.getTime());

        if (mPost.getUser().getPropicloc().equals("")) {
            Picasso.with(getActivity())
                    .load(R.drawable.ic_placeholder)
                    .transform(new CircleTransform(getActivity()))
                    .into(ivProfilePicture);
        } else {
            Picasso.with(getActivity())
                    .load(mPost.getUser().getPropicloc())
                    .transform(new CircleTransform(getActivity()))
                    .into(ivProfilePicture);
        }
        ivProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mPost.getUser().getOuid().equals(uid)) {
                    getActivity().startActivity(ProfileActivity.createIntent(getActivity(), mPost.getUser()));
                }
            }
        });

        ibLikePost.setOnClickListener(this);
        ibDislikePost.setOnClickListener(this);

        if (mPost.disliked) {
            ibDislikePost.setColorFilter(getActivity().getResources().getColor(R.color.base_green));
        } else {
            ibDislikePost.setColorFilter(getActivity().getResources().getColor(R.color.grayicon));
        }

        if (!mPost.getPostImage().equals("")) {
            Log.e(TAG, "showing post image");
            ivPostImage.setVisibility(View.VISIBLE);
            ivPostImage.setImageResource(R.drawable.ic_placeholder);

            Picasso.with(getActivity())
                    .load(mPost.getPostImage())
                    .into(ivPostImage);

            ivPostImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FullImageFragment fullImageFragment = FullImageFragment.newInstance(uid, mPost, mPost.getPostingUsersId().equals(uid));
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(android.R.id.content, fullImageFragment, FullImageFragment.TAG)
                            .addToBackStack(null)
                            .commit();
                }
            });
        } else {
            ivPostImage.setVisibility(View.GONE);
        }


        if (!commentsFetched) {
            CommentsManager.getInstance(uid, getActivity()).fetchComments(mPostid);
        }

        if (mAdapter == null) {
            mAdapter = new CommentAdapter(getActivity(), mComments, mPost.getPostId());
        }

        lv.setAdapter(mAdapter);

        ivPostComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comment = etComment.getText().toString();
                if (!comment.equals("")) {
                    CommentsManager.getInstance(uid, getActivity()).postComment(mPost.getPostId(), comment);
                    Comment c = Comment.createComment(uid);
                    c.setComment(comment);
                    c.setUser(UserManager.getInstance(uid).me());
                    c.setTime("Just Now");
                    c.setPostId(mPost.getPostId());
                    mAdapter.addComment(c);
                    etComment.setText("");
                }
            }
        });
    }

    @Override
    public void onCommentsLoaded(List<Comment> comments) {
        commentsFetched = true;
        mAdapter.addComments(comments);
    }

    @Override
    public void onProfilePostsRetrieved(ArrayList<Post> posts) {}

    @Override
    public void onSinglePostRetrieved(Post post) {
        mPost = post;
        initializePost();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ibLikePost) {
            if (!mPost.getLiked()) {
                mPost.setLiked(true);
                PostManager.getInstance().likePost(uid, mPost.getPostId());
                ((ImageButton) v).setColorFilter(getActivity().getResources().getColor(R.color.base_green));
            } else {
                mPost.setLiked(false);
                PostManager.getInstance().unLikePost(uid, mPost.getPostId());
                ((ImageButton) v).setColorFilter(getActivity().getResources().getColor(R.color.grayicon));
            }
        } else if (v.getId() == R.id.ibDislikePost) {
            if (!mPost.disliked) {
                mPost.disliked = true;
                PostManager.getInstance().dislikePost(mPost, uid);
                ((ImageButton) v).setColorFilter(getActivity().getResources().getColor(R.color.base_green));
            } else {
                mPost.disliked = false;
                PostManager.getInstance().unDislikePost(mPost, uid);
                ((ImageButton) v).setColorFilter(getActivity().getResources().getColor(R.color.grayicon));
            }
        }
    }
}
