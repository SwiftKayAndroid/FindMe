package com.swiftkaytech.findme.tasks;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.swiftkaytech.findme.R;
import com.swiftkaytech.findme.utils.VarHolder;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kevin Haines on 8/22/15.
 */
public class AuthenticateUser extends AsyncTask<String,String,String> {

    //objects
    ProgressDialog pDialog;
    Context context;
    Activity activity;

    //String variables
    private String email;       //email to be sent to server
    private String password;    //users password to be sent to server
    private String authkey;     //key used to verify that this request came from this app
    String response;            //response from the httppost request


    public AuthenticateUser(Context context,Activity activity){

        this.context = context;
        this.activity = activity;

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        // Showing progress dialog
        pDialog = new ProgressDialog(context);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);
        pDialog.show();
        //set post data values
        email = VarHolder.credemail;
        password = VarHolder.credpassword;
        authkey = VarHolder.authkey;

    }

    @Override
    protected String doInBackground(String... params) {

        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(context.getString(R.string.ipaddress) + "login.php");

        try {
            // Add data
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
            nameValuePairs.add(new BasicNameValuePair("email",email));
            nameValuePairs.add(new BasicNameValuePair("password",password));
            nameValuePairs.add(new BasicNameValuePair("authkey",authkey));


            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            // Execute HTTP Post Request

            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            response = httpclient.execute(httppost, responseHandler);


        } catch (ClientProtocolException e) {
            e.printStackTrace();
            response = "error";

        } catch (IOException e) {
            e.printStackTrace();
            response = "error";
        }

        return response;
    }



    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        Log.w(VarHolder.TAG,"http response from authentication: " + result);

        //dismiss progressDialog if showing
        if (pDialog.isShowing())
            pDialog.dismiss();

        //check http post returned data for result
        if(result.equals("denied")){
            //this code runs if the users login info doesn't match what's in the database
            Toast.makeText(context, "Your details do not match what is on file", Toast.LENGTH_LONG).show();

        }else if(result.equals("reg")) {
            //this code is run if the server returns that the user hasnt verified their email
            Toast.makeText(context, "You must verify your email first.", Toast.LENGTH_LONG).show();

        } else{

            try {

                //obtain the values from the json array
                JSONObject obj = new JSONObject(result);
                String uid = obj.getJSONObject("info").getString("uid");
                String firstname = obj.getJSONObject("info").getString("firstname");
                String lastname = obj.getJSONObject("info").getString("lastname");
                String gender = obj.getJSONObject("info").getString("gender");
                String looking_for_gender = obj.getJSONObject("info").getString("looking_for_gender");
                String zip = obj.getJSONObject("info").getString("zip");
                String email = obj.getJSONObject("info").getString("email");
                String propicloc = obj.getJSONObject("info").getString("propicloc");


                SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(context);

                //store returned values to SharedPreferences
                SharedPreferences.Editor editor = mPreferences.edit();
                editor.putString("uid",uid);
                editor.apply();
                editor.putString("email", email);
                editor.apply();
                editor.putString("password",password);
                editor.apply();
                editor.putString("zip", zip);
                editor.apply();
                editor.putString("firstname", firstname);
                editor.apply();
                editor.putString("lastname", lastname);
                editor.apply();
                editor.putString("gender", gender);
                editor.apply();
                editor.putString("looking_for_gender", looking_for_gender);
                editor.apply();
                editor.putString("propicloc", propicloc);
                editor.apply();

                //send user to home page
                Toast.makeText(context, "Login successful.", Toast.LENGTH_LONG).show();
                Intent home = new Intent("com.swiftkaytech.findme.MAINLINEUP");
                home.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(home);
                activity.finish();

            } catch (JSONException e) {

                e.printStackTrace();
                Toast.makeText(context,"There was an error logging you in",Toast.LENGTH_LONG).show();
            }

        }



    }
}
