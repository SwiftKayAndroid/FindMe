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
import com.swiftkaytech.findme.fragment.FriendsListFrag;
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
 * Created by khaines178 on 9/10/15.
 */
public class FriendsAdapter extends BaseAdapter {

    Context context;
    ImageLoader imageLoader;
    LayoutInflater inflater;
    ListView lv;
    String uid;
    List<FriendsListFrag.Friends> flist;

    public FriendsAdapter(Context context,List<FriendsListFrag.Friends> flist, String uid,ListView lv) {
        this.context = context;
        this.uid = uid;
        this.lv = lv;
        this.flist = flist;
        imageLoader = new ImageLoader(context);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

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
        holder.tvunfriend = (TextView) row.findViewById(R.id.tvacceptfriendrequest);
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

        holder.tvunfriend.setText("Unfriend");
        holder.tvdeny.setVisibility(View.GONE);
        holder.tvunfriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new Unfriend().execute(flist.get(position).uid);
                Animation anim = AnimationUtils.loadAnimation(
                        context, android.R.anim.slide_out_right
                );
                anim.setDuration(500);
                row.startAnimation(anim);

                new Handler().postDelayed(new Runnable() {

                    public void run() {


                        FriendsListFrag.flist.remove(position);
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
        TextView tvunfriend;
        TextView tvdeny;

    }


    public class Unfriend extends AsyncTask<String,String,String> {
        String webResponse;
        @Override
        protected String doInBackground(String... params) {
            // Create a new HttpClient and Post Header

            String ouid = params[0];


            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(context.getString(R.string.ipaddress) + "unfriend.php");

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
