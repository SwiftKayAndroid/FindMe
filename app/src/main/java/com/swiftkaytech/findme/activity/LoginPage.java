package com.swiftkaytech.findme.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import com.swiftkaytech.findme.managers.AccountManager;
import com.swiftkaytech.findme.managers.ConnectionManager;
import com.swiftkaytech.findme.tasks.AuthenticateUser;

/**
 * Created by Kevin Haines on 2/5/2015.
 */
public class LoginPage extends Activity implements AuthenticateUser.AuthenticationCompleteListener{

    @Override
    public void onAuthenticationComplete(int status) {
        switch (status) {
            case AuthenticateUser.RESULT_ERROR: {
                Toast.makeText(this, "There was an error logging you in", Toast.LENGTH_LONG).show();
            }
            break;
            case AuthenticateUser.RESULT_SUCCESSFUL: {
                startActivity(MainLineUp.createIntent(this));
                finish();
            }
            break;
            case AuthenticateUser.RESULT_FAILED: {
                Toast.makeText(this, "Email or password incorrect", Toast.LENGTH_LONG).show();
            }
            break;
            case AuthenticateUser.RESULT_REGISTRATION_NOT_COMPLETE: {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Registration Incomplete");
                builder.setMessage("You haven't confirmed your email yet!");
                builder.setPositiveButton("Ok", null);
                builder.setNegativeButton("Resend Email", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AccountManager.getInstance(LoginPage.this).resendRegistrationEmail(etemail.getText().toString());
                    }
                });
                builder.show();
            }
            break;
            default: {

            }
            break;
        }
    }

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

                if(e.equals("") || p.equals("")){
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
                    AuthenticateUser au = AuthenticateUser.getInstance(LoginPage.this);
                    au.authenticate(e, p);
                    au.addListener(LoginPage.this);
                }
            }
        });

        //CHECK BOX FOR STAYING LOGGED IN LISTENER
        cb.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                checked = ((CheckBox)v).isChecked();
            }
        });

        //TEXT VIEW FOR FORGOTTEN PASSWORD LISTENER
        tvforgot.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String email = etemail.getText().toString();
                if(email.equals("")){
                    Toast.makeText(LoginPage.this, "Please fill in the email field first", Toast.LENGTH_LONG).show();
                }else{
                    AccountManager.getInstance(LoginPage.this).recoverPassword(email);
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
