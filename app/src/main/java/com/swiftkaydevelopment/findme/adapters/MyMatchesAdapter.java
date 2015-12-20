package com.swiftkaydevelopment.findme.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.swiftkaydevelopment.findme.activity.MyMatches;
import com.swiftkaydevelopment.findme.utils.ImageLoader;
import com.swiftkaydevelopment.findme.R;

import java.util.List;

/**
 * Created by khaines178 on 9/11/15.
 */
public class MyMatchesAdapter extends BaseAdapter {
    Context context;
    List<MyMatches.Matches> plist;
    ListView lv;
    LayoutInflater inflater;
    ImageLoader imageLoader;
    String uid;


    public MyMatchesAdapter(Context context,List<MyMatches.Matches> plist,String uid,ListView lv) {
        this.context = context;
        this.plist = plist;
        this.uid = uid;
        this.lv = lv;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader = new ImageLoader(context);
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

        View row = inflater.inflate(R.layout.matchlistitem,parent,false);
        final ViewHolder holder = new ViewHolder();
        holder.tvage = (TextView) row.findViewById(R.id.tvmymatchesage);
        holder.tvname = (TextView) row.findViewById(R.id.tvmymatchesname);

        row.setTag(holder);

        MyMatches.Matches thismatch = plist.get(position);

        if(thismatch != null){
            if(thismatch.propicloc.equals("null")){
            }else{
            }
            holder.tvname.setText(thismatch.name);
            holder.tvage.setText(thismatch.dob + "/" + thismatch.city);


        }



        return row;
    }

    class ViewHolder{
        TextView tvname;
        TextView tvage;

    }
}
