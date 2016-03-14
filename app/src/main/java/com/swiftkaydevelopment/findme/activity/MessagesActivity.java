/*
 *      Copyright (C) 2015 Kevin Haines
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 */

package com.swiftkaydevelopment.findme.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.swiftkaydevelopment.findme.R;
import com.swiftkaydevelopment.findme.data.ThreadInfo;
import com.swiftkaydevelopment.findme.data.User;
import com.swiftkaydevelopment.findme.fragment.MessagesFrag;
import com.swiftkaydevelopment.findme.managers.MessagesManager;

import java.io.File;
import java.util.UUID;

public class MessagesActivity extends BaseActivity implements MessagesFrag.PictureMessageListener {
    private static final String TAG = "MessagesActivity";
    private static final String ARG_USER = "ARG_USER";

    private static int RESULT_LOAD_IMAGE = 1;
    boolean camera = false;

    String pathToPicture;

    private User user;
    private MessagesFrag        mMessagesFrag;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_base;
    }

    @Override
    protected Context getContext() {
        return this;
    }

    public static Intent createIntent(Context context, User user) {
        Intent i = new Intent(context, MessagesActivity.class);
        i.putExtra(ARG_USER, user);
        return i;
    }

    @Override
    protected void createActivity(Bundle inState) {

        if (inState != null) {
            user = (User) inState.getSerializable(ARG_USER);
        } else {
            user = (User) getIntent().getExtras().getSerializable(ARG_USER);
        }
       if (getSupportFragmentManager().findFragmentByTag(MessagesFrag.TAG)  == null) {
           mMessagesFrag = MessagesFrag.instance(uid, user);
           getSupportFragmentManager().beginTransaction()
                   .replace(R.id.activityContainer, mMessagesFrag, MessagesFrag.TAG)
                   .addToBackStack(null)
                   .commit();
       }
        setUpToolbar();

    }

    /**
     * Sets up the toolbar for this Activity
     *
     */
    private void setUpToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.baseActivityToolbar);
        mToolbar.setNavigationIcon(R.mipmap.ic_arrow_back_white_24dp);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mToolbar.inflateMenu(R.menu.messages_menu);
        mToolbar.setTitle(user.getFirstname() + " " +  user.getLastname());
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.messagesThreadDeleteAllMessages) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MessagesActivity.this);
                    builder.setTitle("Delete All Messages");
                    builder.setMessage("Are you sure you want to delete all messages");
                    builder.setNegativeButton("Cancel", null);
                    builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ThreadInfo t = ThreadInfo.instance(uid);
                            t.threadId = mMessagesFrag.getThreadId();
                            MessagesManager.getInstance(uid).deleteThread(t);
                        }
                    });
                    builder.show();
                }
                return true;
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (!camera) {
                if (requestCode == RESULT_LOAD_IMAGE && null != data) {
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(selectedImage,
                            filePathColumn, null, null, null);
                    if (cursor != null) {
                        cursor.moveToFirst();
                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        pathToPicture = cursor.getString(columnIndex);
                        cursor.close();
                    }
                }
            }
            mMessagesFrag.onImageSelected(pathToPicture);
        }
    }

    public void popImageDialog(){

        new AlertDialog.Builder(this)
                .setTitle("Please Select")
                .setNeutralButton("Camera", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        camera = true;
                        //launch camera
                        String filePath =
                                Environment.getExternalStorageDirectory() +"/img" + UUID.randomUUID().toString() + ".jpeg";
                        pathToPicture = filePath;
                        File file = new File(filePath);
                        Uri output = Uri.fromFile(file);
                        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, output);
                        startActivityForResult(cameraIntent, 1);
                    }
                })
                .setNegativeButton("Gallery", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // launch gallery
                        camera = false;
                        Intent i = new Intent(
                                Intent.ACTION_PICK,
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                        startActivityForResult(i, RESULT_LOAD_IMAGE);
                    }
                })
                .setPositiveButton("Cancel", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    @Override
    public void onStartImageSelection() {
        popImageDialog();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(ARG_USER, user);
        outState.putString(ARG_UID, uid);
        super.onSaveInstanceState(outState);
    }
}
