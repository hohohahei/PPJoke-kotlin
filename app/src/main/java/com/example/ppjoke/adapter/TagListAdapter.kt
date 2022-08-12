package com.example.ppjoke.adapter

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.provider.CalendarContract
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.example.ppjoke.R
import com.example.ppjoke.bean.TagBean
import com.example.ppjoke.databinding.LayoutTagListItemBinding

class TagListAdapter(data:MutableList<TagBean>):BaseQuickAdapter<TagBean,BaseDataBindingHolder<LayoutTagListItemBinding>>(
    R.layout.layout_tag_list_item,
    data
) {
    @SuppressLint("ResourceAsColor")
    override fun convert(
        holder: BaseDataBindingHolder<LayoutTagListItemBinding>,
        item: TagBean,
        payloads: List<Any>
    ) {
        super.convert(holder, item, payloads)
        if(payloads.isNotEmpty()){
            holder.dataBinding!!.tagItem?.hasFollow=item.hasFollow
        }
    }

    override fun setNewInstance(list: MutableList<TagBean>?) {
        super.setNewInstance(list)
    }

    override fun convert(holder: BaseDataBindingHolder<LayoutTagListItemBinding>, item: TagBean) {
        holder.dataBinding!!.tagItem=item
    }
}