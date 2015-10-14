package com.swiftkaydevelopment.findme;

import android.app.AlertDialog;
import android.content.Context;
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
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
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
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by BN611 on 3/11/2015.
 */
public class MessagesFrag extends AppCompatActivity {

    class Messages{
        String tag;
        String senderid;
        String propicloc;
        String time;
        String message;
        String messageid;
        String messageimageloc;
        boolean justsent = false;

    }
    List<Messages> mlist;

    SharedPreferences prefs;
    Context context;
    String uid;
    String ouid;
    boolean refreshing = false;

    ListView lvthreads;
    EditText etmessage;
    ImageView ivsend;
    boolean iconchanged = false;
    ActionBar actionBar;


    //SENDING PICTURE STUFF
    private static final int PICK_IMAGE = 1;
    private static int RESULT_LOAD_IMAGE = 1;
    private static final int REQUEST_CODE = 1;
    boolean camera = false;

    String pathToPicture;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {


        getMenuInflater().inflate(R.menu.messagesinlinemenu, menu);
        actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.backbuttontwo);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setTitle(VarHolder.ouname);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();
        switch (itemId) {
            case android.R.id.home:
                finish();

                // Toast.makeText(this, "home pressed", Toast.LENGTH_LONG).show();
                break;
            case R.id.delete_thread:{
                new AlertDialog.Builder(context)


                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })

