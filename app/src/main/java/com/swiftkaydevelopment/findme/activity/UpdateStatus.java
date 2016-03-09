package com.swiftkaydevelopment.findme.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.swiftkaydevelopment.findme.managers.AccountManager;
import com.swiftkaydevelopment.findme.R;

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

    public static Intent createIntent(Context context) {
        Intent i = new Intent(context, UpdateStatus.class);
        return i;
    }

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

        mToolbar.setNavigationIcon(R.mipmap.ic_arrow_back_white_24dp);
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
            if (etstatus.getText().toString().equals("") || etstatus.getText().toString().length() > 2000) {
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

