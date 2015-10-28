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
import android.graphics.Color;
import android.os.AsyncTask;

import com.swiftkaytech.findme.R;
import com.swiftkaytech.findme.data.Post;
import com.swiftkaytech.findme.views.tagview.Tag;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class TagManager {

    public static final String TAG = "TagManager";
    private static TagManager manager = null;

    private static String mUid;
    private ArrayList<Tag> tList;

    public static TagManager getInstance(String uid){
        if (manager == null) {
            manager = new TagManager();
        }
        manager.mUid = uid;
        return manager;
    }

    public void fetchTags(String postid, Context context, Post post) {
        new FetchTagsTask(postid, context, post).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
    }

    private class FetchTagsTask extends AsyncTask<Void,Void,ArrayList<Tag>>{
        String postid;
        Context context;
        Post post;

        public FetchTagsTask(String postid, Context context, Post post) {
            this.postid = postid;
            this.context = context;
            this.post = post;
        }

        @Override
        protected ArrayList<Tag> doInBackground(Void... params) {
            ArrayList<Tag> tList = new ArrayList<>();
            ConnectionManager connectionManager = new ConnectionManager();
            connectionManager.setMethod(ConnectionManager.POST);
            connectionManager.addParam("postid", postid);
            connectionManager.setUri("gettags.php");

            try {
                JSONObject jsonObject = new JSONObject(connectionManager.sendHttpRequest());
                JSONArray jsonArray = jsonObject.getJSONArray("tags");

                for(int i = 0; i<jsonArray.length();i++){
                    JSONObject child = jsonArray.getJSONObject(i);
                    Tag tag = new Tag(child.getString("tag"));
                    tag.tagTextColor = Color.parseColor("#FFFFFF");
                    tag.layoutColor = context.getResources().getColor(R.color.base_green);
                    tag.layoutColorPress = Color.parseColor("#555555");
                    tag.radius = 20f;
                    tag.tagTextSize = 14f;
                    tag.layoutBorderSize = 1f;
                    tag.layoutBorderColor = Color.parseColor("#FFFFFF");
                    tList.add(tag);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return tList;
        }

        @Override
        protected void onPostExecute(ArrayList<Tag> tags) {
            super.onPostExecute(tags);
            post.setTags(tags);
        }
    }

    public ArrayList<Tag> getTags(String postid) {
        return tList;
    }


}
