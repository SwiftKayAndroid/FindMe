package com.swiftkaydevelopment.findme.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.List;

/**
 * Created by khaines178 on 8/24/15.
 */
public class SGVAdapter extends BaseAdapter {


    Context context;
    List<ViewPhotosFrag.Pics> plist;

    private static LayoutInflater inflater = null;
    ImageLoader imageLoader;

    public SGVAdapter(Context context,List<ViewPhotosFrag.Pics> plist){
        this.context = context;
        this.plist = plist;
        imageLoader = new ImageLoader(context);
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);


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

        View row = inflater.inflate(R.layout.sgvitem, null);
       ImageView iv = (ImageView) row.findViewById(R.id.ivsgvrow);
        imageLoader.DisplayImage(plist.get(position).postpicloc,iv,true);




        return row;
    }
}
