package com.swiftkaytech.findme.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by khaines178 on 8/24/15.
 */
public class CardAdapter extends BaseAdapter {

    Context context;
    List<Match.Users> ulist;    //list of potential user matches
    ImageLoader imageLoader;    //class used to load images Asynchronously
    LayoutInflater inflater;

    public CardAdapter(Context context,List<Match.Users> ulist){

        this.context = context;
        this.ulist = ulist;
        imageLoader = new ImageLoader(context);
        inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        return ulist.size();
    }

    @Override
    public Object getItem(int position) {
        return ulist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View row = inflater.inflate(R.layout.item,parent,false);
        final ViewHolder holder = new ViewHolder();

        holder.name = (TextView) row.findViewById(R.id.swipeitemname);              //holds the users name
        holder.iv = (ImageView) row.findViewById(R.id.swipeitemimage);              //holds the users profile picture
        holder.desc = (TextView) row.findViewById(R.id.swipeitemaboutme);           //holds the users aboutme
        holder.ibmessage = (ImageButton) row.findViewById(R.id.swipeitemmessage);   //image button to message user
        holder.ibphotos = (ImageButton) row.findViewById(R.id.swipeitemcambtn);     //image button to view users pictures

        row.setTag(holder);     //set the unique tag for this view

        final Match.Users user = ulist.get(position);

        holder.name.setText(user.name);
        holder.desc.setText(user.aboutme);

        if(user.propicloc.equals("null")){

            holder.iv.setImageResource(R.drawable.ic_placeholder);

        }else{
            imageLoader.DisplayImage(user.propicloc,holder.iv,false);
        }

        //go to the current users messages
        holder.ibmessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                VarHolder.ouid = ulist.get(position).uid;
                Intent i = new Intent("com.swiftkaytech.findme.MESSAGESINLINE");
                context.startActivity(i);
            }
        });


        //go to the current users photos
        holder.ibphotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VarHolder.ouid = ulist.get(position).uid;
               // Intent i = new Intent();
                //context.startActivity(i);
            }
        });




        return row;
    }

    //class that holds the views in order to set a unique tag
    class ViewHolder{
        TextView name;
        ImageView iv;
        TextView desc;
        ImageButton ibphotos;
        ImageButton ibmessage;


    }
}
