package com.swiftkaydevelopment.findme.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.swiftkaydevelopment.findme.R;
import com.swiftkaydevelopment.findme.data.User;
import com.swiftkaydevelopment.findme.utils.ImageLoader;
import com.swiftkaydevelopment.findme.views.CircleTransform;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kevin Haines on 3/1/2015.
 */
public class FindPeopleAdapter extends BaseAdapter {

    Context context;
    List<User> users;
    String uid;
    private static LayoutInflater inflater = null;

    public FindPeopleAdapter(Context context, List<User> users, String uid){
        this.context = context;
        this.users = users;
        this.uid = uid;

        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public Object getItem(int position) {
        return users.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void addUsers(ArrayList<User> users) {
        this.users.addAll(users);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = inflater.inflate(R.layout.findpeoplegriditem, parent, false);
        ViewHolder holder = new ViewHolder();

        holder.propic = (ImageView) row.findViewById(R.id.ivFindPeopleGridItemProfilePicture);
        holder.tvDesc = (TextView) row.findViewById(R.id.tvfindpeople);
        holder.tvDesc.setText(users.get(position).getGender().toString().substring(0, 1) + "/" +
        users.get(position).getAge() + "/" + users.get(position).getOrientation().toString());

        if (users.get(position).getPropicloc().equals("")) {
            holder.propic.setImageResource(R.drawable.ic_placeholder);
        } else {
            Picasso.with(parent.getContext())
                    .load(users.get(position).getPropicloc())
                    .transform(new CircleTransform())
                    .into(holder.propic);
        }

        row.setTag(holder);

        return row;
    }

    private class ViewHolder {
        ImageView propic;
        TextView tvDesc;
    }
}