                        .setPositiveButton("Delete All", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                new DeleteAllMessages().execute();

                            }
                        })

                        .show();

            }

        }

        return true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.messageinline);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        this.context = this;
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        uid = getUID();
        ouid = VarHolder.ouid;

        mlist = new ArrayList<Messages>();

       new GetMessages().execute();


        lvthreads = (ListView) findViewById(R.id.lvmessagesinline);
        etmessage = (EditText) findViewById(R.id.etmessaget);
        ivsend = (ImageView) findViewById(R.id.tvsendmessage);
        etmessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(etmessage.length()>0&&!iconchanged){
                    ivsend.setImageResource(R.drawable.sendicon);
                    iconchanged = true;
                }else if(etmessage.length()==0&&iconchanged){
                    ivsend.setImageResource(R.drawable.camera);
                    iconchanged = false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        setListeners();





    }
    private String getUID() {//---------------------------------------------------------------------<<getUID>>
        String KEY = "uid";
        return prefs.getString(KEY,null);
    }//----------------------------------------------------------------------------------------------<</getUID>>

    public void setListeners(){
        ivsend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(iconchanged){
                    //send message

                    String message = etmessage.getText().toString();
                    etmessage.setText("");
                    new SendMessage().execute(message);
                    View view = getCurrentFocus();
                    if(view != null) {
                        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }

                }else{
                    //alertdialog for pics
                    popDialog();
                }
            }
        });


    }


    private class GetMessages extends AsyncTask<String, String, String> {

        String webResponse;

        @Override
        protected String doInBackground(String... params) {
            // Create a new HttpClient and Post Header


            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(getString(R.string.ipaddress) + "getmessages.php");


            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);

                nameValuePairs.add(new BasicNameValuePair("uid", uid));
                nameValuePairs.add(new BasicNameValuePair("ouid", ouid));




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

            Log.w(VarHolder.TAG, "getmessages: " + result);

            if (result.equals("error")) {
                Toast.makeText(context, "Could't connect to Find Me", Toast.LENGTH_LONG).show();
            } else {
                try {

                    JSONObject obj = new JSONObject(result);
                    JSONArray jarray = obj.getJSONArray("ppl");

                    Log.e("kevin", result);
                    if(refreshing)
                        mlist.clear();

                    for(int i = 0;i<jarray.length();i++) {
                        JSONObject childJSONObject = jarray.getJSONObject(i);
                        mlist.add(new Messages());
                        mlist.get(mlist.size()-1).senderid = childJSONObject.getString("senderid");
                        mlist.get(mlist.size()-1).propicloc = childJSONObject.getString("picloc");
                        mlist.get(mlist.size()-1).message = childJSONObject.getString("message");
                        mlist.get(mlist.size()-1).time = childJSONObject.getString("time");
                        mlist.get(mlist.size()-1).tag = childJSONObject.getString("tag");
                        mlist.get(mlist.size()-1).messageid = childJSONObject.getString("id");
                        mlist.get(mlist.size()-1).messageimageloc = childJSONObject.getString("messageimageloc");




                    }
                    List<Messages> templist = new ArrayList<Messages>();
                    templist.addAll(mlist);
                    mlist.clear();
                    for(int i = templist.size() - 1;i>=0;i--){
                        mlist.add(templist.get(i));
                    }

                    //choose your favorite adapter
                    lvthreads.setAdapter(new MessagesAdapter(context,mlist,lvthreads,uid));
                    lvthreads.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                        @Override
                        public boolean onItemLongClick(AdapterView<?> parent, final View view, final int position, long id) {

                            new AlertDialog.Builder(context)


                                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    })
                                    .setNeutralButton("Unsend Message", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            new UnsendMessage().execute(mlist.get(position).tag);
                                            Animation anim = AnimationUtils.loadAnimation(
                                                    context, android.R.anim.slide_out_right
                                            );
                                            anim.setDuration(400);
                                            view.startAnimation(anim);

                                            new Handler().postDelayed(new Runnable() {

                                                public void run() {


                                                    mlist.remove(position);
                                                    BaseAdapter a = (BaseAdapter) lvthreads.getAdapter();
                                                    a.notifyDataSetChanged();

                                                }

                                            }, anim.getDuration());


                                        }
                                    })

                                    .setPositiveButton("Delete Message", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            new DeleteMessage().execute(mlist.get(position).messageid);
                                            Animation anim = AnimationUtils.loadAnimation(
                                                    context, android.R.anim.slide_out_right
                                            );
                                            anim.setDuration(400);
                                            view.startAnimation(anim);

                                            new Handler().postDelayed(new Runnable() {

                                                public void run() {


                                                    mlist.remove(position);
                                                    BaseAdapter a = (BaseAdapter) lvthreads.getAdapter();
                                                    a.notifyDataSetChanged();

                                                }

                                            }, anim.getDuration());
                                        }
                                    })

                                    .show();
                            return true;
                        }
                    });



                }catch(JSONException e){
                    e.printStackTrace();

                }

            }
        }

    }

    private class SendMessage extends AsyncTask<String, String, String> {

        String webResponse;

        @Override
        protected String doInBackground(String... params) {
            // Create a new HttpClient and Post Header
            String message = params[0];


            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(getString(R.string.ipaddress) + "sendmessage.php");


            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);

                nameValuePairs.add(new BasicNameValuePair("uid", uid));
                nameValuePairs.add(new BasicNameValuePair("ouid", ouid));
                nameValuePairs.add(new BasicNameValuePair("message", message));


                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));

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

    }


    private class UnsendMessage extends AsyncTask<String,String,String> {

        String webResponse;
        @Override
        protected String doInBackground(String... params) {
            String tag = params[0];

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(getString(R.string.ipaddress) + "unsendmessage.php");


            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);

                nameValuePairs.add(new BasicNameValuePair("uid", uid));
                nameValuePairs.add(new BasicNameValuePair("tag", tag));



                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));

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

            Log.w(VarHolder.TAG, "Unsend message result: " + result);





        }
    }



    private class DeleteMessage extends AsyncTask<String,String,String> {

        String webResponse;
        @Override
        protected String doInBackground(String... params) {
            String messageid = params[0];

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(getString(R.string.ipaddress) + "deletemessage.php");


            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);

                nameValuePairs.add(new BasicNameValuePair("uid", uid));
                nameValuePairs.add(new BasicNameValuePair("messageid", messageid));



                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));

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

            Log.w(VarHolder.TAG, "delete message result: " + result);





        }
    }

    private class DeleteAllMessages extends AsyncTask<String,String,String> {

        String webResponse;
        @Override
        protected String doInBackground(String... params) {


            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(getString(R.string.ipaddress) + "deleteallmessages.php");


            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);

                nameValuePairs.add(new BasicNameValuePair("uid", uid));
                nameValuePairs.add(new BasicNameValuePair("threadid", VarHolder.threadid));
                nameValuePairs.add(new BasicNameValuePair("authkey", VarHolder.authkey));



                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));

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

            Log.w(VarHolder.TAG, "delete message result: " + result);





        }
    }


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


        updateImage();


    }

    private void updateImage(){


        //next we have to update the imageview to show our selected image
        File imgFile = new File(pathToPicture);

        if(imgFile.exists()){

            Bitmap bm = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

            mlist.add(new Messages());
            mlist.get(mlist.size() - 1).message = "";
            mlist.get(mlist.size() - 1).messageid = "";
            mlist.get(mlist.size() - 1).propicloc = "";
            mlist.get(mlist.size() - 1).senderid = uid;
            mlist.get(mlist.size() - 1).time = "Just Now";
            mlist.get(mlist.size() - 1).messageimageloc = pathToPicture;
            mlist.get(mlist.size() - 1).justsent = true;

            BaseAdapter a = (BaseAdapter) lvthreads.getAdapter();
            a.notifyDataSetChanged();

            new ImageUploadTask(this).execute(pathToPicture, ouid, uid, "sendpicturemessage.php");


        }
    }

    public void popDialog(){


        new AlertDialog.Builder(MessagesFrag.this)
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

                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)

                .show();




    }

}
