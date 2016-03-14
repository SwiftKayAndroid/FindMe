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

import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.swiftkaydevelopment.findme.data.Post;
import com.swiftkaydevelopment.findme.data.User;
import com.swiftkaydevelopment.findme.events.NewsFeedPostsRetrieved;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

public class PostManager {

    public interface PostsListener{
        void onProfilePostsRetrieved(ArrayList<Post> posts);
        void onSinglePostRetrieved(Post post);
    }

    public static final String TAG = "FindMe-PostManager";

    private ArrayList<Post> mPosts = new ArrayList<>();
    private static PostManager manager = null;
    private CopyOnWriteArrayList<PostsListener> mListeners = new CopyOnWriteArrayList<>();

    /**
     * Gets the Singleton instance of this manager
     *
     * @return Singleton instance of this manager
     */
    public static PostManager getInstance(){
        synchronized (PostManager.class) {
            if (manager == null) {
                manager = new PostManager();
            }
        }
        return manager;
    }

    /**
     * Adds a listener to the list of listeners
     *
     * @param listener Listener to add
     */
    public void addPostListener(PostsListener listener) {
        mListeners.add(listener);
    }

    /**
     * Remove a listener from the list of listeners
     *
     * @param listener
     */
    public void removeListener(PostsListener listener) {
        mListeners.remove(listener);
    }

    /**
     * Gets an arraylist of posts from the server
     * todo: will get posts from db first and sync with that
     * @return will be returning list of posts from db
     */
    public ArrayList<Post> fetchPosts(String uid, String lastpost){
        Log.d(TAG, "fetching posts");
        if (TextUtils.isEmpty(uid) || TextUtils.isEmpty(lastpost)) {
            throw new IllegalArgumentException("invalid params");
        }

        new FetchPostsTask(uid, lastpost).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
        return mPosts;
    }

    /**
     * Fetches a single post from the database
     *
     * @param postid Post id of the post to fetch
     * @param uid User's id
     */
    public void getSinglePost(String postid, String uid) {
        new FetchPostTask(postid, uid).execute();
    }

    public void deletePost(Post post) {
        new DeletePostTask(post).execute();
    }

    /**
     * Called to fetch the list of posts for a specific user
     *
     * @param user User to get posts for
     * @param lastpost last currently fetched post
     * @param uid Current User's id
     */
    public void fetchUserPosts(User user, String lastpost, String uid) {
        if (TextUtils.isEmpty(uid) || TextUtils.isEmpty(lastpost) || null == user) {
            throw new IllegalArgumentException("fetch user posts - invalid params");
        }

        new FetchUserPosts(user, lastpost, uid).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
    }

    /**
     * Refreshes the posts
     *
     * @param uid User's id
     */
    public void refreshPosts(String uid) {
        mPosts.clear();
        fetchPosts(uid, "0");
    }

    /**
     * Likes a post
     *
     * @param uid User's id
     * @param postid Post id to like
     */
    public void likePost(String uid, String postid) {
        new PostLike(uid, postid).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
    }

    /**
     * Unlikes a posts
     * todo: this should also be checked on the server to ensure we aren't double liking the same post
     *
     * @param uid User's id
     * @param postid Post id to unlike
     */
    public void unLikePost(String uid, String postid) {
        new UnlikePost(uid, postid).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
    }

    /**
     * Sends request to server to like a post
     *
     */
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

    /**
     * Sends request to server to unlike a post
     * TODO: This needs to be checked on the server to ensure we aren't
     * double liking the same post.
     *
     */
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

    /**
     * Gets the posts from a specific user to display on their profile
     *
     */
    private class FetchUserPosts extends AsyncTask<Void, Void, ArrayList<Post>> {
        User user;
        String lastpost;
        String uid;

        public FetchUserPosts(User user, String lastpost, String uid) {
            this.user = user;
            this.lastpost = lastpost;
            this.uid = uid;
        }

        @Override
        protected ArrayList<Post> doInBackground(Void... params) {
            ConnectionManager connectionManager = new ConnectionManager();
            connectionManager.setMethod(ConnectionManager.POST);
            connectionManager.setUri("getprofileposts.php");
            connectionManager.addParam("ouid", user.getOuid());
            connectionManager.addParam("uid", uid);
            connectionManager.addParam("lp", "0");
            ArrayList<Post> pList = new ArrayList<>();

            final String result = connectionManager.sendHttpRequest();

            if (result != null) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray jsonArray = jsonObject.getJSONArray("posts");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject child = jsonArray.getJSONObject(i);
                        Post post = Post.createPostFromJson(child);
                        pList.add(post);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return pList;
        }

        @Override
        protected void onPostExecute(ArrayList<Post> posts) {
            super.onPostExecute(posts);
            for (PostsListener l : mListeners) {
                if (l != null) {
                    l.onProfilePostsRetrieved(posts);
                }
            }
        }
    }

    /**
     * This class is used to fetch a single post
     *
     */
    private class FetchPostTask extends AsyncTask<Void, Void, Post> {
        String postid;
        String uid;

        public FetchPostTask(String postid, String uid){
            this.postid = postid;
            this.uid = uid;
        }

        @Override
        protected Post doInBackground(Void... params) {
            Log.i(TAG, "fetchPost-doInBackground");

            ConnectionManager connectionManager = new ConnectionManager();
            connectionManager.setMethod(ConnectionManager.POST);
            connectionManager.setUri("getpost.php");
            connectionManager.addParam("postid", postid);
            connectionManager.addParam("uid", uid);

            try {
                JSONObject jsonObject = new JSONObject(connectionManager.sendHttpRequest());
                return Post.createPostFromJson(jsonObject);

            } catch(JSONException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Post post) {
            super.onPostExecute(post);
            for (PostsListener l : mListeners) {
                l.onSinglePostRetrieved(post);
            }
        }
    }

    private class FetchPostsTask extends AsyncTask<Void,Void,ArrayList<Post>> {
        String uid;
        String lastpost;

        public FetchPostsTask(String uid, String lastpost) {
            this.uid = uid;
            this.lastpost = lastpost;
        }

        @Override
        protected ArrayList<Post> doInBackground(Void... params) {
            ConnectionManager connectionManager = new ConnectionManager();
            connectionManager.setMethod(ConnectionManager.POST);
            connectionManager.setUri("getpostsv_1_6_1.php");
            connectionManager.addParam("uid", uid);
            connectionManager.addParam("lp", lastpost);
            ArrayList<Post> pList = new ArrayList<>();

            final String result = connectionManager.sendHttpRequest();

            if (result != null) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray jsonArray = jsonObject.getJSONArray("posts");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject child = jsonArray.getJSONObject(i);
                        Post post = Post.createPostFromJson(child);

                        pList.add(post);
                    }
                } catch (final JSONException e) {
                    e.printStackTrace();
                }
            }
            return pList;
        }

        @Override
        protected void onPostExecute(ArrayList<Post> posts) {
            super.onPostExecute(posts);
            mPosts = posts;
            EventBus.getDefault().postSticky(new NewsFeedPostsRetrieved(posts));
        }
    }

    private class DeletePostTask extends AsyncTask<Void, Void, Void> {
        Post post;

        public DeletePostTask(Post post) {
            this.post = post;
        }

        @Override
        protected Void doInBackground(Void... params) {
            ConnectionManager connectionManager = new ConnectionManager();
            connectionManager.setMethod(ConnectionManager.POST);
            connectionManager.setUri("deletepost.php");
            connectionManager.addParam("postid", post.getPostId());
            connectionManager.sendHttpRequest();
            return null;
        }
    }
}
