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

package com.swiftkaytech.findme.fragment;

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

import com.swiftkaytech.findme.R;
import com.swiftkaytech.findme.adapters.CommentAdapter;
import com.swiftkaytech.findme.data.Comment;
import com.swiftkaytech.findme.managers.CommentsManager;

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
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        ListView lv = (ListView) layout.findViewById(R.id.lvcommentspop);
        lv.setAdapter(new CommentAdapter(getActivity(), mComments, mPostid));
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
