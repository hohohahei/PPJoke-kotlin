package com.example.ppjoke.adapter

import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.example.ppjoke.R
import com.example.ppjoke.databinding.LayoutShareItemBinding
import java.security.AccessController.getContext


class ShareAdapter(context: Context,data:MutableList<ResolveInfo>):BaseQuickAdapter<ResolveInfo,BaseDataBindingHolder<LayoutShareItemBinding>>(
    R.layout.layout_share_item,
    data
) {
    private var packageManager: PackageManager? = null
    override fun convert(holder: BaseDataBindingHolder<LayoutShareItemBinding>, item: ResolveInfo) {
        holder.dataBinding!!.item=item
        holder.dataBinding!!.pageManager=packageManager
    }


   init {
        packageManager = context.packageManager
    }
}