package com.swiftkaytech.findme.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
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
public class NotificationsFrag extends Fragment {

    class Notification{
        String note;
        String noteid;
        String picloc;
        String type;
        String time;
    }

    List<Notification> nlist;

    //objects
    SharedPreferences prefs;
    Context context;
    NotificationsAdapter adapter;


    //gui elements
    ListView lv;


    //primitive

    //Strings
    String uid;
    String lp = "0";

    public NotificationsFrag(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.notificationfrag, container,false);
        context = getActivity();
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        uid = getUID();

        setGUI(layout);

        nlist = new ArrayList<Notification>();

        return layout;
    }

    private void setGUI(View layout){
        lv = (ListView) layout.findViewById(R.id.lvnotifications);

    }

    private String getUID() {//---------------------------------------------------------------------<<getUID>>
        String KEY = "uid";
        return prefs.getString(KEY,null);
    }//----------------------------------------------------------------------------------------------<</getUID>>



    private class GetNotifications extends AsyncTask<String,String,String>{

        String webResponse;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(getString(R.string.ipaddress) + "getfriendrequests.php");


            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);

                nameValuePairs.add(new BasicNameValuePair("uid", uid));
                nameValuePairs.add(new BasicNameValuePair("lp", lp));



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

            Log.w(VarHolder.TAG,result);

            try {

                JSONObject obj = new JSONObject(result);
                JSONArray jarray = obj.getJSONArray("notes");


                for (int i = 0; i < jarray.length(); i++) {
                    JSONObject childJSONObject = jarray.getJSONObject(i);
                    nlist.add(new Notification());
                    nlist.get(nlist.size() - 1).note = childJSONObject.getString("note");
                    nlist.get(nlist.size() - 1).picloc = childJSONObject.getString("propicloc");
                    nlist.get(nlist.size() - 1).time = childJSONObject.getString("time");
                    nlist.get(nlist.size() - 1).noteid = childJSONObject.getString("noteid");
                    nlist.get(nlist.size() - 1).type = childJSONObject.getString("type");


                }

                adapter = new NotificationsAdapter(context,nlist);
                lv.setAdapter(adapter);


            }catch(JSONException e){
                e.printStackTrace();
            }






        }
    }




}
