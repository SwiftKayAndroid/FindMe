package com.swiftkaytech.findme.managers;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kevin Haines on 10/21/15.
 */
public class UserManager {


    public static UserManager instance(){
        UserManager userManager = new UserManager();
        return userManager;
    }
}
