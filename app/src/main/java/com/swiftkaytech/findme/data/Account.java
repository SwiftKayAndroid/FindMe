package com.swiftkaytech.findme.data;

import android.os.AsyncTask;

import com.swiftkaytech.findme.managers.ConnectionManager;

/**
 * Created by Kevin Haines on 10/21/15.
 */
public class Account {
    /**
     * this class is responsible for handling anything to do with the current users account. It will be possible to
     * have more than one account.
     */

    public static void registerGCM(String uid, String token) {
        new RegisterGCMTask(uid, token).execute();
    }

    private static class RegisterGCMTask extends AsyncTask<Void,Void,Void> {
        String token;
        String uid;

        public RegisterGCMTask(String uid, String token) {
            this.token = token;
            this.uid = uid;
        }

        @Override
        protected Void doInBackground(Void... params) {
            ConnectionManager connectionManager = new ConnectionManager();
            connectionManager.addParam("regid", token);
            connectionManager.addParam("uid", uid);
            connectionManager.setMethod(ConnectionManager.POST);
            connectionManager.setUri("registergcm.php");
            connectionManager.sendHttpRequest();
            return null;
        }
    }
}
