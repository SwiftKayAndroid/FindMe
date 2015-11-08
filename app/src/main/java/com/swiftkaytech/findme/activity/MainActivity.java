package com.swiftkaytech.findme.activity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.content.Intent;
import android.widget.TextView;
import android.widget.Toast;

import com.swiftkaytech.findme.R;
import com.swiftkaytech.findme.tasks.AuthenticateUser;


public class MainActivity extends Activity {

    Button btncreate, btnlogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean saved = prefs.getBoolean("loginsaved",false);

        if (saved) {
            String credemail = prefs.getString("email", null);
            String credpassword = prefs.getString("password", null);

            if (credemail == null || credpassword == null) {
                SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(this);

                SharedPreferences.Editor editor = mPreferences.edit();
                editor.putBoolean("loginsaved", false);
                editor.apply();
                setGUI();
            }

            AuthenticateUser au = AuthenticateUser.getInstance(this);
            au.authenticate(prefs.getString("email", null), prefs.getString("password", null));
            startActivity(MainLineUp.createIntent(this));
        } else {
            setGUI();
        }

    }

    private void setGUI(){

        setContentView(R.layout.activity_main);
        btncreate = (Button) findViewById(R.id.btnstartpageregister);
        btnlogin = (Button) findViewById(R.id.btnstartpagelogin);

        btncreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent("com.swiftkaytech.findme.BASICINFO");
                startActivity(i);

            }
        });
        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent("com.swiftkaytech.findme.LOGINPAGE");
                startActivity(i);
            }
        });
    }
}
