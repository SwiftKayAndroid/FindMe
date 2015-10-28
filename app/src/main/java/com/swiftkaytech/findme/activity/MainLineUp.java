package com.swiftkaytech.findme.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.swiftkaytech.findme.R;
import com.swiftkaytech.findme.fragment.FindPeopleFrag;
import com.swiftkaytech.findme.fragment.MessagesListFrag;
import com.swiftkaytech.findme.fragment.NavigationDrawerFragment;
import com.swiftkaytech.findme.fragment.NewsFeedFrag;
import com.swiftkaytech.findme.fragment.NotificationsFrag;
import com.swiftkaytech.findme.fragment.ViewPhotosFrag;
import com.swiftkaytech.findme.utils.ImageLoader;
import com.swiftkaytech.findme.utils.VarHolder;

/**
 * Created by Kevin Haines on 2/5/2015.
 */
public class MainLineUp extends BaseActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks{

    static class ProInfo{
        static String name;
        static String propicloc;
        static String activestatus;
        static String backgroundphoto;
    }

    public static Intent createIntent(Context context){
        Intent i = new Intent(context,MainLineUp.class);
        return i;
    }

    @Override
    public int getLayoutResource() {
        return R.layout.mainlineup;
    }
    @Override
    protected Context getContext() {
        return this;
    }

    private ImageLoader imageloader;

    private NavigationDrawerFragment mNavigationDrawerFragment;
    private ListView mListView;//changed from lv
    private DrawerLayout mDrawerLayout;//changed from dl

    ImageView messages,friendrequests,more,notifications,love;
    private ProgressBar loadingmorepb;
    private TextView mTvName;//changed from tvname
    public ImageView mIvUsersPhoto;//changed from ivusersphoto



    @Override
    protected void createActivity() {
        imageloader = new ImageLoader(this);
        mToolbar = (Toolbar) findViewById(R.id.toolbarNavigation);
        mToolbar.setNavigationIcon(R.drawable.ic_menu_white_24dp);
        setSupportActionBar(mToolbar);

        initializeDrawer();

        int displayvalue = getIntent().getIntExtra("displayvalue", 0);
        if(displayvalue != 0){
            displayView(displayvalue);
        }
        else{
            displayView(VarHolder.NEWSFEED);
        }
    }

    public void initializeDrawer(){
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position,ListView lv,DrawerLayout dl) {
        displayView(position);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            getMenuInflater().inflate(R.menu.navigation_menu, menu);
            return true;
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    void displayView(int position) {
        // update the main content by replacing fragments
        Fragment fragment = null;
        boolean restoring = false; //set to true if restoring fragment from backstack instead of new fragment

        switch (position) {

            case VarHolder.NEWSFEED:

                    //fragment = getSupportFragmentManager().findFragmentByTag(Integer.toString(position));
                    fragment = NewsFeedFrag.getInstance(uid);

                break;
            case VarHolder.MESSAGES:

                break;

            case VarHolder.NOTIFICATIONS:

                break;
            case VarHolder.FRIENDS: {
                Intent i = new Intent("com.swiftkaytech.findme.FRIENDS");
                startActivity(i);
            }
                break;
//            case VarHolder.FINDPEOPLE:
//                fragment = new FindPeopleFrag();
//                break;
            case VarHolder.PROFILE:{
            }
            break;

            case VarHolder.USERSPHOTOS:{

                //this will be its own activity, not a fragment
                VarHolder.ouid = uid;

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
            }

            default:
                break;
        }

        if (fragment != null&&!restoring) {
            getSupportFragmentManager().beginTransaction()
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
}


