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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.swiftkaydevelopment.findme.R;
import com.swiftkaydevelopment.findme.adapters.CommentAdapter;
import com.swiftkaydevelopment.findme.data.Comment;
import com.swiftkaydevelopment.findme.managers.CommentsManager;
import com.swiftkaydevelopment.findme.managers.UserManager;

import java.util.ArrayList;
import java.util.List;

public class CommentsDialog extends AppCompatDialogFragment implements CommentsManager.CommentsManagerListener,
        View.OnClickListener {
    public static final String TAG = "CommentsDialog";
    private static final String ARG_POSTID = "ARG_POSTID";
    private static final String ARG_COMMENTS = "ARG_COMMENTS";
    private static final String ARG_UID = "ARG_UID";

    private String mUid;
    private String mPostid;
    private ArrayList<Comment> mComments = new ArrayList<>();
    private ImageView ivPostComment;
    private EditText etComment;
    private  ListView lv;
    private ProgressBar mProgressBar;

    private CommentAdapter mAdapter;

    /**
     * Factory method to instatiate the comment dialog
     *
     * @param postid Post id
     * @param uid User's id
     * @return new instance of the CommentDialog
     */
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
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View layout = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.commentsfrag, null);

        etComment = (EditText) layout.findViewById(R.id.etcommentonpost);
        ivPostComment = (ImageView) layout.findViewById(R.id.ivCommentSend);
        lv = (ListView) layout.findViewById(R.id.lvcommentspop);
        mProgressBar = (ProgressBar) layout.findViewById(R.id.progressBar);

        Toolbar toolbar = (Toolbar) layout.findViewById(R.id.baseActivityToolbar);
        toolbar.setNavigationIcon(R.mipmap.ic_arrow_back_white_24dp);
        toolbar.setTitle("Comments");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return layout;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (savedInstanceState != null) {
            mProgressBar.setVisibility(View.GONE);
            mComments = (ArrayList) savedInstanceState.getSerializable(ARG_COMMENTS);
        } else {
            mProgressBar.setVisibility(View.VISIBLE);
            CommentsManager.getInstance(mUid, getActivity()).fetchComments(mPostid);
        }

        if (mAdapter == null) {
            mAdapter = new CommentAdapter(getActivity(), mComments, mPostid);
        }
        lv.setAdapter(mAdapter);

        etComment.setImeOptions(etComment.getImeOptions() | EditorInfo.IME_FLAG_NO_FULLSCREEN);
        ivPostComment.setOnClickListener(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(ARG_UID, mUid);
        outState.putString(ARG_POSTID, mPostid);
        outState.putSerializable(ARG_COMMENTS, mComments);
        super.onSaveInstanceState(outState);
    }

    @Override
    @NonNull
    public AppCompatDialog onCreateDialog(Bundle savedInstanceState) {

        AppCompatDialog dialog = new AppCompatDialog(getActivity());
        dialog.supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
                | WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        return dialog;
    }

    @Override
    public void onResume() {
        super.onResume();
        CommentsManager.getInstance(mUid, getActivity()).addListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        CommentsManager.getInstance(mUid, getActivity()).removeListener(this);
    }

    @Override
    public void onCommentsLoaded(List<Comment> comments) {
        mProgressBar.setVisibility(View.GONE);
        mAdapter.addComments(comments);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == ivPostComment.getId()) {
            String comment = etComment.getText().toString();
            if (!TextUtils.isEmpty(comment)) {
                CommentsManager.getInstance(mUid, getActivity()).postComment(mPostid, comment);
                Comment c = Comment.createComment(mUid);
                c.setComment(comment);
                c.setUser(UserManager.getInstance(mUid).me());
                c.setTime("Just Now");
                c.setPostId(mPostid);
                mAdapter.addComment(c);
                etComment.setText("");
            }
        }
    }
}
