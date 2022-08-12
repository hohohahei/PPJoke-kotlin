package com.example.ppjoke.ui.capture

import androidx.databinding.ObservableField
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.xtc.base.BaseViewModel

class PreviewViewModel:BaseViewModel() {
    val btnText = ObservableField<String>()
    val isVideo = ObservableField<Boolean>()

    //如果是图片文件
    val previewUrl = ObservableField<String>()

    //视频播放器
    val player: ObservableField<StyledPlayerView> = ObservableField<StyledPlayerView>()
}