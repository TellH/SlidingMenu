package com.example.tlh.slidingmenu;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;

import com.nineoldandroids.view.ViewHelper;

/**
 * Created by tlh on 2016/2/11.
 */
public class SlidingMenu extends HorizontalScrollView {
    private int mMenuPaddingRight;
    private int mScreenWidth;
    private ViewGroup mWrapper;
    private ViewGroup mMenu;
    private ViewGroup mContent;
    private int mMenuWidth;
    private boolean mShow;
    private boolean once;
    private float mLastX;
    private int mTouchSlop;

    private void init(Context context){
        mMenuPaddingRight=Util.dp2px(context,50);
        //获得屏幕宽度
        WindowManager wm = (WindowManager) context.getSystemService(
                Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        mScreenWidth = dm.widthPixels;
        //获得系统认为是滑动的最短距离
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        //去掉ScrollView滚动条
        setHorizontalScrollBarEnabled(false);
    }
    public SlidingMenu(Context context) {
        super(context);
        init(context);
    }

    public SlidingMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SlidingMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (!once){
            //确定子View的尺寸
            mWrapper= (ViewGroup) getChildAt(0);
            mMenu= (ViewGroup) mWrapper.getChildAt(0);
            mContent= (ViewGroup) mWrapper.getChildAt(1);
            mContent.getLayoutParams().width=mScreenWidth;
            mMenuWidth=mMenu.getLayoutParams().width=mScreenWidth-mMenuPaddingRight;
            once=true;
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (changed){
            //初始时Scroller的状态
            this.scrollTo(mMenuWidth, 0);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_UP:
                if (mShow&&Math.abs(mLastX-getX())<mTouchSlop&&ev.getX()>=mMenuWidth){
                    smoothScrollTo(mMenuWidth, 0);
                    mShow = false;
                    return false;
                }
                if (getScrollX()<mMenuWidth/2) {
                    smoothScrollTo(0, 0);
                    mShow = true;
                }
                else {
                    smoothScrollTo(mMenuWidth,0);
                    mShow = false;
                }
                return false;//这里不能替换成break;
        }
        return super.onTouchEvent(ev);//因为只拦截处理了一种MotionEvent，所以需要调用父类的方法处理剩下的事件。
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
/*     //这是滑动过程中menu不动的效果
       //offset从1变化到0，打开菜单是HorizontalScrollView往回滑的过程
        float offset = l*1.0f/mMenuWidth;
        //设置x方向的偏移量
        ViewHelper.setTranslationX(mMenu, mMenuWidth * (offset));*/

        //这是仿qq的侧滑效果
        //offset从1变化到0，打开菜单是HorizontalScrollView往回滑的过程
        float offset = l*1.0f/mMenuWidth;
        //设置x方向的偏移量，处于关闭状态时，menu的偏移量为mMenuWidth的3/4
        ViewHelper.setTranslationX(mMenu,0.75f*mMenuWidth*offset);
    }
}
