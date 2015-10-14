package com.swiftkaydevelopment.findme.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by khaines178 on 8/25/15.
 */
public class NotificationsAdapter extends BaseAdapter {
    Context context;
    List<NotificationsFrag.Notification> nlist;
    LayoutInflater inflater;
    ImageLoader imageLoader;

    public NotificationsAdapter(Context context,List<NotificationsFrag.Notification> nlist){

        this.context = context;
        this.nlist = nlist;
        inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        imageLoader = new ImageLoader(context);


    }

    @Override
    public int getCount() {
        return nlist.size();
    }

    @Override
    public Object getItem(int position) {
        return nlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = inflater.inflate(R.layout.notificationitem,null);

        final ViewHolder holder = new ViewHolder();

        holder.iv = (ImageView) row.findViewById(R.id.ivnotificationimage);
        holder.title = (TextView) row.findViewById(R.id.tvnotificationtitle);
        holder.desc = (TextView) row.findViewById(R.id.tvnotificationdesc);
        holder.time = (TextView) row.findViewById(R.id.tvnotificationtime);

        row.setTag(holder);

        final NotificationsFrag.Notification note = nlist.get(position);

        if(note.picloc.equals("null")){

        }else{
            imageLoader.DisplayImage(note.picloc,holder.iv,false);
        }

        holder.title.setText("New notification");
        holder.desc.setText(note.note);

        return row;
    }



    class ViewHolder{
        ImageView iv;
        TextView title;
        TextView desc;
        TextView time;
    }
}
