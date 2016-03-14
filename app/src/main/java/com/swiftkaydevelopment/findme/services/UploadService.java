package com.swiftkaydevelopment.findme.services;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.swiftkaydevelopment.findme.R;
import com.swiftkaydevelopment.findme.activity.BaseActivity;
import com.swiftkaydevelopment.findme.managers.AccountManager;

import java.io.File;
import java.util.UUID;


public class UploadService extends BaseActivity {

    private static final String INIT = "INIT";
    private static final String ARG_PIC = "ARG_PIC";

    private ImageView imgView;
    private EditText caption;

    private static int RESULT_LOAD_IMAGE = 1;
    boolean camera = false;

    String pathToPicture;

    public static Intent createIntent(Context context) {
        Intent i = new Intent(context, UploadService.class);
        return i;
    }

    //booleans
    boolean imagechanged = false;
    boolean initialized = false;


    @Override
    protected int getLayoutResource() {
        return 0;
    }

    @Override
    protected Context getContext() {
        return this;
    }

    @Override
    protected void createActivity(Bundle inState) {

        if (inState != null) {
            initialized = inState.getBoolean(INIT);
            pathToPicture = inState.getString(ARG_PIC);
        }
        if (!initialized) {
            popDialog();
        } else {
            initializeGUI();
            updateImage();
        }
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(ARG_PIC, pathToPicture);
        outState.putBoolean(INIT, initialized);
        super.onSaveInstanceState(outState);
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

            imagechanged = true;
            updateImage();
        }
    }

    public void updateImage(){
        if(!initialized){
            initializeGUI();
        }

        File imgFile = new  File(pathToPicture);

        if(imgFile.exists()){
            Bitmap bm = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            imgView.setImageBitmap(bm);
        }
    }

    public void initializeGUI(){
        initialized = true;
        setContentView(R.layout.uploadimage);

        imgView = (ImageView) findViewById(R.id.ivuploadimageimage);
        caption = (EditText) findViewById(R.id.etuploadimagetext);

        Toolbar toolbar = (Toolbar) findViewById(R.id.uploadImageToolbar);
        toolbar.inflateMenu(R.menu.update_status_menu);
        toolbar.setNavigationIcon(R.mipmap.ic_arrow_back_white_24dp);
        toolbar.setTitle("Upload Image");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.updateStatusSend) {
                    if (!imagechanged) {
                        Toast.makeText(getApplicationContext(),
                                "Please select image", Toast.LENGTH_SHORT).show();
                    } else {Toast.makeText(getApplicationContext(),
                            "Image posted successfully", Toast.LENGTH_SHORT).show();
                        AccountManager.getInstance(UploadService.this).uploadImage(pathToPicture, uid, caption.getText().toString(), UploadService.this);
                        finish();
                    }
                    return true;
                }
                return false;
            }
        });

        imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popDialog();
            }
        });
    }

    public void popDialog(){

        new AlertDialog.Builder(UploadService.this)
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
                .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (!initialized) {
                            finish();
                        }
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
