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

package com.swiftkaydevelopment.findme.data;

import android.content.Context;
import android.os.Bundle;

import com.swiftkaydevelopment.findme.R;
import com.swiftkaydevelopment.findme.activity.NotificationActivity;
import com.swiftkaydevelopment.findme.data.datainterfaces.Notifiable;

import java.io.Serializable;

public class Comment implements Serializable, Notifiable {

    public static final String TAG = "Comment";
    private String mCommentId;
    private String mPostId;
    private String mComment;
    private String mTime;
    private User mUser;
    private static String mUid;
    private String mCommentUserId;


    public static Comment createComment(String uid){
        Comment comment = new Comment();
        mUid = uid;
        return comment;
    }

    /**
     * gets the post id related to this comment
     * @return postid related to this comment
     */
    public String getPostId() {
        return mPostId;
    }

    /**
     * sets the unique id of the commenting user
     * @param uid unique id of the commenting user
     */
    public void setCommentUserId(String uid){
        mCommentUserId = uid;
    }

    /**
     * get the commenting users id
     * @return commenting users id
     */
    public String getCommentUserId(){
        return mCommentUserId;
    }

    /**
     * sets the post id related to this comment
     * @param mPostId postid related to this comment
     */
    public void setPostId(String mPostId) {
        this.mPostId = mPostId;
    }

    /**
     * gets the user who posted this comment
     * @return User who posted this comment
     */
    public User getUser() {
        return mUser;
    }

    /**
     * sets the user who posted this comment
     * @param mUser User who posted this comment
     */
    public void setUser(User mUser) {
        this.mUser = mUser;
    }

    /**
     * gets the time since this comment was posted
     * @return time since this comment was posted
     */
    public String getTime() {
        return mTime;
    }

    /**
     * sets the time since this comment was posted
     * @param mTime time since this comment was posted
     */
    public void setTime(String mTime) {
        this.mTime = mTime;
    }

    /**
     * gets the comment text
     * @return comment text
     */
    public String getComment() {
        return mComment;
    }

    /**
     * sets the comment text
     * @param mComment comment text
     */
    public void setComment(String mComment) {
        this.mComment = mComment;
    }

    /**
     * gets the comments id
     * @return comments id
     */
    public String getCommentId() {
        return mCommentId;
    }

    /**
     * sets the comments id
     * @param mCommentId comments id
     */
    public void setCommentId(String mCommentId) {
        this.mCommentId = mCommentId;
    }

    @Override
    public PushData getPushData(Bundle data, Context context) {

//        JSONObject postData = new JSONObject(data.getString("post"));
//        JSONObject userData = new JSONObject(data.getString("user"));

        PushData pushData = new PushData();
        pushData.title = "New comment";
        pushData.message = "Someone commented on your status";
        pushData.resId = R.mipmap.ic_message_black_24dp;
        pushData.intent = PushData.createPendingIntent(NotificationActivity.createIntent(context), context);
        pushData.notificationId = getNotificationId();
        return pushData;
    }

    @Override
    public int getNotificationId() {
        return 5289;
    }

    @Override
    public int getNotificationTypeCount() {
        return 0;
    }
}
