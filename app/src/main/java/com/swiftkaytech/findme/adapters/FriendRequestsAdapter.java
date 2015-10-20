package com.swiftkaytech.findme.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.swiftkaytech.findme.R;
import com.swiftkaytech.findme.fragment.FriendRequestsFrag;
import com.swiftkaytech.findme.utils.ImageLoader;
import com.swiftkaytech.findme.utils.VarHolder;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by BN611 on 3/2/2015.
 */
public class FriendRequestsAdapter extends BaseAdapter {
    Context context;
    List<FriendRequestsFrag.FriendRequests> flist;

    ListView lv;
    private static LayoutInflater inflater = null;
    ImageLoader imageLoader;
    String uid;


    public FriendRequestsAdapter(Context context, List<FriendRequestsFrag.FriendRequests> flist,ListView lv,String uid){
        this.context = context;
        this.flist = flist;
        this.lv = lv;
        this.uid = uid;
        imageLoader = new ImageLoader(context);
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);




    }
    @Override
    public int getCount() {
        return flist.size();
    }

    @Override
    public Object getItem(int position) {
        return flist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {


        final View row = inflater.inflate(R.layout.friendslistitem, null);

        final ViewHolder holder = new ViewHolder();



            holder.iv = (ImageView) row.findViewById(R.id.ivfriendslist);
            holder.tvname = (TextView) row.findViewById(R.id.tvfriendslistitemname);
            holder.tvdesc = (TextView) row.findViewById(R.id.tvfriendslistitemdesc);
            holder.tvaccept = (TextView) row.findViewById(R.id.tvacceptfriendrequest);
            holder.tvdeny = (TextView) row.findViewById(R.id.tvdenyfriendrequest);
            holder.tvname.setText(flist.get(position).name);
            holder.tvdesc.setText(/*TimeManager.getAge(*/flist.get(position).dob + " - " + flist.get(position).location);

            row.setTag(holder);



        holder.iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VarHolder.ouid = flist.get(position).uid;
                Intent profile = new Intent("start.fragment.changeview");
                profile.putExtra("value", VarHolder.PROFILE);
                context.sendBroadcast(profile);
            }
        });




                holder.tvaccept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        new SendFriendRequest().execute(flist.get(position).uid);
                        holder.tvaccept.setVisibility(View.GONE);
                        holder.tvdeny.setVisibility(View.GONE);
                    }
                });

                holder.tvdeny.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new DenyFriendRequest().execute(flist.get(position).uid);
                        Animation anim = AnimationUtils.loadAnimation(
                                context, android.R.anim.slide_out_right
                        );
                        anim.setDuration(500);
                        row.startAnimation(anim);

                        new Handler().postDelayed(new Runnable() {

                            public void run() {


                                FriendRequestsFrag.flist.remove(position);
                                BaseAdapter a = (BaseAdapter) lv.getAdapter();
                                a.notifyDataSetChanged();

                            }

                        }, anim.getDuration());
                    }
                });



        return row;
    }

    class ViewHolder{
        ImageView iv;
        TextView tvname;
        TextView tvdesc;
        TextView tvaccept;
        TextView tvdeny;

    }

    public class SendFriendRequest extends AsyncTask<String,String,String> {
        String webResponse;
        @Override
        protected String doInBackground(String... params) {
            // Create a new HttpClient and Post Header

            String ouid = params[0];


            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(context.getString(R.string.ipaddress) + "sendfriendrequest.php");

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

            } catch (IOException e) {
                e.printStackTrace();

            }



            return webResponse;
        }





    }
    public class DenyFriendRequest extends AsyncTask<String,String,String> {
        String webResponse;
        @Override
        protected String doInBackground(String... params) {
            // Create a new HttpClient and Post Header

            String ouid = params[0];


            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(context.getString(R.string.ipaddress) + "denyfriendrequest.php");

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

            } catch (IOException e) {
                e.printStackTrace();

            }



            return webResponse;
        }





    }



}
