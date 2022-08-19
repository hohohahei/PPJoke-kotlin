package com.example.ppjoke.widget

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.ppjoke.R

class TextItemView@JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyAttrs: Int = 0) : ConstraintLayout(context, attributeSet, defStyAttrs) {
    private var tvTitle:TextView
    private var tvContent:TextView
    private var dividerView:View
    private var hint:String?=null

    @SuppressLint("CustomViewStyleable")
    val typedArray:TypedArray=context.obtainStyledAttributes(attributeSet, R.styleable.MyTextItemView)

    init {
        var root=LayoutInflater.from(context).inflate(R.layout.layout_user_info_tv_item,this)
        tvTitle=root.findViewById(R.id.tv_title)
        tvContent=root.findViewById(R.id.tv_content)
        dividerView=root.findViewById(R.id.divider)
        tvTitle.text=typedArray.getString(R.styleable.MyTextItemView_tv_title)
        tvContent.text=typedArray.getString(R.styleable.MyTextItemView_tv_content)
        hint=typedArray.getString(R.styleable.MyTextItemView_tv_hint)
        tvContent.hint=hint
        if(typedArray.getBoolean(R.styleable.MyTextItemView_isShowDivider,true)){
            dividerView.visibility=VISIBLE
        }else{
            dividerView.visibility= GONE
        }
    }

    fun setText(title:String){
        tvTitle.text=title
    }

    fun setContent(content:String){
        tvContent.text=content
    }

}