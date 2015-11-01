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

package com.swiftkaytech.findme.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.swiftkaytech.findme.R;

public class ErrorDisplayer {
    private static final String TAG = "ErrorDisplayer";

    private static String mUid;
    private static ErrorDisplayer displayer;
    private static Context mContext;

    public static ErrorDisplayer getInstance(Context context) {
        if (displayer == null) {
            displayer = new ErrorDisplayer();
        }
        mContext = context;
        return displayer;
    }

    public void err(String message) {

    }

    public void webErr(String webResponse, String stackTrace) {
        Dialog dialog = new Dialog(mContext);
        View view = ((LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.error_displayer, null);
        TextView error = (TextView) view.findViewById(R.id.errorMessage);
        error.setText(webResponse);

        TextView stack = (TextView) view.findViewById(R.id.stackTrace);
        stack.setText(stackTrace);
        dialog.setContentView(view);
        dialog.show();
    }

}
