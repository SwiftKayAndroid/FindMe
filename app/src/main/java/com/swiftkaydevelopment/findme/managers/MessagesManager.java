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

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.swiftkaydevelopment.findme.activity.MessagesActivity;
import com.swiftkaydevelopment.findme.data.Message;
import com.swiftkaydevelopment.findme.data.ThreadInfo;
import com.swiftkaydevelopment.findme.data.User;
import com.swiftkaydevelopment.findme.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

public class MessagesManager {
    public static final String TAG = "MessagesManager";

    public interface MessagesListener{
        void onRetrieveMoreMessages(ArrayList<Message> moreMessages);
        void onMessageDeleted(Message message);
        void onMessageSentComplete(Message message);
        void onMessageUnsent(Message message);
        void onMessageReceived(Message message);
    }

    public interface MessageThreadListener{
        void onThreadDeleted(ThreadInfo threadInfo);
        void onRetrieveMoreThreads(ArrayList<ThreadInfo> threadInfos);
        void onMessageSentComplete(Message message);
        void onMessageUnsent(Message message);
        void onMessageRecevied(Message message);
        void onThreadsPurged();
    }

    private static String mUid;
    private static MessagesManager manager = null;
    private ArrayList<Message> mMessages;
    private static Context mContext;

    private static CopyOnWriteArrayList<MessageThreadListener> mMessageThreadListeners = new CopyOnWriteArrayList<>();
    private static CopyOnWriteArrayList<MessagesListener> mMessagesListeners = new CopyOnWriteArrayList<>();

    public static MessagesManager getInstance(String uid, Context context){
        if (manager == null) {
            manager = new MessagesManager();
        }
        manager.mUid = uid;
        mContext = context;
        return manager;
    }

    public void addThreadsListener(MessageThreadListener listener) {
        mMessageThreadListeners.add(listener);
    }

    public void removeThreadsListener(MessageThreadListener listener) {
        mMessageThreadListeners.remove(listener);
    }

    public void addMessagesListener(MessagesListener listener) {
        mMessagesListeners.add(listener);
    }

    public void removeMessagesListener(MessagesListener listener) {
        mMessagesListeners.remove(listener);
    }

    public void refreshMessages(ThreadInfo threadInfo, Context context){
        mContext = context;
        new FetchMessagesTask("0", threadInfo).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
    }

    public void refreshThreads(Context context){
        mContext = context;
        new FetchThreadsTask("0").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
    }

    public void getMoreMessages(String lastMessage, ThreadInfo threadInfo, Context context){
        mContext = context;
        new FetchMessagesTask(lastMessage, threadInfo).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
    }

    public void getMoreMessages(String lastMessage, User user, Context context){
        mContext = context;
        new FetchMessagesTask(lastMessage, user).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
    }

    public void deleteAllThreads(String uid) {
        new DeleteAllThreadsTask(uid).execute();
    }

    public ArrayList<Message> getExistingMessages(){
        if (mMessages != null) {
            if (mMessages.size() > 0) {
                return mMessages;
            }
        }
        return null;
    }

    public void getMoreThreads(String lastThread){
        new FetchThreadsTask(lastThread).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
    }

    public void sendMessage(Message message, User user){
        new SendMessageTask(message, user).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
    }

    public void deleteMessage(Message message){
        new DeleteMessageTask(message).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
    }

    public void deleteThread(ThreadInfo threadInfo){
        new DeleteThreadTask(threadInfo).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
    }

    public void unSendMessage(Message message){
        new UnSendMessageTask(message).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
    }

    public void editMessage(Message message){
        new EditMessageTask(message).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
    }

    public void reportMessage(Message message, String reason){
        new ReportMessageTask(message, reason).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
    }

    //todo: issue #69
//    public void sendPictureMessage(){
//
//    }

    public void markThreadAsRead(ThreadInfo threadInfo){
        new MarkThreadAsReadTask(threadInfo).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
    }

    public void markThreadAsDeleted(ThreadInfo threadInfo){
        new MarkThreadAsDeletedTask(threadInfo).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
    }

    public void markThreadAsSeen(String uid, String threadid) {
        new MarkThreadAsSeenTask(uid, threadid).execute();
    }

    //todo: issue #67
//    public void requestModerationBeforeReading(ThreadInfo threadInfo){
//    }

    //// TODO: 10/28/15 issue # 71
//    public void showAsTypingStarted(){}
//
//    public void showAsTypingStopped(){}

