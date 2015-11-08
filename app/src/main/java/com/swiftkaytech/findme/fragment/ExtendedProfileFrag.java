package com.swiftkaytech.findme.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by BN611 on 2/23/2015.
 */
public class ExtendedProfileFrag extends Fragment {

    Context context;
    SharedPreferences prefs;
    TextView tvage,tvzodiac,tvcity,tvgender,tvrelationshipstatus,tvlookingfor,
            tvinterestedin,tvidealfristdate,tvhaskids,tvwantskids,tvprofession,
            tvschool,tvhascar,tvhasownplace,tvcigs,tvweed,tvdrinks,tvheight,tvbodytype,tvperfectmatch,tvtitle;

    String ouid;
    String uid;

    public ExtendedProfileFrag(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.context = getActivity();
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        uid = getUID();
        View layout = inflater.inflate(R.layout.extendedprofile, container, false);

        tvage = (TextView) layout.findViewById(R.id.tvextendedage);
        tvzodiac = (TextView) layout.findViewById(R.id.tvextendedzodiac);
        tvcity = (TextView) layout.findViewById(R.id.extendedcity);
        tvgender = (TextView) layout.findViewById(R.id.extendedgender);
        tvrelationshipstatus = (TextView) layout.findViewById(R.id.extendedrelationshipstatus);
        tvlookingfor = (TextView) layout.findViewById(R.id.extendedlookingfor);
        tvinterestedin = (TextView) layout.findViewById(R.id.extendedinterestedin);
        tvidealfristdate = (TextView) layout.findViewById(R.id.extendedidealfirstdate);
        tvhaskids = (TextView) layout.findViewById(R.id.extendedhaskids);
        tvwantskids = (TextView) layout.findViewById(R.id.extendedwantskids);
        tvprofession = (TextView) layout.findViewById(R.id.extendedprofession);
        tvschool = (TextView) layout.findViewById(R.id.extendedschool);
        tvhascar = (TextView) layout.findViewById(R.id.extendedhascar);
        tvhasownplace = (TextView) layout.findViewById(R.id.extendedhasownplace);
        tvcigs = (TextView) layout.findViewById(R.id.extendedsmokescigs);
        tvweed = (TextView) layout.findViewById(R.id.extendedweed);
        tvdrinks = (TextView) layout.findViewById(R.id.extendeddrinks);
        tvheight = (TextView) layout.findViewById(R.id.extendedheight);
        tvbodytype = (TextView) layout.findViewById(R.id.extendedbodytype);
        tvperfectmatch = (TextView) layout.findViewById(R.id.extendedperfectmatch);
        tvtitle = (TextView) layout.findViewById(R.id.tvextendedtitle);


        setListeners();



        return layout;


    }
    private String getUID() {//---------------------------------------------------------------------<<getUID>>
        String KEY = "uid";
        return prefs.getString(KEY,null);
    }//----------------------------------------------------------------------------------------------<</getUID>>
    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        new GetExtendedInfo().execute();



    }

    private void setListeners(){
tvtitle.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        getFragmentManager().popBackStack();
    }
});





    }

    private class GetExtendedInfo extends AsyncTask<String, String, String> {

        String webResponse;

        @Override
        protected String doInBackground(String... params) {
            // Create a new HttpClient and Post Header


            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(getString(R.string.ipaddress) + "getextendedinfo.php");

            //This is the data to send


            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                nameValuePairs.add(new BasicNameValuePair("ouid", ouid));
                nameValuePairs.add(new BasicNameValuePair("uid", uid));


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

            Log.e("kevin", "extended profile result: " + result);

            try {

                JSONObject obj = new JSONObject(result);
               // JSONArray jarray = obj.getJSONArray("post");
                JSONObject child = obj.getJSONObject("post");
                tvage.setText(/*TimeManager.getAge(*/child.getString("dob"));
                tvzodiac.setText(child.getString("zodiac"));
                tvcity.setText(child.getString("city"));
                tvgender.setText(child.getString("gender"));
                tvrelationshipstatus.setText(child.getString("relationshipstatus"));
                tvlookingfor.setText(child.getString("lookingfor"));
                tvinterestedin.setText(child.getString("interestedin"));
                tvidealfristdate.setText(child.getString("idealfirstdate"));
                tvhaskids.setText(child.getString("haskids"));
                tvwantskids.setText(child.getString("wantskids"));
                tvprofession.setText(child.getString("profession"));
                tvschool.setText(child.getString("school"));
                tvhascar.setText(child.getString("hascar"));
                tvhasownplace.setText(child.getString("hasownplace"));
                tvcigs.setText(child.getString("cigs"));
                tvweed.setText(child.getString("weed"));
                tvdrinks.setText(child.getString("drinks"));
                tvheight.setText(child.getString("height"));
                tvbodytype.setText(child.getString("bodytype"));
                tvperfectmatch.setText(child.getString("perfectmatch"));



            }catch(JSONException e){
                e.printStackTrace();
            }



        }

    }
}
