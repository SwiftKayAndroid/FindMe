package com.swiftkaydevelopment.findme.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.swiftkaydevelopment.findme.R;
import com.swiftkaydevelopment.findme.activity.ProfileActivity;
import com.swiftkaydevelopment.findme.adapters.FindPeopleAdapter;
import com.swiftkaydevelopment.findme.data.User;
import com.swiftkaydevelopment.findme.managers.UserManager;

import java.util.ArrayList;

/**
 * Created by Kevin Haines on 2/25/2015.
 */
public class FindPeopleFrag extends BaseFragment implements UserManager.UserManagerListener{
    public static final String TAG = "FindPeopleFrag";
    private static final String ARG_USERS = "ARG_USERS";

    public static final String UID_ARGS = "UID_ARGS";

    private ArrayList<User> users = new ArrayList<>();
    private FindPeopleAdapter mAdapter;

    GridView mGridView;

    public static FindPeopleFrag newInstance(String id){
        FindPeopleFrag frag = new FindPeopleFrag();
        Bundle b = new Bundle();
        b.putString(UID_ARGS,id);
        frag.setArguments(b);
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            uid = savedInstanceState.getString(UID_ARGS);
            users = (ArrayList) savedInstanceState.getSerializable(ARG_USERS);
        } else {
            if(getArguments() != null){
                uid = getArguments().getString(UID_ARGS);
            }
            UserManager.getInstance(uid, getActivity()).findPeople(uid, "0");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.findpeople, container, false);
        mGridView = (GridView) layout.findViewById(R.id.gvviewfindpeoplefrag);

        return layout;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (savedInstanceState != null) {
            users = (ArrayList) savedInstanceState.getSerializable(ARG_USERS);
        }
        if (mAdapter == null) {
            mAdapter = new FindPeopleAdapter(getActivity(), users, uid);
        }
        mGridView.setAdapter(mAdapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User u = (User) mAdapter.getItem(position);
                getActivity().startActivity(ProfileActivity.createIntent(getActivity(), u));
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(UID_ARGS, uid);
        outState.putSerializable(ARG_USERS, users);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        UserManager.getInstance(uid, getActivity()).addListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        UserManager.getInstance(uid, getActivity()).removeListener(this);
    }

    @Override
    public void onFriendRequestsRetrieved(ArrayList<User> users) {

    }

    @Override
    public void onFriendsRetrieved(ArrayList<User> users) {

    }

    @Override
    public void onMatchesRetrieved(ArrayList<User> users) {

    }

    @Override
    public void onPeopleFound(ArrayList<User> users) {
        if (mAdapter != null && users != null) {
            mAdapter.addUsers(users);
        }
    }
}
