package com.swiftkaytech.findme.activity;

import android.app.ProgressDialog;
import android.content.Context;
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
import com.swiftkaytech.findme.managers.AccountManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by swift on 6/30/2015.
 */
public class UpdateStatus extends BaseActivity {

    TextView tvcounter;
    EditText etstatus;
    ProgressDialog pDialog;

    int charleft = 2000;
    int textcount = 0;
    final int STARTCOUNT = 2000;

    @Override
    protected int getLayoutResource() {
        return R.layout.updatestatus;
    }

    @Override
    protected Context getContext() {
        return this;
    }

    @Override
    protected void createActivity(Bundle inState) {
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
                    tvcounter.setTextColor(Color.WHITE);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.updateStatusSend) {
            if (etstatus.toString().equals("")) {
                //todo: add error message
            } else {
                AccountManager.getInstance(UpdateStatus.this).updateStatus(etstatus.getText().toString(), uid);
                finish();
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
}

