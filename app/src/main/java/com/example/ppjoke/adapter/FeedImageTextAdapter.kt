package com.example.ppjoke.adapter

import android.content.Context
import android.content.Intent
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.example.ppjoke.R
import com.example.ppjoke.bean.FeedBean
import com.example.ppjoke.databinding.LayoutFeedTypeImageBinding
import com.example.ppjoke.databinding.LayoutFeedTypeVideoBinding
import com.example.ppjoke.ui.profile.ProfileActivity

class FeedImageTextAdapter(data: MutableList<FeedBean>, private val feedCategory: String) :
    BaseQuickAdapter<FeedBean, BaseDataBindingHolder<LayoutFeedTypeImageBinding>>(
        R.layout.layout_feed_type_image,
        data
    ), LoadMoreModule {
        override fun convert(
        holder: BaseDataBindingHolder<LayoutFeedTypeImageBinding>,
        item: FeedBean
    ) {
        holder.dataBinding!!.feed = item

            holder.dataBinding!!.feedImage.bindData(item.width ?: 0, item.height ?: 0, 16, item.cover)

        holder.dataBinding!!.feedAuthor.avatar.setOnClickListener {
            val intent=Intent(context,ProfileActivity::class.java)
            intent.putExtra("USERID",item.authorId)
            context.startActivity(intent)
        }

    }

    override fun getItemViewType(position: Int): Int {
        return position
    }


    override fun setNewInstance(list: MutableList<FeedBean>?) {
        super.setNewInstance(list)
    }

}