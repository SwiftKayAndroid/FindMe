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
import android.os.AsyncTask;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.net.URI;
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

    public void uploadImage(String path, String uid, String text) {
        new UploadImageTask(path, uid, text).execute();
    }

    public void updateStatus (String status, String uid) {
        new UpdateStatusTask(status, uid).execute();
    }

    private class UploadImageTask extends AsyncTask<Void, Void, Void> {
        String pathToPicture;
        String uid;
        String text;

        public UploadImageTask(String pathToPicture, String uid, String text) {
            this.pathToPicture = pathToPicture;
            this.uid = uid;
            this.text = text;
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
}
