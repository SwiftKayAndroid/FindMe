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

package com.swiftkaydevelopment.findme.settings;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.swiftkaydevelopment.findme.R;

public class GenderSelectDialog extends AppCompatDialogFragment {

    public interface GenderSelectListener {
        void onGenderSelected(String gender);
    }

    public static final String TAG = "GenderSelectDialog";

    RadioGroup rgGender;
    private GenderSelectListener mListener;

    public void setListener(GenderSelectListener listener) {
        mListener = listener;
    }

    public static GenderSelectDialog newInstance() {
        GenderSelectDialog frag = new GenderSelectDialog();
        return frag;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View layout = inflater.inflate(R.layout.gender_select_dialog, container, false);
        rgGender = (RadioGroup) layout.findViewById(R.id.rgGenderSelect);
        rgGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (mListener != null) {
                    mListener.onGenderSelected(((RadioButton) layout.findViewById(checkedId)).getText().toString());
                }
            }
        });
        return layout;
    }

    @Override
    @NonNull
    public AppCompatDialog onCreateDialog(Bundle savedInstanceState) {
        AppCompatDialog dialog = new AppCompatDialog(getActivity());
        dialog.supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }
}
