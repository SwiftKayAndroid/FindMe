package com.swiftkaytech.findme.managers;

import android.content.Context;
import android.os.AsyncTask;

import com.swiftkaytech.findme.data.User;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Kevin Haines on 10/21/15.
 */
public class UserManager {
    public interface UserManagerListener{
        void onFriendRequestsRetrieved(ArrayList<User> users);
        void onFriendsRetrieved(ArrayList<User> users);
        void onMatchesRetrieved(ArrayList<User> users);
    }
    private static final String TAG = "UserManager";
    private static String mUid;
    private static Context mContext;
    private static User me;

    private CopyOnWriteArrayList<UserManagerListener> mListeners = new CopyOnWriteArrayList<>();

    private static UserManager manager = null;


    public static UserManager getInstance(String uid, Context context){
        if (manager == null) {
            manager = new UserManager();
        }
        manager.mUid = uid;
        manager.mContext = context;

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
            me = User.createUser(mUid, mContext).fetchUser(mUid, mContext);
        }
        return me;
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
     * Sends a request to the server to deny the friend request
     * @param uid String current User's id
     * @param otherUser User whose friend request is getting denied
     */
    public void denyFriendRequest(String uid, User otherUser) {
        new DenyFriendRequestTask(uid, otherUser).execute();
    }

    private class GetFriendsRequestsTask extends AsyncTask<Void, Void, Void> {
        String uid;

        public GetFriendsRequestsTask(String uid) {
            this.uid = uid;
        }

        @Override
        protected Void doInBackground(Void... params) {
            ConnectionManager connectionManager = new ConnectionManager();
            connectionManager.setMethod(ConnectionManager.POST);
            connectionManager.addParam("uid", uid);
            connectionManager.setUri("getfriendrequests.php");
            String result = connectionManager.sendHttpRequest();
            return null;
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

    private class GetFriendsTask extends AsyncTask<Void, Void, Void> {
        String uid;

        public GetFriendsTask(String uid) {
            this.uid = uid;
        }

        @Override
        protected Void doInBackground(Void... params) {
            ConnectionManager connectionManager = new ConnectionManager();
            connectionManager.setMethod(ConnectionManager.POST);
            connectionManager.addParam("uid", uid);
            connectionManager.setUri("getfriends.php");
            String result = connectionManager.sendHttpRequest();
            return null;
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
}
