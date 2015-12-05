package com.swiftkaydevelopment.findme.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.swiftkaydevelopment.findme.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by khaines178 on 9/11/15.
 */
public class MyMatches  extends AppCompatActivity {

    public class Matches{
        public String uid;
        public String propicloc;
        public String name;
        public String dob;
        public String city;
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

        }

        return true;
    }
}
