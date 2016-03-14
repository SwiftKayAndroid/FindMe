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
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.android.vending.billing.IInAppBillingService;
import com.swiftkaydevelopment.findme.monetization.PurchaseClient;

import org.json.JSONException;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by Kevin Haines on 3/9/16.
 * Class Overview:
 */
public class GooglePlayPurchaseTask {
    private static final String TAG = "gglplayprchstask";

    public static final String RESPONSE_BUY_INTENT = "BUY_INTENT";
    public static final String ITEM_TYPE_INAPP = "inapp";
    public static final String ITEM_TYPE_SUBS = "subs";

    // Keys for the responses from InAppBillingService
    public static final String RESPONSE_CODE = "RESPONSE_CODE";
    public static final String RESPONSE_GET_SKU_DETAILS_LIST = "DETAILS_LIST";
    public static final String RESPONSE_INAPP_PURCHASE_DATA = "INAPP_PURCHASE_DATA";
    public static final String RESPONSE_INAPP_SIGNATURE = "INAPP_DATA_SIGNATURE";
    public static final String RESPONSE_INAPP_ITEM_LIST = "INAPP_PURCHASE_ITEM_LIST";
    public static final String RESPONSE_INAPP_PURCHASE_DATA_LIST = "INAPP_PURCHASE_DATA_LIST";
    public static final String RESPONSE_INAPP_SIGNATURE_LIST = "INAPP_DATA_SIGNATURE_LIST";
    public static final String INAPP_CONTINUATION_TOKEN = "INAPP_CONTINUATION_TOKEN";

    // Billing response codes
    public static final int BILLING_RESPONSE_RESULT_OK = 0;

    IInAppBillingService mBillingService;
    ServiceConnection mServiceConn;


    private final GooglePlayProduct mProduct;
    private PurchaseClient mClient;
    private WeakReference<Activity> mActivityRef;

    private GooglePlayPurchaseTask(GooglePlayProduct product) {
        this.mProduct = product;
    }

    public static GooglePlayPurchaseTask init(GooglePlayProduct product) {
        return new GooglePlayPurchaseTask(product);
    }

    public GooglePlayPurchaseTask purchase(PurchaseClient client, Activity activity) {

        this.mClient = client;
        mActivityRef = new WeakReference<>(activity);
        startSetup();
        return this;
    }

    /**
     * Starts the setup process. This will start up the setup process asynchronously.
     * You will be notified through the listener when the setup process is complete.
     * This method is safe to call from a UI thread.
     *
     */
    public void startSetup() {
        if (mActivityRef == null || mActivityRef.get() == null) {
            Log.e(TAG, "Activity ref is null");
            return;
        }

        // Connection to IAB service
        mServiceConn = new ServiceConnection() {
            @Override
            public void onServiceDisconnected(ComponentName name) {
                mBillingService = null;
            }

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.d(TAG, "Billing service connected.");
                mBillingService = IInAppBillingService.Stub.asInterface(service);
                String packageName = mActivityRef.get().getPackageName();
                try {
                    Log.d(TAG, "Checking for in-app billing 3 support.");

                    // check for in-app billing v3 support
                    int response = mBillingService.isBillingSupported(3, packageName, ITEM_TYPE_INAPP);
                    if (response != BILLING_RESPONSE_RESULT_OK) {
                        return;
                    } else {
                        Log.d(TAG, "In-app billing version 3 supported for " + packageName);
                    }

                    // Check for v5 subscriptions support. This is needed for
                    // getBuyIntentToReplaceSku which allows for subscription update
                    response = mBillingService.isBillingSupported(5, packageName, ITEM_TYPE_SUBS);
                    if (response == BILLING_RESPONSE_RESULT_OK) {
                    } else {
                        Log.d(TAG, "Subscription re-signup not available.");
                    }

                    // check for v3 subscriptions support
                    response = mBillingService.isBillingSupported(3, packageName, ITEM_TYPE_SUBS);
                    if (response == BILLING_RESPONSE_RESULT_OK) {

                    } else {

                    }
                }
                catch (RemoteException e) {
                    e.printStackTrace();
                    return;
                }

                launchPurchase();
            }
        };

        Intent serviceIntent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
        serviceIntent.setPackage("com.android.vending");
        List<ResolveInfo> intentServices = mActivityRef.get().getPackageManager().queryIntentServices(serviceIntent, 0);
        if (intentServices != null && !intentServices.isEmpty()) {
            // service available to handle that Intent
            mActivityRef.get().bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);
        }
    }

    /**
     * Dispose of object, releasing resources. It's very important to call this
     * method when you are done with this object. It will release any resources
     * used by it such as service connections. Naturally, once the object is
     * disposed of, it can't be used again.
     *
     */
    public void dispose() {
        if (mServiceConn != null) {
            if (mActivityRef != null && mActivityRef.get() != null) mActivityRef.get().unbindService(mServiceConn);
        }
        mServiceConn = null;
    }

    /**
     * Kicks off the purchase process
     *
     * todo: this is currently returning authentication error until we publish
     *
     */
    private void launchPurchase() {
        if (mActivityRef == null || mActivityRef.get() == null) {
            dispose();
            Log.e(TAG, "activity ref is null");
            return;
        }

        try {
            Log.e(TAG, "launching purchase flow");
            Bundle buyIntentBundle = mBillingService.getBuyIntent(3, mActivityRef.get().getPackageName(), mProduct.sku, ITEM_TYPE_INAPP,
                    mProduct.itemPayload);

            PendingIntent pendingIntent = buyIntentBundle.getParcelable(RESPONSE_BUY_INTENT);
            if (pendingIntent != null) {
                mActivityRef.get().startIntentSenderForResult(pendingIntent.getIntentSender(),
                        GooglePlayBillingClient.REQUEST_CODE_DIGITAL_PURCHASE, new Intent(),
                        Integer.valueOf(0), Integer.valueOf(0),
                        Integer.valueOf(0));
            } else {
                Log.e(TAG, "pending intent is null");
            }
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }  catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * After the purchase goes through we can update the product and then
     * determine if the purchase was successful
     *
     * @param requestCode Request code
     * @param responseCode Response ok or cancelled
     * @param data Intent data from iab
     */
    public void updateAfterPurchase(int requestCode, int responseCode, Intent data) {
        if (requestCode != GooglePlayBillingClient.REQUEST_CODE_DIGITAL_PURCHASE
                || responseCode != Activity.RESULT_OK || data == null) {
            mClient.onPurchaseComplete(mProduct);
            return;
        }

        String purchaseData = data.getStringExtra(RESPONSE_INAPP_PURCHASE_DATA);
        String dataSignature = data.getStringExtra(RESPONSE_INAPP_SIGNATURE);

        if (purchaseData == null || dataSignature == null) {
            mClient.onPurchaseComplete(mProduct);
            return;
        }

        try {
            mProduct.updateAfterPurchase(purchaseData, dataSignature);
            // todo: Verify signature
            mProduct.isPurchased = true; //if verified
            mClient.onPurchaseComplete(mProduct);

        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
