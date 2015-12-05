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

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.swiftkaydevelopment.findme.adapters.CommentAdapter;
import com.swiftkaydevelopment.findme.data.Comment;
import com.swiftkaydevelopment.findme.managers.CommentsManager;
import com.swiftkaydevelopment.findme.managers.UserManager;
import com.swiftkaydevelopment.findme.R;

import java.util.ArrayList;

public class CommentsDialog extends AppCompatDialogFragment {
    public static final String TAG = "CommentsDialog";
    private static final String ARG_POSTID = "ARG_POSTID";
    private static final String ARG_COMMENTS = "ARG_COMMENTS";
    private static final String ARG_UID = "ARG_UID";

    private static String mUid;
    private String mPostid;
    private ArrayList<Comment> mComments;
    private ImageView ivPostComment;
    private ImageView emptyView;
    private EditText etComment;

    private CommentAdapter mAdapter;


    public static CommentsDialog newInstance(String postid, String uid) {
        CommentsDialog frag = new CommentsDialog();
        Bundle b = new Bundle();
        b.putString(ARG_POSTID, postid);
        b.putString(ARG_UID, uid);
        frag.setArguments(b);
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mPostid = savedInstanceState.getString(ARG_POSTID);
            mComments = (ArrayList) savedInstanceState.getSerializable(ARG_COMMENTS);
            mUid = savedInstanceState.getString(ARG_UID);
        } else {
            if (getArguments() != null) {
                mPostid = getArguments().getString(ARG_POSTID);
                mUid = getArguments().getString(ARG_UID);
            }
            if (mPostid != null && !(mPostid.isEmpty())) {
                mComments = CommentsManager.getInstance(mUid, getActivity()).fetchComments(mPostid);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(ARG_UID, mUid);
        outState.putString(ARG_POSTID, mPostid);
        outState.putSerializable(ARG_COMMENTS, mComments);
        super.onSaveInstanceState(outState);
    }

    @Override
    public AppCompatDialog onCreateDialog(Bundle savedInstanceState) {

        AppCompatDialog dialog = new AppCompatDialog(getActivity());
        View layout = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.commentsfrag, null);

        Toolbar toolbar = (Toolbar) layout.findViewById(R.id.baseActivityToolbar);
        toolbar.setNavigationIcon(R.mipmap.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        ListView lv = (ListView) layout.findViewById(R.id.lvcommentspop);
        if (mAdapter == null) {
            mAdapter = new CommentAdapter(getActivity(), mComments, mPostid);
        }
        lv.setAdapter(mAdapter);
        emptyView = (ImageView) layout.findViewById(R.id.commentsEmptyView);
        if (mComments.size() == 0) {
            emptyView.setVisibility(View.VISIBLE);
        } else {
            emptyView.setVisibility(View.GONE);
        }
        etComment = (EditText) layout.findViewById(R.id.etcommentonpost);
        ivPostComment = (ImageView) layout.findViewById(R.id.ivCommentSend);
        ivPostComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comment = etComment.getText().toString();
                if (!comment.equals("")) {
                    CommentsManager.getInstance(mUid, getActivity()).postComment(mPostid, comment);
                    Comment c = Comment.createComment(mUid);
                    c.setComment(comment);
                    c.setUser(UserManager.getInstance(mUid, getActivity()).me());
                    c.setTime("Just Now");
                    c.setPostId(mPostid);
                    mAdapter.addComment(c);
                    etComment.setText("");
                }
            }
        });

        dialog.supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dialog.setContentView(layout);

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        return dialog;
    }
}
