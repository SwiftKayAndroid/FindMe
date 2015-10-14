package com.swiftkaytech.findme.fragment;

import android.support.v7.app.ActionBar;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
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
 * Created by khaines178 on 9/7/15.
 */
public class Commentsfrag extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{

    //comment objects are used to store in arraylist to display
    class Comments{

        String name;            //commenting users name
        String comment;         //comment
        String propicloc;       //commenting users profile picture
        String time;            //amount of time since the comment was posted
        String commentid;       //unique id of the comment as stored in the database
        String commentusersid;  //user id for the person who commented

    }


    List<Comments> plist;       //arraylist of Comments objects

    public Commentsfrag(){}     //empty contructor for fragment initialization

    //booleans
    boolean refreshing = false;

    //strings
    String uid;
    String lp = "0";            //this is the last commentid returned from the last call to GetComments().execute();

    //objects
    ImageLoader imageLoader;
    Context context;
    SharedPreferences prefs;
    BaseAdapter adapter;
    LayoutInflater inflater;

    //gui elements
    EditText etcomment;
    ImageView ivpost;
    SwipeRefreshLayout refreshLayout;
    ListView lv;

    //header gui elements
    ImageView propic;
    TextView name;
    TextView time;
    TextView status;
    TextView locationtwo;
    TextView likey;
    TextView commy;
    ImageView postiv;
    ImageView likepost;


    VarHolder.status stat;



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

            getMenuInflater().inflate(R.menu.main, menu);

