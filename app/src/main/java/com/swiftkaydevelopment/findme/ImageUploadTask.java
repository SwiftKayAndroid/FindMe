package com.swiftkaydevelopment.findme;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

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

import java.io.File;

/**
 * Created by khaines178 on 9/7/15.
 */
class ImageUploadTask extends AsyncTask<String, String, String> {

    String captiontext;
    String filepath;
    Context context;


    public ImageUploadTask(Context context){

        this.context = context;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    String responseStr;
    @Override
    protected String doInBackground(String... params) {
        filepath = params[0];
        captiontext = params[1];
        String uid = params[2];
        String urlpath = params[3];



        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpContext localContext = new BasicHttpContext();
            HttpPost httpPost = new HttpPost(
                    context.getString(R.string.ipaddress)
                            + urlpath);

            MultipartEntity entity = new MultipartEntity(
                    HttpMultipartMode.BROWSER_COMPATIBLE);




            File file = new File(filepath);
            entity.addPart("uploaded_file",new FileBody(file));
            entity.addPart("text", new StringBody(captiontext));
            entity.addPart("uid", new StringBody(uid));
            entity.addPart("ouid", new StringBody(captiontext));

            httpPost.setEntity(entity);


            // execute HTTP post request
            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity resEntity = response.getEntity();

            if (resEntity != null) {

                responseStr = EntityUtils.toString(resEntity).trim();
                Log.e("kevin", responseStr);


                // you can add an if statement here and do other actions based on the response
            }else{
                responseStr = "error";
            };

            return responseStr;
        } catch (Exception e) {

            Toast.makeText(context,
                    "There was an error uploading your image.",
                    Toast.LENGTH_LONG).show();
            Log.e(e.getClass().getName(), e.getMessage(), e);
            return null;
        }

        // (null);
    }


    @Override
    protected void onPostExecute(String sResponse) {

    }
}
