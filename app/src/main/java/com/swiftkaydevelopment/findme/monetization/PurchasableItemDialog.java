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

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.brightappliance.viper.client.data.AttachmentInfo;
import com.brightappliance.viper.client.data.ProductGroup;
import com.sndr.android.beta.BuildConfig;
import com.sndr.android.beta.R;
import com.sndr.android.beta.events.AttachmentPurchaseSuccessFullEven;
import com.sndr.android.beta.managers.ThumbnailManager;
import com.sndr.android.beta.monetization.google.GooglePlayProduct;
import com.sndr.android.beta.monetization.google.GooglePlayPurchaseConstants;

import org.greenrobot.eventbus.EventBus;

import java.text.NumberFormat;
import java.util.UUID;

/**
 * Created by Kevin Haines on 2/9/16.
 * Class Overview: Dialog to display to user when they try to download an attachment marked as purchasable
 *
 */
public class PurchasableItemDialog extends AppCompatDialogFragment implements View.OnClickListener{
    public static final String TAG = "PurchasableItemDialog";

    private static final String ARG_ATTACH = "ARG_ATTACH";
    private static final String ARG_USER = "ARG_USER";
    private static final String ARG_PRODUCT = "ARG_PRODUCT";

    private TextView mTitle;
    private TextView mDetail;
    private TextView mGooglePay;
//    private TextView mPaypal;
    private ImageView mIvThumbnail;
    private TextView mTvCancel;

    private AttachmentInfo mAttachment;
    private String mUsername;
    private ProductGroup.Product mProduct;

    /**
     * Creates a new instance of this dialog
     * @return new instance of PurchasableItemDialog
     */
    public static PurchasableItemDialog newInstanace(AttachmentInfo attachmentInfo, String username, ProductGroup.Product product) {
        PurchasableItemDialog frag = new PurchasableItemDialog();
        Bundle b = new Bundle();
        b.putSerializable(ARG_ATTACH, attachmentInfo);
        b.putString(ARG_USER, username);
        b.putSerializable(ARG_PRODUCT, product);
        frag.setArguments(b);
        return frag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            mAttachment = (AttachmentInfo) getArguments().getSerializable(ARG_ATTACH);
            mUsername = getArguments().getString(ARG_USER);
            mProduct = (ProductGroup.Product) getArguments().getSerializable(ARG_PRODUCT);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.purchasable_item_dialog, container, false);
        mTitle = (TextView) layout.findViewById(R.id.tvItemName);
        mDetail = (TextView) layout.findViewById(R.id.tvDetail);
        mGooglePay = (TextView) layout.findViewById(R.id.tvGooglePay);
//        mPaypal = (TextView) layout.findViewById(R.id.tvPaypal);
        mIvThumbnail = (ImageView) layout.findViewById(R.id.ivThumbnail);
        mTvCancel = (TextView)layout.findViewById(R.id.tvCancel);

        mTvCancel.setOnClickListener(this);
        mGooglePay.setOnClickListener(this);
        return layout;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState != null) {
            mAttachment = (AttachmentInfo) savedInstanceState.getSerializable(ARG_ATTACH);
            mUsername = savedInstanceState.getString(ARG_USER);
            mProduct = (ProductGroup.Product) savedInstanceState.getSerializable(ARG_PRODUCT);
        }
        if (mAttachment != null) {
            mTitle.setText(mAttachment.filename);
            mDetail.setText(getString(R.string.purchase) + " " + mAttachment.filename);
            Log.e(TAG, "price: " + mProduct.getPrice());
            mGooglePay.setText(NumberFormat.getCurrencyInstance().format(mProduct.getPrice()));
            ThumbnailManager.instance(getActivity()).loadThumbnail(mUsername, mAttachment, mIvThumbnail);
        }
    }

    @Override
    @NonNull
    public AppCompatDialog onCreateDialog(Bundle savedInstanceState) {
        AppCompatDialog dialog = new AppCompatDialog(getActivity());
        dialog.supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(ARG_ATTACH, mAttachment);
        outState.putString(ARG_USER, mUsername);
        outState.putSerializable(ARG_PRODUCT, mProduct);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == mGooglePay.getId()) {
            if (BuildConfig.MOCKBILLING) {
//                BrainTreeProduct product = new BrainTreeProduct(mProduct);
                GooglePlayProduct product = new GooglePlayProduct();
                product.sku = GooglePlayPurchaseConstants.SKU_TEST_STAMP_FIVE_GIG;
                product.itemType = GooglePlayPurchaseConstants.UNMANAGED;
                product.itemPayload = UUID.randomUUID().toString(); //todo: temporary for testing
                getActivity().startActivity(BillingActivity.createIntent(getActivity(), product, mUsername));
            } else {
                EventBus.getDefault().postSticky(new AttachmentPurchaseSuccessFullEven(mAttachment));
            }
            dismiss();
        } else if (v.getId() == mTvCancel.getId()) {
            dismiss();
        }
    }
}