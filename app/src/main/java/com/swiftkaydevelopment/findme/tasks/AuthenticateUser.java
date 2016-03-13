package com.swiftkaydevelopment.findme.tasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.swiftkaydevelopment.findme.data.AppConstants;
import com.swiftkaydevelopment.findme.managers.ConnectionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Kevin Haines on 8/22/15.
 */
public class AuthenticateUser {

    public interface AuthenticationCompleteListener{
        void onAuthenticationComplete(int result);
    }

    public static final String TAG = "AuthenticateUser";
    public static final int RESULT_SUCCESSFUL = 1;
    public static final int RESULT_REGISTRATION_NOT_COMPLETE = 0;
    public static final int RESULT_ERROR = -1;
    public static final int RESULT_FAILED = -2;

    private static Context mContext;
    private static CopyOnWriteArrayList<AuthenticationCompleteListener> mListeners = new CopyOnWriteArrayList<>();
    private static AuthenticateUser instance = null;

    //String variables
    private String email;       //email to be sent to server
    private String password;    //users password to be sent to server

    public static AuthenticateUser getInstance(Context context){
        if (instance == null) {
            instance = new AuthenticateUser();
        }
        instance.mContext = context;
        return instance;
    }

    public void addListener(AuthenticationCompleteListener listener) {
        mListeners.add(listener);
    }
    public void removeListener(AuthenticationCompleteListener listener) {mListeners.remove(listener); }

    public void authenticate(String email, String password) {
        if (email != null && password != null) {
            new LoginUser(email, password).execute();
        }
    }

    public static class LoginUser extends AsyncTask<Void, Void, String> {
        String email;
        String password;

        public LoginUser(String email, String password) {
            this.email = email;
            this.password = password;
        }

        @Override
        protected String doInBackground(Void... params) {

            ConnectionManager connectionManager = new ConnectionManager();
            connectionManager.addParam("email", email);
            connectionManager.addParam("password", password);
            connectionManager.setMethod(ConnectionManager.POST);
            connectionManager.setUri("login.php");

            String result = connectionManager.sendHttpRequest();
            if (result != null) {
                return result;
            } else {
                return "error";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            /**
             * User's Login details do not match what the server has
             */
            if (result.equals("denied")) {
                for (AuthenticationCompleteListener l : mListeners) {
                    if (l != null) {
                        l.onAuthenticationComplete(RESULT_FAILED);
                    }
                }
                /**
                 * User did not confirm their email yet
                 */
            } else if (result.equals("reg")) {
                for (AuthenticationCompleteListener l : mListeners) {
                    if (l != null) {
                        l.onAuthenticationComplete(RESULT_REGISTRATION_NOT_COMPLETE);
                    }
                }
                /**
                 * There was some error processing the request
                 */
            } else if (result.equals("error")) {
                for (AuthenticationCompleteListener l : mListeners) {
                    if (l != null) {
                        l.onAuthenticationComplete(RESULT_ERROR);
                    }
                }
            } else {

                try {
                    JSONObject obj = new JSONObject(result);

                    SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);

                    SharedPreferences.Editor editor = mPreferences.edit();
                    editor.putString("uid", obj.getJSONObject("info").getString("uid"));
                    editor.apply();
                    if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
                        editor.putString(AppConstants.PreferenceConstants.PREF_EMAIL, email);
                        editor.apply();
                        editor.putString(AppConstants.PreferenceConstants.PREF_PASSWORD, password);
                        editor.apply();
                    }
                    editor.putString(AppConstants.PreferenceConstants.PREF_ZIP, obj.getJSONObject("info").getString("zip"));
                    editor.apply();
                    editor.putString("firstname", obj.getJSONObject("info").getString("fn"));
                    editor.apply();
                    editor.putString("lastname", obj.getJSONObject("info").getString("ln"));
                    editor.apply();
                    editor.putString("gender", obj.getJSONObject("info").getString("ge"));
                    editor.apply();
                    editor.putString("looking_for_gender", obj.getJSONObject("info").getString("lfg"));
                    editor.apply();
                    editor.putString("propicloc", obj.getJSONObject("info").getString("pic"));
                    editor.apply();

                    for (AuthenticationCompleteListener l : mListeners) {
                        if (l != null) {
                            l.onAuthenticationComplete(RESULT_SUCCESSFUL);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    for (AuthenticationCompleteListener l : mListeners) {
                        if (l != null) {
                            l.onAuthenticationComplete(RESULT_ERROR);
                        }
                    }
                }
            }
        }
    }
}
