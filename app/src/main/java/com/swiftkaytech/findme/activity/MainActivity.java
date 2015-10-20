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
import com.swiftkaytech.findme.utils.VarHolder;


public class MainActivity extends Activity {

    Button btncreate, btnlogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//Remove title bar

         //Intent i = new Intent("com.swiftkaytech.findme.MAINLINEUP");
        //startActivity(i);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean saved = prefs.getBoolean("loginsaved",false);

        if(saved){

            VarHolder.credemail = prefs.getString("email","error");
            VarHolder.credpassword = prefs.getString("password","error");

            if(VarHolder.credemail.equals("error")||VarHolder.credpassword.equals("error")){
                Log.e(VarHolder.TAG,"There was an issue obtaining credentials from sharedPreferences. email : " + VarHolder.credemail +
                " Password : " + VarHolder.credpassword + " loginsaved is changed to false. ");
                SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(this);

                SharedPreferences.Editor editor = mPreferences.edit();
                editor.putBoolean("loginsaved",false);
                setGUI();
            }else {
              AuthenticateUser au = new AuthenticateUser(this,this);
                au.execute();
            }
        }else {

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
