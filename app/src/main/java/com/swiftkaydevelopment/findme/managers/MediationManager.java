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

package com.swiftkaydevelopment.findme.managers;

import android.os.AsyncTask;

import com.swiftkaydevelopment.findme.data.MediationPhoto;
import com.swiftkaydevelopment.findme.events.MediationPhotosRetrieved;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MediationManager {
    public static final String TAG = "MediationManager";

    private static MediationManager sInstance = null;

    public static MediationManager instance() {
        synchronized (MediationManager.class) {
            if (sInstance == null) {
                sInstance= new MediationManager();
            }
            return sInstance;
        }
    }

    public void getMediatedPhotos() {
        new GetMediatedPhotosTask().execute();
    }

    public void setPhotoMediationDecision(MediationPhoto photo) {
        new SetPhotoMediationDecision(photo).execute();
    }

    private class GetMediatedPhotosTask extends AsyncTask<Void, Void, ArrayList<MediationPhoto>> {
        @Override
        protected ArrayList<MediationPhoto> doInBackground(Void... params) {
            ConnectionManager connectionManager = new ConnectionManager();
            connectionManager.setUri("getmediationphotos.php");
            String result = connectionManager.sendHttpRequest();
            ArrayList<MediationPhoto> photos = new ArrayList<>();

            if (result != null) {
                try {
                    JSONObject object = new JSONObject(result);
                    JSONArray array = object.getJSONArray("pics");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject child = array.getJSONObject(i);
                        photos.add(MediationPhoto.createFromJson(child));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return photos;
        }

        @Override
        protected void onPostExecute(ArrayList<MediationPhoto> mediationPhotos) {
            super.onPostExecute(mediationPhotos);
            EventBus.getDefault().postSticky(new MediationPhotosRetrieved(mediationPhotos));
        }
    }

    private class SetPhotoMediationDecision extends AsyncTask<Void, Void, Void> {
        MediationPhoto photo;

        public SetPhotoMediationDecision(MediationPhoto photo) {
            this.photo = photo;
        }

        @Override
        protected Void doInBackground(Void... params) {
            ConnectionManager connectionManager = new ConnectionManager();
            connectionManager.setUri("approvemediatedphoto.php");
            connectionManager.addParam("dataid", photo.dataId.trim());
            connectionManager.addParam("decision", photo.decision);
            connectionManager.addParam("itemid", photo.id);
            connectionManager.setMethod(ConnectionManager.POST);
            connectionManager.addParam("dt", photo.type.trim());
            connectionManager.sendHttpRequest();
            return null;
        }
    }
}
