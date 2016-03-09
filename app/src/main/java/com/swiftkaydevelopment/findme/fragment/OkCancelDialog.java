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

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.swiftkaydevelopment.findme.R;

import java.io.Serializable;

public class OkCancelDialog extends AppCompatDialogFragment implements View.OnClickListener{
    public static final String TAG = "OkCancelDialog";
    private static final String ARG_TITLE = "ARG_TITLE";
    private static final String ARG_MESSAGE = "ARG_MESSAGE";
    private static final String ARG_POS = "ARG_POS";
    private static final String ARG_NEG = "ARG_NEG";
    private static final String ARG_TAG = "ARG_TAG";

    public interface OkCancelDialogListener {
        void onOkPressed(Object tag);
        void onCancelPressed(Object tag);
    }

    String title;
    String message;
    String positiveText;
    String negativeText;

    Object tag;

    private OkCancelDialogListener mListener;

    public static OkCancelDialog newInstance(String title, String message, String positiveText, String negativeText) {
        OkCancelDialog dialog = new OkCancelDialog();
        Bundle b = new Bundle();
        b.putString(ARG_TITLE, title);
        b.putString(ARG_MESSAGE, message);
        b.putString(ARG_POS, positiveText);
        b.putString(ARG_NEG, negativeText);
        dialog.setArguments(b);
        return dialog;
    }

    public void setOkCancelDialogListener(OkCancelDialogListener listener) {
        mListener = listener;
    }

    public void setTag(Object tag) {
        this.tag = tag;
    }

    @Override
    public AppCompatDialog onCreateDialog(Bundle savedInstanceState) {
        AppCompatDialog dialog = new AppCompatDialog(getActivity());
        View layout = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.okcanceldialog, null);

        if (savedInstanceState != null) {
            title = savedInstanceState.getString(ARG_TITLE);
            message = savedInstanceState.getString(ARG_MESSAGE);
            positiveText = savedInstanceState.getString(ARG_POS);
            negativeText = savedInstanceState.getString(ARG_NEG);
            tag = savedInstanceState.getSerializable(ARG_TAG);
        } else if (getArguments() != null) {
            title = getArguments().getString(ARG_TITLE);
            message = getArguments().getString(ARG_MESSAGE);
            positiveText = getArguments().getString(ARG_POS);
            negativeText = getArguments().getString(ARG_NEG);
        }

        if (positiveText == null) {
            positiveText = "OK";
        }
        if (negativeText == null) {
            negativeText = "Cancel";
        }

        TextView tvTitle = (TextView) layout.findViewById(R.id.okTitle);
        TextView tvMessage = (TextView) layout.findViewById(R.id.okMessage);
        TextView tvOk = (TextView) layout.findViewById(R.id.okOk);
        TextView tvCancel = (TextView) layout.findViewById(R.id.okCancel);

        tvTitle.setText(title);
        tvMessage.setText(message);
        tvOk.setText(positiveText);
        tvCancel.setText(negativeText);

        tvOk.setOnClickListener(this);
        tvCancel.setOnClickListener(this);

        dialog.supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(layout);
        return  dialog;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(ARG_TITLE, title);
        outState.putString(ARG_MESSAGE, message);
        outState.putString(ARG_POS, positiveText);
        outState.putString(ARG_NEG, negativeText);
        outState.putSerializable(ARG_TAG, (Serializable) tag);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.okOk) {
            if (mListener != null) {
                mListener.onOkPressed(tag);
            }
            dismiss();

        } else if (v.getId() == R.id.okCancel) {
            if (mListener != null) {
                mListener.onCancelPressed(tag);
            }
            dismiss();
        }
    }
}
