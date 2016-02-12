package com.example.tlh.slidingmenu;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
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
    private int mPointId;
    private VelocityTracker mVelocityTracker;
    //一秒钟对应的像素点个数
    private int mFling;

    private void init(Context context){
        mMenuPaddingRight=Util.dp2px(context,50);
        //获得屏幕宽度
        WindowManager wm = (WindowManager) context.getSystemService(
                Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        mScreenWidth = dm.widthPixels;
        //获得系统定义常量
        ViewConfiguration viewConfiguration = ViewConfiguration.get(getContext());
        mTouchSlop = viewConfiguration.getScaledTouchSlop();
        mFling = viewConfiguration.getScaledMinimumFlingVelocity();
        //去掉ScrollView滚动条和over scroll出现的边缘月牙
        setHorizontalScrollBarEnabled(false);
        setHorizontalFadingEdgeEnabled(false);
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

        Log.d("TAG", "onMeasure() called with: " + "widthMeasureSpec = [" + widthMeasureSpec + "], heightMeasureSpec = [" + heightMeasureSpec + "]");
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
        initVelocityTracker(ev);
        switch (ev.getAction()){
            case MotionEvent.ACTION_UP:
                //如果是Fling操作则触发开关
                if (computeVelocity()){
                    toggle();
                    return false;
                }
                //如果在菜单打开时点击了内容区域，自动关闭菜单
                if (mShow&&Math.abs(mLastX-ev.getX())<mTouchSlop&&ev.getX()>=mMenuWidth){
                    toggle();
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
            case MotionEvent.ACTION_CANCEL:
                mLastX=ev.getX();
                release();
                break;
            case MotionEvent.ACTION_DOWN:
                mLastX=ev.getX();
                //得到第一个落下的手指的id
                mPointId=ev.getPointerId(0);
                break;
        }
        return super.onTouchEvent(ev);//因为只拦截处理了一种MotionEvent，所以需要调用父类的方法处理剩下的事件。
    }

    private void toggle() {
        if (mShow){
            smoothScrollTo(mMenuWidth,0);
            mShow = false;
        }else {
            smoothScrollTo(0, 0);
            mShow = true;
        }
    }

    private void initVelocityTracker(MotionEvent ev) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();//获得VelocityTracker类实例
        }
        //将事件加入到VelocityTracker类实例中
        mVelocityTracker.addMovement(ev);
    }

    private boolean computeVelocity() {

        //判断当ev事件是MotionEvent.ACTION_UP时：计算速率
        final VelocityTracker velocityTracker = mVelocityTracker;
        // 1000 provides pixels per second
        velocityTracker.computeCurrentVelocity(1000,200); //设置units的值为1000，意思为一秒时间内运动了多少个像素
//        Log.i("test","velocityTraker"+velocityTracker.getXVelocity());
        Log.d("TAG","velocityTracker.getXVelocity():"+velocityTracker.getXVelocity()+"mFling:"+mFling);
        if (Math.abs(velocityTracker.getXVelocity(mPointId))>mFling)
            return true;
        else return false;
    }

    private void release() {
        if(null != mVelocityTracker) {
            mVelocityTracker.clear();
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }
    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
/*     //这是滑动过程中menu不动的效果
       //offset从1变化到0，打开菜单是HorizontalScrollView往回滑的过程
        float offset = l*1.0f/mMenuWidth;
        //设置x方向的偏移量
        ViewHelper.setTranslationX(mMenu, mMenuWidth * (offset));
*/
        //这是仿qq的侧滑效果
        //offset从1变化到0，打开菜单是HorizontalScrollView往回滑的过程
        float offset = l*1.0f/mMenuWidth;
        //设置x方向的偏移量，处于关闭状态时，menu的偏移量为mMenuWidth的3/4
        ViewHelper.setTranslationX(mMenu,0.75f*mMenuWidth*offset);
    }
}
