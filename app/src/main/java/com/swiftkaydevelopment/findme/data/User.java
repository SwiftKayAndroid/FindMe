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

package com.swiftkaydevelopment.findme.data;

import android.os.AsyncTask;
import android.util.Log;

import com.swiftkaydevelopment.findme.managers.ConnectionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Kevin Haines on 10/21/15.
 */
public class User implements Serializable {

    public interface UserListener {
        void onPicturesFetched(ArrayList<Post> picPosts);
    }

    public static final String TAG = "FindMe - User";

    public enum Gender{
        MALE {
            @Override
            public String toString() {
                return "Male";
            }
        }, FEMALE {
            @Override
            public String toString() {
                return "Female";
            }
        }
    }
    public enum Orientation{
        STRAIGHT {
            @Override
            public String toString() {
                return "Straight";
            }
        }, GAY {
            @Override
            public String toString() {
                return "Gay";
            }
        }, BISEXUAL {
            @Override
            public String toString() {
                return "Bisexual";
            }
        }, OTHER {
            @Override
            public String toString() {
                return "";
            }
        }
    }

    public enum OnlineStatus {
        ONLINE, OFFLINE, HIDDEN, UNKNOWN
    }
    public enum InterestedIn {
        MEN, WOMEN, BOTH
    }

    private static String mUid;
    public String mOuid;
    public String mPropicloc;
    public Gender mGender;
    public String mName;
    public String mFirstname;
    public String mLastname;
    public int mLookingFor;
    public int mAge;
    private boolean mIsFriend;
    private boolean mIsBlocked;
    private boolean mIsMatch;
    public Orientation mOrientation;
    public OnlineStatus mOnlineStatus;
    public String mRelationshipStatus;
    private String mAboutMe;
    private InterestedIn mInterestIn;
    public String city;
    public String distance;
    public String hasKids;
    public String wantsKids;
    public String profession;
    public String school;
    public String weed;

    private CopyOnWriteArrayList<UserListener> mListeners = new CopyOnWriteArrayList<>();

    /**
     * Creates a new instance of a user
     *
     * @return new instance of User
     */
    public static User createUser(){
        return new User();
    }

