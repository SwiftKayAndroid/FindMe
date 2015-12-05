package com.swiftkaydevelopment.findme.fragment;

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

import com.swiftkaydevelopment.findme.R;

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




    }

    private void setListeners(){
tvtitle.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        getFragmentManager().popBackStack();
    }
});





    }

}
