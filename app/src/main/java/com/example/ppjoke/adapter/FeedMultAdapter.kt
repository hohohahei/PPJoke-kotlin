package com.example.ppjoke.adapter

import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.example.ppjoke.R
import com.example.ppjoke.bean.FeedBean
import com.example.ppjoke.databinding.LayoutFeedTypeImageBinding
import com.example.ppjoke.databinding.LayoutFeedTypeVideoBinding
import com.example.ppjoke.exoplayer.PageListPlayDetector
import com.example.ppjoke.utils.MMKVUtils


class FeedMultAdapter(
    data: MutableList<FeedBean>,
    private val feedCategory: String?,
    var playDetector: PageListPlayDetector?=null
) :
    BaseMultiItemQuickAdapter<FeedBean, BaseViewHolder>(data) {

    override fun convert(holder: BaseViewHolder, item: FeedBean) {
        when (holder.itemViewType) {
            TEXT,
            TEXT_IMG -> {
                val bindingImage = DataBindingUtil.bind<LayoutFeedTypeImageBinding>(holder.itemView)
                bindingImage!!.feed = item
                bindingImage.feedImage.bindData(
                    item.width ?: 0,
                    item.height ?: 0,
                    16,
                    item.cover
                )
                bindingImage.feedAuthor.feedDelete.visibility=if(item.author?.userId==MMKVUtils.getInstance().getUserId())View.VISIBLE else View.GONE
            }
            VIDEO -> {
                val bindVideo = DataBindingUtil.bind<LayoutFeedTypeVideoBinding>(holder.itemView)
                bindVideo!!.feed = item
                bindVideo.listPlayerView.bindData(
                    feedCategory,
                    item.width ?: 720,
                    item.height ?: 1280,
                    item.cover,
                    item.url
                )
                bindVideo.feedAuthor.feedDelete.visibility=if(item.author?.userId==MMKVUtils.getInstance().getUserId())View.VISIBLE else View.GONE
            }
        }
    }

    override fun onViewAttachedToWindow(holder: BaseViewHolder) {
        if(holder.itemViewType== VIDEO) {
            val bindVideo = DataBindingUtil.bind<LayoutFeedTypeVideoBinding>(holder.itemView)
            if (bindVideo != null) {
                playDetector?.addTarget(bindVideo.listPlayerView)
            }
        }
    }

    override fun onViewDetachedFromWindow(holder: BaseViewHolder) {
        if(holder.itemViewType== VIDEO) {
            val bindVideo = DataBindingUtil.bind<LayoutFeedTypeVideoBinding>(holder.itemView)
            if (bindVideo != null) {
                playDetector?.removeTarget(bindVideo.listPlayerView)
            }
        }
    }


    override fun setNewInstance(list: MutableList<FeedBean>?) {
        super.setNewInstance(list)
    }

    companion object {
        const val TEXT_IMG = 1 //图片-文字
        const val VIDEO = 2 //视频
        const val TEXT = 3 //文字
    }

    init {
        addItemType(TEXT, R.layout.layout_feed_type_image)
        addItemType(VIDEO, R.layout.layout_feed_type_video)
        addItemType(TEXT_IMG, R.layout.layout_feed_type_image)
    }
}