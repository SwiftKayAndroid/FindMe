package com.swiftkaytech.findme.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import com.swiftkaytech.findme.R;
import com.swiftkaytech.findme.adapters.FindPeopleAdapter;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kevin Haines on 2/25/2015.
 */
public class FindPeopleFrag extends BaseFragment {

    public static final String UID_ARGS = "UID_ARGS";

    public class Peeps{

        public String uid;
        public String picloc;
        public String distance;
    }
    List<Peeps> plist;

    GridView gv;

    String lastpost = "0";
    boolean refreshing = false;

    public static FindPeopleFrag newInstance(String id){
        FindPeopleFrag frag = new FindPeopleFrag();
        Bundle b = new Bundle();
        b.putString(UID_ARGS,id);
        frag.setArguments(b);
        return frag;
    }

    public FindPeopleFrag(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            uid = savedInstanceState.getString(UID_ARGS);
        } else {
            if(getArguments() != null){
                uid = getArguments().getString(UID_ARGS);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(UID_ARGS,uid);
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.findpeople, null);


        gv = (GridView) layout.findViewById(R.id.gvviewfindpeoplefrag);

        plist = new ArrayList<Peeps>();
        new GetPeople().execute();

        return layout;
    }

    private class GetPeople extends AsyncTask<String, String, String> {

        String webResponse;

        @Override
        protected String doInBackground(String... params) {
            // Create a new HttpClient and Post Header

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(getString(R.string.ipaddress) + "findpeople.php");

            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                nameValuePairs.add(new BasicNameValuePair("lastpost", lastpost));


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

            if (result.equals("error")) {
                toast("couldn't connect to Find Me");
            } else {
                try {

                    JSONObject obj = new JSONObject(result);
                    JSONArray jarray = obj.getJSONArray("ppl");

                    log(result);
                    if(refreshing)
                        plist.clear();

                    for(int i = 0;i<jarray.length();i++) {
                        JSONObject childJSONObject = jarray.getJSONObject(i);
                        plist.add(new Peeps());
                        plist.get(plist.size()-1).uid = childJSONObject.getString("uid");
                        plist.get(plist.size()-1).picloc = childJSONObject.getString("propicloc");
                        plist.get(plist.size()-1).distance = childJSONObject.getString("distance");

                    }

                    //choose your favorite adapter
                    if(gv.getAdapter() == null) {
                        gv.setAdapter(new FindPeopleAdapter(getActivity(), plist,uid));
                    }else{
                        BaseAdapter a = (BaseAdapter) gv.getAdapter();
                        a.notifyDataSetChanged();
                    }
                    lastpost = plist.get(plist.size() - 1).uid;
                }catch(JSONException e){
                    e.printStackTrace();
                }
            }
        }
    }
}
