package com.swiftkaytech.findme.fragment;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.swiftkaytech.findme.R;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by khaines178 on 8/24/15.
 */
public class ViewPhotosFrag extends Fragment {

    Context context;
    SharedPreferences prefs;

    public class Pics{
        public String postpicloc;
        public String postid;
        public String post;
        public boolean lockedstatus;

    }

    List<Pics> plist;

    //PRIMITIVES

    //strings
    String uid;//users id
    String ouid;// this is the user's id for the profile the user is viewing
    String lp;//this is the id for the last picture we received from the server

    //gui elements
    ProgressDialog pDialog;

    public ViewPhotosFrag(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.viewpicturesfrag,container,false);//set the fragment layout
        context = getActivity();
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        lp = "0";
        plist = new ArrayList<Pics>();


        return layout;
    }


    public void getPics(){


        new GetPictures().execute();

    }

    private class GetPictures extends AsyncTask<String,String,String>{

        String webResponse;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(context);
            pDialog.setMessage("Loading...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://pawnacity.com/swift/passwordrecover.php");

            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                nameValuePairs.add(new BasicNameValuePair("email",uid));
                nameValuePairs.add(new BasicNameValuePair("ouid",ouid));
                nameValuePairs.add(new BasicNameValuePair("lp",lp));

                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request

                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                webResponse = httpclient.execute(httppost, responseHandler);


            } catch (ClientProtocolException e) {
                e.printStackTrace();
                webResponse = "error";

            } catch (IOException e) {

                e.printStackTrace();
                webResponse = "error";

            }

            return webResponse;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            /**
             * if the ProgressDialog pDialog is showing(which it should be since we
             * initialized it in onPreExecute()) then dismiss it
             */
            if(pDialog.isShowing())
                pDialog.dismiss();

            try {

                /**
                 * take the returned JSON array and break it down
                 * store in an arraylist
                 * if adapter is null create adapter and assign it to the gridview
                 * else notify adapter that the data set has changed
                 */
                JSONObject obj = new JSONObject(result);
                JSONArray jarray = obj.getJSONArray("pics");

                for(int i = 0;i<jarray.length();i++) {
                    JSONObject childJSONObject = jarray.getJSONObject(i);
                    plist.add(new Pics());
                    plist.get(plist.size() - 1).postpicloc = childJSONObject.getString("postpicloc");
                    plist.get(plist.size() - 1).post = childJSONObject.getString("post");
                    plist.get(plist.size() - 1).postid = childJSONObject.getString("postid");
                    if(childJSONObject.getString("lockedstatus").equals("locked")) {
                        plist.get(plist.size() - 1).lockedstatus = true;
                    }else{
                        plist.get(plist.size() - 1).lockedstatus = false;
                    }
                }


            }catch(JSONException e){
                e.printStackTrace();

            }





        }
    }






}