    /**
     * Creates a new User from json object
     *
     * @param object JsonObject containing user information
     * @return new User
     */
    public static User createUserFromJson(JSONObject object) {
        try {
            User user = createUser();
            user.mOuid = object.getString("uid");
            user.mPropicloc = object.getString("pic");
            user.setFirstname(object.getString("fn"));
            user.mLastname = object.getString("ln");
            user.city = object.getString("cy");
            user.distance = object.getString("dist");
            user.mGender = setGenderFromString(object.getString("ge"));
            user.mInterestIn = setInterestedInFromString(object.getString("lfg"));
            user.mOrientation = setOrientationFromString(object.getString("ori"));
            user.mIsFriend = object.getBoolean("frnd");
            user.mIsMatch = object.getBoolean("mch");
            user.mAboutMe = object.getString("am");
            user.mAge = Integer.parseInt(object.getString("age"));
            user.mLookingFor = object.getInt("lf");
            user.mRelationshipStatus = object.getString("rs");
            user.hasKids = object.getString("hk");
            user.wantsKids = object.getString("wk");
            user.profession = object.getString("pro");
            user.school = object.getString("sc");
            user.weed = object.getString("we");
            return user;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public User fetchUser(String ouid, String uid){
        Log.i(TAG, "fetchUser");
        mOuid = ouid;
        try {
            return new FetchUserTask(ouid, uid, this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i(TAG,"fetchUser - returning null");
        return this;
    }

    /**
     * class to fetch all of the users details
     */
    private static class FetchUserTask extends AsyncTask<Void,Void,User> implements Serializable{
        String ouid;
        User user;
        String uid;

        public FetchUserTask(String ouid, String uid, User user){
            this.ouid = ouid;
            this.user = user;
            this.uid = uid;
        }

        @Override
        protected User doInBackground(Void... params) {
            Log.i(TAG, "fetchUser-doInBackground");
            ConnectionManager connectionManager = new ConnectionManager();
            connectionManager.setMethod(ConnectionManager.POST);
            connectionManager.setUri("getuser.php");
            connectionManager.addParam("uid", uid);
            connectionManager.addParam("ouid", ouid);
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

    private class FetchPhotosTask extends AsyncTask<Void, Void, ArrayList<Post>> {
        User user;
        String uid;
        String lastpage;

        public FetchPhotosTask(User user, String uid, String lastpage) {
            this.user = user;
            this.uid = uid;
            this.lastpage = lastpage;
        }

        @Override
        protected ArrayList<Post> doInBackground(Void... params) {
            ConnectionManager connectionManager = new ConnectionManager();
            connectionManager.setMethod(ConnectionManager.POST);
            connectionManager.setUri("getpictures.php");
            connectionManager.addParam("uid", uid);
            connectionManager.addParam("ouid", user.getOuid());
            connectionManager.addParam("lastpage", lastpage);
            String result = connectionManager.sendHttpRequest();
            ArrayList<Post> pics = new ArrayList<>();

            if (result != null) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray jsonArray = jsonObject.getJSONArray("pics");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject child = jsonArray.getJSONObject(i);
                        Post p = Post.createPostFromJson(child);
                        pics.add(p);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return pics;
        }

        @Override
        protected void onPostExecute(ArrayList<Post> pics) {
            super.onPostExecute(pics);
            for (UserListener l : mListeners) {
                if (l != null) {
                    l.onPicturesFetched(pics);
                }
            }
        }
    }

    /**
     * Adds a listener to this manager
     *
     * @param listener Listener to add
     */
    public void addListener(UserListener listener) {
        mListeners.add(listener);
    }

    /**
     * Removes a listener from this manager
     *
     * @param listener Listener to remove
     */
    public void removeListener(UserListener listener) {
        mListeners.remove(listener);
    }

    /**
     * Sets the gender enum based on a string
     *
     * @param gender Gender object representing the users gender
     */
    public static Gender setGenderFromString(String gender){
        switch (gender){
            case "Male":{
                return Gender.MALE;
            }
            case "Female":{
                return Gender.FEMALE;
            }
            default:{
                return null;
            }
        }
    }

    /**
     * sets orientation enum based on string orientation
     * @param orientation string to be used to set orientation
     * @return Orientation enum from string
     */
    public static Orientation setOrientationFromString(String orientation) {
        if (orientation.equalsIgnoreCase("Straight")) {
            return Orientation.STRAIGHT;
        } else if (orientation.equalsIgnoreCase("Gay")) {
            return Orientation.GAY;
        } else if (orientation.equalsIgnoreCase("Bisexual")) {
            return Orientation.BISEXUAL;
        } else {
            return Orientation.OTHER;
        }
    }

    /**
     * sets online status based on string online status
     * @param status string to be used to set online status
     * @return OnlineStatus enum from string value
     */
    public static OnlineStatus setOnlineStatusFromString(String status) {
        if (status.equals("online")) {
            return OnlineStatus.ONLINE;
        } else if (status.equals("offline")) {
            return OnlineStatus.OFFLINE;
        } else if (status.equals("hidden")) {
            return OnlineStatus.HIDDEN;
        } else {
            return OnlineStatus.UNKNOWN;
        }
    }

    public static InterestedIn setInterestedInFromString(String interest) {
        if (interest.equals("Men")) {
            return InterestedIn.MEN;
        } else if (interest.equals("Women")) {
            return InterestedIn.WOMEN;
        } else {
            return InterestedIn.BOTH;
        }
    }

    /**
     * fetches the users mFirstname
     * @return String of users mFirstname
     */
    public String getFirstname() {
        return mFirstname;
    }

    /**
     * sets the users mFirstname
     * @param firstname mFirstname of user
     */
    public void setFirstname(String firstname) {
        this.mFirstname = firstname;
    }

    /**
     * gets the mGender that the user is interested in
     * @return InterestedIn enum for what mGender the user
     * is interested in
     */
    public InterestedIn getInterestedIn() {
        return mInterestIn;
    }

    /**
     * sets the mGender that the user is interested in
     * @param interestedIn mGender user is interested in
     */
    public void setInterestedIn(InterestedIn interestedIn) {
        this.mInterestIn = interestedIn;
    }

    public String getLookingForString() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Integer> entry : DataParser.LookingForParser.getMap().entrySet()) {
            if (DataParser.contains(mLookingFor, entry.getValue())) {
                sb.append(entry.getKey() + " ");
            }
        }

        return sb.toString();
    }

    /**
     * gets the users about me
     * @return String users about me
     */
    public String getAboutMe() {
        return mAboutMe;
    }

    /**
     * sets the users about me
     * @param aboutMe string users about me
     */
    public void setAboutMe(String aboutMe) {
        this.mAboutMe = aboutMe;
    }

    /**
     * gets a list of the users picture uri locations
     */
    public void getPictures(String lastpage) {
        new FetchPhotosTask(this, mUid, lastpage).execute();
    }

    /**
     *
     * @return online status enum
     */
    public OnlineStatus getOnlineStatus() {
        return mOnlineStatus;
    }

    /**
     *
     * @param onlineStatus set online status enum
     */
    public void setOnlineStatus(OnlineStatus onlineStatus) {
        this.mOnlineStatus = onlineStatus;
    }

    /**
     *
     * @return get the users sexual mOrientation enum
     */
    public Orientation getOrientation() {
        return mOrientation;
    }

    /**
     *
     * @param orientation set the users sexual mOrientation enum
     */
    public void setOrientation(Orientation orientation) {
        this.mOrientation = orientation;
    }

    /**
     *
     * @return boolean true if matched false if not
     */
    public boolean isMatch() {
        return mIsMatch;
    }

    /**
     *
     * @param isMatch boolean true if matched false if not
     */
    public void setIsMatch(boolean isMatch) {
        this.mIsMatch = isMatch;
    }

    /**
     *
     * @return boolean true if blocked false if not
     */
    public boolean isBlocked() {
        return mIsBlocked;
    }

    /**
     *
     * @param isBlocked boolean true if blocked false if not
     */
    public void setIsBlocked(boolean isBlocked) {
        this.mIsBlocked = isBlocked;
    }

    /**
     *
     * @return boolean true if user is friend to current user false if not
     */
    public boolean isFriend() {
        return mIsFriend;
    }

    /**
     *
     * @param isFriend boolean true if user is friends with current user false if not
     */
    public void setIsFriend(boolean isFriend) {
        this.mIsFriend = isFriend;
    }

    /**
     *
     * @return int users mAge
     */
    public int getAge() {
        return mAge;
    }

    /**
     *
     * @param age int users mAge
     */
    public void setAge(int age) {
        this.mAge = age;
    }

    /**
     *
     * @return string users mLastname
     */
    public String getLastname() {
        return mLastname;
    }

    /**
     * sets the users last mName.
     * also if the mFirstname is not empty and the users
     * full mName is this will smartly update the users fullname.
     * set first mName does not do this same smart update
     * @param lastname string users last mName
     */
    public void setLastname(String lastname) {
        this.mLastname = lastname;

        if (mFirstname != null && !mFirstname.isEmpty()) {
            mName = mFirstname + " " + lastname;
        }
    }

    /**
     *
     * @return string users full mName
     */
    public String getName() {
        return mFirstname + " " + mLastname;
    }

    /**
     *
     * @param name string users full mName
     */
    public void setName(String name) {
        this.mName = name;
    }

    /**
     *
     * @return Gender enum for users mGender
     */
    public Gender getGender() {
        return mGender;
    }

    /**
     *
     * @param gender Gender enum for users mGender
     */
    public void setGender(Gender gender) {
        this.mGender = gender;
    }

    /**
     *
     * @return users profile picture uri mLocation
     */
    public String getPropicloc() {
        return mPropicloc;
    }

    /**
     *
     * @param propicloc users profile picture uri mLocation
     */
    public void setPropicloc(String propicloc) {
        this.mPropicloc = propicloc;
    }

    /**
     *
     * @return users unique user id
     */
    public String getOuid() {
        return mOuid;
    }

    /**
     *
     * @param uid users unique user id
     */
    public void setOuid(String uid) {
        this.mOuid = uid;
    }

    /**
     * determines if the users being compared are the same user
     * @param user user to be compared to
     * @return true if the two users are the same user
     */
    public boolean isSameAs(User user){
        return mOuid.equals(user.getOuid());
    }
}
