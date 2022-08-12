package com.example.ppjoke.ui.detail

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.OverScroller
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import androidx.customview.widget.ViewDragHelper
import com.blankj.utilcode.util.ConvertUtils
import com.example.ppjoke.R
import com.example.ppjoke.widget.FullScreenPlayerView

class ViewZoomBehavior : CoordinatorLayout.Behavior<FullScreenPlayerView> {
    private var overScroller: OverScroller? = null
    private var minHeight = 0
    private var scrollingId = 0
    private var viewDragHelper: ViewDragHelper? = null
    private var scrollingView: View? = null
    private var refChild: FullScreenPlayerView? = null
    private var childOriginalHeight = 0
    private var canFullscreen = false
    private var runnable: FlingRunnable? = null

    constructor() {}
    constructor(context: Context,attributeSet: AttributeSet):this(){
        val array=context.obtainStyledAttributes(attributeSet, R.styleable.view_zoom_behavior,
            0,0)
        scrollingId=array.getResourceId(R.styleable.view_zoom_behavior_scrolling_id,0)
        minHeight=array.getDimensionPixelOffset(R.styleable.view_zoom_behavior_min_height,ConvertUtils.dp2px(200f))
        array.recycle()
        overScroller = OverScroller(context)
    }

    override fun onLayoutChild(
        parent: CoordinatorLayout,
        child: FullScreenPlayerView,
        layoutDirection: Int
    ): Boolean {

        //我们需要在这里去获取 scrollingView,
        // 并全局保存下child view.
        // 并计算出初始时 child的底部值，也就是它的高度。我们后续拖拽滑动的时候，它就是最大高度的限制
        // 与此同时 还需要计算出，当前页面是否可以进行视频的全屏展示，即h>w即可。
        if(viewDragHelper==null){
            viewDragHelper=ViewDragHelper.create(parent,1.0f,mCallback)
            scrollingView=parent.findViewById(scrollingId)
            refChild=child
            childOriginalHeight=child.measuredHeight
            canFullscreen=childOriginalHeight>parent.measuredHeight
        }
        return super.onLayoutChild(parent, child, layoutDirection)
    }

    private val mCallback= object : ViewDragHelper.Callback() {
        //告诉ViewDragHelper 什么时候 可以拦截 手指触摸的这个View的手势分发
        override fun tryCaptureView(child: View, pointerId: Int): Boolean {
            return (canFullscreen
                    && refChild!!.bottom >= minHeight && refChild!!.bottom <= childOriginalHeight)
        }

        //告诉ViewDragHelper 在屏幕上滑动多少距离才算是拖拽
        override fun getViewVerticalDragRange(child: View): Int {
            return viewDragHelper!!.touchSlop
        }

        //告诉ViewDragHelper手指拖拽的这个View 本次滑动最终能够移动的距离
        override fun clampViewPositionVertical(child: View, top: Int, dy: Int): Int {
            if(refChild==null||dy==0) return 0

            //dy>0 代表手指从屏幕上放往屏幕下方滑动
            //dy<0 代表手指从屏幕下方 往屏幕上方滑动

            //手指从下往上滑动。dy<0 这意味着refchild的底部 会被向上移动。所以 它的底部的最小值 不能小于minheight
            if(dy<0&& refChild!!.bottom<minHeight||
                //手指从上往下滑动。dy>0 意味着refChild的底部会被向下移动。所以它的底部的最大值 不能超过父容器的高度 也即childOriginalHeight
                (dy>0&& refChild!!.bottom>childOriginalHeight)||
                //手指 从屏幕上方 往下滑动。如果scrollinghview 还没有滑动到列表的最顶部，
                // 也意味围着列表还可以向下滑动，此时咱们应该让列表自行滑动，不做拦截
                (dy>0&&(scrollingView!=null&& scrollingView!!.canScrollVertically(-1)))){
                return 0
            }

            var maxConsumed=0
            if(dy>0){
                //如果本次滑动的dy值 追加上 refchild的bottom 值会超过 父容器的最大高度值
                //此时 咱们应该 计算一下
                if(refChild!!.bottom+dy>childOriginalHeight){
                    maxConsumed=childOriginalHeight- refChild!!.bottom
                }else{
                    maxConsumed=dy
                }
            }else{
                //else 分支里面 dy的值 是负值。
                //如果本次滑动的值  dy 加上refChild的bottom 会小于minHeight。 那么咱们应该计算一下本次能够滑动的最大距离
                if(refChild!!.bottom+dy<minHeight){
                    maxConsumed=minHeight- refChild!!.bottom
                }else{
                    maxConsumed=dy
                }
            }

            var layoutParams= refChild!!.layoutParams
            layoutParams.height=layoutParams.height+maxConsumed
            refChild!!.layoutParams=layoutParams
            if(mViewZoomCallback!=null){
                mViewZoomCallback!!.onDragZoom(layoutParams.height)
            }
            return maxConsumed
        }

        //指的是 我们的手指 从屏幕上 离开的时候 会被调用
        override fun onViewReleased(releasedChild: View, xvel: Float, yvel: Float) {
            super.onViewReleased(releasedChild, xvel, yvel)
            //bugfix:这里应该是refChild.getBottom() < childOriginalHeight
            if (refChild!!.bottom in (minHeight + 1) until childOriginalHeight &&yvel!=0f){
                refChild!!.removeCallbacks(runnable)
                runnable=FlingRunnable(refChild!!)
                runnable?.fling(xvel.toInt(),yvel.toInt())
            }
        }
    }

