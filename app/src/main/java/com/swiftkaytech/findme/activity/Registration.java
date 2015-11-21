package com.swiftkaytech.findme.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
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

import com.swiftkaytech.findme.R;
import com.swiftkaytech.findme.managers.ConnectionManager;

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

    EditText etemail, etpass, etpassconfirm, etzip;
    Button btnreg;
    ProgressDialog pDialog;

    public static Intent createIntent(Context context, String firstname, String lastname,
                                      String dob, String gender) {
        Intent i = new Intent(context, Registration.class);
        i.putExtra("firstname", firstname);
        i.putExtra("lastname", lastname);
        i.putExtra("dob", dob);
        i.putExtra("gender", gender);
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//Remove title bar
        setContentView(R.layout.registration);

        etemail = (EditText) findViewById(R.id.etregemail);
        etpass = (EditText) findViewById(R.id.etregpass);
        etpassconfirm = (EditText) findViewById(R.id.etregpassconfirm);
        etzip = (EditText) findViewById(R.id.etregisterzip);
        btnreg = (Button) findViewById(R.id.btnregister);
        setListeners();
    }

    private void setListeners() {
        btnreg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etemail.getText().toString();
                String pass = etpass.getText().toString();
                String passcon = etpassconfirm.getText().toString();
                String zip = etzip.getText().toString();

                if (email.equals("") || !(email.contains("@"))) {
                    etemail.setText("");
                    etemail.setHint("Invalid Email");
                    etemail.setHintTextColor(Color.RED);


                } else if (pass.equals("")) {
                    Toast.makeText(Registration.this, "Please enter a password", Toast.LENGTH_LONG).show();
                } else if (!(pass.equals(passcon))) {
                    Toast.makeText(Registration.this, "The passwords do not match", Toast.LENGTH_LONG).show();
                } else if (zip.length() != 5) {
                    Toast.makeText(Registration.this, "Invalid Zip", Toast.LENGTH_LONG).show();
                } else {

                    register(email, pass, zip);

                }
            }
        });
    }

    private void register(String email, String pass, String zip) {
        String firstname = getIntent().getExtras().getString("firstname");
        String lastname = getIntent().getExtras().getString("lastname");
        String dob = getIntent().getExtras().getString("dob");
        String gender = getIntent().getExtras().getString("gender");
        new Register(gender, dob, lastname, firstname).execute(email, pass, zip);
    }

    private class Register extends AsyncTask<String, String, String> {
        String webResponse;
        String firstname;
        String lastname;
        String dob;
        String gender;

        public Register(String gender, String dob, String lastname, String firstname) {
            this.gender = gender;
            this.dob = dob;
            this.lastname = lastname;
            this.firstname = firstname;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
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
            dob = dob.replace("/", "-");

            ConnectionManager connectionManager = new ConnectionManager();
            connectionManager.addParam("email", email);
            connectionManager.addParam("password", password);
            connectionManager.addParam("zip", zip);
            connectionManager.addParam("firstname", firstname);
            connectionManager.addParam("lastname", lastname);
            connectionManager.addParam("dob", dob);
            connectionManager.addParam("gender", gender);
            connectionManager.addParam("authkey", "findme_authkey_1781_authentication_token=17811781");
            connectionManager.setMethod(ConnectionManager.POST);
            connectionManager.setUri("register.php");
            String result = connectionManager.sendHttpRequest();
            if (result != null) {
                return result;
            } else {
                return "error";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (pDialog.isShowing())
                pDialog.dismiss();
            Log.e("kevin", result);

            if (result.contains("accepted")) {
                Toast.makeText(Registration.this, "Please confirm email and log in", Toast.LENGTH_LONG).show();
                Intent i = new Intent("com.swiftkaytech.findme.LOGINPAGE");
                startActivity(i);


            } else {
                Toast.makeText(Registration.this, "There was an error setting up your account email may be in use", Toast.LENGTH_LONG).show();
            }
        }
    }

}
