package com.example.ppjoke.widget.dialog

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.WindowManager
import android.widget.TextView
import com.blankj.utilcode.util.ConvertUtils
import com.example.ppjoke.R
import com.example.ppjoke.utils.ViewHelper

class LoadingDialog(context: Context):AlertDialog(context) {
    private var loadingText:TextView?=null
    override fun show() {
        super.show()
        setContentView(R.layout.layout_loading_view)
        loadingText=findViewById(R.id.loading_text)
        window!!.attributes.apply {
            width=WindowManager.LayoutParams.WRAP_CONTENT
            height=WindowManager.LayoutParams.WRAP_CONTENT
            gravity=Gravity.CENTER
            dimAmount=0.5f
        }
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        ViewHelper.setViewOutline(findViewById(R.id.loading_layout),ConvertUtils.dp2px(10f),ViewHelper.RADIUS_ALL)
    }

    fun setLoadingText(loadingText:String){
        if(this.loadingText!=null) {
            this.loadingText!!.text = loadingText
        }
    }
}