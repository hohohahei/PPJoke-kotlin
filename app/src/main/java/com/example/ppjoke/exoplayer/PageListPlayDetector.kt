package com.example.ppjoke.exoplayer

import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView

/**
 * 列表视频自动播放检测逻辑
 */
class PageListPlayDetector {
    //收集一个个的能够进行视频播放的对象
    var mTargets:MutableList<IPlayTarget> = ArrayList()
    lateinit var  mRecyclerView:RecyclerView
    //正在播放的那个
    var playingTarget:IPlayTarget?=null

    fun addTarget(target: IPlayTarget) {
        mTargets.add(target)
    }

    fun removeTarget(target: IPlayTarget){
        mTargets.remove(target)
    }

    constructor(owner: LifecycleOwner,recyclerView: RecyclerView){
        mRecyclerView=recyclerView

        owner.lifecycle.addObserver(object :LifecycleEventObserver{
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                if (event==Lifecycle.Event.ON_DESTROY){
                    playingTarget=null
                    mTargets.clear()
                    mRecyclerView.removeCallbacks(delayAutoPlay)
                    recyclerView.removeOnScrollListener(scrollerListener)
                    owner.lifecycle.removeObserver(this)
                }

            }

        })
        recyclerView.adapter?.registerAdapterDataObserver(mDataObserver)
        recyclerView.addOnScrollListener(scrollerListener)
    }

    var scrollerListener:RecyclerView.OnScrollListener=object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            if (newState==RecyclerView.SCROLL_STATE_IDLE){  //recycleView停止滚动时
                autoPlay()
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            if(dx==0&&dy==0){
                /*时序问题，当执行了AdaptDataObserve#onItemRangeInserted 可能还没有被布局到recycleView上
                所以此时，recycleView.getChildCount()还是等于0的
                等childView被布局到RecycleView上之后，会执行onScrolled（）方法
                并且此时dx，dy都等于0
                */
                postAutoPlay()
            }else{
                //如果有正在播发的，且滑动时被划出了屏幕 则停止他
                if(playingTarget!=null&&playingTarget!!.isPlaying&&!isTargetInBounds(playingTarget!!)){
                    playingTarget!!.inActive()
                }
            }
        }
    }

    private fun postAutoPlay(){
        mRecyclerView.post(delayAutoPlay)
    }

    val delayAutoPlay:Runnable= Runnable {
        kotlin.run { autoPlay() }
    }

    private final val mDataObserver:RecyclerView.AdapterDataObserver=object :RecyclerView.AdapterDataObserver(){
        override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
            postAutoPlay()
        }
    }

    private fun autoPlay(){
        if(mTargets.size<=0||mRecyclerView.childCount<=0){
            return
        }
        if(playingTarget!=null&&playingTarget!!.isPlaying&&isTargetInBounds(playingTarget!!)){
            return
        }
        var activeTarget:IPlayTarget?=null
        for (target in mTargets){
            var inBounds=isTargetInBounds(target)
            if(inBounds){
                activeTarget=target
                break
            }
        }
        if (activeTarget!=null){
            if (playingTarget!=null){
                playingTarget!!.inActive()  //暂停播放
            }
            playingTarget=activeTarget
            activeTarget.onActive()
        }
    }

    /**
     * 检测IPlayTarge所在的ViewGroup是否至少还有一半的大小在屏幕内
     */
    private fun isTargetInBounds(target: IPlayTarget):Boolean{
        var owner: ViewGroup? =target.owner
        ensureRecycleViewLocation()
        if (!owner!!.isShown||!owner.isAttachedToWindow){
            return false
        }
        var location=IntArray(2)
        owner.getLocationOnScreen(location)  //getLocationOnScreen()：播放控件相对于屏幕的左上角为原点的坐标（x,y）
        //location[1] 播放控件顶部和屏幕顶部的距离
        //owner.height 是播放控件的高度
        var center=location[1]+owner.height/2
        //承载视频播放的画面的ViewGroup它需要至少一半的大小，在RecycleView上下范围内
        return center>=rvLocation!!.first&&center<=rvLocation!!.second
    }

    private var rvLocation:Pair<Int,Int>?=null
    //存储了recycleView相对于屏幕顶部的的两个高度（recycleView顶部与屏幕顶部的距离和recycleView底部和屏幕顶部的距离）
    private fun ensureRecycleViewLocation():Pair<Int,Int>{
        if (rvLocation==null){
            val location = IntArray(2)
            mRecyclerView.getLocationOnScreen(location)

            val top=location[1] //相对于屏幕左上角的Y轴数据（也就是高度）location[0]是相对屏幕左上角的X轴数据
            val bottom=top+mRecyclerView.height

            rvLocation= Pair(top,bottom)
        }
        return rvLocation as Pair<Int, Int>
    }
}