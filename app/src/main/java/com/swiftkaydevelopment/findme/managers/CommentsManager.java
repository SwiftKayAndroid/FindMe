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

package com.swiftkaydevelopment.findme.managers;

import android.content.Context;
import android.os.AsyncTask;

import com.swiftkaydevelopment.findme.data.Comment;
import com.swiftkaydevelopment.findme.data.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class CommentsManager {

    public interface CommentsManagerListener {
        void onCommentsLoaded(List<Comment> comments);
    }
    private static final String TAG = "FindMe-CommentsManager";

    private String mUid;
    private String mPostId;
    private static CommentsManager manager = null;
    private CopyOnWriteArrayList<CommentsManagerListener> mListeners = new CopyOnWriteArrayList<>();
    private static Context mContext;

    public static CommentsManager getInstance(String uid, Context context){
        if (manager == null) {
            manager = new CommentsManager();
        }
        manager.mUid = uid;
        mContext = context;
        return manager;
    }

    /**
     * Adds a Listener to the list of listeners
     *
     * @param listener Listener to add
     */
    public void addListener(CommentsManagerListener listener) {
        mListeners.addIfAbsent(listener);
    }

    /**
     * Removes a Listner from the list of listeners
     *
     * @param listener Listener to remove
     */
    public void removeListener(CommentsManagerListener listener) {
        mListeners.remove(listener);
    }

    public void fetchComments(String postid){
        mPostId = postid;
        new FetchCommentsTask(postid).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
    }

    public void postComment(String postid, String comment) {
        new PostCommentTask(postid, mUid, comment).execute();
    }

    private class PostCommentTask extends AsyncTask<Void, Void, Void> {
        String postid;
        String uid;
        String comment;

        public PostCommentTask(String postid, String uid, String comment) {
            this.postid = postid;
            this.uid = uid;
            this.comment = comment;
        }

        @Override
        protected Void doInBackground(Void... params) {
            ConnectionManager connectionManager = new ConnectionManager();
            connectionManager.setMethod(ConnectionManager.POST);
            connectionManager.setUri("postcomment.php");
            connectionManager.addParam("uid", uid);
            connectionManager.addParam("comment", comment);
            connectionManager.addParam("postid", postid);
            connectionManager.sendHttpRequest();
            return null;
        }
    }

    private class FetchCommentsTask extends AsyncTask<Void, Void, ArrayList<Comment>>{
        String postid;

        public FetchCommentsTask(String postid){
            this.postid = postid;
        }
        @Override
        protected ArrayList<Comment> doInBackground(Void... params) {
            ArrayList<Comment> cList = new ArrayList<>();
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
                    c.setUser(User.createUser().fetchUser(c.getCommentUserId(), mUid));
                    c.setPostId(mPostId);
                    cList.add(c);
                }
            } catch(JSONException e) {
                e.printStackTrace();
            }

            return cList;
        }

        @Override
        protected void onPostExecute(ArrayList<Comment> comments) {
            super.onPostExecute(comments);

            for (CommentsManagerListener l : mListeners) {
                l.onCommentsLoaded(comments);
            }
        }
    }
}
