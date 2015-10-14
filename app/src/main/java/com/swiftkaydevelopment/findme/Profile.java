package com.swiftkaydevelopment.findme;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

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
 * Created by khaines178 on 9/13/15.
 */
public class Profile extends AppCompatActivity {



    List<NewsFeedFrag.Posts> plist;

    //profile information
    String name;
    String age;
    String city;
    String gender;
    String aboutme;
    String propicloc;
    String lp = "0";
    String uid;
    SharedPreferences prefs;
    Context context;
    String ouid;
    ListView lv;
    LayoutInflater inflater;
    ProgressBar pb;

    BaseAdapter adapter;
    ImageLoader imageLoader;



    TextView tvname,tvage,tvaboutme;
    ImageView ivprofilepicture;

    //booleans
    boolean self = false;//this is set to true if the profile being viewed is the users own profile
    boolean refreshing;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {


        getMenuInflater().inflate(R.menu.matches_menu, menu);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.backbuttontwo);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        if(name != null) {
            actionBar.setTitle(name);
        }

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profilefrag);
        context = this;
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        uid = getUID();
        ouid = VarHolder.ouid;
        if(uid.equals(ouid)){
            self = true;
        }
        plist = new ArrayList<NewsFeedFrag.Posts>();
        imageLoader = new ImageLoader(context);
        setGUI();


    }

    private String getUID() {//---------------------------------------------------------------------<<getUID>>
        String KEY = "uid";
        return prefs.getString(KEY,null);
    }//----------------------------------------------------------------------------------------------<</getUID>>

    private void setGUI(){

        lv = (ListView) findViewById(R.id.lvprofile);
        pb = (ProgressBar) findViewById(R.id.pbprofile);

        new GetProfileInformation().execute();

    }

    private View getProfileHeader(){

        View header = inflater.inflate(R.layout.profileheader,null);
        tvname = (TextView) header.findViewById(R.id.tvprofilename);
        tvaboutme = (TextView) header.findViewById(R.id.tvprofileaboutme);
        tvage = (TextView) header.findViewById(R.id.tvprofileage);
        ivprofilepicture = (ImageView) header.findViewById(R.id.ivprofilepicture);
        tvage.setText(age + "/" + gender + "/" + city);
        tvname.setText(name);
        tvaboutme.setText(aboutme);

        if(propicloc.equals("null")){
            ivprofilepicture.setImageResource(R.drawable.ic_placeholder);
        }else{
            imageLoader.DisplayImage(propicloc,ivprofilepicture,true);
        }


        return header;
    }

    private void addUser(){

    }
    private void messageUser(){

    }

    private void reportSpam(){

    }


    private class GetProfileInformation extends AsyncTask<String, String, String> {

        String webResponse;

        @Override
        protected String doInBackground(String... params) {
            // Create a new HttpClient and Post Header


            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(getString(R.string.ipaddress) + "getprofileinfo.php");


            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                nameValuePairs.add(new BasicNameValuePair("ouid", ouid));
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
            Log.w(VarHolder.TAG, "get profile info: " + result);
            pb.setVisibility(View.GONE);
            lv.setVisibility(View.VISIBLE);



            //initialize json objects
            try {

                JSONObject obj = new JSONObject(result);
                JSONArray jarray = obj.getJSONArray("info");
                //add data to plist


                JSONObject childJSONObject = jarray.getJSONObject(0);
                name = childJSONObject.getString("name");
                age = childJSONObject.getString("dob");
                city = childJSONObject.getString("city");
                gender = childJSONObject.getString("gender");
                aboutme = childJSONObject.getString("aboutme");
                propicloc = childJSONObject.getString("propicloc");




            }catch (JSONException e){
                e.printStackTrace();
            }

            lv.addHeaderView(getProfileHeader());

            if(refreshing){

                adapter = new NewsFeedAdapter(context, plist, uid, lv);
                lv.setAdapter(adapter);

                //else notifydatasetchanged
            }else{
                BaseAdapter a = (BaseAdapter) lv.getAdapter();
                if(a == null) {
                    adapter = new NewsFeedAdapter(context, plist, uid, lv);
                    lv.setAdapter(adapter);
                }else {
                    a.notifyDataSetChanged();
                    Log.e("kevin", "datasetchanged");
                }
            }

        }
    }


    private class GetUsersPosts extends AsyncTask<String, String, String> {

        String webResponse;

        @Override
        protected String doInBackground(String... params) {
            // Create a new HttpClient and Post Header


            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(getString(R.string.ipaddress) + "getprofileposts.php");


            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                nameValuePairs.add(new BasicNameValuePair("ouid", ouid));
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
            Log.w(VarHolder.TAG, "get profile info: " + result);
            pb.setVisibility(View.GONE);
            lv.setVisibility(View.VISIBLE);

            lv.addHeaderView(getProfileHeader());

            if(refreshing){

                adapter = new NewsFeedAdapter(context, plist, uid, lv);
                lv.setAdapter(adapter);

                //else notifydatasetchanged
            }else{
                BaseAdapter a = (BaseAdapter) lv.getAdapter();
                if(a == null) {
                    adapter = new NewsFeedAdapter(context, plist, uid, lv);
                    lv.setAdapter(adapter);
                }else {
                    a.notifyDataSetChanged();
                    Log.e("kevin", "datasetchanged");
                }
            }

        }
    }
}
