package com.swiftkaytech.findme.utils;

/**
 * Created by swift on 6/15/2015.
 */
public class VarHolder {

    public static final int NEWSFEED = 1;
    public static final int FINDPEOPLE = 2;
    public static final int USERSPHOTOS = 3;
    public static final int MATCHES = 4;
    public static final int GAMES = 5;
    public static final int SETTINGS = 6;
    public static final int PROFILE = 7;
    public static final int MESSAGESINLINE = 8;
    public static final int NOTIFICATIONS = 9;
    public static final int MESSAGES = 10;
    public static final int UPDATESTATUS = 11;
    public static final int EARNFREECREDITS = 12;
    public static final int STATUSPHOTO = 13;
    public static final int STATUSLIKES = 14;
    public static final int COMMENTS = 15;
    public static final int FRIENDS = 16;




    public static String ouid;
    public static String postid;
    public static String typeid;
    public static String ouname;
    public static String threadid;

    //USED FOR REGISTRATION
    public static String firstname;
    public static String lastname;
    public static String dob;
    public static String gender;

    //used for authentication
    public static String credemail;
    public static String credpassword;
    public static final String authkey = "1781";

    //used for storing information for commentsfrag
    public static status status = new status();

    public static class status{
        String postinguserid;
        String name;
        String posttime;
        String post;
        String postimagelocation;
        String postinguserslocation;
        String numlikes;
        String[] likeduserids;
        String numcomments;
        String propiclocation;
        String postid;
        boolean liked = false;

    }

    //log string
    public static final String TAG = "swiftkaydevelopment";
}
