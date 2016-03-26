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
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import com.swiftkaydevelopment.findme.R;
import com.swiftkaydevelopment.findme.fragment.BaseFragment;
import com.swiftkaydevelopment.findme.managers.SettingsManager;

public class Settings extends BaseFragment {
    public static final String TAG = "Settings";

    private Switch mSwitchMediate;

    public static Settings newInstance(String uid) {
        Settings frag = new Settings();
        Bundle b = new Bundle();
        b.putString(ARG_UID, uid);
        frag.setArguments(b);
        return frag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.setting, container, false);
        mSwitchMediate = (Switch) layout.findViewById(R.id.mediatePicturesSwitch);
        Toolbar toolbar = (Toolbar) layout.findViewById(R.id.settingsToolbar);
        toolbar.setNavigationIcon(R.mipmap.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
        return layout;
    }

    /**
     * Save the settings
     *
     */
    private void save() {
        String mediate;
        if (mSwitchMediate.isChecked()) {
            mediate = "yes";
        } else {
            mediate = "no";
        }

        SettingsManager.instance().updateMediationSettings(mediate, uid);
    }
}
