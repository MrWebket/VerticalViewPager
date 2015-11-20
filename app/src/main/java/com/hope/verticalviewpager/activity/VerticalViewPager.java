package com.hope.verticalviewpager.activity;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.Scroller;

import com.hope.verticalviewpager.R;

/**
 *
 * Created by Hope on 15/11/20.
 */
public class VerticalViewPager extends FrameLayout {

    private static final String TAG = VerticalViewPager.class.getSimpleName();

    private static final int[] COLORS = new int[] { Color.BLACK, Color.BLUE, Color.RED, Color.YELLOW };

    /** 往下拖拽 */
    private static final int TOUCH_DRAG_DOWN = 1;

    private int mTouchState;

    private static final int CHILD_COUNT = 4;

    private int childPaddingLeft;
    private int childPaddingRight;
    private int childPaddingBottom;
    private int childPaddingTop;

    private int childOffset;

    private Scroller mScroller;

    public VerticalViewPager(Context context) {
        super(context);

        init();
    }

    public VerticalViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public VerticalViewPager(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    private void init() {
        for (int i = 0; i < CHILD_COUNT; i++) {
            View child = LayoutInflater.from(getContext()).inflate(R.layout.item, null);
            child.setBackgroundColor(COLORS[i]);
            addView(child, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        }

        childPaddingLeft = DisplayUtil.dip2px(getContext(), 30);
        childPaddingRight = DisplayUtil.dip2px(getContext(), 30);
        childPaddingBottom = DisplayUtil.dip2px(getContext(), 50);
        childPaddingTop = DisplayUtil.dip2px(getContext(), 100);

        childOffset = DisplayUtil.dip2px(getContext(), 10);

        setBackgroundColor(Color.WHITE);

        mScroller = new Scroller(getContext(), new DecelerateInterpolator());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int totalChildCount = getChildCount();

        int mTotalChildOffset = 0;

        for (int i = 0; i < totalChildCount; i++) {
            View childView = getChildAt(i);

            int width = widthMeasureSpec - childPaddingLeft - childPaddingRight + mTotalChildOffset;
            int height = heightMeasureSpec - childPaddingTop - childPaddingBottom + mTotalChildOffset;

            childView.measure(width,
                    height);

            if(i == totalChildCount - 2) {
                getChildAt(i + 1).measure(width, height);
                break;
            }
            mTotalChildOffset += childOffset;
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        int totalChildCount = getChildCount();

        int mTotalChildOffset = 0;

        for (int i = 0 ; i < totalChildCount; i++) {
            View childView = getChildAt(i);
            int l = (getWidth() - childView.getMeasuredWidth()) / 2;
            int t = childPaddingTop - mTotalChildOffset;

            childView.layout(left + l, t,
                    left + l + childView.getMeasuredWidth(), t + childView.getMeasuredHeight());

            if(i == totalChildCount - 2) { //把最后一个藏起来
                View nextView = getChildAt(i + 1);
//                nextView.layout(childView.getLeft(), -nextView.getMeasuredHeight(), childView.getLeft() + childView.getWidth(), 0);
                Log.d(TAG, "onLayout nextView.getMeasuredHeight() = " + nextView.getMeasuredHeight());

                MarginLayoutParams params = (MarginLayoutParams) nextView.getLayoutParams();
                params.setMargins(childView.getLeft(), -nextView.getMeasuredHeight(), childView.getRight(), 0);

                break;
            }

            mTotalChildOffset += childOffset * 2;
        }
    }

    private View getMoveView() {
        return getChildAt(getChildCount() - 1);
    }

    private float mInterceptDownY;
    private float mInterceptMoveY;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mInterceptDownY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                mInterceptMoveY = ev.getY();

                float distanceY = mInterceptMoveY - mInterceptDownY;

                if(Math.abs(distanceY) < 30) {
                    return false;
                }
                mTouchDownY = mInterceptDownY;

                return true;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }


    private void move(float distanceY) {

        Log.d(TAG, "onTouchEvent distanceY = " + distanceY);
        Log.d(TAG, "onTouchEvent getMoveView().getWidth() = " + getMoveView().getWidth());
        Log.d(TAG, "onTouchEvent getMoveView().getLeft() = " + getMoveView().getLeft());
        Log.d(TAG, "onTouchEvent getMoveView().getHeight() = " + getMoveView().getHeight());

        int top =  -getMoveView().getMeasuredHeight() + (int) distanceY;

//        getMoveView().layout(getMoveView().getLeft(), top,
//                getMoveView().getLeft() + getMoveView().getWidth(), (int) distanceY + getMoveView().getHeight());

        MarginLayoutParams params = (MarginLayoutParams) getMoveView().getLayoutParams();
        params.topMargin = top;

        getMoveView().setLayoutParams(params);
    }



    private float mTouchDownY;
    private float mTouchMoveY;

    private float mLastDistanceY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mTouchDownY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                mTouchMoveY = event.getY();
                mLastDistanceY = mTouchMoveY - mTouchDownY;

                mTouchState = TOUCH_DRAG_DOWN;
                move(mLastDistanceY);
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if(mLastDistanceY != 0) {
                    switch (mTouchState) {
                        case TOUCH_DRAG_DOWN:
                            mScroller.startScroll(0, (int) mLastDistanceY, 0, getMoveView().getMeasuredHeight());
                            break;
                    }
                }
                break;
        }
        return true;
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mScroller.computeScrollOffset()) {
            switch (mTouchState) {
                case TOUCH_DRAG_DOWN:
                    Log.d(TAG, "computeScroll value = " + (mLastDistanceY + mScroller.getCurrY()));
                    move(mLastDistanceY + mScroller.getCurrY());
                    break;
            }

        }
        postInvalidate();
    }
}
