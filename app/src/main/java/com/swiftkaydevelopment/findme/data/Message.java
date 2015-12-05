package com.swiftkaydevelopment.findme.data;

import java.io.Serializable;

/**
 * Created by Kevin Haines on 10/21/15.
 */
public class Message implements Serializable{
    /**
     * this class will be responsible for storing and managing message data as well as performing
     * operations such as delete, unsend, resend, storing until regained internet access, reporting,
     * getting information about, editing sent messages, sending read status and delete status
     */

    public static final int READ = 1;
    public static final int UNREAD = 0;
    public static final int SEEN = 1;
    public static final int UNSEEN = 0;
    public static final int DELETED = 1;
    public static final int NOT_DELETED = 0;


    private static String mUid;
    private String mMessageId;
    private String mMessage;
    private String mTime;
    private String mOuid;
    private User user;
    private String mThreadId;
    private int mReadStatus;
    private int mSeenStatus;
    private int mDeletedStatus;
    private String mTag;
    private String mSenderId;
    private String mMessageImageLocation;

    public static Message instance(String uid) {
        Message message = new Message();
        message.mUid = uid;
        return message;
    }

    public void attachUser(){
        //todo: here is where we will call a method from
        //UserManager to check in cache for user and return
        //or fetch the user from the server
    }

    public static String getUid() {
        return mUid;
    }

    public static void setUid(String mUid) {
        Message.mUid = mUid;
    }

    public String getMessageId() {
        return mMessageId;
    }

    public void setMessageId(String mMessageId) {
        this.mMessageId = mMessageId;
    }

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String mMessage) {
        this.mMessage = mMessage;
    }

    public String getTime() {
        return mTime;
    }

    public void setTime(String mTime) {
        this.mTime = mTime;
    }

    public String getOuid() {
        return mOuid;
    }

    public String getSenderId() { return mSenderId; }

    public void setSenderId(String id) { mSenderId = id; }

    public void setOuid(String mOuid) {
        this.mOuid = mOuid;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getThreadId() {
        return mThreadId;
    }

    public void setThreadId(String mThreadId) {
        this.mThreadId = mThreadId;
    }

    public int getReadStatus() {
        return mReadStatus;
    }

    public void setReadStatus(int mreadStatus) {
        this.mReadStatus = mreadStatus;
    }

    public int getSeenStatus() {
        return mSeenStatus;
    }

    public void setSeenStatus(int mSeenStatus) {
        this.mSeenStatus = mSeenStatus;
    }

    public int getDeletedStatus() {
        return mDeletedStatus;
    }

    public void setDeletedStatus(int mDeletedStatus) {
        this.mDeletedStatus = mDeletedStatus;
    }

    public void setTag(String tag) {
        mTag = tag;
    }

    public String getTag() {
        return mTag;
    }
}
