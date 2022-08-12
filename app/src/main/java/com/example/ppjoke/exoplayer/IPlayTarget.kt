package com.example.ppjoke.exoplayer

import android.view.ViewGroup

interface IPlayTarget {
    val owner: ViewGroup?

    //活跃状态 视频可播放
    fun onActive()

    //非活跃状态，暂停它
    fun inActive()

    val isPlaying: Boolean
}