    public static void messageNotificationReceived(Message msg) {

        if (mMessageThreadListeners.size() < 1 && mMessagesListeners.size() <1) {
            sendNotification(msg);
        } else {
            for (MessagesListener l : mMessagesListeners) {
                if (l != null) {
                    l.onMessageReceived(msg);
                }
            }
            for (MessageThreadListener l : mMessageThreadListeners) {
                if (l != null) {
                    l.onMessageRecevied(msg);
                }
            }
        }
    }

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param message GCM message received.
     */
    public static void sendNotification(Message message) {
        Intent intent = MessagesActivity.createIntent(mContext, message.getUser());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(mContext)
                .setSmallIcon(R.drawable.redfsmall)
                .setContentTitle("New Message")
                .setContentText(message.getMessage())
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    private class FetchMessagesTask extends AsyncTask<Void, Void, ArrayList<Message>>{
        String lastMessage;
        ThreadInfo threadInfo;
        User user;

        public FetchMessagesTask(String lastMessage, ThreadInfo threadInfo) {
            this.lastMessage = lastMessage;
            this.threadInfo = threadInfo;
        }

        public FetchMessagesTask(String lastMessage, User user) {
            this.lastMessage = lastMessage;
            this.user = user;
        }

        @Override
        protected ArrayList<Message> doInBackground(Void... params) {
            ArrayList<Message> mList = new ArrayList<>();

            ConnectionManager connectionManager = new ConnectionManager();
            connectionManager.setMethod(ConnectionManager.POST);
            connectionManager.addParam("uid", mUid);
            connectionManager.addParam("lastmessage", lastMessage);
            if (threadInfo != null) {
                connectionManager.addParam("ouid", threadInfo.ouid);
            } else {
                connectionManager.addParam("ouid", user.getOuid());
            }
            connectionManager.setUri("getmessages.php");

            try {
                JSONObject jsonObject = new JSONObject(connectionManager.sendHttpRequest());
                JSONArray jsonArray = jsonObject.getJSONArray("messages");

                int length = jsonArray.length();
                for(int i = 0; i < length; i++) {
                    JSONObject child = jsonArray.getJSONObject(i);
                    Message m = Message.instance();
                    m.setTime(child.getString("time"));

                    if (child.getString("deleted_status").equals("deleted")) {
                        m.setDeletedStatus(1);
                    } else {
                        m.setDeletedStatus(0);
                    }
                    m.setMessage(child.getString("message"));
                    m.setMessageId(child.getString("id"));
                    m.setOuid(child.getString("ouid"));
                    m.setSenderId(child.getString("senderid"));
                    if (child.getString("readstat").equals("read")) {
                        m.setReadStatus(1);
                    } else {
                        m.setReadStatus(0);
                    }
                    if (child.getString("seenstat").equals("seen")) {
                        m.setSeenStatus(1);
                    } else {
                        m.setSeenStatus(0);
                    }
                    m.setTag(child.getString("tag"));
                    m.setThreadId(child.getString("threadid"));
                    m.setUser(User.createUserFromJson(child.getJSONObject("user")));
                    mList.add(m);
                }
            } catch(JSONException e) {
                e.printStackTrace();
            }
            return mList;
        }

        @Override
        protected void onPostExecute(ArrayList<Message> messages) {
            super.onPostExecute(messages);

            for (MessagesListener l : mMessagesListeners) {
                l.onRetrieveMoreMessages(messages);
            }
        }
    }

    private class FetchThreadsTask extends AsyncTask<Void, Void, ArrayList<ThreadInfo>>{
        String lastThread;

        public FetchThreadsTask(String lastThread) {
            this.lastThread = lastThread;
        }

        @Override
        protected ArrayList<ThreadInfo> doInBackground(Void... params) {
            ArrayList<ThreadInfo> tList = new ArrayList<>();

            ConnectionManager connectionManager = new ConnectionManager();
            connectionManager.setMethod(ConnectionManager.POST);
            connectionManager.addParam("uid", mUid);
            connectionManager.addParam("lastthread", lastThread);
            connectionManager.setUri("getmessagethreads.php");

            final String result = connectionManager.sendHttpRequest();

            if (result != null) {

                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray jsonArray = jsonObject.getJSONArray("threads");
                    int length = jsonArray.length();
                    for (int i = 0; i < length; i++) {
                        JSONObject child = jsonArray.getJSONObject(i);
                        ThreadInfo t = ThreadInfo.instance(mUid);
                        t.ouid = child.getString("ouid");
                        t.lastMessage = child.getString("message");
                        if (child.getString("readstat").equals("read")) {
                            t.readStatus = 1;
                        } else {
                            t.readStatus = 0;
                        }
                        t.threadId = child.getString("threadid");
                        t.threadUser = User.createUserFromJson(child.getJSONObject("user"));
                        t.time = child.getString("time");
                        tList.add(t);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return tList;
        }

        @Override
        protected void onPostExecute(ArrayList<ThreadInfo> threadInfos) {
            super.onPostExecute(threadInfos);

            for (MessageThreadListener l : mMessageThreadListeners) {
                if (l != null) {
                    l.onRetrieveMoreThreads(threadInfos);
                }
            }
        }
    }

    private class SendMessageTask extends AsyncTask<Void, Void, String>{
        Message message;
        User user;

        public SendMessageTask(Message message, User user) {
            this.message = message;
            this.user = user;
        }

        @Override
        protected String doInBackground(Void... params) {
            ConnectionManager connectionManager = new ConnectionManager();
            connectionManager.setMethod(ConnectionManager.POST);
            connectionManager.addParam("uid", mUid);
            connectionManager.addParam("ouid", user.getOuid());
            connectionManager.addParam("message", message.getMessage());
            connectionManager.setUri("sendmessage.php");
            return connectionManager.sendHttpRequest();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Message m = Message.instance();
            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONObject child = jsonObject.getJSONObject("lastmessage");

                m.setThreadId(child.getString("threadid"));
                m.setSeenStatus(0);
                m.setReadStatus(1);
                m.setOuid(child.getString("ouid"));
                m.setDeletedStatus(0);
                m.setMessage(child.getString("message"));
                m.setMessageId("id");
                m.setTime("Just now");
                m.setUser(UserManager.getInstance(mUid, mContext).me());

            } catch (JSONException e) {
                e.printStackTrace();
            }

            for (MessagesListener l : mMessagesListeners) {
                l.onMessageSentComplete(m);
            }

            for (MessageThreadListener l : mMessageThreadListeners) {
                l.onMessageSentComplete(m);
            }
        }
    }

    private class DeleteMessageTask extends AsyncTask<Void, Void, Void>{
        Message message;


        public DeleteMessageTask(Message message) {
            this.message = message;
        }

        @Override
        protected Void doInBackground(Void... params) {
            ConnectionManager connectionManager = new ConnectionManager();
            connectionManager.setMethod(ConnectionManager.POST);
            connectionManager.addParam("uid", mUid);
            connectionManager.addParam("messageid", message.getMessageId());
            connectionManager.setUri("deletemessage.php");
            connectionManager.sendHttpRequest();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            for (MessagesListener l : mMessagesListeners) {
                if (l != null) {
                    l.onMessageDeleted(message);
                }
            }
        }
    }

    private class DeleteThreadTask extends AsyncTask<Void, Void, Void>{
        ThreadInfo threadInfo;

        public DeleteThreadTask(ThreadInfo threadInfo) {
            this.threadInfo = threadInfo;
        }

        @Override
        protected Void doInBackground(Void... params) {
            ConnectionManager connectionManager = new ConnectionManager();
            connectionManager.setMethod(ConnectionManager.POST);
            connectionManager.addParam("uid", mUid);
            connectionManager.addParam("threadid", threadInfo.threadId);
            connectionManager.setUri("deletethread.php");
            connectionManager.sendHttpRequest();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            for (MessageThreadListener l : mMessageThreadListeners) {
                l.onThreadDeleted(threadInfo);
            }
        }
    }

    private class UnSendMessageTask extends AsyncTask<Void, Void, Void>{
        Message message;

        public UnSendMessageTask(Message message) {
            this.message = message;
        }

        @Override
        protected Void doInBackground(Void... params) {
            ConnectionManager connectionManager = new ConnectionManager();
            connectionManager.setMethod(ConnectionManager.POST);
            connectionManager.addParam("uid", mUid);
            connectionManager.addParam("tag", message.getTag());
            connectionManager.setUri("unsendmessage.php");
            connectionManager.sendHttpRequest();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            for (MessagesListener l : mMessagesListeners) {
                l.onMessageUnsent(message);
            }

            for (MessageThreadListener l : mMessageThreadListeners) {
                l.onMessageUnsent(message);
            }
        }
    }

    private class DeleteAllThreadsTask extends AsyncTask<Void, Void, Void> {
        String uid;

        public DeleteAllThreadsTask(String uid) {
            this.uid = uid;
        }

        @Override
        protected Void doInBackground(Void... params) {
            ConnectionManager connectionManager = new ConnectionManager();
            connectionManager.setMethod(ConnectionManager.POST);
            connectionManager.addParam("uid", uid);
            connectionManager.setUri("deleteallthreads.php");
            connectionManager.sendHttpRequest();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            for (MessageThreadListener l : mMessageThreadListeners) {
                if (l != null) {
                    l.onThreadsPurged();
                }
            }
        }
    }

    private class EditMessageTask extends AsyncTask<Void, Void, Void>{
        Message message;

        public EditMessageTask(Message message) {
            this.message = message;
        }

        @Override
        protected Void doInBackground(Void... params) {
            ConnectionManager connectionManager = new ConnectionManager();
            connectionManager.setMethod(ConnectionManager.POST);
            connectionManager.addParam("uid", mUid);
            connectionManager.addParam("message", message.getMessage());
            connectionManager.addParam("messageid", message.getMessageId());
            connectionManager.setUri("editmessage.php");
            connectionManager.sendHttpRequest();
            return null;
        }
    }

    private class ReportMessageTask extends AsyncTask<Void, Void, Void>{
        Message message;
        String reportReason;

        public ReportMessageTask(Message message, String reportReason) {
            this.message = message;
            this.reportReason = reportReason;
        }

        @Override
        protected Void doInBackground(Void... params) {
            ConnectionManager connectionManager = new ConnectionManager();
            connectionManager.setMethod(ConnectionManager.POST);
            connectionManager.addParam("uid", mUid);
            connectionManager.addParam("messageid", message.getMessageId());
            connectionManager.addParam("reportreason", reportReason);
            connectionManager.setUri("reportmessage.php");
            connectionManager.sendHttpRequest();
            return null;
        }
    }
//todo:
//    private class SendPictureMessageTask extends AsyncTask<Void, Void, Void>{
//        String messageText;
//        File picture;
//
//        public SendPictureMessageTask(String messageText, File picture) {
//            this.messageText = messageText;
//            this.picture = picture;
//        }
//
//        @Override
//        protected Void doInBackground(Void... params) {
//            ConnectionManager connectionManager = new ConnectionManager();
//            connectionManager.setMethod(ConnectionManager.POST);
//            connectionManager.addParam("uid", mUid);
//            connectionManager.addParam("message", messageText);
//            connectionManager.setUri("");
//            connectionManager.sendHttpRequest();
//            return null;
//        }
//    }

    private class MarkThreadAsReadTask extends AsyncTask<Void, Void, Void>{
        ThreadInfo threadInfo;

        public MarkThreadAsReadTask(ThreadInfo threadInfo) {
            this.threadInfo = threadInfo;
        }

        @Override
        protected Void doInBackground(Void... params) {
            ConnectionManager connectionManager = new ConnectionManager();
            connectionManager.setMethod(ConnectionManager.POST);
            connectionManager.addParam("uid", mUid);
            connectionManager.addParam("threadid", threadInfo.threadId);
            connectionManager.setUri("markthreadasread.php");
            connectionManager.sendHttpRequest();
            return null;
        }
    }

    private class MarkThreadAsDeletedTask extends AsyncTask<Void, Void, Void>{
        ThreadInfo threadInfo;

        public MarkThreadAsDeletedTask(ThreadInfo threadInfo) {
            this.threadInfo = threadInfo;
        }

        @Override
        protected Void doInBackground(Void... params) {
            ConnectionManager connectionManager = new ConnectionManager();
            connectionManager.setMethod(ConnectionManager.POST);
            connectionManager.addParam("uid", mUid);
            connectionManager.addParam("threadid", threadInfo.threadId);
            connectionManager.setUri("markthreadasdeleted.php");
            connectionManager.sendHttpRequest();
            return null;
        }
    }

    private class MarkThreadAsSeenTask extends AsyncTask<Void, Void, Void> {
        String uid;
        String threadid;

        public MarkThreadAsSeenTask(String uid, String threadid) {
            this.uid = uid;
            this.threadid = threadid;
        }

        @Override
        protected Void doInBackground(Void... params) {
            ConnectionManager connectionManager = new ConnectionManager();
            connectionManager.setMethod(ConnectionManager.POST);
            connectionManager.addParam("uid", uid);
            connectionManager.addParam("threadid", threadid);
            connectionManager.setUri("seenmessage.php");
            connectionManager.sendHttpRequest();
            return null;
        }
    }
//todo:
//    private class RequestModerationBeforeReadingTask extends AsyncTask<Void, Void, Void>{
//        ThreadInfo threadInfo;
//
//        public RequestModerationBeforeReadingTask(ThreadInfo threadInfo) {
//            this.threadInfo = threadInfo;
//        }
//
//        @Override
//        protected Void doInBackground(Void... params) {
//            ConnectionManager connectionManager = new ConnectionManager();
//            connectionManager.setMethod(ConnectionManager.POST);
//            connectionManager.addParam("uid", mUid);
//            connectionManager.addParam("threadid", threadInfo.threadId);
//            connectionManager.setUri("");
//            connectionManager.sendHttpRequest();
//            return null;
//        }
//    }
}
