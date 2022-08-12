package com.example.ppjoke.exoplayer

import android.app.Application
import android.view.LayoutInflater
import com.example.ppjoke.AppGlobals
import com.example.ppjoke.R
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ui.PlayerControlView
import com.google.android.exoplayer2.ui.StyledPlayerView

class PageListPlay {
    var exoPlayer: ExoPlayer?
    var playerView: StyledPlayerView?
    var controlView: PlayerControlView?
    var playUrl: String? = null
    fun release() {
        if (exoPlayer != null) {
            exoPlayer!!.playWhenReady = false
            exoPlayer!!.stop(true)
            exoPlayer!!.release()
            exoPlayer = null
        }
        if (playerView != null) {
            playerView!!.player = null
            playerView = null
        }
        if (controlView != null) {
            controlView!!.player = null
            controlView = null
        }
    }

    /**
     * 切换与播放器exoplayer 绑定的exoplayerView。用于页面切换视频无缝续播的场景
     *
     * @param newPlayerView
     * @param attach
     */
    fun switchPlayerView(newPlayerView: StyledPlayerView, attach: Boolean) {
        playerView!!.player = if (attach) null else exoPlayer
        newPlayerView.player = if (attach) exoPlayer else null
    }

    init {
        val application: Application = AppGlobals.getApplication()
        //创建exoplayer播放器实例
        exoPlayer = ExoPlayer.Builder(application).build()


        //加载咱们布局层级优化之后的能够展示视频画面的View
        playerView = LayoutInflater.from(application)
            .inflate(R.layout.layout_exo_player_view, null, false) as StyledPlayerView?

        //加载咱们布局层级优化之后的视频播放控制器
        controlView = LayoutInflater.from(application)
            .inflate(R.layout.layout_exo_player_controller_view, null, false) as PlayerControlView?
        //别忘记 把播放器实例 和 playerView，controlView相关联
        //如此视频画面才能正常显示,播放进度条才能自动更新
        playerView!!.player = exoPlayer
        controlView!!.player = exoPlayer
    }
}