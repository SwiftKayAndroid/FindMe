package com.swiftkaydevelopment.findme;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Created by BN611 on 2/5/2015.
 */
public class MainLineUp extends AppCompatActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks{



    static class ProInfo{
        static String name;
        static String propicloc;
        static String activestatus;
        static String backgroundphoto;
    }

    ImageLoader imageloader;
    SharedPreferences prefs;

    //--------- NAVIGATION DRAWER ITEMS ----------->>

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in
     */
    private CharSequence mTitle;
    ListView lv;
    DrawerLayout dl;
    //--------------------------------------------/>>



    //GUI COMPONENTS
    //'''''''''''''''''''''''''''''''''''''''''''''''''
    ImageView messages,friendrequests,more,notifications,love;
    ProgressBar loadingmorepb;
    TextView tvname;
    public static ImageView ivusersphoto;

    Button credits;


    //STRINGS
    String uid;
    String title;
    static int currentselection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme_Light);
        setContentView(R.layout.mainlineup);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        imageloader = new ImageLoader(this);


        //setDrawer(savedInstanceState);
        //setGUI();
        uid = getUID();
        initializeDrawer();

        this.registerReceiver(mBroadcastReceiver, new IntentFilter("start.fragment.changeview"));
        int displayvalue = getIntent().getIntExtra("displayvalue", 0);
        if(displayvalue != 0){
            displayView(displayvalue);

        }
        else{
            displayView(VarHolder.NEWSFEED);

        }
    }


    @Override
    public void onBackPressed() {


    }

    public void initializeDrawer(){

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));


    }

    @Override
    public void onNavigationDrawerItemSelected(int position,ListView lv,DrawerLayout dl) {
        // update the main content by replacing fragments
        this.lv = lv;
        this.dl = dl;

        displayView(position);
    }

    public void restoreActionBar() {
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(android.support.v7.app.ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            displayView(intent.getIntExtra("value", 0));
        }
    };


    private String getUID() {//---------------------------------------------------------------------<<getUID>>
        String KEY = "uid";
        return prefs.getString(KEY,null);
    }//----------------------------------------------------------------------------------------------<</getUID>>




    /**
     * Diplaying fragment view for selected nav drawer list item
     * */
    void displayView(int position) {
        // update the main content by replacing fragments
        android.app.Fragment fragment = null;
        boolean restoring = false; //set to true if restoring fragment from backstack instead of new fragment



        switch (position) {

            case VarHolder.NEWSFEED:
                if(NewsFeedFrag.isActive){
                   // getFragmentManager().popBackStack(Integer.toString(position),
                           // android.app.FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    fragment = getFragmentManager().findFragmentByTag(Integer.toString(position));
                    //restoring = true;
                }else {
                    fragment = new NewsFeedFrag();
                }
                title = "News Feed";
                break;
            case VarHolder.MESSAGES:
                fragment = new MessagesListFrag();
                title = "Messages";
                break;

            case VarHolder.NOTIFICATIONS:
                title = "Notifications";
                fragment = new NotificationsFrag();
                break;
            case VarHolder.FRIENDS: {
                title = "Friends";
                Intent i = new Intent("com.swiftkaytech.findme.FRIENDS");
                startActivity(i);
            }
                break;
            case VarHolder.FINDPEOPLE:
                title = "Find Friends";
                fragment = new FindPeopleFrag();
                break;
            case VarHolder.PROFILE:{
                //this is going to be replaced with its own activity, not a fragment
                // fragment = new ProfileFrag();
            }
            break;

            case VarHolder.USERSPHOTOS:{

                //this will be its own activity, not a fragment
                VarHolder.ouid = uid;
                fragment = new ViewPhotosFrag();
            }
            break;
            case VarHolder.MATCHES:{

                //this will be its own activity
                Intent i = new Intent("com.swiftkaytech.findme.MATCH");
                startActivity(i);
            }
            break;
            case VarHolder.SETTINGS:{
                //this will be its own activity

            }
            break;
            case VarHolder.UPDATESTATUS:{
                //this is its own activity
                Intent i = new Intent("com.swiftkaytech.findme.UPDATESTATUS");
                startActivity(i);
            }
            break;
            case VarHolder.EARNFREECREDITS:{
                //fragment = new EarnFreeCreditsFrag(this,mDrawerLayout,mDrawerList);
            }
            break;

            case VarHolder.STATUSPHOTO:{
                Intent i = new Intent("com.swiftkaytech.findme.UPLOADSERVICE");
                startActivity(i);
            }
            break;
            case VarHolder.COMMENTS:{

                Intent i = new Intent("com.swiftkaytech.findme.COMMENTS");
                startActivity(i);
                title = "Comments";
            }

            default:
                break;
        }

        if(title != null)
        mNavigationDrawerFragment.title = title;
        setTitle(title);
        if (fragment != null&&!restoring) {
            android.app.FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .addToBackStack(Integer.toString(position))
                    .replace(R.id.frame_container, fragment, Integer.toString(position)).commit();

            // update selected item and title, then close the drawer
            mNavigationDrawerFragment.getListView().setItemChecked(position, true);
            mNavigationDrawerFragment.getListView().setSelection(position);


            mNavigationDrawerFragment.getDrawerLayout().closeDrawer(mNavigationDrawerFragment.getListView());
        } else {
            // error in creating fragment
            Log.e("kevin", "Error in creating fragment or fragment restored from backstack");
        }
    }


    public void setTitle(String title){


        getSupportActionBar().setTitle(title);

    }


    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        unregisterReceiver(mBroadcastReceiver);
    }


    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        this.registerReceiver(mBroadcastReceiver, new IntentFilter("start.fragment.changeview"));
    }


}


