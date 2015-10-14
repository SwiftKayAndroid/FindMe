package com.swiftkaydevelopment.findme.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
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
 * Created by swift on 6/18/2015.
 */
public class Registration extends Activity {


    EditText etemail,etpass,etpassconfirm,etzip;
    Button btnreg;
    ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//Remove title bar
        setContentView(R.layout.registration);

        setGUI();
        setListeners();


    }


    public void setGUI(){

        etemail = (EditText) findViewById(R.id.etregemail);
        etpass = (EditText) findViewById(R.id.etregpass);
        etpassconfirm = (EditText) findViewById(R.id.etregpassconfirm);
        etzip = (EditText) findViewById(R.id.etregisterzip);
        btnreg = (Button) findViewById(R.id.btnregister);


    }

    private void setListeners(){
        btnreg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etemail.getText().toString();
                String pass = etpass.getText().toString();
                String passcon = etpassconfirm.getText().toString();
                String zip = etzip.getText().toString();

                if(email.equals("")||!(email.contains("@"))){
                    etemail.setText("");
                    etemail.setHint("Invalid Email");
                    etemail.setHintTextColor(Color.RED);


                }else if(pass.equals("")){
                    Toast.makeText(Registration.this, "Please enter a password", Toast.LENGTH_LONG).show();
                }else if(!(pass.equals(passcon))){
                    Toast.makeText(Registration.this, "The passwords do not match", Toast.LENGTH_LONG).show();
                }else if(zip.length()!=5){
                    Toast.makeText(Registration.this, "Invalid Zip", Toast.LENGTH_LONG).show();
                }else{

                    new Register().execute(email,pass,zip);

                }


            }
        });


    }


    private class Register extends AsyncTask<String,String,String> {
        String webResponse;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(Registration.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... params) {

            String email = params[0];
            String password = params[1];
            String zip = params[2];
            String firstname = VarHolder.firstname;
            String lastname = VarHolder.lastname;
            String dob = VarHolder.dob;
            String gender = VarHolder.gender;

            String[] dobarray = dob.split("/");
            String newdob = dobarray[2] + "-" + dobarray[0] + "-" + dobarray[1];

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(getString(R.string.ipaddress) + "register.php");

            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                nameValuePairs.add(new BasicNameValuePair("email", email));
                nameValuePairs.add(new BasicNameValuePair("password", password));
                nameValuePairs.add(new BasicNameValuePair("zip", zip));
                nameValuePairs.add(new BasicNameValuePair("firstname", firstname));
                nameValuePairs.add(new BasicNameValuePair("lastname", lastname));
                nameValuePairs.add(new BasicNameValuePair("dob", newdob));
                nameValuePairs.add(new BasicNameValuePair("gender", gender));
                nameValuePairs.add(new BasicNameValuePair("authkey", "1781"));

                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request

                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                webResponse = httpclient.execute(httppost, responseHandler);


            } catch (ClientProtocolException e) {
                e.printStackTrace();
                webResponse = "error";
                Log.e("kevin", "error connection refused");


            } catch (IOException e) {
                e.printStackTrace();
                webResponse = "error";

            }


            return webResponse;
        }


            @Override
            protected void onPostExecute(String result){
                super.onPostExecute(result);
                // Dismiss the progress dialog
                if (pDialog.isShowing())
                    pDialog.dismiss();
                Log.e("kevin", result);

                if(result.contains("accepted")){
                    Toast.makeText(Registration.this, "Please confirm email and log in", Toast.LENGTH_LONG).show();
                    Intent i = new Intent("com.swiftkaytech.findme.LOGINPAGE");
                    startActivity(i);


                }else{
                    Toast.makeText(Registration.this, "There was an error setting up your account email may be in use", Toast.LENGTH_LONG).show();
                }





        }
    }


    }
