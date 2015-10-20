package com.swiftkaytech.findme.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
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
import org.w3c.dom.Text;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import com.swiftkaytech.findme.R;
import com.swiftkaytech.findme.adapters.NewsFeedAdapter;
import com.swiftkaytech.findme.com.shamanland.fab.ShowHideOnScroll;
import com.swiftkaytech.findme.utils.VarHolder;

/**
 * Created by kevin haines on 2/8/2015.
 */
public class NewsFeedFrag extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    public class Posts{
        public String postinguserid;
        public String firstname;
        public String lastname;
        public String posttime;
        public String post;
        public String postimagelocation;
        public String postinguserslocation;
        public String numlikes;
        public String[] likeduserids;
        public String numcomments;
        public String propiclocation;
        public String postid;
        public boolean liked = false;
        public String lastcomment;
        public String lastcommentid;
        public String lastcommentuid;
        public String lastcommentpropicloc;
        public String lastcommentname;
        public String lastcommenttime;
    }

    //initialize prefs
    SharedPreferences prefs;

    String lp = "0";
    String uid;



    //LISTVIEW DATA
    List<Posts> plist;
    NewsFeedAdapter adapter;


    //BOOLEANS
    boolean statuspost = false;
    boolean loadingMore = false;
    static boolean active = true;
    boolean shrinkview = false;
    boolean refreshing = false;
    public static boolean fabvisible = false;


    //NEWSFEED GUI ELEMENTS

    TextView poststatus;
    TextView postphoto;
    ProgressBar pb;

    View fab;//floating action button
    public static View fabstatus;//floating action button
    public static View fabphoto;//floating action button
    ListView lv;
    SwipeRefreshLayout swipeLayout;


    //UNIVERSAL OBJECTS
    private static LayoutInflater inflater = null;

    Context context;

    int lastpos;
    int maxheight;

    public static boolean isActive;

    android.app.FragmentManager fragmentManager = getFragmentManager();


    public NewsFeedFrag(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(VarHolder.TAG, "creating newsfeedfrag");
        //initialize prefs
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        //get info and store
        this.context = getActivity();
        uid = getUID();
        plist = new ArrayList<Posts>();
        Log.d(VarHolder.TAG, "created newsfeedfrag");

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.d(VarHolder.TAG, "view is created newsfeedfrag");
        if(!isActive) {
            if (hasConnection()) {
//get posts

                runRefresh();

            } else {
                Toast.makeText(context, "No Internet Connection. Please try again!", Toast.LENGTH_LONG).show();
            }
        }else{
            Log.d(VarHolder.TAG, "restoring active newsfeedfrag");
            BaseAdapter a = (BaseAdapter) lv.getAdapter();
            Log.w(VarHolder.TAG,Integer.toString(plist.size()));
            if(a == null) {
                adapter = new NewsFeedAdapter(context, plist, uid, lv);
                lv.setAdapter(adapter);
            }else {
                a.notifyDataSetChanged();
                Log.e("kevin", "datasetchanged");
            }
            pb.setVisibility(View.GONE);
            lv.setVisibility(View.VISIBLE);
            Log.d(VarHolder.TAG, "fragment restored newsfeedfrag");
        }
        isActive = true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //INITIALIZE UI
        View newsfeedview = inflater.inflate(R.layout.newsfeedfrag, container, false);
        setGUI(newsfeedview);
        setListeners();

        return newsfeedview;

    }

    private String getUID() {//---------------------------------------------------------------------<<getUID>>
        String KEY = "uid";
        return prefs.getString(KEY,null);
    }//----------------------------------------------------------------------------------------------<</getUID>>

    @Override
    public void onPause() {
        super.onPause();
        Log.d(VarHolder.TAG, "pausing newsfeedfrag");

    }
    @Override
    public void onResume() {
        Log.d(VarHolder.TAG, "onResume of newsfeedfrag");
        super.onResume();

        if(fabvisible) {
            Animation rotation = AnimationUtils.loadAnimation(context, R.anim.rotate_counterclockwise);
            rotation.setRepeatCount(0);
            rotation.setFillAfter(true);
            fab.startAnimation(rotation);
            fabstatus.setVisibility(View.GONE);
            fabphoto.setVisibility(View.GONE);
            fabvisible = false;


        }

    }

    private void setListeners() {

        fabphoto.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //start activity for photo
                Intent i = new Intent("com.swiftkaytech.findme.UPLOADSERVICE");
                startActivity(i);

            }
        });
        //<--------------	POST STATUS BUTTON LISTENER 	------------->
        fabstatus.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //start activity status
                Intent i = new Intent("com.swiftkaytech.findme.UPDATESTATUS");
                startActivity(i);

            }
        });
    }//end set listeners

    private void setGUI(View nfv) {

        fab = nfv.findViewById(R.id.fab);
        fabstatus = nfv.findViewById(R.id.fabpencil);
        fabphoto = nfv.findViewById(R.id.fabcamera);
        lv = (ListView) nfv.findViewById(R.id.Lvnewsfeed);
        lv.setOnTouchListener(new ShowHideOnScroll(fab));

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!fabvisible) {
                    Animation rotation = AnimationUtils.loadAnimation(context, R.anim.rotate_clockwise);
                    rotation.setRepeatCount(0);
                    rotation.setFillAfter(true);
                    fab.startAnimation(rotation);
                    //fab.setVisibility(View.GONE);
                    fabstatus.setVisibility(View.VISIBLE);
                    fabphoto.setVisibility(View.VISIBLE);
                    fabvisible = true;

                    /*int[] fablocs = new int[2];
                    fab.getLocationOnScreen(fablocs);

                    //fabstatus translation
                    int[] statuslocs = new int[2];
                    fabstatus.getLocationOnScreen(statuslocs);
                    fabstatus.setVisibility(View.VISIBLE);
                    Animation animation = new TranslateAnimation(0, 0,fablocs[1], statuslocs[1]);
                    animation.setDuration(200);
                    animation.setFillAfter(true);
                    fabstatus.startAnimation(animation);*/

                } else {

                    Animation rotation = AnimationUtils.loadAnimation(context, R.anim.rotate_counterclockwise);
                    rotation.setRepeatCount(0);
                    rotation.setFillAfter(true);
                    fab.startAnimation(rotation);
                    //fab.setVisibility(View.GONE);
                    fabstatus.setVisibility(View.GONE);
                    fabphoto.setVisibility(View.GONE);
                    fabvisible = false;
                }
            }
        });
        fabstatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent("com.swiftkaytech.findme.UPDATESTATUS");
                startActivity(i);
            }
        });
        fabphoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent("com.swiftkaytech.findme.UPLOADSERVICE");
                startActivity(i);
            }
        });

        pb = (ProgressBar) nfv.findViewById(R.id.pbnewsfeed);

        swipeLayout = (SwipeRefreshLayout) nfv.findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.black,
                android.R.color.holo_blue_bright,
                android.R.color.black);

    }

    @Override
    public void onRefresh() {
        runRefresh();

    }

    private void runRefresh() {//----------------------------------------------------------------<<runGetPosts>>

        refreshing = true;
        swipeLayout.setRefreshing(true);
        lp = "0";
        GetPosts gp = new GetPosts();
        gp.execute();
        Log.d("kevin", "refreshing news feed");

    }//-------------------------------------------------------------------------------------------<</runGetPosts>>
    private void runGetAdditional() {//----------------------------------------------------------------<<runGetAdditional>>

        refreshing = false;
        loadingMore = true;
        GetPosts gp = new GetPosts();
        gp.execute();
        Log.d("kevin", "getting additional posts" );

    }//-------------------------------------------------------------------------------------------<</runGetAdditional>>


    //<<<<<<==========================================================>> GETPOSTS <<============================================>>>>>
    private class GetPosts extends AsyncTask<String, String, String> {

        String webResponse;

        @Override
        protected String doInBackground(String... params) {
            // Create a new HttpClient and Post Header


            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(getString(R.string.ipaddress)+ "getposts.php");



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

            Log.e("kevin", result);
            //setrefreshing to false
            swipeLayout.setRefreshing(false);
            //remove pb
            pb.setVisibility(View.GONE);
            //check to see if response is error
            //if error display error message
            if (result.equals("error")) {
                Toast.makeText(context, "Could't connect to Find Me", Toast.LENGTH_LONG).show();
            }else {
                //if refreshing clear plist
                if(refreshing) {
                    plist.clear();
                }

                //initialize json objects
                try {

                    JSONObject obj = new JSONObject(result);
                    JSONArray jarray = obj.getJSONArray("posts");
                    //add data to plist

                    for(int i = 0;i<jarray.length();i++) {
                        JSONObject childJSONObject = jarray.getJSONObject(i);
                        plist.add(new Posts());
                        plist.get(plist.size() - 1).postid = childJSONObject.getString("postid");
                        plist.get(plist.size() - 1).firstname = childJSONObject.getString("firstname");
                        plist.get(plist.size() - 1).lastname = childJSONObject.getString("lastname");
                        plist.get(plist.size() - 1).post = childJSONObject.getString("post");
                        plist.get(plist.size() - 1).numcomments = childJSONObject.getString("numcomments");
                        plist.get(plist.size() - 1).numlikes = childJSONObject.getString("numlikes");
                        plist.get(plist.size() - 1).postimagelocation = childJSONObject.getString("postpicloc");
                        plist.get(plist.size() - 1).postinguserid = childJSONObject.getString("postingusersid");
                        plist.get(plist.size() - 1).postinguserslocation = childJSONObject.getString("distance");
                        plist.get(plist.size() - 1).posttime = childJSONObject.getString("time");
                        plist.get(plist.size() - 1).propiclocation = childJSONObject.getString("propicloc");
                        plist.get(plist.size() - 1).lastcomment = childJSONObject.getString("comment");
                        plist.get(plist.size() - 1).lastcommentuid = childJSONObject.getString("commentuid");
                        plist.get(plist.size() - 1).lastcommenttime = childJSONObject.getString("commenttime");
                        plist.get(plist.size() - 1).lastcommentname = childJSONObject.getString("commentusersname");
                        plist.get(plist.size() - 1).lastcommentpropicloc = childJSONObject.getString("commentuserspropicloc");
                        plist.get(plist.size() - 1).lastcommentid = childJSONObject.getString("commentid");


                        JSONArray arr = childJSONObject.getJSONArray("likeduserids");

                        if (arr.length() > 0) {
                            plist.get(plist.size() - 1).likeduserids = new String[arr.length()];
                            for (int e = 0; e < arr.length(); e++) {
                                String s = plist.get(plist.size() - 1).likeduserids[e] = arr.getString(e);
                                if (s.equals(uid)) {
                                    plist.get(plist.size() - 1).liked = true;
                                }
                            }

                        }
                    }

                    //if refreshing set adapter

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


                    //set last post
                    if(plist.size()>0) {
                        lp = plist.get(plist.size() - 1).postid;
                    }
                }catch(JSONException e){
                    e.printStackTrace();

                }

                lv.setOnScrollListener(new AbsListView.OnScrollListener() {

                    //useless here, skip!


                    @Override
                    public void onScrollStateChanged(AbsListView view, int scrollState) {
                    }

                    //dumdumdum

                    @Override
                    public void onScroll(AbsListView view, int firstVisibleItem,

                                         int visibleItemCount, int totalItemCount) {


                        int lastInScreen = firstVisibleItem + visibleItemCount;


                        if (lastInScreen >= lastpos) {
                            shrinkview = true;

                            lastpos = lastInScreen;
                        } else {
                            shrinkview = false;

                            lastpos = lastInScreen;
                        }

                        //is the bottom item visible & not loading more already ? Load more !

                        if ((lastInScreen == totalItemCount) && !(loadingMore) && (plist.size() > 24)) {
                            loadingMore = true;

                            runGetAdditional();


                        }

                    }

                });

            }
            lv.setVisibility(View.VISIBLE);
            loadingMore = false;
            refreshing = false;


        }// if array is null

    }





    public boolean hasConnection() {
        ConnectivityManager cm = (ConnectivityManager) this.getActivity().getSystemService(
                Context.CONNECTIVITY_SERVICE);

        NetworkInfo wifiNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiNetwork != null && wifiNetwork.isConnected()) {
            return true;
        }

        NetworkInfo mobileNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (mobileNetwork != null && mobileNetwork.isConnected()) {
            return true;
        }

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()) {
            return true;
        }

        return false;
    }







}
