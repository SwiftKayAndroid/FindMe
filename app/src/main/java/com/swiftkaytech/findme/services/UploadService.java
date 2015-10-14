package com.swiftkaytech.findme.services;

import java.io.File;
import java.util.Random;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
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
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class UploadService extends Activity {


    /**
     * UploadService.java
     *
     * popDialog()
     * Prompt user for camera or from gallery for image
     * if(camera)
     *      start camera activity
     *      snap photo
     *      accept photo
     *      imagechanged = true
     *      store photo location
     * if(gallery)
     *      start gallery select
     *      user chooses photo
     *      imagechanged = true
     *      store photo location
     *
     * updateImage();
     *
     * getUID()
     *      get the current users id and store as string
     *
     *      Initialize UI--initializeGUI();
     *      get photo from stored photo location
     *      display photo in imageview
     *
     *  wait for user to input caption or select upload
     *
     *  on upload clicked
     *      make sure an image is selected
     *      start service to upload photo
     */

    private static final int PICK_IMAGE = 1;
    private ImageView imgView;
    private TextView upload;
    private EditText caption;
    private Bitmap bitmap;
    private ProgressDialog dialog;
    String uid;
    SharedPreferences prefs;

    private static int RESULT_LOAD_IMAGE = 1;
    private static final int REQUEST_CODE = 1;
    boolean camera = false;

    String pathToPicture;



    //booleans
    boolean imagechanged = false;

    boolean initialized = false;

    /** Called when the activity is first created. */
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

        if(camera){
            //leave blank because all the work has been done already

        }else {
            //if selected from gallery
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

    public void updateImage(){
        if(!initialized){
           initializeGUI();
        }

        //next we have to update the imageview to show our selected image
        File imgFile = new  File(pathToPicture);

        if(imgFile.exists()){

            Bitmap bm = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

            imgView.setImageBitmap(bm);

        }






    }
    public void initializeGUI(){
        initialized = true;
        setContentView(R.layout.uploadimage);
        //set up the UI components
        imgView = (ImageView) findViewById(R.id.ivuploadimageimage);
        upload = (TextView) findViewById(R.id.tvuploadimageupload);
        caption = (EditText) findViewById(R.id.etuploadimagetext);
        upload.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                if (imagechanged == false) {
                    Toast.makeText(getApplicationContext(),
                            "Please select image", Toast.LENGTH_SHORT).show();
                } else {

                    new ImageUploadTask(UploadService.this).execute(pathToPicture,caption.getText().toString(),uid,"uploadstatusimage.php");
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
