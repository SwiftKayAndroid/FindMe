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

package com.swiftkaydevelopment.findme.database.gatewayInterfaces;

import com.swiftkaydevelopment.findme.data.Post;

import java.util.List;

/**
 * Created by Kevin Haines on 2/25/16.
 * Class Overview:
 */
public interface PostsGateway {

    /**
     * Gets a list of all the posts in the db
     *
     * @return List of posts
     */
    List<Post> findAll();

    /**
     * Gets a single post from the Db
     *
     * @return post
     */
    Post find(String postid);

    /**
     * Inserts a post into the db
     *
     * @param post Post to insert
     */
    void insert(Post post);

    /**
     * Inserts a list of Posts into the db
     *
     * @param posts Posts to insert
     */
    void insertAll(List<Post> posts);

    /**
     * Updates a post
     *
     * @param post Post to update
     */
    void update(Post post);

    /**
     * Deletes a list of posts from the db
     *
     * @param posts Posts to delete
     */
    void deleteAll(List<Post> posts);

    /**
     * Deletes a single post from the db
     *
     * @param post Post to delete
     */
    void delete(Post post);
}
