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
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.swiftkaytech.findme.R;
import com.swiftkaytech.findme.adapters.FindPeopleAdapter;

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
 * Created by BN611 on 2/25/2015.
 */
public class FindPeopleFrag extends Fragment {

    public class Peeps{

        public String uid;
        public String picloc;
        public String distance;
    }
    List<Peeps> plist;

    ImageView ivtoggle,ivsettings;
    TextView tvtitle;
    GridView gv;

    Context context;
    SharedPreferences prefs;

    String uid;
    String genderpref;
    String zip;
    String lastpost = "0";
    boolean refreshing = false;


    public FindPeopleFrag(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.context = getActivity();
        View layout = inflater.inflate(R.layout.findpeople, container, false);
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        genderpref = prefs.getString("genderpref", "both");
        zip = prefs.getString("zippref","nearme");
        uid = getUID();


        gv = (GridView) layout.findViewById(R.id.gvviewfindpeoplefrag);

        plist = new ArrayList<Peeps>();
        new GetPeople().execute();


        return layout;

    }

    private String getUID() {//---------------------------------------------------------------------<<getUID>>
        String KEY = "uid";
        return prefs.getString(KEY,null);
    }//----------------------------------------------------------------------------------------------<</getUID>>
    public void setGUI(View v){



    }




    private class GetPeople extends AsyncTask<String, String, String> {

        String webResponse;

        @Override
        protected String doInBackground(String... params) {
            // Create a new HttpClient and Post Header


            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(getString(R.string.ipaddress) + "findpeople.php");


            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                nameValuePairs.add(new BasicNameValuePair("gender", genderpref));
                nameValuePairs.add(new BasicNameValuePair("uid", uid));
                nameValuePairs.add(new BasicNameValuePair("zip", zip));
                nameValuePairs.add(new BasicNameValuePair("lastpost", lastpost));


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
                        plist.clear();

                    for(int i = 0;i<jarray.length();i++) {
                        JSONObject childJSONObject = jarray.getJSONObject(i);
                        plist.add(new Peeps());
                        plist.get(plist.size()-1).uid = childJSONObject.getString("uid");
                        plist.get(plist.size()-1).picloc = childJSONObject.getString("propicloc");
                        plist.get(plist.size()-1).distance = childJSONObject.getString("distance");
                        
                    }

                    //choose your favorite adapter
                    if(gv.getAdapter() == null) {
                        gv.setAdapter(new FindPeopleAdapter(context, plist,uid));
                    }else{
                        BaseAdapter a = (BaseAdapter) gv.getAdapter();
                        a.notifyDataSetChanged();
                    }
                    lastpost = plist.get(plist.size() - 1).uid;
                }catch(JSONException e){
                    e.printStackTrace();
                }
            }
        }
    }
}
