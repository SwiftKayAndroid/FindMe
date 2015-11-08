package com.swiftkaytech.findme.views;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

/**
 * Created by Kevin Haines on 10/25/15.
 * Copyright (C) 2015 Kevin Haines
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * <p>
 * Package Name: com.swiftkaydevelopment.testurlconnectionmanager
 * Project: Testurlconnectionmanager
 * Class Name:
 * Class Description:
 */
public class ExpandableLinearLayout extends LinearLayout {
    public static final String TAG = "expandableLinearLayout";

    private boolean isExpanded = false;
    private final int DURATION = 200;

    /** These are used for computing child frames based on their gravity. */
    private final Rect mTmpContainerRect = new Rect();
    private final Rect mTmpChildRect = new Rect();

    public ExpandableLinearLayout(Context context) {
        super(context);
        init();
    }

    public ExpandableLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ExpandableLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    private void init(){
        setVisibility(View.GONE);
    }

    public void expand(){
        setVisibility(View.VISIBLE);
        int childCount = this.getChildCount();
//        float finishsize = 0;
//        int dp = 35;
//
//        Resources resources = this.getContext().getResources();
//        DisplayMetrics metrics = resources.getDisplayMetrics();
//        float px = dp * (metrics.densityDpi / 160f);
//
//        if (childCount > 0) {
//            ListView child = (ListView) this.getChildAt(0);
//            child.getParent().getParent().requestDisallowInterceptTouchEvent(true);
//            int listItemCount = child.getAdapter().getCount();
//            Log.e(TAG, "listitemcount " + Integer.toString(listItemCount));
//            if (listItemCount > 8) {
//                finishsize = 8 * px;
//            } else {
//                finishsize = listItemCount * px;
//            }
//        }


        AnimatorSet set = new AnimatorSet();
//        this.getLayoutParams().height = (int) finishsize;
        set.play(ObjectAnimator.ofFloat(this, View.SCALE_Y, 0, 1));
        setPivotY(0);
        set.setDuration(DURATION);
        set.start();
        isExpanded = true;

    }

    public void retract(){
        AnimatorSet set = new AnimatorSet();
        set.play(ObjectAnimator.ofFloat(this, View.SCALE_Y, 1, 0));
        setPivotY(0);
        set.setDuration(DURATION);
        set.start();
        isExpanded = false;
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

                setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    public boolean isExpanded(){
        return isExpanded;
    }
}
