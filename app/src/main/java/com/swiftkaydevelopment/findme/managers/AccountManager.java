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
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.swiftkaydevelopment.findme.data.NewsfeedPrefData;

import java.net.MalformedURLException;
import java.net.URL;

public class AccountManager {
    private static final String TAG = "AccountManager";

    private static AccountManager manager = null;
    public static Context mContext;

    public static AccountManager getInstance(Context context) {
        if (manager == null) {
            manager = new AccountManager();
        }
        manager.mContext = context;
        return manager;
    }

    public void resendRegistrationEmail (String email) {
        new ResendEmailTask(email).execute();
        Toast.makeText(mContext, "Email Resent", Toast.LENGTH_SHORT).show();
    }

    public void recoverPassword(String email) {
        new RecoverPassword(email).execute();
        Toast.makeText(mContext, "Your password has been sent to your email", Toast.LENGTH_LONG).show();
    }

    public void updateNewsfeedSettings(String status, String uid, String distance,
                                       String gender, String straight, String gay, String bi) {
        new UpdateNewsfeedSettingsTask(uid, distance, gender, straight, gay, bi, status).execute();
    }

    public void deletePicture(String uid, String url) {
        new DeletePictureTask(uid, url).execute();

    }
    public void changeProfilePicture(String uid, String url) {
        new ChangeProfilePictureTask(uid, url).execute();
    }

    /**
     * Gets the User's id
     *
     * @return user's id
     */
    public String getUserId() {
        return PreferenceManager.getDefaultSharedPreferences(mContext).getString("uid", "");
    }

