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

import android.content.Context;
import android.net.Uri;
import android.widget.MediaController;
import android.widget.VideoView;

public class VideoManager {
    public static final String TAG = "VideoManager";

    public static void playVideo(Context context, VideoView view, String url) {

        Uri uri = Uri.parse(url);
        MediaController controller = new MediaController(context);
        view.setMediaController(controller);
        controller.setAnchorView(view);
        view.setVideoURI(uri);
        view.start();
    }
}
