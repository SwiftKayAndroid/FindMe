package com.swiftkaytech.findme.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.swiftkaytech.findme.R;
import com.swiftkaytech.findme.adapters.FriendsAdapter;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by khaines178 on 9/9/15.
 */
public class FriendsListFrag extends Fragment {


    public class Friends{
        public String name;
        public String uid;
        public String propicloc;
        public String location;
        public String dob;
    }

    public static List<Friends> flist;
    Context context;
    String uid;
    SharedPreferences prefs;
    BaseAdapter adapter;
    ListView lv;

    boolean isActive;
    boolean refreshing;

    public FriendsListFrag() {
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = getActivity();
        flist = new ArrayList<Friends>();
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        uid = getUID();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(!isActive){

            new GetFriends().execute();

        }else{
            Log.d(VarHolder.TAG, "restoring active friendsrequests");
            BaseAdapter a = (BaseAdapter) lv.getAdapter();
            Log.w(VarHolder.TAG,Integer.toString(flist.size()));
            if(a == null) {
                adapter = new FriendsAdapter(context, flist,uid, lv);
                lv.setAdapter(adapter);
            }else {
                a.notifyDataSetChanged();
                Log.e("kevin", "datasetchanged on friendrequests");
            }
        }
        isActive = true;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.friendslistfrag,container,false);
        lv = (ListView) layout.findViewById(R.id.lvfriends);

        return layout;

    }

    private String getUID() {//---------------------------------------------------------------------<<getUID>>
        String KEY = "uid";
        return prefs.getString(KEY,null);
    }//----------------------------------------------------------------------------------------------<</getUID>>


    private class GetFriends extends AsyncTask<String, String, String> {

        String webResponse;

        @Override
        protected String doInBackground(String... params) {
            // Create a new HttpClient and Post Header


            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(getString(R.string.ipaddress) + "getfriendslist.php");


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
            Log.w(VarHolder.TAG,"Get Friends List: " + result);

            if (result.equals("error")) {
                Toast.makeText(context, "Could't connect to Find Me", Toast.LENGTH_LONG).show();
            } else {
                try {

                    JSONObject obj = new JSONObject(result);
                    JSONArray jarray = obj.getJSONArray("ppl");

                    Log.e("kevin", result);
                    if(refreshing)
                        flist.clear();

                    for(int i = 0;i<jarray.length();i++) {
                        JSONObject childJSONObject = jarray.getJSONObject(i);
                        flist.add(new Friends());
                        flist.get(flist.size()-1).uid = childJSONObject.getString("uid");
                        flist.get(flist.size()-1).propicloc = childJSONObject.getString("propicloc");
                        flist.get(flist.size()-1).location = childJSONObject.getString("location");
                        flist.get(flist.size()-1).dob = childJSONObject.getString("dob");
                        flist.get(flist.size()-1).name = childJSONObject.getString("name");


                    }

                    //choose your favorite adapter
                    BaseAdapter a = (BaseAdapter) lv.getAdapter();
                    Log.w(VarHolder.TAG, Integer.toString(flist.size()));
                    if(a == null) {
                        adapter = new FriendsAdapter(context, flist,uid, lv);
                        lv.setAdapter(adapter);
                    }else {
                        a.notifyDataSetChanged();
                        Log.e("kevin", "datasetchanged on friendrequests");
                    }



                }catch(JSONException e){
                    e.printStackTrace();

                }

            }
        }

    }
}
