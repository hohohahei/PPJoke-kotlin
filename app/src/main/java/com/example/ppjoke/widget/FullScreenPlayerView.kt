package com.example.ppjoke.widget

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.ScreenUtils
import com.example.ppjoke.R
import com.example.ppjoke.exoplayer.PageListPlay
import com.example.ppjoke.exoplayer.PageListPlayManager
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.ui.PlayerControlView
import com.google.android.exoplayer2.ui.StyledPlayerView

/**
 * 视频详情页全屏播放专用
 */
class FullScreenPlayerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) :
    ListPlayerView(context, attrs, defStyleAttr, defStyleRes) {
    private val exoPlayerView: StyledPlayerView?
     override fun setSize(widthPx: Int, heightPx: Int) {
        if (widthPx >= heightPx) {
            super.setSize(widthPx, heightPx)
            return
        }
        val maxWidth: Int = ScreenUtils.getScreenWidth()
        val maxHeight: Int = ScreenUtils.getScreenHeight()
        val params: ViewGroup.LayoutParams = getLayoutParams()
        params.width = maxWidth
        params.height = maxHeight
         layoutParams = params
        val coverLayoutParams: FrameLayout.LayoutParams = cover.getLayoutParams() as LayoutParams
        coverLayoutParams.width = (widthPx / (heightPx * 1.0f / maxHeight)).toInt()
        coverLayoutParams.height = maxHeight
        coverLayoutParams.gravity = Gravity.CENTER
         cover.layoutParams = coverLayoutParams
    }

    override fun setLayoutParams(params: ViewGroup.LayoutParams) {
        if (mHeightPx > mWidthPx) {
            val layoutWidth = params.width
            val layoutheight = params.height
            val coverLayoutParams: ViewGroup.LayoutParams = cover.getLayoutParams()
            coverLayoutParams.width = ((mWidthPx / (mHeightPx * 1.0f / layoutheight)).toInt())
            coverLayoutParams.height = layoutheight
            cover.layoutParams = coverLayoutParams
            if (exoPlayerView != null) {
                val layoutParams = exoPlayerView.layoutParams
                if (layoutParams != null && layoutParams.width > 0 && layoutParams.height > 0) {
                    val scalex = coverLayoutParams.width * 1.0f / layoutParams.width
                    val scaley = coverLayoutParams.height * 1.0f / layoutParams.height
                    exoPlayerView.scaleX = scalex
                    exoPlayerView.scaleY = scaley
                }
            }
        }
        super.setLayoutParams(params)
    }

    override fun onActive() {
        val pageListPlay: PageListPlay = PageListPlayManager.get(mCategory!!)
        val playerView = exoPlayerView //pageListPlay.playerView;
        val controlView: PlayerControlView? = pageListPlay.controlView
        val exoPlayer: ExoPlayer? = pageListPlay.exoPlayer
        if (playerView == null) {
            return
        }

        //主动关联播放器与exoplayerview
        pageListPlay.switchPlayerView(playerView, true)
        val parent = playerView.parent
        if (parent !== this) {
            if (parent != null) {
                (parent as ViewGroup).removeView(playerView)
            }
            val coverParams: ViewGroup.LayoutParams = cover.getLayoutParams()
            this.addView(playerView, 1, coverParams)
        }
        val ctrlParent = controlView?.parent
        if (ctrlParent !== this) {
            if (ctrlParent != null) {
                (ctrlParent as ViewGroup).removeView(controlView)
            }
            val params = FrameLayout.LayoutParams(
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

    override fun inActive() {
        super.inActive()
        val pageListPlay: PageListPlay = PageListPlayManager.get(mCategory!!)
        //主动切断exoplayer与视频播放器的联系
        if (exoPlayerView != null) {
            pageListPlay.switchPlayerView(exoPlayerView, false)
        }
    }

    init {
        exoPlayerView = LayoutInflater.from(context)
            .inflate(R.layout.layout_exo_player_view, null, false) as StyledPlayerView?
    }
}