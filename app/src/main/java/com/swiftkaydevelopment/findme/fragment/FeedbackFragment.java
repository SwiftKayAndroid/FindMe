/*
 *      Copyright (C) 2015 Kevin Haines
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 */

package com.swiftkaydevelopment.findme.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.swiftkaydevelopment.findme.R;
import com.swiftkaydevelopment.findme.managers.ConnectionManager;

public class FeedbackFragment extends BaseFragment implements View.OnClickListener {
    public static final String TAG = "FeedbackFragment";

    private EditText etFeedback;

    public static FeedbackFragment newInstance(String uid) {
        FeedbackFragment frag = new FeedbackFragment();
        Bundle b = new Bundle();
        b.putString(ARG_UID, uid);
        frag.setArguments(b);
        return frag;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.feedback, container, false);

        Toolbar toolbar = (Toolbar) layout.findViewById(R.id.toolbar);
        toolbar.setTitle("Feedback");
        toolbar.setNavigationIcon(R.mipmap.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        Button submit = (Button) layout.findViewById(R.id.submitButton);
        etFeedback = (EditText) layout.findViewById(R.id.etFeedback);
        submit.setOnClickListener(this);
        return layout;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.submitButton) {
            if (!etFeedback.getText().toString().isEmpty()) {
                new SubmitFeedbackTask(uid, etFeedback.getText().toString()).execute();
                Toast.makeText(getActivity(), "Thank you for your feedback", Toast.LENGTH_LONG).show();
                getActivity().getSupportFragmentManager().popBackStack();
            }
        }
    }

    private class SubmitFeedbackTask extends AsyncTask<Void, Void, Void> {
        String uid;
        String feedback;

        public SubmitFeedbackTask(String uid, String feedback) {
            this.uid = uid;
            this.feedback = feedback;
        }

        @Override
        protected Void doInBackground(Void... params) {
            ConnectionManager connectionManager = new ConnectionManager();
            connectionManager.setMethod(ConnectionManager.POST);
            connectionManager.setUri("feedback.php");
            connectionManager.addParam("uid", uid);
            connectionManager.addParam("feedback", feedback);
            connectionManager.sendHttpRequest();
            return null;
        }
    }
}
