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
import android.view.Window;
import android.view.WindowManager;
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

    private static String mUid;
    private String mPostid;
    private ArrayList<Comment> mComments;


    public static CommentsDialog newInstance(String postid) {
        CommentsDialog frag = new CommentsDialog();
        Bundle b = new Bundle();
        b.putString(ARG_POSTID, postid);
        frag.setArguments(b);
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mPostid = savedInstanceState.getString(ARG_POSTID);
            mComments = (ArrayList) savedInstanceState.getSerializable(ARG_COMMENTS);
        } else {
            if (getArguments() != null) {
                mPostid = getArguments().getString(ARG_POSTID);
            }
            if (mPostid != null && !(mPostid.isEmpty())) {
                mComments = CommentsManager.getInstance(mUid, getActivity()).fetchComments(mPostid);
            }
        }


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

        dialog.supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dialog.setContentView(layout);

//        Display display = getActivity().getWindowManager().getDefaultDisplay();
//        Point size = new Point();
//        display.getSize(size);
//        int width = ((int) (size.x / 1.1));
//        int height = ((int) (size.y / 1.1));
//        dialog.getWindow().setLayout(width, height);
        return dialog;
    }
}
