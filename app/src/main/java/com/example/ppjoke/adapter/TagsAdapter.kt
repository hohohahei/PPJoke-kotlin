package com.example.ppjoke.adapter

import android.annotation.SuppressLint
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.example.ppjoke.R
import com.example.ppjoke.bean.TagBean
import com.example.ppjoke.databinding.LayoutTagListItemBinding
import com.example.ppjoke.databinding.LayoutTagsItemBinding

class TagsAdapter(data:MutableList<TagBean>):
    BaseQuickAdapter<TagBean, BaseDataBindingHolder<LayoutTagsItemBinding>>(
    R.layout.layout_tags_item,
    data
) {

    override fun setNewInstance(list: MutableList<TagBean>?) {
        super.setNewInstance(list)
    }

    override fun convert(holder: BaseDataBindingHolder<LayoutTagsItemBinding>, item: TagBean) {
        holder.dataBinding!!.tagItem=item
    }
}