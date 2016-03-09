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

package com.swiftkaydevelopment.findme.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;

import com.swiftkaydevelopment.findme.R;

public class FilterInputAdapter extends RecyclerView.Adapter<FilterInputAdapter.FilterInputViewHolder>{
    public static final String TAG = "FilterInputAdapter";

    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_CONTENT = 1;


    private static String mUid;

    @Override
    public FilterInputViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        if (viewType == VIEW_TYPE_HEADER) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.filter_input_header, parent, false);
        } else {
            view = null;
            //view = LayoutInflater.from(parent.getContext()).inflate();
        }
        return new FilterInputViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(FilterInputViewHolder holder, int position) {

    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return VIEW_TYPE_HEADER;
        }
        return VIEW_TYPE_CONTENT;
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    protected class FilterInputViewHolder extends RecyclerView.ViewHolder {

        protected int viewType;
        CheckBox cbMessages;
        CheckBox cbPosts;
        CheckBox cbComments;
        EditText etBannedWords;
        public FilterInputViewHolder(View itemView, int viewType) {
            super(itemView);
            this.viewType = viewType;

            if (viewType == VIEW_TYPE_HEADER) {
                cbMessages = (CheckBox) itemView.findViewById(R.id.cbFilterInputMessages);
                cbPosts = (CheckBox) itemView.findViewById(R.id.cbFilterInputPosts);
                cbComments = (CheckBox)itemView.findViewById(R.id.cbFilterInputComments);
                etBannedWords = (EditText) itemView.findViewById(R.id.etFilterInputBannedWord);
            } else {

            }
        }
    }

}
