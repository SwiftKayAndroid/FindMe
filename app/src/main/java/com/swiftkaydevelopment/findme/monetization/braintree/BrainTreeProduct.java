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

package com.swiftkaydevelopment.findme.monetization.braintree;

import com.brightappliance.viper.client.data.ProductGroup;
import com.sndr.android.beta.monetization.BaseProduct;
import com.sndr.android.beta.monetization.PurchaseFlow;

/**
 * Created by Kevin Haines on 3/9/16.
 * Class Overview:
 */
public class BrainTreeProduct extends BaseProduct {
    public ProductGroup.Product mProduct;
    public ProductGroup mProductGroup;

    public BrainTreeProduct(ProductGroup.Product mProduct) {
        this.mProduct = mProduct;
        billingType = PurchaseFlow.INDEPENDANT_PRODUCT;
    }

    public BrainTreeProduct(ProductGroup mProductGroup) {
        this.mProductGroup = mProductGroup;
        billingType = PurchaseFlow.INDEPENDANT_PRODUCT;
    }
}
