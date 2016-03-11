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

import android.annotation.TargetApi;
import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.text.NumberFormat;

/**
 * Created by Kevin Haines on 2/12/16.
 * Class Overview:
 */
public class CurrencyEditText extends EditText {
    private static final String TAG = "$EditText";

    private static final String DEFAULT_PRICE = "0.00";
    private static final String IME_DONE_LABEL = "Done";

    private boolean mShowCurrencyCode = true;
    private boolean mShowCurrencySign = true;

    private OnFocusChangeListener currencyFocusChangeListener = new OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            //TODO: this is a kinda hackish way of forcing the cursor to the end of the edit text
            //todo: setSelection doesn't seem to work however.
            if (hasFocus) {
                setText(getText().toString());
                InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (v != null) {
                    inputMethodManager.showSoftInput(v, InputMethodManager.SHOW_FORCED);
                } else {
                    inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                }

                setSelection(getText().toString().length());
            }
        }
    };

    private TextWatcher monetizationTextWater = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            removeTextChangedListener(this);

            String text = s.toString().replaceAll("[^\\d.]", "");
            text = text.replace(".", "");

            if (TextUtils.isEmpty(text)) {
                setText(formatCurrency("0.00"));
            }

            StringBuilder builder = new StringBuilder();

            if (mShowCurrencyCode) {
                builder.append(NumberFormat.getCurrencyInstance().getCurrency().getCurrencyCode());
            }
            if (mShowCurrencySign) {
                builder.append(NumberFormat.getCurrencyInstance().getCurrency().getSymbol());
            }

            if (text.length() > 2) {
                String afterDecimal = text.substring(text.length() -2, text.length());
                String beforeDecimal = text.substring(0, text.length() - 2);

                if (beforeDecimal.length() > 0 && beforeDecimal.charAt(0) == '0') {
                    beforeDecimal = beforeDecimal.substring(1, beforeDecimal.length());
                }

                if (beforeDecimal.length() > 0 && beforeDecimal.charAt(0) == '0') {
                    beforeDecimal = beforeDecimal.substring(1, beforeDecimal.length());
                }

                if (TextUtils.isEmpty(beforeDecimal)) {
                    beforeDecimal = "0";
                }

                builder.append(beforeDecimal);
                builder.append(".");
                builder.append(afterDecimal);
                setText(builder.toString());
            }

            setSelection(getText().toString().length());
            addTextChangedListener(this);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    public CurrencyEditText(Context context) {
        super(context);
        init();
    }

    public CurrencyEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CurrencyEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(21)
    public CurrencyEditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    /**
     * Initializes this CurrencyEditText instance
     *
     */
    private void init() {
        this.setInputType(InputType.TYPE_CLASS_NUMBER);
        this.addTextChangedListener(monetizationTextWater);
        this.setOnFocusChangeListener(currencyFocusChangeListener);
        this.setImeActionLabel(IME_DONE_LABEL, EditorInfo.IME_ACTION_DONE);
        this.setText(DEFAULT_PRICE);
    }

    /**
     * Sets whether we want to show the currency code or not
     * IE: USD
     *     EU
     *
     * @param showCurrencyCode true to show currency code
     */
    public void setShowCurrencyCode(boolean showCurrencyCode) {
        mShowCurrencyCode = showCurrencyCode;
    }

    /**
     * Sets whether we want to show the currency sign
     *
     * @param showCurrencySign true to show currency sign
     */
    private void setShowCurrencySign(boolean showCurrencySign) {
        mShowCurrencySign = showCurrencySign;
    }

    /**
     * Gets the currency formatted String based on a string value of a double
     *
     * @param stringToConvert String to convert into currency formatted string
     * @return Currency formatted string
     */
    public String formatCurrency(String stringToConvert) {
        stringToConvert = stringToConvert.replaceAll("[^\\d.]", "");
        try {
            double parsed = Double.parseDouble(stringToConvert);

            String formatted = NumberFormat.getCurrencyInstance().format((parsed));

            if (!mShowCurrencySign) {
                formatted = formatted.replaceAll("[^\\d.]", "");
            }

            if (mShowCurrencyCode) {
                formatted = NumberFormat.getCurrencyInstance().getCurrency().getCurrencyCode() + formatted;
            }

            return formatted;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return formatCurrency("0.00");
        }
    }

    /**
     * Sets the price
     *
     * @param price double value of price
     */
    public void setPrice(double price) {
        setText(formatCurrency(String.valueOf(price)));
    }

    /**
     * Gets a clean String value of the price without Currency codes or
     * Currency symbols
     *
     * @return Clean money String
     */
    public String getCleanPriceString() {
        return getText().toString().replaceAll("[^\\d.]", "");
    }

    /**
     * Gets the double value of the price entered.
     *
     * @return double value of price entered.
     */
    public double getPriceDouble() {
        try {
            return Double.parseDouble(getText().toString().replaceAll("[^\\d.]", ""));
        } catch (NumberFormatException e) {
            e.printStackTrace();
            Log.e(TAG, "invalid double");
            return 0.00D;
        }
    }
}
