package com.swiftkaydevelopment.findme.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.swiftkaydevelopment.findme.R;
import com.swiftkaydevelopment.findme.data.Notification;

import java.util.ArrayList;

/**
 * Created by khaines178 on 8/24/15.
 */
public class NotificationsFrag extends BaseFragment {
    public static final String TAG = "NotificationsFrag";
    private static final String ARG_NOTES = "ARG_NOTES";

    private ArrayList<Notification> mNotifications;

    public static NotificationsFrag newInstance(String uid) {
        NotificationsFrag frag = new NotificationsFrag();
        Bundle b = new Bundle();
        b.putString(ARG_UID, uid);
        frag.setArguments(b);
        return  frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mNotifications = (ArrayList) savedInstanceState.getSerializable(ARG_NOTES);
            uid = savedInstanceState.getString(ARG_UID);
        } else {
            if (getArguments() != null) {
                uid = getArguments().getString(ARG_UID);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.notificationfrag, container,false);

        return layout;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (savedInstanceState != null) {
            uid = savedInstanceState.getString(ARG_UID);
            mNotifications = (ArrayList) savedInstanceState.getSerializable(ARG_NOTES);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(ARG_UID, uid);
        outState.putSerializable(ARG_NOTES, mNotifications);
        super.onSaveInstanceState(outState);
    }
}
