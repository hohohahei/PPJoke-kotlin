package com.example.ppjoke.adapter

import android.content.Intent
import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.example.ppjoke.R
import com.example.ppjoke.bean.CommentBean
import com.example.ppjoke.databinding.LayoutFeedCommentListItemBinding
import com.example.ppjoke.ui.profile.ProfileActivity
import com.example.ppjoke.utils.MMKVUtils

class FeedCommentAdapter(data: MutableList<CommentBean>,private val userId: Long) :
    BaseQuickAdapter<CommentBean, BaseDataBindingHolder<LayoutFeedCommentListItemBinding>>(
        R.layout.layout_feed_comment_list_item,
        data
    ), LoadMoreModule {

    override fun convert(
        holder: BaseDataBindingHolder<LayoutFeedCommentListItemBinding>,
        item: CommentBean
    ) {
        holder.dataBinding!!.comment=item
        holder.dataBinding!!.commentCover.bindData(item.width?:0,item.height?:0,0,item.imageUrl)
        holder.dataBinding!!.labelAuthor.visibility=if (item.author!!.userId==userId) View.VISIBLE else View.GONE
        holder.dataBinding!!.commentDelete.visibility=if (item.author.userId==MMKVUtils.getInstance().getUserId()) View.VISIBLE else View.GONE
    }


}