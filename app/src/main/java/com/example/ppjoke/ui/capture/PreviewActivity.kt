package com.example.ppjoke.ui.capture

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.ppjoke.R
import com.example.ppjoke.databinding.ActivityPreviewBinding
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DataSpec
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.FileDataSource
import com.google.android.exoplayer2.util.Util
import com.xtc.base.BaseMvvmActivity
import java.io.File

class PreviewActivity : BaseMvvmActivity<ActivityPreviewBinding,PreviewViewModel>() {
    //TODO 与生命周期绑定
    private var player: ExoPlayer? = null
    companion object{
        const val KEY_PREVIEW_URL = "preview_url"
        const val KEY_PREVIEW_VIDEO = "preview_video"
        const val KEY_PREVIEW_BTNTEXT = "preview_btntext"
        const val REQ_PREVIEW = 1000

        fun startActivityForResult(
            activity: Activity,
            previewUrl: String?,
            isVideo: Boolean,
            btnText: String?
        ) {
            val intent = Intent(activity, PreviewActivity::class.java)
            intent.putExtra(KEY_PREVIEW_URL, previewUrl)
            intent.putExtra(KEY_PREVIEW_VIDEO, isVideo)
            //右上角按钮的文字
            intent.putExtra(KEY_PREVIEW_BTNTEXT, btnText)
            activity.startActivityForResult(intent, REQ_PREVIEW)
            activity.overridePendingTransition(0, 0)
        }
    }
    override fun initView(savedInstanceState: Bundle?) {
        binding.viewModel=mViewModel
        binding.lifecycleOwner=this
        val previewUrl = intent.getStringExtra(KEY_PREVIEW_URL)
        val isVideo = intent.getBooleanExtra(KEY_PREVIEW_VIDEO, false)
        val btnText = intent.getStringExtra(KEY_PREVIEW_BTNTEXT)
        mViewModel!!.btnText.set(btnText)
        mViewModel!!.isVideo.set(isVideo)
        if (isVideo) {
            if (previewUrl != null) {
                previewVideo(previewUrl)
            }
        } else {
            if (previewUrl != null) {
                previewImage(previewUrl)
            }
        }
        binding.actionClose.setOnClickListener {
            finish()
        }
        binding.actionOk.setOnClickListener {
            setResult(RESULT_OK, Intent())
            finish()
        }
    }

    private fun previewImage(previewUrl: String) {
        mViewModel!!.previewUrl.set(previewUrl)
    }

    private fun previewVideo(previewUrl: String) {
        player = ExoPlayer.Builder(this).build()
        binding.playerView.player=player
        createMediaSource(previewUrl).let {
            player!!.addMediaSource(it)
        }

        player!!.prepare()
        player!!.playWhenReady = true
    }

    private fun createMediaSource(previewUrl: String): ProgressiveMediaSource {
        var uri: Uri? = null
        val file = File(previewUrl)
        if (file.exists()) {
            val dataSpec = DataSpec(Uri.fromFile(file))
            val fileDataSource = FileDataSource()
            try {
                fileDataSource.open(dataSpec)
                uri = fileDataSource.uri
            } catch (e: FileDataSource.FileDataSourceException) {
                e.printStackTrace()
            }
        } else {
            uri = Uri.parse(previewUrl)
        }
        val factory: ProgressiveMediaSource.Factory =  ProgressiveMediaSource.Factory(
            DefaultDataSourceFactory(
                this, Util.getUserAgent(
                    this,
                    packageName
                )
            )
        )
        val mediaItem=MediaItem.fromUri(uri!!)
        return factory.createMediaSource(mediaItem)
    }

    override fun getViewBinding(): ActivityPreviewBinding {
        return DataBindingUtil.setContentView<ActivityPreviewBinding>(this,R.layout.activity_preview)
    }

    override fun getViewModel(): PreviewViewModel? {
       return ViewModelProvider(this).get(PreviewViewModel::class.java)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onPause() {
        super.onPause()
        player?.playWhenReady = false
    }

    override fun onResume() {
        super.onResume()
        player?.playWhenReady = true
    }

    override fun onDestroy() {
        super.onDestroy()
        if (player != null) {
            player!!.playWhenReady = false
            player!!.stop(true)
            player!!.release()
        }
    }

}