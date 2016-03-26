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

import android.app.Dialog;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import com.swiftkaydevelopment.findme.R;
import com.swiftkaydevelopment.findme.data.AppConstants;

public class MediateTermsFragment extends AppCompatDialogFragment implements View.OnClickListener {
    public static final String TAG = "MediateTermsFragment";

    private Button accept, cancel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.mediate_terms, container, false);
        accept = (Button) layout.findViewById(R.id.accept);
        cancel = (Button) layout.findViewById(R.id.cancel);

        accept.setOnClickListener(this);
        cancel.setOnClickListener(this);
        return layout;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AppCompatDialog dialog = new AppCompatDialog(getActivity());
        dialog.supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == accept.getId()) {
            PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putBoolean(AppConstants.PreferenceConstants.PREF_MEDIATE_TERMS, true).commit();
            dismiss();
        } else if (v.getId() == cancel.getId()) {
            getActivity().finish();
        }
    }
}
