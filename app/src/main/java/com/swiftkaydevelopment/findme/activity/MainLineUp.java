package com.swiftkaydevelopment.findme.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.squareup.picasso.Picasso;
import com.swiftkaydevelopment.findme.BuildConfig;
import com.swiftkaydevelopment.findme.R;
import com.swiftkaydevelopment.findme.database.DatabaseManager;
import com.swiftkaydevelopment.findme.gcm.QuickstartPreferences;
import com.swiftkaydevelopment.findme.gcm.RegistrationIntentService;
import com.swiftkaydevelopment.findme.managers.UserManager;
import com.swiftkaydevelopment.findme.newsfeed.NewsFeedFrag;
import com.swiftkaydevelopment.findme.views.CircleTransform;

/**
 * Created by Kevin Haines on 2/5/2015.
 */
public class MainLineUp extends BaseActivity {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private NavigationView          mNavigationView;
    private ActionBarDrawerToggle   drawerToggle;
    private DrawerLayout            drawerLayout;

    /**
     * Creates a new Intent to start this activity
     *
     * @param context Calling Context
     * @return new Intent to start this activity with
     */
    public static Intent createIntent(Context context){
        return new Intent(context, MainLineUp.class);
    }

    @Override
    public int getLayoutResource() {
        return R.layout.mainlineup;
    }

    @Override
    protected Context getContext() {
        return this;
    }

    @Override
    protected void createActivity(Bundle b) {
        mToolbar = (Toolbar) findViewById(R.id.toolbarNavigation);
        //todo: get new assets and remove ic menu from drawable
        mToolbar.setNavigationIcon(R.mipmap.ic_menu_white_24dp);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        setSupportActionBar(mToolbar);

        initializeDrawer();

        registerGCMReceiver();
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);
        boolean sentToken = sharedPreferences
                .getBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false);
        if (checkPlayServices() && !sentToken) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(Gravity.LEFT)) {
            drawerLayout.closeDrawer(Gravity.LEFT);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    /**
     * Registers the Gcm Broadcast receiver
     *
     */
    private void registerGCMReceiver() {
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(context);
                boolean sentToken = sharedPreferences
                        .getBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false);
                if (!sentToken) {
                    Toast.makeText(MainLineUp.this, getString(R.string.token_error_message), Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     *
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    /**
     * Initializes the Navigation Drawer
     *
     */
    public void initializeDrawer(){
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavigationView = (NavigationView) findViewById(R.id.nvMainNavigation);
        View headerView = mNavigationView.inflateHeaderView(R.layout.drawerheader);

        setUpHeader(headerView);

        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                selectMenuItem(menuItem);
                drawerLayout.closeDrawers();
                return false;
            }
        });

        drawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close) {

            @Override
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };

        drawerLayout.setDrawerListener(drawerToggle);

        selectMenuItem(mNavigationView.getMenu().findItem(R.id.menuNewsfeed));
    }

    /**
     * Handles navigation view item selection
     *
     * @param menuItem the menu to select
     */
    private void selectMenuItem(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.menuNewsfeed:
                if (getSupportFragmentManager().findFragmentByTag(NewsFeedFrag.TAG) == null) {
                    NewsFeedFrag newsFeedFrag = NewsFeedFrag.getInstance(uid);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frame_container, newsFeedFrag, NewsFeedFrag.TAG)
                            .addToBackStack(null)
                            .commit();
                }
                break;
            case R.id.menuConnect:
                startActivity(FindPeopleActivity.createIntent(MainLineUp.this));
                break;
            case R.id.menuProfileViews:
                startActivity(ProfileViewsActivity.createIntent(this));
                break;
            case R.id.menuMatches:
             startActivity(MatchActivity.createIntent(this));
                break;
            case R.id.menuPhotos:
                startActivity(ViewPhotos.createIntent(this, UserManager.getInstance(uid).me()));
            break;
            case R.id.menuSettings:
//                launchPrefs();
                break;
        }
    }

    /**
     * Sets up the navigation drawer header view
     *
     * @param header Header View
     */
    private void setUpHeader(View header) {
        ImageView headermessage = (ImageView) header.findViewById(R.id.ivdrawerheadermessages);
        ImageView headernotes = (ImageView) header.findViewById(R.id.ivdrawerheadernotes);
        ImageView headerfriends = (ImageView) header.findViewById(R.id.ivdrawerheadfriends);
//        ImageView headermatch = (ImageView) header.findViewById(R.id.ivdrawerheadermatch);
        TextView tvname = (TextView) header.findViewById(R.id.tvmatchmymatches);
        ImageView ivusersphoto = (ImageView) header.findViewById(R.id.ivdrawerusersphoto);
        RelativeLayout headerlay = (RelativeLayout) header.findViewById(R.id.rldrawerheader);
        LinearLayout headermenu = (LinearLayout) header.findViewById(R.id.fggf);

        tvname.setText(prefs.getString("firstname", null) + " " + prefs.getString("lastname", null));
        String imgloc = prefs.getString("propicloc", "");
        if (!TextUtils.isEmpty(imgloc)) {
            if (BuildConfig.GLIDE) {
                Picasso.with(this)
                        .load(imgloc)
                        .transform(new CircleTransform(this))
                        .into(ivusersphoto);
            } else {
                Picasso.with(this)
                        .load(imgloc)
                        .transform(new CircleTransform(this))
                        .into(ivusersphoto);
            }
        } else {
            if (BuildConfig.GLIDE) {
                Picasso.with(this)
                        .load(R.drawable.ic_placeholder)
                        .transform(new CircleTransform(this))
                        .into(ivusersphoto);
            }
            Picasso.with(this)
                    .load(R.drawable.ic_placeholder)
                    .transform(new CircleTransform(this))
                    .into(ivusersphoto);
        }

        tvname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(ProfileActivity.createIntent(MainLineUp.this, UserManager.getInstance(uid).me()));
            }
        });

        ivusersphoto.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(ProfileActivity.createIntent(MainLineUp.this, UserManager.getInstance(uid).me()));
            }
        });

        headermessage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(MessagesListActivity.createIntent(MainLineUp.this));
            }
        });

        headernotes.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(NotificationActivity.createIntent(MainLineUp.this));
            }
        });

        headerfriends.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(FriendsActivity.createIntent(MainLineUp.this));
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.navigation_menu, menu);
        if (BuildConfig.DEBUG) {
            menu.findItem(R.id.navMenuClearDatabases).setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START);
        } else if (item.getItemId() == R.id.navMenuClearDatabases) {
            DatabaseManager.instance(this).clearDatabases();
        }
        return super.onOptionsItemSelected(item);
    }
}