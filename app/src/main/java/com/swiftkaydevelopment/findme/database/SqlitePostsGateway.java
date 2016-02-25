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

package com.swiftkaydevelopment.findme.database;

import android.content.ContentValues;
import android.database.Cursor;

import com.swiftkaydevelopment.findme.data.Post;
import com.swiftkaydevelopment.findme.database.gatewayInterfaces.PostsGateway;

import java.util.List;

/**
 * Created by Kevin Haines on 2/25/16.
 * Class Overview:
 */
public class SqlitePostsGateway extends BaseSQLiteGateway implements PostsGateway {

    public SqlitePostsGateway(DatabaseHelper sqLiteModule) {
        super(sqLiteModule);
    }

    @Override
    public List<Post> findAll() {
        return null;
    }

    @Override
    public Post find(String postid) {
        return null;
    }

    @Override
    public void insert(Post post) {

    }

    @Override
    public void insertAll(List<Post> posts) {

    }

    @Override
    public void update(Post post) {

    }

    @Override
    public void deleteAll(List<Post> posts) {

    }

    @Override
    public void delete(Post post) {

    }

    private ContentValues postToContentValues(Post post) {
        ContentValues values = new ContentValues();

        return values;
    }

    private Post cursorToPost(Cursor c) {
        Post post = Post.createPost();

        return post;
    }
}
