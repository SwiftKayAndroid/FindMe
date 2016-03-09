package com.swiftkaydevelopment.findme.managers;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.swiftkaydevelopment.findme.data.User;
import com.swiftkaydevelopment.findme.database.DatabaseManager;
import com.swiftkaydevelopment.findme.events.ConnectionsFoundEvent;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Kevin Haines on 10/21/15.
 */
public class UserManager {
    public interface UserManagerListener{
        void onFriendRequestsRetrieved(ArrayList<User> users);
        void onFriendsRetrieved(ArrayList<User> users);
        void onMatchesRetrieved(ArrayList<User> users);
        void onProfileViewsFetched(ArrayList<User> users);
    }
    private static final String TAG = "UserManager";
    private String mUid;
    private static User me;

    private CopyOnWriteArrayList<UserManagerListener> mListeners = new CopyOnWriteArrayList<>();

    private static UserManager manager = null;


    public static UserManager getInstance(String uid){
        synchronized (UserManager.class) {
            if (manager == null) {
                manager = new UserManager();
            }
            manager.mUid = uid;
        }

        return manager;
    }

    public void addListener(UserManagerListener listener) {
        mListeners.add(listener);
    }

    public void removeListener(UserManagerListener listener) {
        mListeners.remove(listener);
    }

    public User me() {
        if (me == null) {
            try {
                me = new FetchUserTask(mUid, User.createUser()).execute().get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return me;
    }

    /**
     * Gets the list of profile views for the user
     *
     * @param uid User's id
     * @param lastpage lastpage of views fetched
     */
    public void getProfileViews(String uid, String lastpage) {
        new GetProfileViewsTask(uid, lastpage).execute();
    }

    public void invalidateMe() {
        me = null;
    }

    /**
     * Retrieves a list of Friend Requests
     * @param uid current users id
     */
    public void getFriendRequests(String uid) {
        new GetFriendsRequestsTask(uid).execute();
    }

    /**
     * Sends a friend request to the other user
     * @param uid String user's id
     * @param otherUser User to send friend request to
     */
    public void sendFriendRequest(String uid, User otherUser) {
        new SendFriendRequestTask(uid, otherUser).execute();
    }

    public void addProfileView(String uid, User user) {
        new AddProfileViewTask(uid, user).execute();
    }

    /**
     * Sends request to get list of friends
     * @param uid Current User's id
     */
    public void getFriends(String uid) {
        new GetFriendsTask(uid).execute();
    }

    /**
     * Sends a request to match with the other user
     * @param uid String current User's id
     * @param otherUser User to match with
     */
    public void sendMatchRequest(String uid, User otherUser) {
        new SendMatchRequestTask(uid, otherUser).execute();
    }

    /**
     * Gets a list of the current User's matches
     * @param uid String Current User's id
     */
    public void getMatches(String uid) {
        new GetMatchesTask(uid).execute();
    }

    /**
     * Sends a request to block a User
     * @param uid Current User's id
     * @param otherUser User to block
     */
    public void blockUser(String uid, User otherUser) {
        new BlockUserTask(uid, otherUser).execute();
    }

    /**
     * Sends request to server to unfriend the specified user
     * @param uid String current User's id
     * @param otherUser User to unfriend
     */
    public void unfriend(String uid, User otherUser) {
        new UnfriendTask(uid, otherUser).execute();
    }

    /**
     * First gets the stored list of users from the db then goes
     * and fetches more from the server.
     *
     * @param uid User's account id
     * @param lastpost last post
     * @param context Current context
     * @return List of users
     */
    public ArrayList<User> findPeopleSync(String uid, String lastpost, Context context) {
        ArrayList<User> users = DatabaseManager.instance(context).getUsers();
        if (!users.isEmpty()) {
            lastpost = users.get(users.size() - 1).getOuid();
        }
        new FindPeopleTask(uid, lastpost).execute();
        return users;
    }

    /**
     * Pulls the list of users from the database
     *
     * @param uid User's id
     * @param lastpost lastpost
     * @param context Current context
     * @return List of users
     */
    public ArrayList<User> findPeople(String uid, String lastpost, Context context) {
        ArrayList<User> users = DatabaseManager.instance(context).getUsers();
        if (users.isEmpty()) {
            new FindPeopleTask(uid, lastpost).execute();
        }

        return users;
    }

    public void clearUsers(Context context) {
        DatabaseManager.instance(context).clearUsers();
    }

    public void updateProfile(String about, String orientation, String status,
                              String haskids, String wantskids, String weed, String profession, String school, Map<String, String> lookingForMap) {
        new UpdateProfileTask(about, orientation, status, haskids, wantskids, weed, profession, school, lookingForMap, mUid).execute();
    }

    /**
     * Sends a request to the server to deny the friend request
     * @param uid String current User's id
     * @param otherUser User whose friend request is getting denied
     */
    public void denyFriendRequest(String uid, User otherUser) {
        new DenyFriendRequestTask(uid, otherUser).execute();
    }

    private class UpdateProfileTask extends AsyncTask<Void, Void, Void> {
        String about;
        String orientation;
        String uid;
        String status;
        String haskids;
        String wantskids;
        String weed;
        String profession;
        String school;
        Map<String, String> lookingForMap;

        public UpdateProfileTask(String about, String orientation, String status,
                                 String haskids, String wantskids, String weed, String profession,
                                 String school, Map<String, String> lookingForMap, String uid) {
            this.about = about;
            this.orientation = orientation;
            this.uid = uid;
            this.status = status;
            this.haskids = haskids;
            this.wantskids = wantskids;
            this.weed = weed;
            this.profession = profession;
            this.school = school;
            this.lookingForMap = lookingForMap;
        }

        @Override
        protected Void doInBackground(Void... params) {
            ConnectionManager connectionManager = new ConnectionManager();
            connectionManager.setMethod(ConnectionManager.POST);
            connectionManager.setUri("updateprofile.php");
            connectionManager.addParam("uid", uid);
            connectionManager.addParam("aboutme", about);
            connectionManager.addParam("orientation", orientation);
            connectionManager.addParam("status", status);
            connectionManager.addParam("haskids", haskids);
            connectionManager.addParam("wantskids", wantskids);
            connectionManager.addParam("weed", weed);
            connectionManager.addParam("profession", profession);
            connectionManager.addParam("school", school);

            for (Map.Entry<String, String> entry : lookingForMap.entrySet()) {
                connectionManager.addParam(entry.getKey().toLowerCase(), entry.getValue());
            }
            //todo: server still needs to be able to update the looking for stuff
            connectionManager.sendHttpRequest();

            return null;
        }
    }

    /**
     * Class to fetch all of the users details
     *
     */
    private class FetchUserTask extends AsyncTask<Void,Void,User> implements Serializable {
        String uid;
        User user;

        public FetchUserTask(String uid, User user){
            this.uid = uid;
            this.user = user;
        }

        @Override
        protected User doInBackground(Void... params) {
            Log.i(TAG, "fetchUser-doInBackground");
            ConnectionManager connectionManager = new ConnectionManager();
            connectionManager.setMethod(ConnectionManager.POST);
            connectionManager.setUri("getuser.php");
            connectionManager.addParam("uid",mUid);
            connectionManager.addParam("ouid",uid);
            final String result = connectionManager.sendHttpRequest();

            if (result != null) {

                try {
                    JSONObject jsonObject = new JSONObject(result);
                    user  = User.createUserFromJson(jsonObject);

                } catch (final JSONException e) {
                    e.printStackTrace();
                }
            }
            return user;
        }
    }

    private class GetProfileViewsTask extends AsyncTask<Void, Void, ArrayList<User>> {
        String uid;
        String lastpage;

        public GetProfileViewsTask(String uid, String lastpage) {
            this.uid = uid;
            this.lastpage = lastpage;
        }

        @Override
        protected ArrayList<User> doInBackground(Void... params) {
            ConnectionManager connectionManager = new ConnectionManager();
            connectionManager.setMethod(ConnectionManager.POST);
            connectionManager.addParam("uid", uid);
            connectionManager.addParam("lastpage", lastpage);
            connectionManager.setUri("getprofileviews.php");
            String result = connectionManager.sendHttpRequest();
            ArrayList<User> users = new ArrayList<>();

            if (result != null) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray jsonArray = jsonObject.getJSONArray("users");

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
            for (UserManagerListener l : mListeners) {
                if (l != null) {
                    l.onProfileViewsFetched(users);
                }
            }
        }
    }

    private  class AddProfileViewTask extends AsyncTask<Void, Void, Void> {
        String uid;
        User user;

        public AddProfileViewTask(String uid, User user) {
            this.uid = uid;
            this.user = user;
        }

        @Override
        protected Void doInBackground(Void... params) {
            ConnectionManager connectionManager = new ConnectionManager();
            connectionManager.setMethod(ConnectionManager.POST);
            connectionManager.addParam("uid", uid);
            connectionManager.addParam("ouid", user.getOuid());
            connectionManager.setUri("addprofileview.php");
            String result = connectionManager.sendHttpRequest();
            return null;
        }
    }

    private class GetFriendsRequestsTask extends AsyncTask<Void, Void, ArrayList<User>> {
        String uid;

        public GetFriendsRequestsTask(String uid) {
            this.uid = uid;
        }

        @Override
        protected ArrayList<User> doInBackground(Void... params) {
            ConnectionManager connectionManager = new ConnectionManager();
            connectionManager.setMethod(ConnectionManager.POST);
            connectionManager.addParam("uid", uid);
            connectionManager.setUri("getfriendrequests.php");
            String result = connectionManager.sendHttpRequest();
            ArrayList<User> users = new ArrayList<>();

            if (result != null) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray jsonArray = jsonObject.getJSONArray("ppl");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject child = jsonArray.getJSONObject(i);
                        User u = User.createUserFromJson(child.getJSONObject("user"));
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

            for (UserManagerListener l : mListeners) {
                if (l != null) {
                    l.onFriendRequestsRetrieved(users);
                }
            }
        }
    }

    private class SendFriendRequestTask extends AsyncTask<Void, Void, Void> {
        String uid;
        User user;

        public SendFriendRequestTask(String uid, User user) {
            this.uid = uid;
            this.user = user;
        }

        @Override
        protected Void doInBackground(Void... params) {
            ConnectionManager connectionManager = new ConnectionManager();
            connectionManager.setMethod(ConnectionManager.POST);
            connectionManager.addParam("uid", uid);
            connectionManager.setUri("sendfriendrequest.php");
            connectionManager.addParam("ouid", user.getOuid());
            String result = connectionManager.sendHttpRequest();
            return null;
        }
    }

    private class GetFriendsTask extends AsyncTask<Void, Void, ArrayList<User>> {
        String uid;

        public GetFriendsTask(String uid) {
            this.uid = uid;
        }

        @Override
        protected ArrayList<User> doInBackground(Void... params) {
            ConnectionManager connectionManager = new ConnectionManager();
            connectionManager.setMethod(ConnectionManager.POST);
            connectionManager.addParam("uid", uid);
            connectionManager.setUri("getfriendslist.php");
            String result = connectionManager.sendHttpRequest();
            ArrayList<User> users = new ArrayList<>();

            if (result != null) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray jsonArray = jsonObject.getJSONArray("ppl");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject child = jsonArray.getJSONObject(i);
                        User u = User.createUserFromJson(child.getJSONObject("user"));
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
            for (UserManagerListener l : mListeners) {
                if (l != null) {
                    l.onFriendsRetrieved(users);
                }
            }
        }
    }

    private class SendMatchRequestTask extends AsyncTask<Void, Void, Void> {
        String uid;
        User user;

        public SendMatchRequestTask(String uid, User user) {
            this.uid = uid;
            this.user = user;
        }

        @Override
        protected Void doInBackground(Void... params) {
            ConnectionManager connectionManager = new ConnectionManager();
            connectionManager.setMethod(ConnectionManager.POST);
            connectionManager.addParam("uid", uid);
            connectionManager.setUri("sendmatchrequest.php");
            connectionManager.addParam("ouid", user.getOuid());
            String result = connectionManager.sendHttpRequest();
            return null;
        }
    }

    private class GetMatchesTask extends AsyncTask<Void, Void, Void> {
        String uid;

        public GetMatchesTask(String uid) {
            this.uid = uid;
        }

        @Override
        protected Void doInBackground(Void... params) {
            ConnectionManager connectionManager = new ConnectionManager();
            connectionManager.setMethod(ConnectionManager.POST);
            connectionManager.addParam("uid", uid);
            connectionManager.setUri("getmatches.php");
            String result = connectionManager.sendHttpRequest();
            return null;
        }
    }

    private class BlockUserTask extends AsyncTask<Void, Void, Void> {
        String uid;
        User user;

        public BlockUserTask(String uid, User user) {
            this.uid = uid;
            this.user = user;
        }

        @Override
        protected Void doInBackground(Void... params) {
            ConnectionManager connectionManager = new ConnectionManager();
            connectionManager.setMethod(ConnectionManager.POST);
            connectionManager.addParam("uid", uid);
            connectionManager.setUri("blockuser.php");
            connectionManager.addParam("ouid", user.getOuid());
            String result = connectionManager.sendHttpRequest();
            return null;
        }
    }

    private class DenyFriendRequestTask extends AsyncTask<Void, Void, Void>{
        String uid;
        User user;

        public DenyFriendRequestTask(String uid, User user) {
            this.uid = uid;
            this.user = user;
        }

        @Override
        protected Void doInBackground(Void... params) {
            ConnectionManager connectionManager = new ConnectionManager();
            connectionManager.setMethod(ConnectionManager.POST);
            connectionManager.addParam("uid", uid);
            connectionManager.setUri("denyfriendrequest.php");
            connectionManager.addParam("ouid", user.getOuid());
            String result = connectionManager.sendHttpRequest();
            return null;
        }
    }

    private class UnfriendTask extends AsyncTask<Void, Void, Void> {
        String uid;
        User user;

        public UnfriendTask(String uid, User user) {
            this.uid = uid;
            this.user = user;
        }

        @Override
        protected Void doInBackground(Void... params) {
            ConnectionManager connectionManager = new ConnectionManager();
            connectionManager.setMethod(ConnectionManager.POST);
            connectionManager.addParam("uid", uid);
            connectionManager.setUri("unfriend.php");
            connectionManager.addParam("ouid", user.getOuid());
            String result = connectionManager.sendHttpRequest();
            return null;
        }
    }

    private class FindPeopleTask extends AsyncTask<Void, Void, ArrayList<User>> {
        String uid;
        String lastpost;

        public FindPeopleTask(String uid, String lastpost) {
            this.uid = uid;
            this.lastpost = lastpost;
        }

        @Override
        protected ArrayList<User> doInBackground(Void... params) {
            ConnectionManager connectionManager = new ConnectionManager();
            connectionManager.setMethod(ConnectionManager.POST);
            connectionManager.addParam("uid", uid);
            connectionManager.addParam("lastpost", lastpost);
            connectionManager.setUri("findpeople.php");
            String result = connectionManager.sendHttpRequest();
            ArrayList<User> users = new ArrayList<>();

            if (result != null) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray jsonArray = jsonObject.getJSONArray("ppl");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject child = jsonArray.getJSONObject(i);
                        User u = User.createUserFromJson(child);
                        if (u != null) {
                            users.add(u);
                        }
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
            EventBus.getDefault().postSticky(new ConnectionsFoundEvent(users));
        }
    }
}
