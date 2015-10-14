package com.swiftkaydevelopment.findme;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;

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
 * Created by khaines178 on 9/11/15.
 */
public class MyMatches  extends AppCompatActivity {

    class Matches{
        String uid;
        String propicloc;
        String name;
        String dob;
        String city;
    }

    List<Matches> plist;
    String uid;
    SharedPreferences prefs;
    Context context;
    BaseAdapter adapter;
    ListView lv;
    String lp = "0";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mymatches);
        context = this;
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        uid = getUID();
        lv = (ListView) findViewById(R.id.lvmymatcheslist);
        plist = new ArrayList<Matches>();

        new GetMatches().execute();




    }

    private String getUID() {//---------------------------------------------------------------------<<getUID>>
        String KEY = "uid";
        return prefs.getString(KEY, null);
    }//----------------------------------------------------------------------------------------------<</getUID>>

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {


        getMenuInflater().inflate(R.menu.simplemenu, menu);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.backbuttontwo);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setTitle("My Matches");

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();
        switch (itemId) {
            case android.R.id.home:
                finish();

                // Toast.makeText(this, "home pressed", Toast.LENGTH_LONG).show();
                break;

        }

        return true;
    }

    private class GetMatches extends AsyncTask<String,String,String> {
        String webResponse;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(getString(R.string.ipaddress) + "getmatcheslist.php");

            try{
                List<NameValuePair> nvps = new ArrayList<NameValuePair>();
                nvps.add(new BasicNameValuePair("uid",uid));
                nvps.add(new BasicNameValuePair("lp",lp));

                httpPost.setEntity(new UrlEncodedFormEntity(nvps));


                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                webResponse = httpclient.execute(httpPost, responseHandler);


            }catch (ClientProtocolException e) {
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

            Log.w(VarHolder.TAG, "Getmatcheslist result: " + result);

            if(result.equals("error")){
                Toast.makeText(context, "Could't connect to Find Me", Toast.LENGTH_LONG).show();
            }else {

                //initialize json objects
                try {

                    JSONObject obj = new JSONObject(result);
                    JSONArray jarray = obj.getJSONArray("ppl");
                    //add data to plist

                    for (int i = 0; i < jarray.length(); i++) {
                        JSONObject childJSONObject = jarray.getJSONObject(i);
                        plist.add(new Matches());
                        plist.get(plist.size() - 1).uid = childJSONObject.getString("uid");
                        plist.get(plist.size() - 1).name = childJSONObject.getString("name");
                        plist.get(plist.size() - 1).city = childJSONObject.getString("city");
                        plist.get(plist.size() - 1).propicloc = childJSONObject.getString("propicloc");
                        plist.get(plist.size() - 1).dob = childJSONObject.getString("dob");


                    }

                    BaseAdapter a = (BaseAdapter) lv.getAdapter();
                    if (a == null) {
                        adapter = new MyMatchesAdapter(context, plist, uid, lv);
                        lv.setAdapter(adapter);
                    } else {
                        a.notifyDataSetChanged();
                        Log.e("kevin", "datasetchanged");
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


        }
    }
}
