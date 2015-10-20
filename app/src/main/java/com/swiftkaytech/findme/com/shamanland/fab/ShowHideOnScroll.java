package com.swiftkaytech.findme.com.shamanland.fab;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.swiftkaytech.findme.fragment.NewsFeedFrag;
import com.swiftkaytech.findme.R;

/**
 * Implementation of {@link View.OnTouchListener} which shows/hides specified view
 * when {@link #onScrollUp()} and {@link #onScrollDown()} events recognized.
 */
public class ShowHideOnScroll extends ScrollDetector implements Animation.AnimationListener {
    private final View mView;
    private final int mShow;
    private final int mHide;

    int changer = 1;

    /**
     * Construct object with defaults animations
     *
     * @param view null not allowed
     */
    public ShowHideOnScroll(View view) {
        this(view, R.animator.floating_action_button_show, R.animator.floating_action_button_hide);
    }

    /**
     * Construct object with custom animations.
     *
     * @param view     null not allowed
     * @param animShow anim resource id
     * @param animHide anim resource id
     */
    public ShowHideOnScroll(View view, int animShow, int animHide) {
        super(view.getContext());
        mView = view;
        mShow = animShow;
        mHide = animHide;
    }

    @Override
    public void onScrollDown() {
        if (mView.getVisibility() != View.VISIBLE) {
            //mView.setVisibility(View.VISIBLE);

        }if(changer == 0){
            changer = 1;
            animate(mShow);
        }

    }

    @Override
    public void onScrollUp() {
        if (mView.getVisibility() == View.VISIBLE) {
            //mView.setVisibility(View.GONE);

        }
        if(changer ==1){
            changer = 0;
            animate(mHide);
        }

    }

    private void animate(int anim) {
        if (anim != 0) {
            Animation a = AnimationUtils.loadAnimation(mView.getContext(), anim);
            a.setAnimationListener(this);

            a.setFillAfter(true);
            mView.startAnimation(a);
            setIgnore(true);
        }
    }

    @Override
    public void onAnimationStart(Animation animation) {
        // empty
        if(NewsFeedFrag.fabstatus.getVisibility() == View.VISIBLE){
            NewsFeedFrag.fabstatus.setVisibility(View.GONE);
            NewsFeedFrag.fabphoto.setVisibility(View.GONE);
            NewsFeedFrag.fabvisible = false;
        }
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        setIgnore(false);
    }

    @Override
    public void onAnimationRepeat(Animation animation) {
        // empty
    }
}