    public NewsfeedPrefData getNewsfeedPreferences() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        NewsfeedPrefData prefData = new NewsfeedPrefData();
        prefData.status = prefs.getString("relationship_status_pref", "both");
        prefData.straight = prefs.getString("orientation_pref_straight", "yes");
        prefData.gay = prefs.getString("orientation_pref_gay", "yes");
        prefData.bi = prefs.getString("orientation_pref_bi", "yes");
        prefData.gender = prefs.getString("gender_pref", "both");
        prefData.distance = prefs.getString("distance_pref", "800");
        return prefData;
    }

    private class DeletePictureTask extends AsyncTask<Void, Void, Void> {
        String uid;
        String url;

        public DeletePictureTask(String uid, String url) {
            this.uid = uid;
            this.url = url;
        }

        @Override
        protected Void doInBackground(Void... params) {
            ConnectionManager connectionManager = new ConnectionManager();
            connectionManager.setMethod(ConnectionManager.POST);
            connectionManager.addParam("uid", uid);
            connectionManager.addParam("url", url);
            connectionManager.setUri("deletepicture.php");
            connectionManager.sendHttpRequest();
            return null;
        }
    }

    private class ChangeProfilePictureTask extends AsyncTask<Void, Void, Void> {
        String uid;
        String url;

        public ChangeProfilePictureTask(String uid, String url) {
            this.uid = uid;
            this.url = url;
        }

        @Override
        protected Void doInBackground(Void... params) {
            ConnectionManager connectionManager = new ConnectionManager();
            connectionManager.setMethod(ConnectionManager.POST);
            connectionManager.addParam("uid", uid);
            connectionManager.addParam("url", url);
            connectionManager.setUri("changeprofilepicture.php");
            connectionManager.sendHttpRequest();
            return null;
        }
    }

    private class ResendEmailTask extends AsyncTask<Void, Void, Void> {
        String email;

        public ResendEmailTask(String email) {
            this.email = email;
        }

        @Override
        protected Void doInBackground(Void... params) {
            ConnectionManager connectionManager = new ConnectionManager();
            connectionManager.addParam("email", email);
            connectionManager.setMethod(ConnectionManager.POST);
            connectionManager.setUri("resendemail.php");
            connectionManager.sendHttpRequest();
            return null;
        }
    }

    private class RecoverPassword extends AsyncTask<Void, Void, Void> {
        String email;

        public RecoverPassword(String email) {
            this.email = email;
        }

        @Override
        protected Void doInBackground(Void... params) {
            ConnectionManager connectionManager = new ConnectionManager();
            connectionManager.addParam("email", email);
            connectionManager.setMethod(ConnectionManager.POST);
            connectionManager.setUri("recoverpassword.php");
            connectionManager.sendHttpRequest();
            return null;
        }
    }

    private class ChangePasswordTask extends AsyncTask<Void, Void, Void> {
        String currentpassword;
        String uid;
        String newpassword;

        public ChangePasswordTask(String currentpassword, String uid, String newpassword) {
            this.currentpassword = currentpassword;
            this.uid = uid;
            this.newpassword = newpassword;
        }

        @Override
        protected Void doInBackground(Void... params) {
            return null;
        }
    }

    public void uploadImage(String path, String uid, String text, Context context) {
        new UploadImageTask(path, uid, text, context).execute();
    }

    public void updateStatus (String status, String uid) {
        new UpdateStatusTask(status, uid).execute();
    }

    private class UploadImageTask extends AsyncTask<Void, Void, Void> {
        String pathToPicture;
        String uid;
        String text;
        Context context;

        public UploadImageTask(String pathToPicture, String uid, String text, Context context) {
            this.pathToPicture = pathToPicture;
            this.uid = uid;
            this.text = text;
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(context, "Uploading image", Toast.LENGTH_LONG).show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            ConnectionManager connectionManager = new ConnectionManager();
            try {
                URL url = new URL("http://www.swiftkay.com/findme/uploadstatusimage.php");
                connectionManager.uploadFile(url, pathToPicture, uid, text);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private class UpdateStatusTask extends AsyncTask<Void, Void, Void> {
        String status;
        String uid;

        public UpdateStatusTask(String status, String uid) {
            this.status = status;
            this.uid = uid;
        }

        @Override
        protected Void doInBackground(Void... params) {
            ConnectionManager connectionManager = new ConnectionManager();
            connectionManager.setMethod(ConnectionManager.POST);
            connectionManager.setUri("updatestatus.php");
            connectionManager.addParam("uid", uid);
            connectionManager.addParam("status", status);
            String result = connectionManager.sendHttpRequest();
            return null;
        }
    }

    private class UpdateNewsfeedSettingsTask extends AsyncTask<Void, Void, Void> {
        String uid;
        String distance;
        String gender;
        String straight;
        String gay;
        String bi;
        String status;

        public UpdateNewsfeedSettingsTask(String uid, String distance, String gender, String straight, String gay, String bi, String status) {
            this.uid = uid;
            this.distance = distance;
            this.gender = gender;
            this.straight = straight;
            this.gay = gay;
            this.bi = bi;
            this.status = status;
        }

        @Override
        protected Void doInBackground(Void... params) {
            ConnectionManager connectionManager = new ConnectionManager();
            connectionManager.setMethod(ConnectionManager.POST);
            connectionManager.setUri("updatenewsfeedsettings.php");
            connectionManager.addParam("uid", uid);
            connectionManager.addParam("status", status);
            connectionManager.addParam("gender", gender);
            connectionManager.addParam("straight", straight);
            connectionManager.addParam("gay", gay);
            connectionManager.addParam("bi", bi);
            connectionManager.addParam("distance", distance);
            connectionManager.sendHttpRequest();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
            SharedPreferences.Editor editor = prefs.edit();

            editor.putString("relationship_status_pref", status);
            editor.apply();
            editor.putString("gender_pref", gender);
            editor.apply();
            editor.putString("orientation_pref_straight", straight);
            editor.apply();
            editor.putString("orientation_pref_gay", gay);
            editor.apply();
            editor.putString("orientation_pref_bi", bi);
            editor.apply();
            editor.putString("distance_pref", distance);
            editor.apply();
        }
    }
}
