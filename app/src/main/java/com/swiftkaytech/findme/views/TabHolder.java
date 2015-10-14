package com.swiftkaytech.findme.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by khaines178 on 9/9/15.
 */
public class TabHolder extends RelativeLayout {

    LinearLayout tabhost;
    List<View> tabs;
    LayoutInflater inflater;
    Context context;
    float logicalDensity;
    int previouslocation = -1;

    View scroller;


    //demensions
    int linlayoutheight = 45;
    int scrollerheight = 5;


    List<pos> positions;
    class pos{
        int x;
        int y;
    }
    int currentSelected = 0;

    public TabHolder(Context context) {
        super(context);
        this.context = context;
        tabs = new ArrayList<View>();
        tabhost = new LinearLayout(context);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);


    }

    public TabHolder(Context context,AttributeSet attrs) {
        super(context,attrs);
        tabs = new ArrayList<View>();
        this.context = context;
        tabhost = new LinearLayout(context);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }
    public TabHolder(Context context,AttributeSet attrs,int defStyle){
        super(context,attrs,defStyle);
        tabs = new ArrayList<View>();
        this.context = context;
        tabhost = new LinearLayout(context);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }
    public void initialize(float logicalDensity){
       this.logicalDensity = logicalDensity;
        //int px = (int) Math.ceil(dp * logicalDensity);

    }

    private void configureLinearLayout(){

        int px = (int) Math.ceil(linlayoutheight * logicalDensity);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,px);
        Log.d(VarHolder.TAG,"Linear Layout Height: " + Integer.toString(px));
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        tabhost.setLayoutParams(params);
        tabhost.setOrientation(LinearLayout.HORIZONTAL);
        Drawable divider = getResources().getDrawable(R.drawable.verticledivider);
        tabhost.setDividerDrawable(divider);
        tabhost.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);

        //tabhost.setBackgroundResource(R.drawable.blackbuttonbackground);
        positions = new ArrayList<pos>();


    }

    private void initializeScroller(){
        scroller = new View(context);
        scroller.setBackgroundColor(getResources().getColor(R.color.holoblue));
        Drawable divider = getResources().getDrawable(R.drawable.blackbuttonbackground);
        scroller.setBackground(divider);
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        int width = display.getWidth();
        int scrollerWidth = width/tabs.size();
        int px = (int) Math.ceil(scrollerheight * logicalDensity);
        LayoutParams params = new LayoutParams(scrollerWidth,px);
        Log.d(VarHolder.TAG,"Scroller Height: " + Integer.toString(px));
        int[] locs = new int[2];
//currentSelected = tabs.size() - 1;
        tabs.get(currentSelected).getLocationOnScreen(locs);
        params.leftMargin = locs[0];
        params.addRule(RelativeLayout.ALIGN_LEFT);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        scroller.setLayoutParams(params);
        //this.removeAllViews();
        this.addView(scroller);
        invalidate();



    }


    public void addTab(View tab){

        tabs.add(tab);

        resetTabHolder();
    }

    public void setSelected(int index){
        currentSelected = index;
        animateToSelected();



    }

    private void animateToSelected(){


        int[] tablocs = new int[2];
        int[] scrolllocs = new int[2];
//currentSelected = tabs.size() - 1;
        tabs.get(currentSelected).getLocationOnScreen(tablocs);
        scroller.getLocationOnScreen(scrolllocs);
        Animation animation;
        if(previouslocation != -1) {
            animation = new TranslateAnimation(scrolllocs[0], tablocs[0], 0, 0);
        }else{
             animation = new TranslateAnimation(previouslocation, 0, 0, 0);
        }
        animation.setDuration(200);
        animation.setFillAfter(true);
        scroller.startAnimation(animation);
        previouslocation = tablocs[0];

        //invalidate();



    }
    public void removeTab(int index){
        if(tabs.size()<index){
            Log.d("TabHolder","Cannot remove tab, tab index not in arraylist");
        }else {
            tabs.remove(index);
        }
        resetTabHolder();
    }

    private void resetTabHolder(){
        configureLinearLayout();
        tabhost.removeAllViews();
        positions.clear();

        for(int i = 0;i<tabs.size();i++){


            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            params.weight = 1;
            params.gravity = Gravity.CENTER;
            View mtab = tabs.get(i);

            mtab.setLayoutParams(params);

            tabhost.addView(mtab);


        }
        this.removeAllViews();
        this.addView(tabhost);


        invalidate();


        initializeScroller();

    }


}
