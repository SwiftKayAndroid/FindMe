package com.swiftkaydevelopment.findme.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.crashlytics.android.Crashlytics;
import com.swiftkaydevelopment.findme.R;
import com.swiftkaydevelopment.findme.tasks.AuthenticateUser;

import io.fabric.sdk.android.Fabric;


public class MainActivity extends AppCompatActivity {

    private Button btncreate, btnlogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean saved = prefs.getBoolean("loginsaved", false);

        if (saved) {
            String credemail = prefs.getString("email", null);
            String credpassword = prefs.getString("password", null);

            if (credemail == null || credpassword == null) {
                SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor editor = mPreferences.edit();
                editor.putBoolean("loginsaved", false);
                editor.apply();

                setGUI();
            } else {
                AuthenticateUser au = AuthenticateUser.getInstance(this);
                au.authenticate(prefs.getString("email", null), prefs.getString("password", null));
                startActivity(MainLineUp.createIntent(this));
                finish();
            }
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
