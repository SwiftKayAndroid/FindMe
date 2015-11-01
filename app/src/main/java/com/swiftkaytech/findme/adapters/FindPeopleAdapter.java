package com.swiftkaytech.findme.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.swiftkaytech.findme.R;
import com.swiftkaytech.findme.data.User;
import com.swiftkaytech.findme.fragment.FindPeopleFrag;
import com.swiftkaytech.findme.utils.ImageLoader;

import java.util.List;

/**
 * Created by BN611 on 3/1/2015.
 */
public class FindPeopleAdapter extends BaseAdapter {


    Context context;
    List<User> plist;
    String uid;
    private static LayoutInflater inflater = null;
    ImageLoader imageLoader;

    public FindPeopleAdapter(Context context,List<User> plist,String uid){
        this.context = context;
        this.plist = plist;
        this.uid = uid;

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

        View row;
        row = inflater.inflate(R.layout.findpeoplegriditem, null);
        ImageView iv = (ImageView) row.findViewById(R.id.ivpicsgrid);
//        if(plist.get(position).picloc.equals("")){
//            iv.setImageResource(R.drawable.ic_placeholder);
//        }else {
//            imageLoader.DisplayImage(plist.get(position).picloc, iv, false);
//        }
//        TextView tv = (TextView) row.findViewById(R.id.tvfindpeople);
//        String dis = String.format("%.1f", Float.valueOf(plist.get(position).distance));
//        tv.setText(dis);


        return row;
    }
}
