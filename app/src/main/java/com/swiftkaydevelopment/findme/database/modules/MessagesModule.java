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

package com.swiftkaydevelopment.findme.database.modules;

import com.swiftkaydevelopment.findme.data.Message;

import java.util.ArrayList;

/**
 * Created by Kevin Haines on 3/3/16.
 * Class Overview:
 */
public interface MessagesModule {

    /**
     * Creates a message in the db
     *
     * @param message Message to create
     * @return true if successful
     */
    boolean createMessage(Message message);

    /**
     * Updates a message in the database
     *
     * @param message Message to update
     * @return true if successful
     */
    boolean updateMessage(Message message);

    /**
     * Deletes a message from the database
     *
     * @param message Message to delete
     * @return true if successful
     */
    boolean deleteMessage(Message message);

    /**
     * Gets a specific Message from the database using
     * the messages unique id
     *
     * @param messageId Message unique id
     * @return the Message if found
     */
    Message getMessage(String messageId);

    /**
     * Gets a list of messages from the Database
     *
     * @param uid User's id
     * @param ouid Other users id
     * @return List of messages if found
     */
    ArrayList<Message> getMessages(String uid, String ouid);
}
