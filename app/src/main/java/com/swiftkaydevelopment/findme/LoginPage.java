package com.swiftkaydevelopment.findme;

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

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kevin Haines on 2/5/2015.
 */
public class LoginPage extends Activity {



    //GUI ELEMENTS
    Button btn;
    EditText etemail,etpassword;
    CheckBox cb;
    TextView tvforgot;

    ProgressDialog pDialog;


    //primitive data
    boolean checked = false;



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
                Log.d(VarHolder.TAG, "Checked: " + String.valueOf(checked));
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
        /**
         * Recover
         * this class performs an asyncronous call to httppost to request that the server
         * send the current users password to their email address
         *
         * post info
         * email
         * authkey
         *
         * returned info
         * "accepted"
         * "denied"
         * mysql error message
         */
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


            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://pawnacity.com/swift/passwordrecover.php");



            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                nameValuePairs.add(new BasicNameValuePair("email",email));


                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request

                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                webResponse = httpclient.execute(httppost, responseHandler);





            } catch (ClientProtocolException e) {
                e.printStackTrace();
                webResponse = "error";


            } catch (IOException e) {

                e.printStackTrace();
                webResponse = "error";

            }



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
