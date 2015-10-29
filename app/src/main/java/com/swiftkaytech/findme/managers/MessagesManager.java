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

package com.swiftkaytech.findme.managers;

import android.os.AsyncTask;

import com.swiftkaytech.findme.data.Message;
import com.swiftkaytech.findme.data.ThreadInfo;
import com.swiftkaytech.findme.data.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

public class MessagesManager {

    public interface MessagesListener{
        void onRetrieveMoreMessages(ArrayList<Message> moreMessages);
        void onMessageDeleted(Message message);
        void onMessageSentComplete(Message message);
        void onMessageUnsent(Message message);
    }

    public interface MessageThreadListener{
        void onThreadDeleted(ThreadInfo threadInfo);
        void onRetrieveMoreThreads(ArrayList<ThreadInfo> threadInfos);
        void onMessageSentComplete(Message message);
        void onMessageUnsent(Message message);
    }

    private static String mUid;
    private static MessagesManager manager = null;
    private ArrayList<Message> mMessages;

    private CopyOnWriteArrayList<MessageThreadListener> mMessageThreadListeners;
    private CopyOnWriteArrayList<MessagesListener> mMessagesListeners;

    public static MessagesManager getInstance(String uid){
        if (manager == null) {
            manager = new MessagesManager();
        }
        manager.mUid = uid;
        return manager;
    }

    public void refreshMessages(ThreadInfo threadInfo){
        new FetchMessagesTask("0", threadInfo).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
    }

    public void refreshThreads(){
        new FetchThreadsTask("0").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
    }

    public void getMoreMessages(String lastMessage, ThreadInfo threadInfo){
        new FetchMessagesTask(lastMessage, threadInfo).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
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

    public void sendMessage(Message message){
        new SendMessageTask(message).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
    }

    public void deleteMessage(Message message, ThreadInfo threadInfo){
        new DeleteMessageTask(message, threadInfo).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
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

    //todo: issue #67
//    public void requestModerationBeforeReading(ThreadInfo threadInfo){
//    }

    //// TODO: 10/28/15 issue # 71
//    public void showAsTypingStarted(){}
//
//    public void showAsTypingStopped(){}

    private class FetchMessagesTask extends AsyncTask<Void, Void, ArrayList<Message>>{
        String lastMessage;
        ThreadInfo threadInfo;

        public FetchMessagesTask(String lastMessage, ThreadInfo threadInfo) {
            this.lastMessage = lastMessage;
            this.threadInfo = threadInfo;
        }

        @Override
        protected ArrayList<Message> doInBackground(Void... params) {
            ArrayList<Message> mList = new ArrayList<>();

            ConnectionManager connectionManager = new ConnectionManager();
            connectionManager.setMethod(ConnectionManager.POST);
            connectionManager.addParam("uid", mUid);
            connectionManager.addParam("lastmessage", lastMessage);
            connectionManager.addParam("threadid", threadInfo.ouid);
            connectionManager.setUri("getmessages.php");

            try {
                JSONObject jsonObject = new JSONObject(connectionManager.sendHttpRequest());
                JSONArray jsonArray = jsonObject.getJSONArray("messages");

                int length = jsonArray.length();
                for(int i = 0; i < length; i++) {
                    JSONObject child = jsonArray.getJSONObject(i);
                    Message m = Message.instance(mUid);
                    m.setTime(child.getString("time"));

                    if (child.getString("deleted_status").equals("deleted")) {
                        m.setDeletedStatus(1);
                    } else {
                        m.setDeletedStatus(0);
                    }
                    m.setMessage(child.getString("message"));
                    m.setMessageId(child.getString("id"));
                    m.setOuid(child.getString("ouid"));
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
                    m.setThreadId(child.getString("threadid"));
                    m.setUser(User.createUser(mUid).fetchUser(child.getString("ouid")));
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

            try {
                JSONObject jsonObject = new JSONObject(connectionManager.sendHttpRequest());
                JSONArray jsonArray = jsonObject.getJSONArray("threads");
                int length = jsonArray.length();
                for (int i = 0; i < length; i++) {
                    JSONObject child = jsonArray.getJSONObject(i);
                    ThreadInfo t = ThreadInfo.instance(mUid);
                    t.ouid = child.getString("ouid");
                    t.lastMessage = child.getString("message");
                    if (child.getString("readstatus").equals("read")) {
                        t.readStatus = 1;
                    } else {
                        t.readStatus = 0;
                    }
                    t.threadId = child.getString("threadid");
                    t.threadUser = User.createUser(mUid).fetchUser(child.getString("ouid"));
                    t.time = child.getString("time");
                    tList.add(t);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return tList;
        }

        @Override
        protected void onPostExecute(ArrayList<ThreadInfo> threadInfos) {
            super.onPostExecute(threadInfos);

            for (MessageThreadListener l : mMessageThreadListeners) {
                l.onRetrieveMoreThreads(threadInfos);
            }
        }
    }

    private class SendMessageTask extends AsyncTask<Void, Void, String>{
        Message message;

        public SendMessageTask(Message message) {
            this.message = message;
        }

        @Override
        protected String doInBackground(Void... params) {
            ConnectionManager connectionManager = new ConnectionManager();
            connectionManager.setMethod(ConnectionManager.POST);
            connectionManager.addParam("uid", mUid);
            connectionManager.addParam("message", message.getMessage());
            connectionManager.setUri("sendmessage.php");
            return connectionManager.sendHttpRequest();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Message m = Message.instance(mUid);
            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("lastmessage");
                    JSONObject child = jsonArray.getJSONObject(0);

                    m.setThreadId(child.getString("threadid"));
                    m.setSeenStatus(0);
                    m.setReadStatus(1);
                    m.setOuid(child.getString("ouid"));
                    m.setDeletedStatus(0);
                    m.setMessage(child.getString("message"));
                    m.setMessageId("id");
                    m.setTime("Just now");
                    m.setUser(User.createUser(mUid).fetchUser(m.getOuid()));

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
        ThreadInfo threadInfo;

        public DeleteMessageTask(Message message, ThreadInfo threadInfo) {
            this.message = message;
            this.threadInfo = threadInfo;
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
                l.onMessageDeleted(message);
            }

            for (MessageThreadListener l : mMessageThreadListeners) {
                l.onThreadDeleted(threadInfo);
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
            connectionManager.addParam("messageid", message.getMessageId());
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
