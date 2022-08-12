package com.example.ppjoke.widget.dialog

import android.app.AlertDialog
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.ResolveInfo
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.FrameLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ConvertUtils
import com.example.ppjoke.adapter.ShareAdapter
import com.example.ppjoke.utils.ViewHelper
import com.example.ppjoke.widget.CornerFrameLayout

class ShareDialog(context: Context):AlertDialog(context) {
    private var layout: CornerFrameLayout?=null
    private var shareContent:String?=""
    private var mListener:View.OnClickListener?=null
    private lateinit var shareAdapter: ShareAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        super.onCreate(savedInstanceState)

        layout= CornerFrameLayout(context)
        layout!!.setBackgroundColor(Color.WHITE)
        layout!!.setViewOutline(ConvertUtils.dp2px(20f),ViewHelper.RADIUS_TOP)
        var gridView=RecyclerView(context)
        gridView.layoutManager=GridLayoutManager(context,4)

        shareAdapter= ShareAdapter(context,ArrayList())
        queryShareItems()
        shareAdapter.setOnItemClickListener{_,view,position->
            val pkg=shareAdapter.data[position].activityInfo.packageName
            val cls=shareAdapter.data[position].activityInfo.name
            val intent=Intent().apply {
                action=Intent.ACTION_SEND
                type="text/plain"
                component= ComponentName(pkg, cls)
                putExtra(Intent.EXTRA_TEXT,shareContent)
            }
            context.startActivity(intent)
            if (mListener!=null){
                mListener!!.onClick(view)
            }
            dismiss()

        }
        gridView.adapter=shareAdapter
        val margin=ConvertUtils.dp2px(20f)
        var params=FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
        params.leftMargin =margin
        params.rightMargin = margin
        params.gravity=Gravity.CENTER

        layout!!.addView(gridView,params)
        setContentView(layout!!)
        window?.setGravity(Gravity.BOTTOM)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)

    }

    fun setShareContent(shareContent:String){
        this.shareContent=shareContent
    }

    fun setShareItemClickListener(listener:View.OnClickListener){
        mListener=listener
    }

    private fun queryShareItems(){
        val intent=Intent()
        intent.action=Intent.ACTION_SEND
        intent.type="text/plain"
        val resolveInfo=context.packageManager.queryIntentActivities(intent,0)
        for (i in resolveInfo){

            val packageName=i.activityInfo.packageName
            println("packName: $packageName")
            if(TextUtils.equals(packageName,"com.tencent.mm")||TextUtils.equals(packageName,"com.tencent.mobileqq")){
                shareAdapter.addData(i)
            }
        }

    }
}