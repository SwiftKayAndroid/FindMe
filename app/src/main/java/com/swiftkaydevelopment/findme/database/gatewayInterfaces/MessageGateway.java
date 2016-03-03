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

import com.swiftkaydevelopment.findme.data.Message;

import java.util.ArrayList;

/**
 * Created by Kevin Haines on 3/3/16.
 * Class Overview:
 */
public interface MessageGateway {

    /**
     * Inserts a message into the database
     *
     * @param message Message to insert
     * @return true if successful
     */
    boolean insert(Message message);

    /**
     * Updates a message in the db
     *
     * @param message Message to update
     * @return true if successful
     */
    boolean update(Message message);

    /**
     * Deletes a message from the db
     *
     * @param message Message to delete
     * @return true if successful
     */
    boolean delete(Message message);

    /**
     * Finds a message in the db
     *
     * @param messageId id of the message to find
     * @return Message if found
     */
    Message find(String messageId);

    /**
     * Gets all the messages based on the conversation between
     * two users
     *
     * @param uid Current User's account id
     * @param ouid other users id
     * @return list of messages for that conversation
     */
    ArrayList<Message> findAll(String uid, String ouid);
}
