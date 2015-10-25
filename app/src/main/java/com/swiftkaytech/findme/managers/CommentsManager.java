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

package com.swiftkaytech.findme.managers;

import android.os.AsyncTask;

import com.swiftkaytech.findme.data.Comment;
import com.swiftkaytech.findme.data.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CommentsManager {
    private static final String TAG = "FindMe-CommentsManager";

    private String mUid;
    private String mPostId;
    private static CommentsManager manager = null;

    public static CommentsManager getInstance(String uid){
        if (manager == null) {
            manager = new CommentsManager();
        }
        manager.mUid = uid;
        return manager;
    }

    public List<Comment> fetchComments(String postid){
        List<Comment> cList = new ArrayList<>();
        mPostId = postid;
        try {
            cList = new FetchCommentsTask(postid).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (Comment comment : cList) {
            comment.setUser(User.createUser(mUid).fetchUser(comment.getCommentUserId()));
        }
        return cList;
    }

    private class FetchCommentsTask extends AsyncTask<Void,Void,List<Comment>>{
        String postid;

        public FetchCommentsTask(String postid){
            this.postid = postid;
        }
        @Override
        protected List<Comment> doInBackground(Void... params) {
            List<Comment> cList = new ArrayList<>();
            ConnectionManager connectionManager = new ConnectionManager();
            connectionManager.setMethod(ConnectionManager.POST);
            connectionManager.setUri("getcomments.php");
            connectionManager.addParam("postid", postid);
            connectionManager.addParam("lastpost","0");

            try {
                JSONObject jsonObject = new JSONObject(connectionManager.sendHttpRequest());
                JSONArray jsonArray = jsonObject.getJSONArray("comments");

                for(int i = 0; i<jsonArray.length();i++){
                    JSONObject child = jsonArray.getJSONObject(i);
                    Comment c = Comment.createComment(mUid);
                    c.setComment(child.getString("comment"));
                    c.setTime(child.getString("time"));
                    c.setCommentUserId(child.getString("commentusersid"));
                    c.setPostId(mPostId);
                    cList.add(c);
                }
            } catch(JSONException e) {
                e.printStackTrace();
            }

            return cList;
        }
    }
}
