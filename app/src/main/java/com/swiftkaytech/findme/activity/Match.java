package com.swiftkaytech.findme.activity;

import android.content.Intent;
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
import android.widget.Toast;

import com.swiftkaytech.findme.R;
import com.swiftkaytech.findme.adapters.CardAdapter;
import com.swiftkaytech.findme.flingswipe.SwipeFlingAdapterView;
import com.swiftkaytech.findme.managers.ConnectionManager;
import com.swiftkaytech.findme.utils.VarHolder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by khaines178 on 8/24/15.
 */
public class Match extends AppCompatActivity {

    public class Users{
        public String uid;
       public String name;
        public String propicloc;
        public String aboutme;


    }

    List<Users> ulist;
    SharedPreferences prefs;

    //GUI ELEMENTS
    SwipeFlingAdapterView flingContainer;
    CardAdapter adapter;

    //PRIMITIVES

    //strings
    String uid;

    //integers
    int i = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tinderlayout);
        ulist = new ArrayList<Users>();
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        uid = getUID();
        setGUI();
    }

    void setGUI(){
        flingContainer = (SwipeFlingAdapterView) findViewById(R.id.frame);

        adapter = new CardAdapter(this,ulist);
        flingContainer.setAdapter(adapter);

        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                // this is the simplest way to delete an object from the Adapter (/AdapterView)
                Log.d("LIST", "removed object!");
                ulist.remove(0);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onLeftCardExit(Object dataObject) {
                //Do something on the left!
                //You also have access to the original object.
                //If you want to use it just cast it (String) dataObject
                Toast.makeText(Match.this, "Left!", Toast.LENGTH_SHORT).show();
                Log.d(VarHolder.TAG,"Left swipe noted");
            }

            @Override
            public void onRightCardExit(Object dataObject) {
                Toast.makeText(Match.this, "Right!", Toast.LENGTH_SHORT).show();
                Log.d(VarHolder.TAG,"Right swipe noted");
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
                // Ask for more data here

                adapter.notifyDataSetChanged();
                Log.d("LIST", "notified. ulist size: " + Integer.toString(ulist.size()));

            }

            @Override
            public void onScroll(float scrollProgressPercent) {

            }
        });
    }



    private String getUID() {//---------------------------------------------------------------------<<getUID>>
        String KEY = "uid";
        return prefs.getString(KEY, null);
    }//----------------------------------------------------------------------------------------------<</getUID>>


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();
        switch (itemId) {
            case android.R.id.home:
                finish();

                // Toast.makeText(this, "home pressed", Toast.LENGTH_LONG).show();
                break;
            case R.id.menumatchesicon:{
                Intent i = new Intent("com.swiftkaytech.findme.MYMATCHES");
                startActivity(i);
            }
            break;

        }

        return true;
    }
//
//    private class GetMatches extends AsyncTask<String,String,String>{
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//        }
//
//        @Override
//        protected String doInBackground(String... params) {
//            String uriAppend = "getmatches.php";
//
//            ConnectionManager connectionManager = new ConnectionManager();
//            connectionManager.setMethod(ConnectionManager.POST);
//            connectionManager.setUri(getString(R.string.ipaddress) + uriAppend);
//            connectionManager.addParam("uid", uid);
//
//            return connectionManager.sendHttpRequest();
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
//            super.onPostExecute(result);
//            Log.w(VarHolder.TAG,"Getmatches result: " + result);
//
//            if(result.equals("error")){
//                Toast.makeText(Match.this, "Could't connect to Find Me", Toast.LENGTH_LONG).show();
//            }else {
//
//                //initialize json objects
//                try {
//
//                    JSONObject obj = new JSONObject(result);
//                    JSONArray jarray = obj.getJSONArray("ppl");
//                    //add data to plist
//
//                    for (int i = 0; i < jarray.length(); i++) {
//                        JSONObject childJSONObject = jarray.getJSONObject(i);
//                        ulist.add(new Users());
//                        ulist.get(ulist.size() - 1).uid = childJSONObject.getString("uid");
//                        ulist.get(ulist.size() - 1).name = childJSONObject.getString("name");
//                        ulist.get(ulist.size() - 1).aboutme = childJSONObject.getString("aboutme");
//                        ulist.get(ulist.size() - 1).propicloc = childJSONObject.getString("propicloc");
//
//
//                    }
//
//                    BaseAdapter a = (BaseAdapter) flingContainer.getAdapter();
//                    a.notifyDataSetChanged();
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
}
