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
import com.swiftkaytech.findme.data.Post;
import com.swiftkaytech.findme.data.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PostManager {

    public static final String TAG = "FindMe-PostManager";

    private String mUid;
    private List<Post> mPosts;
    private static PostManager manager = null;

    public static PostManager getInstance(String uid){
        if (manager == null) {
            manager = new PostManager();
        }
        manager.mUid = uid;
        return manager;
    }

    private void onPostFetched(Post post){

    }

    public List<Post> fetchPosts(String postid){
        Post p = Post.createPost(mUid).fetchPost(postid);
        onPostFetched(p);

        try {
            mPosts = new FetchPostsTask(mUid).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (Post post : mPosts) {
            post.setUser(User.createUser(mUid).fetchUser(post.getPostingUsersId()));
            post.setComments(CommentsManager.getInstance(mUid).fetchComments(post.getPostId()));
        }
        return mPosts;
    }

    private class FetchPostsTask extends AsyncTask<Void,Void,List<Post>> {
        String uid;

        public FetchPostsTask(String uid) {
            this.uid = uid;
        }

        @Override
        protected List<Post> doInBackground(Void... params) {
            ConnectionManager connectionManager = new ConnectionManager();
            connectionManager.setMethod(ConnectionManager.POST);
            connectionManager.setUri("getposts.php");
            connectionManager.addParam("postid", uid);
            connectionManager.addParam("lastpost","0");
            List<Post> pList = new ArrayList<>();

            try {
                JSONObject jsonObject = new JSONObject(connectionManager.sendHttpRequest());
                JSONArray jsonArray = jsonObject.getJSONArray("comments");

                for(int i = 0; i<jsonArray.length();i++){
                    JSONObject child = jsonArray.getJSONObject(i);
                    Post post = Post.createPost(mUid);
                    post.setPostText(jsonObject.getString("post"));
                    post.setPostingUsersId(jsonObject.getString("postingusersid"));
                    post.setNumComments(jsonObject.getInt("numcomments"));
                    post.setNumLikes(jsonObject.getInt("numlikes"));
                    post.setTime(jsonObject.getString("time"));

                    pList.add(post);
                }
            } catch(JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }


    public List<Post> getPosts(){
        return mPosts;
    }



}
