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
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.swiftkaytech.findme.R;
import com.swiftkaytech.findme.data.Comment;
import com.swiftkaytech.findme.utils.ImageLoader;
import com.swiftkaytech.findme.views.ExpandableLinearLayout;
import com.swiftkaytech.findme.views.tagview.TagView;

import java.util.ArrayList;

public class CommentAdapter extends BaseAdapter implements View.OnClickListener{

    public static final String TAG = "CommentAdapter";

    private Context mContext;
    private ArrayList<Comment> mCommentList;
    private String mPostid;

    public CommentAdapter(Context mContext, ArrayList<Comment> cList, String postid) {
        this.mContext = mContext;
        this.mCommentList = cList;
        this.mPostid = postid;
    }

    @Override
    public int getCount() {
        return mCommentList.size();
    }

    @Override
    public Object getItem(int position) {
        return mCommentList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View row, ViewGroup parent) {

        row = ((LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.commentrow, null);
        ViewHolder holder = new ViewHolder();

        holder.tvComment = (TextView) row.findViewById(R.id.tvcomment);
        holder.tvCommentName = (TextView) row.findViewById(R.id.tvcommentusername);
        holder.tvCommentTime = (TextView) row.findViewById(R.id.tvcommenttime);
        holder.ivCommentProfilePicture = (ImageView) row.findViewById(R.id.ivcommentpropic);

        holder.tvCommentTime.setText(mCommentList.get(position).getTime());
        holder.tvComment.setText(mCommentList.get(position).getComment());
        holder.tvCommentName.setText(mCommentList.get(position).getUser().getName());
        ImageLoader imageLoader = new ImageLoader(mContext);
        imageLoader.DisplayImage(mCommentList.get(position).getUser().getPropicloc(),holder.ivCommentProfilePicture,false);
        return row;
    }

    public class ViewHolder{
        TextView tvCommentName, tvComment, tvCommentTime;
        ImageView ivCommentProfilePicture;
    }

    /**
     * sets the post id for these comments
     * @param postid  post id for these comments
     */
    public void setPostId(String postid) {
        mPostid = postid;
    }

    /**
     * gets the post id for these comments
     * @return post id for these comments
     */
    public String getPostId(){
        return mPostid;
    }

    @Override
    public void onClick(View v) {

    }
}