            return true;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.commentsfrag);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        stat = VarHolder.status;
        this.context = this;
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        uid = getUID();
        imageLoader = new ImageLoader(context);
        plist = new ArrayList<Comments>();
        inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        setGUI();
        //refreshLayout.setRefreshing(true);
        new GetComments().execute();

    }

    private void setGUI(){


        lv = (ListView) findViewById(R.id.lvcommentspop);
        etcomment = (EditText) findViewById(R.id.etcommentonpost);
        ivpost = (ImageView) findViewById(R.id.ivcommentpost);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container_comments);

        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.black,
                android.R.color.holo_blue_bright,
                android.R.color.black);


        ivpost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comment = etcomment.getText().toString();
                if(!comment.equals("")) {
                    new SendComment().execute(comment);
                    etcomment.setText("");
                    View view = getCurrentFocus();
                    if(view != null) {
                        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                }
            }
        });

        View header = inflater.inflate(R.layout.commentsheader,null,false);
        propic = (ImageView) header.findViewById(R.id.ivnewsfeedprofilepic);
        name = (TextView) header.findViewById(R.id.newsfeedusername);
        time = (TextView) header.findViewById(R.id.newsfeedposttime);
        locationtwo = (TextView) header.findViewById(R.id.tvnewsfeedlocationone);
        postiv = (ImageView) header.findViewById(R.id.ivnewsfeedpostedimage);
        status = (TextView) header.findViewById(R.id.tvnewsfeedposttext);
        likey = (TextView) header.findViewById(R.id.tvnewsfeedrownumlikes);
        likepost = (ImageView) header.findViewById(R.id.ivnewsfeedrowlike);
        commy = (TextView) header.findViewById(R.id.tvnewsfeedrownumcomments);

        if(!stat.propiclocation.equals("null")) {
            imageLoader.DisplayImage(stat.propiclocation, propic, false);
        }else{
            propic.setImageResource(R.drawable.ic_placeholder);
        }
        name.setText(stat.name);
        time.setText(stat.posttime);
        locationtwo.setText(stat.postinguserslocation);

        if(stat.postimagelocation.equals("null")){
            postiv.setVisibility(View.GONE);
        }else{
            imageLoader.DisplayImage(stat.postimagelocation,postiv,true);
        }
        status.setText(stat.post);

        if(stat.numlikes.equals("0")) {
            likey.setText("No likes");
        }else{
            int thisnumlikes = Integer.parseInt(stat.numlikes);
            if(thisnumlikes>1){
                likey.setText(stat.numlikes + " like");
            }else{
                likey.setText(stat.numlikes + " likes");
            }

        }

        //-----------LIKE POST BUTTON IMAGEVIEW--------------->>>>>>
        if(stat.liked){
            likepost.setImageResource(R.drawable.like);
        }else {
            likepost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    stat.liked = true;
                    likepost.setImageResource(R.drawable.like);
                    PostLike pl = new PostLike();
                    pl.execute(uid, stat.postid);

                    int templikes = Integer.parseInt(stat.numlikes);
                    templikes++;
                    if (templikes > 1) {
                        likey.setText(Integer.toString(templikes) + "likes");
                    } else {
                        likey.setText(Integer.toString(templikes) + "like");
                    }
                }
            });
        }



        //-----------NUMBER OF COMMENTS TEXTVIEW---------------->>>>>

        if(stat.numcomments.equals("0")){
            commy.setText("No comments");
        }else if(stat.numcomments.equals("1")){
            commy.setText("1 comment");
        }else{
            commy.setText(stat.numcomments + " comments");
        }



        lv.addHeaderView(header);


        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.backbuttontwo);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setTitle("Comments");

    }

    @Override
    public void onRefresh() {
        refreshing = true;
        refreshLayout.setRefreshing(true);
        lp = "0";
        Log.d(VarHolder.TAG,"Refreshing Comments");
        new GetComments().execute();

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

    private String getUID() {//---------------------------------------------------------------------<<getUID>>
        String KEY = "uid";
        return prefs.getString(KEY,null);
    }//----------------------------------------------------------------------------------------------<</getUID>>

    private class GetComments extends AsyncTask<String,String,String> {

        String webResponse;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }


        @Override
        protected String doInBackground(String... params) {

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(getString(R.string.ipaddress) + "getcomments.php");

            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("uid", uid));
                nameValuePairs.add(new BasicNameValuePair("lp", lp));
                nameValuePairs.add(new BasicNameValuePair("postid", stat.postid));

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
            Log.d(VarHolder.TAG, result);

            refreshLayout.setRefreshing(false);

            //check to see if response is error
            //if error display error message
            if (result.equals("error")) {
                Toast.makeText(context, "Could't connect to Find Me", Toast.LENGTH_LONG).show();
            } else {
                //if refreshing clear plist
                if (refreshing) {
                    plist.clear();
                }

                //initialize json objects
                try {

                    JSONObject obj = new JSONObject(result);
                    JSONArray jarray = obj.getJSONArray("comments");
                    //add data to plist


                    for(int i = 0;i<jarray.length();i++) {
                        JSONObject childJSONObject = jarray.getJSONObject(i);
                        plist.add(new Comments());
                        plist.get(plist.size() - 1).commentid = childJSONObject.getString("id");
                        plist.get(plist.size() - 1).comment = childJSONObject.getString("comment");
                        plist.get(plist.size() - 1).propicloc = childJSONObject.getString("propicloc");
                        plist.get(plist.size() - 1).commentusersid = childJSONObject.getString("commentusersid");
                        plist.get(plist.size() - 1).name = childJSONObject.getString("name");
                        plist.get(plist.size() - 1).time = childJSONObject.getString("time");

                    }


                    if(refreshing){

                        adapter = new CommentsAdapter(context, plist);
                        lv.setAdapter(adapter);

                        //else notifydatasetchanged
                    }else{
                        BaseAdapter a = (BaseAdapter) lv.getAdapter();
                        if(a == null) {
                            adapter = new CommentsAdapter(context, plist);
                            lv.setAdapter(adapter);
                        }else {
                            a.notifyDataSetChanged();
                            Log.e("kevin", "datasetchanged");
                        }
                    }


                    //set last post
                    if(plist.size()>0) {
                        lp = plist.get(plist.size() - 1).commentid;
                    }
                    lv.setSelection(plist.size() -1);
                    refreshing = false;



                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }
    }









    //======================================================================= POST LIKE ----->

    class PostLike extends AsyncTask<String,String,String> {

        String webResponse;
        @Override
        protected String doInBackground(String... params) {
            String uid = params[0];
            String postid = params[1];

            // Create a new HttpClient and Post Header

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(context.getString(R.string.ipaddress) + "likepost.php");

            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                nameValuePairs.add(new BasicNameValuePair("uid", uid));
                nameValuePairs.add(new BasicNameValuePair("postid", postid));

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
            Log.d("kevin", "postlike result: " + result);
        }
    }


    private class SendComment extends AsyncTask<String,String,String>{
        String webResponse;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... params) {
            String comment = params[0];

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(getString(R.string.ipaddress) + "postcomment.php");

            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                nameValuePairs.add(new BasicNameValuePair("uid", uid));
                nameValuePairs.add(new BasicNameValuePair("comment", comment));
                nameValuePairs.add(new BasicNameValuePair("postid", stat.postid));


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
            Log.d(VarHolder.TAG, "post comment result: " + result);
            refreshing = true;
            refreshLayout.setRefreshing(false);
            lp = "0";
            new GetComments().execute();



        }
    }



}
