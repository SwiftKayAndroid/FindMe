package com.swiftkaytech.findme.fragment;

import android.support.v7.app.ActionBar;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
 * Created by Kevin Haines on 3/2/2015.
 */
public class FriendsFrag extends AppCompatActivity {

    SharedPreferences prefs;
    Context context;
    String uid;


    MyAdapter mAdapter;
    ViewPager mPager;

    LayoutInflater inflater;

    public FriendsFrag(){}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main, menu);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.backbuttontwo);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setTitle("Friends");

        return true;

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friendsfrag);
        this.context = this;
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        uid = getUID();
        inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        mAdapter = new MyAdapter(getSupportFragmentManager());

        mPager = (ViewPager)findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);
        mPager.setCurrentItem(0);



        final TabHolder holder = (TabHolder) findViewById(R.id.tabholder);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        float logicalDensity = metrics.density;
        holder.initialize(logicalDensity);

        View mTabone = inflater.inflate(R.layout.mytab,null,false);
        TextView tvtabone = (TextView) mTabone.findViewById(R.id.tvtabtext);
        mTabone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.currentSelected!=0){
                    holder.setSelected(0);
                    mPager.setCurrentItem(0,true);
                }
            }
        });
        tvtabone.setText("Friend Requests");
        holder.addTab(mTabone);

        View mTabtwo = inflater.inflate(R.layout.mytab,null,false);
        TextView tvtabtwo = (TextView) mTabtwo.findViewById(R.id.tvtabtext);
        mTabtwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.currentSelected != 1) {
                    holder.setSelected(1);
                    mPager.setCurrentItem(1,true);
                }
            }
        });
        tvtabtwo.setText("Friends");
        holder.addTab(mTabtwo);
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                holder.setSelected(position);
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });



    }

    private String getUID() {//---------------------------------------------------------------------<<getUID>>
        String KEY = "uid";
        return prefs.getString(KEY,null);
    }//----------------------------------------------------------------------------------------------<</getUID>>




}
