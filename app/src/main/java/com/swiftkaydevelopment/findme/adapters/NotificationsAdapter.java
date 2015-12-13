package com.swiftkaydevelopment.findme.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.swiftkaydevelopment.findme.R;
import com.swiftkaydevelopment.findme.data.Notification;
import com.swiftkaydevelopment.findme.utils.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kevin Haines on 8/25/15.
 */
public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.NotificationViewHolder> {

    public interface NotifactionsAdapterListener {
        void onNotificationClicked(Notification note);
    }
    Context mContext;
    List<Notification> nlist;
    ImageLoader imageLoader;

    private NotifactionsAdapterListener mListener;

    public NotificationsAdapter(Context context, ArrayList<Notification> nlist){

        this.mContext = context;
        this.nlist = nlist;
        imageLoader = new ImageLoader(context);

    }

    public void setListener(NotifactionsAdapterListener listener) {
        mListener = listener;
    }

    @Override
    public NotificationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.notificationitem, parent, false);
        return new NotificationViewHolder(layout);
    }

    @Override
    public void onBindViewHolder(NotificationViewHolder holder, int position) {
        final Notification note = nlist.get(position);
        holder.iv.setImageResource(note.resId);
        holder.title.setText(note.title);
        holder.desc.setText(note.description);
        holder.itemView.setTag(note);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onNotificationClicked((Notification) v.getTag());
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return nlist.size();
    }

    public void addNotifications(ArrayList<Notification> notes) {
        nlist.addAll(notes);
        notifyDataSetChanged();
    }

    protected class NotificationViewHolder extends RecyclerView.ViewHolder{
        ImageView iv;
        TextView title;
        TextView desc;
        TextView time;

        public NotificationViewHolder(View itemView) {
            super(itemView);

            iv = (ImageView) itemView.findViewById(R.id.ivnotificationimage);
            title = (TextView) itemView.findViewById(R.id.tvnotificationtitle);
            desc = (TextView) itemView.findViewById(R.id.tvnotificationdesc);
            time = (TextView) itemView.findViewById(R.id.tvnotificationtime);
        }
    }
}
