package com.swiftkaytech.findme.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.swiftkaytech.findme.R;
import com.swiftkaytech.findme.fragment.MessagesFrag;
import com.swiftkaytech.findme.utils.ImageLoader;
import com.swiftkaytech.findme.utils.VarHolder;

import java.io.File;
import java.util.List;

/**
 * Created by BN611 on 3/29/2015.
 */
public class MessagesAdapter extends BaseAdapter {
    Context context;
    List<MessagesFrag.Messages> mlist;
    ListView lv;
    String uid;
    private static LayoutInflater inflater = null;
    ImageLoader imageLoader;
    SharedPreferences prefs;
    String profilepicturelocation;

    public MessagesAdapter(Context context,List<MessagesFrag.Messages> mlist,ListView lv, String uid){
        this.context = context;
        this.mlist = mlist;
        this.lv = lv;
        this.uid = uid;
        imageLoader = new ImageLoader(context);
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        profilepicturelocation = prefs.getString("propicloc","null");

    }
    @Override
    public int getCount() {
        return mlist.size();
    }

    @Override
    public Object getItem(int position) {
        return mlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final MessagesFrag.Messages thismess = mlist.get(position);
        final ViewHolder holder = new ViewHolder();
        View row;

        if(thismess.senderid.equals(uid)){
            //usermessage
            thismess.propicloc = profilepicturelocation;

            if(thismess.messageimageloc.equals("null")) {

                row = inflater.inflate(R.layout.messageitemuser, null);

                holder.tvmessage = (TextView) row.findViewById(R.id.tvmessageuser);
                holder.tvtime = (TextView) row.findViewById(R.id.tvmessageusertime);
                holder.ivpropic = (ImageView) row.findViewById(R.id.ivmessageitemuser);



                row.setTag(holder);

                holder.tvmessage.setText(thismess.message);
            }else{


                row = inflater.inflate(R.layout.picturemessageuser, null);
                holder.tvtime = (TextView) row.findViewById(R.id.tvpicturemessageuser);
                holder.sentimage = (ImageView) row.findViewById(R.id.ivpicturemessageuserimage);
                holder.ivpropic = (ImageView) row.findViewById(R.id.ivpicturemessageuser);

                row.setTag(holder);
                if(thismess.justsent){
                    File imgFile = new  File(thismess.messageimageloc);

                    if(imgFile.exists()) {

                        Bitmap bm = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                        holder.sentimage.setImageBitmap(bm);
                    }
                }
                imageLoader.DisplayImage(thismess.messageimageloc,holder.sentimage,true);
            }

        }else{
            //other users message

            if(thismess.messageimageloc.equals("null")) {
                row = inflater.inflate(R.layout.messageitemother, null);

                holder.ivpropic = (ImageView) row.findViewById(R.id.ivmessageother);
                holder.tvmessage = (TextView) row.findViewById(R.id.tvmessageothermessage);
                holder.tvtime = (TextView) row.findViewById(R.id.tvmessageothertime);

                holder.ivpropic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        VarHolder.ouid = thismess.senderid;

                        Intent profile = new Intent("start.fragment.changeview");
                        profile.putExtra("value", VarHolder.PROFILE);
                        context.sendBroadcast(profile);
                    }
                });



                row.setTag(holder);

                holder.tvmessage.setText(thismess.message);
            }else{
                row = inflater.inflate(R.layout.picturemessage, null);

                holder.tvtime = (TextView) row.findViewById(R.id.tvpicturemessagetime);
                holder.sentimage = (ImageView) row.findViewById(R.id.ivpicturemessagesentimage);
                holder.ivpropic = (ImageView) row.findViewById(R.id.ivpicturemessagepropic);

                row.setTag(holder);
                imageLoader.DisplayImage(thismess.messageimageloc,holder.sentimage,true);

            }

        }



        holder.tvtime.setText(/*TimeManager.compareTimeToNow(*/thismess.time);
        if(thismess.propicloc.equals("null")){
            holder.ivpropic.setImageResource(R.drawable.ic_placeholder);
        }else {
            imageLoader.DisplayImage(thismess.propicloc, holder.ivpropic, false);
        }


        return row;
    }

    class ViewHolder{
        ImageView ivpropic;
        TextView tvmessage;
        TextView tvtime;
        ImageView sentimage;


    }
}
