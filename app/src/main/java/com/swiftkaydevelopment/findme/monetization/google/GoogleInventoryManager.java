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

package com.swiftkaydevelopment.findme.monetization.google;

import android.os.RemoteException;

import org.json.JSONException;

import java.util.List;

/**
 * Created by Kevin Haines on 3/9/16.
 * Class Overview:
 */
public class GoogleInventoryManager {

    private static final String TAG = "gglinvtorymanger";
    public static final String ITEM_TYPE_INAPP = "inapp";
    public static final String ITEM_TYPE_SUBS = "subs";
    public static final int BILLING_RESPONSE_RESULT_OK = 0;

    private static GoogleInventoryManager sInstance = null;

    private GoogleInventoryManager(){}

    public static GoogleInventoryManager instance() {
        synchronized (GoogleInventoryManager.class) {
            if (sInstance == null) {
                sInstance = new GoogleInventoryManager();
            }
        }
        return sInstance;
    }

    public void queryInventory(){}

    public GoogleInventory queryInventory(boolean querySkuDetails, List<String> moreItemSkus,
                                          List<String> moreSubsSkus) {
        try {
            GoogleInventory inv = new GoogleInventory();
            queryPurchases(inv, ITEM_TYPE_INAPP);

            if (querySkuDetails) {
                querySkuDetails(ITEM_TYPE_INAPP, inv, moreItemSkus);
            }

            queryPurchases(inv, ITEM_TYPE_SUBS);

            if (querySkuDetails) {
                querySkuDetails(ITEM_TYPE_SUBS, inv, moreSubsSkus);
            }


            return inv;
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Consumes a given in-app product. Consuming can only be done on an item
     * that's owned, and as a result of consumption, the user will no longer own it.
     * This method may block or take long to return. Do not call from the UI thread.
     *
     * @param itemInfo The PurchaseInfo that represents the item to consume.
     */
    public void consume(GooglePurchase itemInfo){

        if (!itemInfo.mItemType.equals(ITEM_TYPE_INAPP)) {
            return;
        }

        try {
            String token = itemInfo.getToken();
            String sku = itemInfo.getSku();
            if (token == null || token.equals("")) {
                return;
            }

            int response = mService.consumePurchase(3, mContext.getPackageName(), token);
            if (response == BILLING_RESPONSE_RESULT_OK) {

            } else {
                //todo: handle this
            }
        }
        catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