    override fun onTouchEvent(
        parent: CoordinatorLayout,
        child: FullScreenPlayerView,
        ev: MotionEvent
    ): Boolean {
        if (!canFullscreen || viewDragHelper == null) return super.onTouchEvent(parent, child, ev)
        viewDragHelper!!.processTouchEvent(ev)
        return true
    }

    override fun onInterceptTouchEvent(
        parent: CoordinatorLayout,
        child: FullScreenPlayerView,
        ev: MotionEvent
    ): Boolean {
        return if (!canFullscreen || viewDragHelper == null) super.onInterceptTouchEvent(
            parent,
            child,
            ev
        ) else viewDragHelper!!.shouldInterceptTouchEvent(ev)
    }

    private var mViewZoomCallback: ViewZoomCallback? = null
    fun setViewZoomCallback(callback: ViewZoomCallback?) {
        mViewZoomCallback = callback
    }

    interface ViewZoomCallback {
        fun onDragZoom(height: Int)
    }

    private inner class FlingRunnable(private val mFlingView: View) : Runnable {
        fun fling(xvel: Int, yvel: Int) {
            /**
             * startX:开始的X值，由于我们不需要再水平方向滑动 所以为0
             * startY:开始滑动时Y的起始值，那就是flingview的bottom值
             * xvel:水平方向上的速度，实际上为0的
             * yvel:垂直方向上的速度。即松手时的速度
             * minX:水平方向上 滚动回弹的越界最小值，给0即可
             * maxX:水平方向上 滚动回弹越界的最大值，实际上给0也是一样的
             * minY：垂直方向上 滚动回弹的越界最小值，给0即可
             * maxY:垂直方向上，滚动回弹越界的最大值，实际上给0 也一样
             */
            overScroller!!.fling(
                0,
                mFlingView.bottom,
                xvel,
                yvel,
                0,
                Int.MAX_VALUE,
                0,
                Int.MAX_VALUE
            )
            run()
        }

        override fun run() {
            val params = mFlingView.layoutParams
            val height = params.height
            //判断本次滑动是否以滚动到最终点。
            if (overScroller!!.computeScrollOffset() && height >= minHeight && height <= childOriginalHeight) {
                val newHeight = Math.min(overScroller!!.currY, childOriginalHeight)
                if (newHeight != height) {
                    params.height = newHeight
                    mFlingView.layoutParams = params
                    if (mViewZoomCallback != null) {
                        mViewZoomCallback!!.onDragZoom(newHeight)
                    }
                }
                ViewCompat.postOnAnimation(mFlingView, this)
            } else {
                mFlingView.removeCallbacks(this)
            }
        }
    }
}