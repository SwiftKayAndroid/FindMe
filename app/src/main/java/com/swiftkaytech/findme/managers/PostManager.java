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

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.swiftkaytech.findme.data.Post;
import com.swiftkaytech.findme.data.User;
import com.swiftkaytech.findme.utils.VarHolder;
import com.swiftkaytech.findme.views.tagview.Tag;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PostManager {
    public static final String TAG = "FindMe-PostManager";

    private String mUid;
    private ArrayList<Post> mPosts;
    private static PostManager manager = null;

    public static PostManager getInstance(String uid){
        if (manager == null) {
            manager = new PostManager();
        }
        manager.mUid = uid;
        return manager;
    }

    public ArrayList<Post> fetchPosts(Context context){
        try {
            Log.d(TAG, "fetching posts");
            mPosts = new FetchPostsTask(mUid).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (Post post : mPosts) {
            post.setUser(User.createUser(mUid).fetchUser(post.getPostingUsersId()));
            post.setComments(CommentsManager.getInstance(mUid).fetchComments(post.getPostId()));
            TagManager.getInstance(mUid).fetchTags(post.getPostId(), context, post);
        }
        return mPosts;
    }

    private class FetchPostsTask extends AsyncTask<Void,Void,ArrayList<Post>> {
        String uid;

        public FetchPostsTask(String uid) {
            this.uid = uid;
        }

        @Override
        protected ArrayList<Post> doInBackground(Void... params) {
            ConnectionManager connectionManager = new ConnectionManager();
            connectionManager.setMethod(ConnectionManager.POST);
            connectionManager.setUri("getposts.php");
            connectionManager.addParam("uid", uid);
            connectionManager.addParam("lp","0");
            ArrayList<Post> pList = new ArrayList<>();

            try {
                JSONObject jsonObject = new JSONObject(connectionManager.sendHttpRequest());
                JSONArray jsonArray = jsonObject.getJSONArray("posts");

                for(int i = 0; i<jsonArray.length();i++){
                    JSONObject child = jsonArray.getJSONObject(i);
                    Post post = Post.createPost(mUid);
                    post.setPostText(child.getString("post"));
                    post.setPostingUsersId(child.getString("postingusersid"));
                    post.setNumComments(child.getInt("numcomments"));
                    post.setNumLikes(child.getInt("numlikes"));
                    post.setTime(child.getString("time"));
                    post.setPostId(child.getString("postid"));
                    post.setLiked(child.getBoolean("liked"));

                    pList.add(post);
                }
            } catch(JSONException e) {
                e.printStackTrace();
            }
            return pList;
        }
    }


    public ArrayList<Post> getPosts(Context context){
        if (mPosts != null) {
            if (mPosts.size() > 0) {
                return mPosts;
            } else {
                return fetchPosts(context);
            }
        } else {
            return fetchPosts(context);
        }
    }

    public ArrayList<Post> refreshPosts(Context context) {
        mPosts.clear();
        return fetchPosts(context);
    }

    public void clearPosts(){
        mPosts.clear();
        mPosts = null;
    }

    public void likePost(String postid) {
        new PostLike(mUid, postid).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,null);
    }

    public void unLikePost(String postid) {
        new UnlikePost(mUid, postid).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
    }

    private class PostLike extends AsyncTask<String,String,String> {
        String uid;
        String postid;

        public PostLike(String uid, String postid) {
            this.uid = uid;
            this.postid = postid;
        }

        @Override
        protected String doInBackground(String... params) {

            ConnectionManager connectionManager = new ConnectionManager();
            connectionManager.setMethod(ConnectionManager.POST);
            connectionManager.setUri("likepost.php");
            connectionManager.addParam("uid", uid);
            connectionManager.addParam("postid", postid);

            return connectionManager.sendHttpRequest();
        }
    }

    private class UnlikePost extends AsyncTask<String,String,String> {
        String uid;
        String postid;

        public UnlikePost(String uid, String postid) {
            this.uid = uid;
            this.postid = postid;
        }

        @Override
        protected String doInBackground(String... params) {

            ConnectionManager connectionManager = new ConnectionManager();
            connectionManager.setMethod(ConnectionManager.POST);
            connectionManager.setUri("unlikepost.php");
            connectionManager.addParam("uid", uid);
            connectionManager.addParam("postid", postid);

            return connectionManager.sendHttpRequest();
        }
    }
}
