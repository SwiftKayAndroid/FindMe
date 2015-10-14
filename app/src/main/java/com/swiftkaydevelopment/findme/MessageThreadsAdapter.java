package com.swiftkaydevelopment.findme;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by BN611 on 3/9/2015.
 */
public class MessageThreadsAdapter extends BaseAdapter {


    Context context;
    List<MessagesListFrag.MessageThreads> mlist;
    ListView lv;
    private static LayoutInflater inflater = null;
    ImageLoader imageLoader;
    String uid;


    public MessageThreadsAdapter(Context context,List<MessagesListFrag.MessageThreads> mlist,ListView lv,String uid){

        this.context = context;
        this.lv = lv;
        this.mlist = mlist;
        this.uid = uid;
        imageLoader = new ImageLoader(context);
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

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




        final View row = inflater.inflate(R.layout.messagethreadlistitem, null);

        final ViewHolder holder = new ViewHolder();



        holder.ivpropic = (ImageView) row.findViewById(R.id.ivmessagethread);
        holder.tvname = (TextView) row.findViewById(R.id.tvmessagethreadname);
        holder.tvmessage = (TextView) row.findViewById(R.id.tvmessagethreadmessage);
        holder.tvtime = (TextView) row.findViewById(R.id.tvmessagethreadtime);
        holder.checkmark = (ImageView) row.findViewById(R.id.ivcheckmarkmessagethread);


        row.setTag(holder);

        final MessagesListFrag.MessageThreads thismess = mlist.get(position);
        if(thismess != null){
            if(!thismess.propicloc.equals("")){
                imageLoader.DisplayImage(thismess.propicloc,holder.ivpropic,false);
            }
            holder.tvname.setText(thismess.name);
            holder.tvmessage.setText(thismess.message);
            holder.tvtime.setText(/*TimeManager.compareTimeToNow(*/thismess.time);
            if(!thismess.readstat){
//set the typeface of the textviews to bold....

            }
            if(thismess.seenstat){
                //makecheckmark visible
                holder.checkmark.setVisibility(View.VISIBLE);

            }

            holder.ivpropic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    VarHolder.ouid = thismess.uid;
                    Intent profile = new Intent("start.fragment.changeview");
                    profile.putExtra("value", VarHolder.PROFILE);
                    context.sendBroadcast(profile);
                }
            });

        }


        return row;
    }


    class ViewHolder{
        ImageView ivpropic;
        TextView tvname;
        TextView tvmessage;
        TextView tvtime;
        ImageView checkmark;


    }
}
