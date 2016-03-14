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

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.sndr.android.beta.monetization.PurchaseClient;
import com.sndr.android.beta.monetization.PurchaseFlow;

/**
 * Created by Kevin Haines on 3/9/16.
 * Class Overview:
 */
public class GooglePlayBillingClient implements PurchaseClient<GooglePlayProduct> {
    private static final String TAG = "gglplaybillclient";

    public static final int REQUEST_CODE_DIGITAL_PURCHASE = 5444;

    PurchaseFlow mPurchaseFlow;

    private GooglePlayPurchaseTask mCurrentTask = null;

    private static GooglePlayBillingClient sInstance = null;

    private GooglePlayBillingClient() {};

    public static GooglePlayBillingClient instance() {
        synchronized (GooglePlayBillingClient.class) {
            if (sInstance == null) {
                sInstance = new GooglePlayBillingClient();
            }
        }
        return sInstance;
    }

    @Override
    public void makePurchase(Activity activity, GooglePlayProduct product) {
        if (activity instanceof PurchaseFlow) {
            mPurchaseFlow = (PurchaseFlow) activity;
        }

        if (mCurrentTask == null) {
            mCurrentTask = GooglePlayPurchaseTask.init(product).purchase(this, activity);
        } else {
            Log.w(TAG, "can only process one purchase at a time");
        }
    }

    @Override
    public void onPurchaseComplete(GooglePlayProduct product) {
        clearCurrentPurchase();
        if (product.isPurchased) {
            mPurchaseFlow.onPurchaseSuccessful();
        } else {
            mPurchaseFlow.onPurchaseFailed();
        }
    }

    /**
     * Clears the current task to make available for
     * a new purchase task.
     *
     */
    public void clearCurrentPurchase() {
        mCurrentTask.dispose();
        mCurrentTask = null;
        System.gc(); // for added security we request gc to clear any transaction info still in memory
    }

    /**
     * Updates the product after the purchase occurs.
     *
     * @param requestCode The requestCode as you received it.
     * @param resultCode The resultCode as you received it.
     * @param data The data (Intent) as you received it.
     */
    public void handleActivityResult(int requestCode, int resultCode, Intent data) {
        mCurrentTask.updateAfterPurchase(requestCode, resultCode, data);
    }
}
