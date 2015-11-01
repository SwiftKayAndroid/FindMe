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
public class MainLineUp extends BaseActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private static final int NEWSFEED = 1;
    private static final int FINDPEOPLE = 2;
    private static final int MESSAGETHREADS = 7;

    public static Intent createIntent(Context context){
        Intent i = new Intent(context, MainLineUp.class);
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

    private NavigationDrawerFragment mNavigationDrawerFragment;

    @Override
    protected void createActivity(Bundle b) {
        mToolbar = (Toolbar) findViewById(R.id.toolbarNavigation);
        mToolbar.setNavigationIcon(R.drawable.ic_menu_white_24dp);
        setSupportActionBar(mToolbar);

        initializeDrawer();

        int displayvalue = getIntent().getIntExtra("displayvalue", 0);
        if (displayvalue != 0) {
            displayView(displayvalue);
        }
        else{
            displayView(VarHolder.NEWSFEED);
        }
    }

    @Override
    protected Bundle saveState(Bundle b) {
        return b;
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
    public void onNavigationDrawerItemSelected(int position) {
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

    private void displayView(int position) {
        Fragment fragment = null;

        switch (position) {
            case VarHolder.NEWSFEED:
                    fragment = NewsFeedFrag.getInstance(uid);
                break;
            case FINDPEOPLE:
                startActivity(FindPeopleActivity.createIntent(MainLineUp.this));
                break;

            default:
                break;
        }

        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .addToBackStack(Integer.toString(position))
                    .replace(R.id.frame_container, fragment, NewsFeedFrag.TAG)
                    .commit();

            mNavigationDrawerFragment.getListView().setItemChecked(position, true);
            mNavigationDrawerFragment.getListView().setSelection(position);
            mNavigationDrawerFragment.getDrawerLayout().closeDrawer(mNavigationDrawerFragment.getListView());
        } else {
            // error in creating fragment
            Log.e("kevin", "Error in creating fragment or fragment restored from backstack");
        }
    }
}


