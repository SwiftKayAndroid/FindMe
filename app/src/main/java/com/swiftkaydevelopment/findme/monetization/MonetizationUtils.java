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

package com.swiftkaydevelopment.findme.monetization;

import android.text.TextUtils;

import java.text.NumberFormat;

/**
 * Created by Kevin Haines on 2/10/16.
 * Class Overview:
 */
public class MonetizationUtils {

    /**
     * Gets the currency formatted String based on a string value of a double
     *
     * @param stringToConvert String to convert into currency formatted string
     * @return Currency formatted string
     */
    public static String formatCurrency(String stringToConvert) {
        NumberFormat currencyInstance = NumberFormat.getCurrencyInstance();
        stringToConvert = stringToConvert.replaceAll("[^\\d.]", "");

        String cc = NumberFormat.getCurrencyInstance().getCurrency().getCurrencyCode();

        try {
            return cc + currencyInstance.format(Double.valueOf(stringToConvert));
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return cc + currencyInstance.format(0.00);
        }
    }

    /**
     * Gets a double using a string
     *
     * @param price String containing the price
     * @return double value of the price String
     */
    public static double getDoubleFromPriceString(String price) {
        price = price.replaceAll("[^\\d.]", "");
        if (TextUtils.isEmpty(price)) {
            return 0.00D;
        }
        return Double.valueOf(price);
    }
}
