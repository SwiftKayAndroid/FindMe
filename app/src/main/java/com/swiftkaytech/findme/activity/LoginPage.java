package com.swiftkaytech.findme.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.swiftkaytech.findme.R;
import com.swiftkaytech.findme.managers.ConnectionManager;
import com.swiftkaytech.findme.tasks.AuthenticateUser;
import com.swiftkaytech.findme.utils.VarHolder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kevin Haines on 2/5/2015.
 */
public class LoginPage extends Activity {

    //GUI ELEMENTS
    private Button btn;
    private EditText etemail,etpassword;
    private CheckBox cb;
    private
    TextView tvforgot;

    ProgressDialog pDialog;

    private boolean checked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//Remove title bar
        setContentView(R.layout.loginpage);

        setGUI();//initialize ui elements
        setListeners();//set ui listeners
    }

    private void setGUI(){
        //ESTABLISH IDENTITIES OF UI ELEMENTS
        //CALLED FROM onCreate()

        btn = (Button) findViewById(R.id.btnloginlogin);
        etemail = (EditText) findViewById(R.id.etloginemail);
        etpassword = (EditText) findViewById(R.id.etloginpassword);
        cb = (CheckBox) findViewById(R.id.cbloginstayloggedin);
        tvforgot = (TextView) findViewById(R.id.tvloginforgotpassword);

    }

    public void setListeners(){
        //SET LISTENERS ON UI ELEMENTS
        //CALLED FROM onCreate()

        //SUBMIT BUTTON
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String e = etemail.getText().toString();//email
                String p = etpassword.getText().toString();//password

                if(e.equals("")||p.equals("")){
                    Toast.makeText(LoginPage.this, "Please make sure all fields are filled in.", Toast.LENGTH_LONG).show();

                }else{

                    SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(LoginPage.this);

                    SharedPreferences.Editor editor = mPreferences.edit();
                    if(checked){
                        editor.putBoolean("loginsaved", true);
                        editor.apply();
                    }else{
                        editor.putBoolean("loginsaved", false);
                        editor.apply();
                    }
                    VarHolder.credemail = e;
                    VarHolder.credpassword = p;
                    AuthenticateUser au = new AuthenticateUser(LoginPage.this,LoginPage.this);
                    au.execute();
                }
            }
        });

        //CHECK BOX FOR STAYING LOGGED IN LISTENER
        cb.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                checked = ((CheckBox)v).isChecked();
                Log.d(VarHolder.TAG,"Checked: " + String.valueOf(checked));
            }
        });

        //TEXT VIEW FOR FORGOTTEN PASSWORD LISTENER
        tvforgot.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String s = etemail.getText().toString();
                if(s.equals("")){
                    Toast.makeText(LoginPage.this, "Please fill in the email field first", Toast.LENGTH_LONG).show();
                }else{
                    Recover r = new Recover();
                    r.execute(s);
                }
            }
        });
    }

    private class Recover extends AsyncTask<String,String,String> {
        String webResponse;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(LoginPage.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String...params) {
            String email = params[0];
            ConnectionManager cm = new ConnectionManager();
            cm.setMethod(ConnectionManager.POST);
            cm.setUri("http://pawnacity.com/swift/passwordrecover.php");
            //todo: impliment this functionality

            return webResponse;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();

            if(result.contains("accepted")){
                String message = "Your login details have been sent to your email.";
                new AlertDialog.Builder(LoginPage.this)
                        .setTitle("Find Me Says....")
                        .setMessage(message)

                        .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })

                        .show();
            }else{
                Toast.makeText(LoginPage.this, "There was an error processing your request", Toast.LENGTH_LONG).show();
            }
        }
    }
}
