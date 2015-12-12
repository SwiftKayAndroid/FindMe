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

public class FilterManager {
    private static final String TAG = "FilterManager";

    private static String mUid;
    private static Context mContext;

    private static FilterManager sInstance = null;

    public static FilterManager getInstance(Context context, String uid) {
        if (sInstance == null) {
            sInstance = new FilterManager();
        }
        mContext = context;
        mUid = uid;
        return sInstance;
    }

    /**
     * Calls the server to add a String of words to that user's
     * blocked words list.
     * @param words
     */
    public void addFilterWords(String words, String msgs, String posts, String comments) {
        new AddFilterWordsTask(words, mUid, msgs, comments, posts).execute();
    }

    private static class AddFilterWordsTask extends AsyncTask<Void, Void, Void> {
        String words;
        String uid;
        String messages;
        String comments;
        String posts;

        public AddFilterWordsTask(String words, String uid, String messages, String comments, String posts) {
            this.words = words;
            this.uid = uid;
            this.comments = comments;
            this.messages = messages;
            this.posts = posts;
        }

        @Override
        protected Void doInBackground(Void... params) {
            ConnectionManager connectionManager = new ConnectionManager();
            connectionManager.setMethod(ConnectionManager.POST);
            connectionManager.addParam("uid", uid);
            connectionManager.addParam("words", words);
            connectionManager.addParam("messages", messages);
            connectionManager.addParam("comments", comments);
            connectionManager.addParam("posts", posts);
            connectionManager.setUri("addfilterwords.php");
            connectionManager.sendHttpRequest();
            return null;
        }
    }
}
