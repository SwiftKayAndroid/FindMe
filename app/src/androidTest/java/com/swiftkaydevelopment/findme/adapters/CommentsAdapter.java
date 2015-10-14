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
 * Created by Kevin Haines on 9/8/15.
 */
public class CommentsAdapter extends BaseAdapter {

    //objects
    Context context;
    List<Commentsfrag.Comments> plist;
    ImageLoader imageLoader;
    LayoutInflater inflater;

    public CommentsAdapter(Context context,List<Commentsfrag.Comments> plist) {
        this.context = context;
        this.plist = plist;
        imageLoader = new ImageLoader(context);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);


    }

    @Override
    public int getCount() {
        return plist.size();
    }

    @Override
    public Object getItem(int position) {
        return plist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = inflater.inflate(R.layout.commentrow,parent, false);
        final ViewHolder holder = new ViewHolder();

        //initialize viewgroup elements
        holder.propic = (ImageView) row.findViewById(R.id.ivcommentpropic);
        holder.comment = (TextView) row.findViewById(R.id.tvcomment);
        holder.name = (TextView) row.findViewById(R.id.tvcommentusername);
        holder.time = (TextView) row.findViewById(R.id.tvcommenttime);

        row.setTag(holder); //set unique tag for view row

        Commentsfrag.Comments thiscomment = plist.get(position);

        if(thiscomment != null) {
            if (thiscomment.propicloc.equals("null")) {
                holder.propic.setImageResource(R.drawable.ic_placeholder);

            } else {
                imageLoader.DisplayImage(thiscomment.propicloc, holder.propic, false);

            }

            holder.comment.setText(thiscomment.comment);
            holder.name.setText(thiscomment.name);
            holder.time.setText(thiscomment.time);

            return row;
        }
        return null;
    }

    //class to hold the view objects in this viewgroup
    class ViewHolder{

        ImageView propic;
        TextView name;
        TextView comment;
        TextView time;
    }
}
