package com.example.ppjoke.adapter

import android.content.Intent
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.example.ppjoke.R
import com.example.ppjoke.bean.CommentBean
import com.example.ppjoke.databinding.LayoutFeedCommentListItemBinding
import com.example.ppjoke.ui.profile.ProfileActivity

class FeedCommentAdapter(data: MutableList<CommentBean>) :
    BaseQuickAdapter<CommentBean, BaseDataBindingHolder<LayoutFeedCommentListItemBinding>>(
        R.layout.layout_feed_comment_list_item,
        data
    ), LoadMoreModule {

//    override fun convert(
//        holder: BaseDataBindingHolder<LayoutFeedCommentListItemBinding>,
//        item: CommentBean,
//        payloads: List<Any>
//    ) {
//        super.convert(holder, item, payloads)
//        if(payloads.isNotEmpty()){
//            holder.dataBinding!!.comment=item
//        }
//    }
    override fun convert(
        holder: BaseDataBindingHolder<LayoutFeedCommentListItemBinding>,
        item: CommentBean
    ) {
        holder.dataBinding!!.comment=item
        holder.dataBinding!!.commentCover.bindData(item.width?:0,item.height?:0,0,item.imageUrl)
    }


}