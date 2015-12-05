package com.swiftkaydevelopment.findme.managers;

import java.util.List;

/**
 * Created by Kevin Haines on 10/21/15.
 */
public class LookingForManager {
    public enum LookingFor{
        CASUAL_DATING, LONG_TERM_RELATIONSHIP, HOOK_UPS, FWB,
        FRIENDS, NETWORKING, OTHER
    }
    List<LookingFor> lookingFors;

    public List<LookingFor> getLookingFors(){

        return lookingFors;
    }

}
