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
import android.util.Log;

import com.swiftkaydevelopment.findme.data.User;
import com.swiftkaydevelopment.findme.data.Post;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

public class PostManager {

    public interface PostsListener{
        void onPostsRetrieved(ArrayList<Post> posts);
    }

    public static final String TAG = "FindMe-PostManager";

    private String mUid;
    private ArrayList<Post> mPosts = new ArrayList<>();
    private static PostManager manager = null;
    private static Context mContext;
    private CopyOnWriteArrayList<PostsListener> mListeners = new CopyOnWriteArrayList<>();

    public static PostManager getInstance(String uid, Context context){
        if (manager == null) {
            manager = new PostManager();
        }
        manager.mUid = uid;
        mContext = context;
        return manager;
    }

    public void addPostListener(PostsListener listener) {
        mListeners.add(listener);
    }

    public void removeListener(PostsListener listener) {
        mListeners.remove(listener);
    }

    /**
     * Gets an arraylist of posts from the server
     * todo: will get posts from db first and sync with that
     * @param context
     * @return
     */
    public ArrayList<Post> fetchPosts(Context context, String lastpost){
        Log.d(TAG, "fetching posts");
        new FetchPostsTask(mUid, lastpost).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
//        for (Post post : mPosts) {
//            //TagManager.getInstance(mUid).fetchTags(post.getPostId(), context, post);
//        }
        return mPosts;
    }

    public void fetchUserPosts(Context context, User user, String lastpost) {
        new FetchUserPosts(user, lastpost).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
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
            connectionManager.setUri("getposts.php");
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
                        Post post = Post.createPost(mUid, mContext);
                        post.setPostText(child.getString("post"));
                        post.setPostingUsersId(child.getString("postingusersid"));
                        post.setNumComments(child.getInt("numcomments"));
                        post.setNumLikes(child.getInt("numlikes"));
                        post.setTime(child.getString("time"));
                        post.setPostId(child.getString("postid"));
                        post.setLiked(child.getBoolean("liked"));
                        post.setPostImage(child.getString("postpicloc"));
                        User u = User.createUser(mUid, mContext);
                        JSONObject user = child.getJSONObject("user");
                        u.setInterestedIn(User.setInterestedInFromString(user.getString("looking_for_gender")));
                        u.setOuid(user.getString("uid"));
                        u.setFirstname(user.getString("firstname"));
                        u.setLastname(user.getString("lastname"));
                        u.setGender(User.setGenderFromString(user.getString("gender")));
                        u.setIsBlocked(false);
                        u.setOrientation(User.setOrientationFromString(user.getString("orientation")));
                        u.setPropicloc(user.getString("propicloc"));
                        u.setAboutMe(user.getString("aboutme"));
                        u.setAge(Integer.parseInt(user.getString("age")));
                        post.setUser(u);
                        u.setLocation(User.setLocationFromArray(user.getJSONObject("location")));

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
            for (PostsListener l : mListeners) {
                if (l != null) {
                    l.onPostsRetrieved(posts);
                }
            }
        }
    }

    private class FetchUserPosts extends AsyncTask<Void, Void, ArrayList<Post>> {
        User user;
        String lastpost;

        public FetchUserPosts(User user, String lastpost) {
            this.user = user;
            this.lastpost = lastpost;
        }

        @Override
        protected ArrayList<Post> doInBackground(Void... params) {
            ConnectionManager connectionManager = new ConnectionManager();
            connectionManager.setMethod(ConnectionManager.POST);
            connectionManager.setUri("getprofileposts.php");
            connectionManager.addParam("ouid", user.getOuid());
            connectionManager.addParam("uid", mUid);
            connectionManager.addParam("lp", "0");
            ArrayList<Post> pList = new ArrayList<>();

            final String result = connectionManager.sendHttpRequest();

            if (result != null) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray jsonArray = jsonObject.getJSONArray("posts");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject child = jsonArray.getJSONObject(i);
                        Post post = Post.createPost(mUid, mContext);
                        post.setPostText(child.getString("post"));
                        post.setPostingUsersId(child.getString("postingusersid"));
                        post.setNumComments(child.getInt("numcomments"));
                        post.setNumLikes(child.getInt("numlikes"));
                        post.setTime(child.getString("time"));
                        post.setPostId(child.getString("postid"));
                        post.setLiked(child.getBoolean("liked"));
                        post.setPostImage(child.getString("postpicloc"));
                        post.setUser(user);
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
                    l.onPostsRetrieved(posts);
                }
            }
        }
    }


    public ArrayList<Post> getPosts(Context context){
        if (mPosts != null) {
            if (mPosts.size() > 0) {
                return mPosts;
            } else {
                return fetchPosts(context, "0");
            }
        } else {
            return fetchPosts(context, "0");
        }
    }

    public ArrayList<Post> refreshPosts(Context context) {
        mPosts.clear();
        return fetchPosts(context, "0");
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
