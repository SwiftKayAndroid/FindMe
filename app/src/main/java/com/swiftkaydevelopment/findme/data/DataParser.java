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

import java.util.HashMap;
import java.util.Map;

public class DataParser {
    public static final String TAG = "DataParser";

    public static class LookingForParser {

        public static final String SEX = "Sex";
        public static final String FWB = "FWB";
        public static final String FRIENDS = "Friends";
        public static final String SOMETHING_SERIOUS = "Something Serious";
        public static final String IDK = "Idk";
        public static final String CHAT = "Chat";

        public static final int SEX_VAL = 2;
        public static final int FWB_VAL = 4;
        public static final int FRIENDS_VAL = 8;
        public static final int SOMETHING_SERIOUS_VAL = 16;
        public static final int IDK_VAL = 32;
        public static final int CHAT_VAL = 64;

        public static Map<String, Integer> lookingFormap =
                new HashMap<>();

        public static Map<String, Integer> getMap() {
            lookingFormap.clear();
            lookingFormap.put(SEX, SEX_VAL);
            lookingFormap.put(FWB, FWB_VAL);
            lookingFormap.put(FRIENDS, FRIENDS_VAL);
            lookingFormap.put(SOMETHING_SERIOUS, SOMETHING_SERIOUS_VAL);
            lookingFormap.put(IDK, IDK_VAL);
            lookingFormap.put(CHAT, CHAT_VAL);
            return lookingFormap;
        }
    }

    public static int addValue(int currentValue, int addedValue) {
        return (currentValue | addedValue);
    }

    public static int removeValue(int currentValue, int removeValue) {
        return (currentValue ^ removeValue);
    }

    public static boolean contains(int currentValue, int expected) {
        return ((currentValue & expected) != 0);
    }
}
