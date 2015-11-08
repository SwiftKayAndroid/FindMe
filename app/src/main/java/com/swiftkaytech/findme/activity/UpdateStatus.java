package com.swiftkaytech.findme.activity;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.swiftkaytech.findme.R;

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
 * Created by swift on 6/30/2015.
 */
public class UpdateStatus extends AppCompatActivity {

    TextView tvcounter;
    EditText etstatus;
    ProgressDialog pDialog;
    Toolbar mToolbar;

    String uid;

    int charleft = 2000;
    int textcount = 0;
    final int STARTCOUNT = 2000;

    SharedPreferences prefs;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.updateStatusSend) {
            if (etstatus.toString().equals("")) {
                //todo: add error message
            } else {
                new PostStatus().execute(etstatus.getText().toString());
            }
        } else if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.update_status_menu, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.updatestatus);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        uid = getUID();
        tvcounter = (TextView) findViewById(R.id.tvupdatestatuscounter);
        etstatus = (EditText) findViewById(R.id.etupdatestatus);
        mToolbar = (Toolbar) findViewById(R.id.updateStatusToolbar);

        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        mToolbar.setTitle("Update Status");

        setSupportActionBar(mToolbar);

        etstatus.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                textcount = etstatus.getText().toString().length();
                charleft = STARTCOUNT - textcount;
                tvcounter.setText(Integer.toString(charleft));
                if (charleft > 0) {
                    tvcounter.setTextColor(Color.GREEN);
                } else if (charleft == 0) {
                    tvcounter.setTextColor(Color.BLACK);
                } else {
                    tvcounter.setTextColor(Color.RED);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private String getUID() {//---------------------------------------------------------------------<<getUID>>
        String KEY = "uid";
        return prefs.getString(KEY, null);
    }//----------------------------------------------------------------------------------------------<</getUID>>

    private class PostStatus extends AsyncTask<String, String, String> {
        String webResponse;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(UpdateStatus.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... params) {

            String status = params[0];


            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(getString(R.string.ipaddress) + "updatestatus.php");

            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                nameValuePairs.add(new BasicNameValuePair("status", status));
                nameValuePairs.add(new BasicNameValuePair("uid", uid));


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
        protected void onPostExecute(String s) {
            if (pDialog.isShowing()) {
                pDialog.dismiss();
            }
            if(s.equals("accepted")){
                Toast.makeText(UpdateStatus.this,"Status uploaded successfully!", Toast.LENGTH_LONG).show();
                finish();
            }else{
                Toast.makeText(UpdateStatus.this,"There was an error updating your status.", Toast.LENGTH_LONG).show();
                finish();
            }

            //dismiss activity
        }
    }
}

