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

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;

import com.swiftkaydevelopment.findme.monetization.braintree.BrainTreePurchaseManager;
import com.swiftkaydevelopment.findme.monetization.google.GooglePlayBillingClient;


/**
 * Created by Kevin Haines on 3/9/16.
 * Class Overview:
 */
public class BillingActivity extends AppCompatActivity implements PurchaseFlow {
    private static final String TAG = "BillingActiv";
    private static final String ARG_PRODUCT = "ARG_PRODUCT";
    private static final String ARG_USER = "ARG_USER";

    private BaseProduct mProduct;
    private String mUsername;

    public static Intent createIntent(Context context, BaseProduct product, String username) {
        Intent i = new Intent(context, BillingActivity.class);
        i.putExtra(ARG_PRODUCT, product);
        i.putExtra(ARG_USER, username);
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mProduct = (BaseProduct) getIntent().getExtras().getSerializable(ARG_PRODUCT);
        mUsername = getIntent().getStringExtra(ARG_USER);
        purchase(mProduct.billingType);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        Log.w(TAG, "onActivityResult: request: " + requestCode + " result: " + resultCode);
//
//        if (resultCode == Activity.RESULT_OK) {
//            //Handle result of BrainTree purchase
//            if (requestCode == BrainTreePurchaseManager.REQUEST_CODE_BRAINTREE_PURCHASE) {
//                PaymentMethodNonce paymentMethodNonce = data.getParcelableExtra(
//                        BraintreePaymentActivity.EXTRA_PAYMENT_METHOD_NONCE
//                );
//                if (paymentMethodNonce != null) {
//                    String nonce = paymentMethodNonce.getNonce();
//                    Log.e(TAG, "nonce: " + nonce);
//                    // Send the nonce to your server.
//                    handleBraintreePurchase(nonce);
//                } else {
//                    Log.e(TAG, "paymentMethodNonce is null");
//                }
//            } else if (requestCode == GooglePlayBillingClient.REQUEST_CODE_DIGITAL_PURCHASE) {
//                GooglePlayBillingClient.instance().handleActivityResult(requestCode, resultCode, data);
//            }
//        } else {
//            BrainTreePurchaseManager.instance().clearCurrentPurchase();
//            onPurchaseFailed();
//        }
    }

    /**
     * Handles the result of the Payment Nonce
     * This will execute tasks to send the payment nonce off to
     * the server to continue the payment process.
     *
     * @param nonce String payment nonce
     */
    private void handleBraintreePurchase(String nonce) {
        if (TextUtils.isEmpty(nonce) || TextUtils.isEmpty(mUsername)) {
            Log.e(TAG, "purchase is invalid - nonce is empty");
            return;
        }

        BrainTreePurchaseManager.instance().submitTransactionToServer(mUsername, nonce);
    }

    @Override
    public void purchase(int purchaseType) {
        if (null != mProduct) {
            getPurchaseClient(purchaseType).makePurchase(this, mProduct);
        }
    }

    @Override
    public void onPurchaseSuccessful() {

    }

    @Override
    public void onPurchaseFailed() {
        //TODO: Show error of some kind
        finish();
    }

    @Override
    public void validatePurchase() {

    }

    @Override
    public PurchaseClient getPurchaseClient(int purchaseType) {
        if (purchaseType == PurchaseFlow.DIGITIAL_GOODS) {
            return GooglePlayBillingClient.instance();
        }
        return BrainTreePurchaseManager.instance();
    }
}
