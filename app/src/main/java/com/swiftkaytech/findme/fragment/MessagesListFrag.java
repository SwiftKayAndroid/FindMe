package com.swiftkaytech.findme.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.swiftkaytech.findme.R;
import com.swiftkaytech.findme.adapters.MessageThreadsAdapter;
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
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by BN611 on 3/9/2015.
 */
public class MessagesListFrag extends Fragment {


    public class MessageThreads{
        public String uid;
        public String propicloc;
        public String time;
        public String message;
        public String name;
        public String threadid;
        public boolean readstat;
        public boolean seenstat;
    }

    public static List<MessageThreads> mlist;

    //primitives

    //Strings
    String uid;

    //booleans
    boolean refreshing = false;


    //Objects
    SharedPreferences prefs;
    Context context;

    //gui elements
    ListView lvthreads;


    public MessagesListFrag(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.context = getActivity();
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        uid = getUID();
        mlist = new ArrayList<MessageThreads>();

        new GetMessageThreads().execute();

        View layout = inflater.inflate(R.layout.messageslistfrag, container, false);
        lvthreads = (ListView) layout.findViewById(R.id.lvmessageslist);

        return layout;


    }

    private String getUID() {//---------------------------------------------------------------------<<getUID>>
        String KEY = "uid";
        return prefs.getString(KEY,null);
    }//----------------------------------------------------------------------------------------------<</getUID>>


    private class GetMessageThreads extends AsyncTask<String, String, String> {

        String webResponse;

        @Override
        protected String doInBackground(String... params) {
            // Create a new HttpClient and Post Header


            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(getString(R.string.ipaddress) + "getmessagelist.php");


            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);

                nameValuePairs.add(new BasicNameValuePair("uid", uid));



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

            if (result.equals("error")) {
                Toast.makeText(context, "Could't connect to Find Me", Toast.LENGTH_LONG).show();
            } else {
                try {

                    JSONObject obj = new JSONObject(result);
                    JSONArray jarray = obj.getJSONArray("ppl");

                    Log.e("kevin", result);
                    if(refreshing)
                        mlist.clear();

                    for(int i = 0;i<jarray.length();i++) {
                        JSONObject childJSONObject = jarray.getJSONObject(i);
                        mlist.add(new MessageThreads());
                        mlist.get(mlist.size()-1).uid = childJSONObject.getString("ouid");
                        mlist.get(mlist.size()-1).propicloc = childJSONObject.getString("propicloc");
                        mlist.get(mlist.size()-1).message = childJSONObject.getString("message");
                        mlist.get(mlist.size()-1).time = childJSONObject.getString("time");
                        mlist.get(mlist.size()-1).name = childJSONObject.getString("name");
                        mlist.get(mlist.size()-1).threadid = childJSONObject.getString("threadid");
                        if(childJSONObject.getString("readstat").equals("yes")) {
                            mlist.get(mlist.size() - 1).readstat = true;
                        }else{
                            mlist.get(mlist.size() - 1).readstat = false;
                        }
                        if(childJSONObject.getString("seenstat").equals("yes")){
                            mlist.get(mlist.size() - 1).seenstat = true;
                        }else{
                           mlist.get(mlist.size() - 1).seenstat = false;
                        }


                    }

                    //choose your favorite adapter
                    lvthreads.setAdapter(new MessageThreadsAdapter(context, mlist, lvthreads, uid));
                    lvthreads.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                        @Override
                        public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                            new AlertDialog.Builder(context)


                                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    })

                                    .setPositiveButton("Delete Thread", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {

                                            new DeleteThread().execute(mlist.get(position).threadid);
                                        }
                                    })

                                    .show();
                            return true;
                        }
                    });
                    lvthreads.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            VarHolder.ouid = mlist.get(position).uid;
                            VarHolder.ouname = mlist.get(position).name;
                            VarHolder.threadid = mlist.get(position).threadid;
                            Intent mesinline = new Intent("com.swiftkaytech.findme.MESSAGESINLINE");
                            startActivity(mesinline);
                        }
                    });



                }catch(JSONException e){
                    e.printStackTrace();

                }

            }
        }

    }


    private class DeleteThread extends AsyncTask<String,String,String>{

        String webResponse;
        @Override
        protected String doInBackground(String... params) {
            String threadid = params[0];

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(getString(R.string.ipaddress) + "deleteallmessages.php");


            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);

                nameValuePairs.add(new BasicNameValuePair("uid", uid));
                nameValuePairs.add(new BasicNameValuePair("threadid", threadid));
                nameValuePairs.add(new BasicNameValuePair("authkey", VarHolder.authkey));



                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));

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

            Log.w(VarHolder.TAG, "delete message result: " + result);





        }
    }



}
