package com.example.ppjoke.widget

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.*
import android.widget.FrameLayout
import android.widget.ImageView
import com.blankj.utilcode.util.ScreenUtils
import com.example.ppjoke.R
import com.example.ppjoke.exoplayer.PageListPlay
import com.example.ppjoke.exoplayer.PageListPlayManager
import com.example.ppjoke.exoplayer.IPlayTarget
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.ui.PlayerControlView
import com.google.android.exoplayer2.ui.StyledPlayerView


/**
 * 列表视频播放专用
 */
open class ListPlayerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) :
    FrameLayout(context, attrs, defStyleAttr, defStyleRes), IPlayTarget,
    PlayerControlView.VisibilityListener,Player.Listener {
    var bufferView: View
    var cover: PPImageView
    var blur: PPImageView
    protected var playBtn: ImageView
    protected var mCategory: String? = null
    protected var mVideoUrl: String? = null
    override var isPlaying =false
    protected var mWidthPx = 0
    protected var mHeightPx = 0



    override fun onTouchEvent(event: MotionEvent): Boolean {
        //点击该区域时 我们主动让视频控制器显示出来
        val pageListPlay: PageListPlay = PageListPlayManager.get(mCategory!!)
        pageListPlay.controlView?.show()
        return true
    }

    fun bindData(
        category: String?,
        widthPx: Int,
        heightPx: Int,
        coverUrl: String?,
        videoUrl: String?
    ) {
        mCategory = category
        mVideoUrl = videoUrl
        mWidthPx = widthPx
        mHeightPx = heightPx
        cover.setImageUrl(coverUrl)

        //如果该视频的宽度小于高度,则高斯模糊背景图显示出来
        if (widthPx < heightPx) {
            PPImageView.setBlurImageUrl(blur, coverUrl, 10)
            blur.visibility = VISIBLE
        } else {
            blur.visibility = INVISIBLE
        }
        setSize(widthPx, heightPx)
    }

    open fun setSize(widthPx: Int, heightPx: Int) {
        //这里主要是做视频宽大与高,或者高大于宽时  视频的等比缩放
        val maxWidth: Int = ScreenUtils.getScreenWidth()
        var layoutHeight = 0
        val coverWidth: Int
        val coverHeight: Int
        if (widthPx >= heightPx) {
            coverWidth = maxWidth
            coverHeight = (heightPx / (widthPx * 1.0f / maxWidth)).toInt()
            layoutHeight = coverHeight
        } else {
            coverHeight = maxWidth
            layoutHeight = coverHeight
            coverWidth = (widthPx / (heightPx * 1.0f / maxWidth)).toInt()
        }
        val params = layoutParams
        params.width = maxWidth
        params.height = layoutHeight
        layoutParams = params
        val blurParams = blur.layoutParams
        blurParams.width = maxWidth
        blurParams.height = layoutHeight
        blur.layoutParams = blurParams
        val coverParams = cover.layoutParams as LayoutParams
        coverParams.width = coverWidth
        coverParams.height = coverHeight
        coverParams.gravity = Gravity.CENTER
        cover.layoutParams = coverParams
        val playBtnParams = playBtn.layoutParams as LayoutParams
        playBtnParams.gravity = Gravity.CENTER
        playBtn.layoutParams = playBtnParams
    }

    override val owner: ViewGroup
        get() = this

    override fun onActive() {
        //视频播放,或恢复播放

        //通过该View所在页面的mCategory(比如首页列表tab_all,沙发tab的tab_video,标签帖子聚合的tag_feed) 字段，
        //取出管理该页面的Exoplayer播放器，ExoplayerView播放View,控制器对象PageListPlay

        //点击该区域时 我们主动让视频控制器显示出来
        val pageListPlay : PageListPlay = PageListPlayManager.get(mCategory!!)
        val playerView: StyledPlayerView? = pageListPlay.playerView
        val controlView: PlayerControlView? = pageListPlay.controlView
        val exoPlayer: ExoPlayer? = pageListPlay.exoPlayer
        if (playerView == null) {
            return
        }

        //此处我们需要主动调用一次 switchPlayerView，把播放器Exoplayer和展示视频画面的View ExoplayerView相关联
        //为什么呢？因为在列表页点击视频Item跳转到视频详情页的时候，详情页会复用列表页的播放器Exoplayer，然后和新创建的展示视频画面的View ExoplayerView相关联，达到视频无缝续播的效果
        //如果 我们再次返回列表页，则需要再次把播放器和ExoplayerView相关联
        pageListPlay.switchPlayerView(playerView, true)
        val parent = playerView.parent
        if (parent !== this) {
            //把展示视频画面的View添加到ItemView的容器上
            if (parent != null) {
                (parent as ViewGroup).removeView(playerView)
                //还应该暂停掉列表上正在播放的那个
                (parent as ListPlayerView).inActive()
            }
            val coverParams = cover.layoutParams
            this.addView(playerView, 1, coverParams)
        }
        val ctrlParent = controlView?.parent
        if (ctrlParent !== this) {
            //把视频控制器 添加到ItemView的容器上
            if (ctrlParent != null) {
                (ctrlParent as ViewGroup).removeView(controlView)
            }
            val params = LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            params.gravity = Gravity.BOTTOM
            this.addView(controlView, params)
        }

        //如果是同一个视频资源,则不需要从重新创建mediaSource。
        //但需要onPlayerStateChanged 否则不会触发onPlayerStateChanged()
        if (TextUtils.equals(pageListPlay.playUrl, mVideoUrl)) {
            onPlayerStateChanged(true, Player.STATE_READY)
        } else {
            val mediaSource: MediaSource = PageListPlayManager.createMediaSource(mVideoUrl)
            exoPlayer?.prepare(mediaSource)
            exoPlayer?.repeatMode = Player.REPEAT_MODE_ONE
            pageListPlay.playUrl = mVideoUrl
        }
        controlView?.show()
        controlView?.addVisibilityListener(this)
        exoPlayer?.addListener(this)
        exoPlayer?.playWhenReady = true
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        println("到了onDetachedFromWindow")
        isPlaying = false
        bufferView.visibility = GONE
        cover.visibility = VISIBLE
        playBtn.visibility = VISIBLE
        playBtn.setImageResource(R.drawable.icon_video_play)
    }



    override fun inActive() {
        println("到了inActive")
        //暂停视频的播放并让封面图和 开始播放按钮 显示出来
        val pageListPlay: PageListPlay = PageListPlayManager.get(mCategory!!)
        if (pageListPlay.exoPlayer == null || pageListPlay.controlView == null || pageListPlay.exoPlayer == null) return
        pageListPlay.exoPlayer!!.playWhenReady = false
        pageListPlay.controlView!!.removeVisibilityListener(this)
        pageListPlay.exoPlayer!!.removeListener(this)
        cover.visibility = VISIBLE
        playBtn.visibility = VISIBLE
        playBtn.setImageResource(R.drawable.icon_video_play)
    }

    override fun onVisibilityChange(visibility: Int) {
        println("到了onVisibilityChange")
        playBtn.visibility = visibility
        playBtn.setImageResource(if (isPlaying) R.drawable.icon_video_pause else R.drawable.icon_video_play)
    }

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        println("到了onPlayerStateChanged")
        //监听视频播放的状态
        val pageListPlay: PageListPlay = PageListPlayManager.get(mCategory!!)
        val exoPlayer: ExoPlayer? = pageListPlay.exoPlayer
        if (playbackState == Player.STATE_READY && exoPlayer?.bufferedPosition != 0L && playWhenReady) {
            cover.visibility = GONE
            bufferView.visibility = GONE
        } else if (playbackState == Player.STATE_BUFFERING) {
            bufferView.visibility = VISIBLE
        }

        isPlaying =
            playbackState == Player.STATE_READY && exoPlayer?.bufferedPosition != 0L && playWhenReady
        playBtn.setImageResource(if (isPlaying) R.drawable.icon_video_pause else R.drawable.icon_video_play)
    }

    val playController: PlayerControlView?
        get() {
            val listPlay: PageListPlay = PageListPlayManager.get(mCategory!!)
            return listPlay.controlView
        }

    init {
        LayoutInflater.from(context).inflate(R.layout.layout_player_view, this, true)

        //缓冲转圈圈的view
        bufferView = findViewById(R.id.buffer_view)
        //封面view
        cover = findViewById(R.id.cover)
        //高斯模糊背景图,防止出现两边留嘿
        blur = findViewById(R.id.blur_background)
        //播放盒暂停的按钮
        playBtn = findViewById(R.id.play_btn)
        playBtn.setOnClickListener { v: View? ->
            if (isPlaying) {
                inActive()
            } else {
                onActive()
            }
        }
        this.transitionName = "listPlayerView"
    }
}
