package com.example.tlh.slidingmenu;

import android.content.Context;
import android.util.TypedValue;

/**
 * Created by tlh on 2016/2/11.
 */
public class Util {
    public static int dp2px (Context context,int dp){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    public static int sp2px (Context context,int sp){
        return (int) TypedValue.applyDimension (TypedValue.COMPLEX_UNIT_SP,sp ,context.getResources().getDisplayMetrics());
    }

}
