package com.swiftkaydevelopment.findme.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.swiftkaydevelopment.findme.R;
import com.swiftkaydevelopment.findme.data.Notification;
import com.swiftkaydevelopment.findme.utils.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kevin Haines on 8/25/15.
 */
public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.NotificationViewHolder> {

    public interface NotifactionsAdapterListener extends PaginationAdapterInterface<Notification> {
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

        holder.title.setText(note.title);
        holder.desc.setText(note.description);

        if (note.user != null && !TextUtils.isEmpty(note.user.getPropicloc())) {
            Picasso.with(mContext)
                    .load(note.user.getPropicloc())
                    .into(holder.iv);
        } else {
            Picasso.with(mContext)
                    .load(note.resId)
                    .into(holder.iv);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onNotificationClicked(note);
                }
            }
        });

        if (position == getItemCount() && (getItemCount() % PaginationAdapterInterface.FULL_PAGE) == 0) {
            if (mListener != null) {
                mListener.onLastItem(note);
            }
        }
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
