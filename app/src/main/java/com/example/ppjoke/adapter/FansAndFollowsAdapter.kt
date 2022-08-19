package com.example.ppjoke.adapter

import android.annotation.SuppressLint
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.example.ppjoke.R
import com.example.ppjoke.bean.TagBean
import com.example.ppjoke.bean.UserBean
import com.example.ppjoke.databinding.LayoutTagListItemBinding
import com.example.ppjoke.databinding.LayoutUserItemBinding

class FansAndFollowsAdapter(data:MutableList<UserBean>):
    BaseQuickAdapter<UserBean, BaseDataBindingHolder<LayoutUserItemBinding>>(
    R.layout.layout_user_item,
    data
) {
    @SuppressLint("ResourceAsColor")
    override fun convert(
        holder: BaseDataBindingHolder<LayoutUserItemBinding>,
        item: UserBean,
        payloads: List<Any>
    ) {
        super.convert(holder, item, payloads)
        if(payloads.isNotEmpty()){
            holder.dataBinding!!.userItem?.hasFollow=item.hasFollow
        }
    }

    override fun setNewInstance(list: MutableList<UserBean>?) {
        super.setNewInstance(list)
    }

    override fun convert(holder: BaseDataBindingHolder<LayoutUserItemBinding>, item: UserBean) {
        holder.dataBinding!!.userItem=item
    }
}