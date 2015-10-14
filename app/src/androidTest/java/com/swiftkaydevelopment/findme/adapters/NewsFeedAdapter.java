package com.swiftkaydevelopment.findme.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.AsyncTask;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
 * Created by BN611 on 2/10/2015.
 */
public class NewsFeedAdapter extends BaseAdapter {

    Context context;
    List<NewsFeedFrag.Posts> posts;
    String uid;
    ListView lv;
    private static LayoutInflater inflater = null;
    ImageLoader imageLoader;

    public NewsFeedAdapter(Context context,List<NewsFeedFrag.Posts> posts,String uid,ListView lv){
        super();
        this.context = context;
        this.posts = posts;
        this.uid = uid;
        this.lv = lv;
        imageLoader = new ImageLoader(context);
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);



    }



    @Override
    public int getCount() {
        return posts.size();
    }

    @Override
    public Object getItem(int position) {
        return posts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;
        final ViewHolder holder = new ViewHolder();
        row = null;

        if (row == null){

            row = inflater.inflate(R.layout.newsfeedrow, null);

            holder.name = (TextView) row.findViewById(R.id.newsfeedusername);
            holder.time = (TextView) row.findViewById(R.id.newsfeedposttime);
            holder.locationtwo = (TextView) row.findViewById(R.id.tvnewsfeedlocationone);
            holder.propic = (ImageView) row.findViewById(R.id.ivnewsfeedprofilepic);
            holder.postiv = (ImageView) row.findViewById(R.id.ivnewsfeedpostedimage);
            holder.status = (TextView) row.findViewById(R.id.tvnewsfeedposttext);
            holder.likey = (TextView) row.findViewById(R.id.tvnewsfeedrownumlikes);
            holder.likepost = (ImageView) row.findViewById(R.id.ivnewsfeedrowlike);
            holder.commy = (TextView) row.findViewById(R.id.tvnewsfeedrownumcomments);

            holder.rlcommentholder = (RelativeLayout) row.findViewById(R.id.rllastcomment);
            holder.commentiv = (ImageView) row.findViewById(R.id.ivnewsfeedlastcommentpropic);
            holder.comment = (TextView) row.findViewById(R.id.tvnewsfeedlastcomment);
            holder.commentname = (TextView) row.findViewById(R.id.tvlastcommentname);

            row.setTag(holder);
        }

        final NewsFeedFrag.Posts thispost = posts.get(position);

        if(thispost.numcomments.equals("0")){
            holder.rlcommentholder.setVisibility(View.GONE);
        }else{

            holder.comment.setText(thispost.lastcomment);
            holder.commentname.setText(thispost.lastcommentname);
            if(thispost.lastcommentpropicloc.equals("null")){
                holder.commentiv.setImageResource(R.drawable.ic_placeholder);
            }else{
                imageLoader.DisplayImage(thispost.lastcommentpropicloc,holder.commentiv,false);
            }
        }

        if(thispost != null){

            if(thispost.postimagelocation.equals("null")){

                holder.postiv.setVisibility(View.GONE);
            }else{
                imageLoader.DisplayImage(thispost.postimagelocation, holder.postiv,true);

                holder.postiv.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        viewImage(thispost.postimagelocation);

                    }


                });
            }

            holder.name.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    VarHolder.ouid = thispost.postinguserid;
                    android.util.Log.d("kevin",thispost.postinguserid);

                }
            });

            holder.name.setText(thispost.firstname + " " + thispost.lastname);

            String timesince = /*TimeManager.compareTimeToNow(*/thispost.posttime;
            holder.time.setText(timesince);


            String dis = /*String.format("%.1f", Float.valueOf(*/thispost.postinguserslocation;
            holder.locationtwo.setText(dis + " miles away");

            holder.status.setText(thispost.post);

            if(thispost.propiclocation.equals("null")){
                holder.propic.setImageResource(R.drawable.ic_placeholder);
            }else{
                imageLoader.DisplayImage(thispost.propiclocation, holder.propic,false);


            }





            //===============================  SET UP NUMBER OF LIKES TEXTVIEW======================
            if(thispost.numlikes.equals("0")) {
                holder.likey.setText("No likes");
            }else{
                    int thisnumlikes = Integer.parseInt(thispost.numlikes);
                    if(thisnumlikes>1){
                        holder.likey.setText(thispost.numlikes + " people liked this!");
                    }else{
                        holder.likey.setText(thispost.numlikes + " person liked this!");
                    }

                }

                holder.likey.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        VarHolder.postid = thispost.postid;
                        Intent status = new Intent("start.fragment.changeview");
                        status.putExtra("value", VarHolder.STATUSLIKES);
                        context.sendBroadcast(status);

                    }
                });

            }


        //-----------NUMBER OF COMMENTS TEXTVIEW---------------->>>>>

        if(thispost.numcomments.equals("0")){
            holder.commy.setText("No comments");
        }else if(thispost.numcomments.equals("1")){
            holder.commy.setText("1 comment");
        }else{
            holder.commy.setText(thispost.numcomments + " comments");
        }

        holder.commy.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                VarHolder.status.name = thispost.firstname + " " + thispost.lastname;
                VarHolder.status.liked = thispost.liked;
                VarHolder.status.likeduserids = thispost.likeduserids;
                VarHolder.status.numcomments = thispost.numcomments;
                VarHolder.status.numlikes = thispost.numlikes;
                VarHolder.status.post = thispost.post;
                VarHolder.status.postid = thispost.postid;
                VarHolder.status.postimagelocation = thispost.postimagelocation;
                VarHolder.status.postinguserid = thispost.postinguserid;
                VarHolder.status.postinguserslocation = thispost.postinguserslocation;
                VarHolder.status.posttime = thispost.posttime;
                VarHolder.status.propiclocation = thispost.propiclocation;

                Intent status = new Intent("start.fragment.changeview");
                status.putExtra("value", VarHolder.COMMENTS);
                context.sendBroadcast(status);

            }
        });


        //-----------LIKE POST BUTTON IMAGEVIEW--------------->>>>>>
        if(thispost.liked){
            holder.likepost.setImageResource(R.drawable.like);
        }else {
            holder.likepost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    thispost.liked = true;
                    holder.likepost.setImageResource(R.drawable.like);
                    PostLike pl = new PostLike();
                    pl.execute(uid, thispost.postid);

                    int templikes = Integer.parseInt(thispost.numlikes);
                    templikes++;
                    if (templikes > 1) {
                        holder.likey.setText(Integer.toString(templikes) + "likes");
                    } else {
                        holder.likey.setText(Integer.toString(templikes) + "like");
                    }
                }
            });
        }




            holder.propic.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    VarHolder.ouid = thispost.postinguserid;


                    Intent profile = new Intent("start.fragment.changeview");
                    profile.putExtra("value", VarHolder.PROFILE);
                    context.sendBroadcast(profile);



                }
            });











        return row;
    }

    public class ViewHolder
    {
        ImageView propic;
        TextView name;
        TextView time;
        TextView status;
        TextView locationtwo;
        TextView likey;
        TextView commy;
        ImageView postiv;
        TextView likedinfo;
        ImageView likepost;
        RelativeLayout rlcommentholder;
        ImageView commentiv;
        TextView comment;
        TextView commentname;
    }

    //======================================================================= POST LIKE ----->

    class PostLike extends AsyncTask<String,String,String> {

        String webResponse;
        @Override
        protected String doInBackground(String... params) {
            String uid = params[0];
            String postid = params[1];

            // Create a new HttpClient and Post Header

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(context.getString(R.string.ipaddress) + "likepost.php");

            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                nameValuePairs.add(new BasicNameValuePair("uid", uid));
                nameValuePairs.add(new BasicNameValuePair("postid", postid));

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
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            android.util.Log.d("kevin", "postlike result: " + result);
        }
    }



    public void viewImage(final String picloc) {//==============================================================================

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();


        int width = display.getWidth();
        int height = display.getHeight();

         PopupWindow pop = new PopupWindow(context);

        // Inflate the popup_layout.xml

        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        pop.setAnimationStyle(R.style.pop_animation);
        ViewGroup layout = new RelativeLayout(context);
        ImageView iv = new ImageView(context);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layout.addView(iv,params);
        layout.setBackgroundColor(Color.BLACK);
        pop.setContentView(layout);

        pop.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        pop.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);

        imageLoader.DisplayImage(picloc,iv,true);


        pop.setFocusable(true);

        pop.showAtLocation(layout, Gravity.CENTER, 0, 0);

    }//==========================================================================================================================
}
