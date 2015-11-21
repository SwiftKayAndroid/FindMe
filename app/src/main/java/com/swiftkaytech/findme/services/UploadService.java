package com.swiftkaytech.findme.services;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.swiftkaytech.findme.R;
import com.swiftkaytech.findme.managers.AccountManager;

import java.io.File;
import java.util.Random;


public class UploadService extends AppCompatActivity {

    private static final int PICK_IMAGE = 1;
    private ImageView imgView;
    private TextView upload;
    private EditText caption;
    private Bitmap bitmap;
    private ProgressDialog dialog;
    private String uid;
    SharedPreferences prefs;

    private static int RESULT_LOAD_IMAGE = 1;
    private static final int REQUEST_CODE = 1;
    boolean camera = false;

    String pathToPicture;

    //booleans
    boolean imagechanged = false;

    boolean initialized = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        popDialog();
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        uid = getUID();
    }
    private String getUID() {//---------------------------------------------------------------------<<getUID>>
        String KEY = "uid";
        return prefs.getString(KEY,null);
    }//----------------------------------------------------------------------------------------------<</getUID>>

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (!camera) {
                if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getContentResolver().query(selectedImage,
                            filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor.getString(columnIndex);
                    pathToPicture = picturePath;
                    cursor.close();
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
        upload = (TextView) findViewById(R.id.tvuploadimageupload);
        caption = (EditText) findViewById(R.id.etuploadimagetext);
        upload.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                if (imagechanged == false) {
                    Toast.makeText(getApplicationContext(),
                            "Please select image", Toast.LENGTH_SHORT).show();
                } else {
                    AccountManager.getInstance(UploadService.this).uploadImage(pathToPicture, uid, caption.getText().toString());
                    finish();
                }
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
                        Random randy = new Random();
                        String filePath =
                                Environment.getExternalStorageDirectory() +"/img" + Integer.toString(randy.nextInt(1000000)) + ".jpeg";
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
                        finish();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
