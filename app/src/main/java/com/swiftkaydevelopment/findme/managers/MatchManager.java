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

import com.swiftkaydevelopment.findme.data.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

public class MatchManager {

    public interface MatchManagerListener {
        void onPotentialsFound(ArrayList<User> users);
        void onMatchesRetrieved(ArrayList<User> users);
        void onLikedUsersRetrieved(ArrayList<User> users);
        void onWhoLikedMeRetrieved(ArrayList<User> users);
    }
    private static final String TAG = "MatchManager";

    private static String mUid;
    private Context mContext;

    private CopyOnWriteArrayList<MatchManagerListener> mListeners = new CopyOnWriteArrayList<>();

    private static MatchManager sInstance = null;

    public static MatchManager getInstance(Context context) {
        if (sInstance == null) {
            synchronized (MatchManager.class) {
                sInstance = new MatchManager(context);
            }
        }
        return sInstance;
    }

    public MatchManager(Context mContext) {
        this.mContext = mContext;
    }

    public void matchUser(User user, String uid) {
        new MatchUserTask(uid, user).execute();
    }

    public void dislikeUser(User user, String uid) {
        new DislikeUserTask(uid, user).execute();
    }

    public void addListener(MatchManagerListener listener) {
        mListeners.addIfAbsent(listener);
    }

    public void removeListener(MatchManagerListener listener) {
        mListeners.remove(listener);
    }

    public void getPotentialMatches(String uid) {
        new GetPotentialMatchesTask(uid).execute();
    }

    public void getMatches(String uid, String lastpage) {
        new GetMatches(uid, lastpage).execute();
    }

    public void getLikedUsers(String uid, String lastpage) {
        new GetLikedUsers(uid, lastpage).execute();
    }

    public void getWhoLikedMe(String uid, String lastpage) {
        new GetWhoLikedMe(uid, lastpage).execute();
    }

    private class MatchUserTask extends AsyncTask<Void, Void, Void> {
        String uid;
        User user;

        public MatchUserTask(String uid, User user) {
            this.uid = uid;
            this.user = user;
        }

        @Override
        protected Void doInBackground(Void... params) {
            ConnectionManager connectionManager = new ConnectionManager();
            connectionManager.setMethod(ConnectionManager.POST);
            connectionManager.addParam("uid", uid);
            connectionManager.addParam("ouid", user.getOuid());
            connectionManager.setUri("matchuser.php");
            connectionManager.sendHttpRequest();
            return null;
        }
    }

    private class DislikeUserTask extends AsyncTask<Void, Void, Void> {
        String uid;
        User user;

        public DislikeUserTask(String uid, User user) {
            this.uid = uid;
            this.user = user;
        }

        @Override
        protected Void doInBackground(Void... params) {
            ConnectionManager connectionManager = new ConnectionManager();
            connectionManager.setMethod(ConnectionManager.POST);
            connectionManager.addParam("uid", uid);
            connectionManager.addParam("ouid", user.getOuid());
            connectionManager.setUri("dislikeuser.php");
            connectionManager.sendHttpRequest();
            return null;
        }
    }

    private class GetPotentialMatchesTask extends AsyncTask<Void, Void, ArrayList<User>> {
        String uid;

        public GetPotentialMatchesTask(String uid) {
            this.uid = uid;
        }

        @Override
        protected ArrayList<User> doInBackground(Void... params) {
            ConnectionManager connectionManager = new ConnectionManager();
            connectionManager.setMethod(ConnectionManager.POST);
            connectionManager.addParam("uid", uid);
            connectionManager.setUri("getpotentialmatches.php");
            String result = connectionManager.sendHttpRequest();
            ArrayList<User> users = new ArrayList<>();

            if (result != null) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray jsonArray = jsonObject.getJSONArray("ppl");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject child = jsonArray.getJSONObject(i);
                        User u = User.createUserFromJson(child);
                        users.add(u);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return users;
        }

        @Override
        protected void onPostExecute(ArrayList<User> users) {
            super.onPostExecute(users);

            for (MatchManagerListener l : mListeners) {
                if (l != null) {
                    l.onPotentialsFound(users);
                }
            }
        }
    }

    private class GetMatches extends AsyncTask<Void, Void, ArrayList<User>> {
        String uid;
        String lastpage;

        public GetMatches(String uid, String lastpage) {
            this.uid = uid;
            this.lastpage = lastpage;
        }

        @Override
        protected ArrayList<User> doInBackground(Void... params) {
            ConnectionManager connectionManager = new ConnectionManager();
            connectionManager.setMethod(ConnectionManager.POST);
            connectionManager.addParam("uid", uid);
            connectionManager.addParam("lastpage", lastpage);
            connectionManager.setUri("getmatches.php");
            String result = connectionManager.sendHttpRequest();
            ArrayList<User> users = new ArrayList<>();

            if (result != null) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray jsonArray = jsonObject.getJSONArray("ppl");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject child = jsonArray.getJSONObject(i);
                        User u = User.createUserFromJson(child);
                        users.add(u);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return users;
        }

        @Override
        protected void onPostExecute(ArrayList<User> users) {
            super.onPostExecute(users);

            for (MatchManagerListener l : mListeners) {
                if (l != null) {
                    l.onMatchesRetrieved(users);
                }
            }
        }
    }

    private class GetLikedUsers extends AsyncTask<Void, Void, ArrayList<User>> {
        String uid;
        String lastpage;

        public GetLikedUsers(String uid, String lastpage) {
            this.uid = uid;
            this.lastpage = lastpage;
        }

        @Override
        protected ArrayList<User> doInBackground(Void... params) {
            ConnectionManager connectionManager = new ConnectionManager();
            connectionManager.setMethod(ConnectionManager.POST);
            connectionManager.addParam("uid", uid);
            connectionManager.addParam("lastpage", lastpage);
            connectionManager.setUri("getlikedusers.php");
            String result = connectionManager.sendHttpRequest();
            ArrayList<User> users = new ArrayList<>();

            if (result != null) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray jsonArray = jsonObject.getJSONArray("ppl");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject child = jsonArray.getJSONObject(i);
                        User u = User.createUserFromJson(child);
                        users.add(u);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return users;
        }

        @Override
        protected void onPostExecute(ArrayList<User> users) {
            super.onPostExecute(users);

            for (MatchManagerListener l : mListeners) {
                if (l != null) {
                    l.onLikedUsersRetrieved(users);
                }
            }
        }
    }

    private class GetWhoLikedMe extends AsyncTask<Void, Void, ArrayList<User>> {
        String uid;
        String lastpage;

        public GetWhoLikedMe(String uid, String lastpage) {
            this.uid = uid;
            this.lastpage = lastpage;
        }

        @Override
        protected ArrayList<User> doInBackground(Void... params) {
            ConnectionManager connectionManager = new ConnectionManager();
            connectionManager.setMethod(ConnectionManager.POST);
            connectionManager.addParam("uid", uid);
            connectionManager.addParam("lastpage", lastpage);
            connectionManager.setUri("getwholikedme.php");
            String result = connectionManager.sendHttpRequest();
            ArrayList<User> users = new ArrayList<>();

            if (result != null) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray jsonArray = jsonObject.getJSONArray("ppl");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject child = jsonArray.getJSONObject(i);
                        User u = User.createUserFromJson(child);
                        users.add(u);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return users;
        }
        @Override
        protected void onPostExecute(ArrayList<User> users) {
            super.onPostExecute(users);

            for (MatchManagerListener l : mListeners) {
                if (l != null) {
                    l.onWhoLikedMeRetrieved(users);
                }
            }
        }
    }
}
