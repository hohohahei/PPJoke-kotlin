package com.example.ppjoke.widget.dialog

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.example.ppjoke.R
import com.example.ppjoke.adapter.ShareAdapter
import com.example.ppjoke.databinding.LayoutBottomShareDialogBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

class BottomShareDialog(context: Context):BottomSheetDialog(context) {
    private lateinit var binding:LayoutBottomShareDialogBinding
    private var shareContent:String?=""
    private var mListener: View.OnClickListener?=null
    private lateinit var shareAdapter: ShareAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.layout_bottom_share_dialog,null,false)
        binding.recyclerView.layoutManager= GridLayoutManager(context,4)
        shareAdapter= ShareAdapter(context,ArrayList())
        queryShareItems()
        shareAdapter.setOnItemClickListener{_,view,position->
            val pkg=shareAdapter.data[position].activityInfo.packageName
            val cls=shareAdapter.data[position].activityInfo.name
            val intent= Intent().apply {
                action= Intent.ACTION_SEND
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
        binding.recyclerView.adapter=shareAdapter
        setContentView(binding.root)

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
            if(TextUtils.equals(packageName,"com.tencent.mm")|| TextUtils.equals(packageName,"com.tencent.mobileqq")){
                shareAdapter.addData(i)
            }
        }

    }
}