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

package com.swiftkaydevelopment.findme.events;

import com.swiftkaydevelopment.findme.data.Post;

import java.util.ArrayList;

/**
 * Created by Kevin Haines on 2/29/16.
 * Class Overview:
 */
public class NewsFeedPostsRetrieved {
    public ArrayList<Post> posts;

    public NewsFeedPostsRetrieved(ArrayList<Post> posts) {
        this.posts = posts;
    }
}
